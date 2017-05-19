package nl.vpro.domain.subtitles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class DefaultOffsetGuesserTest {

    DefaultOffsetGuesser guesser = new DefaultOffsetGuesser(LocalDateTime.of(2017, 5, 19, 17, 0).atZone(ZoneId.of("Europe/Amsterdam")).toInstant());
    @Test
    public void live() throws Exception {

        TimeLine line = TimeLine.builder().start(Duration.ofHours(10)).end(Duration.ofHours(10).plusMinutes(4)).build();
        assertThat(guesser.apply(line)).isEqualTo(Duration.parse("PT9H59M57S"));
    }

    @Test
    public void liveByFirstLongCue() throws Exception {
        TimeLine line = TimeLine.builder().start(Duration.ofMinutes(2)).end(Duration.ofHours(10)).build();
        assertThat(guesser.apply(line)).isEqualTo(Duration.parse("PT9H59M57S"));
    }

    @Test
    public void recorded() throws Exception {
        TimeLine line = TimeLine.builder().start(Duration.ofMinutes(2)).end(Duration.ofMinutes(2).plusSeconds(1)).build();
        assertThat(guesser.apply(line)).isEqualTo(Duration.ofMinutes(2));
    }

}
