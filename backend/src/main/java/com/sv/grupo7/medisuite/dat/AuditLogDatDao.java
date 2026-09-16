package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.audit.AuditLog;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Component
public class AuditLogDatDao {

    private static final Path DAT_FILE = Path.of("audit_backup.dat");

    public void save(List<AuditLog> logs) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                DAT_FILE, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (AuditLog log : logs) {
                writer.write(String.join("|",
                    String.valueOf(log.getId()),
                    log.getAction(),
                    log.getEntityName(),
                    String.valueOf(log.getEntityId()),
                    String.valueOf(log.getCreatedAt())
                ));
                writer.newLine();
            }
        }
    }
}
