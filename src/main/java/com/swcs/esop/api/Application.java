package com.swcs.esop.api;

import com.swcs.esop.api.module.trigger.TriggerService;
import com.swcs.esop.api.util.AppUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 阮程
 * @date 2022/10/20
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        init();
    }

    /**
     * 启动完成之后执行一系列初始化操作
     */
    private static void init() {
        initTrigger();
    }

    /**
     * 初始化定时任务
     */
    private static void initTrigger() {
        TriggerService triggerService = AppUtils.getBean(TriggerService.class);
        triggerService.initTriggers();
    }
}
