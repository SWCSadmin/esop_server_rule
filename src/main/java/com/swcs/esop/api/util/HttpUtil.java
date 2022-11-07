package com.swcs.esop.api.util;

import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.common.exception.NodeServerException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class HttpUtil {

    public static List<Integer> STATUS_SUCCESS_List = Arrays.asList(
            HttpStatus.SC_OK, HttpStatus.SC_CREATED, HttpStatus.SC_NO_CONTENT
    );

    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static String doGet(String url, Map<String, String> headers) {
        HttpGet http = new HttpGet(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(http::setHeader);
        }
        return doHttp(http);
    }

    public static String doPost(String url, Map<String, String> headers, HttpEntity httpEntity) {
        HttpPost http = new HttpPost(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(http::setHeader);
        }
        if (httpEntity != null) {
            http.setEntity(httpEntity);
            http.setHeader("Content-type", "application/json;charset=UTF-8");
        }
        return doHttp(http);
    }

    public static String doPut(String url, Map<String, String> headers, HttpEntity httpEntity) {
        HttpPut http = new HttpPut(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(http::setHeader);
        }
        if (httpEntity != null) {
            http.setEntity(httpEntity);
            http.setHeader("Content-type", "application/json;charset=UTF-8");
        }
        return doHttp(http);
    }

    public static String doDelete(String url) {
        return doDelete(url, null);
    }

    public static String doDelete(String url, Map<String, String> headers) {
        HttpDelete http = new HttpDelete(url);
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(http::setHeader);
        }
        return doHttp(http);
    }

    private static String doHttp(HttpRequestBase httpRequest) {
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            String resp = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            if (STATUS_SUCCESS_List.contains(response.getStatusLine().getStatusCode())) {
                return resp;
            }
            throw new NodeServerException(resp);
        } catch (IOException e) {
            throw new NodeServerException(e);
        }
    }
}
