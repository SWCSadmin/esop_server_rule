package com.swcs.esop.api.entity;

import com.alibaba.excel.util.StringUtils;
import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.ParticipantType;
import com.swcs.esop.api.enums.Status;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Granting Limitation services from ESOP system (Granting at 1% and (0.1% or HKD 5 M)) - At participant level
 * Input parameters:
 * 1. Participant Level (e.g. General staff, Executive Director, etc) (Participant type)
 * 2. Total number of shares issued for this grant (Total shares Issused)
 * 3. Number of options in granted in this grant (Granted participant options)
 * 4. Aggregated number of options granted to this participant within the 12 months period (Aggregated Granted options)
 * 5. Closing price on the granted date (Closing price)
 * <p>
 * Output:
 * 1. Total number of share options available to be granted (Total Available Shares)
 * Processing Rule:
 * 1. Aggregated %  = ((Aggregated Granted options) / (Total shares Issused)) x 100
 * 2. Aggregated value  = (Aggregated Granted options) x (Closing price)
 * "3. If (Participant type =
 * ""Independent Non-executive Director"" |
 * ""Associate of Independent Non-executive Director"" |
 * ""Substantial Shareholder"" |
 * ""Associate of Substantial Shareholder""
 * ) then apply step 4. Otherwise apply step 5."
 * 4. if {((Aggregated %) < 0.1%) and (Aggregated value < HKD 5, 000, 000)} then return (Granted participant options). Otherwise return 0 + {Notification to "listed company"}
 * 5. if {(Aggregated %) < 1%} then return (Granted participant options). Otherwise return 0.
 *
 * @author 阮程
 * @date 2022/11/7
 */
@Data
public class GrantingInput2 extends GrantingInput {

    private ParticipantType participantType;
    private BigDecimal totalIssuedOptions;
    private BigDecimal grantedParticipantOptions;
    private BigDecimal aggregatedGrantedOptions;
    private BigDecimal closingPrice;

    @Override
    public ApiResult paramsValid() {
        if (participantType == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "participantType");
        }
        if (totalIssuedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "totalIssuedOptions");
        }
        if (grantedParticipantOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "grantedParticipantOptions");
        }
        if (aggregatedGrantedOptions == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "aggregatedGrantedOptions");
        }
        if (closingPrice == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "closingPrice");
        }
        if (StringUtils.isBlank(recipientID)) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "recipientID");
        }
        return ApiResult.success();
    }

    public ApiResult ruleCalculation() {
        BigDecimal data = new BigDecimal(0);
        BigDecimal data1 = aggregatedGrantedOptions.divide(totalIssuedOptions).multiply(new BigDecimal(100));
        BigDecimal data2 = aggregatedGrantedOptions.multiply(closingPrice);

        if (participantType.equals(ParticipantType.IndependentNonExecutiveDirector)
                || participantType.equals(ParticipantType.SubstantialShareholder)
                || participantType.equals(ParticipantType.AssociateOfIndependentNonExecutiveDirector)
                || participantType.equals(ParticipantType.AssociateOfSubstantialShareholder)
        ) {
            if (data1.doubleValue() < 0.1D && data2.intValue() < 5000000) {
                data = grantedParticipantOptions;
            } else {
                notificationListedCompany("notify_template/listed_company/1.txt");
            }
        } else {
            if (data1.doubleValue() < 1.0D) {
                data = grantedParticipantOptions;
            }
        }
        return ApiResult.success().setData(data);
    }
}
