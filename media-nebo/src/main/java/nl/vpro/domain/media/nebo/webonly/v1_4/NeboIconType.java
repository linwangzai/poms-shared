//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.06.07 at 01:34:11 PM CEST
//


package nl.vpro.domain.media.nebo.webonly.v1_4;

import java.math.BigInteger;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for nebo_iconType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="nebo_iconType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nebo_iconType")
public class NeboIconType {

    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger id;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger value) {
        this.id = value;
    }

}
