package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.meeuw.jaxbdocumentation.DocumentationAdder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import com.google.common.io.Files;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.search.MediaSearchResult;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.user.Broadcaster;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests whether the POMS schema's are changed. If tests-cases fail here, fix them, but <em>also don't forget to make the changes to the XSD's also to the manually maintained ones</em>.
 * in nl/vpro/domain/media
 *
 * E.g. in nl/vpro/domain/media/vproMedia.xsd
 *
 * So normally you'd have to change <em>two</em> XSD's.
 *
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@Slf4j
public class SchemaTest {

    private final static File DIR = Files.createTempDir();

    private static JAXBContext context;

    @BeforeClass
    public static void generateXSDs() throws JAXBException, IOException {
        context = generate(
            // media
            Program.class,
            Segment.class,
            Schedule.class,
            Group.class,
            MediaTable.class,
            Broadcaster.class,
            // search
            MediaForm.class,
            MediaSearchResult.class,
            MediaListItem.class,
            // update
            MediaIdentifiableImpl.class,
            ProgramUpdate.class,
            GroupUpdate.class,
            SegmentUpdate.class,
            MoveAction.class,
            BulkUpdate.class,
            ImageUpdate.class,
            LocationUpdate.class,
            StreamingStatus.class,
            //
            TranscodeRequest.class,
            TranscodeStatus.class,
            ItemizeRequest.class,
            LiveItemizeRequest.class,
            ItemizeResponse.class,
            // no namespace
            XmlCollection.class
            //



        );

    }
    @Test
    public void testMedia() throws IOException {
        testNamespace(Xmlns.MEDIA_NAMESPACE);

    }

    @Test
    public void testMediaSearch() throws IOException {
        testNamespace(Xmlns.SEARCH_NAMESPACE);
    }

    @Test
    public void testShared() throws IOException {
        testNamespace(Xmlns.SHARED_NAMESPACE);
    }

    @Test
    public void testUpdate() throws IOException {
        testNamespace(Xmlns.UPDATE_NAMESPACE);
    }

    @Test
    public void testAbsent() throws IOException {
        testNamespace("");
    }

    /**
     * Checks wether manual XSD contains the correct channels.
     */
    @Test
    public void testChannels() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", "channelEnum", Channel.class);
    }


    @Test
    public void testProgramType() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", "programTypeEnum", ProgramType.class);
    }

    @Test
    public void testGroupType() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", "groupTypeEnum", GroupType.class);
    }

    @Test
    public void testSegmentType() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", "segmentTypeEnum", SegmentType.class);
    }
    @Test
    public void testMediaType() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", "mediaTypeEnum", MediaType.class);
    }

    protected <T extends Enum<T>> void testEnum(String resource, String enumTypeName, Class<T> enumClass) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(getClass().getResourceAsStream(resource));

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList)xPath.evaluate("/schema/simpleType[@name='" + enumTypeName + "']/restriction/enumeration", document, XPathConstants.NODESET);

        Set<String> valuesInXsd = new TreeSet<>();
        for (int i  = 0; i < nodes.getLength(); i++) {
            valuesInXsd.add(nodes.item(i).getAttributes().getNamedItem("value").getTextContent());
        }

        Set<String> valuesInEnum = new TreeSet<>();

        Method method = enumClass.getDeclaredMethod("values");
        T[] values = (T[]) method.invoke(null);
        for (T v : values) {
            XmlEnumValue xmlEnumValue = enumClass.getField(v.name()).getAnnotation(XmlEnumValue.class);
            valuesInEnum.add(xmlEnumValue != null ? xmlEnumValue.value() : v.name());
        }
        assertThat(valuesInXsd)
            .isEqualTo(
                valuesInEnum);
    }

    private static File getFile(final String namespace) {
        String filename = namespace;
        if (StringUtils.isEmpty(namespace)) {
            filename = "absentnamespace";
        }
        return new File(DIR, filename + ".xsd");
    }

    private  void testNamespace(String namespace) throws IOException {
        File file = getFile(namespace);
        InputStream control = getClass().getResourceAsStream("/schema/" + file.getName());
        if (control == null) {
            System.out.println(file.getName());
            IOUtils.copy(new FileInputStream(file), System.out);
            throw new RuntimeException("No file " + file.getName());
        }
        Diff diff = DiffBuilder.compare(control)
            .withTest(file)
            .checkForIdentical()
            .build();

        assertThat(diff.hasDifferences())
            .withFailMessage("" + file + " should be equal to " + getClass().getResource("/schema/" + file.getName())).isFalse();
    }

    private static JAXBContext generate(Class... classes) throws JAXBException, IOException {
        DocumentationAdder collector = new DocumentationAdder(classes);

        JAXBContext context = JAXBContext.newInstance(classes);

        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
                    return null;
                }
                File f = getFile(namespaceUri);
                if (f.exists()) {
                    f = File.createTempFile(namespaceUri, "");
                }
                log.info(namespaceUri + " -> " + f);

                StreamResult result = new StreamResult(f);
                result.setSystemId(f);
                FileOutputStream fo = new FileOutputStream(f);
                result.setOutputStream(fo);

                return result;
            }
        });
        return context;
    }
}
