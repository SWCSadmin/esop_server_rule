package com.swcs.esop.api.module.excel;

import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.entity.KycParticipantInfo;
import com.swcs.esop.api.enums.KycStatus;
import com.swcs.esop.api.enums.RiskRating;
import com.swcs.esop.api.enums.WarningType;
import com.swcs.esop.api.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.List;

/**
 * @author 阮程
 * @date 2022/11/1
 */
public class KycParticipantInfoReadListener extends BaseReadListenerReturnStatus<KycParticipantInfo> {

    public KycParticipantInfoReadListener() {
        super(true, "participant_info");
    }

    @Override
    protected void dataValid(KycParticipantInfo o, List<String> errorList) {
        if (!o.primaryKeyIsBlank()) {
            List<KycParticipantInfo> list = listDbRecord(o);
            if (list.isEmpty()) {
                errorList.add("participant_id " + getMessage("IS_INVALID_VALUE"));
            }
        }

        /**
         * kyc 校验规则
         * 1， 假如 “KYC Status” == “Rejected”,  “Reject Justification” 字段必须选一个对应的值  (“Reject Justification” field input is required”)
         * 2， 假如 “Type of warning” == PEP 或者 PEP – Domestic (10/11), 字段 “Source of Wealth” and“Source of  Funds” 必须有值
         * 3， 字段 “Last KYC Reviewed Date”,自动填充，默认值是当前日期
         * 4， 假如 “Type of Warning” 有值, 并且 “KYC Risk Rating” = “High” 则 “KYC Remarks” 字段必须有值  (“KYC Remarks” field input is required”)
         * 5， "KYC Status" 和"KYC Risk Rating" 一起决定  “Next KYC Review Date"的值和发送提醒(reminder) 的次数。
         *  1）假如 "KYC Status" = "Pending List"  "Next KYC Review Date” = (当前日期) + 10
         *  2）假如 "KYC Status" = "Conditional Approval" 最多3次提醒 后 "KYC Status" = "Suspended"， "Next KYC Review Date” = (当前日期) + 90
         *  3）假如 "KYC Status" = "Suspended" 最多3次提醒后 "KYC Status" = "Rejected" ， "Next KYC Review Date” = (当前日期) + 90
         *  4）假如 "KYC Status" = "Approved"
         *      "KYC Risk Rating" = "High" ， "Next KYC Review Date” = (当前日期) + 270
         *      "KYC Risk Rating" = "Medium" ， "Next KYC Review Date” = (当前日期) + 360
         *      "KYC Risk Rating" = "Low" ， "Next KYC Review Date” = (当前日期) + 720
         *  5）假如 "KYC Status" = "Rejected" ， "Next KYC Review Date” = 当前日期
         */
        KycStatus kycStatus = KycStatus.typeOf(Integer.valueOf(o.getKyc_status()));
        RiskRating riskRating = RiskRating.typeOf(Integer.valueOf(o.getKyc_risk_level()));

        // 1.
        if (KycStatus.Rejected​.equals(kycStatus) && StringUtils.isBlank(o.getReject_justification())) {
            errorList.add("reject_justification " + getMessage("REQUIRED"));
        }
        // 2.
        String warning_type = o.getWarning_type();
        WarningType warningType = null;
        if (StringUtils.isNotBlank(warning_type)) {
            warningType = WarningType.typeOf(Integer.valueOf(warning_type));
            if (WarningType.PEP.equals(warningType) || WarningType.PEPDomestic.equals(warningType)) {
                String wealth_source_en = o.getWealth_source_en();
                String wealth_source_sc = o.getWealth_source_sc();
                String wealth_source_tc = o.getWealth_source_tc();
                if (StringUtils.isBlank(wealth_source_en)
                        && StringUtils.isBlank(wealth_source_sc)
                        && StringUtils.isBlank(wealth_source_tc)) {
                    errorList.add("wealth_source " + getMessage("REQUIRED"));
                }
                String funds_source_en = o.getFunds_source_en();
                String funds_source_sc = o.getFunds_source_sc();
                String funds_source_tc = o.getFunds_source_tc();
                if (StringUtils.isBlank(funds_source_en)
                        && StringUtils.isBlank(funds_source_sc)
                        && StringUtils.isBlank(funds_source_tc)) {
                    errorList.add("funds_source " + getMessage("REQUIRED"));
                }
            }
        }
        // 3.
        o.setLast_kyc_review_date(DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_YYYY_MM_DD));

        if (warningType != null) {
            // 4.
            if (RiskRating.High​.equals(riskRating)) {
                String kyc_remarks_en = o.getKyc_remarks_en();
                String kyc_remarks_sc = o.getKyc_remarks_sc();
                String kyc_remarks_tc = o.getKyc_remarks_tc();
                if (StringUtils.isBlank(kyc_remarks_en)
                        && StringUtils.isBlank(kyc_remarks_sc)
                        && StringUtils.isBlank(kyc_remarks_tc)) {
                    errorList.add("kyc_remarks " + getMessage("REQUIRED"));
                }
            }

            // 5.
            int days = 0;
            switch (kycStatus) {
                case PendingList​:
                    days = 10;
                    break;
                case ConditionalApproval​:
                    days = 90;
                    o.setReminder_count("3");
                    break;
                case Suspended:
                    days = 90;
                    o.setReminder_count("3");
                    break;
                case Approved​:
                    switch (riskRating) {
                        case High​:
                            days = 270;
                            break;
                        case Medium​:
                            days = 360;
                            break;
                        case Low:
                            days = 720;
                            break;
                    }
                    break;
                case Rejected​:
                    days = 0;
                    break;
            }

            Date nextKycReviewDate = days == 0 ? new Date() : DateUtil.addDays(new Date(), days);
            o.setNext_kyc_review_date(DateFormatUtils.format(nextKycReviewDate, Constants.DATE_FORMAT_YYYY_MM_DD));
        }
    }

}