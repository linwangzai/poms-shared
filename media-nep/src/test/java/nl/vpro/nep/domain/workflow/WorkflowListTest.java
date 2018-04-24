package nl.vpro.nep.domain.workflow;

import java.io.IOException;

import org.junit.Test;

import nl.vpro.nep.service.impl.TranscodeServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public class WorkflowListTest {


    @Test
    public void unmarshall() throws IOException {
        WorkflowList list = TranscodeServiceImpl.MAPPER.readValue(getClass().getResourceAsStream("/example.json"), WorkflowList.class);
        assertThat(list.getWorkflowExecutions()).hasSize(20);
        assertThat(list.getTotalResults()).isEqualTo(26);
        assertThat(list.getNext().getHref()).isEqualTo("http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl/api/workflows?page=1&size=20");

    }
}
