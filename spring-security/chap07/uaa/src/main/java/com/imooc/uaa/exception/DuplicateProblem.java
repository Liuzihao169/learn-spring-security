package com.imooc.uaa.exception;

import com.imooc.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DuplicateProblem extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/duplicate");

    public DuplicateProblem(String msg) {
        super(
            TYPE,
            "发现重复数据",
            Status.CONFLICT,
            msg);
    }
}
