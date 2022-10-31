package com.swcs.esop.api.module.sender;

import com.swcs.esop.api.entity.Notification;
import com.swcs.esop.api.util.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

/**
 * @author 阮程
 * @date 2018-12-10
 */
public class MailSender implements Sender {
    public static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private String subject;
    private String content;
    private String[] to;
    private String[] cc;

    public MailSender(Notification notification) {
        this.subject = notification.getRecipientID();
        this.content = notification.loadTemplate();
        this.to = notification.getRecipientID().split(",");
    }

    public MailSender(String subject, String content, String[] to, String[] cc) {
        this.subject = subject;
        this.content = content;
        this.to = to;
        this.cc = cc;
    }

    @Override
    public boolean send() {
        try {
            JavaMailSender javaMailSender = AppUtils.getBean(JavaMailSender.class);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(AppUtils.getProperty("spring.mail.username"));
            message.setTo(to);
            if (cc != null) {
                message.setCc(cc);
            }
            message.setSubject(subject);
            message.setText(content);
            message.setSentDate(new Date());
            javaMailSender.send(message);
            logger.info("email send success");
            return true;
        } catch (Exception e) {
            logger.error("email send error", e);
            return false;
        }
    }
}
