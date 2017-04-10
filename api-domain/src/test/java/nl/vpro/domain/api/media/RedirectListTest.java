package nl.vpro.domain.api.media;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RedirectListTest {

    private RedirectList instance = new RedirectList();

    {
        Map<String, String> redirects = new HashMap<>();
        redirects.put("a", "b");
        instance.redirects = redirects;
    }

    @Test
    public void json() throws Exception {
        //Jackson2Mapper.getInstance().writeValue(System.out, instance);
        String expected = "{\"lastUpdate\":\"1970-01-01T01:00:00+01:00\",\"map\":{\"a\":\"b\"}}";
        RedirectList rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(instance, expected);
        assertThat(rounded.getList()).hasSize(1);
    }

    @Test
    public void jaxb() throws Exception {
        JAXBTestUtil.roundTripAndSimilarAndEquals(instance,
            "<redirects lastUpdate=\"1970-01-01T01:00:00+01:00\" xmlns=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <entry from=\"a\" to=\"b\"/>\n" +
            "</redirects>");

    }

}
