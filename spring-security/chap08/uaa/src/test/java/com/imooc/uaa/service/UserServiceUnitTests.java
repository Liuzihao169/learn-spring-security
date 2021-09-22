package com.imooc.uaa.service;

import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.util.TotpUtil;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Optional;

import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class UserServiceUnitTests {

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private RoleRepo roleRepo;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    public void setup() {
        val idForEncode = "bcrypt";
        // 要支持的多种编码器
        val encoders = Map.of(
            idForEncode, new BCryptPasswordEncoder(),
            "SHA-1", new MessageDigestPasswordEncoder("SHA-1")
        );
        passwordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        TotpUtil totpUtil = new TotpUtil();
        userService = new UserService(userRepo, roleRepo, passwordEncoder, totpUtil);
    }

    @Test
    public void givenUsernameAndPassword_shouldFindUser() {
        val user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13012341234")
            .name("New User")
            .email("new_user@local.dev")
            .enabled(true)
            .build();
        val username = "user";
        given(userRepo.findOptionalByUsername(username))
            .willReturn(Optional.of(user));
        val optionalUser = userService.findOptionalByUsernameAndPassword("user", "12345678");
        assertTrue(optionalUser.isPresent());
        val emptyUser = userService.findOptionalByUsernameAndPassword("user", "12345");
        assertTrue(emptyUser.isEmpty());
    }

    @Test
    public void givenUser_ThenRegisterSuccess() {
        val user = User.builder()
            .username("new_user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13012341234")
            .name("New User")
            .email("new_user@local.dev")
            .build();
        given(roleRepo.findOptionalByRoleName(ROLE_USER))
            .willReturn(Optional.of(Role.builder().id(1L).roleName(ROLE_USER).build()));
        given(userRepo.save(any(User.class)))
            .willReturn(user.withId(1L));
        val saved = userService.register(user);
        assertEquals(1L, saved.getId());
    }

    @Test
    public void givenPasswordEncodedInOldFormat_thenUpgradeEncodingSuccess() {
        val oldPasswordHash = "{SHA-1}7ce0359f12857f2a90c7de465f40a95f01cb5da9";
        val rawPassword = "abcd1234";
        assertTrue(passwordEncoder.matches(rawPassword, oldPasswordHash));
        assertTrue(passwordEncoder.upgradeEncoding(oldPasswordHash));
        val newPasswordHash = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, newPasswordHash));
    }
}
