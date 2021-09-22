package com.imooc.uaa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.uaa.integration.passay.PassayPropertiesMessageResolver;
import lombok.RequiredArgsConstructor;
import org.passay.MessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
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
            .addResourceHandler("/resources/**")
            .addResourceLocations("/resources/");
    }

//    /**
//     * 使用 Sprig Mvc 配置 CORS
//     * @param registry Cors 注册表
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        if (environment.acceptsProfiles(Profiles.of("dev"))) {
//            registry.addMapping("/**")
//                .allowedHeaders("*")
//                .exposedHeaders("X-Authenticate")
//                .allowedOrigins("http://localhost:4001");
//        } else {
//            registry.addMapping("/**")
//                .allowedHeaders("*")
//                .exposedHeaders("X-Authenticate")
//                .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS")
//                .allowedOrigins("https://uaa.imooc.com"); // 生产主机域名
//        }
//    }
}
