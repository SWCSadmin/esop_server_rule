package com.swcs.esop.api.enums;

/**
 * @author 阮程
 * @date 2022/12/21
 */
public enum WarningType {

    OngoingCourtCase,
    PastCourtCase,
    ReprimandFromRegulators,
    RestrictionNoticeIssuedByRegulators,
    InquiryFromRegulators,
    TakeoverByGovernment,
    FileForInsolvency,
    UnderMergersAndAcquisition,
    MajorCorporate,
    PEP,
    PEPDomestic,
    MajorTargetOfSocialMedia,
    InvolvedInBusinessWithHighRisk,
    OtherAdverseNews;

    public static WarningType typeOf(int c) {
        WarningType[] arr = WarningType.values();
        for (int i = 0; i < arr.length; i++) {
            if (c == i + 1) {
                return arr[i];
            }
        }
        return null;
    }

}
