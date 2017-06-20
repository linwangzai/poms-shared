/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlType(name = "dateRangeMatcherType", propOrder = {"begin", "end"})
public class DateRangeMatcher extends RangeMatcher<Instant> implements Predicate<Instant> {

    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant begin;
    @XmlElement
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant end;


    public DateRangeMatcher() {
    }

    public DateRangeMatcher(Instant begin, Instant end) {
        super(begin, end);
    }

    public DateRangeMatcher(Instant begin, Instant end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    @Deprecated
    public DateRangeMatcher(Date begin, Date end, Boolean inclusiveEnd) {
        super(DateUtils.toInstant(begin), DateUtils.toInstant(end), inclusiveEnd);
    }

    @Deprecated
    public DateRangeMatcher(Date begin, Date end) {
        super(DateUtils.toInstant(begin), DateUtils.toInstant(end));
    }


    public DateRangeMatcher(Instant begin, Instant end, Boolean inclusiveEnd, Match match) {
        super(begin, end, inclusiveEnd, match);
    }

    @Override
    protected boolean defaultIncludeEnd() {
        return false;

    }

    @Override
    public boolean test(Instant date) {
        return super.testComparable(date);

    }
}
