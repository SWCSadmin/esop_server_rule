package com.swcs.esop.api.module.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.swcs.esop.api.common.base.ExcelUploadEntity;
import com.swcs.esop.api.util.NodeServiceUtil;

/**
 * 批量新增
 *
 * @author 阮程
 * @date 2022/11/1
 */
public abstract class BaseReadListenerReturnStatus<T extends ExcelUploadEntity> extends BaseReadListener<T> {

    public BaseReadListenerReturnStatus(boolean upsert, String sheetName) {
        super(upsert, sheetName);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (!insertList.isEmpty()) {
            try {
                buildErrorRecord(NodeServiceUtil.batchAddEntityReturnStatus(insertList), insertList, true);
            } catch (Exception e) {
                for (T t : insertList) {
                    errorNum++;
                    t.setStatus(e.getMessage());
                }
            }
        }
        if (!updateList.isEmpty()) {
            try {
                buildErrorRecord(NodeServiceUtil.batchUpdateEntityReturnStatus(updateList), updateList, false);
            } catch (Exception e) {
                for (T t : updateList) {
                    errorNum++;
                    t.setStatus(e.getMessage());
                }
            }
        }
    }

}
