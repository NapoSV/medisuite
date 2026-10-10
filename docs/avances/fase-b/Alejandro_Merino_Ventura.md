# Guía Avance 3 Fase B — Alejandro Sebastián Merino Ventura

> Responsable: Alejandro Sebastián Merino Ventura · CIF 2026020122 · GitHub @amerino  
> Tarea: B3b, códigos de reserva únicos bajo concurrencia  
> Rama personal: `b3b-reservation-codes` · PR listo: jueves 15/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

Necesitas [B2](Alejandro_Vigil_Ramirez.md) @aavigil, [B5](Walter_Vasquez_Amaya.md) @wvasquez y [B4](Carlos_Ventura_Velasquez.md) @cventura. Coordina con [Bayron](Bayron_Orellana_Rojas.md) @borellana: V11 pertenece a sus recordatorios; tu migración nueva será V12 si el esquema exige secuencia o constraint adicional.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git log --oneline origin/feature/avance3-fase-b -25
git status --short
```

Confirma PR mergeados y que `V12__...sql` no existe ya. Puedes preparar un test de contrato del formato mientras esperas. La rama B3b debe nacer de la integradora actualizada.

## 🎯 Qué vas a hacer y por qué

Hoy `AppointmentService.create()` usa `new Random().nextInt(10000)` y guarda `COD-####`. Bajo concurrencia, dos citas pueden recibir el mismo código. El ejemplo del prompt `R-20261005-00042` tiene **16 caracteres**, pero la columna y entidad actuales permiten **10**. Tampoco `AtomicLong` resuelve unicidad entre varias instancias o reinicios.

Implementarás un generador respaldado por secuencia/constraint de PostgreSQL vía JDBC, integrado con el servicio de citas. La concurrencia se demostrará con múltiples solicitudes simultáneas y una BD real de QA. La persistencia `.dat` y scheduler del Avance 2 continúan como evidencia de evolución, pero no garantizan el código de reserva.

**Rúbrica directa:** concurrencia con BD (1.0 compartido), JDBC (1.0 compartido), colecciones/genéricos y evolución A1/A2.

## 🛠️ Preparación

Abre Git Bash en tu clon:

```bash
cd /c/Users/hlopez/medisuite
java --version
mvn --version
git fetch origin
git switch -c b3b-reservation-codes origin/feature/avance3-fase-b
git branch --show-current
```

Java debe ser 21. Una rama mantiene tu modificación aislada; un commit es una unidad revisable; un PR integra tras review. No sobrescribas una rama local existente ni uses `git reset --hard` para quitar cambios ajenos.

## ✍️ Paso a paso del código

### A. Prevalidación del esquema

Lee `Appointment.java`, `V5__appointments.sql` y `database/schema.sql`. La entidad tiene `@Column(length = 10)`; V5 crea `VARCHAR(10)`. El esquema base contiene unicidad `(tenant_id,reservation_code)`, pero V5 puede haberse aplicado en entornos donde ese constraint no esté. Consulta el catálogo de constraints en una BD de QA descartable antes de cambiarlo. No supongas que editar `database/schema.sql` modifica una BD ya migrada por Flyway.

Elige un formato de 10 caracteres, por ejemplo `R-00000001` (`R-` + 8 dígitos). Preserva los códigos existentes `COD-####`; no los reescribas. Documenta límite de ocho dígitos y falla de forma controlada si la secuencia supera el formato, en lugar de truncar y causar colisiones.

### B. Migración V12 de la secuencia/constraint

Crea `backend/src/main/resources/db/migration/V12__reservation_codes.sql` **después de mergear V11**. Usa una secuencia PostgreSQL para generar números únicos entre JVMs. Antes de añadir `UNIQUE (tenant_id, reservation_code)`, una consulta de prevalidación debe confirmar que no hay duplicados existentes. Si hay duplicados, no borres ni modifiques citas automáticamente: comunica la lista anonimizada/IDs a Héctor para reparación aprobada.

Incluye prueba desde esquema limpio y desde V11; confirma que Flyway no modifica V1–V10. Si la BD compartida usa rol runtime sin permisos DDL, la migración debe ejecutarse con el rol Flyway previsto por el proyecto, nunca elevando permisos del usuario de aplicación.

### C. DAO JDBC y generador

