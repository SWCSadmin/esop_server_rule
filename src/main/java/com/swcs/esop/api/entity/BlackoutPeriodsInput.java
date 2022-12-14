package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.AnnouncementType;
import com.swcs.esop.api.enums.BlackoutPeriodType;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.enums.StockExchange;
import com.swcs.esop.api.util.DateUtil;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;

/**
 * Blackout Periods
 * Input parameters:
 * 1. Exchange (e.g. H or A or Both) (Stock Type)
 * 2. Blackout period type is either Annual, Interim or Quarterly (Blackout Period Type)
 * 3. Result publication type (i.e. Publication, Advance, Warning) (Announcement Type)
 * 3. Financial Year End (FYE) or Half-year end (HYE) or Quarter end (QE) (Finance End Date)
 * 4. Start date for next Financial Quarter (Next Financial Start Date)
 * 4. Date on publishing the company result (Publication Date)
 * 5. Notice period to notify (e.g. HKEX/SSE) and/or directors and senior management. Default is 1 day (Notice Period)
 * <p>
 * Output:
 * 1. Return (Notice Date, Grant Prohibition Date, Exercise Prohibition Date, Blackout start Date, Blackout End Date,  Deadline Date)
 * Processing Rule:
 * If (Announcement Type) == Publication then Goto Publication. If (Announcement Type) == Advance then Goto Advance. Otherwise Goto Warning.
 * Publication:
 * 1. If (Stock Type) == H then execute step 2 to 4. If (Stock Type) == A execute step 5 to 7. If (Stock Type) == H & A then execute step 8.
 * 2. If (Blackout Period Type) == "Annual" then Duration = 90 days (3 mths); Blackout = 60 days. If from step 1 then Goto step 10
 * 3. If (Blackout Period Type) == "Interim" then Duration = 60 days (2 mths); Blackout = 30 days. If from step 1 then Goto step 10
 * 4. If (Blackout Period Type) == "Quarterly" then Duration = 45 days; Blackout = 30 days. If from step 1 then Goto step 10
 * 5. If (Blackout Period Type) == "Annual" then Duration = 120 days (4 mths); Blackout = 30 days. If from step 1 then Goto step 10
 * 6. If (Blackout Period Type) == "Interim" then Duration = 60 days (2 mths); Blackout = 30 days. If from step 1 then Goto step 10
 * 7. If (Blackout Period Type) == "Quarterly" then Duration = 30 days (1 mth); Blackout = 30 days. If from step 1 then Goto step 10
 * 8. If ((Stock Type) == H & A) Pick the smallest Duration; largest Blackout between Step 2 to 7.
 * 9. Goto step 10
 * Advance:
 * 1. If (Stock Type) == A then continue. Otherwise Goto 10.
 * 2. If (Blackout Period Type) == "Annual" then Duration = 30 days (1 mth); Blackout = 10 days.
 * 3. If (Blackout Period Type) == "Interim" then Duration = 30 days (1 mth); Blackout = 10 days.
 * 4. If (Blackout Period Type) == "Quarterly" then Duration = 30 days (1 mth); Blackout = 10 days.
 * 5. Goto step 10
 * Warning:
 * 1. If (Stock Type) == A then continue. Otherwise Goto 10.
 * 2. If (Blackout Period Type) == "Annual" then Duration = 30 days (1 mth); Blackout = 10 days.
 * 3. If (Blackout Period Type) == "Interim" then Duration = 30 days (1 mth); Blackout = 10 days.
 * 4. If (Blackout Period Type) == "Quarterly" then Duration = 30 days (1 mth); Blackout = 10 days.
 * 5. Goto step 10
 * 10. Deadline Date = (Finance End Date) + Duration
 * 11. (Blackout End Date) = (Publication Date)
 * 12. If (Deadline Date) > (Next Financial End Date) then (Deadline Date) = (Next Financial End Date)
 * 13. (Blackout Start Date) = Later{((Finance End Date) + 1) or ((Publication Date) - Blackout)}
 * 14. (Notice Date) = (Blackout Start Date) - (Notice Period)
 * 15. If (Stock Type == Both) then (Grant Prohibition Date) = Earlier {((Publication Date) - 30 days)) or ((Deadline Date) - 30 days) or (Blackout Start Date)}
 * 16. If (Stock Type == H ) then (Grant Prohibition Date) = Earlier {((Publication Date) - 30 days)) or ((Deadline Date) - 30 days)}
 * 17. If (Stock Type == A) then (Grant Prohibition Date) = (Blackout Start Date)
 * 18. If (Stock Type == Both) then (Exercise Prohibition Date) = (Blackout Start Date)
 * 19. If (Stock Type == H) then (Exercise Prohibition Date) = (Blackout End Date)
 * 20. If (Stock Type == A) then (Exercise Prohibition Date) = (Blackout Start Date)
 * 21. Return (Notice Date); (Grant Prohibition Date); (Exercise Prohibition Date); (Blackout Start Date); (Blackout End Date); (Dealine Date)
 *
 * @author ??????
 * @date 2022/11/17
 */
