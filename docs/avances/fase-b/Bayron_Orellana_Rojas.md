# Guía Avance 3 Fase B — Bayron Alexander Orellana Rojas

> Responsable: Bayron Alexander Orellana Rojas · CIF 2026011707 · GitHub @crislomsu (cuenta verificada el 07/10/2026)  
> Tarea: B3a, recordatorios concurrentes con PostgreSQL y V11  
> Rama personal: `b3a-reminders-concurrency` · PR listo: jueves 15/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

Tu código Java depende de [B2](Alejandro_Vigil_Ramirez.md) @aavigil, [B5](Walter_Vasquez_Amaya.md) @wvasquez y [B4](Carlos_Ventura_Velasquez.md) @cventura.

> **🔒 Decisión cerrada (09/10/2026 — H. López, numeración Flyway):**
> - **V11** → `appointment_reminders.sql` — propietario único: @borellana (tú).
> - **V12** → `reservation_codes.sql` — propietario único: @amerino.
> - **V13+** → libre para la próxima migración que surja.
>
> No hay negociación. Si al momento de abrir tu PR encuentras otra `V11__*.sql` en la rama integradora (no debería), **renómbrala** a `V13__` (V12 está reservada para Merino) y menciónalo en el PR. Flores revisa, no crea migraciones.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git log --oneline origin/feature/avance3-fase-b -25
git status --short
```

Confirma los PR mergeados. V11 es tuyo (ver aviso arriba); si por error existe otra V11 en la rama integradora, renómbrala a V13 y continúa — no te detengas.

## 🎯 Qué vas a hacer y por qué

El PDF exige una tarea concurrente con finalidad real, acceso a BD desde la tarea, `DataSource`, cierre de conexiones, excepciones y control de carrera. Crearás una cola **persistida** de recordatorios de citas. Un `ScheduledExecutorService` revisará citas próximas, insertará recordatorios idempotentes y actualizará los que pierdan validez. No se afirmará que se enviaron correos o SMS si no existe canal de envío.

También revalidarás el módulo A1/A2 de citas: disponibilidad, doble reserva, motivo de cancelación y fecha al completar. Esa regresión protege funcionalidades anteriores mientras añades la cola.

**Rúbrica directa:** concurrencia con BD (1.0 compartido), JDBC/CRUD (1.0 compartido), evolución de avances.

## 🛠️ Preparación

En Git Bash, desde tu clon:

```bash
cd /c/Users/hlopez/medisuite
java --version
mvn --version
git fetch origin
git switch -c b3a-reminders-concurrency origin/feature/avance3-fase-b
git branch --show-current
```

Java debe ser 21. Una rama evita mezclar tu trabajo con otras tareas; un commit guarda un cambio lógico; un PR pide revisión. Si hay cambios locales, consérvalos antes de cambiar de rama. No hagas pruebas de migración en la BD Neon compartida.

## ✍️ Paso a paso del código

### A. Modelo de datos y migración V11

Crea `backend/src/main/resources/db/migration/V11__appointment_reminders.sql`. Inspecciona primero `V5__appointments.sql`, `V10__appointment_cancellation_and_completion.sql` y esquema efectivo de QA. Modelo mínimo:

- `id BIGSERIAL PRIMARY KEY`.
- `tenant_id BIGINT NOT NULL` con FK a `tenants`.
- `appointment_id BIGINT NOT NULL` con FK a `appointments`.
- `scheduled_for TIMESTAMPTZ NOT NULL`.
- `status` restringido a estados inequívocos, por ejemplo `PENDING` y `CANCELLED`.
- `created_at`, `updated_at` y, si corresponde, `processed_at`.
- `UNIQUE (tenant_id, appointment_id, scheduled_for)` para idempotencia.
- Índice para `(tenant_id, status, scheduled_for)`.

Si una cita se reprograma, un recordatorio viejo debe cancelarse y el nuevo insertarse con la nueva hora. Si se cancela la cita, no debe quedar un recordatorio activo. Decide si el sistema crea un recordatorio 24 horas antes o para citas del día siguiente y documenta zona `America/El_Salvador`. No uses un `timestamp` sin zona ni compares día local con UTC sin conversión.

Una migración Flyway debe aplicarse una vez. `IF NOT EXISTS` no arregla una estructura errónea ni convierte una operación en reversible. Valida en PostgreSQL descartable tanto un esquema nuevo como uno que ya está en V10; no modifiques V1–V10 publicados.

### B. DAO JDBC y puerto

Crea `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReminderJdbcDao.java`, implementando `ReminderPort` de B4. Recibe `@Qualifier("jdbcDataSource")`. Toda consulta lleva `tenant_id` explícito; un worker no hereda `TenantContext` del hilo de request. No consultes solo la clínica 1. El worker debe recorrer tenants o consultar candidatos de todas las clínicas con el tenant en cada fila, siempre limitado por estado y fecha.

Operaciones reales:

1. `SELECT` candidatos de cita no cancelada próximos a vencimiento.
2. `INSERT` mediante `PreparedStatement` con `ON CONFLICT ... DO NOTHING` para impedir duplicados.
3. `UPDATE` recordatorios obsoletos a `CANCELLED` cuando la cita cambia/cancela.
4. `SELECT` de la cola `PENDING` para evidencia o pantalla administrativa futura.

Eso demuestra inserción, consulta y actualización con `executeQuery`/`executeUpdate`. El `UNIQUE` de BD garantiza idempotencia entre instancias; una comprobación previa en Java no la garantiza. Captura `SQLException` mediante B5 y cierra recursos aun cuando una operación falle. Si un lote incluye varias modificaciones dependientes, usa transacción explícita en una sola conexión.

### C. Servicio concurrente

Crea `backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderService.java`. Usa `ScheduledExecutorService` de un hilo, nombre legible y `@PostConstruct`/`@PreDestroy` o ciclo de vida Spring equivalente. La tarea corre cada diez minutos, pero la primera ejecución puede ir después de que Flyway/beans estén listos. En cada vuelta registra número de candidatos/insertados/cancelados, no nombres ni motivos clínicos.

Captura y registra fallos de manera que una excepción no detenga silenciosamente todas las ejecuciones futuras. `shutdown()` y `awaitTermination` evitan perder una tarea activa al apagar. Si el proyecto se despliega en dos instancias, ambos workers pueden disparar: el índice único evita duplicados; documenta la limitación de trabajo redundante. No marques `SENT` si no hay entrega externa real.

### D. Tests de concurrencia y datos

Crea `backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderServiceTest.java`. Usa un reloj inyectable (`Clock`) o tiempos fijos para no depender del día de ejecución. Testea servicio con un puerto simulado y DAO con PostgreSQL descartable/Testcontainers.

Casos mínimos:

1. Dos ejecuciones simultáneas producen una sola fila por cita/hora.
2. Una cita CANCELLED no crea recordatorio.
3. Reprogramar invalida el anterior y crea uno nuevo.
4. Un fallo SQL queda registrado y el próximo ciclo sigue ejecutándose.
5. Se cierra el executor al apagar.
6. Dos tenants con IDs de cita distintos no mezclan registros.
7. Las horas cercanas a medianoche local se calculan correctamente.

`@RepeatedTest` puede estresar idempotencia, pero un test repetido sin assertion no aporta valor. Evita sleeps largos; controla executor con hooks/test fakes.

### E. Regresión A1/A2

Con Flores @wflores, reejecuta crear cita, doble reserva de doctor/horario, reprogramar, cancelar con motivo ≥5 caracteres y completar solo desde el día de cita. Avisa a Merino si su generador cambia el código `COD-####`. Conserva el código de reserva de las citas existentes.

