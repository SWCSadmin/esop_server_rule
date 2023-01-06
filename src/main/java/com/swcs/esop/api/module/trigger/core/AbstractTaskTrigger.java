package com.swcs.esop.api.module.trigger.core;

import com.swcs.esop.api.entity.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledFuture;

public abstract class AbstractTaskTrigger implements TaskTrigger {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractTaskTrigger.class);

    protected ThreadPoolTaskScheduler scheduler;
    protected Trigger trigger;
    protected ScheduledFuture<?> future;

    public AbstractTaskTrigger(ThreadPoolTaskScheduler scheduler, Trigger trigger) {
        this.scheduler = scheduler;
        this.trigger = trigger;
    }

    @Override
    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    @Override
    public final void run() {
        try {
            runTask();
        } catch (Exception e) {
            logger.error("定时任务[" + trigger.getName() + "] 执行异常", e);
            cancel();
        }
    }

    protected void cancel() {
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }

    protected String getProp(String key) {
        return getProp(key, null);
    }

    protected String getProp(String key, String def) {
        return this.trigger.getProps().getProperty(key, def);
    }

    protected void logInfo(String msg) {
        logger.info("[{}] {}", this.trigger.getName(), msg);
    }

    protected void logError(String msg) {
        logger.error("[{}] {}", this.trigger.getName(), msg);
    }

    protected void logError(String msg, Exception e) {
        logger.error("[{}] {}", this.trigger.getName(), msg, e);
    }
}
