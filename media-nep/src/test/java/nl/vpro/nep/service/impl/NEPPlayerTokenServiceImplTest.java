package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;

import nl.vpro.nep.domain.PlayreadyResponse;
import nl.vpro.nep.domain.WideVineResponse;
import nl.vpro.nep.service.NEPPlayerTokenService;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPPlayerTokenServiceImplTest extends AbstractNEPTest {



    NEPPlayerTokenService impl = new NEPPlayerTokenServiceImpl(
        getProperty("baseUrl"),
        getProperty("widevinekey"),
        getProperty("playreadykey")
    );

    public NEPPlayerTokenServiceImplTest() {
        super("nep.tokengenerator-api");
    }

    @Test
    public void widevine() {
        WideVineResponse wideVineResponse = impl.widevineToken("145.58.169.92");
        log.info("{}", wideVineResponse);

    }


    @Test
    public void playready() {
        PlayreadyResponse wideVineResponse = impl.playreadyToken("145.58.169.92");
        log.info("{}", wideVineResponse);

    }
}
