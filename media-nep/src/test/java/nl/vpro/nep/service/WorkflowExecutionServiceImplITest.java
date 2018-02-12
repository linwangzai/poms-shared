package nl.vpro.nep.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

import org.junit.Test;

import nl.vpro.nep.domain.workflow.WorkflowExecution;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class WorkflowExecutionServiceImplITest {


    @Test
    public void getStatuses() {
        WorkflowExecutionServiceImpl nepService = new WorkflowExecutionServiceImpl();
        nepService.init();
        Iterator<WorkflowExecution> statuses = nepService.getStatuses(null, null, 100L);

        while(statuses.hasNext()) {
            log.info("{}", statuses.next());
        }

    }


}
