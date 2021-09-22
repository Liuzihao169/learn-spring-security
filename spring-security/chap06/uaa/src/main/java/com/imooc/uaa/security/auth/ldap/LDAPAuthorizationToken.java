package com.imooc.uaa.security.auth.ldap;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class LDAPAuthorizationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String credentials;

    public LDAPAuthorizationToken(String principal, String credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
    }

    public Object getCredentials() {
        return credentials;
    }

    public Object getPrincipal() {
        return principal;
    }
}
