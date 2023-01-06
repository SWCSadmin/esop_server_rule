package com.swcs.esop.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author 阮程
 * @date 2022/10/24
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    /**
     * node 服务地址
     */
    private String nodeServerAddr;
    private boolean auth = true;
    private String loginId;
    private String loginPwd;
    /**
     * 文件上传路径
     */
    private String uploadFilePath;
    /**
     * 一次性文件路径
     */
    private String onceFilePath;
    /**
     * 定时任务配置文件路径
     */
    private String triggerFilePath;

}
