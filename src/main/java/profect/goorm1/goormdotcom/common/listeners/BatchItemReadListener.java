package profect.goorm1.goormdotcom.common.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchItemReadListener<T> implements ItemReadListener<T> {
    private int itemCount = 0;

    @Override
    public void beforeRead() {
        log.info("Start to read an item");
    }

    @Override
    public void afterRead(T t) {
        itemCount+=1;
        log.info("Finished read an item. count = {}", itemCount);
    }

    @Override
    public void onReadError(Exception e) {
        log.error(e.getMessage());
    }
}
