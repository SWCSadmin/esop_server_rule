package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.db.IncentiveSchedule;
import com.swcs.esop.api.entity.db.PlanInfo;
import com.swcs.esop.api.enums.CommunicationTypeEnum;
import com.swcs.esop.api.enums.ESOPState;
import com.swcs.esop.api.enums.IncentiveStatus;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.NodeServiceUtil;
import com.swcs.esop.api.util.StringUtil;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ClassRelativeResourceLoader;
import sun.management.MemoryUsageCompositeData;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @author 阮程
 * @date 2022/10/26
 */
@Data
public class Notification {

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    /**
     * Possible method supported: Email or SMS or Wechat or Whatsapp or Frontend UI
     */
    private CommunicationTypeEnum communicationType;
    /**
     * ID is based on the communication type. Can be email, phone number, wechat ID, etc.
     */
    private String recipientID;
    /**
     * Recipient first and last names in English or Chinese
     */
    private String recipientName;
    /**
     * Subject header of the message in English or Chinese
     */
    private String topicTitle;
    /**
     * Message in English or Chinese
     */
    private String message = "";
    /**
     * Sender first and last names
     */
    private String senderName;
    /**
     * Sender email or phone number
     */
    private String senderContact;
    /**
     * Sender Job position in the company
     */
    private String senderJobPosition;

    public String loadTemplate() {
        String filePath = "notify_template" + File.separator + communicationType.toString().toLowerCase() + ".txt";
        return loadTemplate(filePath);
    }

    public String loadTemplate(String filePath) {
        try {
            String template = AppUtils.getResource(filePath);
            template = StringUtil.replaceVar(template, "recipientID", recipientID);
            template = StringUtil.replaceVar(template, "recipientName", recipientName);
            template = StringUtil.replaceVar(template, "topicTitle", topicTitle);
            template = StringUtil.replaceVar(template, "message", message);
            template = StringUtil.replaceVar(template, "senderName", senderName);
            template = StringUtil.replaceVar(template, "senderContact", senderContact);
            template = StringUtil.replaceVar(template, "senderJobPosition", senderJobPosition);
            return template;
        } catch (IOException e) {
            logger.error("load notify template error", e);
        }
        return message;
    }

    public static ApiResult getMessageTemplate(String scheduler_batch_id, String participant_id) {
        IncentiveManagement incentiveManagement = new IncentiveManagement();
        incentiveManagement.setSchedule_batch_id(scheduler_batch_id);
        incentiveManagement.setParticipant_id(participant_id);
        List<IncentiveManagement> incentiveManagementList = NodeServiceUtil.getEntity(incentiveManagement);
        if (incentiveManagementList.size() > 0) {
            return getMessageTemplate(incentiveManagementList.get(0));
        }
        return ApiResult.error(Status.RECORD_NOT_EXIST_ERROR);
    }

    public static ApiResult getMessageTemplate(IncentiveManagement incentiveManagement) {
        String incentive_status = incentiveManagement.getIncentive_status();
        if (StringUtils.isNotBlank(incentive_status)) {
            IncentiveStatus incentiveStatus = IncentiveStatus.typeOf(Integer.valueOf(incentive_status));
            if (incentiveStatus != null) {
                ESOPState esopState = incentiveStatus.getESOPStae();
                String message = null;
                try {
                    if (esopState != null) {
                        switch (esopState) {
                            case Grant:
                            case Vest:
                            case Exercise:
                                message = AppUtils.getResource("message/email/" + esopState.toString().toLowerCase() + ".txt");
                                break;
                        }

                        if (message != null) {
                            List<IncentiveSchedule> incentiveSchedules = NodeServiceUtil.getIncentiveSchedule(incentiveManagement.getSchedule_batch_id());
                            if (incentiveSchedules == null || incentiveSchedules.isEmpty()) {
                                return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "incentive_schedule not found");
                            }
                            IncentiveSchedule incentiveSchedule = incentiveSchedules.get(0);

                            PlanInfo planInfo = NodeServiceUtil.getPlanInfoByPlanId(incentiveSchedule.getPlan_id());
                            if (planInfo == null) {
                                return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "plan_info not found");
                            }

                            CompanyInfo companyInfo = NodeServiceUtil.getCompanyInfoByCompanyId(planInfo.getCompany_id());
                            if (companyInfo == null) {
                                return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "company_info not found");
                            }

                            message = StringUtil.replaceVar(message, "plan_id", incentiveSchedule.getPlan_id());
                            message = StringUtil.replaceVar(message, "plan_type", planInfo.getPlan_type());
                            message = StringUtil.replaceVar(message, "plan_name", planInfo.getPlan_name_en());
                            message = StringUtil.replaceVar(message, "plan_start_date", DateFormatUtils.format(planInfo.getStart_date(), Constants.DATE_FORMAT_YYYY_MM_DD));
                            message = StringUtil.replaceVar(message, "listed_company", companyInfo.getCompany_name_en());
                            switch (esopState) {
                                case Grant:
                                    if (incentiveSchedule.getOffer_date() == null) {
                                        return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "offer_date is null");
                                    }
                                    message = StringUtil.replaceVar(message, "granted_date", DateFormatUtils.format(incentiveSchedule.getOffer_date(), Constants.DATE_FORMAT_YYYY_MM_DD));
                                case Vest:
                                    if (incentiveSchedule.getVesting_date() == null) {
                                        return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "vesting_date is null");
                                    }
                                    message = StringUtil.replaceVar(message, "vested_date", DateFormatUtils.format(incentiveSchedule.getVesting_date(), Constants.DATE_FORMAT_YYYY_MM_DD));
                                case Exercise:
                                    if (incentiveSchedule.getExercise_start_date() == null) {
                                        return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "exercise_start_date is null");
                                    }
                                    message = StringUtil.replaceVar(message, "exercised_date", DateFormatUtils.format(incentiveSchedule.getExercise_start_date(), Constants.DATE_FORMAT_YYYY_MM_DD));
                                    break;
                            }
                            return ApiResult.success().setData(message);
                        } else {
                            return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "current incentive_status has not message template");
                        }
                    } else {
                        return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "current incentive_status has not message template");
                    }
                } catch (IOException e) {
                    return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, e.getMessage());
                }
            } else {
                return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "current incentive_status has not message template");
            }
        } else {
            return ApiResult.errorWithArgs(Status.GET_NOTIFY_MESSAGE_ERROR, "incentive_status is empty");
        }
    }

}
