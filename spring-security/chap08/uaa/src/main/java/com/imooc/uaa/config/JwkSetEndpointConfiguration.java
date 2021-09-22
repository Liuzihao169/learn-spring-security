package com.imooc.uaa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;

/**
 * Authorization Server 的安全配置
 * 需要配置以下 /.well-known/jwks.json 允许公开访问
 * 在存在多个 Security 配置的情况下，需要设置不同的顺序，@Order 是必须的
 */
@Order(1)
@Configuration
class JwkSetEndpointConfiguration extends AuthorizationServerSecurityConfiguration {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requestMatchers(req -> req.mvcMatchers("/.well-known/jwks.json"))
            .authorizeRequests(req -> req.mvcMatchers("/.well-known/jwks.json").permitAll());
    }
}
