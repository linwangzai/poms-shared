/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractFilter<T> implements DelegatingDisplayablePredicate<T> {

    protected Constraint<T> constraint;

    public AbstractFilter() {
    }

    public AbstractFilter(Constraint<T> constraint) {
        this.constraint = constraint;
    }

    public Constraint<T> getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint<T> constraint) {
        this.constraint = constraint;
    }
    @Override
    public Constraint<T> getPredicate() {
        return constraint;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof AbstractFilter)) {
            return false;
        }

        AbstractFilter filter = (AbstractFilter)o;

        if(constraint != null ? !constraint.equals(filter.constraint) : filter.constraint != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return constraint != null ? constraint.hashCode() : 0;
    }

    public static class Serializer extends JsonSerializer<Constraint> {
        @Override
        public void serialize(Constraint value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if(value != null) {
                jgen.writeObject(value);
            } else {
                jgen.writeNull();
            }
        }

    }

    public static class DeSerializer extends JsonDeserializer<Constraint> {
        @Override
        public Constraint deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return jp.readValueAs(Constraint.class);
        }
    }
}
