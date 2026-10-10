# Guía Avance 3 Fase B — Walter Alejandro Flores Hernández

> Responsable: Walter Alejandro Flores Hernández · CIF 2026011012 · GitHub @wflores  
> Tarea: B8, regresión A1/A2/A3, pruebas y validación integral  
> Rama personal: `b-flores-tests-y-migration-helper` · PR listo: viernes 16/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

Puedes preparar la matriz de casos desde el inicio. Para las pruebas que usan `JdbcErrorCode`, espera [B5 de Vásquez](Walter_Vasquez_Amaya.md) @wvasquez. Coordina con [Merino](Alejandro_Merino_Ventura.md) @amerino antes de editar `AppointmentServiceTest`, porque su nuevo constructor exige mock adicional. [Bayron](Bayron_Orellana_Rojas.md) @borellana es el único dueño de V11; tú validas su migración, no creas otra.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git log --oneline origin/feature/avance3-fase-b -25
git status --short
```

Confirma PR mergeados cuando una prueba los importe. Un texto de commit no sustituye una compilación de la rama integrada.

## 🎯 Qué vas a hacer y por qué

El Avance 3 pide pruebas de service, controller, utilidades, validaciones, reglas de negocio, éxito y error. El prompt recibido se centraba en añadir nueve anotaciones JUnit a tres archivos; el PDF no exige esa lista. Tu objetivo es mejorar pruebas que **puedan fallar ante un bug real**, mantener los asserts existentes y encontrar regresiones de los Avances 1/2.

Hay un riesgo concreto: `MedicalRecordService.findFullByPatientId()` filtra el expediente por tenant, pero en el fallback llama `patientRepo.findById(patientId)` sin tenant. `addVitalSign()` también usa `findById(patientId)` antes de crear/asociar signos. Debes probar que un usuario de la clínica A no pueda provocar cambios en un paciente de la clínica B, y coordinar corrección con Héctor si falla. La seguridad multi-tenant de A1 no puede darse por cumplida sin esa prueba.

**Rúbrica directa:** JUnit/Mockito (0.5 compartido), evolución de avances (0.5), pruebas y calidad de datos.

## 🛠️ Preparación

Abre Git Bash en tu clon:

```bash
cd /c/Users/hlopez/medisuite
java --version
mvn --version
docker compose version
git fetch origin
git switch -c b-flores-tests-y-migration-helper origin/feature/avance3-fase-b
git branch --show-current
```

Java debe ser 21 y Docker debe estar disponible para Testcontainers. Una rama aísla cambios, un commit captura una unidad lógica y un PR permite review. No uses una base Neon compartida para cargar datos de prueba ni modificar migraciones.

## ✍️ Paso a paso del código

### A. `AppointmentServiceTest.java`

> **🔒 Decisión cerrada (09/10/2026 — H. López):** tú (@wflores) eres el **único dueño** de `AppointmentServiceTest.java`. Merino (B3b) **no** edita este archivo. En su PR, Merino solo documenta en el PR body el formato esperado del mock (ejemplo: `when(reservationCodeGenerator.next()).thenReturn("R-00000001")`) y tú lo integras en tu rama B8. Si Merino incluye un cambio al archivo por error, pídele que lo saque — no hay dueño compartido.

El archivo actual tiene seis métodos `@Test`, no siete. Conserva los seis asserts y añade casos que faltan: reprogramar a slot ocupado, cancelar con motivo muy largo, completar cita ya cancelada, código de reserva único tras integrar B3b y paciente/doctor de otro tenant. Reemplaza fechas dependientes de `now()` cerca de medianoche por reloj controlable o márgenes amplios para evitar falsos fallos.

Cuando B3b esté mergeado y `AppointmentService` reciba `ReservationCodeGenerator` en su constructor, agrega el mock de esa dependencia en tu setup. Define un valor de retorno estable (ejemplo: `"R-00000001"`) para que `reservationCode` no quede nulo en el test de éxito.

### B. `PatientServiceTest.java`

El archivo ya tiene casos de búsqueda y actualización. Añade casos de tenant distinto, DUI duplicado y validación de campos si el servicio lo exige. Usa `verify` para asegurar que la búsqueda llama al repositorio con tenant del contexto. Una prueba que solo comprueba `not null` no detecta fuga entre clínicas.

### C. `MedicalRecordServiceTest.java`

Añade dos pruebas adversariales:

1. `findFullByPatientId()` con `TenantContext=1`, expediente ausente en tenant 1 y paciente ID perteneciente a tenant 2: debe rechazar sin crear expediente.
2. `addVitalSign()` con el mismo cruce: debe rechazar sin guardar signos ni expediente.
3. Una receta de tenant B buscada por ID con sesión de A debe ser rechazada.
4. Crear cita con doctor de A y paciente de B debe rechazarse.

> **🔒 Decisión cerrada (09/10/2026 — H. López, fix aplicado en rama integradora):** las fugas multi-tenant de `MedicalRecordService`, `PrescriptionService`, `AppointmentService`, `DoctorService` y `PatientService` **ya están corregidas** en `feature/avance3-fase-b`. Antes de arrancar B8, haz `git pull origin feature/avance3-fase-b` para que tu rama tenga los fixes.
>
> Los cambios ya aplicados:
> - `PatientRepository`, `MedicalRecordRepository`, `PrescriptionRepository`, `AppointmentRepository`, `DoctorRepository` tienen `findByIdAndTenantId(Long id, Long tenantId)`.
> - Los services usan `findByIdAndTenantId(id, TenantContext.currentTenantId())` en todos los puntos de lectura por ID.
> - Tus tests adversariales ahora validan **comportamiento ya enforced**, no revelan defectos nuevos. Si uno falla, el culprit es el test (mock o setup), no el service.
>
> **Tu B8 añade pruebas de contrato:** verifica que `findByIdAndTenantId` se invoca con el tenant correcto, y que un ID de otro tenant devuelve `Optional.empty()` → `BusinessException("…no existe")`. No reescribas el service. La prioridad válida/inválida se prueba contra `NORMAL/URGENTE/EMERGENCIA` (commit `f78e73a`).

### D. JUnit con propósito

Usa `@BeforeEach` para datos comunes, `@DisplayName` para explicar una regla, `@Tag` para distinguir unit/integration y `@RepeatedTest` si hay riesgo de no determinismo. `@BeforeAll`/`@AfterAll` solo cuando se abre/cierra un recurso compartido. `@Disabled` exige una razón real y una incidencia enlazada; no crees una prueba de “feature futura” solo por la anotación. En cada nuevo caso debe haber `assertEquals`, `assertThrows`, `assertFalse` o `verify` que falle si el código se rompe.

### E. Migraciones y script integral

Revisa V11 de Bayron y V12 de Merino en PostgreSQL descartable, desde un esquema nuevo y desde V10. Verifica constraint único, FK, índices y error claro al duplicar recordatorio/código. En la BD compartida usa consultas de solo lectura para verificar el estado antes de proponer DDL; el usuario runtime podría no tener rol propietario.

Crea `scripts/validate-all.sh` con checks reales, no `grep` de anotaciones. Contenido inicial completo:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
(cd backend && mvn test)
(cd frontend && pnpm build && pnpm lint)
printf 'OK compilacion, pruebas unitarias y frontend\n'
```

