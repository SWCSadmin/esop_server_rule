package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author 阮程
 * @date 2022/12/8
 */
@Data
public class KycInput implements BaseRuleInput {

    public static final String DATA_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private KycStatus kycStatus;
    private RiskRating riskRating;


    @Override
    public ApiResult paramsValid() {
        if (kycStatus == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "kycStatus");
        }
        if (riskRating == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "riskRating");
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult ruleCalculation() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        KycOutput output = new KycOutput();

        switch (kycStatus) {
            case PendingList​:
                calendar.add(Calendar.DATE, 10);
                break;
            case Approved​:
                switch (riskRating) {
                    case High​:
                        calendar.add(Calendar.DATE, 270);
                        break;
                    case Medium​:
                        calendar.add(Calendar.DATE, 360);
                        break;
                    case Low:
                        calendar.add(Calendar.DATE, 540);
                        break;
                }
                break;
            case Rejected​:
                break;
            case ConditionalApproval​:
                calendar.add(Calendar.DATE, 90);
                break;
            case Suspended:
                break;
        }

        output.setReminder(1);
        output.setNextKycReviewDate(calendar.getTime());
        return ApiResult.success().setData(output);
    }

}