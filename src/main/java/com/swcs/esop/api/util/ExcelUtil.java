package com.swcs.esop.api.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.module.excel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static ApiResult parseIncentiveManagement(MultipartFile file, boolean upsert, String planId) {
        IncentiveManagementReadListener incentiveManagementReadListener = new IncentiveManagementReadListener(upsert, planId);
        KpiStatusInfoReadListener kpiStatusInfoReadListener = new KpiStatusInfoReadListener(upsert, planId);
        return parseExcel(file, incentiveManagementReadListener, kpiStatusInfoReadListener);
    }

    public static ApiResult parseTrustTransactions(MultipartFile file, boolean upsert, String startDate, String endDate) {
        TrustTransactionsReadListener trustTransactionsReadListener = new TrustTransactionsReadListener(upsert, startDate, endDate);
        return parseExcel(file, trustTransactionsReadListener);
    }

    public static ApiResult parseParticipantInfo(MultipartFile file, boolean upsert) {
        ParticipantInfoIndividualReadListener participantInfoReadListener = new ParticipantInfoIndividualReadListener(upsert);
        return parseExcel(file, participantInfoReadListener);
    }

    public static ApiResult parseParticipantInfoForIndividual(MultipartFile file, boolean upsert) {
        ParticipantInfoIndividualReadListener participantInfoReadListener = new ParticipantInfoIndividualReadListener(upsert);
        ContactPersonIndividualReadListener contactPersonReadListener = new ContactPersonIndividualReadListener(upsert, participantInfoReadListener);
        return parseExcel(file, participantInfoReadListener, contactPersonReadListener);
    }

    public static ApiResult parseParticipantInfoForVendor(MultipartFile file, boolean upsert) {
        VendorInfoReadListener vendorInfoReadListener = new VendorInfoReadListener(upsert);
        ParticipantInfoVendorReadListener participantInfoReadListener = new ParticipantInfoVendorReadListener(upsert);
        ContactPersonVendorReadListener contactPersonReadListener = new ContactPersonVendorReadListener(upsert, participantInfoReadListener);
        return parseExcel(file, vendorInfoReadListener, participantInfoReadListener, contactPersonReadListener);
    }

    public static ApiResult parseKycForIndividual(MultipartFile file) {
        KycParticipantInfoReadListener kycParticipantInfoIndividualReadListener = new KycParticipantInfoReadListener();
        return parseExcel(file, kycParticipantInfoIndividualReadListener);
    }

    public static ApiResult parseKycForCorporate(MultipartFile file) {
        KycVendorInfoReadListener vendorInfoReadListener = new KycVendorInfoReadListener();
        KycParticipantInfoReadListener kycParticipantInfoVendorReadListener = new KycParticipantInfoReadListener();
        KycContactPersonVendorReadListener kycContactPersonVendorReadListener = new KycContactPersonVendorReadListener();
        return parseExcel(file, vendorInfoReadListener, kycParticipantInfoVendorReadListener, kycContactPersonVendorReadListener);
    }

    private static ApiResult parseExcel(MultipartFile file, BaseReadListener... listeners) {
        try {
            ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
            List<ReadSheet> readSheets = new ArrayList<>();
            for (int i = 0; i < listeners.length; i++) {
                ReadSheet readSheet = EasyExcel.readSheet(i).head(listeners[i].getGenericClassT()).registerReadListener(listeners[i]).build();
                readSheets.add(readSheet);
            }
            excelReader.read(readSheets);
            excelReader.close();

            boolean success;
            for (BaseReadListener listener : listeners) {
                success = listener.isSuccess();
                if (!success) {
                    String filePath = AppUtils.getProperty("app.once-file-path");
                    File dirFile = new File(filePath);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    String fileName = "error_" + file.getOriginalFilename();
                    String fullFilePath = filePath + File.separator + fileName;
                    ExcelWriter excelWriter = EasyExcel.write(fullFilePath).build();
                    for (int i = 0; i < listeners.length; i++) {
                        WriteSheet writeSheet = EasyExcel.writerSheet(i, listeners[i].getSheetName()).head(listeners[i].getGenericClassT()).build();
                        excelWriter.write(listeners[i].getCacheList(), writeSheet);
                    }
                    excelWriter.close();
                    return ApiResult.successWithArgs(Status.UPLOAD_DATA_ERROR).setData(fileName);
                }
            }
            return ApiResult.success();
        } catch (Exception e) {
            logger.error("", e);
            return ApiResult.errorWithArgs(Status.UPLOAD_ERROR, e.getMessage());
        }
    }
}
