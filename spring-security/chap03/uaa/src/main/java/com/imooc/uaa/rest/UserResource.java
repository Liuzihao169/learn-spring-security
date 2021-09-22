package com.imooc.uaa.rest;

import com.imooc.uaa.domain.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class UserResource {
    @GetMapping("/me")
    public User getProfile() {
        return User.builder()
            .name("张三")
            .username("zhangsan")
            .roles(Collections.singletonList("USER"))
            .build();
    }
}
