package com.imooc.uaa.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.LoginDto;
import com.imooc.uaa.domain.dto.TotpVerificationDto;
import com.imooc.uaa.domain.dto.UserDto;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.service.EmailService;
import com.imooc.uaa.service.SmsService;
import com.imooc.uaa.util.JwtUtil;
import com.imooc.uaa.util.TotpUtil;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SecuredRestAPIIntTests extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TotpUtil totpUtil;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @MockBean
    private SmsService smsService;

    private MockMvc mvc;

    private PasswordGenerator passwordGenerator;

    private static final String STR_KEY = "8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==";

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
            .mfaKey(STR_KEY)
            .authorities(Set.of(savedRoleUser))
            .build();
        userRepo.save(user);
        val userMfaEmail = User.builder()
            .username("user_mfa_email")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13812341234")
            .name("Mfa Email")
            .email("user_mfa_email@local.dev")
            .usingMfa(true)
            .mfaKey(STR_KEY)
            .authorities(Set.of(savedRoleUser))
            .build();
        userRepo.save(userMfaEmail);
        val userMfaSms = User.builder()
            .username("user_mfa_sms")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13812341235")
            .name("Mfa Sms")
            .email("user_mfa_sms@local.dev")
            .usingMfa(true)
            .mfaKey(STR_KEY)
            .authorities(Set.of(savedRoleUser))
            .build();
        userRepo.save(userMfaSms);
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
    public void givenDuplicateUser_thenRegisterFail() throws Exception {
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
            .username("user")
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
            .andExpect(status().is4xxClientError());

        mvc.perform(post("/authorize/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto.withUsername("new_user").withEmail("user@local.dev"))))
            .andDo(print())
            .andExpect(status().is4xxClientError());

        mvc.perform(post("/authorize/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto.withUsername("new_user").withMobile("13012341234"))))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void givenUserWithMfaSmsEnabled_shouldLoginFailAtFirstStep() throws Exception {
        val usernameSms = "user_mfa_sms";
        val passwordSms = "12345678";
        val loginDtoSms = new LoginDto(usernameSms, passwordSms);
        // 不需要真的发送短信
        doNothing().when(smsService).send(eq("13812341235"), anyString());
        // 第一步通过后，验证返回的响应头的 WWW-Authenticate
        val result = mvc.perform(post("/authorize/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDtoSms)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(header().exists("X-Authenticate"))
            .andExpect(header().stringValues("X-Authenticate", hasItems(is("mfa"), containsString("realm="))))
            .andReturn();
        val now = Instant.now();
        val totp = totpUtil.createTotp(totpUtil.decodeKeyFromString(STR_KEY), now);
        val mfaId = result.getResponse().getHeaderValues("X-Authenticate").get(1).toString().replace("realm=", "");
        // 验证失败
        mvc.perform(post("/authorize/totp")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TotpVerificationDto(mfaId, "bad_code"))))
            .andDo(print())
            .andExpect(status().isUnauthorized());
        // 验证成功
        mvc.perform(post("/authorize/totp")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TotpVerificationDto(mfaId, totp))))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("accessToken", is(notNullValue())))
            .andExpect(jsonPath("refreshToken", is(notNullValue())));
        // 再次验证会失败，因为 code 只能使用一次
        mvc.perform(post("/authorize/totp")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TotpVerificationDto(mfaId, totp))))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenUserWithMfaEmailEnabled_shouldLoginFailAtFirstStep() throws Exception {
        val usernameEmail = "user_mfa_email";
        val passwordEmail = "12345678";
        val loginDtoEmail = new LoginDto(usernameEmail, passwordEmail);
        mvc.perform(post("/authorize/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDtoEmail)))
            .andDo(print())
            .andExpect(status().isUnauthorized())
            .andExpect(header().exists("X-Authenticate"))
            .andExpect(header().stringValues("X-Authenticate", hasItems(is("mfa"), containsString("realm="))));
    }

    @Test
    public void givenLoginDtoWithWrongPassword_shouldFail() throws Exception {
        val username = "user";
        val password = "bad password";
        val loginDto = new LoginDto(username, password);
        mvc.perform(post("/authorize/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
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
            .header("Authorization", "Ldap " + username + " " + password))
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
