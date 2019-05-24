package nl.vpro.domain.media.support;

import java.util.Arrays;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class OwnablesTest {


    public static class AOwnable implements OwnableR {
        private final OwnerType ownerType;

        public AOwnable(OwnerType ownerType) {
            this.ownerType = ownerType;
        }

        @Override
        public OwnerType getOwner() {
            return ownerType;

        }
    }

    @Test
    public void containsDuplicateOwner() {


        assertThat(Ownables.containsDuplicateOwner(Arrays.asList(new AOwnable(OwnerType.BROADCASTER), new AOwnable(OwnerType.AUTHORITY)))).isFalse();
        assertThat(Ownables.containsDuplicateOwner(Arrays.asList(new AOwnable(OwnerType.BROADCASTER), new AOwnable(OwnerType.BROADCASTER)))).isTrue();
        assertThat(Ownables.containsDuplicateOwner(Arrays.asList(new AOwnable(OwnerType.BROADCASTER)))).isFalse();
        assertThat(Ownables.containsDuplicateOwner(Arrays.asList())).isFalse();
    }
}
