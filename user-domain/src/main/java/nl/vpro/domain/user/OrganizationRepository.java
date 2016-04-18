/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;

interface OrganizationRepository {

    <T extends Organization> List<T> findAll(Class<T> clazz);

    <T extends Organization> T get(String id, Class<T> clazz);

    <T extends Organization> T getByProperty(String property, String value, Class<T> clazz);

    <T extends Organization> T getByPropertyIgnoreCase(String property, String value, Class<T> clazz);

    <T extends Organization> T merge(T broadcaster);

    <T extends Organization> void delete(T broadcaster);

}
