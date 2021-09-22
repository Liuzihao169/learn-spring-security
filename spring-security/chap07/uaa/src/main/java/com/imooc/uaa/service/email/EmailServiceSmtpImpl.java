package com.imooc.uaa.service.email;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "mooc.email-provider", name = "name", havingValue = "smtp")
@RequiredArgsConstructor
@Service
public class EmailServiceSmtpImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Override
    public void send(String email, String msg) {
        val message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("service@imooc.com");
        message.setSubject("慕课网实战Spring Security 登录验证码");
        message.setText("验证码为:" + msg);
        emailSender.send(message);
    }
}
