package com.bonade.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.FlowableMailProperties;
import org.flowable.spring.boot.FlowableProperties;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.app.FlowableAppProperties;
import org.flowable.spring.boot.idm.FlowableIdmProperties;
import org.flowable.spring.boot.process.FlowableProcessProperties;
import org.flowable.spring.boot.process.Process;
import org.flowable.spring.boot.process.ProcessAsync;
import org.flowable.spring.boot.process.ProcessAsyncHistory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 流程图编码配置，不配置流程图出现中文乱码
 */
@Configuration
@EnableConfigurationProperties(FlowableProperties.class)
public class FlowDataSourceProcessEngineAutoConfig extends ProcessEngineAutoConfiguration {

    @Value("${flowable.diagram.activityFontName}")
    private String activityFontName;

    @Value("${flowable.diagram.labelFontName}")
    private String labelFontName;

    @Value("${flowable.diagram.annotationFontName}")
    private String annotationFontName;

    public FlowDataSourceProcessEngineAutoConfig(FlowableProperties flowableProperties,
                                                        FlowableProcessProperties processProperties, FlowableAppProperties appProperties, FlowableIdmProperties idmProperties,
                                                        FlowableMailProperties mailProperties) {
        super(flowableProperties, processProperties, appProperties, idmProperties, mailProperties);
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager,
                                                                             @Process ObjectProvider<IdGenerator> processIdGenerator,
                                                                             ObjectProvider<IdGenerator> globalIdGenerator,
                                                                             @ProcessAsync ObjectProvider<AsyncExecutor> asyncExecutorProvider,
                                                                             @ProcessAsyncHistory ObjectProvider<AsyncExecutor> asyncHistoryExecutorProvider) throws IOException {

        SpringProcessEngineConfiguration conf = super.springProcessEngineConfiguration(dataSource, platformTransactionManager, processIdGenerator, globalIdGenerator, asyncExecutorProvider, asyncHistoryExecutorProvider);
        //conf.setIdmEngineConfigurator(idmEngineConfigurator(dataSource));
        conf.setActivityFontName(activityFontName);
        conf.setLabelFontName(labelFontName);
        conf.setAnnotationFontName(annotationFontName);

        return conf;
    }

}
