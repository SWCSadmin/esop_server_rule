package com.swcs.esop.api.entity;

import com.swcs.esop.api.enums.CommunicationTypeEnum;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ClassRelativeResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author 阮程
 * @date 2022/10/26
 */
@Data
public class Notification {

    private static final Logger logger = LoggerFactory.getLogger(Notification.class);

    /**
     * Possible method supported: Email or SMS or Wechat or Whatsapp or Frontend UI
     */
    private CommunicationTypeEnum communicationType;
    /**
     * ID is based on the communication type. Can be email, phone number, wechat ID, etc.
     */
    private String recipientID;
    /**
     * Recipient first and last names in English or Chinese
     */
    private String recipientName;
    /**
     * Subject header of the message in English or Chinese
     */
    private String topicTitle;
    /**
     * Message in English or Chinese
     */
    private String message;
    /**
     * Sender first and last names
     */
    private String senderName;
    /**
     * Sender email or phone number
     */
    private String senderContact;
    /**
     * Sender Job position in the company
     */
    private String senderJobPosition;

    public String loadTemplate() {
        String filePath = "notify_template" + File.separator + communicationType.toString().toLowerCase() + ".txt";
        return loadTemplate(filePath);
    }

    public String loadTemplate(String filePath) {
        filePath = "classpath:" + filePath;
        try {
            InputStream is = new ClassRelativeResourceLoader(Notification.class).getResource(filePath).getInputStream();
            String template = IOUtils.toString(is, StandardCharsets.UTF_8);
            return template.replace("{{recipientID}}", recipientID)
                    .replace("{{recipientName}}", recipientName)
                    .replace("{{topicTitle}}", topicTitle)
                    .replace("{{message}}", message)
                    .replace("{{senderName}}", senderName)
                    .replace("{{senderContact}}", senderContact)
                    .replace("{{senderJobPosition}}", senderJobPosition)
                    ;
        } catch (IOException e) {
            logger.error("load notify template error", e);
        }
        return message;
    }
}
