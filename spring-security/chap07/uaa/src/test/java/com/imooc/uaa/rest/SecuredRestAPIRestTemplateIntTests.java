package com.imooc.uaa.rest;

import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.domain.dto.UserProfileDto;
import com.imooc.uaa.repository.PermissionRepo;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.util.JwtUtil;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Set;

import static com.imooc.uaa.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class SecuredRestAPIRestTemplateIntTests extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String STR_KEY = "8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==";

    @BeforeEach
    public void setup() {
        val permissionsUserRead = Permission.builder().displayName("查询用户").authority("USER_READ").build();
        val permissionsUserAdmin = Permission.builder().displayName("管理用户").authority("USER_ADMIN").build();
        userRepo.deleteAllInBatch();
        roleRepo.deleteAllInBatch();
        permissionRepo.deleteAllInBatch();
        val savedPermissionsUserRead = permissionRepo.save(permissionsUserRead);
        val savedPermissionsUserAdmin = permissionRepo.save(permissionsUserAdmin);
        val roleAdmin = Role.builder()
            .roleName(ROLE_ADMIN)
            .displayName("管理员")
            .permissions(Set.of(savedPermissionsUserRead, savedPermissionsUserAdmin))
            .build();
        val savedRoleAdmin = roleRepo.save(roleAdmin);

        val userWithRoleAdmin = User.builder()
            .username("user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13011111111")
            .name("New Admin")
            .email("user_admin@local.dev")
            .mfaKey(STR_KEY)
            .roles(Set.of(savedRoleAdmin))
            .build();
        userRepo.save(userWithRoleAdmin);
    }

    @Test
    public void givenAuthRequest_shouldSucceedWith200() {
        val username = "user";
        val authorities = Set.of(
            Permission.builder().authority("USER_ADMIN").build(),
            Permission.builder().authority("USER_READ").build());
        val roles = Set.of(
            Role.builder()
                .roleName(ROLE_USER)
                .permissions(Set.of(Permission.builder().authority("USER_READ").build()))
                .build(),
            Role.builder()
                .roleName(ROLE_ADMIN)
                .permissions(authorities)
                .build()
        );
        val user = User.builder()
            .username(username)
            .roles(roles)
            .build();
        val token = jwtUtil.createAccessToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        val request = new HttpEntity<>(headers);
        ResponseEntity<UserProfileDto> result = template
            .exchange("/api/me", HttpMethod.GET, request, UserProfileDto.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
