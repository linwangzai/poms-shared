package nl.vpro.domain;

import java.time.Instant;

import nl.vpro.domain.user.Editor;

import static nl.vpro.domain.AbstractPublishableObject_.*;
import static nl.vpro.domain.Changeables.setProperty;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@SuppressWarnings("JavadocReference")
public class Accountables {

    public static void fillFor(Accountable accountable, Instant now, Editor currentUser) {
        Changeables.fillFor(accountable, now);
        accountable.setLastModifiedBy(currentUser);
        if (accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(currentUser);
        }
    }


    /**
     * Used by implementations of {@link org.hibernate.Interceptor}
     */
    public static boolean updateEntity(
        Editor user,
        boolean updateLastModified,
        Accountable accountable, Object[] state, String[] propertyNames) {
        boolean updated = false;

        Changeables.updateEntity(accountable, updateLastModified,  CREATION_INSTANT, LAST_MODIFIED, state, propertyNames);

        if(accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(user);
            setProperty(CREATED_BY, accountable.getCreatedBy(), state, propertyNames);
            updated = true;
        }

        if(accountable.getLastModifiedBy() == null || (updateLastModified && accountable.hasChanges())) {
            accountable.setLastModifiedBy(user);
            setProperty(LAST_MODIFIED_BY, accountable.getLastModifiedBy(), state, propertyNames);
            updated = true;
        }

        return updated;
    }


}
