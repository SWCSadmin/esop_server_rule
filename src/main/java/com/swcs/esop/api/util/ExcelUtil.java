package com.swcs.esop.api.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.IncentiveManagement;
import com.swcs.esop.api.entity.KpiStatusInfo;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.module.excel.IncentiveManagementReadListener;
import com.swcs.esop.api.module.excel.KpiStatusInfoReadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static ApiResult parseIncentiveManagement(MultipartFile file, boolean upsert) {
        try {
            ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
            IncentiveManagementReadListener incentiveManagementReadListener = new IncentiveManagementReadListener(upsert);
            KpiStatusInfoReadListener kpiStatusInfoReadListener = new KpiStatusInfoReadListener(upsert);
            ReadSheet readSheet1 = EasyExcel.readSheet(0).head(IncentiveManagement.class).registerReadListener(incentiveManagementReadListener).build();
            ReadSheet readSheet2 = EasyExcel.readSheet(1).head(KpiStatusInfo.class).registerReadListener(kpiStatusInfoReadListener).build();
            // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheet1, readSheet2);

            if (incentiveManagementReadListener.isSuccess() && kpiStatusInfoReadListener.isSuccess()) {
                return ApiResult.success();
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("incentiveManagement", incentiveManagementReadListener.getCachedDataList());
                data.put("kpiStatusInfo", kpiStatusInfoReadListener.getCachedDataList());
                return ApiResult.success(Status.INCENTIVE_MANAGEMENT_UPLOAD_DATA_ERROR).setData(data);
            }
        } catch (Exception e) {
            logger.error("", e);
            return ApiResult.errorWithArgs(Status.INCENTIVE_MANAGEMENT_UPLOAD_ERROR, e.getMessage());
        }


    }

}
