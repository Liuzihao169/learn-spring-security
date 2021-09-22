package com.imooc.uaa.config;

import com.imooc.uaa.security.auth.ExternalAuthenticationProvider;
import com.imooc.uaa.security.auth.ldap.LDAPAuthService;
import com.imooc.uaa.security.auth.ldap.LDAPAuthenticationProvider;
import com.imooc.uaa.security.auth.ldap.LDAPAuthorizationFilter;
import com.imooc.uaa.security.dsl.ClientErrorLoggingConfigurer;
import com.imooc.uaa.security.jwt.JwtFilter;
import com.imooc.uaa.security.rolehierarchy.RoleHierarchyService;
import com.imooc.uaa.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@EnableWebSecurity(debug = true)
@Configuration
@Order(99)
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;
    private final LDAPAuthService ldapAuthService;
    private final JwtFilter jwtFilter;
    private final Environment environment;
    private final RoleHierarchyService roleHierarchyService;
//    private final UserDetailsServiceImpl userDetailsServiceImpl;
//    private final UserDetailsPasswordServiceImpl userDetailsPasswordServiceImpl;
//    private final Environment environment;
//    private final LdapProperties ldapProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 配置跨域
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport))
            .authorizeRequests(authorizeRequests -> authorizeRequests
                .mvcMatchers("/", "/authorize/**").permitAll()
                .mvcMatchers("/admin/**").hasRole(Constants.AUTHORITY_STAFF)
                .mvcMatchers("/api/users/by-email/{email}").hasRole("USER")
                .mvcMatchers("/api/users/{username}/**").access("hasRole('" +
                    Constants.AUTHORITY_ADMIN +
                    "') or @userValidationService.checkUsername(authentication, #username)")
                .mvcMatchers("/api/**").authenticated()
                .anyRequest().denyAll())
            .addFilterBefore(new LDAPAuthorizationFilter(new AntPathRequestMatcher("/api/**")), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        web
            .ignoring()
            .antMatchers("/resources/**", "/static/**", "/public/**", "/h2-console/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*
         * 希望使用内置的 LDAP 认证可以使用下面代码块代替自定义的 LDAPAuthenticationProvider
         * auth.ldapAuthentication()
         *             .userDnPatterns("uid={0}")
         *             .contextSource()
         *             .url(ldapProperties.getUrls()[0])
         *             .root(ldapProperties.getBase());
         */
        auth
            .authenticationProvider(new ExternalAuthenticationProvider())
            .authenticationProvider(new LDAPAuthenticationProvider(ldapAuthService));

//        auth
//            .userDetailsService(userDetailsServiceImpl) // 配置 AuthenticationManager 使用 userService
//            .passwordEncoder(passwordEncoder()) // 配置 AuthenticationManager 使用 userService
//            .userDetailsPasswordManager(userDetailsPasswordServiceImpl); // 配置密码自动升级服务
    }

    /**
     * 我们在 Spring Boot 中有几种其他方式配置 CORS
     * 参见 https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-cors
     * Mvc 的配置方式见 WebMvcConfig 中的代码
     *
     * @return CorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许跨域访问的主机
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4001"));
        } else {
            configuration.setAllowedOrigins(Collections.singletonList("https://uaa.imooc.com"));
        }
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.addExposedHeader("X-Authenticate");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ClientErrorLoggingConfigurer clientErrorLogging() {
        return new ClientErrorLoggingConfigurer(new ArrayList<>());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 默认编码算法的 Id
        val idForEncode = "bcrypt";
        // 要支持的多种编码器
        val encoders = Map.of(
            idForEncode, new BCryptPasswordEncoder(),
            "SHA-1", new MessageDigestPasswordEncoder("SHA-1")
        );
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }

    @ConditionalOnProperty(prefix = "mooc.security", name = "role-hierarchy-enabled", havingValue = "true")
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(roleHierarchyService.getRoleHierarchyExpr());
        return roleHierarchy;
    }
}
