package com.imooc.uaa.validation;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    private final MessageResolver messageResolver;
    @Override
    public void initialize(final ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        val validator = new PasswordValidator(messageResolver, Arrays.asList(
            // 长度规则：8 - 30 位
            new LengthRule(8, 30),
            // 至少有一个大写字母
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            // 至少有一个小写字母
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            // 至少有一个数字
            new CharacterRule(EnglishCharacterData.Digit, 1),
            // 至少有一个特殊字符
            new CharacterRule(EnglishCharacterData.Special, 1),
            // 不允许连续 3 个字母，按字母表顺序
            // 比如字母形式 'abcde', 数字形式 '34567', qwery 键盘形式 'asdfg'
            // wrap 为 false 表示不超过限制长度的连续是允许的，比如 'xyzabc'
            new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
            // 不允许 3 个连续数字
            new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
            // 不允许 QWERTY 键盘上的三个连续相邻的按键所代表的字符
            new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
            // 不允许包含空格
            new WhitespaceRule()));
        val result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.join(",", validator.getMessages(result)))
            .addConstraintViolation();
        return false;
    }
}
