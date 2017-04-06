/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import nl.vpro.domain.api.media.ScheduleResult;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rico
 */
public class ScheduleEventTest {


    @Test
    //@Ignore("Fails for https://java.net/jira/browse/JAXB-1069")
    public void testListMedia() throws Exception {
        nl.vpro.domain.media.ScheduleEvent mediaEvent;
        ApiScheduleEvent apiEvent;

        mediaEvent = new nl.vpro.domain.media.ScheduleEvent(Channel.NED3, new Date(0), new Date(100));
        Program program = MediaTestDataBuilder.program().creationDate(new Date(100)).mid("VPROWON_12345").withSubtitles().build();

        apiEvent = new ApiScheduleEvent(mediaEvent, program);
        List<ApiScheduleEvent> events = new ArrayList<>();
        events.add(apiEvent);
        ScheduleResult result = new ScheduleResult(new Result<>(events, 0L, 10, 1L));


        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:scheduleResult total=\"1\" offset=\"0\" max=\"10\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:items>\n" +
            "        <api:item xsi:type=\"api:scheduleEventApiType\" channel=\"NED3\" midRef=\"VPROWON_12345\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "            <media:start>1970-01-01T01:00:00+01:00</media:start>\n" +
            "            <media:duration>P0DT0H0M0.100S</media:duration>\n" +
            "            <media:poProgID>VPROWON_12345</media:poProgID>\n" +
            "            <media:program embeddable=\"true\" hasSubtitles=\"true\" mid=\"VPROWON_12345\" sortDate=\"1970-01-01T01:00:00.100+01:00\" creationDate=\"1970-01-01T01:00:00.100+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "                <media:credits/>\n" +
            "                <media:locations/>\n" +
            "                <media:scheduleEvents/>\n" +
            "                <media:images/>\n" +
            "                <media:availableSubtitles>\n" + 
            "                    <media:availableSubtitle language=\"nl\" type=\"CAPTION\"/>\n" + 
            "                </media:availableSubtitles>" +
            "                <media:segments/>\n" +
            "            </media:program>\n" +
            "        </api:item>\n" +
            "    </api:items>\n" +
            "</api:scheduleResult>";
        ScheduleResult roundtripped = JAXBTestUtil.roundTripAndSimilar(result, expected);
        assertThat(roundtripped.getItems().get(0).getChannel()).isEqualTo(Channel.NED3);
    }

    @Test
    //@Ignore("Fails for https://java.net/jira/browse/JAXB-1069")
    public void testGetMedia() throws Exception {
        nl.vpro.domain.media.ScheduleEvent mediaEvent;
        ApiScheduleEvent apiEvent;

        mediaEvent = new nl.vpro.domain.media.ScheduleEvent(Channel.NED3, new Date(0), new Date(100));
        Program program = MediaTestDataBuilder.program().creationDate(new Date(100)).mid("VPROWON_12346").build();

        apiEvent = new ApiScheduleEvent(mediaEvent, program);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:scheduleItem channel=\"NED3\" midRef=\"VPROWON_12346\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <media:guideDay>1969-12-31+01:00</media:guideDay>\n" +
            "    <media:start>1970-01-01T01:00:00+01:00</media:start>\n" +
            "    <media:duration>P0DT0H0M0.100S</media:duration>\n" +
            "    <media:poProgID>VPROWON_12346</media:poProgID>\n" +
            "    <media:program embeddable=\"true\" hasSubtitles=\"false\" mid=\"VPROWON_12346\" sortDate=\"1970-01-01T01:00:00.100+01:00\" creationDate=\"1970-01-01T01:00:00.100+01:00\" workflow=\"FOR PUBLICATION\">\n" +
            "        <media:credits/>\n" +
            "        <media:locations/>\n" +
            "        <media:scheduleEvents/>\n" +
            "        <media:images/>\n" +
            "        <media:availableSubtitles/>\n" +
            "        <media:segments/>\n" +
            "    </media:program>\n" +
            "</api:scheduleItem>";
        ApiScheduleEvent roundtripped = JAXBTestUtil.roundTripAndSimilar(apiEvent, expected);
        assertThat(roundtripped.getChannel()).isEqualTo(Channel.NED3);
    }
}