La creación actual usa `doctorRepo.findById` y `patientRepo.findById`, y asigna tenant desde el doctor; eso no prueba que el paciente pertenezca a esa clínica. Añade un test con doctor de tenant A y paciente de tenant B que deba rechazarse sin guardar. Revisa también `cancel`, `complete` y `reschedule` por ID: un ID de otra clínica no debe permitir mutaciones. Coordina una corrección pequeña con Héctor y Merino si el test falla; la cola de recordatorios nunca debe importar citas cross-tenant.

## ✅ Cómo verificar

```bash
cd backend
mvn -q -DskipTests compile
mvn -q -Dtest=AppointmentReminderServiceTest test
cd ..
```

La prueba de migración y concurrencia usa una BD desechable. No cambies Flyway/credenciales de la base compartida para forzar un test verde. Adjunta a PR el resultado de un `SELECT` anonimizado con dos ejecuciones y una sola fila.

**Autovalidación propuesta:** `scripts/validate-b3a-reminders.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/main/resources/db/migration/V11__appointment_reminders.sql
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReminderJdbcDao.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderService.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderServiceTest.java
(cd backend && mvn -q -DskipTests compile && mvn -q -Dtest=AppointmentReminderServiceTest test)
printf 'OK B3a recordatorios\n'
```

