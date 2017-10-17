/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.VersionService;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.SegmentType;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;

@XmlRootElement(name = "segment")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "segmentUpdateType", propOrder = {
        "start"
        })
public final class SegmentUpdate extends MediaUpdate<Segment> implements Comparable<SegmentUpdate> {

    private SegmentUpdateConfig updateConfig = new SegmentUpdateConfig();

    private SegmentUpdate() {
        this(MediaBuilder.segment());
    }

    private SegmentUpdate(MediaBuilder.AbstractSegmentBuilder builder) {
        this(builder, null);
    }

    private SegmentUpdate(Segment segment) {
        this(MediaBuilder.segment(segment), VersionService.);
    }


    public static SegmentUpdate create() {
        return new SegmentUpdate(MediaBuilder.segment());
    }

    public static SegmentUpdate create(MediaBuilder.AbstractSegmentBuilder builder) {
        return new SegmentUpdate(builder);
    }

    public static SegmentUpdate create(Segment segment) {
        return new SegmentUpdate(segment);
    }

    @Override
    public MediaBuilder.SegmentBuilder getBuilder() {
        return (MediaBuilder.SegmentBuilder) super.getBuilder();
    }

    @Override
    public SegmentUpdateConfig getConfig() {
        return updateConfig;
    }

    //@XmlAttribute
    @Override
    public SegmentType getType() {
        return builder.build().getType();
    }


    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE, required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    public java.time.Duration getStart() {
        return builder.build().getStart();
    }

    public void setStart(java.time.Duration start) {
        getBuilder().start(start);
    }
    @XmlAttribute
    public void setMidRef(String string) {
        getBuilder().midRef(string);
    }
    public String getMidRef() {
        Segment built = builder.build();
        return built == null ? null : built.getMidRef();
    }


    @Override
    public int compareTo(SegmentUpdate segmentUpdate) {
        return builder.build().compareTo(segmentUpdate.builder.build());
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent != null && parent instanceof ProgramUpdate) {
            ((MediaBuilder.SegmentBuilder)builder).parent((ProgramUpdate) parent);
        }
    }


}
