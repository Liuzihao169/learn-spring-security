package com.imooc.uaa.annotation;

import com.imooc.uaa.util.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('" +
    Constants.AUTHORITY_ADMIN +
    "') and authentication.name != #username")
public @interface RoleAdminNotSelf {
}
