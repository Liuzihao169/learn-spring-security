package com.imooc.uaa.web.rest;

import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.CreateOrUpdateRoleDto;
import com.imooc.uaa.domain.dto.CreateUserDto;
import com.imooc.uaa.domain.dto.UserProfileDto;
import com.imooc.uaa.repository.PermissionRepo;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.service.email.EmailService;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Set;

import static com.imooc.uaa.util.Constants.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminResourceIntTests extends BaseIntegrationTest {

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

    private MockMvc mvc;

    private User userWithRoleUser;

    private User userWithRoleAdmin;

    private User userMfaSms;

    private Role roleUser;

    private Role roleNotBuiltIn;

    private static final String STR_KEY = "8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==";

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        val permissionsUserRead = Permission.builder().displayName("????????????").authority("USER_READ").build();
        val permissionsUserAdmin = Permission.builder().displayName("????????????").authority("USER_ADMIN").build();
        userRepo.deleteAllInBatch();
        roleRepo.deleteAllInBatch();
        permissionRepo.deleteAllInBatch();
        val savedPermissionsUserRead = permissionRepo.save(permissionsUserRead);
        val savedPermissionsUserAdmin = permissionRepo.save(permissionsUserAdmin);
        roleUser = Role.builder()
            .roleName(ROLE_USER)
            .displayName("??????")
            .permissions(Collections.singleton(savedPermissionsUserRead))
            .build();
        val roleAdmin = Role.builder()
            .roleName(ROLE_ADMIN)
            .displayName("?????????")
            .permissions(Set.of(savedPermissionsUserRead, savedPermissionsUserAdmin))
            .build();
        roleNotBuiltIn = Role.builder()
            .roleName("ROLE_NOT_BUILTIN")
            .displayName("???????????????")
            .build();
        val roleStaff = Role.builder()
            .roleName(ROLE_STAFF)
            .displayName("??????????????????")
            .permissions(Set.of(savedPermissionsUserRead, savedPermissionsUserAdmin))
            .build();
        val savedRoleUser = roleRepo.save(roleUser);
        val savedRoleAdmin = roleRepo.save(roleAdmin);
        roleRepo.save(roleNotBuiltIn);
        val savedRoleStaff = roleRepo.save(roleStaff);
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
            .roles(Set.of(savedRoleAdmin, savedRoleStaff))
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
     * ???????????? QueryDsl ??????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenAdminRole_shouldFindWithPredicateSuccess() throws Exception {
        mvc.perform(get("/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .queryParam("username", "user_admin")
            .queryParam("roles.roleName", "ROLE_STAFF"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));

        mvc.perform(get("/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .queryParam("roles.roleName", "ROLE_USER"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)));
    }

    /**
     * ????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenCreateUserDto_thenCreateUserSuccess() throws Exception {
        val newName = "New Name";
        val newMobile = "13512341234";
        val newEmail = "new_email@local.dev";
        val newUsername = "new_user";
        doNothing().when(emailService).send(anyString(), anyString());
        mvc.perform(post("/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(CreateUserDto.builder().username(newUsername).name(newName).mobile(newMobile).email(newEmail).build())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is(newUsername)))
            .andExpect(jsonPath("$.name", is(newName)))
            .andExpect(jsonPath("$.mobile", is(newMobile)))
            .andExpect(jsonPath("$.email", is(newEmail)));
    }

    /**
     * ???????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenAdminRole_whenUpdatingUser_shouldSuccess() throws Exception {
        val name = "new user";
        val mobile = "13512341234";
        val email = "new@local.dev";
        mvc.perform(put("/admin/users/{username}", userMfaSms.getUsername())
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
     * ????????????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenUser_thenToggleUserEnabledSuccess() throws Exception {
        mvc.perform(put("/admin/users/{username}/enabled", userWithRoleUser.getUsername())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.enabled", is(false)));
    }

    /**
     * ????????????????????????????????????
     * ????????????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenUser_whenAuthSelf_thenToggleUserEnabledFail() throws Exception {
        mvc.perform(put("/admin/users/{username}/enabled", userWithRoleAdmin.getUsername())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    /**
     * ???????????????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenUsername_thenGetUserAvailableRolesSuccess() throws Exception {
        mvc.perform(get("/admin/users/{username}/roles/available", userWithRoleAdmin.getUsername())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * ??????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenUsernameAndRoleIds_thenUpdateUserRolesSuccess() throws Exception {
        val roleNew1 = Role.builder()
            .roleName("ROLE_NEW_1")
            .displayName("?????????1")
            .build();
        val roleNew1Saved = roleRepo.save(roleNew1);
        val roleNew2 = Role.builder()
            .roleName("ROLE_NEW_2")
            .displayName("?????????2")
            .build();
        val roleNew2Saved = roleRepo.save(roleNew2);
        mvc.perform(put("/admin/users/{username}/roles", userWithRoleUser.getUsername())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Set.of(roleNew1Saved.getId(), roleNew2Saved.getId()))))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * ??????????????????????????????
     * ?????????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenUsernameAndRoleIds_whenAuthSelf_thenUpdateUserRolesFail() throws Exception {
        val roleNew1 = Role.builder()
            .roleName("ROLE_NEW_1")
            .displayName("?????????1")
            .build();
        val roleNew1Saved = roleRepo.save(roleNew1);
        val roleNew2 = Role.builder()
            .roleName("ROLE_NEW_2")
            .displayName("?????????2")
            .build();
        val roleNew2Saved = roleRepo.save(roleNew2);
        mvc.perform(put("/admin/users/{username}/roles", userWithRoleAdmin.getUsername())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Set.of(roleNew1Saved.getId(), roleNew2Saved.getId()))))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    /**
     * ????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenAuthRequest_thenFindAllRolesSuccess() throws Exception {
        mvc.perform(get("/admin/roles")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(is(4)))
            .andExpect(jsonPath("$.content.[*].roleName", hasItem(roleUser.getRoleName())));
    }

    /**
     * ????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenRoleNameAndDisplayName_thenCreateRoleSuccess() throws Exception {
        val roleName = "ROLE_NEW";
        val displayName = "????????????";
        mvc.perform(post("/admin/roles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(CreateOrUpdateRoleDto.builder().roleName(roleName).displayName(displayName).build())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.roleName", is(roleName)))
            .andExpect(jsonPath("$.displayName", is(displayName)));
    }

    /**
     * ????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenRoleNameAndDisplayName_thenUpdateRoleSuccess() throws Exception {
        val roleName = "ROLE_NEW";
        val displayName = "????????????";
        mvc.perform(put("/admin/roles/{roleId}", roleUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(CreateOrUpdateRoleDto.builder().roleName(roleName).displayName(displayName).build())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(roleUser.getId().intValue())))
            .andExpect(jsonPath("$.roleName", is(roleName)))
            .andExpect(jsonPath("$.displayName", is(displayName)));
    }

    /**
     * ????????????????????????
     * ?????????????????????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenRoleId_whenDeleteBuiltIn_thenFail() throws Exception {
        mvc.perform(delete("/admin/roles/{roleId}", roleUser.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    /**
     * ????????????????????????
     * ??????????????????????????????????????????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenRoleId_whenDeleteNotBuiltIn_thenSuccess() throws Exception {
        mvc.perform(delete("/admin/roles/{roleId}", roleNotBuiltIn.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }

    /**
     * ???????????????????????? /admin/** ??????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_STAFF})
    @Test
    public void givenAuthRequestWithoutAdminRole_shouldFail() throws Exception {
        mvc.perform(get("/admin/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    /**
     * ??????????????????????????? /admin/** ??????
     *
     * @throws Exception ??????
     */
    @WithMockUser(username = "user_admin", authorities = {ROLE_ADMIN, ROLE_STAFF})
    @Test
    public void givenJWTRequestWithAdminRole_shouldSuccessWith200() throws Exception {
        mvc.perform(get("/admin/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
