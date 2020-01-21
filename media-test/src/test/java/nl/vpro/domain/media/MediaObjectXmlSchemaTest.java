/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import javax.xml.validation.*;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.IntegerVersion;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.domain.media.MediaTestDataBuilder.program;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

/**
 * This class verifies JAXB XML output format and wether this format complies to the vproMedia.xsd schema definitions.
 * It's located here so it can use the test data builder for more concise code.
 */
@SuppressWarnings("UnnecessaryLocalVariable")
@Slf4j
public class MediaObjectXmlSchemaTest {

    private static JAXBContext jaxbContext;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tashkent"));
        try {
            jaxbContext = JAXBContext.newInstance("nl.vpro.domain.media");
        } catch(JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Marshaller marshaller;

    static {
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch(JAXBException e) {
            log.error("Unable to create marshaller", e);
        }
    }

    public static Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema;
        try {
            schema = factory.newSchema(new Source[]{
                new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/w3/xml.xsd")),
                new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/vproShared.xsd")),
                new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/vproMedia.xsd"))}
            );
            schemaValidator = schema.newValidator();
        } catch(SAXException e) {
            log.error("Unable to create schemaValidator", e);
        }

        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @BeforeEach
    public void init() {
        Locale.setDefault(Locales.DUTCH);
    }

    @Test
    public void testMid() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program mid=\"MID_000001\" embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().mid("MID_000001").build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testAvailableSubtitles() {
        String expected = "<program embeddable=\"true\" hasSubtitles=\"true\" mid=\"MID_000001\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <availableSubtitles language=\"nl\" type=\"CAPTION\"/>\n" +
            "    <availableSubtitles language=\"nl\" type=\"TRANSLATION\"/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments/>\n" +
            "</program>";

        Program program = program().lean().mid("MID_000001").build();
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.CAPTION));
    	program.getAvailableSubtitles().add(new AvailableSubtitles(Locales.DUTCH,
            SubtitlesType.TRANSLATION));

