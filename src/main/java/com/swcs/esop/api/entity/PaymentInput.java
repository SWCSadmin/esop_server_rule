package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.IncentiveStatus;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 阮程
 * @date 2022/11/4
 */
@Data
public class PaymentInput {

    private IncentiveStatus incentiveStatus;
    private BigDecimal withholdingTax;
    private BigDecimal costs;
    private BigDecimal shares;
    private BigDecimal pc = new BigDecimal(0);
    private BigDecimal mp;

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
    public ApiResult calculation() {
        // TODO: 逻辑不完整待定
        BigDecimal data = new BigDecimal(0);

        switch (incentiveStatus.getESOPStatus()) {
            case NA:
                break;
            case Grant:
                break;
            case Vest:
                if (incentiveStatus.isContributionOrCostsRequired()) {
                    data = costs.add(withholdingTax);
                } else {
                    if (pc.intValue() > 0) {
                        data = pc.divide(new BigDecimal(100)).multiply(shares).multiply(mp).add(withholdingTax);
                    }
                }
                break;
            case Exercise:
                if (incentiveStatus.isContributionOrCostsRequired()) {
                    data = costs.add(withholdingTax);
                } else {
                    if (costs.intValue() > 0) {
                        data = costs.multiply(shares).add(withholdingTax);
                    }
                }
                break;
            case Cancelled:
                break;
        }
        return ApiResult.success().setData(data);
    }
}
