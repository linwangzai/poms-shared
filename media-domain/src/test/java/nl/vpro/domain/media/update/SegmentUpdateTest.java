/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.StringReader;
import java.time.Duration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class SegmentUpdateTest extends MediaUpdateTest {

    @Test
    public void testCreate() throws Exception {
        SegmentUpdate update = SegmentUpdate.create();
        update.setStart(Duration.ofMillis(100));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><segment embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><start>P0DT0H0M0.100S</start></segment>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void unmarshal() throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(SegmentUpdate.class, ProgramUpdate.class, GroupUpdate.class);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><update:segment xmlns:update=\"urn:vpro:media:update:2009\" xmlns=\"urn:vpro:media:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:media=\"urn:vpro:media:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:shared=\"urn:vpro:shared:2009\" midRef=\"VARA_101377256\" avType=\"VIDEO\" embeddable=\"true\" publishStart=\"2016-05-11T00:18:00+02:00\"><update:broadcaster>VARA</update:broadcaster><update:title type=\"MAIN\">Haalt Douwe Bob de finale van het Songfestival?</update:title><update:description type=\"MAIN\">Onze Douwe Bob nam het met zijn nummer Slow down op tegen Rusland, Malta en Armenië. Kenners voorspelden een finaleplek voor Douwe en dat heeft hij ook waar weten te maken. We belde direct na de halve finale met Cornald Maas, die in Stockholm was. Daarna gingen we nabeschouwen met Maan, Shirma Rouse en Gijs Staverman. </update:description><update:duration>P0DT0H14M40.000S</update:duration><update:credits><update:person role=\"UNDEFINED\"><update:givenName>Maan</update:givenName><update:familyName>de Steenwinkel</update:familyName></update:person><update:person role=\"UNDEFINED\"><update:givenName>Shirma</update:givenName><update:familyName>Rouse</update:familyName></update:person><update:person role=\"UNDEFINED\"><update:givenName>Gijs</update:givenName><update:familyName>Staverman</update:familyName></update:person><update:person role=\"UNDEFINED\"><update:givenName>Jeroen</update:givenName><update:familyName>Pauw</update:familyName></update:person></update:credits><update:website>http://pauw.vara.nl</update:website><update:locations/><update:scheduleEvents/><update:images><update:image type=\"STILL\" highlighted=\"false\"><update:title>Haalt Douwe Bob de finale van het Songfestival?</update:title><update:description>Onze Douwe Bob nam het met zijn nummer Slow down op tegen Rusland, Malta en Armenië. Kenners voorspelden een finaleplek voor Douwe en dat heeft hij ook waar weten te maken. We belde direct na de halve finale met Cornald Maas, die in Stockholm was. Daarna gingen we nabeschouwen met Maan, Shirma Rouse en Gijs Staverman. </update:description><update:imageLocation><update:url>http://download.omroep.nl/vara/2016/05/10/20160510-pauw-songfestival_7.jpg</update:url></update:imageLocation></update:image></update:images><update:start>P0DT0H1M15.000S</update:start></update:segment>";
        MediaUpdate segment = (MediaUpdate) jc.createUnmarshaller().unmarshal(new StringReader(xml));
        assertThat(segment).isInstanceOf(SegmentUpdate.class);

    }
}
