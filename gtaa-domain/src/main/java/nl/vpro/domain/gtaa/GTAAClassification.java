package nl.vpro.domain.gtaa;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@GTAAScheme(Scheme.classification)
@XmlType(name = "classification",
    propOrder = {
        "value",
        "notes",
        "redirectedFrom"
    }
)
@XmlRootElement(name = "classification")
public class GTAAClassification extends AbstractSimpleValueThesaurusItem {

    @lombok.Builder(builderClassName = "Builder")
    public GTAAClassification(String id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }
    public GTAAClassification() {

    }


    public static GTAAClassification create(Description description) {
        final GTAAClassification answer = new GTAAClassification();
        fill(description, answer);
        return answer;
    }
}

