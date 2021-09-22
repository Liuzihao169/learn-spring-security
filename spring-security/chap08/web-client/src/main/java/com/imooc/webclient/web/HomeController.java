package com.imooc.webclient.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return login();
    }

    @GetMapping("/oauth2_login")
    public String getLoginPage(Model model) {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
        Iterable<ClientRegistration> clientRegistrations;
        clientRegistrations = (InMemoryClientRegistrationRepository) clientRegistrationRepository;
        clientRegistrations.forEach(registration -> oauth2AuthenticationUrls
            .put(registration.getClientName(), "oauth2/authorization/" + registration.getRegistrationId())
        );
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "oauth_login";
    }
}
