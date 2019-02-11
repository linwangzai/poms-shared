package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import org.junit.Test;

import nl.vpro.nep.domain.NEPItemizeRequest;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class NEPItemizeServiceImplITest {

    @Test
    public void itemize() {
        Instant start = Instant.now();
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl("https://itemizer-npocdn-stg.nepworldwide.nl/v1/api/itemizer/job",
            "");
        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("AT_2073522");
        request.setStarttime("00:00:00.000");
        request.setEndtime("00:02:21.151");
        log.info("response: {} {}", itemizer.itemize(request), start);


    }
}
