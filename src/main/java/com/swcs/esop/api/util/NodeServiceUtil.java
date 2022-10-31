package com.swcs.esop.api.util;

import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.config.AppProperties;
import com.swcs.esop.api.entity.ApiResult;
import com.swcs.esop.api.entity.NodeVerifyTokenResponse;
import com.swcs.esop.api.entity.User;
import com.swcs.esop.api.enums.Status;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Component
public class NodeServiceUtil {
    static String nodeServerAddr;

    static {
        AppProperties appProperties = AppUtils.getBean(AppProperties.class);
        nodeServerAddr = appProperties.getNodeServerAddr();
    }


    public static ApiResult getLoginToken(String loginId) {
        HttpGet http = new HttpGet(nodeServerAddr + "/get/logintoken/" + loginId);

        HttpClient httpClient = HttpClients.createDefault();

        try {
            HttpResponse response = httpClient.execute(http);
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return ApiResult.success().setData(resp);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
    }

    public static ApiResult verifyToken(User user) {
        HttpClient httpClient = HttpClients.createDefault();

        HttpPost http = new HttpPost(nodeServerAddr + "/verify/logintoken");
        http.setHeader("Content-Type", "application/json; charset=utf-8");
        http.setHeader("Authorization", "Bearer " + user.getToken());

        Map<String, Object> data = new HashMap<>();
        data.put("rinfo", user);
        http.setEntity(new StringEntity(JSON.toJSONString(data), "UTF-8"));
        try {
            HttpResponse response = httpClient.execute(http);
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return ApiResult.success(JSON.parseObject(resp, NodeVerifyTokenResponse.class));
            }
            return ApiResult.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, resp);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
    }
}
