package nl.vpro.domain;

import java.util.Date;

import nl.vpro.util.DateUtils;

/**
 * Some deprecated methods related to {@link Embargo}s
 *
 * @author Michiel Meeuwissen
 * @since 5.2
 */
public interface EmbargoDeprecated<T extends EmbargoDeprecated<T>> extends Embargo<T> {

    @Deprecated
    default Date getPublishStart() {
        return DateUtils.toDate(getPublishStartInstant());
    }

    @Deprecated
    default void setPublishStart(Date publishStart) {
        setPublishStartInstant(DateUtils.toInstant(publishStart));
    }
    @Deprecated
    default Date getPublishStop() {
        return DateUtils.toDate(getPublishStopInstant());
    }

    @Deprecated
    default void setPublishStop(Date publishStop) {
        setPublishStopInstant(DateUtils.toInstant(publishStop));
    }


}