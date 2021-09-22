package com.imooc.webclient.web;

import com.imooc.webclient.domain.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RequiredArgsConstructor
@Controller
public class GrantTypeController {

    @Value("${todos.base-uri}")
    private String todosBaseUri;

    private final WebClient webClient;

    @GetMapping(value = "/authorize", params = "grant_type=authorization_code")
    public String authorization_code_grant(Model model) {
        Todo[] todos = retrieveTodosByWebClient("web-client-auth-code");
        model.addAttribute("todos", todos);
        return "index";
    }

    @GetMapping("/authorized")		// authorization_code 流程中定义的重定向 URL
    public String authorized(Model model) {
        Todo[] todos = retrieveTodosByWebClient("web-client-auth-code");
        model.addAttribute("todos", todos);
        return "index";
    }

    @GetMapping(value = "/authorize", params = "grant_type=client_credentials")
    public String client_credentials_grant(Model model) {
        Todo[] todos = retrieveTodosByWebClient("web-client-client-credentials");
        model.addAttribute("todos", todos);
        return "index";
    }

    @PostMapping(value = "/authorize", params = "grant_type=password")
    public String password_grant(Model model) {
        Todo[] todos = retrieveTodosByWebClient("web-client-password");
        model.addAttribute("todos", todos);
        return "index";
    }

    private Todo[] retrieveTodosByWebClient(String clientRegistrationId) {
        return this.webClient
            .get()
            .uri(this.todosBaseUri)
            .attributes(clientRegistrationId(clientRegistrationId))
            .retrieve()
            .bodyToMono(Todo[].class)
            .block();
    }
}
