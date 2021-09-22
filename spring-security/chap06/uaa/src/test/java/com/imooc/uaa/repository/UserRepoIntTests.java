package com.imooc.uaa.repository;

import com.imooc.uaa.domain.User;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        val user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("12345678"))
            .mobile("13012341234")
            .name("New User")
            .email("new_user@local.dev")
            .mfaKey(strKey)
            .build();
        testEntityManager.persist(user);
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
}
