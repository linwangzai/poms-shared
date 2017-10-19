/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;

/**
 * A api-schedule event is like a poms schedule event but:
 * 1. it is in the api namespace
 * 2. it's internal mediaobject is not xmltransient
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@XmlRootElement(name = "scheduleItem")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventApiType", propOrder = {

})
@XmlSeeAlso(Match.class)
public class ApiScheduleEvent extends ScheduleEvent {

    public ApiScheduleEvent() {
    }

    public ApiScheduleEvent(nl.vpro.domain.media.ScheduleEvent event) {
        super(event);
    }

    public ApiScheduleEvent(nl.vpro.domain.media.ScheduleEvent event, MediaObject media) {
        super(event, media);
    }


    @XmlElements({
        @XmlElement(name = "program", namespace = Xmlns.MEDIA_NAMESPACE, type = Program.class),
        @XmlElement(name = "group", namespace = Xmlns.MEDIA_NAMESPACE, type = Group.class),
        @XmlElement(name = "segment", namespace = Xmlns.MEDIA_NAMESPACE, type = Segment.class)
    })
    @Override
    public MediaObject getParent() {
        return super.getParent();
    }
    @Override
    public void setParent(MediaObject o) {
        this.mediaObject = o;
    }

    @JsonProperty("media")
    public MediaObject getMediaJSON() {
        return this.getParent();
    }

    public void setMediaJSON(MediaObject media) {
        this.setParent(media);
    }
}
