package com.bonade;

import org.flowable.ui.common.rest.idm.remote.RemoteAccountResource;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        LiquibaseAutoConfiguration.class
})
@ComponentScan(basePackages = {"com.bonade", "org.flowable.ui"
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {RemoteAccountResource.class}))

public class FlowableApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableApplication.class, args);
    }

    @Bean
    public FlowableModelerAppProperties flowableModelerAppProperties() {
        FlowableModelerAppProperties flowableModelerAppProperties = new FlowableModelerAppProperties();
        return flowableModelerAppProperties;
    }

}

