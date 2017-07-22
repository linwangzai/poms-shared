/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 * Creation date 1 nov 2008.
 */
package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import javax.persistence.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.user.Editor;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.validation.PomsValidatorGroup;
import nl.vpro.validation.Publishable;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * Abstract publishable object is an abstract implemention of {@link Accountable} and {@link Embargo}.
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@Publishable(groups = {PomsValidatorGroup.class})
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "abstractPublishableObjectType", namespace = Xmlns.SHARED_NAMESPACE)
//@XmlTransient
@Slf4j
public abstract class AbstractPublishableObject<T extends AbstractPublishableObject<T>>
    extends DomainObject implements Accountable, Embargo<T> {


    @Column(nullable = false)
    protected Instant creationDate = Instant.now();

    @Column(nullable = false)
    protected Instant lastModified;

    @ManyToOne(optional = false)
    @JoinColumn(name = "createdby_principalid")
    protected Editor createdBy;


    @ManyToOne(optional = false)
    @JoinColumn(name = "lastmodifiedby_principalid")
    protected Editor lastModifiedBy;

    @Column(nullable = true)
    protected Instant publishStart;

    @Column(nullable = true)
    protected Instant  publishStop;

    @Column(nullable = true)
    protected Instant lastPublished;



    protected AbstractPublishableObject(AbstractPublishableObject<T> source) {
        this.creationDate = source.creationDate;
        this.createdBy = source.createdBy;
        this.lastModified = source.lastModified;
        this.lastModifiedBy = source.lastModifiedBy;
        this.publishStart = source.publishStart;
        this.publishStop = source.publishStop;
        this.lastPublished = source.lastPublished;
    }


    public AbstractPublishableObject() {
    }

    public AbstractPublishableObject(long id) {
        this.id = id;
    }



    @Override
    public Instant getLastModifiedInstant() {
        return lastModified;
    }

    @Override
    public void setLastModifiedInstant(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    @XmlAttribute(name = "creationDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getCreationInstant() {
        return creationDate;
    }

    @Override
    public void setCreationInstant(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Editor getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Editor createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Editor getLastModifiedBy() {
        return lastModifiedBy;
    }

    @Override
    public void setLastModifiedBy(Editor lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    @XmlAttribute(name = "publishStart")
    @JsonProperty("publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @Override
    public T setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return (T) this;
    }

    @Override
    @XmlAttribute(name = "publishStop")
    @JsonProperty("publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @Override
    public T setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return (T) this;
    }

    protected abstract String getUrnPrefix();

    // can be resolved if indeed no need to override any more
    private void setUnrecognizedUrn(String urn) {
        throw new IllegalArgumentException("The urn " + urn + " is not valid for objects with urns " + getUrnPrefix());
    }

    @XmlAttribute(name = "urn")
    @JsonProperty("urn")
    public final String getUrn() {
        return getId() == null ? null : (getUrnPrefix() + getId());
    }

    public void setUrn(String urn) {
        if(urn == null) {
            id = null;
            return;
        }
        int i = urn.lastIndexOf(':') + 1;
        if(!getUrnPrefix().equals(urn.substring(0, i))) {
            log.debug("Specified prefix '" + urn.substring(0, i) + "' is not equal to" +
                " required prefix " + getUrnPrefix());
            setUnrecognizedUrn(urn);
            return;
        }
        String id = urn.substring(i, urn.length());
        if("null".equals(id)) {
            log.debug("Urn was unset");
            setId(null);
        } else {
            setId(Long.parseLong(id));
        }
    }


    public Instant getLastPublishedInstant() {
        return lastPublished;
    }

    public void setLastPublishedInstant(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("creationDate", creationDate)
            .append("lastModified", lastModified)
            .append("createdBy", createdBy)
            .append("lastModifiedBy", lastModifiedBy)
            .append("publishStart", publishStart)
            .append("publishStop", publishStop)
            .append("lastPublished", lastPublished)
            .toString();
    }




    protected void beforeUnmarshal(Unmarshaller u, Object parent) {
        // These things appear in XML, and if they don't, they are null (and not the default value in this class)
        creationDate = null;
    }


}
