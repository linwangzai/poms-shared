//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 03:34:08 PM CET 
//


package nl.vpro.domain.npo.restriction.v2_1;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="starttijd" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="eindtijd" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "starttijd",
    "eindtijd"
})
@XmlRootElement(name = "tijdsbeperking")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Tijdsbeperking {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected XMLGregorianCalendar starttijd;
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected XMLGregorianCalendar eindtijd;

    /**
     * Gets the value of the starttijd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public XMLGregorianCalendar getStarttijd() {
        return starttijd;
    }

    /**
     * Sets the value of the starttijd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setStarttijd(XMLGregorianCalendar value) {
        this.starttijd = value;
    }

    /**
     * Gets the value of the eindtijd property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public XMLGregorianCalendar getEindtijd() {
        return eindtijd;
    }

    /**
     * Sets the value of the eindtijd property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setEindtijd(XMLGregorianCalendar value) {
        this.eindtijd = value;
    }

}
