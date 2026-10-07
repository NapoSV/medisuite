package com.sv.grupo7.medisuite.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcPoolConfig {

    @Bean(name = "jdbcDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.tomcat")
    public DataSource jdbcDataSource() {
        return new DataSource();
    }
}
