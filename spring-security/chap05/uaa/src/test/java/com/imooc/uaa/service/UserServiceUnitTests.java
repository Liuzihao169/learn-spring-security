package com.imooc.uaa.service;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.util.JwtUtil;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class UserServiceUnitTests {

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private RoleRepo roleRepo;

    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    public void setup() {
        val appProperties = new AppProperties();
        jwtUtil = new JwtUtil(appProperties);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepo, roleRepo, passwordEncoder, jwtUtil);
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
        given(roleRepo.findOptionalByAuthority("ROLE_USER"))
            .willReturn(Optional.of(Role.builder().id(1L).authority("ROLE_USER").build()));
        given(userRepo.save(any(User.class)))
            .willReturn(user.withId(1L));
        val saved = userService.register(user);
        assertEquals(1L, saved.getId());
    }

    @Test
    public void givenUsernameAndPassword_ThenLoginSuccess() {
        val username = "zhangsan";
        val password = "password";
        val role = Role.builder().id(1L).authority("ROLE_USER").build();
        val user = User.builder().username(username).password(passwordEncoder.encode(password)).authorities(Set.of(role)).build();
        given(userRepo.findOptionalByUsername(username))
            .willReturn(Optional.of(user));
        val jwt = userService.login(username, password);
        val expectedJwt = jwtUtil.createAccessToken(user);
        assertEquals(expectedJwt, jwt.getAccessToken());
    }

    @Test
    public void givenUsernameAndWrongPassword_ThenLoginThrowAccessDeniedException() {
        val username = "zhangsan";
        val password = "password";
        val wrongPassword = "wrong";
        val role = Role.builder().id(1L).authority("ROLE_USER").build();
        val user = User.builder().username(username).password(passwordEncoder.encode(password)).authorities(Set.of(role)).build();
        given(userRepo.findOptionalByUsername(username))
            .willReturn(Optional.of(user));
        assertThrows(AccessDeniedException.class, () -> userService.login(username, wrongPassword));
    }

    @Test
    public void givenWrongUsername_ThenLoginThrowAccessDeniedException() {
        val username = "zhangsan";
        val password = "password";
        given(userRepo.findOptionalByUsername(username))
            .willReturn(Optional.empty());
        assertThrows(AccessDeniedException.class, () -> userService.login(username, password));
    }
}
