package com.sv.grupo7.medisuite.dao.jdbc;

import com.sv.grupo7.medisuite.exception.DataAccessException;
import com.sv.grupo7.medisuite.exception.JdbcErrorCode;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers(disabledWithoutDocker = true)
class BaseJdbcDaoIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void sqlRealYRecursosVerificadosSobrePostgres() throws Exception {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setUrl(postgres.getJdbcUrl());
        source.setUser(postgres.getUsername());
        source.setPassword(postgres.getPassword());
        try (Connection c = source.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE probe (id bigint primary key, name text not null, tenant_id bigint not null)");
            s.execute("CREATE TABLE probe_code (code text not null, tenant_id bigint not null, "
                    + "CONSTRAINT uq_probe_code UNIQUE (tenant_id, code))");
            s.execute("INSERT INTO probe VALUES (1,'ana',1),(2,'bety',1),(3,'cora',2)");
            s.execute("INSERT INTO probe_code VALUES ('R1',1)");
        }

        BaseJdbcDao dao = new BaseJdbcDao(source) {};

        List<String> rows = dao.query(
                "SELECT name FROM probe WHERE tenant_id = ? ORDER BY id",
                rs -> rs.getString(1), 1L);
        assertThat(rows).containsExactly("ana", "bety");

        Optional<String> missing = dao.queryOne(
                "SELECT name FROM probe WHERE tenant_id = ? AND id = ?",
                rs -> rs.getString(1), 99L, 1L);
        assertThat(missing).isEmpty();

        assertThat(dao.update("UPDATE probe SET name = ? WHERE id = ?", "ANA-2", 1L)).isEqualTo(1);
        assertThat(dao.update("UPDATE probe SET name = ? WHERE id = ?", "nadie", 999L)).isZero();
        assertThat(dao.countBy("SELECT COUNT(*) FROM probe WHERE tenant_id = ?", 1L)).isEqualTo(2);

        assertThatThrownBy(() -> dao.update(
                "INSERT INTO probe_code VALUES (?, ?)", "R1", 1L))
                .isInstanceOf(DataAccessException.class)
                .satisfies(ex -> assertThat(((DataAccessException) ex).getCode())
                        .isEqualTo(JdbcErrorCode.UNIQUE_VIOLATION));
    }
}
