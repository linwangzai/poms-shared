/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.time.Duration;

import javax.xml.bind.JAXB;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.DateUtils;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class MediaFormTest {

    @Test
    public void testGetSort() throws Exception {
        MediaForm in = new MediaForm();
        MediaSortOrderList list = new MediaSortOrderList();
        list.put(MediaSortField.sortDate, Order.DESC);
        list.put(MediaSortField.title, null);
        list.add(TitleSortOrder.builder().textualType(TextualType.LEXICO).build());
        in.setHighlight(true);

        in.setSortFields(list);
        MediaForm out = JAXBTestUtil.roundTripAndSimilar(in,
            "<api:mediaForm highlight=\"true\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <api:sortFields>\n" +
                "        <api:sort order=\"DESC\">sortDate</api:sort>\n" +
                "        <api:sort order=\"ASC\">title</api:sort>\n" +
                "        <api:titleSort textualType=\"LEXICO\" order=\"ASC\" />\n"+
                "    </api:sortFields>\n" +
                "</api:mediaForm>"
        );
        assertThat(out.getSortFields()).hasSize(3);
        MediaForm result = Jackson2TestUtil.roundTripAndSimilarAndEquals(out, "{\n" +
            "  \"sort\" : {\n" +
            "    \"sortDate\" : \"DESC\",\n" +
            "    \"title\" : \"ASC\",\n" +
            "    \"title:0\" : {\n" +
            "      \"order\" : \"ASC\",\n" +
            "      \"textualType\" : \"LEXICO\",\n" +
            "      \"sortField\" : \"title\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"highlight\" : true\n" +
            "}");
        assertThat(result.getSortFields()).hasSize(3);
    }


    @Test
    public void parseWithEmptySort() throws Exception {
        String example = "{\n" +
            "  \"sort\" : {\n" +
            "  }\n" +
            "}\n";
        MediaForm form = Jackson2Mapper.getInstance().readValue(new StringReader(example), MediaForm.class);
        assertThat(form.getSortFields()).isEmpty();
        Jackson2TestUtil.roundTripAndSimilar(form, example);
    }

    @Test
    public void testGetTags() throws Exception {
        MediaForm in = MediaFormBuilder.form().tags(Match.SHOULD, new Tag("XML")).build();
        MediaForm out = JAXBTestUtil.roundTripAndSimilar(in,
                "<api:mediaForm  xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                    "    <api:searches>\n" +
                    "        <api:tags match=\"SHOULD\">\n" +
                    "            <api:matcher match=\"SHOULD\">XML</api:matcher>\n" +
                    "        </api:tags>\n" +
                    "    </api:searches>\n" +
                    "</api:mediaForm>"
        );
        assertThat(out.getSearches().getTags().size()).isEqualTo(1);
        assertThat(out.getSearches().getTags().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(out.getSearches().getTags().get(0).getValue()).isEqualTo("XML");
    }

    @Test
    public void testGetFacets() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        MediaForm in = MediaFormBuilder.form().broadcasterFacet().scheduleEvents(
            new ScheduleEventSearch(Channel.NED3, DateUtils.toInstant(simpleDateFormat.parse("2015-01-26")), DateUtils.toInstant(simpleDateFormat.parse("2015-01-27")))).build();
        MediaForm out = JAXBTestUtil.roundTripAndSimilar(in,
            "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <api:searches>\n" +
                "        <api:scheduleEvents>\n" +
                "            <api:begin>2015-01-26T00:00:00+01:00</api:begin>\n" +
                "            <api:end>2015-01-27T00:00:00+01:00</api:end>\n" +
                "            <api:channel>NED3</api:channel>\n" +
                "        </api:scheduleEvents>\n" +
                "    </api:searches>\n" +
                "    <api:facets>\n" +
                "        <api:broadcasters sort=\"VALUE_ASC\">\n" +
                "            <api:max>24</api:max>\n" +
                "        </api:broadcasters>\n" +
                "    </api:facets>\n" +
                "</api:mediaForm>");
        assertThat(out.getFacets().getBroadcasters().getSort()).isEqualTo(FacetOrder.VALUE_ASC);
    }

    @Test
    public void testGetFacetsBackwards() throws Exception {
        MediaForm out = JAXB.unmarshal(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<mediaForm xmlns=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" highlight=\"false\">\n" +
            "    <searches>\n" +
            "        <scheduleEvents inclusiveEnd=\"true\">\n" +
            "            <begin>2015-01-26T00:00:00+01:00</begin>\n" +
            "            <end>2015-01-27T00:00:00+01:00</end>\n" +
            "            <channel>NED3</channel>\n" +
            "        </scheduleEvents>\n" +
            "    </searches>\n" +
            "    <facets>\n" +
            "        <broadcasters sort=\"REVERSE_TERM\">\n" +
            "            <threshold>0</threshold>\n" +
            "            <offset>0</offset>\n" +
            "            <max>24</max>\n" +
            "        </broadcasters>\n" +
            "    </facets>\n" +
            "</mediaForm>"), MediaForm.class);
        assertThat(out.getFacets().getBroadcasters().getSort()).isEqualTo(FacetOrder.VALUE_DESC);
    }


    @Test
    public void testFilterTags() throws IOException {
        String tagForm = "{\n" +
            "    \"facets\": {\n" +
            "        \"tags\": {\n" +
            "            \"filter\": {\n" +
            "                \"tags\":  {\n" +
            "                    \"matchType\": \"WILDCARD\",\n" +
            "                    \"match\": \"MUST\",\n" +
            "                    \"value\": \"Lief*\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"max\": 10000\n" +
            "        }\n" +
            "    },\n" +
            "    \"searches\": {\n" +
            "        \"tags\": {\n" +
            "            \"matchType\": \"WILDCARD\",\n" +
            "            \"match\": \"SHOULD\",\n" +
            "            \"value\": \"Lief*\"\n" +
            "        }\n" +
            "\n" +
            "    }\n" +
            "}";
        MediaForm form = Jackson2Mapper.getInstance().readValue(tagForm, MediaForm.class);
        assertThat(form.getSearches().getTags().get(0).getValue()).isEqualTo("Lief*");
        assertThat(form.getSearches().getTags().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(form.getSearches().getTags().get(0).getMatchType().getName()).isEqualTo(StandardMatchType.WILDCARD.getName());
        assertThat(form.getFacets().getTags().getFilter().getTags().get(0).getMatch()).isEqualTo(Match.MUST);
        assertThat(form.getFacets().getTags().getFilter().getTags().get(0).getMatchType().getName()).isEqualTo(StandardMatchType.WILDCARD.getName());
    }

    @Test
    public void testSubSearch() throws IOException {
        String example = "{\n" +
            "    \"facets\": {\n" +
            "        \"relations\":  {\n" +
            "            \"subSearch\": {\n" +
            "                \"broadcasters\": \"VPRO\"\n" +
            "            },\n" +
            "            \"value\": [\n" +
            "                {\n" +
            "                    \"name\": \"labels\",\n" +
            "                    \"sort\": \"COUNT_DESC\",\n" +
            "                    \"max\": 3,\n" +
            "                    \"subSearch\": {\n" +
            "                        \"types\": \"LABEL\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"artiesten\",\n" +
            "                    \"sort\": \"COUNT_DESC\",\n" +
            "                    \"max\": 3,\n" +
            "                    \"subSearch\": {\n" +
            "                        \"types\": \"ARTIST\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        MediaForm form = assertThatJson(MediaForm.class, example).isSimilarTo(example).get();
        assertThat(form.getFacets()).isNotNull();
        assertThat(form.getFacets().getRelations()).isNotNull();
        assertNotNull(form.getFacets().getRelations().getSubSearch());
        assertNotNull(form.getFacets().getRelations().getSubSearch().getBroadcasters());
        assertThat(form.getFacets().getRelations().getSubSearch().getBroadcasters().getMatchers().get(0).getValue()).isEqualTo("VPRO");

        assertThat(form.getFacets().getRelations().getFacets()).hasSize(2);
        assertNotNull(form.getFacets().getRelations().getFacets().get(0).getSubSearch());
        assertThat(form.getFacets().getRelations().getFacets().get(0).getSubSearch().getTypes().get(0).getValue()).isEqualTo("LABEL");


    }

    @Test
    public void testFuzzinessBinding() throws Exception {
        MediaForm form = MediaForm.builder().fuzzyText("bla").build();
        JAXBTestUtil.roundTripAndSimilar(form, "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:text fuzziness=\"AUTO\" match=\"SHOULD\">bla</api:text>\n" +
            "    </api:searches>\n" +
            "</api:mediaForm>");

        Jackson2TestUtil.roundTripAndSimilar(form, "{\n" +
            "  \"searches\" : {\n" +
            "    \"text\" : {\n" +
            "      \"value\" : \"bla\",\n" +
            "      \"match\" : \"should\",\n" +
            "      \"fuzziness\" : \"auto\"\n" +
            "    }\n" +
            "  }\n" +
            "}");


    }


    @Test
    public void testTitleSearch() throws Exception {
        MediaForm form = MediaForm
            .builder()
            .fuzzyText("bla")
            .build();
        JAXBTestUtil.roundTripAndSimilar(form, "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:text fuzziness=\"AUTO\" match=\"SHOULD\">bla</api:text>\n" +
            "    </api:searches>\n" +
            "</api:mediaForm>");

        Jackson2TestUtil.roundTripAndSimilar(form, "{\n" +
            "  \"searches\" : {\n" +
            "    \"text\" : {\n" +
            "      \"value\" : \"bla\",\n" +
            "      \"match\" : \"should\",\n" +
            "      \"fuzziness\" : \"auto\"\n" +
            "    }\n" +
            "  }\n" +
            "}");


    }
    private static String LUNATIC_BACKWARD_COMPATIBLE = "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
        "    <api:searches>\n" +
        "        <api:durations match=\"MUST\">\n" +
        "            <api:matcher inclusiveEnd=\"false\">\n" +
        "                <api:begin>1970-01-01T01:05:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:10:00+01:00</api:end>\n" +
        "            </api:matcher>\n" +
        "        </api:durations>\n" +
        "    </api:searches>\n" +
        "    <api:facets>\n" +
        "        <api:durations>\n" +
        "            <api:range>\n" +
        "                <api:name>0-5m</api:name>\n" +
        "                <api:begin>1970-01-01T01:00:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:05:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>5-10m</api:name>\n" +
        "                <api:begin>1970-01-01T01:05:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:10:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>10m-30m</api:name>\n" +
        "                <api:begin>1970-01-01T01:10:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:30:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>30m-60m</api:name>\n" +
        "                <api:begin>1970-01-01T01:30:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T02:00:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>60m-∞</api:name>\n" +
        "                <api:begin>1970-01-01T02:00:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T05:00:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "        </api:durations>\n" +
        "    </api:facets>\n" +
        "</api:mediaForm>\n";
    @Test
    public void testDurations() throws IOException, SAXException {
        String json = "{\n" +
            "\n" +
            "    \"searches\" : {\n" +
            "        \"durations\" : [ {\n" +
            "            \"begin\" : 300001,\n" +
            "            \"end\" : 600000\n" +
            "        } ]\n" +
            "    },\n" +
            "    \"facets\" : {\n" +
            "        \"durations\" : [ {\n" +
            "            \"name\" : \"0-5m\",\n" +
            "            \"begin\" : 1,\n" +
            "            \"end\" : 300000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"5-10m\",\n" +
            "            \"begin\" : 300001,\n" +
            "            \"end\" : 600000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"10m-30m\",\n" +
            "            \"begin\" : 600001,\n" +
            "            \"end\" : 1800000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"30m-60m\",\n" +
            "            \"begin\" : 1800001,\n" +
            "            \"end\" : 3600000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"60m-∞\",\n" +
            "            \"begin\" : 3600001,\n" +
            "            \"end\" : 14400000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        } ]\n" +
            "    }\n" +
            "}\n";

        MediaForm fromJson = Jackson2Mapper.STRICT.readerFor(MediaForm.class).readValue(new StringReader(json));
        JAXBTestUtil.roundTripAndSimilar(fromJson, "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:durations match=\"MUST\">\n" +
            "            <api:matcher>\n" +
            "                <api:begin>PT5M0.001S</api:begin>\n" +
            "                <api:end>PT10M</api:end>\n" +
            "            </api:matcher>\n" +
            "        </api:durations>\n" +
            "    </api:searches>\n" +
            "    <api:facets>\n" +
            "        <api:durations>\n" +
            "            <api:range>\n" +
            "                <api:name>0-5m</api:name>\n" +
            "                <api:begin>PT0.001S</api:begin>\n" +
            "                <api:end>PT5M</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>5-10m</api:name>\n" +
            "                <api:begin>PT5M0.001S</api:begin>\n" +
            "                <api:end>PT10M</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>10m-30m</api:name>\n" +
            "                <api:begin>PT10M0.001S</api:begin>\n" +
            "                <api:end>PT30M</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>30m-60m</api:name>\n" +
            "                <api:begin>PT30M0.001S</api:begin>\n" +
            "                <api:end>PT1H</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>60m-∞</api:name>\n" +
            "                <api:begin>PT1H0.001S</api:begin>\n" +
            "                <api:end>PT4H</api:end>\n" +
            "            </api:range>\n" +
            "        </api:durations>\n" +
            "    </api:facets>\n" +
            "</api:mediaForm>\n");
    }
    @Test
    public void testBackwards() {
        MediaForm form = JAXB.unmarshal(new StringReader(LUNATIC_BACKWARD_COMPATIBLE), MediaForm.class);
        assertThat(((DurationRangeFacetItem) form.getFacets().getDurations().getRanges().get(0)).getEnd()).isEqualTo(Duration.ofMinutes(5));

    }
}
