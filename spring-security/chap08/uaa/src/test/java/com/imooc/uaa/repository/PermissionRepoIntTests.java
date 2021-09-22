package com.imooc.uaa.repository;

import com.imooc.uaa.config.QueryDslConfig;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@DataJpaTest
public class PermissionRepoIntTests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private RoleRepo roleRepo;

    private Role roleUser;

    private Role roleAdmin;

    private Permission permRead;

    private Permission permWrite;

    @BeforeEach
    public void setup() {
        val perm_Read = Permission.builder().authority("READ").displayName("查询").build();
        val perm_Write = Permission.builder().authority("WRITE").displayName("写入").build();
        val perm_Admin = Permission.builder().authority("ADMIN").displayName("管理").build();
        permRead = testEntityManager.persist(perm_Read);
        permWrite = testEntityManager.persist(perm_Write);
        testEntityManager.persist(perm_Admin);
        roleUser = Role.builder().roleName(ROLE_USER).displayName("用户").builtIn(true).permissions(Set.of(permRead)).build();
        testEntityManager.persist(roleUser);
        roleAdmin = Role.builder().roleName(ROLE_ADMIN).displayName("管理员").builtIn(true).permissions(Set.of(permRead, permWrite)).build();
        testEntityManager.persist(roleAdmin);
    }

    @AfterEach
    public void clear() {
        testEntityManager.clear();
    }

    @Test
    public void givenRoleName_shouldFindAvailablePermissions() {
        val permsUser = permissionRepo.findAvailablePermissions(roleUser.getId());
        assertEquals(2, permsUser.size());
        val permsAdmin = permissionRepo.findAvailablePermissions(roleAdmin.getId());
        assertEquals(1, permsAdmin.size());
    }

    @Test
    public void givenPermission_shouldCreateSuccess() {
        val perm_Read = Permission.builder().authority("DELETE").displayName("删除").build();
        val saved = permissionRepo.save(perm_Read);
        assertEquals(perm_Read.getAuthority(), saved.getAuthority());
        assertNotNull(perm_Read.getId());
    }

    @Test
    public void givenPermission_whenExistsName_thenCountByAuthorityAndIdNotShouldSuccess() {
        permissionRepo.findOptionalByAuthority("READ")
            .ifPresent(permission -> {
                assertTrue(permissionRepo.countByAuthorityAndIdNot("WRITE", permission.getId()) > 0);
            });
    }

    @Test
    public void givenIds_thenFindByIdInShouldSuccess() {
        val result = permissionRepo.findByIdIn(Set.of(permRead.getId(), permWrite.getId()));
        assertEquals(2, result.size());
        roleUser.setPermissions(result);
        val saved = roleRepo.save(roleUser);
        assertNotNull(saved);
    }

//    @Test
//    public void givenQueryDsl_thenQuerySuccess() {
//        Predicate predicate = QPermission.permission.authority.contains("READ");
//        assertEquals(1, permissionRepo.findAll(predicate).spliterator().estimateSize());
//        QPermission permission = QPermission.permission;
//        QRole roles = new QRole("roles");
//        JPAQuery<Permission> query = jpaQueryFactory
//            .select(permission)
//            .from(permission)
//            .innerJoin(permission.roles, roles);
//
//        permissionRepo.findAll(query, PageRequest.of(0, 20)) ;
//    }
}
