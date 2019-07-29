package nl.vpro.domain.gtaa;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import nl.vpro.openarchives.oai.Label;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "newObjectType"
)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = GTAANewPerson.class, name = "person"),
        @JsonSubTypes.Type(value = GTAANewGenericConcept.class, name = "concept")
    })
@XmlSeeAlso({
    GTAANewPerson.class,
    GTAANewGenericConcept.class
})
public interface GTAANewConcept {
    String getValue();

    List<String> getNotes();

    Scheme getObjectType();

    default List<Label> getNotesAsLabel() {
        return getNotes().stream().map(Label::forValue).collect(Collectors.toList());
    }
}
