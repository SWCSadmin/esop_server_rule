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
    GrantEmailOffered​,
    GrantFailedKYC​,
    GrantLapsed​,
    GrantAccepted​,
    GrantRejected​,
    GrantFailedKPI​,
    Granted​,
    VestEmailOffered,
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

    public boolean isContributionOrCostsRequired() {
        switch (this) {
            case VestFailedPayment​:
            case ExerciseFailedPayment​:
                return true;
            default:
                return false;
        }
    }

    public static IncentiveStatus typeOf(int c) {
        IncentiveStatus[] arr = IncentiveStatus.values();
        for (int i = 0; i < arr.length; i++) {
            if (c == i + 1) {
                return arr[i];
            }
        }
        return null;
    }

    public int intValue() {
        return this.ordinal() + 1;
    }

    public ESOPState getESOPStae() {
        ESOPState esopState = null;
        switch (this) {
            case Incomplete​:
            case PendingGrantApproval​:
            case FailedKYC​:
                esopState = ESOPState.NA;
                break;
            case GrantApproved​:
            case GrantFailedKYC​:
            case GrantLapsed​:
            case GrantAccepted​:
            case GrantRejected​:
            case GrantFailedKPI​:
            case Granted​:
                esopState = ESOPState.Grant;
                break;
            case VestFailedPayment​:
            case VestFailedKYC​:
            case VestRejected​:
            case VestAccepted​:
            case Vested​:
            case VestedCustodian​:
                esopState = ESOPState.Vest;
                break;
            case ExerciseLapsed​:
            case ExerciseFailedPayment​:
            case Exercised​:
            case ExercisedCustodian​:
                esopState = ESOPState.Exercise;
                break;
            case Cancelled​:
                esopState = ESOPState.Cancelled;
                break;
        }
        return esopState;
    }

}
