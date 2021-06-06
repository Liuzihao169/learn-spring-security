## SpringSecurity源码学习

#### 一、Spring-Boot的自动配置

看到它的自动配置类：`SecurityFilterAutoConfiguration`

```java

	@Bean
	@ConditionalOnBean(name = DEFAULT_FILTER_NAME)
	public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration(
			SecurityProperties securityProperties) {
		DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(
				DEFAULT_FILTER_NAME);
		registration.setOrder(securityProperties.getFilter().getOrder());
		registration.setDispatcherTypes(getDispatcherTypes(securityProperties));
		return registration;
	}
```

该自动配置类中向容器加入了DelegatingFilterProxyRegistrationBean注册器，并传入了一个目标bean名称springSecurityFilterChain，其实SpringSecurity的内部核心就是一系列过滤器链；它是基于责任链的设计模式，

#### 二、SpringSecurity基本流程

![image-20210606163557677](https://gitee.com/liuzihao169/pic/raw/master/image/20210606163559.png)

##### 2.1 **UsernamePasswordAuthenticationFilter**

该过滤器主要是拦截前端提交的POST请求，进行认证处理，代码如下：

```java
// 在父类中
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
	  
  
  // 判断是登录请求，这里也就是我们在配置类中设置的登录url
  // 并且判断是否为post请求
		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);

			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request is to process authentication");
		}
		// 这个是用来存储用户认证信息的类
		Authentication authResult;

		try {
      // 获取认证信息；会调用UsernamePasswordAuthenticationFilter的attemptAuthentication
      // 获取到表单数据
			authResult = attemptAuthentication(request, response);
      
```

==attemptAuthentication(request, response)方法==

```java
		HttpServletResponse response) throws AuthenticationException {
		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException(
					"Authentication method not supported: " + request.getMethod());
		}
		// 获取到用户名&密码
		String username = obtainUsername(request);
		String password = obtainPassword(request);

		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}

		username = username.trim();
		
      // 构造UsernamePasswordAuthenticationToken内部能够存储认证前和认证后到信息
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);

		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);
		
    // Manager的认证方法
		return this.getAuthenticationManager().authenticate(authRequest);
	
```



##### ProviderManager

这个是认证的入口，这个内部会维护多种认证方式，是一个List<AuthenticationProvider>  认证时实际上是委托模式，找到对应的认证，根据传入的Authentication;

```java
// ProviderManager内部代码，我们用的表单认证，这个类是UsernamePasswordAuthenticationToken.class
for (AuthenticationProvider provider : getProviders()) {
			// 判断是否匹配
  if (!provider.supports(toTest)) {
				continue;
			}
  // 获取认证结果，会调用到UserDetailsService的认证方法，返回用户信息
  // 否则会认证失败
  result = provider.authenticate(authentication);

```

与之匹配的认证器是DaoAuthenticationProvider，**内部关联了UserDetailsService，这样就会进行自定义的认证方式，也就是查询数据库。**

#### 参考文献：https://docs.spring.io/spring-security/site/docs/5.4.5/reference/html5/#servlet-hello