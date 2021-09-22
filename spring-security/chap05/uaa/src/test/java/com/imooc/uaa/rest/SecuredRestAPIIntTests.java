package com.imooc.uaa.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.LoginDto;
import com.imooc.uaa.domain.dto.UserDto;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.util.JwtUtil;
import com.unboundid.util.Base64;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecuredRestAPIIntTests extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mvc;

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        passwordGenerator = new PasswordGenerator();
        userRepo.deleteAllInBatch();
        roleRepo.deleteAllInBatch();
        val roleUser = Role.builder()
            .authority(ROLE_USER)
            .build();
        val roleAdmin = Role.builder()
            .authority(ROLE_ADMIN)
            .build();
        val savedRoleUser = roleRepo.save(roleUser);
        roleRepo.save(roleAdmin);

        val user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13012341234")
            .name("New User")
            .email("user@local.dev")
            .authorities(Set.of(savedRoleUser))
            .build();
        userRepo.save(user);
    }

    @Test
    public void givenUserDto_thenRegisterSuccess() throws Exception {
        // 使用 Passay 提供的 PasswordGenerator 生成符合规则的密码
        val password = passwordGenerator.generatePassword(8,
            // 至少有一个大写字母
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            // 至少有一个小写字母
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            // 至少有一个数字
            new CharacterRule(EnglishCharacterData.Digit, 1),
            // 至少有一个特殊字符
            new CharacterRule(EnglishCharacterData.Special, 1));
        val userDto = UserDto.builder()
            .username("new_user")
            .password(password)
            .matchingPassword(password)
            .mobile("13912341234")
            .name("New User")
            .email("new_user@local.dev")
            .build();

        mvc.perform(post("/authorize/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void givenLoginDto_shouldReturnJwt() throws Exception {
        val username = "user";
        val password = "12345678";
        val loginDto = new LoginDto(username, password);
        mvc.perform(post("/authorize/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void givenAuthRequest_shouldSucceedWith200() throws Exception {
        val username = "user";
        val authorities = Set.of(
            Role.builder()
                .authority(ROLE_USER)
                .build(),
            Role.builder()
                .authority(ROLE_ADMIN)
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        val token = jwtUtil.createAccessToken(user);
        mvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void givenBadCredential_shouldFail() throws Exception {
        val token = "bad credentials";
        mvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void givenAccessTokenAndRefreshToken_shouldReturnNewAccessToken() throws Exception {
        val username = "user";
        val authorities = Set.of(
            Role.builder()
                .authority(ROLE_USER)
                .build(),
            Role.builder()
                .authority(ROLE_ADMIN)
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        val past = Instant.now().minusNanos(appProperties.getJwt().getAccessTokenExpireTime()).toEpochMilli();
        val token = jwtUtil.createJWTToken(user, past);
        val refreshToken = jwtUtil.createRefreshToken(user);
        mvc.perform(post("/authorize/token/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .param("refreshToken", refreshToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("accessToken", is(notNullValue())))
            .andExpect(jsonPath("refreshToken", is(notNullValue())))
            .andExpect(jsonPath("accessToken", not(token)))
            .andExpect(jsonPath("refreshToken", is(refreshToken)));
    }

    @Test
    public void givenRefreshToken_whenAccessSecuredApi_shouldFail() throws Exception {
        val username = "user";
        val authorities = Set.of(
            Role.builder()
                .authority(ROLE_USER)
                .build(),
            Role.builder()
                .authority(ROLE_ADMIN)
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        val refreshToken = jwtUtil.createRefreshToken(user);
        mvc.perform(post("/api/me")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + refreshToken))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void givenAuthRequestWithoutAdminRole_shouldFail() throws Exception {
        val username = "wangwu";
        val authorities = Set.of(
            Role.builder()
                .authority(ROLE_USER)
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        val token = jwtUtil.createAccessToken(user);
        mvc.perform(get("/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void givenAuthRequestWithAdminRole_shouldSuccessWith200() throws Exception {
        val username = "user";
        val authorities = Set.of(
            Role.builder()
                .authority(ROLE_USER)
                .build(),
            Role.builder()
                .authority(ROLE_ADMIN)
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        val token = jwtUtil.createAccessToken(user);
        mvc.perform(get("/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "externaluser", password = "pass")
    @Test
    public void givenExternalUser_shouldSuccessWith200() throws Exception {
        mvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "externaluser", password = "pass1")
    @Test
    public void givenExternalUser_shouldFailWith402() throws Exception {
        mvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void givenJWTRequestWithAdminRole_shouldSuccessWith200() throws Exception {
        val username = "user";
        val authorities = Set.of(
            Role.builder()
                .authority("ROLE_USER")
                .build(),
            Role.builder()
                .authority("ROLE_ADMIN")
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        val token = jwtUtil.createAccessToken(user);
        mvc.perform(get("/admin/users").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void givenLDAPUsernameAndPassword_shouldSuccessWith200() throws Exception {
        val username = "zhaoliu";
        val password = "123";
        mvc.perform(get("/api/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Basic " + Base64.encode(username + ":" + password)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void givenLDAPWrongPassword_shouldFail() throws Exception {
        val username = "zhaoliu";
        val password = "1234";
        mvc.perform(get("/api/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Ldap " + username + " " + password))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }
}
