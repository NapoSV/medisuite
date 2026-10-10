package com.sv.grupo7.medisuite.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JdbcPoolConfig {

    // Lee url/username/password/driver desde spring.datasource.* (ya está en el yml)
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    // Hikari queda como DataSource primario de verdad, explícito y garantizado
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource hikariDataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    // Pool secundario Tomcat JDBC para la tarea B2
    @Bean(name = "jdbcDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.tomcat")
    public org.apache.tomcat.jdbc.pool.DataSource jdbcDataSource() {
        return new org.apache.tomcat.jdbc.pool.DataSource();
    }
}