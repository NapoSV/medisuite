package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.audit.AuditLog;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogDatDaoIntegrationTest {

    @Test
    void save_debeCrearArchivoDat() throws Exception {

        AuditLogDatDao dao = new AuditLogDatDao();

        AuditLog log = new AuditLog();
        log.setAction("CREATE");
        log.setEntityName("Patient");
        log.setEntityId(1L);
        log.setIpAddress("127.0.0.1");

        dao.save(List.of(log));

        Path directorio = Path.of("audit-backups");

        boolean existeArchivoDat;

        try (Stream<Path> archivos = Files.list(directorio)) {
            existeArchivoDat = archivos.anyMatch(path ->
                    path.getFileName().toString().endsWith(".dat")
            );
        }

        assertTrue(existeArchivoDat);
    }

    @Test
    void save_debeGuardarYRecuperarAuditLogs() throws Exception {

        AuditLogDatDao dao = new AuditLogDatDao();

        AuditLog log = new AuditLog();
        log.setAction("CREATE");
        log.setEntityName("Patient");
        log.setEntityId(1L);
        log.setIpAddress("127.0.0.1");

        dao.save(List.of(log));

        Path directorio = Path.of("audit-backups");

        Path archivoDat;

        try (Stream<Path> archivos = Files.list(directorio)) {
            archivoDat = archivos
                    .filter(path -> path.getFileName().toString().endsWith(".dat"))
                    .max((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(a)
                                    .compareTo(Files.getLastModifiedTime(b));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElseThrow();
        }

        List<?> datos;

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(archivoDat.toFile()))) {
            datos = (List<?>) in.readObject();
        }

        assertNotNull(datos);
        assertEquals(1, datos.size());

        AuditLog recuperado = (AuditLog) datos.get(0);

        assertEquals("CREATE", recuperado.getAction());
        assertEquals("Patient", recuperado.getEntityName());
        assertEquals(1L, recuperado.getEntityId());
        assertEquals("127.0.0.1", recuperado.getIpAddress());
    }


}