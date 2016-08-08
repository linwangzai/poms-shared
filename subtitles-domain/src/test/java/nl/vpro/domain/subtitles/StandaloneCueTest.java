package nl.vpro.domain.subtitles;

import java.time.Duration;
import java.util.Locale;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class StandaloneCueTest {

    @Test
    public void json() throws Exception {
        StandaloneCue cue = new StandaloneCue(CueBuilder.forMid("MID_123").content("bla bla").sequence(0).build(), Locale.US, SubtitlesType.TRANSLATION, Duration.ZERO);

        Jackson2TestUtil.roundTripAndSimilar(cue, "{\n" +
            "  \"parent\" : \"MID_123\",\n" +
            "  \"sequence\" : 0,\n" +
            "  \"type\" : \"TRANSLATION\",\n" +
            "  \"offset\" : 0,\n" +
            "  \"content\" : \"bla bla\",\n" +
            "  \"lang\" : \"en-US\"\n" +
            "}");


    }

}
