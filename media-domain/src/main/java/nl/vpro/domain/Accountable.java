package nl.vpro.domain;

import java.time.Instant;

import nl.vpro.domain.user.Editor;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface Accountable {

    boolean hasChanges();

    Instant getLastModifiedInstant();
    void setLastModifiedInstant(Instant lastModified);
    Instant getCreationInstant();
    void setCreationInstant(Instant creationDate);

    Editor getCreatedBy();
    void setCreatedBy(Editor createdBy);
    Editor getLastModifiedBy();
    void setLastModifiedBy(Editor lastModifiedBy);


}
