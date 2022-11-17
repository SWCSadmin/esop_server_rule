package com.swcs.esop.api.common.base;

import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.entity.Notification;
import com.swcs.esop.api.enums.CommunicationTypeEnum;
import com.swcs.esop.api.util.AppUtils;

/**
 * @author 阮程
 * @date 2022/11/7
 */
public interface BaseRuleInput {

    /**
     * 参数校验
     *
     * @return
     */
    ApiResult paramsValid();

    /**
     * 规则计算
     *
     * @return
     */
    ApiResult ruleCalculation();

    default ApiResult calculation() {
        ApiResult apiResult = paramsValid();
        if (apiResult.isSuccess()) {
            apiResult = ruleCalculation();
        }
        return apiResult;
    }

}
