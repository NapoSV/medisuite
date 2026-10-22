package com.sv.grupo7.medisuite.service.concurrent;

import com.sv.grupo7.medisuite.service.port.ReminderPort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Worker concurrente que mantiene la cola de recordatorios.
 *
 * <p>Un hilo (ScheduledExecutorService) revisa periódicamente las citas
 * próximas, inserta recordatorios idempotentes y cancela los que quedaron
 * obsoletos. No se marca SENT porque no hay canal de envío real.</p>
 *
 * <p>Si el despliegue tiene varias instancias, cada una dispara su propio
 * worker; el índice único de la tabla evita duplicados, pero existe trabajo
 * redundante. Limitación documentada en el PR.</p>
 */
@Service
public class AppointmentReminderService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderService.class);

    /** Cadencia del worker; la primera ejecución puede esperar a Flyway. */
    static final long INITIAL_DELAY_MINUTES = 2;
    static final long PERIOD_MINUTES = 10;

    /** Zona operativa de la clínica para calcular la ventana de 24 h. */
    static final ZoneId CLINIC_ZONE = ZoneId.of("America/El_Salvador");

    private final ReminderPort reminderPort;
    private final Clock clock;
    private final AtomicInteger cycleCount = new AtomicInteger();
    private ScheduledExecutorService executor;

    @Autowired
    public AppointmentReminderService(ReminderPort reminderPort) {
        this(reminderPort, Clock.systemDefaultZone());
    }

    /** Constructor para test con Clock inyectable y puerto simulado. */
    public AppointmentReminderService(ReminderPort reminderPort, Clock clock) {
        this.reminderPort = reminderPort;
        this.clock = clock;
    }

    @PostConstruct
    void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "appointment-reminder-worker");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::runSafely,
                INITIAL_DELAY_MINUTES, PERIOD_MINUTES, TimeUnit.MINUTES);
        log.info("AppointmentReminderService iniciado (cada {} min)", PERIOD_MINUTES);
    }

    @PreDestroy
    void stop() {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
        log.info("AppointmentReminderService detenido");
    }

    /**
     * Ejecuta un ciclo de trabajo capturando cualquier excepción para que
     * un fallo puntual no detenga ejecuciones futuras.
     */
    void runSafely() {
        try {
            runOnce();
        } catch (RuntimeException ex) {
            log.error("Fallo en ciclo de recordatorios (ciclo {})", cycleCount.get(), ex);
        }
    }

    /** Un ciclo: cancelar obsoletos y luego encolar los próximos. */
    void runOnce() {
        int cycle = cycleCount.incrementAndGet();
        int cancelled = reminderPort.cancelStaleReminders(clock, CLINIC_ZONE);
        int inserted = reminderPort.enqueueDueReminders(clock, CLINIC_ZONE);
        log.info("Recordatorios ciclo {}: cancelados={} insertados={}", cycle, cancelled, inserted);
    }

    /** Visible para tests: ejecuta un ciclo ahora sin esperar al scheduler. */
    public void triggerNow() {
        runSafely();
    }

    public boolean isRunning() {
        return executor != null && !executor.isShutdown();
    }
}