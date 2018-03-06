/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.Roles;

public interface UserService<T extends User> {

    <S> S doAs(String principalId, Callable<S> handler) throws Exception;

    T get(String id);

    List<? extends T> findUsers(String name, int limit);

    T update(T user);

    void delete(T object);

    T currentUser();

    default String currentPrincipalId() {
        T currentUser = currentUser();
        return currentUser == null ? null : currentUser.getPrincipalId();
    }

    void authenticate(String principalId, String password);

    boolean currentUserHasRole(String... roles);

    boolean currentUserHasRole(List<String> roles);

    void authenticate(String principalId);

    Object getAuthentication();

    void restoreAuthentication(Object authentication);


    /**
     * Default implemention without consideration of the roles. This can be overridden.
     */
    default Logout systemAuthenticate(String principalId, String... roles) {
        authenticate(principalId);
        return this::dropAuthentication;
    }

    void dropAuthentication();

    default boolean isPrivilegedUser() {
        return currentUserHasRole(
            Roles.SUPERADMIN_ROLE,
            Roles.SUPERPROCESS_ROLE,
            Roles.PUBLISHER_ROLE,
            Roles.SUPPORT_ROLE,
            Roles.SYSTEM_ROLE
        );
    }

    default boolean isProcessUser() {
        return currentUserHasRole(
            Roles.PROCESS_ROLE,
            Roles.SUPERPROCESS_ROLE
        );
    }

    default boolean isPublisher() {
        return currentUserHasRole(Roles.PUBLISHER_ROLE);
    }

    /**
     * Submits callable in the given {@link ExecutorService}, but makes sure that it is executed as the current user
     */
    default <R> Future<R> submit(ExecutorService executorService, Callable<R> callable) {
        return submit(executorService, callable, null);
    }


    /**
     * Submits callable in the given {@link ExecutorService}, but makes sure that it is executed as the current user
     * @param logger If not <code>null</code> catch exceptions and log as error.
     * @since 5.6
     */
    default <R> Future<R> submit(ExecutorService executorService, Callable<R> callable, Logger logger) {
        Object authentication;
        try {
            authentication = getAuthentication();
        } catch(Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            authentication = null;
        }
        final Object onBehalfOf = authentication;
        return executorService.submit(() -> {
            try {
                if (onBehalfOf != null) {
                    try {
                        restoreAuthentication(onBehalfOf);
                    } catch (Exception e) {
                        LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    }
                }
                return callable.call();
            } catch (Exception e) {
                if (logger != null) {
                    logger.error(e.getMessage(), e);
                    return null;
                } else {
                    throw e;
                }
            } finally {
                dropAuthentication();
            }
        });
    }
    interface  Logout extends AutoCloseable {
        @Override
        void close();

    }
}
