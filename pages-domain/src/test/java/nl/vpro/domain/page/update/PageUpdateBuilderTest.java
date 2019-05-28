/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javax.xml.bind.JAXB;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.page.Image;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
public class PageUpdateBuilderTest {

    @Test
    public void testPage() throws Exception {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").build();
        PageUpdate result = JAXBTestUtil.roundTripAndSimilar(page, "<pageUpdate:page type=\"ARTICLE\" url=\"http://www.vpro.nl\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:pageUpdate=\"urn:vpro:pages:update:2013\"/>");
        assertThat(result.getType()).isEqualTo(PageType.ARTICLE);
    }

    @Test
    public void testBroadcasters() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").broadcasters("VPRO", "VARA").build();
        PageUpdate result = JAXBTestUtil.roundTripContains(page,
            "<pageUpdate:broadcaster xmlns:pageUpdate='urn:vpro:pages:update:2013'>VPRO</pageUpdate:broadcaster>",
            "<pageUpdate:broadcaster xmlns:pageUpdate='urn:vpro:pages:update:2013'>VARA</pageUpdate:broadcaster>"
        );
        assertThat(result.getBroadcasters()).containsExactly("VPRO", "VARA");
    }

    @Test
    public void testPortal() throws Exception {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").portal(new PortalUpdate("VproNL", "http://www.vpro.nl")).build();
        PageUpdate result = JAXBTestUtil.roundTripAndSimilar(page,
            "<pageUpdate:page type=\"ARTICLE\" url=\"http://www.vpro.nl\" xmlns:pageUpdate=\"urn:vpro:pages:update:2013\">\n" +
            "    <pageUpdate:portal id=\"VproNL\" url=\"http://www.vpro.nl\"/>\n" +
            "</pageUpdate:page>\n");
        assertThat(result.getPortal()).isEqualTo(new PortalUpdate("VproNL", "http://www.vpro.nl"));
    }

    @Test
    public void testTitle() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").title("Page title").build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:title>Page title</pageUpdate:title>");
        assertThat(result.getTitle()).isEqualTo("Page title");
    }

    @Test
    public void testSubtitle() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").subtitle("Page subtitle").build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:subtitle>Page subtitle</pageUpdate:subtitle>");
        assertThat(result.getSubtitle()).isEqualTo("Page subtitle");
    }

    @Test
    public void testKeywords() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").keywords("key", "word").build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:keyword>key</pageUpdate:keyword>\n" +
            "    <pageUpdate:keyword>word</pageUpdate:keyword>");
        assertThat(result.getKeywords()).containsExactly("key", "word");
    }

    @Test
    public void testSummary() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").summary("summary").build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:summary>summary</pageUpdate:summary>");
        assertThat(result.getSummary()).isEqualTo("summary");
    }

    @Test
    public void testParagraphs() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").paragraphs(new ParagraphUpdate(null, "body1", null), new ParagraphUpdate(null, "body2", null)).build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:paragraphs>\n" +
            "        <pageUpdate:paragraph>\n" +
            "            <pageUpdate:body>body1</pageUpdate:body>\n" +
            "        </pageUpdate:paragraph>\n" +
            "        <pageUpdate:paragraph>\n" +
            "            <pageUpdate:body>body2</pageUpdate:body>\n" +
            "        </pageUpdate:paragraph>\n" +
            "    </pageUpdate:paragraphs>");
        assertThat(result.getParagraphs()).containsExactly(new ParagraphUpdate(null, "body1", null), new ParagraphUpdate(null, "body2", null));
    }

    @Test
    public void testTags() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").tags("tag1", "tag2").build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:tag>tag1</pageUpdate:tag>\n" +
            "    <pageUpdate:tag>tag2</pageUpdate:tag>");
        assertThat(result.getTags()).containsExactly("tag1", "tag2");
    }

    @Test
    public void testLinks() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").links(new LinkUpdate("http://www.vpro.nl", "Link text")).build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:link pageRef=\"http://www.vpro.nl\">\n" +
            "        <pageUpdate:text>Link text</pageUpdate:text>\n" +
            "    </pageUpdate:link>");
        assertThat(result.getLinks()).containsExactly(new LinkUpdate("http://www.vpro.nl", "Link text"));
    }

    @Test
    public void testEmbeds() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").embeds(new EmbedUpdate("MID_1234", "Title", "Description")).build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:embeds>\n" +
            "        <pageUpdate:embed midRef=\"MID_1234\">\n" +
            "            <pageUpdate:title>Title</pageUpdate:title>\n" +
            "            <pageUpdate:description>Description</pageUpdate:description>\n" +
            "        </pageUpdate:embed>\n" +
            "    </pageUpdate:embeds>");
        assertThat(result.getEmbeds()).containsExactly(new EmbedUpdate("MID_1234", "Title", "Description"));
    }

    @Test
    public void testImages() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").images(new ImageUpdate(new Image("http://somewhere"))).build();
        PageUpdate result = JAXBTestUtil.roundTrip(page, "<pageUpdate:image>\n" +
            "        <pageUpdate:imageLocation>\n" +
            "            <pageUpdate:url>http://somewhere</pageUpdate:url>\n" +
            "        </pageUpdate:imageLocation>\n" +
            "    </pageUpdate:image>");
        assertThat(result.getImages()).containsExactly(new ImageUpdate(new Image("http://somewhere")));
    }


    @Test
    public void testGenres() throws Exception {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl").genres("3.0.1.1.11").build();
        PageUpdate result = JAXBTestUtil.roundTripAndSimilar(page,
            "<pageUpdate:page type=\"ARTICLE\" url=\"http://www.vpro.nl\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:pageUpdate=\"urn:vpro:pages:update:2013\">\n" +
                "    <pageUpdate:genre>3.0.1.1.11</pageUpdate:genre>\n" +
                "</pageUpdate:page>");
        assertThat(result.getGenres()).containsExactly("3.0.1.1.11");
    }

    private static final Instant TEST_INSTANT = ZonedDateTime.of(LocalDate.of(2016, 4, 18), LocalTime.NOON, Schedule.ZONE_ID).toInstant();


    @Test
    public void testDates() throws Exception {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www/vpro.nl")
            .publishStart(TEST_INSTANT)
            .creationDate(TEST_INSTANT.minus(1, ChronoUnit.DAYS))
            .lastPublished(TEST_INSTANT.plus(1, ChronoUnit.DAYS))
            .build();
        String test = "<pageUpdate:page type=\"ARTICLE\" url=\"http://www/vpro.nl\" publishStart=\"2016-04-18T12:00:00+02:00\" lastPublished=\"2016-04-19T12:00:00+02:00\" creationDate=\"2016-04-17T12:00:00+02:00\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:page=\"urn:vpro:pages:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:pageUpdate=\"urn:vpro:pages:update:2013\"/>\n";
        PageUpdate result = JAXBTestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getPublishStart()).isEqualTo(TEST_INSTANT);
        assertThat(result.getCreationDate()).isEqualTo(TEST_INSTANT.minus(1, ChronoUnit.DAYS));
        assertThat(result.getLastPublished()).isEqualTo(TEST_INSTANT.plus(1, ChronoUnit.DAYS));
    }

    @Test
    public void testDatesJson() throws Exception {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www/vpro.nl")
            .publishStart(TEST_INSTANT)
            .creationDate(TEST_INSTANT.minus(1, ChronoUnit.DAYS))
            .lastPublished(TEST_INSTANT.plus(1, ChronoUnit.DAYS))
            .build();
        String test = "{\n" +
            "  \"type\" : \"ARTICLE\",\n" +
            "  \"url\" : \"http://www/vpro.nl\",\n" +
            "  \"publishStart\" : 1460973600000,\n" +
            "  \"lastPublished\" : 1461060000000,\n" +
            "  \"creationDate\" : 1460887200000\n" +
            "}";
        PageUpdate result = Jackson2TestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getPublishStart()).isEqualTo(TEST_INSTANT);
        assertThat(result.getCreationDate()).isEqualTo(TEST_INSTANT.minus(1, ChronoUnit.DAYS));
        assertThat(result.getLastPublished()).isEqualTo(TEST_INSTANT.plus(1, ChronoUnit.DAYS));


    }



    @Test
    @Ignore("Used for building test data")
    public void testExample() {
        PageUpdate page = PageUpdateBuilder.page(PageType.ARTICLE, "http://www.vpro.nl/article").example().build();
        Writer writer = new StringWriter();
        JAXB.marshal(page, writer);
        System.out.println(writer.toString());
    }
}
