//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.04.03 at 09:16:55 AM CEST 
//


package nl.vpro.domain.npo.restriction.v2_2;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="leeftijd" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "leeftijd"
})
@XmlRootElement(name = "leeftijdsbeperking")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2018-04-03T09:16:55+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class Leeftijdsbeperking {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2018-04-03T09:16:55+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String leeftijd;

    /**
     * Gets the value of the leeftijd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2018-04-03T09:16:55+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getLeeftijd() {
        return leeftijd;
    }

    /**
     * Sets the value of the leeftijd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2018-04-03T09:16:55+02:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setLeeftijd(String value) {
        this.leeftijd = value;
    }

}
