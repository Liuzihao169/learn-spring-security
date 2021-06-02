package com.springsecurity.demospringsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class DemoSpringsecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringsecurityApplication.class, args);
    }

}
