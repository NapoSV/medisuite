package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.audit.AuditLog;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AuditLogDatDao {

    private static final String DIR = "audit-backups";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public void save(List<AuditLog> logs) throws IOException {
        Files.createDirectories(Path.of(DIR));
        String filename = DIR + "/audit_" + LocalDateTime.now().format(FMT) + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(logs);
        }
    }
}
