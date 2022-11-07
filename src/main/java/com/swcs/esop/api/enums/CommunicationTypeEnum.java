package com.swcs.esop.api.enums;

/**
 * @author 阮程
 * @date 2022/10/26
 */
public enum CommunicationTypeEnum {

    Email("Email"),
    SMS("SMS"),
    Wechat("Wechat"),
    Whatsapp("Whatsapp"),
    FrontendUI("Frontend UI");

    private String code;

    CommunicationTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static CommunicationTypeEnum getEnum(String code) {
        for (CommunicationTypeEnum e : CommunicationTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
