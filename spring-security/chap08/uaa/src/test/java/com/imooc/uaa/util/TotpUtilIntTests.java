package com.imooc.uaa.util;

import com.imooc.uaa.common.BaseIntegrationTest;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TotpUtilIntTests extends BaseIntegrationTest {

    @Autowired
    private TotpUtil totpUtil;

    @Test
    public void givenSameKeyAndTotp_whenValidateTwice_thenFail() throws Exception {
        val now = Instant.now();
        val validFuture = now.plus(totpUtil.getTimeStep());
        val key = totpUtil.generateKey();
        val first = totpUtil.createTotp(key, now);
        val newKey = totpUtil.generateKey();
        assertTrue(totpUtil.validateTotp(key, first), "第一次验证应该成功");
        val second = totpUtil.createTotp(key, Instant.now());
        assertEquals(first, second, "时间间隔内生成的两个 TOTP 是一致的");
        val afterTimeStep = totpUtil.createTotp(key, validFuture);
        assertNotEquals(first, afterTimeStep, "过期之后和原来的 TOTP 比较应该不一致");
        assertFalse(totpUtil.validateTotp(newKey, first), "使用新的 key 验证原来的 TOTP 应该失败");
    }

    @Test
    public void givenKey_ThenEncodeAndDecodeSuccess() {
        val key = totpUtil.generateKey();
        val strKey = totpUtil.encodeKeyToString(key);
        val decodeKey = totpUtil.decodeKeyFromString(strKey);
        assertEquals(key, decodeKey, "从字符串中获得的 key 解码后应该和原来的 key 一致");
    }
}
