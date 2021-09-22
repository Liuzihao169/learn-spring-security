package com.imooc.uaa.security.auth.ldap;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class LDAPAuthenticationProvider implements AuthenticationProvider {

    private final LDAPAuthService ldapAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LDAPAuthorizationToken auth = (LDAPAuthorizationToken) authentication;

        if (!ldapAuthService.authenticate((String) auth.getPrincipal(), (String) auth.getCredentials())) {
            throw new AccessDeniedException("Invalid Token");
        }
        auth.setAuthenticated(true);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(LDAPAuthorizationToken.class);
    }
}
