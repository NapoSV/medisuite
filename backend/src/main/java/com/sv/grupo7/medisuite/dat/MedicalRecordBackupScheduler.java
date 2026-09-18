package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.dao.MedicalRecordRepository;
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
public class MedicalRecordBackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordBackupScheduler.class);
    private final MedicalRecordRepository recordRepo;
    private final MedicalRecordDatDao datDao;
    private ScheduledExecutorService executor;

    @PostConstruct
    void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "medical-record-backup");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::backup, 60, 60, TimeUnit.SECONDS);
        log.info("MedicalRecordBackupScheduler iniciado (cada 60s)");
    }

    void backup() {
        try {
            var all = recordRepo.findAll();
            datDao.save(all);
            log.info("Backup .dat: {} expedientes respaldados", all.size());
        } catch (Exception e) {
            log.error("Fallo backup medical_records .dat", e);
        }
    }

    @PreDestroy
    void stop() { if (executor != null) executor.shutdown(); }
}
