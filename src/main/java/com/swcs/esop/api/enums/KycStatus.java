package com.swcs.esop.api.enums;

/**
 * @author 阮程
 * @date 2022/12/7
 */
public enum KycStatus {

    PendingList​,
    Approved​,
    Rejected​,
    ConditionalApproval​,
    Suspended;

    public static KycStatus typeOf(int c) {
        KycStatus[] arr = KycStatus.values();
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
}
