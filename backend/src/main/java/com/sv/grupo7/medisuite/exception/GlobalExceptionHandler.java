package com.sv.grupo7.medisuite.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import java.util.LinkedHashMap;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleForbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "forbidden", "message", "No tiene permiso para esta operacion"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "invalid_credentials", "message", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "business_rule", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(Map.of("error", "validation_failed", "fields", fieldErrors));
    }

    /** Errores JDBC propios: el código ya viene clasificado desde el DAO. */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        return dataAccessResponse(ex.getCode(), ex);
    }

    /**
     * Errores de acceso a datos que llegan por JPA/Spring (por ejemplo, la BD caída al abrir
     * la transacción o una violación de constraint al guardar con un repositorio). Se
     * clasifican con el mismo {@link JdbcErrorCode}, recorriendo la cadena de causas hasta
     * la {@code SQLException} original.
     */
    @ExceptionHandler({org.springframework.dao.DataAccessException.class,
            CannotCreateTransactionException.class})
    public ResponseEntity<Map<String, Object>> handleSpringDataAccess(Exception ex) {
        return dataAccessResponse(JdbcErrorCode.from(ex), ex);
    }

    /** Otro usuario modificó el mismo registro (columna {@code version}, bloqueo optimista). */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(OptimisticLockingFailureException ex) {
        log.warn("Conflicto de versión: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "concurrent_modification");
        body.put("message", "El registro fue modificado por otro usuario. Recargue e intente de nuevo.");
        body.put("timestamp", OffsetDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(500).body(Map.of(
            "error", "Error interno del servidor",
            "timestamp", OffsetDateTime.now().toString()
        ));
    }

    private ResponseEntity<Map<String, Object>> dataAccessResponse(JdbcErrorCode code, Throwable ex) {
        // Log interno con la causa completa (nunca va al cliente)
        log.error("Error de acceso a datos [{}]", code, ex);

        HttpStatus status;
        String message;
        switch (code) {
            case UNIQUE_VIOLATION, FOREIGN_KEY_VIOLATION, NOT_NULL_VIOLATION, CHECK_VIOLATION -> {
                status = HttpStatus.CONFLICT;
                message = "La operación entra en conflicto con los datos existentes";
            }
            case CONNECTION_LOST, TIMEOUT -> {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                message = "Servicio temporalmente no disponible";
            }
            case SERIALIZATION_FAILURE -> {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                message = "Conflicto de concurrencia, intente de nuevo";
            }
            default -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Error interno del servidor";
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "data_access_" + code.name().toLowerCase());
        body.put("message", message);
        body.put("timestamp", OffsetDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
