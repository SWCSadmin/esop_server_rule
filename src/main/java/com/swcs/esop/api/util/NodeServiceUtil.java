package com.swcs.esop.api.util;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.common.exception.NodeServerException;
import com.swcs.esop.api.config.AppProperties;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.IncentiveManagement;
import com.swcs.esop.api.entity.KpiStatusInfo;
import com.swcs.esop.api.entity.auth.NodeVerifyTokenResponse;
import com.swcs.esop.api.entity.auth.User;
import com.swcs.esop.api.entity.auth.UserToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Component
public class NodeServiceUtil {

    public static final Logger logger = LoggerFactory.getLogger(NodeServiceUtil.class);

    private static final String URL_GET_LOGIN_TOKEN = "/get/logintoken/%s";
    private static final String URL_VERIFY_TOKEN = "/verify/logintoken";

    // 激励管理接口
    private static final String URL_GET_INCENTIVE_MANAGEMENT = "/get/incentive_management?schedule_batch_id=%s&participant_id=%s";
    private static final String URL_GET_MULTIPLE_INCENTIVE_MANAGEMENT = "/get/multiple/incentive_management?idsArray=%s";
    private static final String URL_ADD_INCENTIVE_MANAGEMENT = "/add/incentive_management";
    private static final String URL_ADD_MULTIPLE_INCENTIVE_MANAGEMENT = "/add/multiple/incentive_management";
    private static final String URL_UPDATE_INCENTIVE_MANAGEMENT = "/update/incentive_management/%s/%s";
    private static final String URL_UPDATE_MULTIPLE_INCENTIVE_MANAGEMENT = "/update/multiple/incentive_management";
    private static final String URL_DELETE_INCENTIVE_MANAGEMENT = "/delete/incentive_management/%s/%s";

    // KPI 接口
    private static final String URL_GET_ALL_KPI_STATUS = "/get/all_kpi_status/%s/%s";
    private static final String URL_GET_MULTIPLE_KPI_STATUS = "/get/multiple/kpi_status?idsArray=%s";
    private static final String URL_ADD_KPI_STATUS = "/add/kpi_status";
    private static final String URL_ADD_MULTIPLE_KPI_STATUS = "/add/multiple/kpi_status";
    private static final String URL_UPDATE_KPI_STATUS = "/update/kpi_status/%s/%s/%s";
    private static final String URL_UPDATE_MULTIPLE_KPI_STATUS = "/update/multiple/kpi_status";
    private static final String URL_DELETE_KPI_STATUS = "/delete/kpi_status/%s/%s/%s";

    private static String nodeServerAddr;
    private static String loginId;
    private static String loginPwd;
    private static String token;

    static {
        AppProperties appProperties = AppUtils.getBean(AppProperties.class);
        nodeServerAddr = appProperties.getNodeServerAddr();
        loginId = appProperties.getLoginId();
        loginPwd = appProperties.getLoginPwd();
//        autoLogin();  // 暂时不需要
    }


    /**
     * 获取登录 token
     *
     * @param loginId
     * @return
     */
    public static ApiResult<UserToken> getLoginToken(String loginId) {
        String url = getRequestUrl(URL_GET_LOGIN_TOKEN, loginId);
        UserToken userToken = JSON.parseObject(HttpUtil.doGet(url), UserToken.class);
        return ApiResult.success(userToken);
    }

    /**
     * 验证 token
     *
     * @param user
     * @return
     */
    public static ApiResult verifyToken(User user) {
        String url = getRequestUrl(URL_VERIFY_TOKEN);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", "Bearer " + user.getToken());

        NodeVerifyTokenResponse response = JSON.parseObject(HttpUtil.doPost(url, headers, getStringEntity(user)), NodeVerifyTokenResponse.class);
        return ApiResult.success(response);
    }

