package com.imooc.uaa.security.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ClientErrorLoggingFilter extends GenericFilterBean {

    private final List<HttpStatus> errorCodes;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 得到认证对象
        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
        if(request instanceof HttpServletRequest) {
            log.debug("URL {}", ((HttpServletRequest)request).getRequestURL().toString());
        }
        if (auth == null) {
            chain.doFilter(request, response);
            return;
        }
        // 获得响应状态码
        int status = ((HttpServletResponse) response).getStatus();
        // 只处理 4xx 即客户端错误代码
        if (status < 400 || status >= 500) {
            chain.doFilter(request, response);
            return;
        }
        // 如果没有给出要记录的错误码
        if (errorCodes == null || errorCodes.size() == 0) {
            log.debug("用户 " + auth.getName() + " 遇到错误 " + status);
        } else {
            // 否则就从给出的错误码集合中找到匹配的
            if (errorCodes.stream()
                .anyMatch(s -> s.value() == status)) {
                log.debug("用户 " + auth.getName() + " 遇到错误 " + status);
            }
        }

        chain.doFilter(request, response);
    }

}
