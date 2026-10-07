package com.sv.grupo7.medisuite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        // Log interno con la causa completa (nunca va al cliente)
        log.error("Error de acceso a datos [{}]", ex.getCode(), ex);

        HttpStatus status;
        String message;
        switch (ex.getCode()) {
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
        body.put("error", "data_access_" + ex.getCode().name().toLowerCase());
        body.put("message", message);
        body.put("timestamp", OffsetDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(500).body(Map.of(
            "error", "Error interno del servidor",
            "timestamp", OffsetDateTime.now().toString()
        ));
    }
}
