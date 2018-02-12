package nl.vpro.nep.domain.workflow;

import java.io.IOException;

import org.junit.Test;

import nl.vpro.nep.service.WorkflowExecutionServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public class WorkflowListTest {


    @Test
    public void unmarshall() throws IOException {
        WorkflowList list = WorkflowExecutionServiceImpl.MAPPER.readValue(getClass().getResourceAsStream("/example.json"), WorkflowList.class);
        assertThat(list.getWorkflowExecutions()).hasSize(20);
    }
}
