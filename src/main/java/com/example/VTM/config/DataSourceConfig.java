package com.example.VTM.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    // ===== Primary DataSource & JdbcTemplate =====
    @Bean(name = "firstDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.first")
    public DataSource firstDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "firstJdbcTemplate")
    @Primary
    public JdbcTemplate firstJdbcTemplate(@Qualifier("firstDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ===== Second DataSource & JdbcTemplate =====
    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate secondJdbcTemplate(@Qualifier("secondDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ===== Third DataSource & JdbcTemplate =====
    @Bean(name = "thirdDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.third")
    public DataSource thirdDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "thirdJdbcTemplate")
    public JdbcTemplate thirdJdbcTemplate(@Qualifier("thirdDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ===== Fourth DataSource & JdbcTemplate =====
    @Bean(name = "fourthDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.fourth")
    public DataSource fourthDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "fourthJdbcTemplate")
    public JdbcTemplate fourthJdbcTemplate(@Qualifier("fourthDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
