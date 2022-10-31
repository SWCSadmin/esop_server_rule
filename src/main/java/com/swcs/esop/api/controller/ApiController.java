package com.swcs.esop.api.controller;

import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.entity.ApiResult;
import com.swcs.esop.api.entity.Notification;
import com.swcs.esop.api.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/setLang/{lang}")
    public ApiResult setLang(@PathVariable("lang") String lang) {
        LocaleContextHolder.setLocale(StringUtils.parseLocale(lang));
        return ApiResult.success();
    }

    @GetMapping("/message/{code}")
    public ApiResult message(@PathVariable("code") String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return ApiResult.success(messageSource.getMessage(code.toUpperCase(), new Object[]{}, locale));
    }

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
}
