package com.imooc.uaa.security.auth.rest;

import org.springframework.security.core.AuthenticationException;

public class NeedMfaException extends AuthenticationException {
    public NeedMfaException(String msg) {
        super(msg);
    }
}
