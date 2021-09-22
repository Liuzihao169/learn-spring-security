package com.imooc.uaa.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/authorize")
public class AuthorizeResource {
    @GetMapping(value="greeting")
    public String sayHello() {
        return "hello world";
    }
}
