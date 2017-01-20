/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 3 nov 2008.
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.com.neovisionaries.i18n.CountryCode;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.bind.BackwardsCompatibility;
import nl.vpro.domain.media.bind.CountryCodeAdapter;
import nl.vpro.domain.media.bind.LocaleAdapter;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.ThirdParty;
import nl.vpro.nicam.NicamRated;
import nl.vpro.util.DateUtils;
import nl.vpro.util.ResortedSortedSet;
import nl.vpro.util.SortedSetSameElementWrapper;
import nl.vpro.validation.Language;
import nl.vpro.validation.StringList;
import nl.vpro.validation.WarningValidatorGroup;

/**
 * Base objects for programs and groups
 *
 * @author roekoe
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Language
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Program.class, Group.class, Segment.class})
@XmlType(name = "baseMediaType", propOrder = {
    "crids",
    "broadcasters",
    "portals",
    "portalRestrictions",
    "geoRestrictions",
    "titles",
    "descriptions",
    "genres",
    "tags",
    "source",
    "countries",
    "languages",
    "avAttributes",
    "releaseYear",
    "duration",
    "persons",
    "awards",
    "descendantOf",
    "memberOf",
    "ageRating",
    "contentRatings",
    "email",
    "websites",
    "twitterRefs",
    "teletext",
    "predictions",
    "locations",
    "scheduleEvents",
    "relations",
    "images"
})

@JsonPropertyOrder({
    "objectType",
    /* xml attributes */
    "mid",
    "type",
    "avType",
    "workflow",
    "mergedTo",
    "sortDate",
    "creationDate",
    "lastModified",
    "publishStart",
    "publishStop",
    "urn",
    "embeddable",
    /* xml elements */
    "episodeOf",
    "crids",
    "broadcasters",
    "portals",
    "portalRestrictions",
    "geoRestrictions",
    "titles",
    "descriptions",
    "genres",
    "tags",
    "source",
    "hasSubtitles",
    "countries",
    "languages",
    "avAttributes",
    "releaseYear",
    "duration",
    "persons",
    "awards",
    "descendantOf",
    "memberOf",
    "ageRating",
    "contentRatings",
    "email",
    "websites",
    "twitter",
    "teletext",
    "predictions",
    "locations",
    "scheduleEvents",
    "relations",
    "images"
}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = Program.class, name = "program"),
        @JsonSubTypes.Type(value = Group.class, name = "group"),
        @JsonSubTypes.Type(value = Segment.class, name = "segment")
    }
)

// TODO: Filters can be defined in hibernate-mapping in the hibernate-config.xml See https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch19.html
@FilterDefs({
    @FilterDef(name = "titleFilter", parameters = {@ParamDef(name = "title", type = "string")}),
    @FilterDef(name = "typeFilter", parameters = {
        @ParamDef(name = "types", type = "string"),
        @ParamDef(name = "segments", type = "boolean")}),
    @FilterDef(name = "organizationFilter", parameters = {@ParamDef(name = "organizations", type = "string")}),
    @FilterDef(name = "noBroadcast"),
    @FilterDef(name = "hasLocations"),
    @FilterDef(name = "noPlaylist"),
    @FilterDef(name = "eventRange", parameters = {
        @ParamDef(name = "eventStart", type = "date"),
        @ParamDef(name = "eventStop", type = "date")}),
    @FilterDef(name = "creationRange", parameters = {
        @ParamDef(name = "creationStart", type = "date"),
        @ParamDef(name = "creationStop", type = "date")}),
    @FilterDef(name = "modifiedRange", parameters = {
        @ParamDef(name = "modifiedStart", type = "date"),
        @ParamDef(name = "modifiedStop", type = "date")}),
    @FilterDef(name = PublishableObject.PUBLICATION_FILTER),
    @FilterDef(name = PublishableObject.EMBARGO_FILTER, parameters = {@ParamDef(name = "broadcasters", type = "string")}),
    @FilterDef(name = PublishableObject.DELETED_FILTER),
    @FilterDef(name = "relationFilter", parameters = {@ParamDef(name = "broadcasters", type = "string")})})
@Filters({
    @Filter(name = "titleFilter", condition = "0 < (select count(*) from title t where t.parent_id = id and lower(t.title) like :title)"),
    @Filter(name = "typeFilter", condition = "(0 < (select count(*) from program p where p.id = id and p.type in (:types)))"
        + " or (0 < (select count(*) from group_table g where g.id = id and g.type in (:types)))"
        + " or (:segments and 0 < (select count(*) from segment s where s.id = id))"),
    @Filter(name = "organizationFilter",
        condition = "0 < (" +
            "(select count(*) from mediaobject_portal o where o.mediaobject_id = id and o.portals_id in (:organizations))" +
            " + " +
            "(select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:organizations))" +
            " + " +
            "(select count(*) from mediaobject_thirdparty o where o.mediaobject_id = id and o.thirdparties_id in (:organizations))" +
            ")"
    ),
    @Filter(name = "noBroadcast", condition = "0 = (select count(*) from scheduleevent e where e.mediaobject_id = id)"),
    @Filter(name = "hasLocations", condition = "0 < (select count(*) from location l where l.mediaobject_nid = id)"),
    @Filter(name = "noPlaylist", condition = "0 = (select count(*) from group_table g, memberref mr where mr.member_id = id "
        + "and g.id = mr.owner_id " + "and g.type = 'PLAYLIST')"),
    @Filter(name = "eventRange", condition = ":eventStart <= (select min(e.start) from scheduleevent e where e.mediaobject_id = id) and "
        + ":eventStop >= (select min(e.start) from scheduleevent e where e.mediaobject_id = id)"),
    @Filter(name = "creationRange", condition = ":creationStart <= creationDate and :creationStop >= creationDate"),
    @Filter(name = "modifiedRange", condition = ":modifiedStart <= lastModified and :modifiedStop >= lastModified"),
    @Filter(name = PublishableObject.PUBLICATION_FILTER, condition = "(publishStart is null or publishStart <= now()) "
        + "and (publishStop is null or publishStop > now())"
    ),
    @Filter(name = PublishableObject.EMBARGO_FILTER,
        condition =
            "(publishstart is null " +
                "or publishstart < now() " +
                "or (select p.type from program p where p.id = id) != 'CLIP' " +
                "or (0 < (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:broadcasters))))"
    ),
    @Filter(name = PublishableObject.DELETED_FILTER, condition = "(workflow NOT IN ('MERGED', 'FOR_DELETION', 'DELETED') and mergedTo_id is null)")})

@Slf4j
public abstract class MediaObject extends PublishableObject implements NicamRated {

    @Column(name = "mid", nullable = false, unique = true)
    @Size.List({@Size(max = 255), @Size(min = 4)})
    @Pattern(regexp = "^[a-zA-Z0-9][ \\.a-zA-Z0-9_-]*$", flags = {Pattern.Flag.CASE_INSENSITIVE}, message = "{nl.vpro.constraints.mid}")
    protected String mid;

