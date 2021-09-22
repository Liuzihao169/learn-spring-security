package com.imooc.uaa.security.auth.ldap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entry(
    objectClasses = {"inetOrgPerson", "organizationalPerson", "person", "top"})
public final class LDAPUser {
    @Id
    private Name id;

    @Attribute(name = "uid")
    private String username;

    @Attribute(name = "userPassword")
    private String password;
}
