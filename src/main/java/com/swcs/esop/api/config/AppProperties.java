package com.swcs.esop.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    /**
     * node 服务地址
     */
    private String nodeServerAddr;
    private boolean auth = true;


}
