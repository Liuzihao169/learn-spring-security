package com.imooc.uaa.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class UaaSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!isAdminAuthority(authentication)) {
            // 交给父类处理
            super.onAuthenticationSuccess(request, response, authentication);
        }
        else{
            getRedirectStrategy().sendRedirect(request, response, "/admin");
        }
        clearAuthenticationAttributes(request);
    }

    protected boolean isAdminAuthority(final Authentication authentication) {
        return !CollectionUtils.isEmpty(authentication.getAuthorities())
            && authentication.getAuthorities().contains(adminAuthority);
    }
}
