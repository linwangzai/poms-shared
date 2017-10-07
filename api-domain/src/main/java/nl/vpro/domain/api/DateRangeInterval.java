/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;
import nl.vpro.i18n.Locales;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * @author Roelof Jan Koekoek
 * @since 3.1
 */
@XmlType(name = "dateRangeIntervalType", propOrder = {
})
@XmlAccessorType(XmlAccessType.FIELD)
public class DateRangeInterval implements RangeFacet<Instant> {


    public static final String TIMEZONE = "CET";

    public static final ZoneId ZONE = ZoneId.of(TIMEZONE);

    public static final ZoneId GMT = ZoneId.of("GMT");


    @Getter
    @Setter
    private Interval interval;

    public DateRangeInterval() {
    }

    public DateRangeInterval(String interval) {
        this.interval = new Interval(ParsedInterval.parse(interval));
    }


    @Override
    public boolean matches(Instant begin, Instant end) {

        return
            Duration.between(begin, end).equals(interval.getDuration())
                && interval.isBucketBegin(begin)
                && interval.isBucketEnd(end);
    }


    @XmlType(name = "temporalIntervalType", propOrder = {
    })
    public static class Interval extends ParsedInterval<Instant> {


        public Interval(ParseResult pair) {
            super(pair);
        }

        protected ZonedDateTime getZoned(Instant instant) {
            return instant.atZone(Schedule.ZONE_ID);
        }
        protected ZonedDateTime truncated(ZonedDateTime zoned) {
            ZonedDateTime truncated;
            if (getUnit().getChronoField().compareTo(ChronoField.SECOND_OF_DAY) > 0) {
                truncated = zoned.truncatedTo(ChronoField.SECOND_OF_DAY.getBaseUnit());

            } else {
                truncated = zoned.truncatedTo(getUnit().getChronoField().getBaseUnit());
            }
            return truncated;
        }
        protected int getRelevantFieldValue(ZonedDateTime zoned) {
            return zoned.get(getUnit().getChronoField());
        }

        @Override
        public boolean isBucketBegin(Instant begin) {
            ZonedDateTime zoned = getZoned(begin);
            return getRelevantFieldValue(zoned) % amount == 0
                && zoned.equals(truncated(zoned));
        }

        @Override
        public boolean isBucketEnd(Instant end) {
            ZonedDateTime zoned = getZoned(end);
            return getRelevantFieldValue(zoned) % amount == 0
                && zoned.equals(truncated(zoned));

        }

        private static final WeekFields WEEK_FIELDS= WeekFields.of(Locales.DUTCH);

        @Override
        public String print(Instant dateTime) {

            switch(unit) {
                case YEAR:
                    return String.valueOf(dateTime.atZone(ZONE).getYear());
                case MONTH:
                    return String.format("%04d-%02d", dateTime.atZone(ZONE).getYear(), dateTime.atZone(ZONE).getMonthValue());
                case WEEK:
                    return String.format("%04d-W%02d", dateTime.atZone(ZONE).getYear(), dateTime.atZone(ZONE).get(WEEK_FIELDS.weekOfYear()));
                case DAY:
                    return dateTime.atZone(ZONE).toLocalDate().toString();
                case HOUR:
                    return ISO_OFFSET_DATE_TIME.format(dateTime.atZone(ZONE));
                case MINUTE:
                    return ISO_OFFSET_DATE_TIME.format(dateTime.atZone(ZONE));
                default:
                    throw new IllegalArgumentException();
            }

        }

    }


}

