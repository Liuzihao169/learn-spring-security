package com.springsecurity.demospringsecurity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.springsecurity.demospringsecurity.entity.Users;
import com.springsecurity.demospringsecurity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liuzihao
 * @create 2021-05-31-21:34
 */
@Component
public class MyUserServiceDetails implements UserDetailsService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 根据用户名来验证处理
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_name" , userName);
        Users user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        /**
         * 设置权限:
         * 一般是根据用户查询到相关权限，然后进行设置
         * 从数据库中查询相关数据
         * 通常是 权限控制的5张表：
         * 1、用户表   2、用户角色表  3、角色表  4、角色权限表  5、权限表
         *
         */
        List<GrantedAuthority> admin = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_admin,bookInfo");


        return new User(user.getUserName(), passwordEncoder.encode(user.getPassWord()), admin );
    }
}
