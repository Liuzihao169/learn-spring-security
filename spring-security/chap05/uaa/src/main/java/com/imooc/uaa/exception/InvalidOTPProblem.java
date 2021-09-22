package com.imooc.uaa.exception;

import com.imooc.uaa.util.Constants;
import org.springframework.context.MessageSource;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.Locale;

public class InvalidOTPProblem extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create(Constants.PROBLEM_BASE_URI + "/invalid-token");

    public InvalidOTPProblem(String msgCode, MessageSource messageSource, Locale locale) {
        super(
            TYPE,
            messageSource.getMessage("Exception.invalid.otp", null, locale),
            Status.UNAUTHORIZED,
            messageSource.getMessage(msgCode, null, locale));
    }
}
