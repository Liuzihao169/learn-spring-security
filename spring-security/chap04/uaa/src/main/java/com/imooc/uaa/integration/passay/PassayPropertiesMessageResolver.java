package com.imooc.uaa.integration.passay;

import lombok.RequiredArgsConstructor;
import org.passay.AbstractMessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class PassayPropertiesMessageResolver extends AbstractMessageResolver {
    private final MessageSource messageSource;

    @Override
    protected String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
