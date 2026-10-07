package com.sv.grupo7.medisuite.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import javax.sql.DataSource;

public class JdbcPoolConfigTest {

    @Test
    public void testJdbcDataSourceExists() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JdbcPoolConfig.class);
        DataSource dataSource = context.getBean("jdbcDataSource", DataSource.class);
        assertNotNull(dataSource, "El bean jdbcDataSource de Tomcat debe existir y configurarse correctamente");
        context.close();
    }
}
