package com.springsecurity.demospringsecurity.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liuzihao
 * @create 2021-06-06-09:10
 *
 * 认证过滤器，这个每次请求都会经过该过滤器
 */
@Slf4j
public class AuthFilter extends BasicAuthenticationFilter {

    public AuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        log.info("BasicAuthenticationFilter...认证过滤器，进行认证处理");

        /**
         * 我们可以再此进行token的验证，然后获取授权信息，存储到
         *SecurityContextHolder#SecurityContext Authentication(authRequest);
         * 当中，那么登录时就不需要进行认证处理了
         */
        chain.doFilter(request, response);
    }
}
