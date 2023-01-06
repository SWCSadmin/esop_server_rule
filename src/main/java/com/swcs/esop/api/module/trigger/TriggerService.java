package com.swcs.esop.api.module.trigger;

import com.swcs.esop.api.config.scheduler.ScheduleConfig;
import com.swcs.esop.api.entity.Trigger;
import com.swcs.esop.api.module.trigger.core.TaskTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author 阮程
 * @date 2022/12/13
 */
@Service
public class TriggerService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    @Resource(name = "scanTrigger")
    private Trigger scanTrigger;

    public void initTriggers() {
        register(scanTrigger);
    }

    public void register(Trigger trigger) {
        destroy(trigger);
        if (trigger.isEnable()) {
            try {
                Class<?> clazz = Class.forName(trigger.getJavaBean());
                Constructor<?> construct = clazz.getConstructor(ThreadPoolTaskScheduler.class, Trigger.class);
                TaskTrigger task = (TaskTrigger) construct.newInstance(scheduler, trigger);
                startTask(trigger, task);
                logger.info("register task[{}] success", trigger.getName());
            } catch (Exception e) {
                logger.error("register task[{}] error: ", trigger.getName(), e);
            }
        }
    }

    public void destroy(Trigger trigger) {
        ScheduledFuture<?> future = ScheduleConfig.RUNNING_TRIGGERS.get(trigger.getName());
        if (future != null) {
            if (!future.isCancelled()) {
                future.cancel(true);
            }
            logger.info("destroy trigger: " + trigger.toString());
        }
    }

    private void startTask(Trigger trigger, TaskTrigger task) {
        String cron = trigger.getCron();
        ScheduledFuture<?> future;
        if (cron != null) {
            CronTrigger cronTrigger = new CronTrigger(cron);
            future = scheduler.schedule(task, cronTrigger);
            task.setFuture(future);
        } else {
            future = scheduler.schedule(task, new Date());
            task.setFuture(future);
        }
        if (trigger.getName() != null) {
            ScheduleConfig.RUNNING_TRIGGERS.put(trigger.getName(), future);
        }
    }


}
