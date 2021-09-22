package com.imooc.webclient.config;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.function.Consumer;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public void configure(WebSecurity web) {
        web
            .ignoring()
            .antMatchers("/webjars/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authReq -> authReq
                .mvcMatchers("/authorize/**").permitAll()
                .anyRequest().authenticated())
//            .formLogin(form -> form
//                .loginPage("/login").failureUrl("/login-error").permitAll())
//            .logout(logout -> logout.logoutUrl("/perform_logout").logoutSuccessUrl("/login"))
//            .oauth2Client(oauth2Client -> oauth2Client
//                .authorizationCodeGrant(authorizationCodeGrant -> authorizationCodeGrant
//                    .authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository)) // 自定义 code 流程的请求
//                ))
            .oauth2Login()
            ;
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user_web_client")
            .password("password")
            .roles("USER")
            .build();
        return  new InMemoryUserDetailsManager(user);
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        val authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(authorizationRequestCustomizer());
        return  authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer.additionalParameters(params -> params.put("prompt", "consent")); // 添加一个参数
    }
}
