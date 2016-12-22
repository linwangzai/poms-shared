/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "programUrlConstraintType")
public class ProgramUrlConstraint extends TextConstraint<MediaObject>  {

    public ProgramUrlConstraint() {
        caseHandling = CaseHandling.ASIS;
        exact = true;
    }

    public ProgramUrlConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
        exact = true;
    }

    @Override
    public String getESPath() {
        return "locations.programUrl";
    }

    public Boolean getExact() {
        return exact;
    }

    @XmlAttribute
    public void setExact(Boolean exact) {
        this.exact = exact != null ? exact : false;
    }

    @Override
    public boolean test(MediaObject input) {
        for(Location location : input.getLocations()) {
            if(exact && location.getProgramUrl().contains(value)) {
                return true;
            } else if(location.getProgramUrl().equals(value)) {
                return true;
            }
        }

        return false;
    }
}