    /**
     * 获取激励管理信息
     *
     * @param data
     * @return
     */
    public static List<IncentiveManagement> getIncentiveManagement(IncentiveManagement data) {
        String url = getRequestUrl(URL_GET_INCENTIVE_MANAGEMENT, data.getSchedule_batch_id(), data.getParticipant_id());
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveManagement.class);
    }

    /**
     * 获取激励管理信息
     *
     * @param data
     * @return
     */
    public static List<IncentiveManagement> listIncentiveManagement(List<IncentiveManagement> data) {
        String url = getRequestUrl(URL_GET_MULTIPLE_INCENTIVE_MANAGEMENT, JSON.toJSONString(data));
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveManagement.class);
    }

    /**
     * 添加激励管理信息
     *
     * @param data
     * @return
     */
    public static boolean addIncentiveManagement(IncentiveManagement data) {
        String url = getRequestUrl(URL_ADD_INCENTIVE_MANAGEMENT);
        HttpUtil.doPost(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 批量添加激励管理信息
     *
     * @param data
     * @return
     */
    public static boolean batchAddIncentiveManagement(List<IncentiveManagement> data) {
        String url = getRequestUrl(URL_ADD_MULTIPLE_INCENTIVE_MANAGEMENT);
        HttpUtil.doPost(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 更新激励管理
     *
     * @param data
     * @return
     */
    public static boolean updateIncentiveManagement(IncentiveManagement data) {
        String url = getRequestUrl(URL_UPDATE_INCENTIVE_MANAGEMENT, data.getSchedule_batch_id(), data.getParticipant_id());
        HttpUtil.doPut(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 批量更新激励管理
     *
     * @param data
     * @return
     */
    public static boolean batchUpdateIncentiveManagement(List<IncentiveManagement> data) {
        String url = getRequestUrl(URL_UPDATE_MULTIPLE_INCENTIVE_MANAGEMENT);
        HttpUtil.doPut(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 删除激励管理
     *
     * @param data
     * @return
     */
    public static boolean deleteIncentiveManagement(IncentiveManagement data) {
        String url = getRequestUrl(URL_DELETE_INCENTIVE_MANAGEMENT, data.getSchedule_batch_id(), data.getParticipant_id());
        HttpUtil.doDelete(url);
        return true;
    }



    /**
     * 获取 KPI 状态
     *
     * @param data
     * @return
     */
    public static List<KpiStatusInfo> getAllKpiStatusInfo(KpiStatusInfo data) {
        String url = getRequestUrl(URL_GET_ALL_KPI_STATUS, data.getSchedule_batch_id(), data.getParticipant_id());
        return JSON.parseArray(HttpUtil.doGet(url), KpiStatusInfo.class);
    }

    public static List<KpiStatusInfo> getKpiStatusInfo(KpiStatusInfo data) {
        String url = getRequestUrl(URL_GET_MULTIPLE_KPI_STATUS, JSON.toJSONString(Collections.singletonList(data)));
        return JSON.parseArray(HttpUtil.doGet(url), KpiStatusInfo.class);
    }

    /**
     * 获取激励管理信息
     *
     * @param data
     * @return
     */
    public static List<KpiStatusInfo> listKpiStatusInfo(List<KpiStatusInfo> data) {
        String url = getRequestUrl(URL_GET_MULTIPLE_KPI_STATUS, JSON.toJSONString(data));
        return JSON.parseArray(HttpUtil.doGet(url), KpiStatusInfo.class);
    }

    /**
     * 添加激励管理信息
     *
     * @param data
     * @return
     */
    public static boolean addKpiStatusInfo(KpiStatusInfo data) {
        String url = getRequestUrl(URL_ADD_KPI_STATUS);
        HttpUtil.doPost(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 批量添加激励管理信息
     *
     * @param data
     * @return
     */
    public static boolean batchAddKpiStatusInfo(List<KpiStatusInfo> data) {
        String url = getRequestUrl(URL_ADD_MULTIPLE_KPI_STATUS);
        HttpUtil.doPost(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 更新激励管理
     *
     * @param data
     * @return
     */
    public static boolean updateKpiStatusInfo(KpiStatusInfo data) {
        String url = getRequestUrl(URL_UPDATE_KPI_STATUS, data.getSchedule_batch_id(), data.getKpi_no(), data.getParticipant_id());
        HttpUtil.doPut(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 批量更新激励管理
     *
     * @param data
     * @return
     */
    public static boolean batchUpdateKpiStatusInfo(List<KpiStatusInfo> data) {
        String url = getRequestUrl(URL_UPDATE_MULTIPLE_KPI_STATUS);
        HttpUtil.doPut(url, null, getStringEntity(data));
        return true;
    }

    /**
     * 删除激励管理
     *
     * @param data
     * @return
     */
    public static boolean deleteKpiStatusInfo(KpiStatusInfo data) {
        String url = getRequestUrl(URL_DELETE_KPI_STATUS, data.getSchedule_batch_id(), data.getKpi_no(), data.getParticipant_id());
        HttpUtil.doDelete(url);
        return true;
    }



    private static StringEntity getStringEntity(Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("rinfo", data);
        return new StringEntity(JSON.toJSONString(body), ContentType.APPLICATION_JSON);
    }

    /**
     * 获取请求 url
     *
     * @param suffix 接口路径
     * @return
     */
    private static String getRequestUrl(String suffix, Object... args) {
        String url = nodeServerAddr + suffix;
        if (args.length > 0) {
            url = String.format(url, args);
        }
        url = url.replace("\"", "%22").replace("{", "%7b").replace("}", "%7d");
        logger.info("request node server: " + url);
        return url;
    }

    private static void autoLogin() {
        if (StringUtils.isBlank(token)) {
            UserToken userToken = (UserToken) getLoginToken(loginId).getData();
            User user = new User();
            user.setLogin_id(loginId);
            user.setLogin_pwd(loginPwd);
            user.setToken(userToken);
            if (verifyToken(user).isSuccess()) {
                token = userToken.getToken();
            }
        }
    }
}
