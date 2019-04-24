package nl.vpro.domain.media;

import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Data
@JsonSerialize(using = Intention.Serializer.class)
@JsonDeserialize(using = Intention.Deserializer.class)
public class Intention extends DomainObject implements Serializable, Child<Intentions> {


    @ManyToOne(targetEntity = Intentions.class, fetch = FetchType.LAZY)
    @XmlTransient
    private Intentions parent;

    @Enumerated(EnumType.STRING)
    @XmlValue
    public IntentionType value;


    public Intention() {}

    @lombok.Builder(builderClassName = "Builder")
    public Intention(IntentionType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intention intention = (Intention) o;
        return value == intention.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    public static class Serializer extends JsonSerializer<Intention> {
        @Override
        public void serialize(Intention value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getValue().name());
        }
    }


    public static class Deserializer extends JsonDeserializer<Intention> {
        @Override
        public Intention deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new Intention(IntentionType.valueOf(p.getValueAsString()));
        }
    }
}
