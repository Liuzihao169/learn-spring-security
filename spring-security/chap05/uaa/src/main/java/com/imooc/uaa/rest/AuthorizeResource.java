package com.imooc.uaa.rest;

import com.imooc.uaa.domain.Auth;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.LoginDto;
import com.imooc.uaa.domain.dto.UserDto;
import com.imooc.uaa.exception.DuplicateProblem;
import com.imooc.uaa.service.UserService;
import com.imooc.uaa.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {

    private final UserService userService;
    private final MessageSource messageSource;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public void register(@Valid @RequestBody UserDto userDto, Locale locale) {
        if (userService.isUsernameExisted(userDto.getUsername())) {
            throw new DuplicateProblem("Exception.duplicate.username", messageSource, locale);
        }
        if (userService.isEmailExisted(userDto.getEmail())) {
            throw new DuplicateProblem("Exception.duplicate.email", messageSource, locale);
        }
        if (userService.isMobileExisted(userDto.getMobile())) {
            throw new DuplicateProblem("Exception.duplicate.mobile", messageSource, locale);
        }
        val user = User.builder()
            .username(userDto.getUsername())
            .name(userDto.getName())
            .email(userDto.getEmail())
            .mobile(userDto.getMobile())
            .password(userDto.getPassword())
            .build();
        userService.register(user);
    }

    @PostMapping("/token")
    public Auth login(@Valid @RequestBody LoginDto loginDTO) {
        return userService.login(loginDTO.getUsername(), loginDTO.getPassword());
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization, @RequestParam String refreshToken) {
        val PREFIX = "Bearer ";
        val accessToken = authorization.replace(PREFIX, "");
        if (jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateWithoutExpiration(accessToken)) {
            return new Auth(jwtUtil.buildAccessTokenWithRefreshToken(refreshToken), refreshToken);
        }
        throw new AccessDeniedException("Bad Credentials");
    }
}
