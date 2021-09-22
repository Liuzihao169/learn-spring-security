package com.imooc.uaa.rest;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Auth;
import com.imooc.uaa.domain.MfaType;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.LoginDto;
import com.imooc.uaa.domain.dto.SendTotpDto;
import com.imooc.uaa.domain.dto.TotpVerificationDto;
import com.imooc.uaa.domain.dto.UserDto;
import com.imooc.uaa.exception.*;
import com.imooc.uaa.service.EmailService;
import com.imooc.uaa.service.SmsService;
import com.imooc.uaa.service.UserCacheService;
import com.imooc.uaa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {

    private final UserService userService;
    private final UserCacheService userCacheService;
    private final SmsService smsService;
    private final EmailService emailService;
    private final MessageSource messageSource;
    private final AppProperties appProperties;

    @GetMapping("/validation/username")
    public boolean validateUsername(@RequestParam String username) {
        return userService.isUsernameExisted(username);
    }

    @GetMapping("/validation/email")
    public boolean validateEmail(@RequestParam String email) {
        return userService.isEmailExisted(email);
    }

    @GetMapping("/validation/mobile")
    public boolean validateMobile(@RequestParam String mobile) {
        return userService.isMobileExisted(mobile);
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody UserDto userDto, Locale locale) {
        if (userService.isUsernameExisted(userDto.getUsername())) {
            throw new DuplicateProblem(messageSource.getMessage(
                "Exception.duplicate.title", null, locale),
                messageSource.getMessage("Exception.duplicate.username", null, locale));
        }
        if (userService.isEmailExisted(userDto.getEmail())) {
            throw new DuplicateProblem(messageSource.getMessage(
                "Exception.duplicate.title", null, locale),
                messageSource.getMessage("Exception.duplicate.email", null, locale));
        }
        if (userService.isMobileExisted(userDto.getMobile())) {
            throw new DuplicateProblem(messageSource.getMessage(
                "Exception.duplicate.title", null, locale),
                messageSource.getMessage("Exception.duplicate.mobile", null, locale));
        }
        val user = User.builder()
            .username(userDto.getUsername())
            .name(userDto.getName())
            .email(userDto.getEmail())
            .mobile(userDto.getMobile())
            .password(userDto.getPassword())
            .usingMfa(true)
            .enabled(false)
            .build();
        userService.register(user);
    }

    @PostMapping("/token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDTO) {
        return userService.findOptionalByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
            .map(user -> {
                userService.upgradePasswordEncodingIfNeeded(user, loginDTO.getPassword());
                if (!user.isEnabled()) {
                    throw new UserNotEnabledProblem();
                }
                if (!user.isAccountNonLocked()) {
                    throw new UserAccountLockedProblem();
                }
                if (!user.isAccountNonExpired()) {
                    throw new UserAccountExpiredProblem();
                }
                // 不使用多因子认证
                if (!user.isUsingMfa()) {
                    return ResponseEntity.ok().body(userService.login(user));
                }
                // 使用多因子认证
                val mfaId = userCacheService.cacheUser(user);
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header("X-Authenticate", "mfa", "realm=" + mfaId)
                    .build();
            })
            .orElseThrow(BadCredentialProblem::new);
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization, @RequestParam String refreshToken) {
        val accessToken = authorization.replace(appProperties.getJwt().getPrefix(), "");
        return userService.refreshToken(accessToken, refreshToken);
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
    public Auth verifyTotp(@Valid @RequestBody TotpVerificationDto totpVerificationDto) {
        return userCacheService.verifyTotp(totpVerificationDto.getMfaId(), totpVerificationDto.getCode())
            .map(User::getUsername)
            .flatMap(userService::findOptionalByUsername)
            .map(userService::loginWithTotp)
            .orElseThrow(InvalidTotpProblem::new);
    }
}
