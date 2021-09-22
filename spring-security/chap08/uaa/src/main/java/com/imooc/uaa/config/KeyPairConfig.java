package com.imooc.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
public class KeyPairConfig {

    @Bean
    public KeyPair keyPair() throws Exception {
        ClassPathResource ksFile = new ClassPathResource("mooc-jwt.jks");
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, "mooc-pass".toCharArray());
        return ksFactory.getKeyPair("mooc-oauth-jwt");
    }
}
