package com.swcs.esop.api.module.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.swcs.esop.api.entity.ParticipantInfoVendor;
import com.swcs.esop.api.util.NodeServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ParticipantInfoVendorReadListener extends BaseReadListenerReturnStatus<ParticipantInfoVendor> {

    private Map<String, ParticipantInfoVendor> userInputIdMap = new HashMap<>();

    public ParticipantInfoVendorReadListener(boolean upsert) {
        super(upsert, "participant_info");
    }

    @Override
    protected void beforeInvoke(ParticipantInfoVendor o, List<String> errorList) {
        String userInputId = o.getUser_input_id_x();
        if (userInputIdMap.containsKey(userInputId)) {
            errorList.add(getMessage("DUPLICATE_VALUE_EXIST", "user_input_id_x"));
        } else {
            userInputIdMap.put(userInputId, o);
        }
        super.beforeInvoke(o, errorList);
    }

    @Override
    protected void dataValid(ParticipantInfoVendor o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<ParticipantInfoVendor> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("participant_id " + getMessage("IS_INVALID_VALUE"));
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!insertList.isEmpty()) {
            List<ParticipantInfoVendor> list = NodeServiceUtil.batchAddEntity(insertList);
            if (!list.isEmpty()) {
                for (ParticipantInfoVendor t : list) {
                    if (!STATUS_SUCCESS.equals(t.getStatus())) {
                        errorNum++;
                        errorCachedDataMap.put(t.getPrimaryKey(), t.getStatus());
                    } else {
                        ParticipantInfoVendor data = userInputIdMap.get(t.getUser_input_id_x());
                        if (data != null) {
                            data.setParticipant_id(t.getParticipant_id());
                        }
                    }
                }
            }
            // 添加错误信息
            for (ParticipantInfoVendor t : insertList) {
                if (errorCachedDataMap.containsKey(t.getPrimaryKey())) {
                    t.setStatus(getMessage("RECORD_ADD_ERROR") + ": " + errorCachedDataMap.get(t.getPrimaryKey()));
                } else {
                    t.setStatus(getMessage("SUCCESS"));
                }
            }
        }
        if (!updateList.isEmpty()) {
            try {
                buildErrorRecord(NodeServiceUtil.batchUpdateEntityReturnStatus(updateList), updateList, false);
            } catch (Exception e) {
                for (ParticipantInfoVendor t : updateList) {
                    errorNum++;
                    t.setStatus(e.getMessage());
                }
            }
        }
    }

    public Map<String, ParticipantInfoVendor> getUserInputIdMap() {
        return userInputIdMap;
    }

    public void setUserInputIdMap(Map<String, ParticipantInfoVendor> userInputIdMap) {
        this.userInputIdMap = userInputIdMap;
    }
}
