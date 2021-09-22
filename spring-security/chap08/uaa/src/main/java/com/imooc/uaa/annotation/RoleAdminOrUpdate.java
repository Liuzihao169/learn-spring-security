package com.imooc.uaa.annotation;

import com.imooc.uaa.util.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('" +
    Constants.ROLE_ADMIN +
    "', '" +
    Constants.AUTHORITY_USER_UPDATE +
    "')")
public @interface RoleAdminOrUpdate {
}
