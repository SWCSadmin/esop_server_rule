package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.IncentiveStatus;
import com.swcs.esop.api.enums.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 阮程
 * @date 2022/11/7
 */
@Data
public class LapsedIncentiveInput implements BaseRuleInput {


    private IncentiveStatus incentiveStatus;
    private Date offerExpiryDate;
    private Date exerciseEndDate;
    private BigDecimal shares;
    private Date vestingDate;   // 授予日期


    @Override
    public ApiResult paramsValid() {
        if (incentiveStatus == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "incentiveStatus");
        }
        if (incentiveStatus.equals(IncentiveStatus.GrantApproved​) && offerExpiryDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "offerExpiryDate");
        }
        if (incentiveStatus.equals(IncentiveStatus.Granted​) && vestingDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "vestingDate");
        }
        if (incentiveStatus.equals(IncentiveStatus.Vested​) && exerciseEndDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "exerciseEndDate");
        }
        return ApiResult.success();
    }

    /**
     * Output:
     * 1.Number of "Lapsed shares" = (shares)
     * Processing Rule:
     * "If {(((Incentive status) == ""Grant Approved"" ) and ((Current date) >= (Offer Expriy date)) or (Incentive status == ""Grant Rejected | Grant Lapsed | Grant Failed KYC | Grant Failed KPI | Failed KYC | Cancelled"") }
     * then return ""Lapsed shares"" = shares. Otherwise return ""Lapsed shares"" = 0;"
     * "If {(((Incentive status) == ""Granted"" ) and ((Current date) >= (Vesting date))) or (Incentive status == ""Vest Rejected | Vest Failed Payment | Vest Failed KYC | Cancelled"")}
     * then return ""Lapsed shares"" = shares. Otherwise return ""Lapsed shares"" = 0;"
     * "If {(((Incentive status) == ""Vested"" ) and ((Current date) >= (Exercise End Date))) or (Incentive status == ""Exercise Lapsed | Exercise Failed Payment | Cancelled"")}
     * then return ""Lapsed shares"" = shares. Otherwise return ""Lapsed shares"" = 0;"
     *
     * @return
     */
    public ApiResult ruleCalculation() {
        BigDecimal data = new BigDecimal(0);
        Date date = new Date();
        if ((incentiveStatus.equals(IncentiveStatus.GrantApproved​) && date.getTime() >= offerExpiryDate.getTime())
                || (
                incentiveStatus.equals(IncentiveStatus.GrantRejected​)
                        || incentiveStatus.equals(IncentiveStatus.GrantLapsed​)
                        || incentiveStatus.equals(IncentiveStatus.GrantFailedKYC​)
                        || incentiveStatus.equals(IncentiveStatus.GrantFailedKPI​)
                        || incentiveStatus.equals(IncentiveStatus.FailedKYC​)
                        || incentiveStatus.equals(IncentiveStatus.Cancelled​)
        )
        ) {
            data = shares;
        } else if ((incentiveStatus.equals(IncentiveStatus.Granted​) && date.getTime() >= vestingDate.getTime())
                || (
                incentiveStatus.equals(IncentiveStatus.VestRejected​)
                        || incentiveStatus.equals(IncentiveStatus.VestFailedPayment​)
                        || incentiveStatus.equals(IncentiveStatus.VestFailedKYC​)
                        || incentiveStatus.equals(IncentiveStatus.Cancelled​)
        )
        ) {
            data = shares;
        } else if ((incentiveStatus.equals(IncentiveStatus.Vested​) && date.getTime() >= exerciseEndDate.getTime())
                || (
                incentiveStatus.equals(IncentiveStatus.ExerciseLapsed​)
                        || incentiveStatus.equals(IncentiveStatus.ExerciseFailedPayment​)
                        || incentiveStatus.equals(IncentiveStatus.Cancelled​)
        )
        ) {
            data = shares;
        }
        return ApiResult.success().setData(data);
    }
}
