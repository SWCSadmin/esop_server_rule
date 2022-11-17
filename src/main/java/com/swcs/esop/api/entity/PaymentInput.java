package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.IncentiveStatus;
import com.swcs.esop.api.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/11/4
 */
@Data
public class PaymentInput implements BaseRuleInput {

    private IncentiveStatus incentiveStatus;
    private BigDecimal withholdingTax;
    private BigDecimal costs;
    private BigDecimal shares;
    private BigDecimal pc = new BigDecimal(0);
    private BigDecimal mp;

    @Override
    public ApiResult paramsValid() {
        if (incentiveStatus == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "incentiveStatus");
        }
        if (incentiveStatus.equals(IncentiveStatus.Vested​) && pc == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "pc");
        }
        if (incentiveStatus.equals(IncentiveStatus.Exercised​) && costs == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "costs");
        }
        return ApiResult.success();
    }

    /**
     * Output:
     * 1."Payment amount" = (payment amount)
     * Processing Rule:
     * "If {(((Incentive status) == ""Vested"" ) and (PC) > 0) }
     * then return ""payment amount"" = (PC)/100 x (shares) x (MP) + (withholding tax). Otherwise return ""payment amount"" = 0;"
     * "If {(((Incentive status) == ""Exercised"" ) and (Costs) > 0) }
     * then return ""payment amount"" = (Costs) x (shares) + (withholding tax). Otherwise return ""payment amount"" = 0;"
     *
     * @return
     */
    @Override
    public ApiResult ruleCalculation() {
        BigDecimal data = new BigDecimal(0);
        if (incentiveStatus == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "incentiveStatus");
        } else {
            if (incentiveStatus.isContributionOrCostsRequired()) {
                data = costs.add(withholdingTax);
            } else {
                if (incentiveStatus.equals(IncentiveStatus.Vested​) && pc.doubleValue() > 0.00D) {
                    data = pc.divide(new BigDecimal(100)).multiply(shares).multiply(mp).add(withholdingTax);
                } else if (incentiveStatus.equals(IncentiveStatus.Exercised​) && costs.doubleValue() > 0.00D) {
                    data = costs.multiply(shares).add(withholdingTax);
                }
            }
        }
        return ApiResult.success().setData(data);
    }
}
