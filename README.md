# learn-spring-security
学习spring security的小demo和笔记
## SpringSecurity介绍及Demo

### 一、什么是SpringSecurity

Spring Security 基于 Spring 框架，提供了一套 Web 应用安全性的完整解决方案。**SpringSecurity 本质是一个过滤器链。**当中含有大量的过滤器。

#### 1.1 **用户认证（Authentication）和用户授权（Authorization）**

这是安全框架最基本，也是最重要的两块东西。

- 什么是认证？
  - 简答点说就是，告诉系统你是谁？ 最基本的方式就输入账号、秘密登陆 这就是一个认证的过程
- 什么是授权
  - 简单点说就是，你能干什么？也就是你拥有的权限，一般是在认证成功之后，就会有授予权操作

#### 1.2 基于 JWT认证授权流程图

这只是其中一种方案：之前记录过[认证机制对比](https://blog.csdn.net/weixin_43732955/article/details/116569896)，``

![image-20210606151643649](https://gitee.com/liuzihao169/pic/raw/master/image/20210606151656.png)

这是现在一种常见的认证方式，并且是无状态，下次客户端访问时，就可以携带token来访问，服务端进行解析后，通过用户名信息，在redis获取权限信息。在生成token的时候，也可以根据需设置过期时间等...

#### 二、springBoot-demo

下面来编写相关的demo，创建一个springboot项目，引入`security`的Spring-boot的依赖(其他忽略，详情见文末代码)

```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
```

##### 2.1核心配置类：

```java
@Configuration
public class MySecurityConfig extends WebSecurityConfigurerAdapter {
		
    @Autowired
    UserDetailsService myUserServiceDetails;
		
   // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
		
  // 设置自定义的UserServiceDetails，认证时会调用到这个类中，也就是进行数据库查询等
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
                // 设置token过滤器
                .addFilter(new TokenFilter())
                // 设置登录成功过滤器
                .addFilter(new AuthFilter(authenticationManager())).httpBasic();
       
       // 设置错误无权限页面，这里我自定义了一个403页面
       http.exceptionHandling().accessDeniedPage("/403");
    }
}

```

##### 2.2 自定义UserDetails

这个类的核心作用就是完成认证、授权。

- 认证，查询数据库是否存在
- 授权，这个用户拥有哪些权限

```java
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
         * 设置权限列表:
         * 一般是根据用户查询到相关权限，然后进行设置
         * 从数据库中查询相关数据
         * 通常是 权限控制的5张表：
         * 1、用户表   2、用户角色表  3、角色表  4、角色权限表  5、权限表
         *  
         * 如果是角色，设置时需要使用ROLE_字符串进行拼接
         *
         */
        List<GrantedAuthority> admin = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_admin,bookInfo");
			
      
        return new User(user.getUserName(), passwordEncoder.encode(user.getPassWord()), admin );
    }
}
```

##### 2.3 接口权限设置

通常是使用注解设置，例如：

常用注解：`@Secured、@PreAuthorize`  会在接口访问前进行校验

```java
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
```



#### 代码地址：

https://github.com/Liuzihao169/learn-spring-security



