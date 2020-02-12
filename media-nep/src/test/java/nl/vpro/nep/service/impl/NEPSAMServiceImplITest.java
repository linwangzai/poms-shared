package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import org.junit.Test;


/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPSAMServiceImplITest extends  AbstractNEPTest {



    NEPSAMAuthenticator authenticator = new NEPSAMAuthenticator(
        "npo_poms",
        getProperty("password"),
        getProperty("baseUrl")

    );
    NEPSAMServiceImpl impl = new NEPSAMServiceImpl(
        getProperty("baseUrl"),
        null,
        null,
        null,
        null,
        authenticator
    );

    public NEPSAMServiceImplITest() {
        super("nep.sam-api");
    }


    @Test
    public void streamUrlForMid() {
        String streamUrl = impl.streamAccessMid("VPWON_1271554", true, "145.58.169.92", null);
        log.info("{}", streamUrl);

    }


    @Test
    public void streamUrlForLive() {
        String streamUrl = impl.streamAccessLive("npo1", "145.58.169.92", Duration.ofHours(24));
        log.info("{}", streamUrl);

    }


}
