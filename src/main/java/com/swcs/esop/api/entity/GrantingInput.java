package com.swcs.esop.api.entity;

import com.swcs.esop.api.common.Constants;
import com.swcs.esop.api.common.base.BaseRuleInput;
import com.swcs.esop.api.config.async.AsyncTask;
import com.swcs.esop.api.enums.CommunicationTypeEnum;
import com.swcs.esop.api.util.AppUtils;

/**
 * @author 阮程
 * @date 2022/11/16
 */
public abstract class GrantingInput implements BaseRuleInput {

    protected void notificationListedCompany(String recipientId, String templatePath) {
        AsyncTask asyncTask = AppUtils.getBean(AsyncTask.class);
        Notification notification = new Notification();
        notification.setCommunicationType(CommunicationTypeEnum.Email);
        notification.setTopicTitle("Employee Share Award Scheme Notice of Granting Limitation");
        notification.setRecipientID(recipientId);
        notification.setRecipientName("Participant");
        notification.setSenderContact(Constants.EMAIL_DEFAULT_SENDER_CONTACT);
        notification.setSenderJobPosition(Constants.EMAIL_DEFAULT_SENDER_JOB_POSITION);
        notification.setSenderName(Constants.EMAIL_DEFAULT_SENDER_NAME);
        notification.setMessage(notification.loadTemplate(templatePath));
        asyncTask.notify(notification);
    }
}
