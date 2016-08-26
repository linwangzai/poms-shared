/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;

@MappedSuperclass
@Cacheable(true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organizationType", namespace = Xmlns.MEDIA_NAMESPACE)
public abstract class Organization implements Serializable, Identifiable<String>, Comparable<Organization> {

    @Id
    @XmlAttribute
    protected String id;


    @Column(nullable = false)
    @NotNull(message = "displayName not set")
    @Size(min = 1, max = 255, message = "0 < displayName length < 256")
    @XmlValue
    @JsonProperty("value")
    protected String displayName;

    protected Organization() {
    }

    public Organization(String id, String displayName) {
        setId(id);
        setDisplayName(displayName);
    }

    @Override
    @Size(min = 1, max = 255, message = "2 < id < 256")
    @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,}", message = "type should conform to: [A-Z0-9_-]{2,}")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.toUpperCase().trim();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? null : displayName.trim();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("displayName", displayName)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        Organization that = (Organization)o;

        if(id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(Organization organization) {
        return id == null ? (organization.getId() == null ? 0 : -1) :  id.toLowerCase().compareTo(organization.getId().toLowerCase());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