Ejecuta `bash scripts/validate-b3a-reminders.sh`. El script no prueba por sí solo migración en BD; añade evidencia de Testcontainers.

## 📤 Commit, push y PR — acciones tuyas

Revisa staging, secretos y SQL antes del commit. No uses `git add .` ni incluyas coautoría de IA.

```bash
git status --short
git diff --check
git add backend/src/main/resources/db/migration/V11__appointment_reminders.sql backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReminderJdbcDao.java backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderService.java backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderServiceTest.java scripts/validate-b3a-reminders.sh
git diff --cached --check
git diff --cached
git commit -m "feat(reminders): persiste cola idempotente de citas con JDBC"
git push -u origin b3a-reminders-concurrency
```

PR base `feature/avance3-fase-b`; review de @hlopez, @wflores y @amerino. Comparte número de migración y modelo exacto en el grupo.

### Checklist para el PR

```markdown
## B3a — Bayron Alexander Orellana Rojas (@crislomsu)
- [ ] V11 es única y se probó desde esquema nuevo y V10.
- [ ] El worker accede a PostgreSQL vía `jdbcDataSource`.
- [ ] SELECT/INSERT/UPDATE reales están probados.
- [ ] Dos workers no duplican la misma fila.
- [ ] Cancelación/reprogramación dejan cola coherente.
- [ ] Crear/mutar citas por ID de otro tenant se rechaza con prueba.
- [ ] No se afirma que un recordatorio fue enviado sin canal de envío.
- [ ] `bash scripts/validate-b3a-reminders.sh` termina OK.
- [ ] No hay secretos, IP privadas ni PHI staged.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| Flyway dice versión duplicada | V11 es tuyo, V12 de Merino. Si aparece otra V11 ajena, renómbrala a V13 directamente y menciónalo en el PR. |
| Dos filas iguales | Comprueba constraint único real y `ON CONFLICT` contra las mismas columnas. |
| Worker no vuelve a ejecutar | Revisa excepción escapada del `Runnable` y logs; encapsula cada vuelta. |
| Horario erróneo | Compara UTC de BD con fecha local `America/El_Salvador` en test fijo. |
| `Tenant no resuelto` | Pasa tenant explícito; el worker no tiene JWT/ThreadLocal. |

## 📚 Cinco preguntas de defensa

1. **¿Por qué un scheduler?** Revisa citas periódicamente sin bloquear el request de usuario. El trabajo tiene un resultado real en PostgreSQL: una fila de recordatorio pendiente.
2. **¿Cómo evita duplicados?** La clave única de BD y el `INSERT ... ON CONFLICT` hacen idempotente la operación incluso con dos procesos. Un booleano en memoria no bastaría.
3. **¿Qué hace el DataSource?** Cada vuelta pide conexiones del pool para consultar y escribir. El DAO las devuelve con try-with-resources.
4. **¿Qué pasa con una cita cancelada?** El worker no crea recordatorio activo y actualiza el existente si la cita cambia. Las pruebas cubren cancelación y reprogramación.
5. **¿Por qué no dice “enviado”?** Esta fase guarda la cola, pero no implementa SMS/correo. La documentación distingue persistencia de entrega externa.

Lee [Vigil](Alejandro_Vigil_Ramirez.md), [Ventura](Carlos_Ventura_Velasquez.md), [Vásquez](Walter_Vasquez_Amaya.md), [Merino](Alejandro_Merino_Ventura.md) y [Flores](Walter_Flores_Hernandez.md).
