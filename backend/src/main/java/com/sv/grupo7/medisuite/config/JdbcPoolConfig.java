package com.sv.grupo7.medisuite.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcPoolConfig {

    @Bean(name = "jdbcDataSource")
    public DataSource jdbcDataSource() {
        PoolProperties p = new PoolProperties();
        
        // Configuración de conexión básica
        p.setDriverClassName("org.postgresql.Driver");
        p.setUrl("jdbc:postgresql://localhost:5432/medisuite");
        p.setUsername("postgres");
        p.setPassword("postgres");

        // Los 15 parámetros obligatorios del pool de Tomcat
        p.setInitialSize(5);                      // 1
        p.setMaxActive(20);                       // 2
        p.setMaxIdle(10);                         // 3
        p.setMinIdle(5);                          // 4
        p.setMaxWait(10000);                      // 5
        p.setValidationQuery("SELECT 1");         // 6
        p.setTestOnBorrow(true);                  // 7
        p.setTestOnReturn(false);                 // 8
        p.setTestWhileIdle(true);                 // 9
        p.setTimeBetweenEvictionRunsMillis(5000); // 10
        p.setMinEvictableIdleTimeMillis(60000);   // 11
        p.setNumTestsPerEvictionRun(3);           // 12
        p.setRemoveAbandoned(true);               // 13
        p.setRemoveAbandonedTimeout(60);          // 14
        p.setLogAbandoned(true);                  // 15

        DataSource dataSource = new DataSource();
        dataSource.setPoolProperties(p);
        return dataSource;
    }
}
