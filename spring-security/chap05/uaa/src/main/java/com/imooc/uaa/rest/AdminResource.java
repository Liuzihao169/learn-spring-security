package com.imooc.uaa.rest;

import com.imooc.uaa.domain.User;
import com.imooc.uaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminResource {

    private final UserService userService;

    @GetMapping("/users")
    List<User> findAll() {
        return userService.findAll();
    }
}
