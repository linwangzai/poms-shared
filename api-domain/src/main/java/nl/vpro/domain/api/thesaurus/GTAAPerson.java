/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.thesaurus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.Person;
import nl.vpro.domain.media.gtaa.Label;
import nl.vpro.domain.media.gtaa.Status;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.validation.NoHtml;
import nl.vpro.w3.rdf.Description;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Slf4j
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
public class GTAAPerson implements ThesaurusObject, PersonInterface {

    private static final long serialVersionUID = 1L;

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

    @Getter
    @Setter
    private List<Label> notes;

    @Getter
    @Setter
    private List<Names> knownAs;

    @Getter
    @Setter
    @XmlElement
    private Status status;

    @Getter
    @Setter
    private String redirectedFrom;

    @Getter
    @Setter
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant lastModified;

    @Getter
    @Setter
    @XmlElement
    private String gtaaUri;

    public GTAAPerson() {

    }

    public GTAAPerson(Person person) {
    this.givenName = person.getGivenName();
    this.familyName = person.getFamilyName();
    status = person.getGtaaRecord() == null ? null : person.getGtaaRecord().getStatus();
    }

    public static GTAAPerson create(Description description) {
        return create(description, null);
    }

    public String getValue() {
        return givenName + " " + familyName;
    }

    public String getId() {
        return gtaaUri;
    }

    @Override
    public String getType() {
        return null;
    }


    public static GTAAPerson create(Description description, String submittedPrefLabel) {
        if (description == null) {
            log.info("Description is null");
            return null;
        }

        final GTAAPerson answer = new GTAAPerson();

        final Names prefName;
        if (description.getPrefLabel() != null) {
            String label = description.getPrefLabel().getValue();
            if (submittedPrefLabel != null && !submittedPrefLabel.equals(label)) {
                log.warn("Using different submitted label {} in stead of {}", submittedPrefLabel, label);
                label = submittedPrefLabel;
            }
            prefName = Names.of(label);
        } else {
            log.warn("Description has no prefLabel {}", description);
            prefName = Names.of(submittedPrefLabel);
        }
        if (prefName != null) {
            answer.givenName = prefName.getGivenName();
            answer.familyName = prefName.getFamilyName();
        }

        answer.notes = description.getScopeNote();
        answer.lastModified = description.getModified() == null ? null : description.getModified().getValue().toInstant();

        if (description.getAltLabels() != null && !description.getAltLabels().isEmpty()) {
            final List<Names> altNames = description.getAltLabels().stream().map(Names::of)
                    .collect(Collectors.toList());

            if (answer.knownAs == null) {
                answer.knownAs = altNames;
            } else {
                answer.knownAs.addAll(altNames);
            }
        }

        answer.setStatus(description.getStatus());
        answer.setGtaaUri(description.getAbout());

        return answer;
    }

    @Override
    public String getPrefLabel() {
        return familyName + (givenName != null ? ", " + givenName  : "");
    }

    @AllArgsConstructor
    @Data
    @Builder
    public static class Names {

        protected final String givenName;
        protected final String familyName;

        private static Names of(Label label) {
            if (label == null) {
                return null;
            }
            return of(label.getValue());
        }

        private static Names of(String label) {
            if (label == null) {
                return null;
            }
            Names.NamesBuilder names = Names.builder();
            int splitIndex = label.indexOf(", ");

            if (splitIndex > 0) {
                names.givenName(label.substring(splitIndex + 2));
                names.familyName(label.substring(0, splitIndex));
            } else {
                names.familyName(label);
            }
            return names.build();
        }

    }
}
