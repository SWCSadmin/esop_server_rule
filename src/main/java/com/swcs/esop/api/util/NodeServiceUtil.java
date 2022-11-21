package com.swcs.esop.api.util;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.config.AppProperties;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.*;
import com.swcs.esop.api.entity.auth.NodeVerifyTokenResponse;
import com.swcs.esop.api.entity.auth.User;
import com.swcs.esop.api.entity.auth.UserToken;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 阮程
 * @date 2022/10/24
 */
@Component
public class NodeServiceUtil {

    public static final Logger logger = LoggerFactory.getLogger(NodeServiceUtil.class);

    private static final String URL_GET_LOGIN_TOKEN = "/get/logintoken/%s";
    private static final String URL_VERIFY_TOKEN = "/verify/logintoken";
    private static final String URL_GET_ALL_SCHEDULE_GROUPS = "/get/all_schedule_groups/%s";
    private static final String URL_GET_ALL_KPI_CONDITIONS = "/get/all_kpi_conditions/%s";

    private static final Pattern URL_PATTERN = Pattern.compile("\\{(.*?)\\}");

    private static final Map<String, NodeApiMap> CLASS_API_MAP = new HashMap<>();

    private static String nodeServerAddr;
    private static String loginId;
    private static String loginPwd;
    private static String token;

    static {
        // 激励管理接口
        CLASS_API_MAP.put(IncentiveManagement.class.getName(), new NodeApiMap(
                "/get/incentive_management?schedule_batch_id={schedule_batch_id}&participant_id={participant_id}",
                "/get/multiple/incentive_management?idsArray=%s",
                "/add/incentive_management",
                "/add/multiple/incentive_management/return/unsuccess",
                "/update/incentive_management/{schedule_batch_id}/{participant_id}",
                "/update/multiple/incentive_management",
                "/delete/incentive_management/{schedule_batch_id}/{participant_id}"
        ));
        // KPI 接口
        CLASS_API_MAP.put(KpiStatusInfo.class.getName(), new NodeApiMap(
                "/get/all_kpi_status/{schedule_batch_id}/{participant_id}",
                "/get/multiple/kpi_status?idsArray=%s",
                "/add/kpi_status",
                "/add/multiple/kpi_status/return/unsuccess",
                "/update/kpi_status/{schedule_batch_id}/{kpi_no}/{participant_id}",
                "/update/multiple/kpi_status",
                "/delete/kpi_status/{schedule_batch_id}/{kpi_no}/{participant_id}"
        ));
        // participantinfo 接口
        CLASS_API_MAP.put(ParticipantInfo.class.getName(), new NodeApiMap(
                "/get/participantinfo/{participant_id}",
                null,
                "/add/participantinfo",
                null,
                "/update/participantinfo/{participant_id}",
                null,
                "/delete/participantinfo/{participant_id}"
        ));
        // trust_transactions 接口
        CLASS_API_MAP.put(TrustTransactions.class.getName(), new NodeApiMap(
                "/get/trust_transactions?start_date={startDate}&end_date={endDate}&transaction_type={transaction_type}",
                "/get/multiple/trust_transactions?idsArray=%s",
                "/add/trust_transactions",
                "/add/multiple/trust_transactions/return/unsuccess",
                "/update/trust_transactions/{schedule_batch_id}/{kpi_no}/{participant_id}",
                "/update/multiple/trust_transactions",
                "/delete/trust_transactions/{schedule_batch_id}/{kpi_no}/{participant_id}"
        ));

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
        String url = getRequestUrl(varReplace(URL_GET_LOGIN_TOKEN, loginId));
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

    public static <T> List getEntity(T data) {
        if (data != null) {
            Class<?> clz = data.getClass();
            String url = getRequestUrl(varReplace(CLASS_API_MAP.get(clz.getName()).apiGet, data));
            return JSON.parseArray(HttpUtil.doGet(url), clz);
        }
        return new ArrayList();
    }

    public static <T> List listEntity(List<T> data) {
        if (data != null && data.size() > 0) {
            Class<?> clz = data.get(0).getClass();
            String url = getRequestUrl(varReplace(CLASS_API_MAP.get(clz.getName()).apiGetMultiple, data));
            return JSON.parseArray(HttpUtil.doGet(url), clz);
        }
        return new ArrayList();
    }

    public static <T> boolean addEntity(T data) {
        if (data != null) {
            Class clz = data.getClass();
            String url = getRequestUrl(CLASS_API_MAP.get(clz.getName()).apiAdd);
            HttpUtil.doPost(url, null, getStringEntity(data));
        }
        return true;
    }

    public static <T> List<T> batchAddEntity(List<T> data) {
        if (data != null && data.size() > 0) {
            Class clz = data.get(0).getClass();
            String url = getRequestUrl(CLASS_API_MAP.get(clz.getName()).apiAddMultiple);
            return JSON.parseArray(HttpUtil.doPost(url, null, getStringEntity(data)), clz);
        }
        return new ArrayList<>();
    }

    public static <T> boolean updateEntity(T data) {
        if (data != null) {
            Class clz = data.getClass();
            String url = getRequestUrl(varReplace(CLASS_API_MAP.get(clz.getName()).apiUpdate, data));
            HttpUtil.doPut(url, null, getStringEntity(data));
        }
        return true;
    }

    public static <T> boolean batchUpdateEntity(List<T> data) {
        if (data != null && data.size() > 0) {
            Class clz = data.get(0).getClass();
            String url = getRequestUrl(CLASS_API_MAP.get(clz.getName()).apiUpdateMultiple);
            HttpUtil.doPut(url, null, getStringEntity(data));
        }
        return true;
    }

    public static <T> boolean deleteEntity(T data) {
        if (data != null) {
            Class clz = data.getClass();
            String url = getRequestUrl(varReplace(CLASS_API_MAP.get(clz.getName()).apiDelete, data));
            HttpUtil.doDelete(url);
        }
        return true;
    }

    public static List<IncentiveSchedule> getAllScheduleGroups(String planId) {
        String url = getRequestUrl(varReplace(URL_GET_ALL_SCHEDULE_GROUPS, planId));
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveSchedule.class);
    }

    public static List<KpiCondition> getAllKpiConditions(String schedule_batch_id) {
        String url = getRequestUrl(varReplace(URL_GET_ALL_KPI_CONDITIONS, schedule_batch_id));
        return JSON.parseArray(HttpUtil.doGet(url), KpiCondition.class);
    }


    private static StringEntity getStringEntity(Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put("rinfo", data);
        return new StringEntity(JSON.toJSONString(body), ContentType.APPLICATION_JSON);
    }

    private static String varReplace(String str, Object... args) {
        if (str.contains("%s")) {
            str = String.format(str, args);
        } else {
            // url 中 变量替换
            Matcher m = URL_PATTERN.matcher(str);
            while (m.find()) {
                String fieldName = m.group(1);
                Object value = RefUtil.getFieldValue(args[0], fieldName);
                if (value == null) {
                    throw new RuntimeException(fieldName + " value is null");
                }
                str = str.replace("{" + fieldName + "}", value.toString());
            }
        }
        try {
            URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("url encode error", e);
        }
        return str;
    }

    /**
     * 获取请求 url
     *
     * @param suffix 接口路径
     * @return
     */
    private static String getRequestUrl(String suffix) {
        String url = nodeServerAddr + suffix;
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
