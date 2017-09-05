/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.w3.rdf;

import static nl.vpro.domain.media.gtaa.Namespaces.DC_TERMS;
import static nl.vpro.domain.media.gtaa.Namespaces.DC_TERMS_ELEMENTS;
import static nl.vpro.domain.media.gtaa.Namespaces.OPEN_SKOS;
import static nl.vpro.domain.media.gtaa.Namespaces.SKOS;
import static nl.vpro.domain.media.gtaa.Namespaces.SKOS_XL;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import nl.vpro.domain.media.gtaa.AbstractGTAAObject;
import nl.vpro.domain.media.gtaa.Label;
import nl.vpro.domain.media.gtaa.Note;
import nl.vpro.domain.media.gtaa.Status;
import nl.vpro.domain.media.gtaa.XLLabel;
import nl.vpro.dublincore.terms.Date;
import nl.vpro.xml.bind.ZonedDateTimeXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "type", "editorialNote", "creatorResource", "modifiedBy", "prefLabel", "xlPrefLabel", "modified", "uuid", "set", "inScheme", "notation",
        "creator", "status", "tenant", "acceptedBy", "dateAccepted", "dateSubmitted", "hiddenLabels", "xlHiddenLabels", "historyNote", "altLabels",
        "xlAltLabels", "scopeNote", "changeNote" })
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Description extends AbstractGTAAObject {

    private static final String FORWARD = "Forward:";

    @XmlElement
    private ResourceElement type;

    @XmlElement(namespace = OPEN_SKOS)
    private Status status;

    @XmlElement(namespace = OPEN_SKOS)
    private String tenant;

    @XmlElement(namespace = OPEN_SKOS)
    private ResourceElement modifiedBy;

    @XmlElement(namespace = OPEN_SKOS)
    private String acceptedBy;

    @XmlElement(namespace = SKOS)
    private String notation;

    @XmlElement(namespace = DC_TERMS)
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime dateAccepted;

    @XmlElement(namespace = DC_TERMS_ELEMENTS)
    private String creator;

    @XmlElement(namespace = DC_TERMS, name = "creator")
    private ResourceElement creatorResource;

    @XmlElement(namespace = DC_TERMS)
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime dateSubmitted;

    @XmlElement(namespace = DC_TERMS)
    private Date modified;

    @XmlElement(namespace = OPEN_SKOS)
    private UUID uuid;

    @XmlElement(namespace = OPEN_SKOS)
    private ResourceElement set;

    @XmlElement(namespace = SKOS)
    private ResourceElement inScheme;

    @XmlElement(namespace = SKOS, name = "prefLabel")
    private Label prefLabel;

    @XmlElement(namespace = SKOS_XL, name = "prefLabel")
    private XLLabel xlPrefLabel;

    @XmlElement(namespace = SKOS, name = "hiddenLabel")
    @Singular
    private List<Label> hiddenLabels;

    @XmlElement(namespace = SKOS_XL, name = "hiddenLabel")
    @Singular
    private List<XLLabel> xlHiddenLabels;

    @XmlElement(namespace = SKOS)
    private Note historyNote;

    @XmlElement(namespace = SKOS, name = "altLabel")
    private List<Label> altLabels;

    @XmlElement(namespace = SKOS_XL, name = "altLabel")
    @Singular
    private List<XLLabel> xlAltLabels;

    @XmlElement(namespace = SKOS)
    private List<String> changeNote;

    @XmlElement(namespace = SKOS)
    private List<Label> scopeNote;

    @XmlElement(namespace = SKOS)
    private List<Label> editorialNote;

    public Description() {
    }

    public Description(String prefLabel) {
        this.prefLabel = new Label(prefLabel);
    }

    public boolean isPerson() {
        return getSimpleType().contains("Persoon");
    }

    public String getSimpleType() {
        return StringUtils.substringAfterLast(getInScheme().getResource(), "/");
    }

    public boolean hasRedirectedFrom() {
        return getCleanChangeNote().anyMatch(note -> StringUtils.contains(note, FORWARD));
    }

    public String getRedirectedFrom() {
        return getCleanChangeNote().filter(note -> StringUtils.contains(note, FORWARD)).map(note -> StringUtils.substringAfter(note, FORWARD)).findAny().orElse("");
    }

    private Stream<String> getCleanChangeNote() {
        return getChangeNote().stream().map(StringUtils::deleteWhitespace);
    }

    @SuppressWarnings("rawtypes")
    public static class Builder extends AbstractBuilder {

        public Builder type(String type) {
            this.type = ResourceElement.builder().resource(type).build();
            return this;
        }

        public Builder inScheme(String scheme) {
            this.inScheme = ResourceElement.builder().resource(scheme).build();
            return this;
        }

        public Builder prefLabelOrXL(boolean useXlLablel, String label, String tenant) {
            if (useXlLablel) {
                return xlPrefLabel(new XLLabel(label, tenant));
            } else {
                return prefLabel(new Label(label));
            }
        }

    }

    public void addChangeNote(String note) {
        if (changeNote == null) {
            this.changeNote = new ArrayList<>();
        }
        this.changeNote.add(note);
    }
}
