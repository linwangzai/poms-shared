package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.media.support.PublishableObject;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * Expresses an association between MediaObjects. MediaObjects can become a
 * member or episode of other MediaObjects. The member is the member of this
 * relation which references the aggregating owner.
 * <p/>
 * Members or episodes can have an ordering if they are a part of an ordered
 * Group. In that case a MemberRef gets an number value denoting its position in
 * the group. In all other cases this number should retain a null value.
 * <p/>
 * Incoming XML has a member-/episodeof element for each member and references
 * the owner by its URN. The urnRef field holds a temporary reference just for
 * this purpose and should never be persisted.
 *
 * @author roekoe
 * @version $Id$
 * @see MediaObject#memberOf
 * @see Program#episodeOf
 */
@Entity
@FilterDefs({
    @FilterDef(name = PublishableObject.INVERSE_PUBLICATION_FILTER),
    @FilterDef(name = PublishableObject.INVERSE_EMBARGO_FILTER, parameters = {@ParamDef(name = "broadcasters", type = "string")}),
    @FilterDef(name = PublishableObject.INVERSE_DELETED_FILTER)
})
@Filters({
    @Filter(name =  PublishableObject.INVERSE_PUBLICATION_FILTER, condition = "((  " +
        "       (select m.publishStart from mediaobject m where m.id = member_id) is null " +
        "       or now() > (select m.publishStart from mediaobject m where m.id = member_id)" +
        "    ) and (" +
        "       (select m.publishStop from mediaobject m where m.id = member_id) is null " +
        "       or now() < (select m.publishStop from mediaobject m where m.id = member_id)" +
        "    ))"),
    @Filter(name = PublishableObject.INVERSE_EMBARGO_FILTER, condition = "(" +
        "   (select m.publishStart from mediaobject m where m.id = member_id) is null " +
        "   or now() > (select m.publishStart from mediaobject m where m.id = member_id) " +
        "   or 'CLIP' != (select p.type from program p where p.id = member_id) " +
        "   or 0 < (select count(*) from mediaobject_broadcaster b where b.mediaobject_id = member_id and b.broadcasters_id in (:broadcasters))" +
        ")"),
    @Filter(name = PublishableObject.INVERSE_DELETED_FILTER, condition = "(select m.workflow from mediaobject m where m.id = member_id and m.mergedTo_id is null) NOT IN ('MERGED', 'FOR_DELETION', 'DELETED')")})
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(name = "memberRefType")
@JsonPropertyOrder({
    "midRef",
    "urnRef",
    "type",
    "index",
    "highlighted"
})
public class MemberRef implements Identifiable<Long>, Comparable<MemberRef>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    protected MediaObject member;

    @ManyToOne(optional = false)
    protected MediaObject owner;

    @Transient
    protected String urnRef;

    @Transient
    protected String cridRef;

    @Transient
    protected String midRef;

    @Transient
    protected MediaType typeOfOwner;

    protected Integer number;

    @Column(nullable = false, updatable = false)
    protected Instant added;

    @Column(nullable = false)
    @NotNull(message = "nl.vpro.constraints.NotNull")
    protected Boolean highlighted = false;

    public MemberRef() {
    }

    public MemberRef(MediaObject member, MediaObject owner, Integer number) {
        this(null, member, owner, number);
    }

    public MemberRef(Long id, MediaObject member, MediaObject owner, Integer number) {
        if(member == null || owner == null) {
            throw new IllegalArgumentException(String.format("Must supply valid member and owner. Got member: %1$s and owner: %2$s", member, owner));
        }

        this.id = id;
        this.member = member;
        this.owner = owner;
        this.number = number;
    }

    public MemberRef(String mid) {
        this(mid, null);
    }

    public MemberRef(String mid, Integer number) {
        if(mid == null) {
            throw new NullPointerException("Cannot instatiate a memberref with mid null");
        }
        this.midRef = mid;
        this.number = number;
    }

    public MemberRef(MemberRef source) {
        this(source, source.member);
    }

    public MemberRef(MemberRef source, MediaObject member) {
        this(member, source.owner, source.number);
        this.added = source.added;
        this.highlighted = source.highlighted;

        this.cridRef = source.cridRef;
        this.midRef= source.midRef;
        this.urnRef= source.urnRef;
    }

    public static MemberRef copy(MemberRef source){
        return copy(source, source.member);
    }

    public static MemberRef copy(MemberRef source, MediaObject member){
        if(source == null) {
            return null;
        }

        MemberRef copy = new MemberRef(source, member);
        copy.added = source.added;
        return copy;
    }

    public boolean ownerEqualsOnRef(MemberRef that) {
        if(owner != null && that.getOwner() != null) {
            return MediaObjects.equalsOnAnyId(owner, that.getOwner());
        }

        if(owner == null && that.getOwner() != null) {
            MediaObject thatOwner = that.getOwner();
            return thatOwner.getUrn() != null && thatOwner.getUrn().equals(getUrnRef()) ||
                thatOwner.getMid() != null && thatOwner.getMid().equals(getMidRef()) ||
                thatOwner.getCrids().contains(getCridRef());
        }

        if(owner != null && that.getOwner() == null) {
            return owner.getUrn() != null && owner.getUrn().equals(that.getUrnRef()) ||
                owner.getMid() != null && owner.getMid().equals(that.getMidRef()) ||
                owner.getCrids().contains(getCridRef());
        }

        return that.getUrnRef() != null && that.getUrnRef().equals(getUrnRef()) ||
            that.getMidRef() != null && that.getMidRef().equals(getMidRef()) ||
            that.getCridRef() != null && that.getCridRef().equals(getCridRef());
    }

    @XmlTransient
    @Override
    public Long getId() {
        return id;
    }

    @XmlTransient
    public MediaObject getMember() {
        return member;
    }

    public void setMember(MediaObject member) {
        this.member = member;
    }

    @XmlTransient
    public MediaObject getOwner() {
        return owner;
    }

    public void setOwner(MediaObject owner) {
        this.urnRef = null;
        this.cridRef = null;
        this.owner = owner;
    }

    @XmlTransient
    public String getMediaRef() {
        String ref = getMidRef();
        if(ref != null) {
            return ref;
        }

        ref = getCridRef();
        if(ref != null) {
            return ref;
        }

        return getUrnRef();
    }


    public void setMediaRef(String mediaRef) {
        if (mediaRef == null) {
            throw new IllegalArgumentException("Mediaref cannot be null");
        }
        if(mediaRef.startsWith("urn:vpro:media:")) {
            this.urnRef = mediaRef;
        } else if(mediaRef.startsWith("crid://")) {
            this.cridRef = mediaRef;
        } else {
            this.setMidRef(mediaRef);
        }
    }


    /**
     * Retrieves a reference to the owning object and parent of the association
     * by name. This method is primarily here for marshaling purposes. In XML
     * documents a MemberRef element contains an urnRef attribute referencing
     * the owner. First this method tries to construct an urnRef based on the
     * URN of the owner. When this fails, it tries to construct a reference
     * bases on a CRID from the owner.
     *
     * @return reference to the owner by name or null
     */
    @XmlAttribute
    public String getUrnRef() {
        if(owner != null) {
            return owner.getId() == null ? null : owner.getUrn();
        }

        return urnRef;
    }

    /**
     * Set a reference to the owner by name. A reference might consist of an URN
     * of a CRID identifying the owner. This method should only be used when
     * unmarshalling from XML. When importing XML the import service should
     * retrieve the owner with this name and properly initialise this
     * association.
     *
     * @param value a valid URN or CRID
     */
    public void setUrnRef(String value) {
        if(owner != null) {
            throw new IllegalStateException();
        }

        this.urnRef = value;
    }

    @XmlAttribute
    public String getCridRef() {
        if(getUrnRef() != null) {
            return null;
        }

        if(owner != null && owner.getCrids().size() > 0) {
            return owner.getCrids().get(0);
        }

        if(cridRef != null) {
            return cridRef;
        }
        return null;
    }

    public void setCridRef(String crid) {
        if(owner != null || urnRef != null) {
            throw new IllegalStateException(" ");
        }

        cridRef = crid;
    }

    @XmlAttribute
    public String getMidRef() {
        if(this.owner != null) {
            return owner.getMid();
        }
        return midRef;
    }

    public void setMidRef(String midRef) {
        if(owner != null) {
            throw new IllegalStateException("Call set midRef on the enclosed owner, this is a JAXB only setter");
        }

        this.midRef = midRef;
    }

    @XmlAttribute
    public MediaType getType() {
        return owner != null ? MediaType.getMediaType(owner) : typeOfOwner;
    }

    public void setType(MediaType type) {
        if(owner != null && MediaType.getMediaType(owner) != type) {
            throw new IllegalStateException("Supplied type " + type + " does not match owner type " + MediaType.getMediaType(owner));
        }
        typeOfOwner = type;
    }

    @XmlTransient
    boolean isValid() {
        return member != null
            && owner != null
            && !(owner instanceof Group
            && ((Group)owner).isOrdered() && (number == null || number < 1));
    }

    @XmlAttribute(name = "index")
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getAdded() {
        return added;
    }

    public void setAdded(Instant added) {
        this.added = added;
    }

    @XmlAttribute(required = true)
    public Boolean isHighlighted() {
        return highlighted;
    }

    public MemberRef setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted != null ? highlighted : false;
        return this;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }

        if(!(object instanceof MemberRef)) {
            return false;
        }

        MemberRef that = (MemberRef)object;

