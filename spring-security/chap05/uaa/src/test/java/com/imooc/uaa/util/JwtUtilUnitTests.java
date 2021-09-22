package com.imooc.uaa.util;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import io.jsonwebtoken.Jwts;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static com.imooc.uaa.util.Constants.ROLE_ADMIN;
import static com.imooc.uaa.util.Constants.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class JwtUtilUnitTests {

    private JwtUtil jwtUtil;
    private AppProperties appProperties;

    @BeforeEach
    public void setup() {
        appProperties = new AppProperties();
        jwtUtil = new JwtUtil(appProperties);
    }

    @Test
    public void givenUserDetails_thenCreateTokenSuccess() {
        val username = "user";
        val authorities = Set.of(
            Role.builder()
                .authority(ROLE_USER)
                .build(),
            Role.builder()
                .authority(ROLE_ADMIN)
                .build()
        );
        val user = User.builder()
            .username(username)
            .authorities(authorities)
            .build();
        // 创建 jwt
        val token = jwtUtil.createAccessToken(user);
        // 解析
        val parsedClaims = Jwts.parserBuilder().setSigningKey(JwtUtil.key).build().parseClaimsJws(token).getBody();
        // subject 和 username 应该相同
        assertEquals(username, parsedClaims.getSubject());
        // 解析后的角色列表
        val parsedAuthorities = CollectionUtil.convertObjectToList(parsedClaims.get("authorities"));
        // 将原始的 Role 转换为字符串的角色名称列表
        val expectedAuthorities = authorities.stream().map(Role::getAuthority).collect(Collectors.toList());
        assertEquals(expectedAuthorities, parsedAuthorities);
        val refreshToken = jwtUtil.createRefreshToken(user);
        val parsedClaimsFromRefreshToken = jwtUtil.parseClaims(refreshToken, JwtUtil.refreshKey);
        assertTrue(parsedClaimsFromRefreshToken.isPresent());
        assertTrue(parsedClaimsFromRefreshToken.get().getExpiration().getTime() < System.currentTimeMillis() + appProperties.getJwt().getRefreshTokenExpireTime());
        assertTrue(parsedClaimsFromRefreshToken.get().getExpiration().getTime() > System.currentTimeMillis() + appProperties.getJwt().getRefreshTokenExpireTime() - 1000L);
        val accessTokenWithRefreshToken = jwtUtil.buildAccessTokenWithRefreshToken(refreshToken);
        val parsedClaimsFromAccessToken = jwtUtil.parseClaims(accessTokenWithRefreshToken, JwtUtil.key);
        assertTrue(parsedClaimsFromAccessToken.isPresent());
        assertTrue(parsedClaimsFromAccessToken.get().getExpiration().getTime() < System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime());
        assertTrue(parsedClaimsFromAccessToken.get().getExpiration().getTime() > System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime() - 1000L);
    }
}
