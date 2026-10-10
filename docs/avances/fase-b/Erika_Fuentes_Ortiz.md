# Guía Avance 3 Fase B — Erika Alexandra Fuentes Ortiz

> Responsable: Erika Alexandra Fuentes Ortiz · CIF 2026011709 · GitHub @efuentes  
> Tarea: B9, pruebas de recetas, README reproducible y QA clínica  
> Rama personal: `b-fuentes-prescriptions-tests-y-readme` · PR listo: miércoles 14/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

Puedes empezar sin dependencias técnicas cuando Héctor @hlopez confirme la rama integradora. **No esperes a nadie:** Flores es dueño de `docs/fases/casos-de-prueba.md` (matriz QA), tú solo agregas tus filas de recetas al PR final de QA cuando él abra la plantilla. Zair ejecuta el flujo expediente→receta en su rama; tú lo validas desde el lado backend sin bloquear su UI. No esperes hasta el 17/10 para actualizar el README.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git status --short
```

No cambies de rama si tienes modificaciones locales sin preservar. Si la rama remota no existe, avisa a @hlopez.

## 🎯 Qué vas a hacer y por qué

El PDF exige JUnit/Mockito con casos exitosos y de error, assertions reales, y un README que permita ejecutar y probar la aplicación. Tu trabajo técnico principal es probar `PrescriptionService` y detectar reglas de negocio faltantes. El servicio actual usa `PrescriptionRepository`, `MedicalRecordRepository` y `DoctorRepository`; **no** usa `PatientRepository` directamente. `create()` no valida de forma explícita `items` vacío o nulo, así que una prueba que espere rechazo puede revelar una brecha genuina, no un error del test.

El README es una segunda entrega útil: nombres, tecnologías, requisitos, configuración BD, ejecución, tests, cuentas demo, concurrencia y URL del repositorio. No lo reescribas borrando información válida de Avances 1/2.

**Rúbrica directa:** JUnit/Mockito (0.5 compartido), integración de avances, README/entregables.

## 🛠️ Preparación

Abre Git Bash:

```bash
cd /c/Users/hlopez/medisuite
java --version
mvn --version
docker compose version
git fetch origin
git switch -c b-fuentes-prescriptions-tests-y-readme origin/feature/avance3-fase-b
git branch --show-current
```

Java debe ser 21. Docker se usa para verificar el arranque descrito, con `.env` local ya configurado por el equipo. No copies `.env` ni credenciales de `CUENTAS_DEMO.md` al PR. Una rama aísla tu trabajo; el commit describe un cambio lógico y el PR permite revisión.

## ✍️ Paso a paso del código

### A. Lee el servicio real

`PrescriptionService.create(medicalRecordId, userId, diagnosis, notes, items)` busca expediente por ID, doctor por `userId`, combina diagnóstico/notas en `instructions`, asigna tenant del paciente y guarda. `findById()` lanza `RuntimeException` si falta receta. `PrescriptionController.CreateRequest` recibe lista de `PrescriptionItem`. Estas firmas son la base de los mocks; el prompt anterior proponía `DoctorRepository`, `MedicalRecordRepository`, `PrescriptionRepository`, lo cual coincide, pero omitía que `items` puede ser nulo.

### B. `PrescriptionServiceTest.java`

Crea `backend/src/test/java/com/sv/grupo7/medisuite/service/PrescriptionServiceTest.java` con `@ExtendWith(MockitoExtension.class)`, tres `@Mock`, `@InjectMocks` y assertions. Casos mínimos:

1. Expediente, doctor e ítems válidos: `repo.save` recibe receta con tenant, doctor, expediente e ítems enlazados.
2. Expediente inexistente: `BusinessException`, `repo.save` nunca llamado.
3. `userId` no corresponde a doctor: `BusinessException`, sin guardado.
4. Lista de ítems vacía: **ya rechazada en `PrescriptionService.create()`** con `BusinessException("La receta requiere al menos un medicamento")` (fix aplicado 09/10). Tu test solo verifica el comportamiento ya enforced; no necesitas tocar el service.
5. Lista nula: **mismo check ya aplicado** (`items == null || items.isEmpty()`). Tu test verifica.
6. Ítem con medicamento/dosis/duración inválidos: añade `@NotBlank`/`@NotNull` en `PrescriptionItem` + `@Valid` en el controller; test de 400 Bad Request. Esta parte sí la agregas tú (no está hecha).
7. `findById` existente y ausente: respuesta coherente con handler.
8. Dos ítems conservan orden y apuntan a la misma receta.
9. Usuario de tenant A no puede crear receta sobre `medicalRecordId` del tenant B, aunque conozca el ID.
10. Usuario de tenant A no puede obtener por `findById` una receta del tenant B.

No inventes `@Disabled("Feature de Fase D")`: el PDF no pide esa anotación, y un test deshabilitado no demuestra comportamiento. `@RepeatedTest` puede usarse para estabilidad de cálculos si existe un cálculo real; este servicio no calcula dosis. Prefiere seis u ocho casos significativos.

> **🔒 Decisión cerrada (09/10/2026 — H. López, fix aplicado en rama integradora):** las fugas de tenant de `PrescriptionService` (`create` y `findById`), más la validación de ítems vacíos/nulos, **ya están corregidas** en `feature/avance3-fase-b`. Antes de arrancar, haz `git pull origin feature/avance3-fase-b` para que tu rama tenga los fixes.
>
> Lo que ya está hecho:
> - `PrescriptionRepository` y `MedicalRecordRepository` tienen `findByIdAndTenantId`.
> - `PrescriptionService.create()` usa `recordRepo.findByIdAndTenantId(medicalRecordId, tid)` y valida que el doctor pertenece al tenant actual.
> - `PrescriptionService.findById()` usa `repo.findByIdAndTenantId(id, tid)`.
> - `items == null || items.isEmpty()` → `BusinessException("La receta requiere al menos un medicamento")`.
>
> Tus tests (puntos 4, 5, 9, 10) ahora **validan comportamiento ya enforced** — no deben fallar. Si fallan, es un problema de mock o setup, no de service.

### C. README raíz

Edita `README.md` de la raíz sin perder introducción ni rutas. Añade tabla de 11 integrantes y CIF según [índice](README.md) y Avance 2, pero evita duplicar credenciales. Incluye:

1. Nombre y objetivo de MediSuite.
2. Los 11 integrantes y rol técnico actual.
3. Versiones verificadas: Java 21, Spring Boot 3.3.2, React 19, Vite 8, PostgreSQL 16, Docker.
4. Requisitos: Docker Desktop; alternativa local Maven/pnpm solo si realmente funciona.
5. `.env.example`: nombres de variables y cómo obtener una BD de desarrollo/QA; no valores reales.
6. `docker compose up --build`, URLs 5173/8097 y healthcheck GET.
7. `cd backend && mvn test` y `cd frontend && pnpm build && pnpm lint`; explica Testcontainers/Docker.
8. Cuentas demo mediante enlace a `CUENTAS_DEMO.md`, sin repetir contraseña en README público.
9. Concurrencia: scheduler de respaldo `.dat` existente y B3a/B3b **solo cuando estén mergeados**; diferenciar cola de recordatorios de envío real.
10. URL pública del repositorio y estructura de `docs/`/migraciones.

Si la rama B3a/B3b aún no está integrada, deja el README con secciones marcadas “pendiente de integración” y actualízalo en un commit pequeño posterior. No afirmes que el arranque funciona sin probarlo.

### D. QA transversal A1/A2/A3

Con Zair, prueba paciente→expediente→triaje→receta→vista imprimible. Con Vigil, prueba login, bloqueo, logout, cuatro roles y dos tenants. Con Flores, registra en `docs/fases/casos-de-prueba.md` resultados fechados, evidencia y errores reproducibles. Revisa que `README.md` explique el stack de verdad y no una arquitectura aspiracional.

## ✅ Cómo verificar

```bash
cd backend
mvn -q -Dtest=PrescriptionServiceTest test
cd ..
docker compose config
docker compose up --build
```

`docker compose config` puede mostrar variables expandidas: **no pegues su salida en PR o chat**. Ejecuta el arranque solo en tu entorno local de demo, sin tocar Neon compartido. En otra terminal, consulta `curl http://localhost:8097/actuator/health` desde Git Bash y abre frontend; verifica receta de prueba. Si el arranque falla, documenta el error real en README/troubleshooting antes de darlo por reproducible.

