package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import nl.vpro.domain.api.DateRangeMatcher;
import nl.vpro.domain.api.DateRangeMatcherList;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.MatcherList;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class DateRangeMatcherListJson {

    public static class Serializer extends JsonSerializer<DateRangeMatcherList> {
        @Override
        public void serialize(DateRangeMatcherList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value.getMatch() != MatcherList.DEFAULT_MATCH) {
                jgen.writeStartObject();
                jgen.writeArrayFieldStart("value");
                for (DateRangeMatcher matcher : value.asList()) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();
                if (value.getMatch() != null) {
                    jgen.writeStringField("match", value.getMatch().name());
                }
                jgen.writeEndObject();
            } else {
                jgen.writeStartArray();
                for (DateRangeMatcher matcher : value.asList()) {
                    jgen.writeObject(matcher);
                }
                jgen.writeEndArray();

            }
        }
    }

    public static class Deserializer extends JsonDeserializer<DateRangeMatcherList> {

        @Override
        public DateRangeMatcherList deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if (jp.getParsingContext().inObject()) {
                JsonNode jsonNode = jp.readValueAsTree();
                JsonNode m = jsonNode.get("match");
                Match match = m == null ? Match.MUST : Match.valueOf(m.asText().toUpperCase());

                List<DateRangeMatcher> list = new ArrayList<>();
                if (jsonNode.has("value")) {
                    for (JsonNode child : jsonNode.get("value")) {
                        DateRangeMatcher dm = Jackson2Mapper.getInstance().readValue(child.traverse(), DateRangeMatcher.class);
                        list.add(dm);
                    }
                } else {
                    list.add(Jackson2Mapper.getInstance().readValue(jsonNode.traverse(), DateRangeMatcher.class));
                }
                return new DateRangeMatcherList(list, match);
            } else if (jp.getParsingContext().inArray()) {
                List<DateRangeMatcher> list = new ArrayList<>();
                jp.clearCurrentToken();
                Iterator<DateRangeMatcher> i = jp.readValuesAs(DateRangeMatcher.class);
                while (i.hasNext()) {
                    list.add(i.next());
                }
                return new DateRangeMatcherList(list.toArray(new DateRangeMatcher[list.size()]));
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
