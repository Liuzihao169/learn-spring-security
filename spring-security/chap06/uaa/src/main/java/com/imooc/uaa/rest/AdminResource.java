package com.imooc.uaa.rest;

import com.imooc.uaa.domain.User;
import com.imooc.uaa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminResource {

    private final UserService userService;

    @GetMapping("/users")
    Page<User> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/validation/email")
    public boolean validateEmail(@RequestParam String email, @RequestParam String username) {
        return userService.isEmailExistedAndUsernameIsNot(email, username);
    }

    @GetMapping("/validation/mobile")
    public boolean validateMobile(@RequestParam String mobile, @RequestParam String username) {
        return userService.isMobileExistedAndUsernameIsNot(mobile, username);
    }
}