Si el equipo decide ejecutar Testcontainers en este script, añade `(cd backend && mvn -Dtest=PatientRepositoryIT test)` solo tras verificar que Docker está listo y que Flyway levanta esquema descartable. El script debe devolver exit code distinto de cero ante cualquier fallo; una línea `echo OK` no basta para aceptar el PR.

### F. Casos manuales A1/A2/A3

Actualiza `docs/fases/casos-de-prueba.md` o una tabla vinculada con: fecha, rol, tenant, datos sintéticos, pasos, esperado, observado, evidencia y estado. Reejecuta al menos login/bloqueo; alta/búsqueda de pacientes; cita y doble reserva; cancelación con motivo; triaje; expediente; receta e impresión; dashboard por rol; separación de tenants; recordatorios/códigos; error de BD; arranque Docker.

Pide a Zair el resultado del contrato de `priority`: `database/schema.sql` enumera `LOW/MEDIUM/HIGH/CRITICAL`, frontend/service usan `NORMAL/URGENTE/EMERGENCIA`. Verifica el constraint **efectivo** en QA antes de aprobar. Si hay contradicción, asigna corrección a dueño frontend/backend y vuelve a probar.

## ✅ Cómo verificar

```bash
cd backend
mvn -q -Dtest=AppointmentServiceTest,PatientServiceTest,MedicalRecordServiceTest test
cd ..
bash scripts/validate-all.sh
```

