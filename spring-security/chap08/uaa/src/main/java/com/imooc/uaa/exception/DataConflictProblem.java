package com.imooc.uaa.exception;

import com.imooc.uaa.util.Constants;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class DataConflictProblem extends AbstractThrowableProblem {

    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/data-conflict");

    public DataConflictProblem(String msg) {
        super(
            TYPE,
            "数据验证失败",
            Status.CONFLICT,
            msg);
    }
}
