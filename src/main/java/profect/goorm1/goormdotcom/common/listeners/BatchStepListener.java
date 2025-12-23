package profect.goorm1.goormdotcom.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchStepListener implements StepExecutionListener {

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Read Count: {}", stepExecution.getReadCount());
        log.info("Write Count: {}", stepExecution.getWriteCount());
        log.info("Commit Count: {}", stepExecution.getCommitCount());
        return null;
    }
}
