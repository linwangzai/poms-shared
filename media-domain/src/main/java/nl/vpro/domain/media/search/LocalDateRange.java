/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.*;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "localdateRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class LocalDateRange implements Range<ChronoLocalDate, LocalDateRange.Value> {

    @XmlElement
    private Value start;

    @XmlElement
    private Value stop;

    public LocalDateRange() {
    }

    public LocalDateRange(LocalDate start, LocalDate stop) {
        this.start = Value.of(start);
        this.stop = Value.of(stop);
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    @XmlType(name = "localDateRangeValueType")
    public static class Value extends Range.RangeValue<ChronoLocalDate> {

        @XmlValue
        @XmlSchemaType(name = "dateTime")
        LocalDate value;

        public Value() {

        }
        public Value(String value) {
            this.value = LocalDate.parse(value);
        }


        @lombok.Builder
        public Value(Boolean inclusive, LocalDate value) {
            super(inclusive);
            this.value = value;
        }
        public static Value of(LocalDate instant) {
            if (instant == null) {
                return null;
            }
            return builder().value(instant).build();
        }
    }

}
