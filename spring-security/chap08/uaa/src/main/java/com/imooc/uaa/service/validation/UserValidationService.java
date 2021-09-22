package com.imooc.uaa.service.validation;

import com.imooc.uaa.exception.DuplicateProblem;
import com.imooc.uaa.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserValidationService {

    private final UserRepo userRepo;

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 存在与否
     */
    public boolean isUsernameExisted(String username) {
        return userRepo.countByUsername(username) > 0;
    }

    /**
     * 判断电邮地址是否存在
     *
     * @param email 电邮地址
     * @return 存在与否
     */
    public boolean isEmailExisted(String email) {
        return userRepo.countByEmail(email) > 0;
    }

    /**
     * 判断手机号是否存在
     *
     * @param mobile 手机号
     * @return 存在与否
     */
    public boolean isMobileExisted(String mobile) {
        return userRepo.countByMobile(mobile) > 0;
    }

    public boolean checkUsername(Authentication authentication, String username) {
        return authentication.getName().equals(username);
    }

    public void validateUserUniqueFields(String username, String email, String mobile) throws DuplicateProblem {
        if (isUsernameExisted(username)) {
            throw new DuplicateProblem("用户名已存在");
        }
        if (isEmailExisted(email)) {
            throw new DuplicateProblem("电子邮件已存在");
        }
        if (isMobileExisted(mobile)) {
            throw new DuplicateProblem("手机号已存在");
        }
    }

    /**
     * 在编辑用户的场景下，判断电子邮件是否重复，需要规避用户本身的 email
     *
     * @param email    电邮地址
     * @param username 用户名
     * @return 存在与否
     */
    public boolean isEmailExistedAndUsernameIsNot(String email, String username) {
        return userRepo.countByEmailAndUsernameIsNot(email, username) > 0;
    }

    /**
     * 在编辑用户的场景下，判断电子邮件是否重复，需要规避用户本身的手机号
     *
     * @param mobile   手机号
     * @param username 用户名
     * @return 存在与否
     */
    public boolean isMobileExistedAndUsernameIsNot(String mobile, String username) {
        return userRepo.countByMobileAndUsernameIsNot(mobile, username) > 0;
    }
}
