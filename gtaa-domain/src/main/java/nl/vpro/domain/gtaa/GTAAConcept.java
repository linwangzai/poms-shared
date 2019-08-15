package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    property = "objectType"
)
@JsonTypeIdResolver(GTAAConceptIdResolver.class)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = GTAAPerson.class),
        @JsonSubTypes.Type(value = GTAATopic.class),
        @JsonSubTypes.Type(value = GTAATopicBandG.class),

        @JsonSubTypes.Type(value = GTAAGenre.class),
        @JsonSubTypes.Type(value = GTAAGeographicName.class),
        @JsonSubTypes.Type(value = GTAAMaker.class),
        @JsonSubTypes.Type(value = GTAAName.class),
        @JsonSubTypes.Type(value = GTAAClassification.class),

    })

public interface GTAAConcept {


    URI getId();

    List<String> getScopeNotes();

    Instant getLastModified();

    String getName();

    Status getStatus();

    URI getRedirectedFrom();


}
