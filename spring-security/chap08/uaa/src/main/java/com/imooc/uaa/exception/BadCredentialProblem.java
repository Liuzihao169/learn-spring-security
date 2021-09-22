package com.imooc.uaa.exception;

import com.imooc.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BadCredentialProblem extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/bad-credentials");

    public BadCredentialProblem() {
        super(
            TYPE,
            "认证失败",
            Status.UNAUTHORIZED,
            "用户名或密码错误");
    }
}
