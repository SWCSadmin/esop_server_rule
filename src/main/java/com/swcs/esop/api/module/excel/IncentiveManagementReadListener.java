package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.entity.IncentiveManagement;
import com.swcs.esop.api.entity.IncentiveSchedule;
import com.swcs.esop.api.entity.ParticipantInfo;
import com.swcs.esop.api.util.NodeServiceUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量新增
 *
 * @author 阮程
 * @date 2022/11/1
 */
public class IncentiveManagementReadListener extends BaseReadListener<IncentiveManagement> {

    private String planId;
    private List<String> scheduleBatchIds = new ArrayList<>();

    public IncentiveManagementReadListener(boolean upsert, String planId) {
        super(upsert);
        this.planId = planId;

        for (IncentiveSchedule incentiveSchedule : NodeServiceUtil.getAllScheduleGroups(planId)) {
            scheduleBatchIds.add(incentiveSchedule.getSchedule_batch_id());
        }
    }

    @Override
    protected void dataValid(IncentiveManagement o, List<String> errorList) {
        // 判断 schedule_batch_id 是否存在
        if (StringUtils.isNotBlank(o.getSchedule_batch_id()) && !scheduleBatchIds.contains(o.getSchedule_batch_id())) {
            errorList.add("schedule_batch_id " + getMessage("IS_NOT_IN_DB"));
        }
        // 判断 participant_id 是否存在
        if (StringUtils.isNotBlank(o.getParticipant_id())) {
            ParticipantInfo participantInfo = new ParticipantInfo();
            participantInfo.setParticipant_id(o.getParticipant_id());
            if (NodeServiceUtil.getEntity(participantInfo).isEmpty()) {
                errorList.add("participant_id " + getMessage("IS_NOT_IN_DB"));
            }
        }
    }

}
