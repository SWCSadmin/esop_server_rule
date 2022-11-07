package com.swcs.esop.api.module.sender;

import com.swcs.esop.api.entity.Notification;
import com.swcs.esop.api.util.AppUtils;
import com.swcs.esop.api.util.NodeServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.Date;

/**
 * @author 阮程
 * @date 2018-12-10
 */
public class MailSender implements Sender {
    public static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private Notification notification;

    private String subject;
    private String content;
    private String[] to;
    private String[] cc;

    public MailSender(Notification notification) {
        this.notification = notification;
        this.subject = notification.getTopicTitle();
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
            String from;
            if (notification != null && StringUtils.isNotBlank(notification.getSenderName())) {
                from = notification.getSenderName() + "<" + AppUtils.getProperty("spring.mail.username") + ">";
            } else {
                from = AppUtils.getProperty("spring.mail.username");
            }
            message.setFrom(from);
            message.setTo(to);
            if (cc != null) {
                message.setCc(cc);
            }
            message.setSubject(subject);
            message.setText(content);
            message.setSentDate(new Date());
            javaMailSender.send(message);
            logger.info("email [{}] {} send success", this.subject, this.to);
            return true;
        } catch (Exception e) {
            logger.error("email send error\n{}", this.toString(), e);
            return false;
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("MailSender{");
        sb.append("subject='").append(subject).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", to=").append(to == null ? "null" : Arrays.asList(to).toString());
        sb.append(", cc=").append(cc == null ? "null" : Arrays.asList(cc).toString());
        sb.append('}');
        return sb.toString();
    }
}
