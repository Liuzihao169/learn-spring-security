package com.imooc.uaa.web.rest;

import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.UserProfileDto;
import com.imooc.uaa.repository.PermissionRepo;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.util.Constants;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecuredRestAPIIntTests extends BaseIntegrationTest {


    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mvc;

    private User userWithRoleUser;

    private User userWithRoleAdmin;

    private User userMfaSms;

    private static final String STR_KEY = "8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        val permissionsUserRead = Permission.builder().displayName("查询用户").authority(Constants.AUTHORITY_USER_READ).build();
        val permissionsUserAdmin = Permission.builder().displayName("管理用户").authority(Constants.AUTHORITY_USER_ADMIN).build();
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

        userWithRoleAdmin = User.builder()
            .username("user_admin")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13011111111")
            .name("New Admin")
            .email("user_admin@local.dev")
            .mfaKey(STR_KEY)
            .roles(Set.of(savedRoleAdmin))
            .build();
        userRepo.save(userWithRoleAdmin);

        User userMfaEmail = User.builder()
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

    /**
     * 测试使用 JWT token 访问
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user")
    @Test
    public void givenAuthRequest_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/api/me")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * 测试 SecurityConfig 中的 access("hasRole('ADMIN') or @userService.checkUsername(authentication, #username)")
     * 使用管理员角色访问别人的资源时是成功的
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user")
    @Test
    public void givenAdminRole_whenAccessUserResource_shouldSuccess() throws Exception {
        mvc.perform(get("/api/users/user")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * 测试 SecurityConfig 中的 access("hasRole('ADMIN') or @userService.checkUsername(authentication, #username)")
     * 当访问别人的资源时是失败的
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user")
    @Test
    public void givenUserRole_whenAccessOtherUserResource_shouldFail() throws Exception {
        mvc.perform(get("/api/users/other")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    /**
     * 测试 SecurityConfig 中的 access("hasRole('ADMIN') or @userService.checkUsername(authentication, #username)")
     * 当访问自己的资源时是成功的
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user")
    @Test
    public void givenAuthRequest_whenAccessSelfInfo_shouldSuccess() throws Exception {
        mvc.perform(get("/api/users/user")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * 测试 POST /api/me
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user")
    @Test
    public void givenUserRole_whenSavingProfile_shouldSuccess() throws Exception {
        val name = "new user";
        val mobile = "13512341234";
        val email = "new@local.dev";
        mvc.perform(post("/api/me")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(UserProfileDto.builder()
                .name(name)
                .mobile(mobile)
                .email(email)
                .build())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is(name)))
            .andExpect(jsonPath("mobile", is(mobile)))
            .andExpect(jsonPath("email", is(email)));
    }

    /**
     * 测试 @PostAuthorize("returnObject.username == authentication.name")
     * 查询自己的信息应该成功
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user", authorities = {"USER_READ"})
    @Test
    public void givenUserRole_whenQueryUserByEmail_shouldSuccess() throws Exception {
        mvc.perform(get("/api/users/by-email/{email}", "user@local.dev")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * 测试 @PostAuthorize("returnObject.username == authentication.name")
     * 查询别人的应该失败
     *
     * @throws Exception 异常
     */
    @WithMockUser(username = "user")
    @Test
    public void givenUserRole_whenQueryOthersEmail_shouldFail() throws Exception {
        mvc.perform(get("/api/users/by-email/{email}", "user_mfa_email@local.dev")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }
}
