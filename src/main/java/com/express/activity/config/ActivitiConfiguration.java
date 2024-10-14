package com.express.activity.config;


import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
public class ActivitiConfiguration implements ProcessEngineConfigurationConfigurer {

    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Value("${spring.datasource.username}")
    private String dsUsername;

    @Value("${spring.datasource.password}")
    private String dsPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dsDriverClassName;

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setDbHistoryUsed(true);
        processEngineConfiguration.setHistoryLevel(HistoryLevel.FULL);
        processEngineConfiguration.setDataSource(dataSource());
        processEngineConfiguration.initVariableTypes();
        processEngineConfiguration.getVariableTypes().addType(new ActivitiJsonVariableType());
    }

    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(dsDriverClassName);
        dataSource.setUrl(dsUrl);
        dataSource.setUsername(dsUsername);
        dataSource.setPassword(dsPassword);

        return dataSource;
    }

    @Primary
    @Bean
    public TaskExecutor primaryTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // add necessary properties to the executor
        return executor;
    }
}
