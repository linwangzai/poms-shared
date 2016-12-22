package nl.vpro.domain.api.jackson;

import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.TextMatcher;
import nl.vpro.jackson2.Jackson2Mapper;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TextMatcherJsonTest {

    @Test
    public void testGetValueJson() throws Exception {
        TextMatcher in = new TextMatcher("title");
        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, in);

        assertThat(writer.toString()).isEqualTo("\"title\"");
        TextMatcher out = Jackson2Mapper.INSTANCE.readValue(new StringReader(writer.toString()), TextMatcher.class);
        assertThat(out.getValue()).isEqualTo("title");
    }

    @Test
    public void testGetValueFromJson() throws Exception {
        TextMatcher matcher = Jackson2Mapper.INSTANCE.readValue("\"title\"", TextMatcher.class);

        assertThat(matcher).isEqualTo(new TextMatcher("title"));
    }

    @Test
    public void testGetValueJsonInverse() throws Exception {
        TextMatcher in = new TextMatcher("title", Match.NOT);
        StringWriter writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, in);

        assertThat(writer.toString()).isEqualTo("{\"value\":\"title\",\"match\":\"not\"}");
        TextMatcher out = Jackson2Mapper.INSTANCE.readValue(new StringReader(writer.toString()), TextMatcher.class);
        assertThat(out.getValue()).isEqualTo("title");
        assertThat(out.getMatch()).isEqualTo(Match.NOT);
    }

    @Test
    public void testGetValueFromJsonInverse() throws Exception {
        TextMatcher matcher = Jackson2Mapper.INSTANCE.readValue("{\"value\":\"title\",\"match\":\"not\"}", TextMatcher.class);

        assertThat(matcher).isEqualTo(new TextMatcher("title", Match.NOT));
    }
}
