/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.commons.lang3.StringUtils;
import org.meeuw.xml.bind.annotation.XmlDocumentation;

import nl.vpro.domain.Displayable;

@XmlEnum
public enum Region implements Displayable {


    @XmlDocumentation("Means that this object can only be played in the Netherlands")
    NL("Nederland"),

    /**
     * @deprecated Not supported by VMV
     */
    @Deprecated
    @XmlDocumentation("Means that this object can only be played in the Netherlands, Belgium and Luxemburg (This is, as far was we know, not support by the NPO player)")
    BENELUX("Benelux"),

    /**
     * @since 5.6
     */
    @XmlDocumentation("Means that this object can only be played in Europe")
    EUROPE("Europa"),

    /**
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes")
    NLBES("Nederland en de BES-gemeenten"),

     /**
     * @since 5.6
     */
    @XmlDocumentation("Nederland plus BES gemeentes plus Curaçao, St. Maarten en Aruba")
    NLALL("Nederland, de BES-gemeenten, Curaçao, St. Maarten en Aruba"),

    /**
     * @since 5.6
     */
    @XmlDocumentation("European Union incl. BES gemeentes, Curaçao, St. Maarten en Aruba")
    EU("De EU inclusief de BES-gemeenten, Curaçao, St. Maarten en Aruba");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static Region valueOfOrNull(String v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        }
        return valueOf(v);
    }
}
