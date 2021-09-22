package com.imooc.uaa.security.auth.ldap;

import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LDAPAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

    public LDAPAuthorizationFilter(final RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        val AUTH_HEADER = "Authorization";
        val AUTH_PREFIX = "Ldap ";
        val authValue = request.getHeader(AUTH_HEADER);
        if (authValue == null || !authValue.startsWith(AUTH_PREFIX)) {
            return null; // 没有发现认证报头，就交给其他 filter
        }
        val token = authValue.substring(5).split(" ");
        if (token.length != 2) {
            throw new BadCredentialsException("Bad Credentials");
        }
        val authToken = new LDAPAuthorizationToken(token[0], token[1]);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        // 返回一个 Authentication 令牌，交给 AuthenticationProvider 处理
        return authToken;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        attemptAuthentication((HttpServletRequest) req, (HttpServletResponse) res);
        chain.doFilter(req, res);
    }
}
