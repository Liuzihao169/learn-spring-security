package com.springsecurity.demospringsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springsecurity.demospringsecurity.filter.AuthFilter;
import com.springsecurity.demospringsecurity.filter.TokenFilter;
import com.springsecurity.demospringsecurity.security.UnauthEntryPoint;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author liuzihao
 * @create 2021-05-31-21:33
 */
@Configuration
@Slf4j
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService myUserServiceDetails;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserServiceDetails).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         *????????????????????????
         * ?????????????????????????????????????????????????????????????????????
         * ???????????????????????????????????????UnauthEntryPoint.commence??????
         *
          */
       http.exceptionHandling().authenticationEntryPoint(new UnauthEntryPoint());
        // ????????????
       http.formLogin().loginPage("/login.html")
               // ????????????
               .loginProcessingUrl("/login")
               // ??????????????????
               //.defaultSuccessUrl("/index")
               .successHandler(jsonAuthenSuccessHandler())
               .failureHandler((request, response, exception) -> {
               })
               .permitAll()
                // ???????????????
               .and().authorizeRequests().antMatchers("/hello")
               .permitAll()
               .anyRequest().authenticated()
                // ??????csrf
               .and().csrf().disable();
                //.addFilter(new TokenFilter())
                //.addFilter(new AuthFilter(authenticationManager())).httpBasic();
       http.exceptionHandling().accessDeniedPage("/403");
    }

    /**
     * ?????????????????????
     * ??????????????????????????????????????????????????????????????????????????????json?????????????????????????????????
     * @return
     */
    private AuthenticationSuccessHandler jsonAuthenSuccessHandler() {
        return (request, response, authentication) -> {
         log.info("????????????....");
         ObjectMapper objectMapper = new ObjectMapper();
         response.getWriter().write(objectMapper.writeValueAsString(authentication));
        };
    }
}
