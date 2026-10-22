# B3b — Documentación A1/A2: códigos de reserva únicos

> Responsable: Alejandro Sebastián Merino Ventura · @amerino
> Destinatarios: Nicole (explicación de HU-003 y módulo de citas) y Carlos (UML final)
> Estado: rama `b3b-reservation-codes`, pendiente de PR hacia `feature/avance3-fase-b`

## 1. Qué cambia en HU-003 (Avance 1)

HU-003 del Avance 1 pide: *"Genera código de reserva único (ej. COD-0001)"*.

| | Antes (A1/A2) | Ahora (B3b) |
|---|---|---|
| Formato | `COD-####` | `R-########` (`R-` + 8 dígitos, 10 caracteres) |
| Fuente | `new Random().nextInt(10000)` en `AppointmentService.create()` | Secuencia PostgreSQL `reservation_code_seq` (V12) |
| Garantía de unicidad | Ninguna en código; dependía de que el constraint existiera en la BD | Secuencia atómica entre hilos y entre instancias, más `UNIQUE (tenant_id, reservation_code)` |
| Capacidad | 10 000 valores | 99 999 999 valores, sin ciclo |
| Al agotarse | Colisiones | Error explícito; nunca se trunca |
| Citas existentes | — | Conservan su código (`COD-####`, `RSV-####`); no se reescriben |

El criterio de aceptación de HU-003 ("código único") deja de ser una probabilidad y pasa a ser una garantía de la base de datos.

Los códigos son únicos, no consecutivos: si una transacción se revierte, el número de la secuencia consumido no se reutiliza, así que pueden existir saltos.

## 2. Qué cambia en el módulo de citas (Avance 2)

- `AppointmentService` recibe `ReservationCodeGenerator` por constructor y usa `next()`. Se eliminó `Random`.
- `ReservationCodeGenerator` formatea el valor y lanza `IllegalStateException` si supera 99 999 999.
- `ReservationCodeJdbcDao` obtiene el valor con `SELECT nextval('reservation_code_seq')` (`PreparedStatement`/`ResultSet`, try-with-resources) sobre el pool `jdbcDataSource` (B2). Convierte `SQLException` con `JdbcErrorCode` y `DataAccessException` (B5).
- Reintentos (decisión cerrada de H. López): `AppointmentController.create()` reintenta hasta 3 veces ante `UNIQUE_VIOLATION`, sin espera. Al cuarto fallo propaga `DataAccessException`. Cada intento es una transacción nueva (`create()` sigue siendo `@Transactional`), porque tras un error de unicidad PostgreSQL deja abortada la transacción anterior.
- Validación de fecha, doctor/paciente, disponibilidad, tenant y estado `PENDING`: sin cambios.

**Discrepancia a corregir en A2:** `docs/AVANCE2_MEDISUITE.md` (HU-003) dice que el código tiene formato `RSV-XXXX`. El código del Avance 2 en realidad generaba `COD-####`; `RSV-####` solo aparece en los datos de ejemplo de V4. Conviene que Nicole use `R-########` como formato final.

## 3. Comparación `Random` vs secuencia

Riesgo de colisión del esquema anterior (paradoja del cumpleaños, 10 000 valores posibles, aproximación `1 − e^(−n(n−1)/20000)`), para `n` citas dentro del mismo tenant:

| Citas (`n`) | Probabilidad de al menos una colisión |
|---|---|
| 10 | 0,45 % |
| 50 | 11,5 % |
| 100 | 39,0 % |
| 119 | 50,4 % |
| 200 | 86,3 % |

| Criterio | `Random` | Secuencia PostgreSQL |
|---|---|---|
| Colisión posible | Sí, y crece rápido | No por construcción |
| Concurrencia (dos solicitudes simultáneas) | Pueden obtener el mismo código | `nextval()` es atómico |
| Varias instancias / reinicios | Sin coordinación | La fuente de verdad es la BD; no se reinicia |
| Si el constraint falta (BD solo con V5) | Duplicado silencioso | V12 lo añade; los duplicados previos abortan la migración |
| Si el constraint existe | Error 409/500 al azar, sin reintento | Sin colisión; reintento acotado como respaldo |
| `AtomicLong` en memoria | No aplica | Descartado: solo protege una JVM y se reinicia |

Evidencia: `ReservationCodeGeneratorTest` (13 pruebas, PostgreSQL 16 en Testcontainers). Lanza 100 tareas desde un `ExecutorService` de 4 hilos y obtiene 100 códigos distintos; repite 3 rondas (300 códigos); inserta los 100 como citas con el constraint real.

## 4. Persistencia `.dat` frente a la secuencia JDBC

Son mecanismos distintos y no se sustituyen.

| | Respaldo `.dat` (Avance 2) | Secuencia JDBC (B3b) |
|---|---|---|
| Propósito | Copia de seguridad para recuperación ante desastre | Generar un identificador único en cada alta de cita |
| Qué guarda | `audit_log` (archivos fechados en `audit-backups/`) y expedientes (`data/medical_records_backup.dat`) | Un contador en PostgreSQL |
| Cuándo | `ScheduledExecutorService` de un hilo: cada 60 s (auditoría) y cada día (expedientes) | En cada llamada a `create()` |
| Implementación | `DatFileDao<T extends Serializable>` con `ReentrantReadWriteLock` | `ReservationCodeJdbcDao` con `jdbcDataSource` |
| Garantiza unicidad del código de reserva | No | Sí |

Las citas no se respaldan en `.dat`: solo `audit_log` y `medical_records`. El `.dat` y su scheduler siguen siendo la evidencia del Avance 2 (criterios de archivos y concurrencia) y no se modificaron. La lectura de los `.dat` está comprobada por `AuditLogDatDaoIntegrationTest`. Detalle en `docs/PERSISTENCIA_DAT.md`.

## 5. Insumos para el UML final (Carlos)

El diagrama editable es de Carlos. El original `docs/diagramas/DIAGRAMA_CLASES_MEDISUITE_AVANCE2.drawio` incluye `DatFileDao<T>` pero no `AppointmentService` ni clases de B3b. Clases y relaciones a representar, todas ya compiladas en esta rama:

| Clase | Paquete | Relación |
|---|---|---|
| `AppointmentService` | `service` | usa `ReservationCodeGenerator` (inyección por constructor) |
| `ReservationCodeGenerator` | `service.concurrent` | usa `ReservationCodeJdbcDao` |
| `ReservationCodeJdbcDao` | `dao.jdbc` | extiende `BaseJdbcDao`, implementa `ReservationCodePort`; usa `DataSource` (`jdbcDataSource`); lanza `DataAccessException` con `JdbcErrorCode` |
| `ReservationCodePort` | `service.port` | interfaz con `nextCode(Long tenantId)` |
| `AppointmentController` | `controller.api` | usa `AppointmentService`; reintenta ante `UNIQUE_VIOLATION` |

`ReservationCodeJdbcDao` extiende `BaseJdbcDao` e implementa `ReservationCodePort` de B4 (mergeado en PR #48). Usa el pool secundario `jdbcDataSource` (B2) y maneja excepciones mediante B5.
