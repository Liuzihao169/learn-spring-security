package com.imooc.uaa.service;

import com.imooc.uaa.domain.Auth;
import com.imooc.uaa.domain.User;
import com.imooc.uaa.exception.BadCredentialProblem;
import com.imooc.uaa.repository.RoleRepo;
import com.imooc.uaa.repository.UserRepo;
import com.imooc.uaa.util.JwtUtil;
import com.imooc.uaa.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.imooc.uaa.util.Constants.ROLE_USER;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TotpUtil totpUtil;

    /**
     * 注册一个新用户
     *
     * @param user 用户实体
     * @return 保存后的对象
     */
    public User register(User user) {
        return roleRepo.findOptionalByAuthority(ROLE_USER)
            .map(role -> {
                val userToSave = user
                    .withAuthorities(Set.of(role))
                    .withPassword(passwordEncoder.encode(user.getPassword()))
                    .withMfaKey(totpUtil.encodeKeyToString());
                return userRepo.save(userToSave);
            })
            .orElseThrow();
    }

    public Optional<User> findOptionalByUsername(String username) {
        return userRepo.findOptionalByUsername(username);
    }

    public Optional<User> findOptionalByUsernameAndPassword(String username, String password) {
        return findOptionalByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public Auth login(UserDetails userDetails) {
        return new Auth(jwtUtil.createAccessToken(userDetails), jwtUtil.createRefreshToken(userDetails));
    }

    public Auth refreshToken(String accessToken, String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken) && !jwtUtil.validateWithoutExpiration(accessToken)) {
            throw new BadCredentialProblem();
        }
        return new Auth(jwtUtil.buildAccessTokenWithRefreshToken(refreshToken), refreshToken);
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }

    public Auth loginWithTotp(User user) {
        val toSave = user.withMfaKey(totpUtil.encodeKeyToString());
        val saved = saveUser(toSave);
        return login(saved);
    }

    public Optional<String> createTotp(User user) {
        return totpUtil.createTotp(user.getMfaKey());
    }

    public void upgradePasswordEncodingIfNeeded(User user, String rawPassword) {
        if (passwordEncoder.upgradeEncoding(user.getPassword())) {
            userRepo.save(user.withPassword(passwordEncoder.encode(rawPassword)));
        }
    }

    /**
     * 取得全部用户列表
     *
     * @return 全部用户列表
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

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
     * 判断手机号是否存在
     *
     * @param mobile 手机号
     * @return 存在与否
     */
    public boolean isMobileExisted(String mobile) {
        return userRepo.countByMobile(mobile) > 0;
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