/*
        if(this.getId() != null
            && that.getId() != null) {
            return this.getId().equals(that.getId());
        }
*/

        // Jaxb does not initialise the owner but a reference to the owner
        return memberEqualsOnRef(that)
            && ownerEqualsOnRef(that)
            && (number == null ? that.getNumber() == null : number.equals(that.getNumber()));
    }

    @Override
    public int hashCode() {
        if(id == null & member == null && urnRef == null && number == null) {
            return super.hashCode();
        }

        int hash = 5;
        hash = 53 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 53 * hash + (this.getMediaRef() != null ? this.getMediaRef().hashCode() : 0);
        hash = 53 * hash + (this.number != null ? this.number.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(MemberRef memberRef) {
        if(this.getUrnRef() != null
            && memberRef.getUrnRef() != null
            && this.getUrnRef().compareTo(memberRef.getUrnRef()) != 0) {
            return this.getUrnRef().compareTo(memberRef.getUrnRef());
        }
        if(this.number != null && memberRef.getNumber() != null && !this.number.equals(memberRef.getNumber())) {
            return this.number - memberRef.getNumber();
        }

/*
        if(this.added != null && memberRef.getAdded() != null && !this.added.equals(memberRef.getAdded())) {
            return this.added.compareTo(memberRef.getAdded());
        }
*/

        if(this.getMediaRef() != null
            && memberRef.getMediaRef() != null) {
            return getMediaRef().compareTo(memberRef.getMediaRef());
        }

/*
        if(this.id != null && memberRef.getId() != null) {
            return this.getId().compareTo(memberRef.getId());
        }
*/

        return this.hashCode() - memberRef.hashCode();
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof MediaObject) {
            this.member = (MediaObject)parent;
        }
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        if (id != null) {
            builder.append("id", id);
        }
        if (urnRef != null) {
            builder.append("urnRef", urnRef);
        }
        if (cridRef != null) {
            builder.append("cridRef", cridRef);
        }
        if (midRef != null) {
            builder.append("midRef", midRef);
        }
        if (number != null) {
            builder.append("number", number);
        }
        if (added != null) {
            builder.append("added", added);
        }
        if (highlighted != null) {
            builder.append("highlighted", highlighted);
        }
        return builder.toString();
    }

    private boolean memberEqualsOnRef(MemberRef that) {
        MediaObject thatMember = that.getMember();
        return member != null && thatMember != null &&
            (member == thatMember ||
                member.getUrn() != null && member.getUrn().equals(thatMember.getUrn()) ||
                member.getMid() != null && member.getMid().equals(thatMember.getMid()) ||
                memberEqualsOnCrid(thatMember));

    }

    private boolean memberEqualsOnCrid(MediaObject thatMember) {
        if(member.getCrids().isEmpty() || thatMember.getCrids().isEmpty()) {
            return false;
        }

        for(String memberCrid : member.getCrids()) {
            for(String thatCrid : thatMember.getCrids()) {
                if(thatCrid.equals(memberCrid)) {
                    return true;
                }
            }
        }

        return false;
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.added == null) {
            this.added = Instant.now();
        }
    }

}
