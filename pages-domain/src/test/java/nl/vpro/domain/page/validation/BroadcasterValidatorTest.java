/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.validation;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.ServiceLocator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author rico
 */
public class BroadcasterValidatorTest {

    BroadcasterService broadcasterService = mock(BroadcasterService.class);


    private BroadcasterValidator validator = new BroadcasterValidator();

    @Before
    public void init() {
        when(broadcasterService.findAll()).thenReturn(Arrays.asList(Broadcaster.of("VPRO"), Broadcaster.of("EO")));
        Broadcaster vpro = new Broadcaster("VPRO", "VPRO");
        when(broadcasterService.find("VPRO")).thenReturn(vpro);
        validator.initialize(null);

        ServiceLocator.setBroadcasterService(broadcasterService);
    }

    @Test
    public void validBroadcaster() {
        assertThat(validator.isValid(Collections.singletonList("VPRO"), null)).isTrue();
    }

    @Test
    public void invalidBroadcaster() {
        assertThat(validator.isValid(Collections.singletonList("NOTABLE"), null)).isFalse();
    }
}
