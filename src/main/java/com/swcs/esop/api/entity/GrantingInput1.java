package com.swcs.esop.api.entity;

import com.alibaba.excel.util.StringUtils;
import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.IncentiveStatus;
import com.swcs.esop.api.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Granting Limitation services from ESOP system (Granting at 10% & 30%) - At plan level
 * Input parameters:
 * 1. Total issued options for this grant (Total Issued options)
 * 2. Share options granted subsequently for this grant (Granted options)
 * 3. Lapsed options for this grant from the "Granted options" (Lapsed options)
 * 4. Exercised options for this grant from the "Granted options" (Exercised options)
 * 5. Available options for this grant before this "Granted options" (Available options)
 * Output:
 * 1. Total number of share options available for this grant (New Available options)
 * Processing Rule:
 * 1. If ((Granted options) <= Round down {(Total Issued options) x 10% } ) Then (New Available options) = {(Available options) + Round down {(Total Issued options) x 10% } - (Granted options) + (Lapsed options)}. Otherwise (New Available options) = 0
 * 2. Outstanding options = (New Available options) - (Exercised options)
 * 3. If (Outstanding options) < Round down {(Total Issued options) x 30% } Then return (New Available options). Otherwise return (New Available options) = 0
 *
 * @author 阮程
 * @date 2022/11/7
 */
@Data
public class GrantingInput1 extends GrantingInput {

    private BigDecimal totalIssuedOptions;
    private BigDecimal grantedOptions;
    private BigDecimal lapsedOptions;
    private BigDecimal exercisedOptions;
    private BigDecimal availableOptions;

    @Override
    public ApiResult paramsValid() {
        if (totalIssuedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "totalIssuedOptions");
        }
        if (grantedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "grantedOptions");
        }
        if (lapsedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "lapsedOptions");
        }
        if (exercisedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "exercisedOptions");
        }
        if (availableOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "availableOptions");
        }
        return ApiResult.success();
    }

    public ApiResult ruleCalculation() {
        BigDecimal newAvailableOptions = new BigDecimal(0);
        BigDecimal b1 = totalIssuedOptions.multiply(new BigDecimal(0.1)).setScale(0, BigDecimal.ROUND_DOWN);
        if (grantedOptions.intValue() <= b1.intValue()) {
            newAvailableOptions = availableOptions.add(b1).subtract(grantedOptions).add(lapsedOptions);
        }
        BigDecimal outstandingOptions = newAvailableOptions.subtract(exercisedOptions);
        BigDecimal b2 = totalIssuedOptions.multiply(new BigDecimal(0.3)).setScale(0, BigDecimal.ROUND_DOWN);
        if (outstandingOptions.intValue() < b2.intValue()) {
            return ApiResult.success().setData(newAvailableOptions);
        }
        return ApiResult.success().setData(0);
    }
}
