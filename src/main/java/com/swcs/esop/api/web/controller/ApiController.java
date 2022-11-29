package com.swcs.esop.api.web.controller;

import com.swcs.esop.api.common.base.BaseController;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.entity.*;
import com.swcs.esop.api.entity.tax.RSUTaxRateInput;
import com.swcs.esop.api.entity.tax.SASTaxRateInput;
import com.swcs.esop.api.entity.tax.SOSTaxRateInput;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.Future;

/**
 * @author 阮程
 * @date 2022/10/21
 */
@RestController
@RequestMapping("/api")
public class ApiController extends BaseController {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AsyncTask asyncTask;

    /**
     * 语言设置
     *
     * @param lang
     * @return
     */
    @RequestMapping("/setLang/{lang}")
    public ApiResult setLang(@PathVariable("lang") String lang) {
        LocaleContextHolder.setLocale(StringUtils.parseLocale(lang));
        return ApiResult.success();
    }

    /**
     * i18n
     *
     * @param code
     * @return
     */
    @GetMapping("/message/{code}")
    public ApiResult message(@PathVariable("code") String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return ApiResult.success(messageSource.getMessage(code.toUpperCase(), new Object[]{}, locale));
    }

    /**
     * 通知
     *
     * @param notification
     * @return
     */
    @PostMapping("/notify")
    public ApiResult notify(@RequestBody Notification notification) {
        try {
            notification.setMessage(notification.loadTemplate());
            Future<Boolean> future = asyncTask.notify(notification);
            boolean success = future.get();
            if (success) {
                return ApiResult.success();
            }
            return ApiResult.error(Status.NOTIFY_ERROR);
        } catch (Exception e) {
            logger.error("notify error", e);
            return ApiResult.errorWithArgs(Status.INTERNAL_SERVER_ERROR_ARGS, e.getMessage());
        }
    }

    /**
     * 税额计算
     *
     * @param taxRateInput
     * @return
     */
    @GetMapping("/tax/sos")
    public ApiResult taxSOS(SOSTaxRateInput taxRateInput) {
        return taxRateInput.calculation();
    }

    @GetMapping("/tax/sas")
    public ApiResult taxSAS(SASTaxRateInput taxRateInput) {
        return taxRateInput.calculation();
    }

    @GetMapping("/tax/rsu")
    public ApiResult taxRSU(RSUTaxRateInput taxRateInput) {
        return taxRateInput.calculation();
    }

    /**
     * 激励管理上传文件
     *
     * @param file
     * @param upsert
     * @return
     */
    @PostMapping("/im/upload")
    public ApiResult imUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "upsert") boolean upsert, @RequestParam(value = "planId") String planId) {
        return ExcelUtil.parseIncentiveManagement(file, upsert, planId);
    }

    /**
     * 信托交易
     *
     * @param file
     * @param upsert
     * @return
     */
    @PostMapping("/trustTransactions/upload")
    public ApiResult trustTransactionsUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "upsert") boolean upsert, String startDate, String endDate) {
        return ExcelUtil.parseTrustTransactions(file, upsert, startDate, endDate);
    }

    @PostMapping("/participantInfo/upload")
    public ApiResult participantInfoUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "upsert") boolean upsert) {
        return ExcelUtil.parseParticipantInfo(file, upsert);
    }

    /**
     * Cash non-cash payment services from ESOP system - at each participant level
     */
    @PostMapping("/payment")
    public ApiResult payment(@RequestBody PaymentInput paymentInput) {
        return paymentInput.calculation();
    }

    /**
     * Lapsed incentive services from ESOP system - Failed KYC or Failed KPI or Rejected or Expiry date at Participant level
     *
     * @param lapsedIncentiveInput
     * @return
     */
    @PostMapping("/lapsed")
    public ApiResult lapsed(@RequestBody LapsedIncentiveInput lapsedIncentiveInput) {
        return lapsedIncentiveInput.calculation();
    }

    /**
     * Granting Limitation services from ESOP system (Granting at 10% & 30%) - At plan level
     *
     * @param grantingInput
     * @return
     */
    @PostMapping("/granting/1")
    public ApiResult granting1(@RequestBody GrantingInput1 grantingInput) {
        return grantingInput.calculation();
    }

    /**
     * Granting Limitation services from ESOP system (Granting at 1% and (0.1% or HKD 5 M)) - At participant level
     *
     * @param grantingInput
     * @return
     */
    @PostMapping("/granting/2")
    public ApiResult granting2(@RequestBody GrantingInput2 grantingInput) {
        return grantingInput.calculation();
    }


    /**
     * Granting Limitation services from ESOP system (Granting 30% or "btw 30-50%" or "> 75%") - At individual & multiple participant level
     *
     * @param grantingInput
     * @return
     */
    @PostMapping("/granting/3")
    public ApiResult granting3(@RequestBody GrantingInput3 grantingInput) {
        return grantingInput.calculation();
    }

    /**
     * 一次性文件下载, 下载成功之后会被删除
     *
     * @param fileName
     * @throws Exception
     */
    @GetMapping("/download/once")
    public void downloadOnce(String fileName) throws Exception {
        String filePath = AppUtils.getProperty("app.once-file-path");
        String fullFilePath = filePath + File.separator + fileName;
        File file = new File(fullFilePath);
        if (!file.exists()) {
            ApiResult.responseResult(response, ApiResult.error(Status.FILE_NOT_EXIST_ERROR));
        } else {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
                // TODO: 待定
//                file.delete();
            }
        }
    }

    @PostMapping("/blackout")
    public ApiResult blackout(@RequestBody BlackoutPeriodsInput blackoutPeriodsInput) {
        return blackoutPeriodsInput.calculation();
    }
}
