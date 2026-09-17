package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.dao.AuditLogRepository;
import com.sv.grupo7.medisuite.model.audit.AuditLog;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuditBackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuditBackupScheduler.class);
    private final AuditLogRepository auditRepo;
    private final AuditLogDatDao datDao;
    private ScheduledExecutorService executor;

    @PostConstruct
    void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "audit-backup");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::backup, 60, 60, TimeUnit.SECONDS);
        log.info("AuditBackupScheduler iniciado (cada 60s)");
    }

    void backup() {
        try {
            var all = auditRepo.findAll();
            datDao.save(all);
            log.info("Backup .dat: {} eventos respaldados", all.size());
        } catch (Exception e) {
            log.error("Fallo backup audit .dat", e);
        }
    }

    @PreDestroy
    void stop() { if (executor != null) executor.shutdown(); }
}