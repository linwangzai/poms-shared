/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.TextConstraint;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.PortalRestriction;
import nl.vpro.domain.user.Portal;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "portalRestrictionConstraintType")
public class PortalRestrictionConstraint extends TextConstraint<MediaObject> {

    public PortalRestrictionConstraint() {
        caseHandling = CaseHandling.ASIS;
    }

    public PortalRestrictionConstraint(Portal value) {
        super(value.getId());
        caseHandling = CaseHandling.ASIS;
    }

    @Override
    public String getESPath() {
        return "exclusives";
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input == null) return false;
        for (PortalRestriction e : input.getPortalRestrictions()) {
            if (value.equals(e.getPortalId())) {
                return true;
            }
        }
        return false;
    }
}
