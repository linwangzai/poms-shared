package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * @author Eric Kuijt?
 */
@NoArgsConstructor
@XmlRootElement(name="newConcept")
@XmlType(
    name = "newConceptType",
    propOrder = {
    "value",
    "notes"
})
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeName("concept")
public class GTAANewGenericConcept extends AbstractGTAANewConcept  {

    @Getter
    @Setter
    @XmlElement
    private String value;

    @Getter
    @Setter
    @XmlAttribute
    private Scheme objectType;

    @lombok.Builder
    public GTAANewGenericConcept(
        String value,
        @lombok.Singular List<String> notes,
        Scheme scheme) {
        this.value = value;
        this.notes = notes;
        this.objectType = scheme;
    }


}