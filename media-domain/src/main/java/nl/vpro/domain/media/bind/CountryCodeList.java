package nl.vpro.domain.media.bind;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.meeuw.i18n.Region;
import org.meeuw.i18n.countries.Country;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * Might not be needed.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Slf4j
public class CountryCodeList {
    public static class Serializer extends AbstractList.Serializer<Country> {

        @Override
        protected void serializeValue(Country value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
            if (value == null) {
                log.warn("country code is null");
                jgen.writeNull();
            } else {
                jgen.writeObject(new CountryWrapper(value));
            }
        }
    }

    public static class Deserializer extends AbstractList.Deserializer<Region> {

        @Override
        protected Region deserialize(JsonNode node, DeserializationContext ctxt) throws IOException {
            if (node == null) {
                return null;
            }
            CountryWrapper wrapper = Jackson2Mapper.getInstance().readerFor(CountryWrapper.class).readValue(node);
            return wrapper == null ? null : wrapper.getCode();
        }
    }
}
