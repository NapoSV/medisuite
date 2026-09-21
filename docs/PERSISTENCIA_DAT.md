# Persistencia híbrida PostgreSQL + .dat

## Qué va donde
- **PostgreSQL (Neon):** todos los datos transaccionales (pacientes, citas,
  recetas, expedientes, usuarios, tenants, audit_log).
- **Archivos `.dat`:** copia de respaldo asíncrona de `audit_log` cada 60s
  en `data/audit_backup.dat`.

## Por qué
- El profesor exige uso de archivos `.dat` (criterio 9, 1.20 pts).
- Concurrencia (criterio 5, 1.20 pts) se demuestra con `ScheduledExecutorService`
  respaldando `.dat` en un hilo dedicado sin bloquear el request principal.

## Arquitectura
`AuditLogRepository` (JPA) → `AuditBackupScheduler` (hilo daemon) →
`AuditLogDatDao extends DatFileDao<AuditLog>` → archivo `data/audit_backup.dat`
protegido con `ReentrantReadWriteLock`.

## Recuperación ante desastre
Si PostgreSQL se cae, `AuditLogDatDao.loadAll()` puede repoblar el audit log
desde el `.dat` (script manual de recuperación).