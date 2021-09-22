package com.imooc.uaa.repository;

import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
public class RoleRepoIntTests {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    public void setup() {
        val perm_Read = Permission.builder().authority("READ").displayName("查询").build();
        val perm_Write = Permission.builder().authority("WRITE").displayName("写入").build();
        val perm_Admin = Permission.builder().authority("ADMIN").displayName("管理").build();
        val permRead = testEntityManager.persist(perm_Read);
        val permWrite = testEntityManager.persist(perm_Write);
        testEntityManager.persist(perm_Admin);
        val roleUser = Role.builder().roleName(ROLE_USER).displayName("用户").builtIn(true).permissions(Set.of(permRead)).build();
        testEntityManager.persist(roleUser);
        val role_Admin = Role.builder().roleName(ROLE_ADMIN).displayName("管理员").builtIn(true).permissions(Set.of(permRead, permWrite)).build();
        testEntityManager.persist(role_Admin);
    }

    @AfterEach
    public void clear() {
        testEntityManager.clear();
    }

    @Test
    public void givenRoleName_whenRoleExists_thenFindRoleSuccess() {
        val optionalRole = roleRepo.findOptionalByRoleName(ROLE_USER);
        assertTrue(optionalRole.isPresent());
    }
}
