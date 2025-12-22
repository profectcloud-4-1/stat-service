package profect.goorm1.goormdotcom.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchChunkListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("Start a transaction");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("Finish a transaction after commiting a chunk");
        try {
            int second = 2;
            log.info("휴, 잠시만 좀 쉬겠습니다... {}초만", second);
            Thread.sleep(second*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
