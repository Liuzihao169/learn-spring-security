package com.springsecurity.demospringsecurity.config;

import com.springsecurity.demospringsecurity.filter.AuthFilter;
import com.springsecurity.demospringsecurity.filter.TokenFilter;
import com.springsecurity.demospringsecurity.security.UnauthEntryPoint;
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
        /**
         *设置未认证处理器
         * 未设置时：未认证的请求将会跳转到配置到登录页面
         * 设置后未认证的请求将会调用UnauthEntryPoint.commence方法
         *
          */
       http.exceptionHandling().authenticationEntryPoint(new UnauthEntryPoint());
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
               .and().csrf().disable()
                .addFilter(new TokenFilter())
                .addFilter(new AuthFilter(authenticationManager())).httpBasic();


       http.exceptionHandling().accessDeniedPage("/403");
    }
}
