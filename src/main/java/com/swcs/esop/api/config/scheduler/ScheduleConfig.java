package com.swcs.esop.api.config.scheduler;

import com.swcs.esop.api.entity.Trigger;
import com.swcs.esop.api.module.trigger.task.ScanTaskTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ScheduleConfig {

    public static Map<String, ScheduledFuture<?>> RUNNING_TRIGGERS = new ConcurrentHashMap<>();

    private int poolSize = 50;
    private int threadPriority = 5;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadPriority(threadPriority);
        scheduler.setThreadNamePrefix("taskScheduler-");
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.setDaemon(true);
        scheduler.initialize();
        return scheduler;
    }

    @Bean("scanTrigger")
    public Trigger scanTrigger() {
        Trigger trigger = new Trigger();
        trigger.setName("定时任务配置文件扫描任务");
        trigger.setCron("0/1 * * * * *");
        trigger.setJavaBean(ScanTaskTrigger.class.getName());
        trigger.setEnable(true);
        return trigger;
    }

}
