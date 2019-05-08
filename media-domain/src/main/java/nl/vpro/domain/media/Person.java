package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.validation.NoHtml;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType",
    propOrder = {
        "givenName",
        "familyName"
    })
public class Person extends DomainObject implements PersonInterface, Child<MediaObject> {


    public static Person copy(Person source) {
        return copy(source, source.mediaObject);
    }

    public static Person copy(Person source,  MediaObject parent) {
        if (source == null) {
            return null;
        }

        return new Person(source, parent);
    }

    @NoHtml
    @XmlElement
    @Getter
    @Setter
    protected String givenName;

    @NoHtml
    @XmlElement
    @Getter
    @Setter
    protected String familyName;


    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected RoleType role;

    @Column(name = "list_index",
        nullable = true
        // hibernate sucks incredibly https://stackoverflow.com/questions/41194606/why-does-hibernate-require-the-list-index-to-be-nullable
    )
    @XmlTransient
    @NotNull
    @Getter
    @Setter
    private Integer listIndex = 0;

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    protected MediaObject mediaObject;

    @Embedded
    @XmlTransient
    @Getter
    @Setter
    protected GTAARecord gtaaRecord;

    public Person() {
    }

    public Person(String givenName, String familyName) {
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public Person(String givenName, String familyName, RoleType role) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.role = role;
    }

    public Person(Long id, String givenName, String familyName, RoleType role) {
        this(givenName, familyName, role);
        this.id = id;
    }

    public Person(Person source) {
        this(source, source.mediaObject);
    }

    public Person(Person source, MediaObject parent) {
        this(source.getGivenName(), source.getFamilyName(), source.getRole());
        this.gtaaRecord = source.gtaaRecord;

        this.mediaObject = parent;
    }


    @lombok.Builder(builderClassName = "Builder")
    private Person(
        Long id,
        String givenName,
        String familyName,
        RoleType role,
        MediaObject mediaObject,
        GTAARecord gtaaRecord) {
        this(id, givenName, familyName, role);
        this.mediaObject = mediaObject;
        this.gtaaRecord = gtaaRecord;
    }

    /**
     * Sets both the given name and the family name by splitting the String.
     */
    public void setName(String name) {
        String[] split = name.split("\\s+", 2);
        if(split.length == 1) {
            setGivenName("");
            setFamilyName(name);
        } else {
            setGivenName(split[0]);
            setFamilyName(split[1]);
        }
    }



    @Deprecated
    public MediaObject getMediaObject() {
        return mediaObject;
    }

    @Deprecated
    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    @Override
    public void setParent(MediaObject mo) {
        this.mediaObject = mo;
    }

    @Override
    public MediaObject getParent() {
        return this.mediaObject;
    }



    @Override
    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getUri)
                .orElse(null);
    }
    public void setGtaaUri(String uri) {
        this.gtaaRecord = new GTAARecord(uri, null);
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)) {
            return true;
        }
        if(!(o instanceof Person)) {
            return false;
        }

        Person person = (Person)o;

        if(familyName != null ? !familyName.equals(person.familyName) : person.familyName != null) {
            return false;
        }
        if(givenName != null ? !givenName.equals(person.givenName) : person.givenName != null) {
            return false;
        }
        if(role != person.role) {
            return false;
        }
        return Objects.equals(getGtaaUri(), person.getGtaaUri());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        String gtaaUri = getGtaaUri();
        result = 31 * result + (gtaaUri != null ? gtaaUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("givenName", givenName)
            .append("familyName", familyName)
            .append("role", role)
            .append("gtaa_uri", getGtaaUri())
            .toString();
    }


    public static class Builder {
        public Builder gtaaUri(String uri) {
            return gtaaRecord(GTAARecord.builder().uri(uri).build());
        }
    }



}
