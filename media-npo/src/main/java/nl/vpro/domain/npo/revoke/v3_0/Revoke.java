//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.03.04 at 03:34:08 PM CET 
//


package nl.vpro.domain.npo.revoke.v3_0;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="prid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pridexport" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="titel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="platform" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}omroepen"/>
 *       &lt;/sequence>
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "prid",
    "pridexport",
    "titel",
    "platform",
    "omroepen"
})
@XmlRootElement(name = "revoke")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Revoke {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String prid;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String pridexport;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String titel;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String platform;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Omroepen omroepen;
    @XmlAttribute(name = "timestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected XMLGregorianCalendar timestamp;

    /**
     * Gets the value of the prid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getPrid() {
        return prid;
    }

    /**
     * Sets the value of the prid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setPrid(String value) {
        this.prid = value;
    }

    /**
     * Gets the value of the pridexport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getPridexport() {
        return pridexport;
    }

    /**
     * Sets the value of the pridexport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setPridexport(String value) {
        this.pridexport = value;
    }

    /**
     * Gets the value of the titel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getTitel() {
        return titel;
    }

    /**
     * Sets the value of the titel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setTitel(String value) {
        this.titel = value;
    }

    /**
     * Gets the value of the platform property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the value of the platform property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setPlatform(String value) {
        this.platform = value;
    }

    /**
     * Gets the value of the omroepen property.
     * 
     * @return
     *     possible object is
     *     {@link Omroepen }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public Omroepen getOmroepen() {
        return omroepen;
    }

    /**
     * Sets the value of the omroepen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Omroepen }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setOmroepen(Omroepen value) {
        this.omroepen = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2016-03-04T03:34:08+01:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

}