Crea `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReservationCodeJdbcDao.java` que use el `jdbcDataSource` B2 y el puerto B4. Obtén el siguiente valor con `SELECT nextval('...')` en `PreparedStatement`/`ResultSet`. La secuencia genera números únicos aunque varias llamadas lleguen simultáneamente. Cierra conexión y statement; convierte `SQLException` con B5.

Crea `backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGenerator.java`. Formatea el número a diez caracteres; el método `next()` no debe incluir un contador `AtomicLong` como fuente de verdad. Un `AtomicLong` puede medir intentos, pero no garantizar unicidad entre procesos. `ExecutorService` se usa en la prueba de carga concurrente; no necesitas crear un pool de hilos dentro de cada request de cita.

Si la secuencia devuelve un código ya existente por una migración mal inicializada, captura el `UNIQUE_VIOLATION` de la inserción y reintenta **la operación completa**.

> **🔒 Decisión cerrada (09/10/2026 — H. López, política de reintentos):**
> - **Máximo 3 reintentos** sobre la operación completa de `AppointmentService.create()`.
> - Entre reintentos **sin backoff** (colisión es evento raro, no congestión).
> - Al **4º intento fallido**, propaga `DataAccessException` convertida desde `JdbcErrorCode.UNIQUE_VIOLATION` (B5).
> - La transacción es **per-request** (`@Transactional` del método `create`). No hagas transacciones compuestas ni coordines con el worker de recordatorios de Bayron — son flujos independientes.
> - Log de WARN (sin PII) cada vez que haya un reintento, con el `appointmentId` tentativo.

### D. Integración con `AppointmentService`

Modifica únicamente la generación del código en `create()` y su inyección, preservando validación de fecha, doctor/paciente, disponibilidad, tenant y estado `PENDING`.

> **🔒 Decisión cerrada (09/10/2026 — H. López):** **no edites** `AppointmentServiceTest.java`. El dueño único de ese archivo es Walter Flores (B8). En el cuerpo de tu PR documenta el mock esperado para que él lo integre en su rama, por ejemplo:
>
> ```java
> // Para AppointmentServiceTest (lo integra @wflores en B8):
> @Mock ReservationCodeGenerator reservationCodeGenerator;
> when(reservationCodeGenerator.next()).thenReturn("R-00000001");
> ```
>
> Si tu rama tocó ese archivo por el `@InjectMocks` antiguo, revierte el cambio antes de abrir el PR. Flores actualizará el test al integrarse tu B3b.

Revisa que la validación de paciente/doctor pertenezca al mismo tenant antes de guardar; si descubres una falla, notifícala y abre un cambio pequeño con test. El código de reserva no debe revelar datos de paciente ni doctor.

### E. Test concurrente y regresión

Crea `backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGeneratorTest.java`. En PostgreSQL de Testcontainers, lanza 100 tareas desde un `ExecutorService` fijo de cuatro hilos; espera `CompletableFuture.allOf`, recolecta los 100 resultados y compara `Set.size() == 100`. Inserta también citas en una prueba de integración para verificar el constraint real, no solo cadenas únicas.

Casos adicionales:

1. Formato y longitud exacta de 10.
2. Concurrencia repetida sin colisiones.
3. Servicio mantiene fecha/status/tenant y persiste código.
4. Fallo de BD se propaga de forma segura, sin código falso.
5. Secuencia no reutiliza un valor al reiniciar la aplicación.
6. Límite de ocho dígitos produce error explícito.
7. Citas históricas con `COD-####` siguen consultables.

No hagas `@RepeatedTest(10)` con 100 inserciones permanentes en una BD compartida. Usa una base desechable y limpieza transaccional o contenedor por suite.

### F. Documentación A1/A2

Explica a Nicole cómo la nueva garantía cambia HU-003 del Avance 1 y el módulo de citas del Avance 2. Actualiza con Carlos el diagrama UML final y entrega comparación de `Random` vs secuencia con riesgo de colisión. Aporta evidencia de la persistencia `.dat` existente, distinguiéndola de esta secuencia JDBC.

## ✅ Cómo verificar

```bash
cd backend
mvn -q -DskipTests compile
mvn -q -Dtest=ReservationCodeGeneratorTest test
mvn -q -Dtest=AppointmentServiceTest test
cd ..
```