**Autovalidación propuesta:** `scripts/validate-b9-prescriptions.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/PrescriptionServiceTest.java
test -f README.md
(cd backend && mvn -q -Dtest=PrescriptionServiceTest test)
printf 'OK B9 recetas y README\n'
```

Ejecuta `bash scripts/validate-b9-prescriptions.sh`; la verificación manual de Docker y navegador queda documentada aparte.

## 📤 Commit, push y PR — acciones tuyas

Antes del commit revisa staged en busca de claves, tokens, IP privadas y datos clínicos reales. Un README de demo no debe publicar secretos.

```bash
git status --short
git diff --check
git add backend/src/test/java/com/sv/grupo7/medisuite/service/PrescriptionServiceTest.java README.md scripts/validate-b9-prescriptions.sh
git diff --cached --check
git diff --cached
git commit -m "test(prescriptions): cubre reglas clinicas y documenta ejecucion"
git push -u origin b-fuentes-prescriptions-tests-y-readme
```

Si arreglaste `PrescriptionService`, agrégalo en un commit separado con test y explicación. PR base `feature/avance3-fase-b`; review @hlopez y @wflores. No incluyas coautoría de IA.

### Checklist para el PR

```markdown
## B9 — Erika Alexandra Fuentes Ortiz (@efuentes)
- [ ] `PrescriptionServiceTest` cubre éxito, errores y assertions reales.
- [ ] Lista vacía/nula rechazada con `BusinessException` y test verde.
- [ ] Crear/leer receta de otro tenant por ID es rechazado por el backend.
- [ ] README describe el estado integrado, no planes futuros como terminados.
- [ ] `mvn -Dtest=PrescriptionServiceTest test` pasa.
- [ ] Arranque Docker, health GET y flujo de receta probados o bloqueo descrito.
- [ ] `bash scripts/validate-b9-prescriptions.sh` termina OK.
- [ ] Documenté los hallazgos QA A1/A2 con fecha.
- [ ] Staging no contiene secretos, IP privadas ni PHI.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| Test de lista vacía falla | Agrega la validación `if (items == null || items.isEmpty()) throw new BusinessException(...)` en `create()`. |
| `@InjectMocks` tiene dependencia nula | Usa los tres mocks reales de `PrescriptionService`; revisa constructor Lombok. |
| Docker dice variable no definida | Revisa nombres en `.env.example` y tu `.env` local sin compartir valores. |
| Testcontainers falla | Verifica Docker Desktop y conserva log sin credenciales. |
| README contradice código | Corrige la frase según `pom.xml`, `package.json` y PR integrados. |

## 📚 Cinco preguntas de defensa

1. **¿Qué probaste en recetas?** Los caminos de creación válida y errores de expediente, doctor e ítems. Las assertions verifican datos guardados y que el repositorio no se llama en fallos.
2. **¿Para qué Mockito?** Aísla repositorios para probar la regla del servicio. La integración con PostgreSQL se comprueba en pruebas separadas y en demo.
3. **¿Por qué un ítem vacío importa?** Una receta sin medicamento no es clínicamente válida: el servicio la rechaza con `BusinessException` antes de cualquier escritura. El test de lista vacía/nula verifica la regla y evita un `NullPointerException`.
4. **¿Qué necesita el README?** Pasos reproducibles para instalar, configurar BD, ejecutar, probar y entrar con cuentas demo. Solo documenta funciones que el equipo ha integrado.
5. **¿Cómo conectas A1/A2/A3?** A1 define roles y paciente; A2 implementa expediente y receta; A3 exige pruebas, excepciones, documentación y persistencia verificables.

Lee [Zair](Zair_Diaz_Santos.md), [Vigil](Alejandro_Vigil_Ramirez.md) y [Flores](Walter_Flores_Hernandez.md) para la matriz QA.
