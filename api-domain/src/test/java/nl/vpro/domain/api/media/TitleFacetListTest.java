package nl.vpro.domain.api.media;

import java.util.Arrays;

import org.junit.Test;

import nl.vpro.domain.api.*;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class TitleFacetListTest {

    TitleFacetList list;

    {
        TitleFacet facet1 = new TitleFacet();
        {
            TitleSearch subSearch = TitleSearch.builder()
                .value("a*")
                .match(Match.SHOULD)
                .matchType(ExtendedMatchType.WILDCARD)
                .caseSensitive(false)
                .build();

            facet1.setName("titlesWithA");
            facet1.setSubSearch(subSearch);
        }
        TitleFacet facet2 = new TitleFacet();
        {
            TitleSearch subSearch = TitleSearch.builder()
                .value("b*")
                .match(Match.MUST)
                .matchType(ExtendedMatchType.WILDCARD)
                .caseSensitive(false)
                .build();

            facet2.setName("titlesWithB");
            facet2.setSubSearch(subSearch);
        }
        list = new TitleFacetList(Arrays.asList(facet1, facet2));
        list.setMax(11);
        list.setSort(FacetOrder.COUNT_DESC);
    }


       @Test
    public void testJsonBinding() throws Exception {

        list = Jackson2TestUtil.roundTripAndSimilar(list,
            "[ {\n" +
                "  \"max\" : 11,\n" +
                "  \"sort\" : \"COUNT_DESC\"\n" +
                "}, {\n" +
                "  \"name\" : \"titlesWithA\",\n" +
                "  \"subSearch\" : {\n" +
                "    \"value\" : \"a*\",\n" +
                "    \"matchType\" : \"WILDCARD\",\n" +
                "    \"match\" : \"SHOULD\"\n" +
                "  }\n" +
                "}, {\n" +
                "  \"name\" : \"titlesWithB\",\n" +
                "  \"subSearch\" : {\n" +
                "    \"value\" : \"b*\",\n" +
                "    \"matchType\" : \"WILDCARD\"\n" +
                "  }\n" +
                "} ]");


        assertThat(list.facets).hasSize(2);
        assertThat(list.getMax()).isEqualTo(11);
        assertTrue(list.facets.get(0).getSubSearch() != null);
    }

    @Test
    public void testJsonBindingBackwards() throws Exception {
        TitleFacetList backwards = new TitleFacetList();
        backwards.setMax(11);
        backwards.setSort(FacetOrder.COUNT_DESC);
        backwards = Jackson2TestUtil.roundTripAndSimilar(backwards,
            "{\n" +
                "  \"max\" : 11,\n" +
                "  \"sort\" : \"COUNT_DESC\"\n" +
                "}");


        assertThat(backwards.facets).isNull();
        assertThat(backwards.getMax()).isEqualTo(11);
    }


    @Test
    public void testXmlBinding() throws Exception {

        list = JAXBTestUtil.roundTripAndSimilar(list,
            "<local:titleFacetList sort=\"COUNT_DESC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:max>11</api:max>\n" +
                "    <api:title name=\"titlesWithA\">\n" +
                "        <api:subSearch matchType=\"WILDCARD\" match=\"SHOULD\">a*</api:subSearch>\n" +
                "    </api:title>\n" +
                "    <api:title name=\"titlesWithB\">\n" +
                "        <api:subSearch matchType=\"WILDCARD\">b*</api:subSearch>\n" +
                "    </api:title>\n" +
                "</local:titleFacetList>");
        assertThat(list.facets).hasSize(2);
        assertThat(list.facets.get(0).getSubSearch()).isNotNull();
        assertThat(list.facets.get(1).getSubSearch()).isNotNull();

    }


    @Test
    public void testSubSearch() throws Exception {
        String example = "{\n" +
            "  \"value\" : \"a*\"\n" +
            "}";
        TitleSearch subSearch = TitleSearch.builder().value("a*").build();
        TitleSearch search = Jackson2TestUtil.roundTripAndSimilarAndEquals(subSearch, example);
    }


}
