/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.3.0
 */
public class HasGeoRestrictionConstraintTest {

    @Test
    public void testGetValue() throws Exception {
        HasGeoRestrictionConstraint in = new HasGeoRestrictionConstraint();
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasGeoRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    public void testApplyTrue() throws Exception {
        Program program = MediaTestDataBuilder.program().withGeoRestrictions().build();
        assertThat(new HasGeoRestrictionConstraint().test(program)).isTrue();
    }

    @Test
    public void testApplyFalse() throws Exception {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasGeoRestrictionConstraint().test(program)).isFalse();
    }

    @Test
    public void testGetESPath() throws Exception {
        assertThat(new HasGeoRestrictionConstraint().getESPath()).isEqualTo("regions");
    }
}
