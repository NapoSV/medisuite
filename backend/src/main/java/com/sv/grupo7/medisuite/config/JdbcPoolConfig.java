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
        // Hikari sigue siendo el DataSource primario autoconfigurado por Spring Boot.
        // Este bean secundario expone un pool Tomcat JDBC para la tarea B2.
        return new DataSource();
    }
}
