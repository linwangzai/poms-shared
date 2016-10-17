package nl.vpro.domain.media.update.collections;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXB;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.update.LocationUpdate;

import static org.junit.Assert.assertTrue;

public class XmlCollectionTest {

    @Test
    public void marshall() throws IOException, SAXException {
        XmlCollection<LocationUpdate> col = new XmlCollection<>(Arrays.asList(new LocationUpdate()));
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<collection xmlns:update=\"urn:vpro:media:update:2009\" xmlns:media=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <update:location/>\n" +
            "</collection>";
        StringWriter writer = new StringWriter();
        JAXB.marshal(col, System.out);
        JAXB.marshal(col, writer);
        Diff diff = XMLUnit.compareXML(expected, writer.toString());
        assertTrue(diff.toString() + " " + expected, diff.identical());

    }

}
