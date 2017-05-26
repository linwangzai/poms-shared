/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.RangeMatcher;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DefaultDurationXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeMatcherType", propOrder = {"begin", "end"})
public class DurationRangeMatcher extends RangeMatcher<Duration> implements Predicate<Duration> {

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    private Duration begin;

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    private Duration end;


    public DurationRangeMatcher() {
    }

    public DurationRangeMatcher(Duration begin, Duration end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    public DurationRangeMatcher(Duration begin, Duration end) {
        super(begin, end, null);
    }


    @Override
    protected boolean defaultIncludeEnd() {
        return false;

    }

    @Override
    public boolean test(Duration duration) {
        return super.testComparable(duration);

    }
}
