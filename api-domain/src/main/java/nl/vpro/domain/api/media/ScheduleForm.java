/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Form;
import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlRootElement(name = "scheduleForm")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleFormType",
    propOrder = {
        // Intellij warnings are incorrect since parent class is @XmlTransient
        "searches",
        "facets"
    })
public class ScheduleForm extends AbstractMediaForm implements Form, Predicate<MediaObject> {


    public static ScheduleForm from(MediaForm form) {
        ScheduleForm scheduleForm = new ScheduleForm();
        scheduleForm.setSearches(form.getSearches());
        scheduleForm.setFacets(form.getFacets());
        scheduleForm.setHighlight(form.isHighlight());
        return scheduleForm;
    }


}
