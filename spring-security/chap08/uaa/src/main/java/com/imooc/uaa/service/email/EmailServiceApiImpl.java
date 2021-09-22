package com.imooc.uaa.service.email;

import com.sendgrid.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@ConditionalOnProperty(prefix = "mooc.email-provider", name = "name", havingValue = "api")
@RequiredArgsConstructor
@Service
public class EmailServiceApiImpl implements EmailService {

    private final SendGrid sendGrid;

    @Override
    public void send(String email, String msg) {
        val from = new Email("service@imooc.com");
        val subject = "慕课网实战Spring Security 登录验证码";
        val to = new Email(email);
        val content = new Content("text/plain", "验证码为:" + msg);
        val mail = new Mail(from, subject, to, content);
        val request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() == 202) {
                log.info("邮件发送成功");
            } else {
                log.error(response.getBody());
            }
        } catch (IOException e) {
            log.error("请求发生异常 {}", e.getLocalizedMessage());
        }
    }
}
