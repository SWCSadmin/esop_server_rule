package com.swcs.esop.api.module.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.swcs.esop.api.entity.VendorInfo;
import com.swcs.esop.api.util.NodeServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/12/9
 */
public class VendorInfoReadListener extends BaseReadListenerReturnStatus<VendorInfo> {

    private Map<String, VendorInfo> userInputIdMap = new HashMap<>();

    public VendorInfoReadListener(boolean upsert) {
        super(upsert, "vendor_info");
    }

    @Override
    protected void beforeInvoke(VendorInfo o, List<String> errorList) {
        String userInputId = o.getUser_input_id_z();
        if (userInputIdMap.containsKey(userInputId)) {
            errorList.add(getMessage("DUPLICATE_VALUE_EXIST", "user_input_id_z"));
        } else {
            userInputIdMap.put(userInputId, o);
        }
        super.beforeInvoke(o, errorList);
    }

    @Override
    protected void dataValid(VendorInfo o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<VendorInfo> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("vendor_id " + getMessage("IS_INVALID_VALUE"));
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!insertList.isEmpty()) {
            List<VendorInfo> list = NodeServiceUtil.batchAddEntity(insertList);
            if (!list.isEmpty()) {
                for (VendorInfo t : list) {
                    if (!STATUS_SUCCESS.equals(t.getStatus())) {
                        errorNum++;
                        errorCachedDataMap.put(t.getPrimaryKey(), t.getStatus());
                    } else {
                        VendorInfo data = userInputIdMap.get(t.getUser_input_id_z());
                        if (data != null) {
                            data.setVendor_id(t.getVendor_id());
                        }
                    }
                }
            }
            // 添加错误信息
            for (VendorInfo t : insertList) {
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
                for (VendorInfo t : updateList) {
                    errorNum++;
                    t.setStatus(e.getMessage());
                }
            }
        }
    }

    public Map<String, VendorInfo> getUserInputIdMap() {
        return userInputIdMap;
    }

    public void setUserInputIdMap(Map<String, VendorInfo> userInputIdMap) {
        this.userInputIdMap = userInputIdMap;
    }
}
