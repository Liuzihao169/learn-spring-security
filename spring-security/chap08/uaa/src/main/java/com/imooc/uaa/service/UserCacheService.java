package com.imooc.uaa.service;

import com.imooc.uaa.domain.User;
import com.imooc.uaa.util.Constants;
import com.imooc.uaa.util.CryptoUtil;
import com.imooc.uaa.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCacheService {

    private final RedissonClient redisson;
    private final CryptoUtil cryptoUtil;
    private final TotpUtil totpUtil;

    public String cacheUser(User user) {
        val mfaId = cryptoUtil.randomAlphanumeric(12);
        log.debug("生成 mfaId: {}", mfaId);
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId)) {
            cache.put(mfaId, user, totpUtil.getTimeStepInLong(), TimeUnit.SECONDS);
        }
        return mfaId;
    }

    public Optional<User> retrieveUser(String mfaId) {
        log.debug("输入参数 mfaId: {}", mfaId);
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (cache.containsKey(mfaId)) {
            log.debug("找到 mfaId {}", mfaId);
            return Optional.of(cache.get(mfaId));
        }
        return Optional.empty();
    }

    public Optional<User> verifyTotp(String mfaId, String code) {
        log.debug("输入参数 mfaId: {}, code: {}", mfaId, code);
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId) || cache.get(mfaId) == null) {
            return Optional.empty();
        }
        val cachedItem = cache.get(mfaId);
        log.debug("找到用户 {}", cachedItem);
        try {
            val isValid = totpUtil.validateTotp(totpUtil.decodeKeyFromString(cachedItem.getMfaKey()), code);
            log.debug("code {} 的验证结果为 {}", code, isValid);
            if (!isValid) {
                return Optional.empty();
            }
            cache.remove(mfaId);
            log.debug("移除 mfaId: {}", mfaId);
            return Optional.of(cachedItem);
        } catch (InvalidKeyException e) {
            log.error("Key is invalid {}", e.getLocalizedMessage());
        }
        return Optional.empty();
    }
}
