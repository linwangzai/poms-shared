//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.06.07 at 01:34:44 PM CEST
//


package nl.vpro.domain.media.nebo.enrichment.v2_4;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import nl.vpro.domain.media.nebo.base.AfbeeldingenType;
import nl.vpro.domain.media.nebo.base.FragmentenType;
import nl.vpro.domain.media.nebo.base.TabsType;
import nl.vpro.domain.media.nebo.shared.StreamType;
import nl.vpro.domain.media.nebo.shared.StreamsType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the nl.vpro.domain.media.nebo.enrichment.v2_3 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Icon_QNAME = new QName("", "icon");
    private final static QName _Mail_QNAME = new QName("", "mail");
    private final static QName _Tite_QNAME = new QName("", "tite");
    private final static QName _Inhk_QNAME = new QName("", "inhk");
    private final static QName _Inh2_QNAME = new QName("", "inh2");
    private final static QName _GidsTekst_QNAME = new QName("", "gids_tekst");
    private final static QName _Inh1_QNAME = new QName("", "inh1");
    private final static QName _Inh3_QNAME = new QName("", "inh3");
    private final static QName _Atit_QNAME = new QName("", "atit");
    private final static QName _Webs_QNAME = new QName("", "webs");
    private final static QName _Aflevering_QNAME = new QName("", "aflevering");
    private final static QName _Fragmenten_QNAME = new QName("", "fragmenten");
    private final static QName _Afbeeldingen_QNAME = new QName("", "afbeeldingen");
    private final static QName _Tabs_QNAME = new QName("", "tabs");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: nl.vpro.domain.media.nebo.enrichment.v2_3
     *
     */
    public ObjectFactory() {
    }


    public StreamsType createStreamsType() {
        return new StreamsType();
    }

    /**
     * Create an instance of {@link NeboXmlImport }
     *
     */
    public NeboXmlImport createNeboXmlImport() {
        return new NeboXmlImport();
    }

    /**
     * Create an instance of {@link AfleveringType }
     *
     */
    public AfleveringType createAfleveringType() {
        return new AfleveringType();
    }

    /**
     * Create an instance of {@link StreamType }
     *
     */
    public StreamType createStreamType() {
        return new StreamType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AfbeeldingenType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "icon")
    public JAXBElement<AfbeeldingenType> createIcon(AfbeeldingenType value) {
        return new JAXBElement<AfbeeldingenType>(_Icon_QNAME, AfbeeldingenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "mail")
    public JAXBElement<String> createMail(String value) {
        return new JAXBElement<String>(_Mail_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "tite")
    public JAXBElement<String> createTite(String value) {
        return new JAXBElement<String>(_Tite_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "inhk")
    public JAXBElement<String> createInhk(String value) {
        return new JAXBElement<String>(_Inhk_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "inh2")
    public JAXBElement<String> createInh2(String value) {
        return new JAXBElement<String>(_Inh2_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "gids_tekst")
    public JAXBElement<String> createGidsTekst(String value) {
        return new JAXBElement<String>(_GidsTekst_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "inh1")
    public JAXBElement<String> createInh1(String value) {
        return new JAXBElement<String>(_Inh1_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "inh3")
    public JAXBElement<String> createInh3(String value) {
        return new JAXBElement<String>(_Inh3_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "atit")
    public JAXBElement<String> createAtit(String value) {
        return new JAXBElement<String>(_Atit_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "webs")
    public JAXBElement<String> createWebs(String value) {
        return new JAXBElement<String>(_Webs_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AfleveringType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "aflevering")
    public JAXBElement<AfleveringType> createAflevering(AfleveringType value) {
        return new JAXBElement<AfleveringType>(_Aflevering_QNAME, AfleveringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FragmentenType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "fragmenten")
    public JAXBElement<FragmentenType> createFragmenten(FragmentenType value) {
        return new JAXBElement<FragmentenType>(_Fragmenten_QNAME, FragmentenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AfbeeldingenType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "afbeeldingen")
    public JAXBElement<AfbeeldingenType> createAfbeeldingen(AfbeeldingenType value) {
        return new JAXBElement<AfbeeldingenType>(_Afbeeldingen_QNAME, AfbeeldingenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TabsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "tabs")
    public JAXBElement<TabsType> createTabs(TabsType value) {
        return new JAXBElement<TabsType>(_Tabs_QNAME, TabsType.class, null, value);
    }

}
