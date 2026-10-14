package com.sv.grupo7.medisuite.exception;

import java.sql.SQLException;

public enum JdbcErrorCode {
    UNIQUE_VIOLATION,
    FOREIGN_KEY_VIOLATION,
    NOT_NULL_VIOLATION,
    CHECK_VIOLATION,
    CONNECTION_LOST,
    TIMEOUT,
    SERIALIZATION_FAILURE,
    UNKNOWN;

    public static JdbcErrorCode from(Throwable throwable) {
        Throwable current = throwable;
        int depth = 0;
        while (current != null && depth < 20) {
            if (current instanceof SQLException sql) {
                JdbcErrorCode code = fromSqlException(sql);
                if (code != UNKNOWN) {
                    return code;
                }
            }
            current = current.getCause();
            depth++;
        }
        return UNKNOWN;
    }

    private static JdbcErrorCode fromSqlException(SQLException sql) {
        SQLException e = sql;
        int depth = 0;
        while (e != null && depth < 20) {
            JdbcErrorCode code = fromSqlState(e.getSQLState());
            if (code != UNKNOWN) {
                return code;
            }
            e = e.getNextException();
            depth++;
        }
        return UNKNOWN;
    }

    private static JdbcErrorCode fromSqlState(String state) {
        if (state == null) {
            return UNKNOWN;
        }
        return switch (state) {
            case "23505" -> UNIQUE_VIOLATION;
            case "23503" -> FOREIGN_KEY_VIOLATION;
            case "23502" -> NOT_NULL_VIOLATION;
            case "23514" -> CHECK_VIOLATION;
            case "08000", "08001", "08003", "08006" -> CONNECTION_LOST;
            case "57014" -> TIMEOUT;
            case "40001" -> SERIALIZATION_FAILURE;
            default -> UNKNOWN;
        };
    }
}