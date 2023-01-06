package com.swcs.esop.api.module.trigger.task;

import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.config.AppProperties;
import com.swcs.esop.api.entity.Trigger;
import com.swcs.esop.api.module.trigger.TriggerService;
import com.swcs.esop.api.module.trigger.core.AbstractTaskTrigger;
import com.swcs.esop.api.util.AppUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 定时任务扫描类
 * <p>
 * 扫描指定路径文件下的配置文件是否更新
 */
public class ScanTaskTrigger extends AbstractTaskTrigger {
    private static final Logger logger = LoggerFactory.getLogger(ScanTaskTrigger.class);
    private static final String DEFAULT_FILE_PATH = "trigger.json"; // classpath:trigger.json
    private static String FILE_PATH;

    private static long LAST_MODIFIED = 0;
    private static boolean LOADED = false;

    public ScanTaskTrigger(ThreadPoolTaskScheduler scheduler, Trigger trigger) {
        super(scheduler, trigger);
    }

    {
        AppProperties appProperties = AppUtils.getBean(AppProperties.class);
        FILE_PATH = appProperties.getTriggerFilePath();
    }

    @Override
    public void runTask() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                long lastModified = file.lastModified();
                if (lastModified > LAST_MODIFIED) {
                    LAST_MODIFIED = lastModified;
                    register(new FileInputStream(file));
                }
            } else {
                if (!LOADED) {
                    register(AppUtils.getResourceAsStream(DEFAULT_FILE_PATH));
                    LOADED = true;
                }
            }
        } catch (IOException e) {
            logger.error("load notify template error", e);
        }
    }

    private void register(InputStream is) throws IOException {
        String template = IOUtils.toString(is, StandardCharsets.UTF_8);
        List<Trigger> list = JSON.parseArray(template, Trigger.class);
        TriggerService triggerService = AppUtils.getBean(TriggerService.class);
        for (Trigger trigger : list) {
            triggerService.register(trigger);
        }
    }

}
