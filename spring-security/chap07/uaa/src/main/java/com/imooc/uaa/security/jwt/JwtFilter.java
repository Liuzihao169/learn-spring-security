package com.imooc.uaa.security.jwt;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.util.CollectionUtil;
import com.imooc.uaa.util.JwtUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用于 JWT Token 形式的请求过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (checkJWTToken(request)) {
            validateToken(request)
                .filter(claims -> claims.get("authorities") != null)
                .ifPresentOrElse(this::setUpSpringAuthentication, SecurityContextHolder::clearContext);
        }
        chain.doFilter(request, response);
    }

    /**
     * 解析 JWT 得到 Claims
     *
     * @param req HTTP 请求
     * @return JWT Claims
     */
    private Optional<Claims> validateToken(HttpServletRequest req) {
        String jwtToken = req.getHeader(appProperties.getJwt().getHeader()).replace(appProperties.getJwt().getPrefix(), "");
        try {
            return Optional.of(Jwts.parserBuilder().setSigningKey(jwtUtil.getKey()).build().parseClaimsJws(jwtToken).getBody());
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Error parsing jwt {}", e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    /**
     * 构造 Authentication
     *
     * @param claims JWT Claims
     */
    private void setUpSpringAuthentication(Claims claims) {
        Optional.of(claims.get("authorities"))
            .map(o -> {
                val rawList = CollectionUtil.convertObjectToList(o);
                val authorities = rawList.stream().map(String::valueOf).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
            })
            .ifPresentOrElse(
                auth -> SecurityContextHolder.getContext().setAuthentication(auth),
                SecurityContextHolder::clearContext);
    }

    /**
     * 检查 JWT Token 是否在 HTTP 报头中
     *
     * @param req HTTP 请求
     * @return 是否有 JWT Token
     */
    private boolean checkJWTToken(HttpServletRequest req) {
        String authenticationHeader = req.getHeader(appProperties.getJwt().getHeader());
        return authenticationHeader != null && authenticationHeader.startsWith(appProperties.getJwt().getPrefix());
    }
}
