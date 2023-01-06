package com.swcs.esop.api.util;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.config.AppProperties;
import com.swcs.esop.api.entity.*;
import com.swcs.esop.api.entity.auth.NodeVerifyTokenResponse;
import com.swcs.esop.api.entity.auth.User;
import com.swcs.esop.api.entity.auth.UserToken;
import com.swcs.esop.api.entity.db.IncentiveSchedule;
import com.swcs.esop.api.entity.db.PlanInfo;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 阮程
 * @date 2022/10/24
 */
public class NodeServiceUtil {

    public static final Logger logger = LoggerFactory.getLogger(NodeServiceUtil.class);

    private static final String URL_GET_LOGIN_TOKEN = "/get/logintoken/%s";
    private static final String URL_VERIFY_TOKEN = "/verify/logintoken";
    private static final String URL_GET_ALL_SCHEDULE_GROUPS = "/get/all_schedule_groups/%s";
    private static final String URL_GET_GROUP_SCHEDULE_BY_SCHEDULE_GROUP_ID = "/get/group_schedule/%s";
    private static final String URL_GET_ALL_KPI_CONDITIONS = "/get/all_kpi_conditions/%s";
    private static final String URL_GET_LOGININFO = "/get/logininfo/%s";
    private static final String URL_ADD_MULTIPLE_LOGININFO = "/add/multiple/logininfo/return/status";
    private static final String URL_GET_COMPANY_INFO = "/get/companyinfo";
    private static final String URL_GET_PARTICIPANT_INFO_BY_COMPANY = "/get/participantinfoByCompany/{company_id}";
    private static final String URL_GET_PLAN_INFO_BY_COMPANY = "/get/planinfoByCompany/%s";
    private static final String URL_GET_PLAN_INFO_BY_PLAN_ID = "/get/planinfo/%s";
    private static final String URL_GET_COMPANY_INFO_BY_COMPANY_ID = "/get/companyinfo/%s";
    private static final String URL_GET_INCENTIVE_MANAGEMENT = "/get/incentive_management?schedule_batch_id=%s&participant_id=%s";
    private static final String URL_GET_ALLSCHEDULES_BY_PLAN_ID = "/get/allschedules/%s";
    private static final String URL_GET_PARTICIPANTINFO_BY_PARTICIPANT_ID = "/get/participantinfo/%s";
    private static final String URL_GET_ALL_INCENTIVE_MANAGEMENT = "/get/all/incentive_management";
    private static final String URL_GET_ALL_INCENTIVE_MANAGEMENT_BY_INCENTIVE_STATUS = "/get/incentive_management/%s";
    private static final String URL_GET_ALL_KPI_STATUS = "/get/all_kpi_status/%s/%s";

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
        NodeApiMap participantInfoMap = new NodeApiMap(
                "/get/participantinfo/{participant_id}",
                null,
                "/add/participantinfo",
                "/add/multiple/participant_info/return/status",
                "/update/participantinfo/{participant_id}",
                "/update/multiple/participant_info",
                "/delete/participantinfo/{participant_id}"
        );
        CLASS_API_MAP.put(ParticipantInfoIndividual.class.getName(), participantInfoMap);
        CLASS_API_MAP.put(ParticipantInfoVendor.class.getName(), participantInfoMap);
        CLASS_API_MAP.put(KycParticipantInfo.class.getName(), participantInfoMap);
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
        // contactperson 接口
        NodeApiMap contactPersonMap = new NodeApiMap(
                "/get/contactperson/{entity_id}",
                null,
                "/add/contactperson",
                "/add/multiple/contactperson/return/status",
                "/update/contactperson/{contact_id}",
                "/update/multiple/contact_person/return/status",
                "/delete/contactperson/{entity_id}"
        );
        CLASS_API_MAP.put(ContactPersonIndividual.class.getName(), contactPersonMap);
        CLASS_API_MAP.put(ContactPersonVendor.class.getName(), contactPersonMap);
        // vendor_info 接口
        NodeApiMap vendorInfoMap = new NodeApiMap(
                "/get/vendorinfo/{vendor_id}",
                null,
                "/add/vendorinfo",
                "/add/multiple/vendor/return/status",
                "/update/vendorinfo/{vendor_id}",
                "/update/multiple/vendor",
                "/delete/vendorinfo/{vendor_id}"
        );
        CLASS_API_MAP.put(VendorInfo.class.getName(), vendorInfoMap);
        CLASS_API_MAP.put(KycVendorInfo.class.getName(), vendorInfoMap);

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

    public static <T> List<T> batchAddEntityReturnStatus(List<T> data) {
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

    public static <T> List<T> batchUpdateEntityReturnStatus(List<T> data) {
        if (data != null && data.size() > 0) {
            Class clz = data.get(0).getClass();
            String url = getRequestUrl(CLASS_API_MAP.get(clz.getName()).apiUpdateMultiple);
            return JSON.parseArray(HttpUtil.doPut(url, null, getStringEntity(data)), clz);
        }
        return new ArrayList<>();
    }

    public static <T> boolean deleteEntity(T data) {
        if (data != null) {
            Class clz = data.getClass();
            String url = getRequestUrl(varReplace(CLASS_API_MAP.get(clz.getName()).apiDelete, data));
            HttpUtil.doDelete(url);
        }
        return true;
    }

    public static List<ScheduleGroup> getAllScheduleGroups(String planId) {
        String url = getRequestUrl(varReplace(URL_GET_ALL_SCHEDULE_GROUPS, planId));
        return JSON.parseArray(HttpUtil.doGet(url), ScheduleGroup.class);
    }

    public static List<IncentiveSchedule> getAllSchedulesByPlanId(String planId) {
        String url = getRequestUrl(varReplace(URL_GET_ALLSCHEDULES_BY_PLAN_ID, planId));
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveSchedule.class);
    }

