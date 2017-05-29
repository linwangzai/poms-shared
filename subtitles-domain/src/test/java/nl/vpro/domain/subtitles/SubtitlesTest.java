/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.subtitles;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.i18n.Locales.NETHERLANDISH;
import static org.assertj.core.api.Assertions.assertThat;

public class SubtitlesTest {



    @Test
    public void testMarshalToXml() throws IOException, SAXException {
        Subtitles subtitles = Subtitles.webvtt("VPRO_1234", Duration.ofMillis(2 * 60 * 1000), NETHERLANDISH,  "WEBVTT\n\n1\n00:00:00.000 --> 00:01:04.000\nbla\n\n");
        subtitles.setCreationDate(Instant.ofEpochMilli(0));
        subtitles.setLastModified(Instant.ofEpochMilli(0));


        JAXBTestUtil.roundTripAndSimilar(subtitles,
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<subtitles:subtitles mid=\"VPRO_1234\" offset=\"P0DT0H2M0.000S\" creationDate=\"1970-01-01T01:00:00+01:00\" lastModified=\"1970-01-01T01:00:00+01:00\" type=\"CAPTION\" xml:lang=\"nl-NL\" cueCount=\"1\" xmlns:subtitles=\"urn:vpro:media:subtitles:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                    "    <subtitles:content format=\"WEBVTT\" charset=\"UTF-8\">V0VCVlRUCgoxCjAwOjAwOjAwLjAwMCAtLT4gMDA6MDE6MDQuMDAwCmJsYQoK</subtitles:content>\n" +
                    "</subtitles:subtitles>");
    }

    @Test
    public void testUnmarshallFromXml() throws UnsupportedEncodingException {
        String xml =
            "<subtitles mid=\"VPRO_1234\" offset=\"P0DT0H2M0.000S\" creationDate=\"1970-01-01T01:00:00+01:00\" lastModified=\"1970-01-01T01:00:00+01:00\" type=\"CAPTION\" xml:lang=\"nl-NL\" xmlns=\"urn:vpro:media:subtitles:2009\">\n" +
                "    <content format=\"WEBVTT\">" + Base64.getEncoder().encodeToString("Ondertiteling tekst".getBytes()) + "</content>\n" +
                "</subtitles>";

        StringReader reader = new StringReader(xml);

        Subtitles subtitles = JAXB.unmarshal(reader, Subtitles.class);

        assertThat(subtitles.getMid()).isEqualTo("VPRO_1234");
        assertThat(subtitles.getOffset()).isEqualTo(Duration.ofMillis(120000));
        assertThat(new String(subtitles.getContent().getValue(), "UTF-8")).isEqualTo("Ondertiteling tekst");
    }

    @Test
    public void json() throws Exception {
        Subtitles subtitles = Subtitles.webvtt("VPRO_1234",
            Duration.ofMillis(2 * 60 * 1000), NETHERLANDISH,
            "WEBVTT\n" +
                "\n" +
                "1\n" +
                "00:00:02.200 --> 00:00:04.150\n" +
                "888\n" +
                "\n");
        subtitles.setCreationDate(Instant.ofEpochMilli(0));
        subtitles.setLastModified(Instant.ofEpochMilli(0));
        assertThat(subtitles.getCueCount()).isEqualTo(1);

        Jackson2TestUtil.roundTripAndSimilar(subtitles, "{\n" +
            "  \"mid\" : \"VPRO_1234\",\n" +
            "  \"offset\" : 120000,\n" +
            "  \"content\" : {\n" +
            "    \"format\" : \"WEBVTT\",\n" +
            "    \"value\" : \"V0VCVlRUCgoxCjAwOjAwOjAyLjIwMCAtLT4gMDA6MDA6MDQuMTUwCjg4OAoK\",\n" +
            "    \"charset\" : \"UTF-8\"\n" +
            "  },\n" +
            "  \"creationDate\" : \"1970-01-01T01:00:00+01:00\",\n" +
            "  \"lastModified\" : \"1970-01-01T01:00:00+01:00\",\n" +
            "  \"type\" : \"CAPTION\",\n" +
            "  \"lang\" : \"nl-NL\",\n" +
            "  \"cueCount\" : 1\n" +
            "}");
    }

    @Test
    public void from() throws Exception {
        Subtitles subtitles = Subtitles.from(Arrays.asList(
                StandaloneCue.tt888(new Cue(
                "mid", 1, Duration.ZERO, Duration.ofSeconds(64), "bla"))).iterator());
        Jackson2TestUtil.roundTripAndSimilar(subtitles, "{" +
            "  \"mid\" : \"mid\",\n" +
            "  \"content\" : {\n" +
            "    \"format\" : \"WEBVTT\",\n" +
            "    \"value\" : \"V0VCVlRUCgoxCjAwOjAwOjAwLjAwMCAtLT4gMDA6MDE6MDQuMDAwCmJsYQoK\",\n" +
            "    \"charset\" : \"UTF-8\"\n" +
            "  },\n" +
            "  \"type\" : \"CAPTION\",\n" +
            "  \"lang\" : \"nl\",\n" +
            "  \"cueCount\" : 1\n" +
            "}");

    }
}