        Program rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.getAvailableSubtitles()).hasSize(2);
    }

    @Test
    public void testMidSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withMid().build()));
    }

    @Test
    public void testHasSubtitles() {
        String expected = "<program embeddable=\"true\" hasSubtitles=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <availableSubtitles language=\"nl\" type=\"CAPTION\"/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments/>\n" +
            "</program>\n";

        Program program = program().lean().withSubtitles().build();
        Program rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.hasSubtitles()).isTrue();
    }

    @Test
    public void testHasSubtitlesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withSubtitles().build()));
    }

    @Test
    public void testDatesCreatedAndModified() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" lastModified=\"1970-01-01T03:00:00+01:00\" creationDate=\"1970-01-01T01:00:00+01:00\" sortDate=\"1970-01-01T01:00:00+01:00\"  xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().creationInstant(Instant.EPOCH).lastModified(Instant.ofEpochMilli(2 * 60 * 60 * 1000)).build();
        String actual = toXml(program);
        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testCreatedAndModifiedBy() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><images /><scheduleEvents/><segments/></program>";

        Program program = program().lean().withCreatedBy().withLastModifiedBy().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testCreatedAndModifiedBySchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCreatedBy().withLastModifiedBy().build()));
    }

    @Test
    public void testPublishStartStop() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" publishStop=\"1970-01-01T03:00:00+01:00\" publishStart=\"1970-01-01T01:00:00+01:00\" sortDate=\"1970-01-01T01:00:00+01:00\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().publishStart(Instant.EPOCH).publishStop(Instant.ofEpochMilli(2 * 60 * 60 * 1000)).build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testPublishStartStopSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPublishStart().withPublishStop().build()));
    }

    @Test
    public void testCrids() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><crid>crid://bds.tv/9876</crid><crid>crid://tmp.fragment.mmbase.vpro.nl/1234</crid><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withCrids().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testCridsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCrids().build()));
    }

    @Test
    public void testBroadcasters() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><broadcaster id=\"BNN\">BNN</broadcaster><broadcaster id=\"AVRO\">AVRO</broadcaster><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withBroadcasters().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testExclusives() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><exclusive portalId=\"STERREN24\"/><exclusive portalId=\"3VOOR12_GRONINGEN\" stop=\"1970-01-01T01:01:40+01:00\" start=\"1970-01-01T01:00:00+01:00\"/><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withPortalRestrictions().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testExclusivesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPortalRestrictions().build()));
    }

    @Test
    public void testRegions() {

        JAXBTestUtil.roundTripAndSimilar(program().lean().withGeoRestrictions().build(),
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <region regionId=\"NL\" platform=\"INTERNETVOD\"/>\n" +
                "    <region regionId=\"BENELUX\" platform=\"INTERNETVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
                "    <region regionId=\"NL\" platform=\"TVVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <images/>\n" +
                "    <scheduleEvents/>\n" +
                "    <segments/>\n" +
                "</program>\n");
    }

    @Test
    public void testRegionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withGeoRestrictions().build()));
    }

    @Test
    public void testDuration() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><duration>P0DT2H0M0.000S</duration><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withDuration().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testDurationSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withDuration().build()));
    }

    @Test
    public void testPredictions() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><credits/><prediction state=\"REVOKED\">INTERNETVOD</prediction><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().build();

        Prediction prediction = new Prediction(Platform.INTERNETVOD);
        prediction.setState(Prediction.State.REVOKED);
        prediction.setIssueDate(Instant.EPOCH);

        Prediction unavailable = new Prediction(Platform.TVVOD);
        unavailable.setState(Prediction.State.REVOKED);
        unavailable.setIssueDate(Instant.EPOCH);
        unavailable.setPlannedAvailability(false);

        program.getPredictions().add(prediction);
        program.getPredictions().add(unavailable);

        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testPredictionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPredictions().build()));
    }

    @Test
    public void testTitles() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><title type=\"MAIN\" owner=\"BROADCASTER\">Main title</title><title type=\"MAIN\" owner=\"MIS\">Main title MIS</title><title type=\"SHORT\" owner=\"BROADCASTER\">Short title</title><title type=\"SUB\" owner=\"MIS\">Episode title MIS</title><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withTitles().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testDescriptions() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><description type=\"MAIN\" owner=\"BROADCASTER\">Main description</description><description type=\"MAIN\" owner=\"MIS\">Main description MIS</description><description type=\"SHORT\" owner=\"BROADCASTER\">Short description</description><description type=\"EPISODE\" owner=\"MIS\">Episode description MIS</description><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withDescriptions().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testDescriptionsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withDescriptions().build()));
    }

    @Test
    public void testGenres() {
        Program program = program().withGenres().withFixedDates().build();

        Program result = JAXBTestUtil.roundTripAndSimilar(program, "<program embeddable=\"true\" sortDate=\"2015-03-06T00:00:00+01:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2015-03-06T00:00:00+01:00\" lastModified=\"2015-03-06T01:00:00+01:00\" publishDate=\"2015-03-06T02:00:00+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <genre id=\"3.0.1.7.21\">\n" +
            "        <term>Informatief</term>\n" +
            "        <term>Nieuws/actualiteiten</term>\n" +
            "    </genre>\n" +
            "    <genre id=\"3.0.1.8.25\">\n" +
            "        <term>Documentaire</term>\n" +
            "        <term>Natuur</term>\n" +
            "    </genre>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +

            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments/>\n" +
            "</program>");

        assertThat(result.getGenres()).hasSize(2);
    }

    @Test
    public void testGenresSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withGenres().build()));
    }

    @Test
    public void testAgeRating() {
        Program program = program().withAgeRating().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<ageRating xmlns='urn:vpro:media:2009'>12</ageRating>");

        assertThat(result.getAgeRating()).isNotNull();
    }

    @Test
    public void testAgeRatingSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withAgeRating().build()));
    }

    @Test
    public void testContentRating() {
        Program program = program().withContentRating().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<contentRating>ANGST</contentRating>",
            "<contentRating>DRUGS_EN_ALCOHOL</contentRating>");

        assertThat(result.getContentRatings()).hasSize(2);
    }

    @Test
    public void testContentRatingSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withContentRating().build()));
    }

    @Test
    public void testTags() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\"  xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><tag>tag1</tag><tag>tag2</tag><tag>tag3</tag><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withTags().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testTagsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withTags().build()));
    }

    @Test
    public void testPortals() throws Exception {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns=\"urn:vpro:media:2009\"><portal id=\"3VOOR12_GRONINGEN\">3voor12 Groningen</portal><portal id=\"STERREN24\">Sterren24</portal><credits/><locations/><images/><scheduleEvents/><segments/></program>";

        Program program = program().lean().withPortals().build();
        String actual = toXml(program);

        Diff diff = DiffBuilder.compare(expected).withTest(actual).build();
        assertFalse(diff.toString() + " " + actual, diff.hasDifferences());
    }

    @Test
    public void testPortalsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withPortals().build()));
    }

    @Test
    public void testMemberOfAndDescendantOfGraph() {
        AtomicLong id = new AtomicLong(100L);
        String expected =
            "<program xmlns=\"urn:vpro:media:2009\" embeddable=\"true\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
                "    <credits/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:group:100\" midRef=\"AVRO_5555555\" type=\"SERIES\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:group:200\" midRef=\"AVRO_7777777\" type=\"SEASON\"/>\n" +
                "    <descendantOf urnRef=\"urn:vpro:media:segment:301\" midRef=\"VPROWON_104\" type=\"SEGMENT\"/>\n" +
                "    <memberOf added=\"1970-01-01T01:00:00+01:00\" highlighted=\"false\" midRef=\"AVRO_7777777\" index=\"1\" type=\"SEASON\" urnRef=\"urn:vpro:media:group:200\">\n" +
                "        <memberOf highlighted=\"false\" midRef=\"AVRO_5555555\" index=\"1\" type=\"SERIES\" urnRef=\"urn:vpro:media:group:100\"/>\n" +
                "    </memberOf>\n" +
                "    <memberOf highlighted=\"false\" midRef=\"VPROWON_104\" index=\"2\" type=\"SEGMENT\" urnRef=\"urn:vpro:media:segment:301\">\n" +
                "        <segmentOf midRef=\"VPROWON_103\" type=\"CLIP\">\n" +
                "            <memberOf highlighted=\"false\" midRef=\"AVRO_5555555\" index=\"10\" type=\"SERIES\" urnRef=\"urn:vpro:media:group:100\"/>\n" +
                "        </segmentOf>\n" +
                "    </memberOf>\n" +
                "    <memberOf highlighted=\"false\" midRef=\"VPROWON_104\" index=\"3\" type=\"SEGMENT\" urnRef=\"urn:vpro:media:segment:301\">\n" +
                "        <segmentOf midRef=\"VPROWON_103\" type=\"CLIP\">\n" +
                "            <memberOf highlighted=\"false\" midRef=\"AVRO_5555555\" index=\"10\" type=\"SERIES\" urnRef=\"urn:vpro:media:group:100\"/>\n" +
                "        </segmentOf>\n" +
                "    </memberOf>\n" +
                "    <locations/>\n" +
                "    <images/>\n" +
                "    <scheduleEvents/>\n" +
                "    <segments/>\n" +
                "</program>";

        Program program = program().lean().withMemberOf(id).build();
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getMemberOf().first().getGroup().setMid(null);
        program.getMemberOf().first().getGroup().setMid("AVRO_7777777");
        program.getMemberOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getMemberOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");
        program.getMemberOf().first().setAdded(Instant.EPOCH);

        assertThatXml(program).isSimilarTo(expected);

    }

    @Test
    public void testEpisodeOfAndDescendantOfGraph() {

        AtomicLong id = new AtomicLong(100);
        String expected = "<program xmlns=\"urn:vpro:media:2009\" type=\"BROADCAST\" embeddable=\"true\" urn=\"urn:vpro:media:program:100\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:group:101\" midRef=\"AVRO_5555555\" type=\"SERIES\"/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:group:102\" midRef=\"AVRO_7777777\" type=\"SEASON\"/>\n" +
            "    <descendantOf midRef=\"VPROWON_106\" type=\"SEGMENT\"/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <episodeOf added=\"1970-01-01T01:00:00+01:00\" highlighted=\"false\" midRef=\"AVRO_7777777\" index=\"1\" type=\"SEASON\" urnRef=\"urn:vpro:media:group:102\">\n" +
            "        <memberOf highlighted=\"false\" midRef=\"AVRO_5555555\" index=\"1\" type=\"SERIES\" urnRef=\"urn:vpro:media:group:101\"/>\n" +
            "        <memberOf highlighted=\"false\" midRef=\"VPROWON_106\" index=\"2\" type=\"SEGMENT\">\n" +
            "            <segmentOf midRef=\"VPROWON_105\" type=\"CLIP\">\n" +
            "                <memberOf highlighted=\"false\" midRef=\"AVRO_5555555\" index=\"10\" type=\"SERIES\" urnRef=\"urn:vpro:media:group:101\"/>\n" +
            "            </segmentOf>\n" +
            "        </memberOf>\n" +
            "    </episodeOf>\n" +
            "    <segments/>\n" +
            "</program>";

        Program program = program().id(id.getAndIncrement()).lean().type(ProgramType.BROADCAST).withEpisodeOf(id.getAndIncrement(), id.getAndIncrement(), id).build();program.getEpisodeOf().first().setAdded(Instant.EPOCH);
        /* Set MID to null first, then set it to the required MID; otherwise an IllegalArgumentException will be thrown setting the MID to another value */
        program.getEpisodeOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().setMid("AVRO_7777777");
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid(null);
        program.getEpisodeOf().first().getGroup().getMemberOf().first().getGroup().setMid("AVRO_5555555");

        assertThatXml(program).isSimilarTo(expected);
    }

    @Test
    public void testRelations() {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00+01:00\" creationDate=\"1970-01-01T01:00:00+01:00\" urn=\"urn:vpro:media:program:100\" workflow=\"PUBLISHED\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <relation broadcaster=\"AVRO\" type=\"THESAURUS\" urn=\"urn:vpro:media:relation:2\">synoniem</relation>\n" +
            "    <relation broadcaster=\"EO\" type=\"KOOR\" urn=\"urn:vpro:media:relation:4\">Ulfts Mannenkoor</relation>\n" +
            "    <relation broadcaster=\"VPRO\" type=\"ARTIST\" urn=\"urn:vpro:media:relation:3\">Marco Borsato</relation>\n" +
            "    <relation uriRef=\"http://www.bluenote.com/\" broadcaster=\"VPRO\" type=\"LABEL\" urn=\"urn:vpro:media:relation:1\">Blue Note</relation>\n" +
            " \n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments/>\n" +
            "</program>";

        Program program = program().id(100L).lean().creationDate(Instant.EPOCH).workflow(Workflow.PUBLISHED).withRelations().build();


        JAXBTestUtil.roundTripAndSimilar(program, expected);
    }


    @Test
    public void testRelationsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withRelations().build()));
    }

    @Test
    public void testScheduleEvents() throws Exception {

        Program program = program().id(100L).lean().withScheduleEvents().build();
        String actual = toXml(program);

        assertThat(actual).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00.100+01:00\"\n" +
            "    urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <guideDay>1969-12-31+01:00</guideDay>\n" +
            "            <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-03+01:00</guideDay>\n" +
            "            <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "            <duration>P0DT0H0M0.050S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"HOLL\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-08+01:00</guideDay>\n" +
            "            <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "            <duration>P0DT0H0M0.250S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"CONS\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-10+01:00</guideDay>\n" +
            "            <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +

            "    <segments/>\n" +
            "</program>");

        String withScheduleEventOnOldLocation =  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00.100+01:00\"\n" +
            "    urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +

            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <guideDay>1969-12-31+01:00</guideDay>\n" +
            "            <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-03+01:00</guideDay>\n" +
            "            <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "            <duration>P0DT0H0M0.050S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"HOLL\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-08+01:00</guideDay>\n" +
            "            <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "            <duration>P0DT0H0M0.250S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"CONS\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <repeat isRerun=\"true\"/>\n" +
            "            <guideDay>1970-01-10+01:00</guideDay>\n" +
            "            <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        Program unmarshalled = JAXB.unmarshal(new StringReader(withScheduleEventOnOldLocation), Program.class);
        assertThat(unmarshalled.getScheduleEvents()).hasSize(4);
    }

    @Test
    public void testScheduleEventsSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withScheduleEvents().build()));
    }

    @Test
    public void testScheduleEventsWithNet() {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" sortDate=\"1970-01-01T01:00:00+01:00\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"NED1\" midRef=\"VPRO_123456\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "            <guideDay>1970-01-01+01:00</guideDay>\n" +
            "            <start>1970-01-01T01:00:00+01:00</start>\n" +
            "            <duration>P0DT0H1M40.000S</duration>\n" +
            "            <poProgID>VPRO_123456</poProgID>\n" +
            "            <primaryLifestyle>Onbezorgde Trendbewusten</primaryLifestyle>\n" +
            "            <secondaryLifestyle>Zorgzame Duizendpoten</secondaryLifestyle>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <segments/>\n" +
            "</program>";

        ScheduleEvent event = new ScheduleEvent(Channel.NED1, Instant.EPOCH, java.time.Duration.ofSeconds((100)));
        event.setGuideDate(LocalDate.ofEpochDay(0));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");
        event.setPrimaryLifestyle(new Lifestyle("Onbezorgde Trendbewusten"));
        event.setSecondaryLifestyle(new SecondaryLifestyle("Zorgzame Duizendpoten"));

        Program program = program().lean().id(100L).scheduleEvents(event).build();

        JAXBTestUtil.roundTripAndSimilar(program, expected);

        JAXB.unmarshal(new StringReader(expected), Program.class);
    }

    @Test
    public void testScheduleEventsWithNetSchema() throws Exception {
        ScheduleEvent event = new ScheduleEvent(Channel.NED1, Instant.EPOCH,
            java.time.Duration.ofSeconds(100));
        event.setGuideDate(LocalDate.of(1970, 1, 1));
        event.setNet(new Net("ZAPP", "Zapp"));
        event.setPoProgID("VPRO_123456");

        Program program = program().constrained().scheduleEvents(event).build();

        schemaValidator.validate(new JAXBSource(marshaller, program));
    }

    @Test
    @Disabled("Used to generate an example XML document")
    public void generateExample() throws Exception {
        Segment segment = MediaTestDataBuilder
            .segment()
            .withPublishStart()
            .withPublishStop()
            .duration(java.time.Duration.ofSeconds(100))
            .start(java.time.Duration.ofSeconds(5000))
            .withImages()
            .withTitles()
            .withDescriptions()
            .build();

        MediaTestDataBuilder.ProgramTestDataBuilder testBuilder = MediaTestDataBuilder
            .program()
            .type(ProgramType.BROADCAST)
            .withPublishStart()
            .withPublishStop()
            .withCrids()
            .withBroadcasters()
            .withTitles()
            .withDescriptions()
            .withDuration()
            .withMemberOf()
            .withEmail()
            .withWebsites()
            .withLocations()
            .withScheduleEvents()
            .withRelations()
            .withImages()
            .withEpisodeOf()
            .segments(segment)
            .withSegments();

        ProgramUpdate example = ProgramUpdate.create(testBuilder.build());

        System.out.println(toXml(example));
    }

    @Test
    public void testSchedule() throws Exception {

        Schedule schedule = new Schedule(Channel.NED1, Instant.ofEpochMilli(0), Instant.ofEpochMilli(350 + 8 * 24 * 3600 * 1000));
        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(program);

        String actual = toXml(schedule);

        assertThat(actual).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<schedule channel=\"NED1\" start=\"1970-01-01T01:00:00+01:00\"\n" +
            "    stop=\"1970-01-11T01:00:00.800+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-03+01:00</guideDay>\n" +
            "        <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "        <duration>P0DT0H0M0.050S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"HOLL\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-08+01:00</guideDay>\n" +
            "        <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "        <duration>P0DT0H0M0.250S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"CONS\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-10+01:00</guideDay>\n" +
            "        <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "    </scheduleEvent>\n" +
            "</schedule>");


        Schedule unmarshalled = JAXB.unmarshal(new StringReader(actual), Schedule.class);
        assertThat(unmarshalled.getNet()).isNull();
    }


    @Test
    public void testScheduleWithFilter() throws Exception {

        Schedule schedule = new Schedule(Channel.NED3, Instant.ofEpochMilli(0), Instant.ofEpochMilli(350 + 8 * 24 * 3600 * 1000));
        schedule.setFiltered(true);
        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(program);

        String actual = toXml(schedule);

        assertThat(actual).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<schedule channel=\"NED3\" start=\"1970-01-01T01:00:00+01:00\"\n" +
            "    stop=\"1970-01-09T01:00:00.350+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <scheduleEvent channel=\"NED3\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <guideDay>1969-12-31+01:00</guideDay>\n" +
            "        <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "        <duration>P0DT0H0M0.200S</duration>\n" +
            "    </scheduleEvent>\n" +
            "    <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-03+01:00</guideDay>\n" +
            "        <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "        <duration>P0DT0H0M0.050S</duration>\n" +
            "    </scheduleEvent>\n" +
            "</schedule>\n" +
            "");
    }

    @Test
    public void testScheduleWithNetFilter() throws Exception {


        Schedule schedule = Schedule.builder()
            .net(new Net("ZAPP"))
            .start(Instant.EPOCH)
            .stop(Instant.EPOCH.plus(Duration.ofDays(8).plusMillis(350)))
            .filtered(true)
            .build();

        Program program = program().id(100L).lean().withScheduleEvents().build();
        schedule.addScheduleEventsFromMedia(program);

        assertThat(toXml(schedule)).isXmlEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<schedule net=\"ZAPP\" start=\"1970-01-01T01:00:00+01:00\"\n" +
            "    stop=\"1970-01-09T01:00:00.350+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <scheduleEvent channel=\"NED3\" net=\"ZAPP\" urnRef=\"urn:vpro:media:program:100\">\n" +
            "        <repeat isRerun=\"true\"/>\n" +
            "        <guideDay>1970-01-03+01:00</guideDay>\n" +
            "        <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "        <duration>P0DT0H0M0.050S</duration>\n" +
            "    </scheduleEvent>\n" +
            "</schedule>");
    }

    @Test
    public void testCountries() {
        Program program = program().withCountries().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<country code=\"GB\">Verenigd Koninkrijk</country>");

        assertThat(result.getCountries()).hasSize(2);
    }


    @Test
    public void testCountriesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withCountries().build()));
    }

    @Test
    public void testLanguages() {
        Program program = program().withLanguages().build();

        Program result = JAXBTestUtil.roundTripContains(program, "<language code=\"nl\">Nederlands</language>");

        assertThat(result.getLanguages()).hasSize(2);
    }


    @Test
    public void testLanguagesSchema() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, program().constrained().withLanguages().build()));
    }

    @Test
    public void testTwitter() throws JAXBException, IOException, SAXException {
        Program program = program().constrained().build();
        program.setTwitterRefs(Arrays.asList(new TwitterRef("@vpro"), new TwitterRef("#vpro")));
        StringWriter writer = new StringWriter();
        JAXB.marshal(program, writer);
        program = JAXB.unmarshal(new StringReader(writer.toString()), Program.class);
        assertThat(program.getTwitterRefs()).containsExactly(new TwitterRef("@vpro"), new TwitterRef("#vpro"));
        schemaValidator.validate(new JAXBSource(marshaller, program));
    }

    @Test
    public void testWithLocations() {
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <locations>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T15:45:00+01:00\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>1500</bitrate>\n" +
            "                <avFileFormat>MP4</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <offset>P0DT0H13M0.000S</offset>\n" +
            "            <duration>P0DT0H10M0.000S</duration>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T14:45:00+01:00\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>3000</bitrate>\n" +
            "                <avFileFormat>WM</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T13:45:00+01:00\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>2000</bitrate>\n" +
            "                <avFileFormat>WM</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <duration>P0DT0H30M33.000S</duration>\n" +
            "        </location>\n" +
            "        <location owner=\"NEBO\" workflow=\"FOR PUBLICATION\" creationDate=\"2016-03-04T12:45:00+01:00\">\n" +
            "            <programUrl>http://player.omroep.nl/?aflID=4393288</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>1000</bitrate>\n" +
            "                <avFileFormat>HTML</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments/>\n" +
            "</program>\n";

        Program program = program().id(100L).lean().withLocations().build();

        JAXBTestUtil.roundTripAndSimilar(program, expected);
    }

    @Test
    public void testWithLocationWithUnknownOwner() {
        String example = "<program embeddable=\"true\" hasSubtitles=\"false\" urn=\"urn:vpro:media:program:100\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <locations>\n" +
            "        <location owner=\"UNKNOWN\" creationDate=\"2016-03-04T15:45:00+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>MP4</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <offset>P0DT0H13M0.000S</offset>\n" +
            "            <duration>P0DT0H10M0.000S</duration>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "</program>";

        Program program = JAXBTestUtil.unmarshal(example, Program.class);
        assertThat(program.getLocations().first().getOwner()).isNull();
    }

    @Test
    public void testWithDescendantOf() {
        Program program = program().lean().withDescendantOf().build();
        JAXBTestUtil.roundTripAndSimilar(program, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <credits/>\n" +
            "    <descendantOf midRef=\"MID_123456\" type=\"SEASON\"/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:group:2\" type=\"SERIES\"/>\n" +
            "    <descendantOf urnRef=\"urn:vpro:media:program:1\" type=\"BROADCAST\"/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments/>\n" +
            "</program>");
    }

    @Test
    public void testWithIntentions() throws IOException, JAXBException {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-scenarios.xml"), segment, UTF_8);
        String expected = segment.toString();
        log.info(expected);

        Intentions intentions = Intentions.builder()
                .owner(OwnerType.NPO)
                .values(Arrays.asList(IntentionType.ACTIVATING, IntentionType.INFORM_INDEPTH))
                .build();
        Program program = program().lean()
                .mid("9").avType(AVType.AUDIO)
                .type(ProgramType.BROADCAST).embeddable(true)
                .build();

        program.setSortInstant(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).toInstant());

        MediaObjectOwnableLists.addOrUpdateOwnableList(program, program.getIntentions(), intentions);

        String actual = toXml(program);

        assertThat(actual).isXmlEqualTo(segment.toString());

        intentions.setParent(null);
        Intentions intentionsWithoutParent = intentions;
        Program programExpected = JAXBTestUtil.unmarshal(expected, Program.class);
        assertThat((Object) programExpected.getIntentions().iterator().next()).isEqualTo(intentionsWithoutParent);
    }

    @Test
    public void testWithGeoLocations() throws Exception {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/geolocations-scenarios.xml"), segment, UTF_8);
        String expected = segment.toString();
        log.info("Expected: " + expected);

        Program program = program().lean().withGeoLocations()
                .mid("9").avType(AVType.AUDIO)
                .type(ProgramType.BROADCAST).embeddable(true)
                .build();

        program.setSortInstant(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).toInstant());

        JAXBTestUtil.roundTripAndSimilarAndEquals(program, expected);

        String actual = toXml(program);

        assertThat(actual).isXmlEqualTo(segment.toString());

        GeoLocations geoLocations = program.getGeoLocations().first();
        geoLocations.setParent(null);
        GeoLocations geoLocationsWithoutParent = geoLocations;
        Program programExpected = JAXBTestUtil.unmarshal(expected, Program.class);
        assertThat((Object) programExpected.getGeoLocations().iterator().next()).isEqualTo(geoLocationsWithoutParent);
    }

    @Test
    public void testUnmarshalWithNullIntentions() throws IOException {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-null-scenarios.xml"), segment, UTF_8);
        String xmlInput = segment.toString();
        log.info(xmlInput);

        Program programExpected = JAXBTestUtil.unmarshal(xmlInput, Program.class);
        assertThat(programExpected.intentions).isNull();
    }

    @Test
    public void testUnmarshalWithEmptyIntentions() throws IOException {
        StringWriter segment = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/intention-empty-scenarios.xml"), segment, UTF_8);
        String xmlInput = segment.toString();
        log.info(xmlInput);

        Program programExpected = JAXBTestUtil.unmarshal(xmlInput, Program.class);
        assertThat(programExpected.getIntentions().first().getOwner()).isEqualTo(OwnerType.NPO);
    }

    @Test
    public void roundTripWithPrediction() {
        String example =
            "<program xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" type=\"BROADCAST\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPWON_1199058\" sortDate=\"2013-04-09T15:25:00+02:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2013-03-17T06:48:59.719+01:00\" lastModified=\"2018-02-07T11:58:43.578+01:00\" publishDate=\"2015-01-08T17:42:19.202+01:00\" urn=\"urn:vpro:media:program:23197206\">\n" +
                "  <broadcaster id=\"VPRO\">VPRO</broadcaster>\n" +
                "  <credits/>\n" +
                "  <prediction state=\"ANNOUNCED\" publishStop=\"2020-01-02T14:54:44+01:00\">INTERNETVOD</prediction>\n" +
                "  <prediction state=\"ANNOUNCED\" publishStop=\"2020-01-02T14:54:44+01:00\">TVVOD</prediction>\n" +
                "  <locations />\n" +
                "  <images/>\n" +
                "  <scheduleEvents/>\n" +
                "  <segments/>\n" +
                "</program>\n";
        JAXBTestUtil.roundTripAndSimilar(example, Program.class);
    }

    @Test
    public void programWithEverything() throws IOException {
        Program withEverything = MediaTestDataBuilder.program().withEverything()
            .build();
        JAXBTestUtil.roundTripAndSimilar(withEverything, getClass().getResourceAsStream("/program-with-everything.xml"));
    }

    /**
     * Tests wether 'withEveryting' is indeed valid according to manually maintained XSD
     */
    @Test
    public void testUpdateSchema() throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(
            XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema xsdSchema = factory.newSchema(getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Validator xsdValidator = xsdSchema.newValidator();

        ProgramUpdate update = ProgramUpdate.create(MediaTestDataBuilder.program()
            .withEverything(IntegerVersion.of(5, 12))
            .build());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(update, out);
        log.info(new String(out.toByteArray()));

        Source streamSource = new StreamSource(new ByteArrayInputStream(out.toByteArray()));
        xsdValidator.validate(streamSource);
    }

    /**
     * Tests wether 'withEverything' is indeed valid according to manually maintained XSD
     */
    @Test
    public void testSchema() throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema xsdSchema = factory.newSchema(getClass().getResource("/nl/vpro/domain/media/vproMedia.xsd"));
        Validator xsdValidator = xsdSchema.newValidator();

        Program program = MediaTestDataBuilder.program().withEverything().build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(program, out);
        log.info(new String(out.toByteArray()));

        Source streamSource = new StreamSource(new ByteArrayInputStream(out.toByteArray()));
        xsdValidator.validate(streamSource);
    }

    protected String toXml(Object o) throws JAXBException {
        Writer writer = new StringWriter();
        marshaller.marshal(o, writer);
        return writer.toString();
    }
}
