package com.imooc.uaa.rest;

import com.imooc.uaa.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecuredRestAPIRestTemplateIntTests {
    @Autowired
    private TestRestTemplate template;

    @Test
    public void givenAuthRequest_shouldSucceedWith200() throws Exception {
        ResponseEntity<User> result = template
            .withBasicAuth("user", "12345678")
            .getForEntity("/api/me", User.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
