package com.imooc.uaa.security.auth.ldap;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class LDAPAuthServiceUnitTests {

    @MockBean
    private LDAPUserRepo userRepo;

    private LDAPAuthService ldapAuthService;

    @BeforeEach
    public void setup() {
        ldapAuthService = new LDAPAuthService(userRepo);
    }

    @Test
    public void givenUsernameAndPassword_ThenSearchSuccess() {
        val username = "zha";
        val expectedResult = Arrays.asList(
            LDAPUser.builder()
                .username("zhangsan")
                .password("1234")
                .build(),
            LDAPUser.builder()
                .username("ZHANGsi")
                .password("12345")
                .build()
        );
        given(userRepo.findByUsernameLikeIgnoreCase(username)).willReturn(expectedResult);
        val actualResult = ldapAuthService.search(username);
        assertEquals(expectedResult.size(), actualResult.size());
        assertEquals(expectedResult.get(0).getUsername(), actualResult.get(0));
        assertEquals(expectedResult.get(1).getUsername(), actualResult.get(1));
    }

    @Test
    public void giveUsernameAndPassword_ThenAuthenticateSuccess() {
        val username = "zhangsan";
        val password = "123";
        val expectedUser = LDAPUser.builder()
            .username(username)
            .password(password)
            .build();
        given(userRepo.findByUsernameAndPassword(username, password)).willReturn(Optional.of(expectedUser));
        val actualResult = ldapAuthService.authenticate(username, password);
        assertTrue(actualResult);
    }

    @Test
    public void giveWrongPassword_ThenAuthenticateFail() {
        val username = "zhangsan";
        val password = "1234";
        given(userRepo.findByUsernameAndPassword(username, password)).willReturn(Optional.empty());
        val actualResult = ldapAuthService.authenticate(username, password);
        assertFalse(actualResult);
    }
}
