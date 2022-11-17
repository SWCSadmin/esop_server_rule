package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.common.mvc.ApiResult;
import com.swcs.esop.api.enums.AnnouncementType;
import com.swcs.esop.api.enums.BlackoutPeriodType;
import com.swcs.esop.api.enums.Status;
import com.swcs.esop.api.enums.StockExchange;

import java.util.Calendar;
import java.util.Date;

/**
 * Blackout Periods
 * Input parameters:
 * 1. Exchange (e.g. HKEX or SSE) (Stock Exchange)
 * 2. Blackout period type is either Annual, Interim or Quarterly (Blackout Period Type)
 * 3. Result publication type (e.g. Publication, Advance, Warning) (Announcement Type)
 * 3. Financial Year End (FYE) or Half-year end (HYE) or Quarter end (QE) (Start Date)
 * 4. Date on publishing the company result (Publication Date)
 * <p>
 * <p>
 * <p>
 * Output:
 * 1. Return (Deadline Date, Blackout start Date, notification Date)
 * Processing Rule:
 * If (Announcement Type) == Publication then Goto Publication. If (Announcement Type) == Advance then Goto Advance. Otherwise Goto Warning.
 * Publication:
 * 1. If (Stock Exchange) == HKEX then execute step 2 to 4. If (Stock Exchange) == SSE execute step 5 to 7. If (Stock Exchange) == HKEX & SSE then execute step 8.
 * 2. If (Blackout Period Type) == "Annual" then Duration = 3 months
 * 3. If (Blackout Period Type) == "Interim" then Duration = 2 months
 * 4. If (Blackout Period Type) == "Quarterly" then Duration = 45 days
 * 5. If (Blackout Period Type) == "Annual" then Duration = 4 months
 * 6. If (Blackout Period Type) == "Interim" then Duration = 2 months
 * 7. If (Blackout Period Type) == "Quarterly" then Duration = 1 month
 * 8. If ((Stock Exchange) == HKEX & SSE) Duration is the smallest one between Step 2 to 7.
 * 9. Goto step 10
 * Advance:
 * 1. If (Stock Exchange) == SSE then execute step 2 to 4.
 * 2. If (Blackout Period Type) == "Annual" then Duration = 1 month
 * 3. If (Blackout Period Type) == "Interim" then Duration = 1 month
 * 4. If (Blackout Period Type) == "Quarterly" then Duration = 1 month
 * 5. Goto step 10
 * Warning:
 * 1. If (Stock Exchange) == SSE then execute step 2 to 4.
 * 2. If (Blackout Period Type) == "Annual" then Duration = 3 months
 * 3. If (Blackout Period Type) == "Interim" then Duration = 2 months
 * 4. If (Blackout Period Type) == "Quarterly" then Duration = 45 days
 * 5. Goto step 10
 * 10. Deadline Date = (Start Date) + Duration
 * 11. If (Deadline Date) > (start date of next quarter) then (Deadline Date) = (start date of next quarter)
 * 12. Return (Dealine Date)
 *
 * @author 阮程
 * @date 2022/11/17
 */
public class BlackoutPeriodsInput implements BaseRuleInput {

    private StockExchange stockExchange;
    private BlackoutPeriodType blackoutPeriodType;
    private AnnouncementType announcementType;
    private Date startDate;
    private Date publicationDate;

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
        if (startDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "startDate");
        }
        if (publicationDate == null) {
            return ApiResult.errorWithArgs(Status.MISSING_REQUIRED_PARAMS_ERROR, "publicationDate");
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult ruleCalculation() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(startDate);
        calendar2.add(Calendar.MONTH, 3);

        if (AnnouncementType.Publication.equals(announcementType)) {
            publication(calendar);
        } else if (AnnouncementType.Advance.equals(announcementType)) {
            advance(calendar);
        } else {
            warning(calendar);
        }
        // TODO: start date of next quarter 这个值待定什么意思
        if (calendar.getTime().getTime() > calendar2.getTime().getTime()) {
            return ApiResult.success().setData(calendar2.getTime());
        } else {
            return ApiResult.success().setData(calendar.getTime());
        }
    }

    private void publication(Calendar calendar) {
        if (StockExchange.HKEX.equals(stockExchange)) {
            if (BlackoutPeriodType.Annual.equals(stockExchange)) {
                calendar.add(Calendar.MONTH, 3);
            } else if (BlackoutPeriodType.Interim.equals(stockExchange)) {
                calendar.add(Calendar.MONTH, 2);
            } else {
                calendar.add(Calendar.DATE, 45);
            }
        } else if (StockExchange.SSE.equals(stockExchange)) {
            if (BlackoutPeriodType.Annual.equals(stockExchange)) {
                calendar.add(Calendar.MONTH, 4);
            } else if (BlackoutPeriodType.Interim.equals(stockExchange)) {
                calendar.add(Calendar.MONTH, 2);
            } else {
                calendar.add(Calendar.MONTH, 1);
            }
        } else {
            if (BlackoutPeriodType.Annual.equals(stockExchange)) {
                calendar.add(Calendar.MONTH, 3);
            } else if (BlackoutPeriodType.Interim.equals(stockExchange)) {
                calendar.add(Calendar.MONTH, 2);
            } else {
                calendar.add(Calendar.MONTH, 1);
            }
        }
    }

    private void advance(Calendar calendar) {
//        if (BlackoutPeriodType.Annual.equals(stockExchange)) {
//            calendar.add(Calendar.MONTH, 1);
//        } else if (BlackoutPeriodType.Interim.equals(stockExchange)) {
//            calendar.add(Calendar.MONTH, 1);
//        } else {
//            calendar.add(Calendar.MONTH, 1);
//        }
        calendar.add(Calendar.MONTH, 1);
    }

    private void warning(Calendar calendar) {
        if (BlackoutPeriodType.Annual.equals(stockExchange)) {
            calendar.add(Calendar.MONTH, 3);
        } else if (BlackoutPeriodType.Interim.equals(stockExchange)) {
            calendar.add(Calendar.MONTH, 2);
        } else {
            calendar.add(Calendar.DATE, 45);
        }
    }
}
