package com.swcs.esop.api.module.trigger.task;

import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.entity.KycParticipantInfo;
import com.swcs.esop.api.entity.Notification;
import com.swcs.esop.api.entity.Trigger;
import com.swcs.esop.api.enums.CommunicationTypeEnum;
import com.swcs.esop.api.enums.KycStatus;
import com.swcs.esop.api.enums.RiskRating;
import com.swcs.esop.api.module.trigger.core.AbstractTaskTrigger;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.NodeServiceUtil;
import com.swcs.esop.api.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.rmi.ConnectIOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author 阮程
 * @date 2022/12/13
 */
public class KycTrigger extends AbstractTaskTrigger {

    public static final String TEMPLATE_PATH = "notify_template/kyc.txt";
    public static final String DATA_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String YYYYMMDD = "yyyyMMdd";

    public KycTrigger(ThreadPoolTaskScheduler scheduler, Trigger trigger) {
        super(scheduler, trigger);
    }

    /**
     * Daily schedule based on a configuration file with these input parameters
     * - email  KYC recipients
     * - time to start daily process (24 hours format e.g. 13:30)
     * - email template
     * After calling processing rule. Then Call Alert Notification to send email to KYC recipients"
     * <p>
     * 1.调用接口取数
     * 2.调用 KycInput 计算
     * 3.发邮件
     */
    @Override
    public void runTask() throws Exception {
        String email = getProp("email", "swcs_esop@outlook.com");

        logInfo("start");
        /**
         * 1.缺少邮件模板
         */
        Date now = new Date();
        String today = DateFormatUtils.format(now, YYYYMMDD);

        List<KycParticipantInfo> list = NodeServiceUtil.listKyc();
        logInfo("kyc query result size: " + list.size());
        for (KycParticipantInfo kyc : list) {
            String participant_id = kyc.getParticipant_id();
            try {
                KycStatus kycStatus;
                String kyc_status = kyc.getKyc_status();
                if (StringUtils.isNotBlank(kyc_status)) {
                    kycStatus = KycStatus.typeOf(Integer.valueOf(kyc_status));
                } else {
                    logError("participant_id: " + participant_id + " missing required params: kyc_status");
                    continue;
                }
                RiskRating riskRating;
                String kyc_risk_level = kyc.getKyc_risk_level();
                if (StringUtils.isNotBlank(kyc_risk_level)) {
                    riskRating = RiskRating.typeOf(Integer.valueOf(kyc_risk_level));
                } else {
                    logError("participant_id: " + participant_id + " missing required params: kyc_risk_level");
                    continue;
                }
                if (kycStatus == null) {
                    logError("participant_id: " + participant_id + " missing required params: kyc_status");
                    continue;
                }
                if (riskRating == null) {
                    logError("participant_id: " + participant_id + " missing required params: kyc_risk_level");
                    continue;
                }

                String nextKycReviewDate;
                String next_kyc_review_date = kyc.getNext_kyc_review_date();
                if (StringUtils.isBlank(next_kyc_review_date)) {
                    nextKycReviewDate = today;
                } else {
                    nextKycReviewDate = DateFormatUtils.format(DateUtils.parseDate(next_kyc_review_date, DATA_FORMAT_PATTERN), YYYYMMDD);
                }
                int reminder = 1;
                String reminder_count = kyc.getReminder_count();
                if (StringUtils.isNotBlank(reminder_count)) {
                    reminder = Integer.valueOf(reminder_count);
                }

                if (today.equals(nextKycReviewDate)) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());

                    switch (kycStatus) {
                        case PendingList​:
                            reminder++;
                            calendar.add(Calendar.DATE, 10);
                            break;
                        case Approved​:
                            reminder++;
                            switch (riskRating) {
                                case High​:
                                    calendar.add(Calendar.DATE, 270);
                                    break;
                                case Medium​:
                                    calendar.add(Calendar.DATE, 360);
                                    break;
                                case Low:
                                    calendar.add(Calendar.DATE, 540);
                                    break;
                            }
                            break;
                        case Rejected​:
                            continue;
                        case ConditionalApproval​:
                            if (reminder < 4) {
                                reminder++;
                            } else {
                                reminder = 1;
                                kycStatus = KycStatus.Suspended;
                            }
                            calendar.add(Calendar.DATE, 90);
                            break;
                        case Suspended:
                            if (reminder < 4) {
                                reminder++;
                                calendar.add(Calendar.DATE, 90);
                            } else {
                                reminder = 1;
                                kycStatus = KycStatus.Rejected​;
                                calendar.setTime(new Date());
                            }
                            break;
                    }
                    kyc.setKyc_status(Integer.toString(kycStatus.intValue()));
                    kyc.setKyc_status(Integer.toString(reminder));
                    kyc.setNext_kyc_review_date(DateFormatUtils.format(calendar.getTime(), DATA_FORMAT_PATTERN));
                    notify(email, kyc);
                    NodeServiceUtil.updateEntity(kyc);
                }
            } catch (Exception e) {
                logError("participant_id: " + participant_id + " call error: " + e.getMessage(), e);
            }
        }
        logInfo("success");
    }

    private void notify(String recipientId, KycParticipantInfo kyc) {
        AsyncTask asyncTask = AppUtils.getBean(AsyncTask.class);
        Notification notification = new Notification();
        notification.setCommunicationType(CommunicationTypeEnum.Email);
        notification.setRecipientID(recipientId);
        notification.setTopicTitle("Kyc");

        String template = notification.loadTemplate(TEMPLATE_PATH);
        template = StringUtil.replaceVar(template,"reminder", kyc.getReminder_count());
        template = StringUtil.replaceVar(template,"kycStatus", kyc.getKyc_risk_level());
        template = StringUtil.replaceVar(template,"nextKycReviewDate", kyc.getNext_kyc_review_date());
        template = StringUtil.replaceVar(template,"prefer_name_en", kyc.getPrefer_name_en());
        notification.setMessage(template);

        Future<Boolean> future = asyncTask.notify(notification);
        boolean success = false;
        try {
            success = future.get();
        } catch (Exception e) {
            logger.error("", e);
        }
        if (success) {
            logInfo("kyc send email done.");
        } else {
            logInfo("kyc send email error");
        }
    }
}
