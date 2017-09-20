package nl.vpro.domain.api.thesaurus;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import nl.vpro.domain.AbstractChange;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.domain.media.gtaa.ThesaurusItem;
import nl.vpro.domain.media.gtaa.ThesaurusObject;

@ToString
@EqualsAndHashCode
public class ThesaurusChange<T extends ThesaurusObject> extends AbstractChange<T> {

    @XmlAttribute
    private Long sequence;

    @XmlAttribute
    private Long revision;

    @XmlAttribute
    private String mergedTo;

    public ThesaurusChange() {
    }

    @Builder
    public ThesaurusChange(Instant publishDate, String id, Boolean deleted, Boolean tail, T object) {
        super(publishDate, id, deleted, tail, object);
    }


    @Override
    @XmlElements({
            @XmlElement(name = "person", namespace = Xmlns.MEDIA_NAMESPACE, type = GTAAPerson.class),
            @XmlElement(name = "item", type = ThesaurusItem.class)
    })

    public T getObject() {
        return super.getObject();
    }

    @Override
    public void setObject(T p) {
        super.setObject(p);
    }



    @JsonProperty("object")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = GTAAPerson.class, name = "person"),
            @JsonSubTypes.Type(value = ThesaurusItem.class, name = "item")}
    )
    protected T getJsonObject() {
        return super.getObject();
    }


}



