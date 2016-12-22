/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.DescendantRef;
import nl.vpro.domain.media.MediaObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "descendantOfConstraintType")
public class DescendantOfConstraint extends TextConstraint<MediaObject>  {

    public DescendantOfConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public DescendantOfConstraint(String value) {
        super(value);
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "descendantOf.midRef";
    }

    @Override
    public boolean test(MediaObject input) {
        for(DescendantRef ref : input.getDescendantOf()) {
            if(value.equals(ref.getMidRef())) {
                return true;
            }
        }
        return false;
    }
}
