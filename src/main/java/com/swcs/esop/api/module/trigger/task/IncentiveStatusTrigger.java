package com.swcs.esop.api.module.trigger.task;

import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.entity.*;
import com.swcs.esop.api.entity.db.IncentiveSchedule;
import com.swcs.esop.api.enums.ESOPState;
import com.swcs.esop.api.enums.IncentiveStatus;
import com.swcs.esop.api.enums.KpiStatus;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.module.trigger.core.AbstractTaskTrigger;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.NodeServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author 阮程
 * @date 2022/12/13
 */
public class IncentiveStatusTrigger extends AbstractTaskTrigger {

    private AsyncTask asyncTask;

    public IncentiveStatusTrigger(ThreadPoolTaskScheduler scheduler, Trigger trigger) {
        super(scheduler, trigger);
        asyncTask = AppUtils.getBean(AsyncTask.class);
    }

    @Override
    public void runTask() throws Exception {
        logInfo("start");

        List<IncentiveManagement> listGrantApproved​ = NodeServiceUtil.listIncentiveManagementByIncentiveStatus(IncentiveStatus.GrantApproved​.intValue());
        List<IncentiveManagement> listGrantAccepted​ = NodeServiceUtil.listIncentiveManagementByIncentiveStatus(IncentiveStatus.GrantAccepted​.intValue());
        List<IncentiveManagement> listGranted​ = NodeServiceUtil.listIncentiveManagementByIncentiveStatus(IncentiveStatus.Granted​.intValue());
        List<IncentiveManagement> listVestAccepted​ = NodeServiceUtil.listIncentiveManagementByIncentiveStatus(IncentiveStatus.VestAccepted​.intValue());

        logInfo("query result size for GrantApproved: " + listGrantApproved​.size());
        logInfo("query result size for GrantAccepted: " + listGrantAccepted​.size());
        logInfo("query result size for Granted: " + listGranted​.size());
        logInfo("query result size for VestAccepted: " + listVestAccepted​.size());

        // GrantApproved
        for (IncentiveManagement incentiveManagement : listGrantApproved​) {
            IncentiveManagement incentiveManagementDB = NodeServiceUtil.getIncentiveManagementByScheduleBatchId(
                    incentiveManagement.getSchedule_batch_id(), incentiveManagement.getParticipant_id());
            if (incentiveManagementDB != null) {
                notify(getNotify(incentiveManagement));
                incentiveManagementDB.setIncentive_status(Integer.toString(IncentiveStatus.GrantEmailOffered​.intValue()));
                NodeServiceUtil.updateEntity(incentiveManagementDB);
            }
        }

        // GrantAccepted
        for (IncentiveManagement incentiveManagement : listGrantAccepted​) {
            IncentiveManagement incentiveManagementDB = NodeServiceUtil.getIncentiveManagementByScheduleBatchId(
                    incentiveManagement.getSchedule_batch_id(), incentiveManagement.getParticipant_id());
            if (incentiveManagementDB != null) {
                boolean pass = isPass(incentiveManagement);
                if (pass) {
                    incentiveManagementDB.setIncentive_status(Integer.toString(IncentiveStatus.Granted​.intValue()));
                } else {
                    incentiveManagementDB.setIncentive_status(Integer.toString(IncentiveStatus.GrantFailedKPI​.intValue()));
                }
                NodeServiceUtil.updateEntity(incentiveManagementDB);
            }
        }

        // Granted​
        for (IncentiveManagement incentiveManagement : listGranted​) {
            IncentiveManagement incentiveManagementDB = NodeServiceUtil.getIncentiveManagementByScheduleBatchId(
                    incentiveManagement.getSchedule_batch_id(), incentiveManagement.getParticipant_id());
            if (incentiveManagementDB != null) {
                notify(getNotify(incentiveManagement));
                incentiveManagementDB.setIncentive_status(Integer.toString(IncentiveStatus.VestEmailOffered.intValue()));
                NodeServiceUtil.updateEntity(incentiveManagementDB);
            }
        }

        // VestAccepted
        for (IncentiveManagement incentiveManagement : listVestAccepted​) {
            IncentiveManagement incentiveManagementDB = NodeServiceUtil.getIncentiveManagementByScheduleBatchId(
                    incentiveManagement.getSchedule_batch_id(), incentiveManagement.getParticipant_id());
            if (incentiveManagementDB != null) {
                // Current Date>=Vested Date & KPI Status = Pass
                List<IncentiveSchedule> incentiveSchedules = NodeServiceUtil.getIncentiveSchedule(incentiveManagement.getSchedule_batch_id());
                if (incentiveSchedules == null || incentiveSchedules.isEmpty()) {
                    continue;
                }
                IncentiveSchedule incentiveSchedule = incentiveSchedules.get(0);
                boolean pass = isPass(incentiveManagement);
                if (pass && System.currentTimeMillis() >= incentiveSchedule.getVesting_date().getTime()) {
                    incentiveManagementDB.setIncentive_status(Integer.toString(IncentiveStatus.Vested​.intValue()));
                    NodeServiceUtil.updateEntity(incentiveManagementDB);
                }
            }
        }
        logInfo("success");
    }

    private boolean isPass(IncentiveManagement incentiveManagement) {
        boolean pass = true;
        for (KpiStatusInfo kpiStatusInfo : NodeServiceUtil.listKpiStatusInfo(incentiveManagement.getSchedule_batch_id(), incentiveManagement.getParticipant_id())) {
            String kpi_status = kpiStatusInfo.getKpi_status();
            if (StringUtils.isNotBlank(kpi_status)) {
                KpiStatus kpiStatus = KpiStatus.typeOf(Integer.valueOf(kpi_status));
                if (KpiStatus.Fail.equals(kpiStatus)) {
                    pass = false;
                    break;
                }
            }
        }
        return pass;
    }

    private Notification getNotify(IncentiveManagement incentiveManagement) throws IOException {
        IncentiveStatus incentiveStatus = IncentiveStatus.typeOf(Integer.valueOf(incentiveManagement.getIncentive_status()));
        ESOPState esopState = incentiveStatus.getESOPStae();
        String topicTitle = null;
        switch (esopState) {
            case Grant:
            case Vest:
            case Exercise:
                topicTitle = AppUtils.getResource("message/topic/" + esopState.toString().toLowerCase() + ".txt");
                break;
        }
        ParticipantInfo participantInfo = NodeServiceUtil.getParticipantInfoByParticipantId(incentiveManagement.getParticipant_id());
        if (participantInfo == null) {
            logError("participantInfo is not found: " + incentiveManagement.getParticipant_id());
            return null;
        }
        ApiResult apiResult = Notification.getMessageTemplate(incentiveManagement);
        if (apiResult.isSuccess()) {

            Notification notification = new Notification();
            notification.setTopicTitle(topicTitle);

            notification.setRecipientName(participantInfo.getPrefer_name_en());
            notification.setSenderContact(Constants.EMAIL_DEFAULT_SENDER_CONTACT);
            notification.setSenderJobPosition(Constants.EMAIL_DEFAULT_SENDER_JOB_POSITION);
            notification.setSenderName(Constants.EMAIL_DEFAULT_SENDER_NAME);
            notification.setMessage(apiResult.getData().toString());

            return notification;
        }
        return null;
    }

    private void notify(Notification notification) {
        try {
            notification.setMessage(notification.loadTemplate());
            Future<Boolean> future = asyncTask.notify(notification);
            boolean success = future.get();
            if (success) {
                logInfo("notify success");
            } else {
                logInfo("notify failed");
            }
        } catch (Exception e) {
            logError("notify error", e);
        }
    }
}
