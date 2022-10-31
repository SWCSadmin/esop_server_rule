package com.swcs.esop.api.config.async;

import com.swcs.esop.api.entity.Notification;
import com.swcs.esop.api.module.sender.SendFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
@EnableAsync
public class AsyncTask {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async("asyncExecutor")
    public Future<Boolean> notify(Notification notification) {
        try {
            return new AsyncResult<>(SendFactory.build(notification).send());
        } catch (Exception e) {
            logger.error("notify error", e);
            return new AsyncResult<>(false);
        }
    }


}