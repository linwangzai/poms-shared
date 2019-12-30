package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.bind.CreditsDeserializer;

/**
 * A container class for credits, linking the role to an actual entity.
 *
 * For person this at the moment is an embedded entity, for other names, this should be a join with gtaa record.
 *
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlSeeAlso({
    Person.class,
    Name.class
})
@XmlType(name = "creditsType")
@XmlAccessorType(XmlAccessType.NONE)
@JsonDeserialize(using = CreditsDeserializer.class)
public abstract class Credits extends DomainObject implements Child<MediaObject>  {



    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected RoleType role = RoleType.UNDEFINED;


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

    public static Credits copy(Credits credits, MediaObject mediaObject) {
        if (credits instanceof Person) {
            return Person.copy((Person) credits, mediaObject);
        } else if (credits instanceof Name) {
            return Name.copy((Name) credits, mediaObject);
        } else {
            throw new IllegalStateException();
        }
    }


    @Override
    public void setParent(MediaObject mo) {
        this.mediaObject = mo;
    }

    @Override
    public MediaObject getParent() {
        return this.mediaObject;
    }

    @JsonProperty
    protected String getObjectType() {
        return getClass().getSimpleName().toLowerCase();
    }

    public abstract String getName();

}
