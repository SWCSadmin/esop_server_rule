package com.swcs.esop.api.enums;

/**
 * @author 阮程
 * @date 2022/11/4
 */
public enum KpiStatus {
    Pass,
    Fail;

    public static KpiStatus typeOf(int c) {
        KpiStatus[] arr = KpiStatus.values();
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
