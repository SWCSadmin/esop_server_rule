package com.swcs.esop.api.module.sender;

import com.swcs.esop.api.entity.Notification;

/**
 * @author 阮程
 * @date 2018-12-10
 */
public class SendFactory {

    public static Sender build(Notification notification) {
        switch (notification.getCommunicationType()) {
            case Email:
                return new MailSender(notification);
            case SMS:
            case Wechat:
            case Whatsapp:
            case FrontendUI:
            default:
                throw new SendException("Unsupported type: " + notification.getCommunicationType().getCode());
        }
    }
}
