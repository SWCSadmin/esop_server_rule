package com.swcs.esop.api.module.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.swcs.esop.api.entity.IncentiveManagement;
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

    public IncentiveManagementReadListener(boolean upsert) {
        super(upsert);
    }

    @Override
    public void invoke(IncentiveManagement o, AnalysisContext analysisContext) {
        List<String> errorList = new ArrayList<>();

        if (StringUtils.isBlank(o.getSchedule_batch_id())) {
            errorList.add("schedule_batch_id is empty");
        }
        if (StringUtils.isBlank(o.getParticipant_id())) {
            errorList.add("participant_id is empty");
        }

        if (!upsert) {
            if (!NodeServiceUtil.getIncentiveManagement(o).isEmpty()) {
                errorList.add("Record already exists");
            }
        } else {
            if (NodeServiceUtil.getIncentiveManagement(o).isEmpty()) {
                insertList.add(o);
            } else {
                updateList.add(o);
            }
        }

        if (errorList.size() > 0) {
            o.setError(StringUtils.join(errorList, " | "));
            errorNum++;
        }
        cachedDataList.add(o);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (isSuccess()) {
            if (!insertList.isEmpty()) {
                NodeServiceUtil.batchAddIncentiveManagement(insertList);
            }
            if (!updateList.isEmpty()) {
                NodeServiceUtil.batchUpdateIncentiveManagement(updateList);
            }
        }
    }

}
