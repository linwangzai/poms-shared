package nl.vpro.domain.page.update;

import java.io.StringReader;
import java.util.Collections;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXB;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.vpro.domain.page.PageType;
import nl.vpro.domain.page.Section;
import nl.vpro.domain.user.*;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;


public class PageUpdateTest {

    @Mock
    BroadcasterService broadcasterService;

    @Mock
    PortalService portalService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(broadcasterService.find("VPRO")).thenReturn(new Broadcaster("VPRO"));
        when(portalService.find("WETENSCHAP24")).thenReturn(new Portal("WETENSCHAP24","NPOWETENSCHAP"));

        ServiceLocator.setPortalService(portalService);
        ServiceLocator.setBroadcasterService(broadcasterService);
    }



    @Test
    public void validate() {
        PageUpdate pageUpdate = new PageUpdate(PageType.ARTICLE, "http://www.test.vpro.nl/123");
        pageUpdate.setBroadcasters(Collections.singletonList("VPRO"));
        pageUpdate.setTitle("main title");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertThat(validator.validate(pageUpdate)).isEmpty();
        assertThat(pageUpdate.getType()).isNotNull();
    }


    @Test
    public void validateMSE_2589() {
        PageUpdate pageUpdate = JAXB.unmarshal(getClass().getResourceAsStream("/MSE-2589.xml"), PageUpdate.class);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertThat(validator.validate(pageUpdate)).isEmpty();

    }

    @Test
    public void unmarshal() {
        String xml = "<page type=\"ARTICLE\" url=\"http://www.vpro.nl/article/123\"  xmlns=\"urn:vpro:pages:update:2013\">\n" +
            "  <crid>crid://bla/123</crid>\n" +
            "  <broadcaster>VPRO</broadcaster>\n" +
            "  <title>Hoi2</title>\n" +
            "</page>";
        PageUpdate update = JAXB.unmarshal(new StringReader(xml), PageUpdate.class);
        assertThat(update.getTitle()).isEqualTo("Hoi2");
    }

    @Test
    public void json() throws Exception {
        PageUpdate page = PageUpdateBuilder
            .article("http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html")
            .portal(PortalUpdate
                .builder()
                .id("3voor12")
                .url("http://3voor12-beta-test.vpro.nl/")
                .section(Section.builder().displayName("display").path("/bla").build()).build())
            .build();
        PageUpdate rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(page, "{\n" +
            "  \"type\" : \"ARTICLE\",\n" +
            "  \"url\" : \"http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html\",\n" +
            "  \"portal\" : {\n" +
            "    \"section\" : {\n" +
            "      \"path\" : \"/bla\",\n" +
            "      \"value\" : \"display\"\n" +
            "    },\n" +
            "    \"id\" : \"3voor12\",\n" +
            "    \"url\" : \"http://3voor12-beta-test.vpro.nl/\"\n" +
            "  },\n" +
            "  \"_id\" : \"http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html\"\n" +
            "}");

        assertThat(rounded.getPortal().getSection().getDisplayName()).isEqualTo("display");


    }

}
