package com.imooc.uaa.security.auth.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class RestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final UserCacheService userCacheService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try (InputStream is = request.getInputStream()) {
            val jsonNode = objectMapper.readTree(is);
            if (!jsonNode.has("username") || !jsonNode.has("password")) {
                throw new BadCredentialsException("用户名和密码不正确");
            }
            String username = jsonNode.get("username").textValue();
            String password = jsonNode.get("password").textValue();
            if (!jsonNode.has("mfaId")) {
                val user = (User) userDetailsService.loadUserByUsername(username);
                if (!user.isUsingMfa()) {
                    val authRequest = new UsernamePasswordAuthenticationToken(username, password);
                    setDetails(request, authRequest);
                    return this.getAuthenticationManager().authenticate(authRequest);
                }
                response.setHeader("X-Authenticate", "mfa,realm=" + userCacheService.cacheUser(user));
                throw new NeedMfaException("需要多因子认证");
            }
            if (!jsonNode.has("code")) {
                throw new BadCredentialsException("验证码不正确");
            }
            String code = jsonNode.get("code").textValue();
            String mfaId = jsonNode.get("mfaId").textValue();
            return userCacheService.verifyTotp(mfaId, code)
                .map(user -> {
                    val authRequest = new UsernamePasswordAuthenticationToken(username, password);
                    return this.getAuthenticationManager().authenticate(authRequest);
                })
                .orElseThrow(() -> new BadCredentialsException("验证码不正确或已过期"));

        } catch (IOException e) {
            throw new BadCredentialsException("没有找到用户名或密码参数");
        }

    }
}