El resultado esperado es tres clases de test verdes y build completo verde. Reporta por separado los casos manuales y las migraciones; el script no los certifica. Si Maven ejecuta solo tests `*Test` y no `*IT`, corre el test de integración explícitamente con Docker activo.

**Autovalidación específica:** `scripts/validate-b8-regression.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f scripts/validate-all.sh
for file in AppointmentServiceTest PatientServiceTest MedicalRecordServiceTest; do
  test -f "backend/src/test/java/com/sv/grupo7/medisuite/service/${file}.java"
done
(cd backend && mvn -q -Dtest=AppointmentServiceTest,PatientServiceTest,MedicalRecordServiceTest test)
printf 'OK B8 regresion\n'
```

Ejecuta `bash scripts/validate-b8-regression.sh`. La actualización de `AppointmentServiceTest` debe quedar coordinada con Merino.

## 📤 Commit, push y PR — acciones tuyas

Revisa staged y secretos. No hagas `git add .` ni incluyas coautoría de IA.

```bash
git status --short
git diff --check
git add backend/src/test/java/com/sv/grupo7/medisuite/service/AppointmentServiceTest.java backend/src/test/java/com/sv/grupo7/medisuite/service/PatientServiceTest.java backend/src/test/java/com/sv/grupo7/medisuite/service/MedicalRecordServiceTest.java scripts/validate-all.sh scripts/validate-b8-regression.sh docs/fases/casos-de-prueba.md
git diff --cached --check
git diff --cached
git commit -m "test(clinical): cubre regresion de citas y aislamiento de expedientes"
git push -u origin b-flores-tests-y-migration-helper
```

Si la corrección de `MedicalRecordService` se confirma, usa un commit separado con su test, agrega solo rutas necesarias y solicita revisión de @hlopez. PR base `feature/avance3-fase-b`; review @hlopez, @amerino y @zsantos.

### Checklist para el PR

```markdown
## B8 — Walter Alejandro Flores Hernández (@wflores)
- [ ] Tests nuevos tienen assertions que fallan ante un defecto real.
- [ ] Caso cross-tenant de expediente y signos fue probado y, si falló, corregido antes de integración.
- [ ] B3b coordinó edición de `AppointmentServiceTest`.
- [ ] V11 y V12 probadas en BD descartable desde esquema nuevo/V10.
- [ ] Prioridad de triaje coincide con constraint efectivo.
- [ ] `bash scripts/validate-all.sh` y B8 terminan OK.
- [ ] Casos manuales tienen fecha, rol, esperado y observado.
- [ ] Staging no tiene secretos, IP privadas ni datos clínicos reales.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| `AppointmentServiceTest` usa constructor viejo | Agrega el mock de `ReservationCodeGenerator` en tu setup (tú eres dueño del archivo; Merino no lo edita). |
| Test cross-tenant falla | Registra repro, bloquea merge y corrige consulta con tenant explícito. |
| `PatientRepositoryIT` no corre con `mvn test` | Invócalo con `-Dtest=PatientRepositoryIT` y Docker activo. |
| V11/V12 falla en Neon | Detén la operación; reproduce en BD desechable y revisa rol Flyway. |
| Script imprime OK aunque hubo error | Revisa `set -euo pipefail` y paréntesis de subcomandos; elimina `|| true`. |

## 📚 Cinco preguntas de defensa

1. **¿Qué diferencia hay entre prueba unitaria e integración?** La unitaria aísla el servicio con mocks y comprueba reglas. La integración ejecuta SQL/migraciones contra PostgreSQL descartable.
2. **¿Por qué probar dos tenants?** El sistema comparte esquema; cambiar un ID no debe permitir leer o escribir en otra clínica. El test debe cubrir tanto consulta como creación de datos.
3. **¿Por qué las anotaciones no bastan?** Una prueba puede tener muchas anotaciones y cero assertions útiles. La evidencia es que falla cuando la regla se rompe.
4. **¿Qué cubre `validate-all.sh`?** Pruebas backend, build y lint frontend con exit code real. Las pruebas manuales y de migración quedan documentadas aparte.
5. **¿Qué haces con un bug descubierto antes del cierre?** Documento pasos y evidencia, asigno dueño, agrego prueba que lo reproduzca y verifico la corrección en rama integradora.

Lee [Merino](Alejandro_Merino_Ventura.md), [Bayron](Bayron_Orellana_Rojas.md), [Zair](Zair_Diaz_Santos.md) y [Nicole](Nicole_Sanchez_Menjivar.md).
