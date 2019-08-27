package com.bonade.config;

import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 邮箱服务配置
 */
@Configuration
public class FlowableMailConfig {

    @Bean
    public StandaloneProcessEngineConfiguration configuration(DataSource dataSource) {
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setMailServerHost("smtp.163.com");
        configuration.setMailServerPort(678);
        configuration.setMailServerUsername("xxx");
        configuration.setMailServerPassword("xxx");
        configuration.setMailServerUseSSL(true);
        configuration.setMailServerDefaultFrom("xinaml@qq.com");
        return configuration;
    }
}
