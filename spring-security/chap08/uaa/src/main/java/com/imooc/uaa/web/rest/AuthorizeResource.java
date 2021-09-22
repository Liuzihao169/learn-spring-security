package com.imooc.uaa.web.rest;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.MfaType;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.RegisterDto;
import com.imooc.uaa.domain.dto.SendTotpDto;
import com.imooc.uaa.domain.dto.TotpVerificationDto;
import com.imooc.uaa.exception.BadCredentialProblem;
import com.imooc.uaa.exception.InvalidTotpProblem;
import com.imooc.uaa.service.UserCacheService;
import com.imooc.uaa.service.UserService;
import com.imooc.uaa.service.email.EmailService;
import com.imooc.uaa.service.sms.SmsService;
import com.imooc.uaa.service.validation.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {

    private final UserService userService;
    private final UserCacheService userCacheService;
    private final SmsService smsService;
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final UserValidationService userValidationService;

    @GetMapping("/validation/username")
    public boolean validateUsername(@RequestParam String username) {
        return userValidationService.isUsernameExisted(username);
    }

    @GetMapping("/validation/email")
    public boolean validateEmail(@RequestParam String email) {
        return userValidationService.isEmailExisted(email);
    }

    @GetMapping("/validation/mobile")
    public boolean validateMobile(@RequestParam String mobile) {
        return userValidationService.isMobileExisted(mobile);
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterDto registerDto) {
        userValidationService.validateUserUniqueFields(registerDto.getUsername(), registerDto.getEmail(), registerDto.getMobile());
        val user = User.builder()
            .username(registerDto.getUsername())
            .name(registerDto.getName())
            .email(registerDto.getEmail())
            .mobile(registerDto.getMobile())
            .password(registerDto.getPassword())
            .usingMfa(true)
            .enabled(false)
            .build();
        userService.register(user);
    }

    @PutMapping("/totp")
    public void sendTotp(@Valid @RequestBody SendTotpDto sendTotpDto) {
        userCacheService.retrieveUser(sendTotpDto.getMfaId())
            .flatMap(user -> userService.createTotp(user).map(code -> Pair.of(user, code)))
            .ifPresentOrElse(pair -> {
                log.debug("totp: {}", pair.getSecond());
                if (sendTotpDto.getMfaType() == MfaType.SMS) {
                    smsService.send(pair.getFirst().getMobile(), pair.getSecond());
                } else {
                    emailService.send(pair.getFirst().getEmail(), pair.getSecond());
                }
            }, () -> {
                throw new InvalidTotpProblem();
            });
    }

    @PostMapping("/totp")
    public void verifyTotp(@Valid @RequestBody TotpVerificationDto totpVerificationDto) {
        val result = userCacheService.verifyTotp(totpVerificationDto.getMfaId(), totpVerificationDto.getCode());
        if (result.isEmpty()) {
            throw new BadCredentialProblem();
        }
    }
}
