package com.swcs.esop.api.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.IncentiveManagement;
import com.swcs.esop.api.entity.KpiStatusInfo;
import com.swcs.esop.api.entity.TrustTransactions;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.module.excel.IncentiveManagementReadListener;
import com.swcs.esop.api.module.excel.KpiStatusInfoReadListener;
import com.swcs.esop.api.module.excel.TrustTransactionsReadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static ApiResult parseIncentiveManagement(MultipartFile file, boolean upsert, String planId) {
        try {
            ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
            IncentiveManagementReadListener incentiveManagementReadListener = new IncentiveManagementReadListener(upsert, planId);
            KpiStatusInfoReadListener kpiStatusInfoReadListener = new KpiStatusInfoReadListener(upsert, planId);
            ReadSheet readSheet1 = EasyExcel.readSheet(0).head(IncentiveManagement.class).registerReadListener(incentiveManagementReadListener).build();
            ReadSheet readSheet2 = EasyExcel.readSheet(1).head(KpiStatusInfo.class).registerReadListener(kpiStatusInfoReadListener).build();
            // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheet1, readSheet2);

            if (incentiveManagementReadListener.isSuccess() && kpiStatusInfoReadListener.isSuccess()) {
                // 都校验成功之后执行最后的步骤
                return ApiResult.success();
            } else {
                String filePath = AppUtils.getProperty("app.once-file-path");
                File dirFile = new File(filePath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                String fileName = "error_" + file.getOriginalFilename();
                String fullFilePath = filePath + File.separator + fileName;
                ExcelWriter excelWriter = EasyExcel.write(fullFilePath).build();
                WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "grant").head(IncentiveManagement.class).build();
                excelWriter.write(incentiveManagementReadListener.getCacheList(), writeSheet1);
                WriteSheet writeSheet2 = EasyExcel.writerSheet(1, "kpi_status").head(KpiStatusInfo.class).build();
                excelWriter.write(kpiStatusInfoReadListener.getCacheList(), writeSheet2);

                excelWriter.finish();

                return ApiResult.success(Status.INCENTIVE_MANAGEMENT_UPLOAD_DATA_ERROR).setData(fileName);
            }
        } catch (Exception e) {
            logger.error("", e);
            return ApiResult.errorWithArgs(Status.INCENTIVE_MANAGEMENT_UPLOAD_ERROR, e.getMessage());
        }
    }

    public static ApiResult parseTrustTransactions(MultipartFile file, boolean upsert, String startDate, String endDate) {
        try {
            ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
            TrustTransactionsReadListener trustTransactionsReadListener = new TrustTransactionsReadListener(upsert, startDate, endDate);
            ReadSheet readSheet1 = EasyExcel.readSheet(0).head(TrustTransactions.class).registerReadListener(trustTransactionsReadListener).build();
            excelReader.read(readSheet1);

            if (trustTransactionsReadListener.isSuccess()) {
                // 都校验成功之后执行最后的步骤
                return ApiResult.success();
            } else {
                String filePath = AppUtils.getProperty("app.once-file-path");
                File dirFile = new File(filePath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                String fileName = "error_" + file.getOriginalFilename();
                String fullFilePath = filePath + File.separator + fileName;
                ExcelWriter excelWriter = EasyExcel.write(fullFilePath).build();
                WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "transactions").head(TrustTransactions.class).build();
                excelWriter.write(trustTransactionsReadListener.getCacheList(), writeSheet1);

                excelWriter.finish();

                return ApiResult.success(Status.TRUST_TRANSACTIONS_UPLOAD_DATA_ERROR).setData(fileName);
            }
        } catch (Exception e) {
            logger.error("", e);
            return ApiResult.errorWithArgs(Status.TRUST_TRANSACTIONS_UPLOAD_ERROR, e.getMessage());
        }
    }
}
