package com.imooc.uaa.rest;

import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.LoginDto;
import com.imooc.uaa.domain.dto.RegisterDto;
import com.imooc.uaa.domain.dto.TotpVerificationDto;
import com.imooc.uaa.repository.PermissionRepo;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.service.email.EmailService;
import com.imooc.uaa.service.sms.SmsService;
import com.imooc.uaa.util.CryptoUtil;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthorizeResourceIntTests extends BaseIntegrationTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TotpUtil totpUtil;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    @MockBean
    private SmsService smsService;

    private MockMvc mvc;

    private PasswordGenerator passwordGenerator;

    private User userWithRoleUser;

    private User userMfaEmail;

    private User userMfaSms;

    private static final String STR_KEY = "8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        val permissionsUserRead = Permission.builder().displayName("查询用户").authority("USER_READ").build();
        val permissionsUserAdmin = Permission.builder().displayName("管理用户").authority("USER_ADMIN").build();
        passwordGenerator = new PasswordGenerator();
        userRepo.deleteAllInBatch();
        roleRepo.deleteAllInBatch();
        permissionRepo.deleteAllInBatch();
        val savedPermissionsUserRead = permissionRepo.save(permissionsUserRead);
        val savedPermissionsUserAdmin = permissionRepo.save(permissionsUserAdmin);
        val roleUser = Role.builder()
            .roleName(ROLE_USER)
            .displayName("用户")
            .permissions(Collections.singleton(savedPermissionsUserRead))
            .build();
        val roleAdmin = Role.builder()
            .roleName(ROLE_ADMIN)
            .displayName("管理员")
            .permissions(Set.of(savedPermissionsUserRead, savedPermissionsUserAdmin))
            .build();
        val savedRoleUser = roleRepo.save(roleUser);
        val savedRoleAdmin = roleRepo.save(roleAdmin);

        userWithRoleUser = User.builder()
            .username("user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13012341234")
            .name("New User")
            .email("user@local.dev")
            .mfaKey(STR_KEY)
            .roles(Set.of(savedRoleUser))
            .build();
        userRepo.save(userWithRoleUser);

        userMfaEmail = User.builder()
            .username("user_mfa_email")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13812341234")
            .name("Mfa Email")
            .email("user_mfa_email@local.dev")
            .usingMfa(true)
            .mfaKey(STR_KEY)
            .roles(Set.of(savedRoleUser))
            .build();
        userRepo.save(userMfaEmail);

        userMfaSms = User.builder()
            .username("user_mfa_sms")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13812341235")
            .name("Mfa Sms")
            .email("user_mfa_sms@local.dev")
            .usingMfa(true)
            .mfaKey(STR_KEY)
            .roles(Set.of(savedRoleUser))
            .build();
        userRepo.save(userMfaSms);
    }

    @Test
    public void givenUserDto_thenRegisterSuccess() throws Exception {
        // 使用 Passay 提供的 PasswordGenerator 生成符合规则的密码
        val password = cryptoUtil.buildDefaultPassword();
        val userDto = RegisterDto.builder()
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
        val userDto = RegisterDto.builder()
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
        val usernameSms = userMfaSms.getUsername();
        val passwordSms = "12345678";
        val loginDtoSms = new LoginDto(usernameSms, passwordSms);
        // 不需要真的发送短信
        doNothing().when(smsService).send(eq("13812341235"), anyString());
        // 第一步通过后，验证返回的响应头的 X-Authenticate
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
        val usernameEmail = userMfaEmail.getUsername();
        val passwordEmail = "12345678";
        val loginDtoEmail = new LoginDto(usernameEmail, passwordEmail);
        doNothing().when(emailService).send(eq("user_mfa_email@local.dev"), anyString());
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
    public void givenAccessTokenAndRefreshToken_shouldReturnNewAccessToken() throws Exception {
        val past = Instant.now().minusNanos(appProperties.getJwt().getAccessTokenExpireTime()).toEpochMilli();
        val token = jwtUtil.createJWTToken(userWithRoleUser, past);
        val refreshToken = jwtUtil.createRefreshToken(userWithRoleUser);
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
}