    @ElementCollection
    @Column(name = "crids", nullable = false, unique = true)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // TODO cache configuration can be put in a hibernate-config.xml. See https://docs.jboss.org/hibernate/orm/4.0/devguide/en-US/html/ch06.html
    @StringList(maxLength = 255)
    protected List<String> crids;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Size(min = 0, message = "{nl.vpro.constraints.Size.min}") // komt soms voor bij imports.
    protected List<Broadcaster> broadcasters;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    // @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<Portal> portals;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    // @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<ThirdParty> thirdParties;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = "(start is null or start <= now()) "
        + "and (stop is null or stop > now())")
    @Valid
    protected List<PortalRestriction> portalRestrictions;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = "(start is null or start <= now()) "
        + "and (stop is null or stop > now())")
    @Valid
    protected List<GeoRestriction> geoRestrictions;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    //@NotNull(message = "titles: {nl.vpro.constraints.NotNull}") // Somewhy hibernates on merge first merges an object without titles.
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.collection.Size.min}"),
    })
    @Valid
    protected Set<Title> titles;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected Set<Description> descriptions;

    @ManyToMany
    @Cascade({CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REPLICATE, CascadeType.SAVE_UPDATE, CascadeType.PERSIST})
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected Set<Genre> genres;

    @ManyToMany
    @Cascade({CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REPLICATE, CascadeType.SAVE_UPDATE, CascadeType.PERSIST, CascadeType.REMOVE})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @JoinTable(foreignKey = @ForeignKey(name = "fk_mediaobject_tag__mediaobject"),
        inverseForeignKey = @ForeignKey(name = "fk_mediaobject_tag__tag")
    )
    protected Set<Tag> tags;

    protected String source;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<CountryCode> countries;

    @ElementCollection
    @Column(length = 10)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<Locale> languages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "avType: {nl.vpro.constraints.NotNull}")
    protected AVType avType;

    @OneToOne(orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    protected AVAttributes avAttributes;

    @Column(name = "releaseDate")
    protected Short releaseYear;

    @Embedded
    @Valid
    protected Duration duration;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<Person> persons;

    @ElementCollection
    @JoinTable(name = "mediaobject_awards")
    @OrderColumn(name = "list_index", nullable = false)
    @Column(name = "awards")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<String> awards;

    @OneToMany(orphanRemoval = true)
    @JoinTable(name = "mediaobject_memberof", inverseJoinColumns = @JoinColumn(name = "id"))
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filters({
        @Filter(name = PUBLICATION_FILTER, condition = "(" +
            "(mediaobjec10_.mergedTo_id is null) and " +  // MSE-3526 ?
            "(mediaobjec10_.publishstart is null or mediaobjec10_.publishstart < now()) and " +
            "(mediaobjec10_.publishstop is null or mediaobjec10_.publishstop > now())" +
            ")"),
        @Filter(name = EMBARGO_FILTER, condition =
            "(mediaobjec10_2_.type != 'CLIP' " +
            "or mediaobjec10_.publishstart is null " +
            "or mediaobjec10_.publishstart < now() " +
            "or 0 < (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = mediaobjec10_.id and o.broadcasters_id in (:broadcasters)))"),
        @Filter(name = DELETED_FILTER, condition = "(mediaobjec10_.workflow NOT IN ('FOR_DELETION', 'DELETED') and (mediaobjec10_.mergedTo_id is null))")})
    protected Set<MemberRef> memberOf;

    @Enumerated(EnumType.STRING)
    @NotNull(groups = {WarningValidatorGroup.class})
    protected AgeRating ageRating;

    @ElementCollection
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Enumerated(value = EnumType.STRING)
    protected List<ContentRating> contentRatings;

    @ElementCollection
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @StringList(maxLength = 255)
    protected List<String> email;


    @OneToMany(targetEntity = Website.class, orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id", nullable = true)
    // not nullable media/index blocks ordering updates on the collection
    @OrderColumn(name = "list_index", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<Website> websites;

    @OneToMany(targetEntity = TwitterRef.class, orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id", nullable = true)
    // not nullable media/index blocks ordering updates on the collection
    @OrderColumn(name = "list_index", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected List<TwitterRef> twitterRefs;

    protected Short teletext;

    //@Column(columnDefinition = "boolean not null default false") // fails tests
    @Type(type = "nl.vpro.hibernate.FalseToNullType")
    protected Boolean hasSubtitles = null;

    @OneToMany(orphanRemoval = true, mappedBy = "mediaObject")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    protected Set<Prediction> predictions;

    @OneToMany(orphanRemoval = true, mappedBy = "mediaObject")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @SortNatural
    @XmlTransient
    // TODO, why don't we merge ceresRecords and predictions completely?
    protected Set<LocationAuthorityRecord> locationAuthorityRecords = new TreeSet<>();

    @OneToMany(mappedBy = "mediaObject", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(
        name = PUBLICATION_FILTER,
        condition = "((platform is null and  (publishStart is null or publishStart <= now())  and (publishStop is null or publishStop > now())) "
            + " or " +
            " ( not(platform is null)  " +
            "   and ( " +
            "        select count(*) from locationauthorityrecord c where c.platform = platform and c.mediaobject_id = mediaobject_id and " +
            "              (c.restrictionStart is null or c.restrictionStart <= now()) and (c.restrictionStop is null or c.restrictionStop > now()) " +
            "       ) > 0)" +
            ")"

    )
    protected Set<Location> locations;

    @OneToMany(mappedBy = "mediaObject", orphanRemoval = false)
    @SortNatural
    @Cascade({org.hibernate.annotations.CascadeType.MERGE})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected Set<ScheduleEvent> scheduleEvents;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id", updatable = false, nullable = false)
    @Valid
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected Set<Relation> relations;

    @OneToMany(orphanRemoval = true, mappedBy = "mediaObject")
    @OrderColumn(name = "list_index", nullable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = "(publishStart is null or publishStart <= now()) "
        + "and (publishStop is null or publishStop > now())")
//    @Field(name = "images", store=Store.YES, analyze = Analyze.NO,
//        bridge = @FieldBridge(impl = JsonBridge.class, params = @Parameter(name = "class", value = "[Lnl.vpro.domain.media.support.Image;")))
    protected List<Image> images;

    @Column(nullable = false)
    @JsonIgnore // Oh Jackson2...
    protected boolean isEmbeddable = true;

    // The sortDate field is actually calculatable, but it can be a bit expensive, so we cache it's value in a persistent field.
    @Column(name = "sortdate", nullable = true, unique = false)
    protected Instant sortDate;

    // Used for monitoring publication delau. Not exposed via java.
    // Set its value in sql to now() when unmodified media is republished.
    @Column(name = "repubDate", nullable = true, unique = false)
    protected Instant repubDate;

    @Column(nullable = false)
    @JsonIgnore // Oh Jackson2...
    private Boolean locationAuthorityUpdate = false;

    @OneToOne
    private MediaObject mergedTo;

    // Holds the descendantOf value when unmarshalled from XML. Used by XML
    // clients working in a detached environment.
    @Transient
    Set<DescendantRef> descendantOf;

    // Holds the descendantOf value when unmarshalled from XML. Used by XML
    // clients working in a detached environment.
    @Transient
    private String mergedToRef;


    /**
     * If this is set to true, then that indicates that something is changed in the mediaobject which would require
     * a recalculation of the sort date.
     */
    @Transient
    private boolean sortDateValid = false;

    /**
     * If this is set to false, then that indicates that the sort date was set _explictely_ (JAXB unmarshalling), and no other setters can
     * invalidate that.
     */
    @Transient
    private boolean sortDateInvalidatable = true;

    public MediaObject() {
    }

    public MediaObject(long id) {
        super(id);
    }

    public MediaObject(MediaObject source) {
        super(source);
        this.avType = source.avType;
        this.mid = source.mid;
        this.setEmbeddable(source.isEmbeddable);
        source.getCrids().forEach(this::addCrid);
        source.getBroadcasters().forEach(this::addBroadcaster);
        source.getPortals().forEach(this::addPortal);
        source.getPortalRestrictions().forEach(restriction -> this.addPortalRestriction(PortalRestriction.copy(restriction)));
        source.getGeoRestrictions().forEach(restriction -> this.addGeoRestriction(GeoRestriction.copy(restriction)));
        source.getTitles().forEach(title -> this.addTitle(Title.copy(title, this)));
        source.getDescriptions().forEach(description -> this.addDescription(Description.copy(description, this)));
        source.getGenres().forEach(this::addGenre);
        source.getTags().forEach(this::addTag);
        this.source = source.source;
        source.getCountries().forEach(this::addCountry);
        source.getLanguages().forEach(this::addLanguage);
        this.avAttributes = AVAttributes.copy(source.avAttributes);
        this.releaseYear = source.releaseYear;
        this.duration = Duration.copy(source.duration);
        source.getPersons().forEach(person -> this.addPerson(Person.copy(person, this)));
        source.getAwards().forEach(this::addAward);
        source.getMemberOf().forEach(ref -> this.createMemberOf(ref.getOwner(), ref.getNumber()));
        this.ageRating = source.ageRating;
        source.getContentRatings().forEach(this::addContentRating);
        source.getEmail().forEach(this::addEmail);
        source.getWebsites().forEach(website -> this.addWebsite(Website.copy(website)));
        source.getTwitterRefs().forEach(ref -> this.addTwitterRef(TwitterRef.copy(ref)));
        this.teletext = source.teletext;
        source.getPredictions().forEach(prediction -> {
            this.updatePrediction(prediction.getPlatform(), prediction.getPublishStart(), prediction.getPublishStop());
            this.updatePrediction(prediction.getPlatform(), prediction.getState());
        });
        source.getLocations().forEach(location -> this.addLocation(Location.copy(location, this)));
        source.getScheduleEvents().forEach(scheduleevent -> this.addScheduleEvent(ScheduleEvent.copy(scheduleevent, this)));
        source.getRelations().forEach(relation -> this.addRelation(Relation.copy(relation)));
        source.getImages().forEach(images -> this.addImage(Image.copy(images)));
        this.mergedTo = source.mergedTo;
    }

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(urn.lastIndexOf(':') + 1);
        return Long.valueOf(id);
    }

    protected static Date getDurationAsDate(Duration duration) {
        return duration != null ? duration.getValue() : null;
    }

    private static Prediction getPrediction(Platform platform, Collection<Prediction> preds) {
        if (preds != null) {
            for (Prediction prediction : preds) {
                if (prediction.getPlatform().equals(platform)) {
                    return prediction;
                }
            }
        }
        return null;
    }

    protected static <T> List<T> updateList(List<T> toUpdate, Collection<T> values) {
        if (toUpdate != null && toUpdate == values) {
            return toUpdate;
        }
        if (toUpdate == null) {
            toUpdate = new ArrayList<>();
        } else {
            toUpdate.clear();
        }
        if (values != null) {
            toUpdate.addAll(values);
        }
        return toUpdate;
    }

    protected static <T> Set<T> updateSortedSet(Set<T> toUpdate,
                                                      Collection<T> values) {
        if (toUpdate != null && toUpdate == values) {
            return toUpdate;
        }
        if (toUpdate == null) {
            toUpdate = new TreeSet<>();
            if (values != null) {
                toUpdate.addAll(values);
            }

        } else {
            if (values != null) {
                toUpdate.retainAll(values);
                for (T v : values) {
                    for (T toUpdateValue : toUpdate) {
                        if (toUpdateValue != null && toUpdateValue instanceof Updatable && toUpdateValue.equals(v)) {
                            //noinspection unchecked
                            ((Updatable) toUpdateValue).update(v);
                        }
                    }
                }
                toUpdate.addAll(values);
            }
        }
        return toUpdate;
    }

    protected static <E> E getFromList(List<E> list) {
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @XmlAttribute(required = true)
    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        if (mid != null && mid.length() == 0) {
            mid = null;
        }
        if (this.mid != null && mid != null && !this.mid.equals(mid)) {
            throw new IllegalArgumentException("Not allowed to assign new value to MID (current = " + this.mid + ", new = " + mid + ")");
        }
        this.mid = mid;
    }

    @XmlElement(name = "crid")
    @JsonProperty("crids")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getCrids() {
        if (crids == null) {
            crids = new ArrayList<>();
        }
        return crids;
    }

    public void setCrids(List<String> crids) {
        this.crids = crids;
    }

    public MediaObject addCrid(String crid) {
        if (StringUtils.isNotBlank(crid)) {
            crid = crid.trim();
            if (crids == null) {
                crids = new ArrayList<>();
                crids.add(crid);
            } else if (!crids.contains(crid)) {
                crids.add(crid);
            }
        }
        return this;
    }

    public MediaObject removeCrid(String crid) {
        // When calling crids.remove(crid) Hibernate does not re-index the
        // collection of elements, resulting in duplicate crids and a unique
        // constraint violation. Therefore create a new collection!
        if (crid != null && crids != null) {
            List<String> newCrids = new ArrayList<>();

            for (String c : crids) {
                if (!c.equals(crid.trim())) {
                    newCrids.add(c);
                }
            }
            crids = newCrids;
        }
        return this;
    }

    @XmlElement(name = "broadcaster", required = true)
    @JsonProperty("broadcasters")
    @JsonSerialize(using = BackwardsCompatibility.BroadcasterList.Serializer.class)
    @JsonDeserialize(using = BackwardsCompatibility.BroadcasterList.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Broadcaster> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }

    public void setBroadcasters(List<Broadcaster> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public MediaObject addBroadcaster(Broadcaster broadcaster) {
        nullCheck(broadcaster, "broadcaster");

        if (this.broadcasters == null) {
            this.broadcasters = new ArrayList<>();
        }

        if (!broadcasters.contains(broadcaster)) {
            broadcasters.add(broadcaster);
        }

        return this;
    }

    public boolean removeBroadcaster(Broadcaster broadcaster) {
        if (broadcaster == null || broadcasters == null) {
            return false;
        }

        return this.broadcasters.remove(broadcaster);
    }

    public Broadcaster getMainBroadcaster() {
        return getFromList(this.broadcasters);
    }

    @XmlElement(name = "portal")
    @JsonProperty("portals")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Portal> getPortals() {
        if (portals == null) {
            portals = new ArrayList<>();
        }
        return portals;
    }

    public void setPortals(List<Portal> portals) {
        this.portals = updateList(this.portals, portals.stream().distinct().collect(Collectors.toList()));
    }

    public MediaObject addPortal(Portal portal) {
        if (portal == null) {
            return this;
        }

        if (this.portals == null) {
            this.portals = new ArrayList<>();
        }

        if (!portals.contains(portal)) {
            portals.add(portal);
        }

        return this;
    }

    public boolean removePortal(Portal portal) {
        if (portal == null || portals == null) {
            return false;
        }

        return portals.remove(portal);
    }

    public void clearPortals() {
        if (portals != null) {
            portals.clear();
        }
    }

    public List<ThirdParty> getThirdParties() {
        if (thirdParties == null) {
            thirdParties = new ArrayList<>();
        }
        return thirdParties;
    }

    public MediaObject addThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null) {
            return this;
        }

        if (this.thirdParties == null) {
            this.thirdParties = new ArrayList<>();
        } else if (thirdParties.contains(thirdParty)) {
            return this;
        }

        thirdParties.add(thirdParty);
        return this;
    }

    public boolean removeThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null || thirdParties == null) {
            return false;
        }

        return thirdParties.remove(thirdParty);
    }

    public void clearThirdParties() {
        thirdParties.clear();
    }

    @XmlElement(name = "exclusive")
    @JsonProperty("exclusives")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<PortalRestriction> getPortalRestrictions() {
        if (this.portalRestrictions == null) {
            this.portalRestrictions = new ArrayList<>();
        }
        return this.portalRestrictions;
    }

    public void setPortalRestrictions(List<PortalRestriction> portalRestrictions) {
        this.portalRestrictions = portalRestrictions;
    }

    public boolean removePortalRestriction(PortalRestriction restriction) {
        if (this.portalRestrictions != null) {
            return this.portalRestrictions.remove(restriction);
        }
        return false;
    }

    public PortalRestriction findPortalRestriction(Long id) {
        if (portalRestrictions != null) {
            for (PortalRestriction portalRestriction : portalRestrictions) {
                if (portalRestriction.getId().equals(id)) {
                    return portalRestriction;
                }
            }
        }

        return null;
    }

    public void addPortalRestriction(PortalRestriction restriction) {
        if (restriction == null) {
            throw new IllegalArgumentException("PortalRestriction to add should not be null");
        }

        if (this.portalRestrictions == null) {
            this.portalRestrictions = new ArrayList<>();
        }

        if (!portalRestrictions.contains(restriction)) {
            this.portalRestrictions.add(restriction);
        }
    }

    @XmlElement(name = "region")
    @JsonProperty("regions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<GeoRestriction> getGeoRestrictions() {
        if (geoRestrictions == null) {
            geoRestrictions = new ArrayList<>();
        }
        return geoRestrictions;
    }

    public void setGeoRestrictions(List<GeoRestriction> geoRestrictions) {
        this.geoRestrictions = geoRestrictions;
    }

    public GeoRestriction findGeoRestriction(Long id) {
        if (geoRestrictions != null) {
            for (GeoRestriction geoRestriction : geoRestrictions) {
                if (geoRestriction.getId().equals(id)) {
                    return geoRestriction;
                }
            }
        }

        return null;
    }

    public void addGeoRestriction(GeoRestriction restriction) {
        if (restriction == null) {
            throw new IllegalArgumentException("Null GeoRestriction argument not allowed");
        }

        if (geoRestrictions == null) {
            geoRestrictions = new ArrayList<>();
        }

        if (!geoRestrictions.contains(restriction)) {
            geoRestrictions.add(restriction);
            markCeresUpdate();
        }
    }

    public boolean removeGeoRestriction(GeoRestriction restriction) {
        if (this.geoRestrictions != null && this.geoRestrictions.remove(restriction)) {
            markCeresUpdate();
            return true;
        }
        return false;
    }

    @XmlElement(name = "title", required = true)
    @JsonProperty("titles")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Title> getTitles() {
        if (titles == null) {
            titles = new TreeSet<>();
        }

        return sorted(titles);
    }

    public void setTitles(SortedSet<Title> titles) {
        this.titles = titles;
        for (Title t : titles) {
            t.setParent(this);
        }
    }

    public MediaObject addTitle(Title title) {
        if (title != null) {
            title.setParent(this);
            if (titles == null) {
                titles = new TreeSet<>();
            } else {
                titles.remove(title);
            }
            titles.add(title);
        }
        return this;
    }

    public boolean removeTitle(Title title) {
        if (titles == null) {
            return false;
        }

        return titles.remove(title);
    }

    public boolean removeTitle(OwnerType owner, TextualType type) {
        if (titles == null) {
            return false;
        }

        return titles.remove(new Title("", owner, type));
    }

    public MediaObject removeTitlesForOwner(OwnerType owner) {
        if (titles != null) {

            titles.removeIf(title -> title.getOwner().equals(owner));
        }
        return this;
    }

    public Title findTitle(TextualType type) {
        if (titles != null) {
            for (Title title : titles) {
                if (type == title.getType()) {
                    return title;
                }
            }
        }
        return null;
    }

    public Title findTitle(OwnerType owner, TextualType type) {
        if (titles != null) {
            for (Title title : titles) {
                if (owner == title.getOwner() && type == title.getType()) {
                    return title;
                }
            }
        }
        return null;
    }

    public MediaObject addTitle(String title, OwnerType owner, TextualType type) {
        final Title existingTitle = findTitle(owner, type);

        if (existingTitle != null) {
            existingTitle.setTitle(title);
        } else {
            this.addTitle(new Title(title, owner, type));
        }

        return this;
    }

    public String getMainTitle() {
        return MediaObjects.getTitle(sorted(titles), TextualType.MAIN);
    }

    public void setMainTitle(String mainTitle) {
        addTitle(mainTitle, OwnerType.BROADCASTER, TextualType.MAIN);
    }

    /**
     * Retrieves the first sub- or episode title. MIS distributes episode
     * titles. For internal use this episode title is handled as a subtitle.
     *
     * @return - the first subtitle
     */
    public String getSubTitle() {
        return MediaObjects.getTitle(titles, TextualType.SUB);
    }

    public String getShortTitle() {
        return MediaObjects.getTitle(titles, TextualType.SHORT);
    }

    public String getOriginalTitle() {
        return MediaObjects.getTitle(titles, TextualType.ORIGINAL);
    }

    public String getWorkTitle() {
        return MediaObjects.getTitle(titles, TextualType.WORK);
    }

    public String getLexicoTitle() {
        String string = MediaObjects.getTitle(titles, TextualType.LEXICO);
        if (StringUtils.isEmpty(string)) {
            return getMainTitle();
            //return StringUtils.isNotEmpty(string) ? string : TextUtil.getLexico(getMainTitle(), new Locale("nl", "NL"));
        } else {
            return string;
        }
    }

    /**
     * @since 2.1
     */
    public String getAbbreviatedTitle() {
        return MediaObjects.getTitle(titles, TextualType.ABBREVIATION);
    }

    @XmlElement(name = "description")
    @JsonProperty("descriptions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Description> getDescriptions() {
        if (descriptions == null) {
            descriptions = new TreeSet<>();
        }
        return sorted(descriptions);
    }

    public void setDescriptions(SortedSet<Description> descriptions) {
        this.descriptions = descriptions;
        for (Description d : descriptions) {
            d.setParent(this);
        }
    }

    public MediaObject addDescription(Description description) {
        if (description != null) {
            description.setParent(this);
            if (descriptions == null) {
                descriptions = new TreeSet<>();
            } else {
                descriptions.remove(description);
            }
            descriptions.add(description);
        }
        return this;
    }

    public boolean removeDescription(Description description) {
        if (descriptions == null) {
            return false;
        }

        return descriptions.remove(description);
    }

    public boolean removeDescription(OwnerType owner, TextualType type) {
        return removeDescription(new Description("", owner, type));
    }

    public MediaObject removeDescriptionsForOwner(OwnerType owner) {
        if (descriptions != null) {

            descriptions.removeIf(description -> description.getOwner().equals(owner));
        }
        return this;
    }

    public Description findDescription(TextualType type) {
        if (descriptions != null) {
            for (Description description : descriptions) {
                if (type == description.getType()) {
                    return description;
                }
            }
        }
        return null;
    }

    public Description findDescription(OwnerType owner, TextualType type) {
        if (descriptions != null) {
            for (Description description : descriptions) {
                if (owner == description.getOwner()
                    && type == description.getType()) {
                    return description;
                }
            }
        }
        return null;
    }

    public MediaObject addDescription(String description, OwnerType owner,
                                      TextualType type) {
        final Description existingDescription = findDescription(owner, type);

        if (existingDescription != null) {
            existingDescription.setDescription(description);
        } else {
            this.addDescription(new Description(description, owner, type));
        }

        return this;
    }

    public String getMainDescription() {
        if (descriptions != null && descriptions.size() > 0) {
            return sorted(descriptions).first().getDescription();
        }
        return null;
    }

    public String getSubDescription() {
        if (descriptions != null) {
            for (Description description : descriptions) {
                if (description.getType().equals(TextualType.SUB)) {
                    return description.getDescription();
                }
            }
        }
        return null;
    }

    public String getShortDescription() {
        if (descriptions != null) {
            for (Description description : sorted(descriptions)) {
                if (description.getType().equals(TextualType.SHORT)) {
                    return description.getDescription();
                }
            }
        }
        return null;
    }

    @XmlElement(name = "genre")
    @JsonProperty("genres")
    @JsonSerialize(using = BackwardsCompatibility.GenreSortedSet.Serializer.class)
    @JsonDeserialize(using = BackwardsCompatibility.GenreSortedSet.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Genre> getGenres() {
        if (genres == null) {
            genres = new TreeSet<>();
        }
        return sorted(genres);
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = updateSortedSet(this.genres, genres);
    }

    public MediaObject addGenre(Genre genre) {
        nullCheck(genre, "genre");

        if (genres == null) {
            genres = new TreeSet<>();
        }

        if (!genres.contains(genre)) {
            genres.add(genre);
        }

        return this;
    }

    boolean removeGenre(Genre genre) {
        return getGenres().remove(genre);
    }

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Tag> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return sorted(tags);
    }

    /**
     * Consider using nl.vpro.domain.media.TagService#findOrCreate() first.
     */
    public void setTags(Set<Tag> tags) {
        this.tags = updateSortedSet(this.tags, tags);
    }

    public MediaObject addTag(Tag tag) {
        nullCheck(tag, "tag");

        if (tags == null) {
            tags = new TreeSet<>();
        }

        if (!tags.contains(tag)) {
            tags.add(tag);
        }
        return this;
    }

    boolean removeTag(Tag tag) {
        return tags != null && tags.remove(tag);
    }

    @XmlElement
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @XmlElement(name = "country")
    @JsonProperty("countries")
    @JsonSerialize(using = BackwardsCompatibility.CountryCodeList.Serializer.class)
    @JsonDeserialize(using = BackwardsCompatibility.CountryCodeList.Deserializer.class)
    @XmlJavaTypeAdapter(value = CountryCodeAdapter.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<CountryCode> getCountries() {
        if (countries == null) {
            countries = new ArrayList<>();
        }
        return countries;
    }

    public void setCountries(List<CountryCode> countries) {
        this.countries = updateList(this.countries, countries);
    }

    public MediaObject addCountry(String code) {
        CountryCode country = CountryCode.getByCode(code, false);
        if (country == null) {
            throw new IllegalArgumentException("Unknown country " + code);
        }
        return addCountry(country);

    }

    public MediaObject addCountry(CountryCode country) {
        nullCheck(country, "country");

        if (countries == null) {
            countries = new ArrayList<>();
        }

        if (!countries.contains(country)) {
            countries.add(country);
        }
        return this;
    }

    @XmlElement(name = "language")
    @XmlJavaTypeAdapter(value = LocaleAdapter.class)
    @JsonProperty("languages")
    @JsonSerialize(using = BackwardsCompatibility.LanguageList.Serializer.class)
    @JsonDeserialize(using = BackwardsCompatibility.LanguageList.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Locale> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<>();
        }
        return languages;
    }

    public void setLanguages(List<Locale> languages) {
        this.languages = updateList(this.languages, languages);
    }

    public MediaObject addLanguage(Locale language) {
        nullCheck(language, "language");

        if (languages == null) {
            languages = new ArrayList<>();
        }

        if (!languages.contains(language)) {
            languages.add(language);
        }

        return this;
    }

    @XmlAttribute(name = "avType", required = true)
    @JsonProperty("avType")
    public AVType getAVType() {
        return avType;
    }

    public void setAVType(AVType avType) {
        this.avType = avType;
    }

    @XmlElement
    public AVAttributes getAvAttributes() {
        return avAttributes;
    }

    public void setAvAttributes(AVAttributes avAttributes) {
        this.avAttributes = avAttributes;
    }

    @XmlElement()
    public Short getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Short releaseYear) {
        this.releaseYear = releaseYear;
    }

    @XmlElement()
    public Duration getDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    @JsonIgnore
    @XmlTransient
    public void setDuration(java.time.Duration duration) throws ModificationException {
        if (this.duration != null && ObjectUtils.notEqual(this.duration.get(), duration) && hasAuthorizedDuration()) {
            throw new ModificationException("Updating an existing and authorized duration is not allowed");
        }
        if (duration == null) {
            this.duration = null;
        } else if (this.duration == null) {
            this.duration = Duration.of(duration);
        } else {
            this.duration.set(duration);
        }
    }

    public void setDurationWithDate(Date duration) throws ModificationException {
        Date oldDuration = getDurationAsDate(this.duration);
        if (ObjectUtils.notEqual(oldDuration, duration) && hasAuthorizedDuration()) {
            throw new ModificationException("Updating an existing and authorized duration is not allowed");
        }

        if (duration == null) {
            this.duration = null;
        } else if (this.duration == null) {
            this.duration = new Duration(duration);
        } else {
            this.duration.setValue(duration);
        }

    }

    public Date getDurationAsDate() {
        return getDurationAsDate(duration);
    }

    public boolean hasAuthorizedDuration() {
        return duration != null && duration.isAuthorized();
    }

    @XmlElementWrapper(name = "credits")
    @XmlElement(name = "person")
    @JsonProperty("credits")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Person> getPersons() {
        if (persons == null) {
            persons = new ArrayList<>();
        }
        return persons;
    }

    public void setPersons(List<Person> persons) {
        for (Person person : persons) {
            person.setMediaObject(this);
        }
        this.persons = updateList(this.persons, persons);
    }

    public boolean removePerson(Person person) {
        if (persons != null) {
            return persons.remove(person);
        }
        return false;
    }

    public boolean removePerson(Long id) {
        if (persons == null) {
            return false;
        }

        for (Person person : persons) {
            if (id.equals(person.getId())) {
                return removePerson(person);
            }
        }

        return false;
    }

    public MediaObject addPerson(Person person) {
        if (persons == null) {
            persons = new ArrayList<>();
        }

        if (!persons.contains(person)) {
            if (person != null) {
                person.setMediaObject(this);
                person.setListIndex(persons.size());
                persons.add(person);
            }
        }

        return this;
    }

    public Person findPerson(Person person) {
        if (persons == null) {
            return null;
        }

        for (Person p : persons) {
            if (p.equals(person)) {
                return p;
            }
        }

        return null;
    }

    public Person findPerson(Long id) {
        if (persons == null) {
            return null;
        }

        for (Person p : persons) {
            if (p.getId().equals(id)) {
                return p;
            }
        }

        return null;
    }

    @XmlElement(name = "award")
    @JsonProperty("awards")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getAwards() {
        if (awards == null) {
            awards = new ArrayList<>();
        }
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = updateList(this.awards, awards);
    }

    public MediaObject addAward(String award) {
        if (awards == null) {
            awards = new ArrayList<>();
        }

        if (!awards.contains(award)) {
            awards.add(award);
        }

        return this;
    }

    @XmlElement(name = "descendantOf")
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<DescendantRef> getDescendantOf() {
        if (descendantOf == null) {
            descendantOf = new TreeSet<>();
            for (MediaObject media : getAncestors()) {
                descendantOf.add(DescendantRef.forOwner(media));
            }
        }
        return sorted(descendantOf);
    }


    void setDescendantOf(Set<DescendantRef> descendantOf) {
        this.descendantOf = updateSortedSet(this.descendantOf, descendantOf);
    }

    @XmlElement()
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<MemberRef> getMemberOf() {
        if (memberOf == null) {
            memberOf = new TreeSet<>();
        }
        return sorted(memberOf);
    }

    public void setMemberOf(SortedSet<MemberRef> memberOf) {
        this.memberOf = memberOf;
    }

    public boolean isMember() {
        return memberOf != null && memberOf.size() > 0;
    }

    public boolean isMemberOf(MediaObject owner) {
        if (!isMember()) {
            return false;
        }

        for (MemberRef memberRef : memberOf) {
            if (memberRef.getOwner().equals(owner)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMemberOf(MediaObject owner, Integer number) {
        if (!isMember()) {
            return false;
        }

        if (number == null) {
            return isMemberOf(owner);
        }

        for (MemberRef memberRef : memberOf) {
            if (memberRef.getOwner().equals(owner)
                && memberRef.getNumber().equals(number)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasMember(MediaObject member) {
        return member.isMemberOf(this);
    }

    public MemberRef findMemberOfRef(Long memberRefId) {
        for (MemberRef memberRef : memberOf) {
            if (memberRefId.equals(memberRef.getId())) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findMemberOfRef(MediaObject owner) {
        for (MemberRef memberRef : memberOf) {
            if (owner.equals(memberRef.getOwner())) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findMemberOfRef(MediaObject owner, Integer number) {
        if (memberOf == null) {
            return null;
        }

        for (MemberRef memberRef : memberOf) {
            if (memberRef != null) {
                if (owner.equals(memberRef.getOwner())) {
                    if (number == null && memberRef.getNumber() == null
                        || number != null
                        && number.equals(memberRef.getNumber())) {

                        return memberRef;
                    }
                }
            }
        }
        return null;
    }

    MemberRef createMember(MediaObject member, Integer number) throws CircularReferenceException {
        if (number == null) {
            throw new IllegalArgumentException(
                "Must supply an ordering number.");
        }

        if (this.equals(member) || this.hasAncestor(member)) {
            throw new CircularReferenceException(this, findAncestry(member));
        }

        if (member.memberOf == null) {
            member.memberOf = new TreeSet<>();
        }

        MemberRef memberRef = new MemberRef(member, this, number);
        member.memberOf.add(memberRef);
        member.descendantOf = null;
        return memberRef;
    }

    MemberRef createMemberOf(MediaObject owner, Integer number) throws CircularReferenceException {
        return owner.createMember(this, number);
    }

    boolean removeMemberOfRef(MediaObject reference) {
        boolean success = false;
        if (memberOf != null) {
            Iterator<MemberRef> it = memberOf.iterator();

            while (it.hasNext()) {
                MemberRef memberRef = it.next();

                if (memberRef.getOwner().equals(reference)) {
                    it.remove();
                    success = true;
                    descendantOf = null;
                }
            }
        }
        return success;
    }

    boolean removeMemberOfRef(Long memberRefId) {
        boolean success = false;
        if (memberOf != null) {
            Iterator<MemberRef> it = memberOf.iterator();

            while (it.hasNext()) {
                MemberRef memberRef = it.next();

                if (memberRef.getId().equals(memberRefId)) {
                    it.remove();
                    success = true;
                    descendantOf = null;
                }
            }
        }
        return success;
    }

    boolean removeMemberOfRef(MemberRef memberRef) {
        boolean success = false;
        if (memberOf != null) {
            Iterator<MemberRef> it = memberOf.iterator();

            while (it.hasNext()) {
                MemberRef existing = it.next();

                if (existing.equals(memberRef)) {
                    it.remove();
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    @XmlElement()
    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(AgeRating ageRating) {
        if (this.ageRating != ageRating) {
            this.locationAuthorityUpdate = true;
        }
        this.ageRating = ageRating;
    }

    @Override
    @XmlElement(name = "contentRating")
    @JsonProperty("contentRatings")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<ContentRating> getContentRatings() {
        if (contentRatings == null) {
            contentRatings = new ArrayList<>();
        }
        return contentRatings;
    }

    public MediaObject setContentRatings(List<ContentRating> contentRatings) {
        this.contentRatings = updateList(this.contentRatings, contentRatings);
        return this;
    }

    public MediaObject addContentRating(ContentRating rating) {
        if (rating == null) {
            return this;
        }

        if (contentRatings == null) {
            contentRatings = new ArrayList<>();
        }

        if (!contentRatings.contains(rating)) {
            contentRatings.add(rating);
        }

        return this;
    }

    @XmlElement()
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getEmail() {
        if (email == null) {
            email = new ArrayList<>();
        }
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = updateList(this.email, email);
    }

    public String getMainEmail() {
        return getFromList(email);
    }

    public MediaObject addEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return this;
        }

        if (this.email == null) {
            this.email = new ArrayList<>();
        }

        email = email.trim();
        if (!this.email.contains(email)) {
            this.email.add(email);
        }

        return this;
    }

    @XmlElement(name = "website")
    @JsonProperty("websites")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Website> getWebsites() {
        if (websites == null) {
            websites = new ArrayList<>();
        }
        return websites;
    }

    public MediaObject setWebsites(List<Website> websites) {
        this.websites = updateList(this.websites, websites);
        return this;
    }

    public Website getMainWebsite() {
        return getFromList(websites);
    }

    public Website findWebsite(Long id) {
        for (Website website : websites) {
            if (id.equals(website.getId())) {
                return website;
            }
        }
        return null;
    }

    public Website findWebsite(Website website) {
        int index = websites.indexOf(website);
        if (index >= 0) {
            return websites.get(index);
        }
        return null;
    }

    public Website getWebsite(final Website website) {
        for (Website existing : websites) {
            if (existing.equals(website)) {
                return existing;
            }
        }
        return null;
    }

    public void addWebsite(final Website website) {
        if (website != null) {
            getWebsites().remove(website);
            websites.add(website);
        }
    }

    public void addWebsite(int index, final Website website) {
        if (website != null) {
            getWebsites().remove(website);
            if (index < websites.size()) {
                websites.add(index, website);
            } else {
                websites.add(website);
            }
        }
    }

    public boolean removeWebsite(final Long id) {
        for (Iterator<Website> iterator = websites.iterator(); iterator.hasNext(); ) {
            Website website = iterator.next();
            if (id.equals(website.getId())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeWebsite(final Website website) {
        return getWebsites().remove(website);
    }

    @XmlElement(name = "twitter")
    @JsonProperty("twitter")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<TwitterRef> getTwitterRefs() {
        if (twitterRefs == null) {
            twitterRefs = new ArrayList<>();
        }
        return twitterRefs;
    }

    public void setTwitterRefs(List<TwitterRef> twitterRefs) {
        this.twitterRefs = updateList(this.twitterRefs, twitterRefs);
    }

    public void addTwitterRef(TwitterRef ref) {
        if (twitterRefs == null) {
            twitterRefs = new ArrayList<>();
        }

        if (!twitterRefs.contains(ref)) {
            twitterRefs.add(ref);
        }
    }

    @XmlElement()
    public Short getTeletext() {
        return teletext;
    }

    public void setTeletext(Short teletext) {
        this.teletext = teletext;
    }

    @XmlAttribute
    public Boolean isHasSubtitles() {
        return hasSubtitles;
    }

    public void setHasSubtitles(Boolean hasSubtitles) {
        this.hasSubtitles = hasSubtitles;
    }

    public boolean isHasSubtitlesNotNull() {
        return hasSubtitles == null ? false : hasSubtitles;
    }

    public boolean hasEpisode(Program episode) {
        return episode.isEpisodeOf(this);
    }

    public boolean hasAncestor(MediaObject ancestor) {
        if (!isMember()) {
            return false;
        }

        for (MemberRef memberRef : memberOf) {
            if (memberRef.getOwner().equals(ancestor)
                || memberRef.getOwner().hasAncestor(ancestor)) {
                return true;
            }
        }

        return false;
    }

    public List<MediaObject> findAncestry(MediaObject ancestor) {
        List<MediaObject> ancestry = new ArrayList<>();
        findAncestry(ancestor, ancestry);
        return ancestry;
    }

    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        if (isMember()) {
            for (MemberRef memberRef : memberOf) {
                if (memberRef.getOwner().equals(ancestor)) {
                    ancestors.add(ancestor);
                    return;
                }

                memberRef.getOwner().findAncestry(ancestor, ancestors);
                if (!ancestors.isEmpty()) {
                    ancestors.add(memberRef.getOwner());
                    return;
                }
            }
        }
    }

    public boolean hasDescendant(MediaObject descendant) {
        return descendant.hasAncestor(this);
    }

    void addAncestors(SortedSet<MediaObject> set) {
        if (isMember()) {
            for (MemberRef memberRef : memberOf) {
                final MediaObject reference = memberRef.getOwner();
                if (reference != null) {
                    if (set.add(reference)) { // avoid stack overflow if object happens to be descendant of it  self
                        reference.addAncestors(set);
                    }
                }
            }
        }
    }

    public SortedSet<MediaObject> getAncestors() {
        SortedSet<MediaObject> set = new TreeSet<>(
            (mediaObject, mediaObject1) -> {
                if (mediaObject == null || mediaObject1 == null) {
                    return 1;
                }

                if (mediaObject.getId() == null) {
                    if (mediaObject1.getId() == null) {
                        return mediaObject1.hashCode() - mediaObject.hashCode();
                    } else {
                        return 1;
                    }
                }

                return mediaObject.getId().compareTo(
                    mediaObject1.getId() == null ? 0 : mediaObject1.getId());
            }
        );
        addAncestors(set);
        return set;
    }

    @XmlElement(name = "prediction")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("predictions")
    public SortedSet<Prediction> getPredictions() {
        if (predictions == null) {
            predictions = new TreeSet<>();
        }

        // SEE https://jira.vpro.nl/browse/MSE-2313
        return new SortedSetSameElementWrapper<Prediction>(sorted(predictions)) {
            @Override
            protected Prediction adapt(Prediction prediction) {
                if (prediction.getState() == Prediction.State.ANNOUNCED) {
                    for (Location location : MediaObject.this.getLocations()) {
                        if (location.getPlatform() == prediction.getPlatform()) {
                            log.info("Silentely set state of {} to REALIZED (by {}) of object {}", prediction, location.getProgramUrl(), MediaObject.this.mid);
                            realizePrediction(prediction, location);
                            MediaObjects.markForRepublication(MediaObject.this);
                            break;
                        }
                    }
                }
                return prediction;

            }
        };
    }

    public void setPredictions(Collection<Prediction> predictions) {
        this.predictions = updateSortedSet(this.predictions, predictions);
    }

    public Prediction getPrediction(Platform platform) {
        return getPrediction(platform, getPredictions());
    }

    public void updatePrediction(Platform platform, Date publishStart, Date publishStop) {
        Prediction prediction = findOrCreatePrediction(platform);
        prediction.setPublishStart(publishStart);
        prediction.setPublishStop(publishStop);
    }

    public void updatePrediction(Platform platform, Prediction.State state) {
        Prediction prediction = findOrCreatePrediction(platform);
        prediction.setState(state);
    }

    void realizePrediction(Location location) {
        if (locations == null || (!locations.contains(location) && findLocation(location.getId()) == null)) {
            throw new IllegalArgumentException("Can only realize a prediction when accompanying locations is available. Location " + location + " is not available in " + getMid() + " " + locations);
        }

        Platform platform = location.getPlatform();
        if (platform == null) {
            log.debug("Can't realize prediction with location {} because it has no platform", location);
            return;
        }

        Prediction prediction = findOrCreatePrediction(platform);
        realizePrediction(prediction, location);

    }

    void realizePrediction(Prediction prediction, Location location) {
        prediction.setState(Prediction.State.REALIZED);
        Platform platform = location.getPlatform();
        if (platform != null) {
            if (platform == prediction.getPlatform()) {
                prediction.setPublishStart(location.getPublishStart());
                prediction.setPublishStop(location.getPublishStop());
            } else {
                throw new IllegalArgumentException("");
            }

        }
    }

    public boolean removePrediction(Platform platform) {
        if (predictions == null) {
            return false;
        }

        return predictions.remove(new Prediction(platform));
    }

    private Prediction findOrCreatePrediction(Platform platform) {
        Prediction prediction = getPrediction(platform, this.predictions);
        if (prediction == null) {
            prediction = new Prediction(platform);
            prediction.setMediaObject(this);
            if (predictions == null) {
                predictions = new TreeSet<>();
            }
            this.predictions.add(prediction);

        }
        return prediction;
    }

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    @JsonProperty("locations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Location> getLocations() {
        if (locations == null) {
            locations = new TreeSet<>();
        }
        return new ResortedSortedSet<>(locations, Location.PRESENTATION_ORDER);
    }

    public void setLocations(SortedSet<Location> locations) {
        this.locations = locations;
    }

    public Location getLocation(Location location) {
        if (locations != null) {
            for (Location existing : locations) {
                if (existing.equals(location)) {
                    return existing;
                }
            }
        }
        return null;
    }

    public Location findLocation(Long locationId) {
        if (locations != null && locationId != null) {
            for (Location location : locations) {
                if (locationId.equals(location.getId())) {
                    return location;
                }
            }
        }
        return null;
    }

    public Location findLocation(String url) {
        if (locations != null && StringUtils.isNotBlank(url)) {
            for (Location location : locations) {
                if (url.equals(location.getProgramUrl())) {
                    return location;
                }
            }
        }
        return null;
    }

    public Location findLocation(String url, OwnerType owner) {
        if (locations != null && StringUtils.isNotBlank(url) && owner != null) {
            for (Location location : locations) {
                if (location.getProgramUrl().equals(url)
                    && owner == location.getOwner()) {
                    return location;
                }
            }
        }
        return null;
    }

    public MediaObject addLocation(Location location) {
        if (location == null || location.getProgramUrl() == null) {
            throw new IllegalArgumentException(
                "Must supply a not null location with an url.");
        }

        if (locations == null) {
            locations = new TreeSet<>();
        }

        Location existing = findLocation(location.getProgramUrl());

        if (existing != null) {
            if (!Objects.equals(location.getOwner(), existing.getOwner())
                || !Objects.equals(location.getPlatform(), existing.getPlatform())) {

                throw new IllegalArgumentException("Collisions while updating " + existing + " with " + location);
            }

            existing.setAvAttributes(location.getAvAttributes());
            existing.setPublishStartInstant(location.getPublishStartInstant());
            existing.setPublishStopInstant(location.getPublishStopInstant());
            existing.setSubtitles(location.getSubtitles());
            existing.setDuration(location.getDuration());
            existing.setOffset(location.getOffset());
        } else {
            location.setMediaObject(this);
            locations.add(location);

            if (location.hasPlatform()) {
                realizePrediction(location);
            }
        }

        return this;
    }

    public boolean removeLocation(Location location) {
        if (locations != null && locations.remove(location)) {
            markCeresUpdate();
            return true;
        }
        return false;
    }

    public boolean removeLocation(final Long locationId) {
        boolean success = false;
        if (locationId != null && locations != null) {
            Iterator<Location> iterator = locations.iterator();
            while (iterator.hasNext()) {
                Location location = iterator.next();
                if (locationId.equals(location.getId())) {
                    iterator.remove();
                    markCeresUpdate();
                    success = true;
                }
            }

        }
        return success;
    }

    public void revokeLocations(Platform platform) {
        if (locations != null) {
            locations.removeIf(location -> platform.equals(location.getPlatform()));
        }

        Prediction prediction = findOrCreatePrediction(platform);
        prediction.setState(Prediction.State.REVOKED);

        if (platform == Platform.INTERNETVOD) {
            // Clearing this record re-enables asset uploads
            clearCeresRecord(Platform.INTERNETVOD);
        }
    }

    private void clearCeresRecord(Platform platform) {
        if (locationAuthorityRecords != null) {
            locationAuthorityRecords.removeIf(cr -> cr.getPlatform() == platform);
        }
    }

    public boolean hasScheduleEvents() {
        return scheduleEvents != null && scheduleEvents.size() > 0;
    }

    @XmlElementWrapper(name = "scheduleEvents")
    @XmlElement(name = "scheduleEvent")
    @JsonProperty("scheduleEvents")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonManagedReference
    public SortedSet<ScheduleEvent> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        // return Collections.unmodifiableSortedSet(scheduleEvents); Would be nice for hibernate, but jaxb gets confused (run ScheduleTest)
        return sorted(scheduleEvents);
    }

    public void setScheduleEvents(SortedSet<ScheduleEvent> scheduleEvents) {
        this.scheduleEvents = scheduleEvents;
        invalidateSortDate();
    }

    MediaObject addScheduleEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvent != null) {
            if (scheduleEvents == null) {
                scheduleEvents = new TreeSet<>();
            }
            scheduleEvents.add(scheduleEvent);
            invalidateSortDate();
        }
        return this;
    }

    boolean removeScheduleEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvents != null) {
            return scheduleEvents.remove(scheduleEvent);
        }
        return false;
    }

    @XmlElement(name = "relation")
    @JsonProperty("relations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Relation> getRelations() {
        if (this.relations == null) {
            this.relations = new TreeSet<>();
        }
        return sorted(relations);
    }

    public void setRelations(SortedSet<Relation> relations) {
        this.relations = relations;
    }

    public MediaObject addRelation(Relation relation) {
        nullCheck(relation, "relation");

        if (this.relations == null) {
            this.relations = new TreeSet<>();
        }

        this.relations.add(relation);
        return this;
    }

    public Relation findRelation(Relation relation) {
        if (relations != null) {
            for (Relation existing : relations) {
                if (Objects.equals(existing, relation)) {
                    return existing;
                }
            }
        }

        return null;
    }

    public Relation findRelation(Long id) {
        if (relations != null && id != null) {
            for (Relation relation : relations) {
                if (id.equals(relation.getId())) {
                    return relation;
                }
            }
        }

        return null;
    }

    public boolean removeRelation(Long id) {
        if (relations != null && id != null) {
            for (Iterator<Relation> iterator = relations.iterator(); iterator
                .hasNext(); ) {
                Relation relation = iterator.next();

                if (id.equals(relation.getId())) {
                    iterator.remove();
                    return true;
                }
            }
        }

        return false;
    }

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image", namespace = Xmlns.SHARED_NAMESPACE)
    @JsonProperty("images")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Image> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        // Occasionally images contains null elements due to a Hibernate
        // synchronous access issue.
        images.removeIf(Objects::isNull);

        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Image getImage(Image image) {
        if (images != null) {
            for (Image existing : getImages()) {
                if (existing.equals(image)) {
                    return existing;
                }
            }
        }
        return null;
    }

    public Image getImage(int index) {
        if (images == null || index >= images.size()) {
            return null;
        }

        return images.get(index);
    }

    public Image getImage(ImageType type) {
        if (images != null) {
            for (Image image : images) {
                if (image.getType() == type) {
                    return image;
                }
            }
        }

        return null;
    }

    public Image getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public MediaObject addImage(Image image) {
        return addImage(image, images == null ? 0 : images.size());
    }

    public MediaObject addImage(Image image, int index) {
        if (image == null) {
            throw new IllegalArgumentException();
        }
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(index, image);
        image.setMediaObject(this);
        return this;
    }
    public MediaObject clearImages() {
        if (images == null) {
            return this;
        }
        images.forEach(i -> {
            i.setMediaObject(null);
            i.setId(null);
        });
        images.clear();
        return this;
    }



    public List<Image> findImages(OwnerType owner) {
        return getImages()
            .stream()
            .filter(i -> owner.equals(i.getOwner()))
            .collect(Collectors.toList());
    }

    public Image findImage(ImageType type) {
        for (Image image : getImages()) {
            if (type.equals(image.getType())) {
                return image;
            }
        }

        return null;
    }

    public Image findImage(Long id) {
        if (images != null && id != null) {
            for (Image image : getImages()) {
                if (image != null) {
                    if (id.equals(image.getId())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    public Image findImage(String url, OwnerType owner) {
        if (images != null) {
            for (Image image : getImages()) {
                if (image != null) {
                    String uri = image.getImageUri();
                    if (uri != null && uri.equals(url)
                        && owner == image.getOwner()) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    public boolean removeImage(Image image) {
        if (images != null) {
            image.setMediaObject(null);
            return images.remove(image);
        }
        return false;
    }

    public boolean removeImage(Long imageId) {
        boolean success = false;
        if (imageId != null && images != null) {

            for (Image image : getImages()) {
                if (imageId.equals(image.getId())) {
                    success = removeImage(image);
                    break;
                }
            }
        }
        return success;
    }

    public MediaObject removeImagesForOwner(OwnerType owner) {
        if (images != null) {
            Iterator<Image> iterator = getImages().iterator();
            while (iterator.hasNext()) {
                Image image = iterator.next();
                if (image.getOwner().equals(owner)) {
                    image.setMediaObject(null);
                    iterator.remove();
                }
            }
        }
        return this;
    }

    @XmlAttribute(name = "embeddable")
    public boolean isEmbeddable() {
        return isEmbeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        isEmbeddable = embeddable;
    }

    /**
     * MediaObjects with locations managed by Ceres contain a <i>CeresRecord</i> with a Ceres Guci. These records are
     * stored here because Ceres restrictions exist independent of the locations they apply to. Once a location is added
     * it should receive the corresponding CeresRecord as well when it is an Ceres managed location.
     *
     * @return An existing CeresRecord or null when not managed by Ceres
     */
    public LocationAuthorityRecord getLocationAuthorityRecord(Platform platform) {
        if (locationAuthorityRecords != null) {
            for (LocationAuthorityRecord locationAuthorityRecord : locationAuthorityRecords) {
                if (platform.equals(locationAuthorityRecord.getPlatform())) {
                    return locationAuthorityRecord;
                }
            }
        }
        return null;
    }

    public void setLocationAuthorityRecord(LocationAuthorityRecord locationAuthorityRecord) {
        locationAuthorityRecords.add(locationAuthorityRecord);
    }

    /**
     * When true Ceres/Pluto.. needs a restriction update. The underlying field is managed by Hibernate, and not accessible.
     */
    public boolean isLocationAuthorityUpdate() {
        return locationAuthorityUpdate;
    }

    public void setLocationAuthorityUpdate(Boolean ceresUpdate) {
        this.locationAuthorityUpdate = ceresUpdate;
    }

    @Override
    public PublishableObject setPublishStartInstant(Instant publishStart) {
        if (! Objects.equals(this.publishStart, publishStart)) {
            invalidateSortDate();
            if (hasInternetVodAuthority()) {
                locationAuthorityUpdate = true;
            }
        }
        return super.setPublishStartInstant(publishStart);
    }

    @Override
    public PublishableObject setPublishStopInstant(Instant publishStop) {
        if (!Objects.equals(this.publishStop, publishStop)) {
            invalidateSortDate();
            if (hasInternetVodAuthority()) {
                locationAuthorityUpdate = true;
            }
        }
        return super.setPublishStopInstant(publishStop);
    }

    protected boolean hasInternetVodAuthority() {
        return getLocationAuthorityRecord(Platform.INTERNETVOD) != null && getLocationAuthorityRecord(Platform.INTERNETVOD).hasAuthority();
    }

    @Override
    public void setCreationInstant(Instant publishStart) {
        invalidateSortDate();
        super.setCreationInstant(publishStart);
    }

    @Override
    public void setWorkflow(Workflow workflow) {
        if (workflow == Workflow.PUBLISHED && isMerged()) {
            throw new IllegalArgumentException("Merged media should obtain workflow  \"MERGED\" instead of \"PUBLISHED\"");
        }

        if (((this.workflow == Workflow.DELETED && workflow != Workflow.DELETED) || (this.workflow != Workflow.DELETED && workflow == Workflow.DELETED)) && hasInternetVodAuthority()) {
            locationAuthorityUpdate = true;
        }

        super.setWorkflow(workflow);
    }

    /**
     * Returns the sortDate for this MediaObject. The default behaviour for this field falls back to other
     * available fields in order:
     * <ul>
     * <li>First ScheduleEvent</li>
     * <li>Publication start</li>
     * <li>Creation date</li>
     * </ul>
     * <p/>
     * Subclasses might override this behavior and supply a more explicit value persisted separately.
     *
     * @since 1.5
     */
    @JsonProperty
    //@JsonSerialize(using = DateToJsonTimestamp.Serializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    //@JsonDeserialize(using = DateToJsonTimestamp.Deserializer.class)
    @XmlAttribute(name = "sortDate", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Deprecated
    final public Date getSortDate() {
        return DateUtils.toDate(getSortInstant());
    }

    @XmlTransient
    public final Instant getSortInstant() {
        if (!sortDateValid) {
            Instant date = MediaObjects.getSortInstant(this);
            if (date != null) {
                sortDate = date;
            }
            sortDateValid = true;
        }
        return sortDate;
    }

    /**
     * Method is needed for unmarshalling. It does nothing. It may do something in overrides (as in {@link Group})
     */
    final void setSortDate(Date date) {
        this.setSortInstant(DateUtils.toInstant(date));
    }

    /**
     * Method is needed for unmarshalling. It does nothing. It may do something in overrides (as in {@link Group})
     */
    void setSortInstant(Instant date) {
        this.sortDate = date;
        this.sortDateValid = true;
        this.sortDateInvalidatable = false;

    }

    protected void invalidateSortDate() {
        if (this.sortDateInvalidatable) {
            this.sortDateValid = false;
        }
    }

    public boolean isMerged() {
        return mergedTo != null || mergedToRef != null;
    }

    public MediaObject getMergedTo() {
        return mergedTo;
    }

    public void setMergedTo(MediaObject mergedTo) {
        if (this.mergedTo != null && mergedTo != null && !this.mergedTo.equals(mergedTo)) {
            throw new IllegalArgumentException("Can not merge " + this + " to " + mergedTo + " since it is already merged to " + this.mergedTo);
        }

        int depth = 10;
        MediaObject p = mergedTo;
        if (mergedTo != null) {
            while (p.isMerged()) {
                if (this.equals(p)) {
                    throw new IllegalArgumentException("Loop while merging source " + this + " to " + mergedTo);
                }
                if (depth-- == 0) {
                    throw new IllegalArgumentException("Deep regression while merging source " + this + " to " + mergedTo);
                }

                p = p.getMergedTo();
            }
        }

        this.mergedTo = p;
    }

    @XmlAttribute(name = "mergedTo")
    @JsonProperty
    public String getMergedToRef() {
        if (mergedToRef != null) {
            return mergedToRef;
        }

        return mergedTo != null ? mergedTo.getMid() : null;
    }

    public void setMergedToRef(String mergedToRef) {
        this.mergedToRef = mergedToRef;
    }

    /**
     * This setter is not intended for normal use in code. RepubDate is meant for monitoring the publication delays in
     * NewRelic. It contains the scheduled publication date of this MediaObject. This field is set (in SQL) when
     * republishing descendants, and (in code) when revoking locations/images. When the republication delay reaches a
     * certain value an alert is raised in NewRelic.
     */
    public void setRepubDate(Instant repubDate) {
        this.repubDate = repubDate;
    }

    private void markCeresUpdate() {
        // Shouldn't this check on authoritative?
        if (getLocationAuthorityRecord(Platform.INTERNETVOD) != null) {
            locationAuthorityUpdate = true;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("crids", crids)
            .append("broadcasters", broadcasters)
            .append("portals", portals)
            .append("thirdParties", thirdParties)
            .append("portalRestrictions", portalRestrictions)
            .append("geoRestrictions", geoRestrictions)
            .append("titles", titles)
            .append("descriptions", descriptions)
            .append("genres", genres)
            .append("tags", tags)
            .append("source", source)
            .append("countries", countries)
            .append("languages", languages)
            .append("avType", avType)
            .append("avAttributes", avAttributes)
            .append("releaseYear", releaseYear)
            .append("duration", duration)
            .append("persons", persons)
            .append("awards", awards)
            .append("memberOf", memberOf)
            .append("ageRating", ageRating)
            .append("contentRatings", contentRatings)
            .append("email", email)
            .append("websites", websites)
            .append("teletext", teletext)
            .append("locations", locations)
            .append("scheduleEvents", scheduleEvents)
            .append("relations", relations)
            .append("images", images)
            .append("isEmbeddable", isEmbeddable)
            .append("descendantOf", descendantOf)
            .toString();
    }

    public abstract SubMediaType getType();

    /**
     * @since 3.2
     */
    public final MediaType getMediaType() {
        SubMediaType subMediaType = getType();
        return subMediaType == null ? null : subMediaType.getMediaType();
    }

    private void nullCheck(Object o, String type) {
        if (o == null) {
            throw new IllegalArgumentException("Received a null " + type + " argument");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof MediaObject) {
            return super.equals(o) || equalsOnMid((MediaObject) o);
        } else {
            return super.equals(o);
        }
    }

    private boolean equalsOnMid(MediaObject o) {
        return mid != null && mid.equals(o.getMid());
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {

        if (locations != null) {
            for (Location loc : locations) {
                if (loc.isCeresLocation()) {
                    LocationAuthorityRecord.unknownAuthority(this, loc.getPlatform());
                }
            }
        }
        if (predictions != null) {
            for (Prediction pred : predictions) {
                LocationAuthorityRecord.unknownAuthority(this, pred.getPlatform());
            }
        }
    }

    protected static <S> SortedSet<S> sorted(Set<S> set) {
        if (set == null) {
            return null;
        }
        if (set instanceof SortedSet) {
            //noinspection unchecked
            return (SortedSet) set;
        } else {
            return new ResortedSortedSet<S>(set);
        }
    }

    @Override
    protected abstract String getUrnPrefix();


}
