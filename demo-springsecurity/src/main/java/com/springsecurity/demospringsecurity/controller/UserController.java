package com.springsecurity.demospringsecurity.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author liuzihao
 * @create 2021-06-01-21:59
 */
@Controller
public class UserController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello security.....";
    }

    @RequestMapping("/index")
    @ResponseBody
    public String index(){
        return "this is my index";
    }


    @RequestMapping("/index2")
    @ResponseBody
    public String index2(){
        return "this is my index2";
    }

    @RequestMapping("/index3")
    @ResponseBody
    public String index3(){
        return "this is my index3";
    }


    @RequestMapping("/other")
    @ResponseBody
    public String otherSearch(){
        return "otherSearch.....";
    }

    /**
     * 查看人员信息
     * 只有管理员角色才能看
     *  或者使用@PreAuthorize("hasRole('ROLE_admin')") 前提需要开启@EnableGlobalMethodSecurity(prePostEnabled = true)
     * @return
     */
    @RequestMapping("/user/info")
    @ResponseBody
    @Secured({"ROLE_admin"})
    public String lookUserInfo(){
        return "lookUserInfo.....";
    }


    /**
     *  拥有bookInfo权限的才可以进行访问
     *  需要使用注解：@EnableGlobalMethodSecurity(prePostEnabled = true)
     * @return
     */
    @RequestMapping("/book/info")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('bookInfo')")
    public String bookInfo(){
        return "bookInfo.....";
    }

    /**
     * 无权限页面
     * @return
     */
    @RequestMapping("/403")
    public String accessDeniedPage(){
        return "403";
    }



}
