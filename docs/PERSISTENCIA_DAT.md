# Persistencia híbrida PostgreSQL + `.dat`

## Qué va dónde

- **PostgreSQL (Neon):** todos los datos transaccionales: pacientes, citas,
  recetas, expedientes, usuarios, tenants y auditoría.
- **Archivos `.dat`:** respaldos asíncronos y fechados de `audit_log` cada
  60 segundos en `audit-backups/`, además del respaldo diario de expedientes
  en `data/medical_records_backup.dat`.

## Por qué

- El profesor exige uso de archivos `.dat` (criterio 9, 1.20 pts).
- La concurrencia (criterio 5, 1.20 pts) se demuestra mediante
  `ScheduledExecutorService`, ejecutando los respaldos en hilos dedicados sin
  bloquear las solicitudes HTTP.

## Arquitectura

La persistencia se divide en dos flujos:

- `MedicalRecordRepository` (JPA) → `MedicalRecordBackupScheduler` →
  `MedicalRecordDatDao extends DatFileDao<MedicalRecord>` →
  `data/medical_records_backup.dat`, protegido con
  `ReentrantReadWriteLock`.
- `AuditLogRepository` (JPA) → `AuditBackupScheduler` → `AuditLogDatDao` →
  archivos fechados dentro de `audit-backups/`.

## Recuperación ante desastre

Los respaldos pueden recuperarse manualmente con `ObjectInputStream`. La prueba
`AuditLogDatDaoIntegrationTest` comprueba que los objetos serializados se pueden
leer nuevamente sin perder sus datos.
