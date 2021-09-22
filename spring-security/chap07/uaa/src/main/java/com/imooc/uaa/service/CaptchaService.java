package com.imooc.uaa.service;

import cn.leancloud.sms.AVCaptcha;
import cn.leancloud.sms.AVCaptchaDigest;
import cn.leancloud.sms.AVCaptchaOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CaptchaService {

    public AVCaptchaDigest requestCaptcha() {
        val option = new AVCaptchaOption();
        val stream = AVCaptcha.requestCaptchaInBackground(option);
        return stream.blockingFirst();
    }

    public Optional<String> verifyCaptcha(String code, String token) {
        val captcha = new AVCaptchaDigest();
        captcha.setCaptchaToken(token);
        val stream = AVCaptcha.verifyCaptchaCodeInBackground(code, captcha)
            .map(result -> Optional.of(result.getToken()))
            .onErrorReturnItem(Optional.empty());
        return stream.blockingFirst();
    }
}