@Data
public class BlackoutPeriodsInput implements BaseRuleInput {

    private StockExchange stockExchange;
    private BlackoutPeriodType blackoutPeriodType;
    private AnnouncementType announcementType;
    private Date financeEndDate;
    private Date nextFinancialEndDate;
    private Date publicationDate;
    private Integer noticePeriod = 1;

    private int duration = 0;
    private int blackout = 0;

    @Override
    public ApiResult paramsValid() {
        if (stockExchange == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "stockExchange");
        }
        if (blackoutPeriodType == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "blackoutPeriodType");
        }
        if (announcementType == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "announcementType");
        }
        if (financeEndDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "financeEndDate");
        }
        if (nextFinancialEndDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "nextFinancialEndDate");
        }
        if (publicationDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "publicationDate");
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult ruleCalculation() {
        if (AnnouncementType.Publication.equals(announcementType)) {
            if (StockExchange.HKEX.equals(stockExchange)) {
                if (BlackoutPeriodType.Annual.equals(blackoutPeriodType)) {
                    duration = 90;
                    blackout = 60;
                } else if (BlackoutPeriodType.Interim.equals(blackoutPeriodType)) {
                    duration = 60;
                    blackout = 30;
                } else {
                    duration = 45;
                    blackout = 30;
                }
            } else if (StockExchange.SSE.equals(stockExchange)) {
                if (BlackoutPeriodType.Annual.equals(blackoutPeriodType)) {
                    duration = 120;
                    blackout = 30;
                } else if (BlackoutPeriodType.Interim.equals(blackoutPeriodType)) {
                    duration = 60;
                    blackout = 30;
                } else {
                    duration = 30;
                    blackout = 30;
                }
            } else {
                if (BlackoutPeriodType.Annual.equals(blackoutPeriodType)) {
                    duration = 90;
                    blackout = 60;
                } else if (BlackoutPeriodType.Interim.equals(blackoutPeriodType)) {
                    duration = 60;
                    blackout = 30;
                } else {
                    duration = 30;
                    blackout = 30;
                }
            }
        } else if (AnnouncementType.Advance.equals(announcementType)) {
            if (StockExchange.SSE.equals(stockExchange)) {
                duration = 30;
                blackout = 10;
            }
        } else {
            if (StockExchange.SSE.equals(stockExchange)) {
                duration = 30;
                blackout = 10;
            }
        }

        Date deadlineDate = DateUtil.addDays(financeEndDate, duration);
        Date blackoutEndDate = new Date(publicationDate.getTime());

        if (deadlineDate.getTime() > nextFinancialEndDate.getTime()) {
            deadlineDate = new Date(nextFinancialEndDate.getTime());
        }
        Date blackoutStartDate = DateUtil.getLaterDate(DateUtil.addDays(financeEndDate, 1), DateUtil.addDays(publicationDate, -blackout));
        Date noticeDate = DateUtil.addDays(blackoutStartDate, -noticePeriod);
        Date grantProhibitionDate;
        if (StockExchange.HKEX_SSE.equals(stockExchange)) {
            grantProhibitionDate = DateUtil.getEarlierDate(DateUtil.addDays(publicationDate, -30), DateUtil.addDays(deadlineDate, -30));
            grantProhibitionDate = DateUtil.getEarlierDate(grantProhibitionDate, blackoutStartDate);
        } else if (StockExchange.HKEX.equals(stockExchange)) {
            grantProhibitionDate = DateUtil.getEarlierDate(DateUtil.addDays(publicationDate, -30), DateUtil.addDays(deadlineDate, -30));
        } else {
            grantProhibitionDate = new Date(blackoutStartDate.getTime());
        }
        Date exerciseProhibitionDate;
        if (StockExchange.HKEX_SSE.equals(stockExchange)) {
            exerciseProhibitionDate = new Date(blackoutStartDate.getTime());
        } else if (StockExchange.HKEX.equals(stockExchange)) {
            exerciseProhibitionDate = new Date(blackoutEndDate.getTime());
        } else {
            exerciseProhibitionDate = new Date(blackoutStartDate.getTime());
        }

        BlackoutPeriodsOutput output = new BlackoutPeriodsOutput();
        output.setBlackoutEndDate(blackoutEndDate);
        output.setBlackoutStartDate(blackoutStartDate);
        output.setDeadlineDate(deadlineDate);
        output.setExerciseProhibitionDate(exerciseProhibitionDate);
        output.setGrantProhibitionDate(grantProhibitionDate);
        output.setNoticeDate(noticeDate);
        return ApiResult.success().setData(output);
    }

}
