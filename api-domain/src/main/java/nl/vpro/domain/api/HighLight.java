package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hightlightType")
public class HighLight {


    @XmlAttribute
    private String term;

    private List<String> body;

    public HighLight() {

    }

    public HighLight(String term, String... body) {
        this.term = term;
        this.body = Arrays.asList(body);

    }


    public String getTerm() {
        return term;
    }

    public List<String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return term + ":" + body;
    }
}
