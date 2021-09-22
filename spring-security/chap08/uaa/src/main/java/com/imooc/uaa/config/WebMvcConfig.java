package com.imooc.uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.uaa.integration.passay.PassayPropertiesMessageResolver;
import lombok.RequiredArgsConstructor;
import org.passay.MessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final MessageSource messageSource;
    private final Environment environment;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModules(
            new ProblemModule(),
            new ConstraintViolationProblemModule());
    }

    /**
     * 配置自定义的 Passay 消息解析器
     *
     * @return MessageResolver
     */
    @Bean
    public MessageResolver messageResolver() {
        return new PassayPropertiesMessageResolver(messageSource);
    }

    /**
     * 配置 Java Validation 使用国际化的消息资源
     *
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/webjars/**", "/resources/**")
            .addResourceLocations("/webjars/", "/resources/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/oauth/confirm_access").setViewName("authorize");
        registry.addViewController("/oauth/error").setViewName("authorize_error");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
