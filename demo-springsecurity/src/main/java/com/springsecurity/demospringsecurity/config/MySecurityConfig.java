package com.springsecurity.demospringsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author liuzihao
 * @create 2021-05-31-21:33
 */
@Configuration
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
        // 登录页面
       http.formLogin().loginPage("/login.html")
               // 登录请求
               .loginProcessingUrl("/login")
               // 成功访问页面
               .defaultSuccessUrl("/index")
               .permitAll()
               // 不拦截请求
               .and().authorizeRequests().antMatchers("/hello")
               .permitAll()
               .anyRequest().authenticated()
                // 关闭csrf
               .and().csrf().disable();

       http.exceptionHandling().accessDeniedPage("/403");
    }
}
