package com.imooc.uaa.repository;

import com.imooc.uaa.domain.QUser;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.querydsl.core.types.Predicate;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Map;

import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class UserRepoIntTests {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepo userRepo;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        val id = "bcrypt";
        val strKey = "8Uy+OZUaZur9WwcP0z+YxNy+QdsWbtfqA70GQMxMfLeisTd8Na6C7DkjhJWLrGyEyBsnEmmkza6iorytQRh7OQ==";
        passwordEncoder = new DelegatingPasswordEncoder(id, Map.of(
            id, new BCryptPasswordEncoder())
        );
        val role = Role.builder().roleName(ROLE_USER).displayName("用户").build();
        testEntityManager.persist(role);
        val user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13012341234")
            .name("New User")
            .email("new_user@local.dev")
            .roles(Collections.singleton(role))
            .mfaKey(strKey)
            .build();
        testEntityManager.persist(user);
    }

    @AfterEach
    public void clear() {
        testEntityManager.clear();
    }

    @Test
    public void givenUsernameAndPassword_shouldFindMatchedItem() {
        val optionalUser = userRepo.findOptionalByUsername("user");
        assertTrue(optionalUser.isPresent());
        assertTrue(passwordEncoder.matches("12345678", optionalUser.get().getPassword()));
    }

    @Test
    public void givenUsernameAndWrongPassword_shouldReturnEmpty() {
        val optionalUser = userRepo.findOptionalByUsername("user");
        assertTrue(optionalUser.isPresent());
        assertFalse(passwordEncoder.matches("12345", optionalUser.get().getPassword()));
    }

    @Test
    public void givenQueryDsl_thenQuerySuccess() {
        Predicate predicate = QUser.user.roles.any().roleName.contains("user".toUpperCase());
        assertEquals(1, userRepo.findAll(predicate, PageRequest.of(0, 20)).getTotalElements());
    }
}
