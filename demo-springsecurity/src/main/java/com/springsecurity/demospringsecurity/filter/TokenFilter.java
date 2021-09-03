package com.springsecurity.demospringsecurity.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liuzihao
 * @create 2021-06-06-11:13
 *
 * 获取登录信息，并且可以生成token
 * 可以设置登录成功Handler以及登录失败Handler
 *
 * * token过滤器，再登录成功之后，可以进行token生成，并且将授权信息存储到redis当中
 *
 */
@Slf4j
public class TokenFilter extends UsernamePasswordAuthenticationFilter {


    /**
     * 获取表单数据
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//
//      return super.attemptAuthentication(request,response);
//
//    }

    /**
     * 认证成功调用的方法
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {


        log.info("获取主体信息,也就是认证成功的user信息");

        log.info("认证成功后，可以进行生成token");


        log.info("把用户名称和用户权限列表放到redis");

        // 1、认证成功，得到认证成功之后用户信息

        //2、根据用户名生成token

        //3、把用户名称和用户权限列表放到redis

        // 4、返回token
    }

    /**
     * 认证失败调用的方法
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

    }
}
