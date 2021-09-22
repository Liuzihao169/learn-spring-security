package com.imooc.uaa.config;

import com.imooc.uaa.aspect.RoleHierarchyReloadAspect;
import com.imooc.uaa.security.rolehierarchy.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@RequiredArgsConstructor
@EnableAspectJAutoProxy
@Configuration
public class AopConfig {

    private final RoleHierarchyImpl roleHierarchy;
    private final RoleHierarchyService roleHierarchyService;

    @Bean
    public RoleHierarchyReloadAspect roleHierarchyReloadAspect() {
        return new RoleHierarchyReloadAspect(roleHierarchy, roleHierarchyService);
    }
}
