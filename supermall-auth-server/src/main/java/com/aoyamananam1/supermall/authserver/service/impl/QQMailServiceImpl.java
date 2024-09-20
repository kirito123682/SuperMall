package com.aoyamananam1.supermall.authserver.service.impl;

import com.aoyamananam1.supermall.authserver.service.QQMailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class QQMailServiceImpl implements QQMailService {

    @Autowired
    private JavaMailSenderImpl sender;

    @Value("${spring.mail.username}")
    private String name;

    @Override
    public void sendText(String name, String from, String to, String subject, String context) {
        try {
            //支持复杂类型
            MimeMessageHelper helper = new MimeMessageHelper(sender.createMimeMessage(), true);
            //邮件发件人
            helper.setFrom(new InternetAddress(name + "<" +  from + ">"));
            //收件人
            helper.setTo(new InternetAddress(to));
            //邮件主题
            helper.setSubject(subject);
            //邮件内容
            helper.setText(context);
            //发送时间
            helper.setSentDate(new Date());
            //发送
            sender.send(helper.getMimeMessage());
        }catch (Exception e){
            throw new RuntimeException("mail send failed..", e);
        }
    }
}