El test concurrente debe usar PostgreSQL descartable. Valida V12 tras V11 en una DB QA; no apliques una migración experimental sobre Neon compartido. Resultado esperado: 100 códigos distintos, cada uno ≤10 caracteres, con servicio de citas anterior verde.

**Autovalidación propuesta:** `scripts/validate-b3b-codes.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/main/resources/db/migration/V12__reservation_codes.sql
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReservationCodeJdbcDao.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGenerator.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGeneratorTest.java
(cd backend && mvn -q -Dtest=ReservationCodeGeneratorTest,AppointmentServiceTest test)
printf 'OK B3b codigos de reserva\n'
```

Ejecuta `bash scripts/validate-b3b-codes.sh`. Este script no reemplaza la revisión del constraint ni la prueba de migración.

## 📤 Commit, push y PR — acciones tuyas

Antes de commit, revisa staged en busca de claves, IP privadas y archivos `.env`. No incluyas coautoría de IA.

```bash
git status --short
git diff --check
git add backend/src/main/resources/db/migration/V12__reservation_codes.sql backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReservationCodeJdbcDao.java backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGenerator.java backend/src/main/java/com/sv/grupo7/medisuite/service/AppointmentService.java backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGeneratorTest.java scripts/validate-b3b-codes.sh
git diff --cached --check
git diff --cached
git commit -m "feat(appointments): genera codigos de reserva unicos en PostgreSQL"
git push -u origin b3b-reservation-codes
```

**No incluyas `AppointmentServiceTest.java` en tu commit.** Flores es dueño único de ese archivo (ver §D). Base `feature/avance3-fase-b`; review @hlopez, @borellana y @wflores.

### Checklist para el PR

```markdown
## B3b — Alejandro Sebastián Merino Ventura (@amerino)
- [ ] B2, B4, B5 y V11 estaban integrados.
- [ ] V12 se probó desde V11 y base limpia.
- [ ] Código ≤10 caracteres y secuencia/constraint en BD.
- [ ] 100 tareas concurrentes dieron 100 códigos distintos.
- [ ] Mock esperado de `ReservationCodeGenerator` documentado en el cuerpo del PR para que @wflores lo integre en B8.
- [ ] Citas antiguas conservan sus códigos.
- [ ] `bash scripts/validate-b3b-codes.sh` termina OK.
- [ ] No hay secretos, IP privadas ni PHI staged.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| `value too long for varchar(10)` | Compara formato generado con entidad y V5; no trunques el código. |
| V12 falla por duplicados existentes | Para; presenta prevalidación y plan de reparación al equipo. |
| `@InjectMocks` falla o código nulo | No es tu archivo — documenta el mock esperado para @wflores en el PR; él actualiza `AppointmentServiceTest`. |
| 100 tareas generan menos de 100 | Comprueba secuencia de BD y Set; `AtomicLong` local no es solución distribuida. |
| Testcontainers no arranca | Verifica Docker, conserva log, no uses Neon compartido como sustituto improvisado. |

## 📚 Cinco preguntas de defensa

1. **¿Por qué `Random` era insuficiente?** Tiene espacio pequeño y puede repetir códigos. Bajo solicitudes simultáneas, esa probabilidad se vuelve un problema real de integridad.
2. **¿Por qué una secuencia de PostgreSQL?** Coordina múltiples hilos y múltiples instancias de aplicación desde la fuente de verdad. El constraint único impide aceptar un duplicado residual.
3. **¿Por qué no `AtomicLong`?** Solo protege memoria de una JVM y se reinicia. No garantiza unicidad si hay dos servidores ni tras un despliegue.
4. **¿Qué evidencia muestra concurrencia?** Un test lanza 100 tareas y compara todos los códigos; otra prueba los inserta en la BD con el constraint activo. Ambas corren sobre PostgreSQL descartable.
5. **¿Qué ocurre si falla la inserción?** La excepción se clasifica y se controla el reintento según la transacción. Nunca se confirma una cita sin un código persistido válido.

Lee [Bayron](Bayron_Orellana_Rojas.md), [Flores](Walter_Flores_Hernandez.md), [Ventura](Carlos_Ventura_Velasquez.md) y [Vásquez](Walter_Vasquez_Amaya.md).
