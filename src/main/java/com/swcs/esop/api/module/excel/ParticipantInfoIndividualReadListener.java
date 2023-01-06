package com.swcs.esop.api.module.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.swcs.esop.api.entity.LoginAccount;
import com.swcs.esop.api.entity.ParticipantInfoIndividual;
import com.swcs.esop.api.util.NodeServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ParticipantInfoIndividualReadListener extends BaseReadListenerReturnStatus<ParticipantInfoIndividual> {

    private Map<String, ParticipantInfoIndividual> userInputIdMap = new HashMap<>();

    public ParticipantInfoIndividualReadListener(boolean upsert) {
        super(upsert, "participant_info");
    }

    @Override
    protected void beforeInvoke(ParticipantInfoIndividual o, List<String> errorList) {
        String userInputId = o.getUser_input_id_z();
        if (userInputIdMap.containsKey(userInputId)) {
            errorList.add(getMessage("DUPLICATE_VALUE_EXIST", "user_input_id"));
        } else {
            userInputIdMap.put(userInputId, o);
        }
        super.beforeInvoke(o, errorList);
    }

    @Override
    protected void dataValid(ParticipantInfoIndividual o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<ParticipantInfoIndividual> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("participant_id " + getMessage("IS_INVALID_VALUE"));
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        List<LoginAccount> loginAccounts = new ArrayList<>();
        if (!insertList.isEmpty()) {
            List<ParticipantInfoIndividual> list = NodeServiceUtil.batchAddEntity(insertList);
            if (!list.isEmpty()) {
                for (ParticipantInfoIndividual t : list) {
                    if (!STATUS_SUCCESS.equals(t.getStatus())) {
                        errorNum++;
                        errorCachedDataMap.put(t.getPrimaryKey(), t.getStatus());
                    } else {
                        ParticipantInfoIndividual data = userInputIdMap.get(t.getUser_input_id_z());
                        if (data != null) {
                            data.setParticipant_id(t.getParticipant_id());
                        }
                    }
                }
            }
            // 添加错误信息
            for (ParticipantInfoIndividual t : insertList) {
                if (errorCachedDataMap.containsKey(t.getPrimaryKey())) {
                    t.setStatus(getMessage("RECORD_ADD_ERROR") + ": " + errorCachedDataMap.get(t.getPrimaryKey()));
                } else {
                    t.setStatus(getMessage("SUCCESS"));
                    // 判断账号是否存在
                    LoginAccount loginAccount = t.getLoginAccount();
                    if (NodeServiceUtil.getLoginAccount(loginAccount.getLogin_id()).isEmpty()) {
                        loginAccounts.add(loginAccount);
                    }
                }
            }

            if (loginAccounts.size() > 0) {
                // 创建账号
                NodeServiceUtil.batchAddLoginAccount(loginAccounts);
            }
        }
        if (!updateList.isEmpty()) {
            try {
                buildErrorRecord(NodeServiceUtil.batchUpdateEntityReturnStatus(updateList), updateList, false);
            } catch (Exception e) {
                for (ParticipantInfoIndividual t : updateList) {
                    errorNum++;
                    t.setStatus(e.getMessage());
                }
            }
        }
    }

    public Map<String, ParticipantInfoIndividual> getUserInputIdMap() {
        return userInputIdMap;
    }

    public void setUserInputIdMap(Map<String, ParticipantInfoIndividual> userInputIdMap) {
        this.userInputIdMap = userInputIdMap;
    }
}
