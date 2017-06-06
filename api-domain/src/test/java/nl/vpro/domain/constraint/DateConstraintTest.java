package nl.vpro.domain.constraint;

import java.time.LocalDate;

import org.junit.Test;

import nl.vpro.domain.media.Schedule;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3.1
 */
public class DateConstraintTest {

    DateConstraint constraint = new DateConstraint() {
        @Override
        public String getESPath() {
            return null;

        }
    };
    @Test
    public void parser() throws Exception {

        constraint.setDate(" 2000-01-01 midnight +1");
        assertThat(constraint.getDateAsInstant()).isEqualTo(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant());
    }

    @Test
    public void applyDateOperators() throws Exception {

        constraint.setDate("2000-01-01 midnight +1");
        constraint.setOperator(Operator.GT);

        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant())).isFalse();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().minusMillis(1))).isFalse();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().plusMillis(1))).isTrue();


        constraint.setOperator(Operator.GTE);

        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant())).isTrue();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().minusMillis(1))).isFalse();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().plusMillis(1))).isTrue();


        constraint.setOperator(Operator.LT);

        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant())).isFalse();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().minusMillis(1))).isTrue();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().plusMillis(1))).isFalse();

        constraint.setOperator(Operator.LTE);

        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant())).isTrue();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().minusMillis(1))).isTrue();
        assertThat(constraint.applyDate(LocalDate.of(2000, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant().plusMillis(1))).isFalse();

    }

}
