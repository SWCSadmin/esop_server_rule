package com.swcs.esop.api.enums;

/**
 * @author 阮程
 * @date 2022/12/7
 */
public enum RiskRating {

    High​,
    Medium​,
    Low;

    public static RiskRating typeOf(int c) {
        RiskRating[] arr = RiskRating.values();
        for (int i = 0; i < arr.length; i++) {
            if (c == i + 1) {
                return arr[i];
            }
        }
        return null;
    }


}
