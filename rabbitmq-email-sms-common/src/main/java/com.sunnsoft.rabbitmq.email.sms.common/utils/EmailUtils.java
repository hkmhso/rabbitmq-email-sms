package com.sunnsoft.rabbitmq.email.sms.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @ClassName EmailUtils
 * @Description TODO
 * @Author HKM
 * @Date 2020/11/24 19:11
 * @Version 1.0
 **/
@Component
public class EmailUtils {

    private static JavaMailSender javaMailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        if(EmailUtils.javaMailSender == null) {
            EmailUtils.javaMailSender = javaMailSender;
        }
    }

    public static JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }

    //发送邮件
    public static void sendSimpleMail(String toEmail, String fromEmail,String userName) throws Exception {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("蚂蚁课堂|每特教育 新学员提醒");
        message.setText("祝贺您,成为了我们" + userName + ",学员!");
        javaMailSender.send(message);
    }

}
