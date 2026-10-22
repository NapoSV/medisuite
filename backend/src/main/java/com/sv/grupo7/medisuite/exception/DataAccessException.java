package com.sv.grupo7.medisuite.exception;

public class DataAccessException extends RuntimeException {

    private final JdbcErrorCode code;

    public DataAccessException(String operation, JdbcErrorCode code, Throwable cause) {
        super("Fallo de acceso a datos en " + operation + " [" + code + "]", cause);
        this.code = code;
    }

    public JdbcErrorCode getCode() {
        return code;
    }
}