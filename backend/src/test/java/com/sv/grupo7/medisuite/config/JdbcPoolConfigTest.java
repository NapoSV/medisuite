package com.sv.grupo7.medisuite.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@Tag("b2-pool")
class JdbcPoolConfigTest {

    private ApplicationContextRunner contextRunner;

    @BeforeAll
    static void initAll() {
        System.setProperty("test.scenario", "jdbc-pool");
    }

    @BeforeEach
    void setUp() {
        contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
                .withUserConfiguration(JdbcPoolConfig.class)
                .withPropertyValues(
                        "spring.datasource.url=jdbc:postgresql://localhost:5432/medisuite",
                        "spring.datasource.username=test",
                        "spring.datasource.password=test",
                        "spring.datasource.driver-class-name=org.postgresql.Driver",
                        "spring.datasource.hikari.maximum-pool-size=5",
                        "spring.datasource.hikari.minimum-idle=1",
                        "spring.datasource.tomcat.url=jdbc:postgresql://localhost:5432/medisuite",
                        "spring.datasource.tomcat.username=test",
                        "spring.datasource.tomcat.password=test",
                        "spring.datasource.tomcat.driver-class-name=org.postgresql.Driver",
                        "spring.datasource.tomcat.initial-size=5",
                        "spring.datasource.tomcat.max-active=20",
                        "spring.datasource.tomcat.max-idle=10",
                        "spring.datasource.tomcat.min-idle=5",
                        "spring.datasource.tomcat.max-wait=10000",
                        "spring.datasource.tomcat.validation-query=SELECT 1",
                        "spring.datasource.tomcat.test-on-borrow=true",
                        "spring.datasource.tomcat.test-on-return=false",
                        "spring.datasource.tomcat.test-while-idle=true",
                        "spring.datasource.tomcat.time-between-eviction-runs-millis=5000",
                        "spring.datasource.tomcat.min-evictable-idle-time-millis=60000",
                        "spring.datasource.tomcat.num-tests-per-eviction-run=3",
                        "spring.datasource.tomcat.remove-abandoned=true",
                        "spring.datasource.tomcat.remove-abandoned-timeout=60",
                        "spring.datasource.tomcat.log-abandoned=true"
                );
    }

    @Test
    @DisplayName("jdbcDataSource bean carga los 15 parametros desde properties")
    void jdbcDataSourceLoadsFifteenProperties() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("jdbcDataSource");
            DataSource ds = context.getBean("jdbcDataSource", DataSource.class);
            assertThat(ds.getInitialSize()).isEqualTo(5);
            assertThat(ds.getMaxActive()).isEqualTo(20);
            assertThat(ds.getMaxIdle()).isEqualTo(10);
            assertThat(ds.getMinIdle()).isEqualTo(5);
            assertThat(ds.getMaxWait()).isEqualTo(10000);
            assertThat(ds.getValidationQuery()).isEqualTo("SELECT 1");
            assertThat(ds.isTestOnBorrow()).isTrue();
            assertThat(ds.isTestOnReturn()).isFalse();
            assertThat(ds.isTestWhileIdle()).isTrue();
            assertThat(ds.getTimeBetweenEvictionRunsMillis()).isEqualTo(5000);
            assertThat(ds.getMinEvictableIdleTimeMillis()).isEqualTo(60000);
            assertThat(ds.getNumTestsPerEvictionRun()).isEqualTo(3);
            assertThat(ds.isRemoveAbandoned()).isTrue();
            assertThat(ds.getRemoveAbandonedTimeout()).isEqualTo(60);
            assertThat(ds.isLogAbandoned()).isTrue();
        });
    }
}