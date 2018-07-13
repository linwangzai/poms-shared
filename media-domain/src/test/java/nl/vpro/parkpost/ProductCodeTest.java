/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.parkpost;

import org.junit.Test;

/**
 * See https://jira.vpro.nl/browse/MSE-
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class ProductCodeTest {

    @Test
    public void testParse() throws Exception {
        String[] valids = {
            "2P2702MO_DEBATOP2",
            "2P2702VD_DEBATOP2",
            "2P2702VD_GELOVENO",
            "2P2802MO_BORGEN",
            "2P2802MO_KRUISPUN",
            "2P2802MO_VPROIMPO"
        } ;

        for(String code : valids) {
            ProductCode.parse(code);
        }
    }
}
