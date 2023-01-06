package com.swcs.esop.api.module.trigger.core;

import java.util.concurrent.ScheduledFuture;

public interface TaskTrigger extends Runnable {

    void runTask() throws Exception;

    void setFuture(ScheduledFuture<?> future);

}
