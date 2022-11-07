package com.swcs.esop.api.enums;

/**
 * @author 阮程
 * @date 2022/11/4
 */
public enum IncentiveStatus {
    Incomplete​,
    PendingGrantApproval​,
    FailedKYC​,
    GrantApproved​,
    GrantFailedKYC​,
    GrantLapsed​,
    GrantAccepted​,
    GrantRejected​,
    GrantFailedKPI​,
    Granted​,
    VestFailedPayment​,
    VestFailedKYC​,
    VestRejected​,
    VestAccepted​,
    Vested​,
    VestedCustodian​,
    ExerciseLapsed​,
    ExerciseFailedPayment​,
    Exercised​,
    ExercisedCustodian​,
    Cancelled​;


    public ESOPStatus getESOPStatus() {
        switch (this) {
            case Incomplete​:
            case PendingGrantApproval​:
            case FailedKYC​:
                return ESOPStatus.NA;
            case GrantApproved​:
            case GrantFailedKYC​:
            case GrantLapsed​:
            case GrantAccepted​:
            case GrantRejected​:
            case GrantFailedKPI​:
            case Granted​:
                return ESOPStatus.Grant;
            case VestFailedPayment​:
            case VestFailedKYC​:
            case VestRejected​:
            case VestAccepted​:
            case Vested​:
            case VestedCustodian​:
                return ESOPStatus.Vest;
            case ExerciseLapsed​:
            case ExerciseFailedPayment​:
            case Exercised​:
            case ExercisedCustodian​:
                return ESOPStatus.Exercise;
            case Cancelled​:
                return ESOPStatus.Cancelled;
            default:
                return null;
        }
    }

    public boolean isContributionOrCostsRequired() {
        switch (this) {
            case VestFailedPayment​:
            case ExerciseFailedPayment​:
                return true;
            default:
                return false;
        }
    }
}
