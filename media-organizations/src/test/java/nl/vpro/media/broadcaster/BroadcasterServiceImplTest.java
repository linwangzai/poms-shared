package nl.vpro.media.broadcaster;

import org.junit.Test;
import nl.vpro.domain.user.BroadcasterService;

import static org.assertj.core.api.Assertions.assertThat;


public class BroadcasterServiceImplTest {


    BroadcasterService broadcasterService = new BroadcasterServiceImpl("classpath:/broadcasters.properties", false, true);

    @Test
    public void testFind() {
        assertThat(broadcasterService.find("VPRO").getDisplayName()).isEqualTo("VPRO");

    }

    @Test
    public void testFindAll() {
        assertThat(broadcasterService.findAll()).hasSize(63);
    }

    @Test
    public void testMisId() {
        BroadcasterService broadcasterService = new BroadcasterServiceImpl("https://poms.omroep.nl/broadcasters/", false, true);
        assertThat(broadcasterService.find("RTUT")).isNotNull();
        assertThat(broadcasterService.find("RTUT").getMisId()).isEqualTo("RTV Utrecht");

    }
}
