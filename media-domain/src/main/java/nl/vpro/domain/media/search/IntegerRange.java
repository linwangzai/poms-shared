/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "integerRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
public class IntegerRange implements Range<Long, IntegerRange.Value> {

    @XmlElement
    private Value start;

    @XmlElement
    private Value stop;

    public IntegerRange() {
    }

    public static class Builder {
        public Builder start(Long start) {
            this.start = Value.builder().value(start).build();
            return this;
        }

        public Builder stop(Long stop) {
            this.stop = Value.builder().value(stop).build();
            return this;
        }

        public Builder start(Long start, boolean inclusive) {
            this.start = Value.builder().value(start).inclusive(inclusive).build();
            return this;
        }

        public Builder stop(Long stop, boolean inclusive) {
            this.stop = Value.builder().value(stop).inclusive(inclusive).build();
            return this;
        }


        public Builder equals(Long stopandstart) {
            return stop(stopandstart, true).start(stopandstart, true);
        }
        public Builder start(Value start) {
            this.start = start;
            return this;
        }

        public Builder stop(Value stop) {
            this.stop = stop;
            return this;
        }
    }



    @Data
    @EqualsAndHashCode(callSuper = true)
    @XmlType(name = "integerRangeValueType")
    public static class Value extends Range.RangeValue<Long> {

        @XmlValue
        Long value;

        public Value() {

        }
        @lombok.Builder
        public Value(Boolean inclusive, Long value) {
            super(inclusive);
            this.value = value;
        }

        public static Value of(Long instant) {
            return builder().value(instant).build();
        }
    }

}
