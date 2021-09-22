package com.imooc.uaa.config;

import cn.leancloud.AVLogger;
import cn.leancloud.core.AVOSCloud;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
public class LeanCloudConfig {

    private final AppProperties appProperties;
    private final Environment env;

    @PostConstruct()
    public void initialize() {
        if (env.acceptsProfiles(Profiles.of("prod"))) {
            AVOSCloud.setLogLevel(AVLogger.Level.ERROR);
        } else {
            AVOSCloud.setLogLevel(AVLogger.Level.DEBUG);
        }
        AVOSCloud.initialize(appProperties.getLeanCloud().getAppId(), appProperties.getLeanCloud().getAppKey());
    }
}
