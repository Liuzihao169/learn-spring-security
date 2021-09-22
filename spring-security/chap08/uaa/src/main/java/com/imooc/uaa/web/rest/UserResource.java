package com.imooc.uaa.web.rest;

import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.UserProfileDto;
import com.imooc.uaa.service.UserService;
import com.imooc.uaa.util.SecurityUtil;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@SecurityScheme(
    name = "bearerToken",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserResource {

    private final UserService userService;

    /**
     * 用户自己的档案
     *
     * @return 用户档案
     */
    @GetMapping("/me")
    public UserProfileDto getProfile() {
        return userService.findOptionalByUsername(SecurityUtil.getCurrentLogin())
            .map(user -> UserProfileDto
                .builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .build())
            .orElseThrow();
    }

    @PostMapping("/me")
    public User saveProfile(@RequestBody UserProfileDto userProfileDto, Principal principal) {
        return userService.findOptionalByUsername(principal.getName())
            .map(user -> userService.saveUser(user
                .withName(userProfileDto.getName())
                .withEmail(userProfileDto.getEmail())
                .withMobile(userProfileDto.getMobile())))
            .orElseThrow();
    }

    @GetMapping("/principal")
    public String getCurrentPrincipalName(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/users/{username}")
    public String getCurrentUsername(@PathVariable String username) {
        return username;
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @GetMapping("/users/by-email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.findOptionalByEmail(email).orElseThrow();
    }
}
