package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.geographicname)
@XmlType(
    name = "geographicName",
    propOrder = {
    "value",
    "notes",
    "redirectedFrom"
})
@XmlRootElement(name = "geographicName")
public class GTAAGeographicName extends AbstractSimpleValueThesaurusItem  {

    @lombok.Builder(builderClassName = "Builder")
    public GTAAGeographicName(URI id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }

    public GTAAGeographicName() {

    }

    public static GTAAGeographicName create(Description description) {
        final GTAAGeographicName answer = new GTAAGeographicName();
        fill(description, answer);
        return answer;
    }


}
