package com.swcs.esop.api.web.controller;

import com.swcs.esop.api.common.base.BaseController;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.entity.tax.RSUTaxRateInput;
import com.swcs.esop.api.entity.tax.SASTaxRateInput;
import com.swcs.esop.api.entity.tax.SOSTaxRateInput;
import com.swcs.esop.api.enums.SaveMode;
import com.swcs.esop.api.util.ExcelUtil;
import com.swcs.esop.api.util.TaxRateUtil;
import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.entity.*;
import com.swcs.esop.api.enums.Status;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * @param taxRateCalculation
     * @return
     */
    @GetMapping("/tax/sos")
    public ApiResult taxSOS(SOSTaxRateInput taxRateCalculation) {
        return TaxRateUtil.withholdingServiceForSOS(taxRateCalculation);
    }

    @GetMapping("/tax/sas")
    public ApiResult taxSAS(SASTaxRateInput taxRateCalculation) {
        return TaxRateUtil.withholdingServiceForSAS(taxRateCalculation);
    }

    @GetMapping("/tax/rsu")
    public ApiResult taxRSU(RSUTaxRateInput taxRateCalculation) {
        return TaxRateUtil.withholdingServiceForRSU(taxRateCalculation);
    }

    /**
     * 激励管理上传文件
     *
     * @param file
     * @param upsert
     * @return
     */
    @PostMapping("/im/upload")
    public ApiResult imUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "upsert") boolean upsert) {
        return ExcelUtil.parseIncentiveManagement(file, upsert);
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

}
