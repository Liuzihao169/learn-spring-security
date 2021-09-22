package com.imooc.uaa.rest;

import com.imooc.uaa.common.BaseIntegrationTest;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.util.JwtUtil;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecuredRestAPIRestTemplateIntTests extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void givenAuthRequest_shouldSucceedWith200() {
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        val request = new HttpEntity<>(headers);
        ResponseEntity<String> result = template
            .exchange("/api/me", HttpMethod.GET, request, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
