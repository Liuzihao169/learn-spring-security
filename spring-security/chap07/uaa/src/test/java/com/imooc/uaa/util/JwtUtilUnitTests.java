package com.imooc.uaa.util;

import com.imooc.uaa.config.AppProperties;
import com.imooc.uaa.domain.Permission;
import com.imooc.uaa.domain.Role;
import com.imooc.uaa.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Base64;
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
        AppProperties.Jwt jwt = new AppProperties.Jwt();
        val key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        val refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        jwt.setKey(Base64.getEncoder().encodeToString(key.getEncoded()));
        jwt.setRefreshKey(Base64.getEncoder().encodeToString(refreshKey.getEncoded()));
        appProperties.setJwt(jwt);
        jwtUtil = new JwtUtil(appProperties);
    }

    @Test
    public void givenUserDetails_thenCreateTokenSuccess() {
        val username = "user";
        val authorities = Set.of(
            Permission.builder().authority("USER_ADMIN").build(),
            Permission.builder().authority("USER_READ").build());
        val roles = Set.of(
            Role.builder()
                .roleName(ROLE_USER)
                .permissions(Set.of(Permission.builder().authority("USER_READ").build()))
                .build(),
            Role.builder()
                .roleName(ROLE_ADMIN)
                .permissions(authorities)
                .build()
        );
        val user = User.builder()
            .username(username)
            .roles(roles)
            .build();
        // 创建 jwt
        val token = jwtUtil.createAccessToken(user);
        // 解析
        val parsedClaims = Jwts.parserBuilder().setSigningKey(jwtUtil.getKey()).build().parseClaimsJws(token).getBody();
        // subject 和 username 应该相同
        assertEquals(username, parsedClaims.getSubject());
        val refreshToken = jwtUtil.createRefreshToken(user);
        val parsedClaimsFromRefreshToken = jwtUtil.parseClaims(refreshToken, jwtUtil.getRefreshKey());
        assertTrue(parsedClaimsFromRefreshToken.isPresent());
        assertTrue(parsedClaimsFromRefreshToken.get().getExpiration().getTime() < System.currentTimeMillis() + appProperties.getJwt().getRefreshTokenExpireTime());
        assertTrue(parsedClaimsFromRefreshToken.get().getExpiration().getTime() > System.currentTimeMillis() + appProperties.getJwt().getRefreshTokenExpireTime() - 1000L);
    }
}
