package com.swcs.esop.api.entity;

import com.alibaba.excel.util.StringUtils;
import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Granting Limitation services from ESOP system (Granting 30% or "btw 30-50%" or "> 75%") - At individual & multiple participant level
 * Input parameters:
 * 1. Total issued options for this grant (Total Issued options)
 * 2. Total number of shares held by an individual participant or multiple people as at [last granted] (Last Aggregated Shares granted)
 * 3. The lowest percentage holding of that person or multiple people in the 12 month period ending on and inclusive of the date of the grant (Lowest percentage)
 * 4. Newly granted shares for that person or multiple people (Newly granted)
 * <p>
 * Output:
 * 1. The new percentage holding of that person or multiple people as at [today] (inclusive of the this grant) (New percentage)
 * Processing Rule:
 * 1. Previous percentage = (Last Aggregated Shares granted) / (Total Issused options) x 100
 * 2. New percentage = ((Last Aggregated Shares granted)  + (Newly granted))/ (Total Issused options) x 100
 * 3. If {(New percentage > 75%)} then return {("New percentage" = 0) + Notification to "List Company"}. Otherwise continue.  // Over 75% is not allow
 * 4. If {("New percentage" - "Lowest percentage")  <  2%} then continue. Otherwise return {("New percentage" = 0)  + Notification to "List Company"}.   // not allow: this grant results in the increasing by more than 2% from the lowest percentage holding of that person or collectively in the 12 month period ending on and inclusive of the date of the grant
 * 5. If {(Previous percentage < 30%) and (New percentage < 30%)} then return "New percentage". Otherwise continue // not allow: this grant results in 30% or more (individually or collectively)
 * 6. If {(Previous percentage >= 30%) and (Previous percentage < 50%)} then return "New percentage". Otherwise {("New percentage" = 0) + Notification to "List Company"}.  // Over 50% is not allow
 *
 * @author 阮程
 * @date 2022/11/7
 */
@Data
public class GrantingInput3 extends GrantingInput {

    private BigDecimal totalIssuedOptions;
    private BigDecimal lastAggregatedSharesGranted;
    private BigDecimal lowestPercentage;
    private BigDecimal newlyGranted;

    @Override
    public ApiResult paramsValid() {
        if (totalIssuedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "totalIssuedOptions");
        }
        if (lastAggregatedSharesGranted == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "lastAggregatedSharesGranted");
        }
        if (lowestPercentage == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "lowestPercentage");
        }
        if (newlyGranted == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "newlyGranted");
        }
        if (StringUtils.isBlank(recipientID)) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "recipientID");
        }
        return ApiResult.success();
    }

    public ApiResult ruleCalculation() {
        BigDecimal data = new BigDecimal(0);
        BigDecimal previousPercentage = lastAggregatedSharesGranted.divide(totalIssuedOptions).multiply(new BigDecimal(100));
        BigDecimal newPercentage = lastAggregatedSharesGranted.add(newlyGranted).divide(totalIssuedOptions).multiply(new BigDecimal(100));
        if (newPercentage.doubleValue() > 75.00D) {
            notificationListedCompany("notify_template/listed_company/2.txt");
        } else {
            if (newPercentage.subtract(lowestPercentage).doubleValue() < 2.00D) {
                if (previousPercentage.doubleValue() < 30.00D && newPercentage.doubleValue() < 30.00D) {
                    return ApiResult.success().setData(newPercentage);
                }
                if (previousPercentage.doubleValue() >= 30.00D && previousPercentage.doubleValue() < 50.00D) {
                    return ApiResult.success().setData(newPercentage);
                } else {
                    notificationListedCompany("notify_template/listed_company/3.txt");
                }
            } else {
                notificationListedCompany("notify_template/listed_company/4.txt");
            }
        }
        return ApiResult.success().setData(data);
    }
}