    public static List<KpiCondition> getAllKpiConditions(String schedule_batch_id) {
        String url = getRequestUrl(varReplace(URL_GET_ALL_KPI_CONDITIONS, schedule_batch_id));
        return JSON.parseArray(HttpUtil.doGet(url), KpiCondition.class);
    }

    public static List<LoginAccount> getLoginAccount(String login_id) {
        String url = getRequestUrl(varReplace(URL_GET_LOGININFO, login_id));
        return JSON.parseArray(HttpUtil.doGet(url), LoginAccount.class);
    }

    public static List<LoginAccount> batchAddLoginAccount(List<LoginAccount> data) {
        if (data != null) {
            Class clz = data.getClass();
            String url = getRequestUrl(varReplace(URL_ADD_MULTIPLE_LOGININFO, data));
            HttpUtil.doPost(url, null, getStringEntity(data));
        }
        return data;
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
        url = url.replace(" ", "%20").replace("\"", "%22").replace("{", "%7b").replace("}", "%7d");
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

    public static List<CompanyInfo> listCompanyInfo() {
        String url = getRequestUrl(varReplace(URL_GET_COMPANY_INFO));
        return JSON.parseArray(HttpUtil.doGet(url), CompanyInfo.class);
    }

    public static List<PlanInfo> listPlanInfoByCompanyId(String company_id) {
        String url = getRequestUrl(varReplace(URL_GET_PLAN_INFO_BY_COMPANY, company_id));
        return JSON.parseArray(HttpUtil.doGet(url), PlanInfo.class);
    }

    public static List<KycParticipantInfo> listKyc() {
        List<KycParticipantInfo> list = new ArrayList<>();
        for (CompanyInfo data : listCompanyInfo()) {
            String url = getRequestUrl(varReplace(URL_GET_PARTICIPANT_INFO_BY_COMPANY, data));
            list.addAll(JSON.parseArray(HttpUtil.doGet(url), KycParticipantInfo.class));
        }
        return list;
    }

    /**
     * schedule_group_id like schedule_batch_id
     *
     * @param schedule_group_id
     * @return
     */
    public static List<IncentiveSchedule> getIncentiveSchedule(String schedule_group_id) {
        String url = getRequestUrl(varReplace(URL_GET_GROUP_SCHEDULE_BY_SCHEDULE_GROUP_ID, schedule_group_id));
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveSchedule.class);
    }

    public static PlanInfo getPlanInfoByPlanId(String plan_id) {
        String url = getRequestUrl(varReplace(URL_GET_PLAN_INFO_BY_PLAN_ID, plan_id));
        List<PlanInfo> list = JSON.parseArray(HttpUtil.doGet(url), PlanInfo.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static CompanyInfo getCompanyInfoByCompanyId(String company_id) {
        String url = getRequestUrl(varReplace(URL_GET_COMPANY_INFO_BY_COMPANY_ID, company_id));
        List<CompanyInfo> list = JSON.parseArray(HttpUtil.doGet(url), CompanyInfo.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static ParticipantInfo getParticipantInfoByParticipantId(String participant_id) {
        String url = getRequestUrl(varReplace(URL_GET_PARTICIPANTINFO_BY_PARTICIPANT_ID, participant_id));
        List<ParticipantInfo> list = JSON.parseArray(HttpUtil.doGet(url), ParticipantInfo.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static IncentiveManagement getIncentiveManagementByScheduleBatchId(String schedule_batch_id, String participant_id) {
        String url = getRequestUrl(varReplace(URL_GET_INCENTIVE_MANAGEMENT, schedule_batch_id, participant_id));
        List<IncentiveManagement> list = JSON.parseArray(HttpUtil.doGet(url), IncentiveManagement.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static List<IncentiveManagement> listIncentiveManagement() {
        String url = getRequestUrl(URL_GET_ALL_INCENTIVE_MANAGEMENT);
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveManagement.class);
    }

    public static List<IncentiveManagement> listIncentiveManagementByIncentiveStatus(int incentive_status) {
        String url = getRequestUrl(varReplace(URL_GET_ALL_INCENTIVE_MANAGEMENT_BY_INCENTIVE_STATUS, incentive_status));
        return JSON.parseArray(HttpUtil.doGet(url), IncentiveManagement.class);
    }

    public static List<KpiStatusInfo> listKpiStatusInfo(String schedule_batch_id, String participant_id) {
        String url = getRequestUrl(varReplace(URL_GET_ALL_KPI_STATUS, schedule_batch_id, participant_id));
        return JSON.parseArray(HttpUtil.doGet(url), KpiStatusInfo.class);
    }
}
