# Guía Avance 3 Fase B — Héctor Napoleón López Ruiz

> Responsable: Héctor Napoleón López Ruiz · CIF 2026010132 · GitHub @hlopez  
> Tarea: B1, dashboard JDBC seguro y coordinación de entrega  
> Rama personal: `b1-dashboard-backend` · PR listo: jueves 15/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

**Estado del 06/10/2026:** la rama integradora ya está publicada desde `a1b6dc0`. La implementación preparatoria de B1 está publicada en `b1-dashboard-backend` (`85b82b3`) y su [PR #41](https://github.com/NapoSV/medisuite/pull/41) está en borrador. B2/B4/B5 siguen siendo dependencias para la integración final; no impiden preparar código, pruebas y contrato de B1. No marques como completada la QA manual de dos tenants ni retires el borrador hasta probar el conjunto integrado.

Antes de **integrar o cerrar B1**, confirma que están mergeados [B2 de Vigil](Alejandro_Vigil_Ramirez.md) @aavigil, [B5 de Vásquez](Walter_Vasquez_Amaya.md) @wvasquez y [B4 de Ventura](Carlos_Ventura_Velasquez.md) @cventura. La preparación de B1 y su PR borrador pueden avanzar sin esos merges. Eres también el punto de contacto para comunicar a los 10 compañeros la base exacta de `feature/avance3-fase-b`.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git log --oneline origin/feature/avance3-fase-b -25
git status --short
```

Verifica PR mergeados en GitHub, no solo coincidencia de texto de commit. Si falta cualquiera, define DTO/contrato con William mientras esperas, sin importar clases inexistentes a la rama integradora.

## 🎯 Qué vas a hacer y por qué

El dashboard actual devuelve cuatro números de repositorios JPA. Su controlador acepta `tenantId` como parámetro con default `1`, y `prescriptions.count()` y `audit.count()` cuentan todas las clínicas. Esto es un riesgo real de aislamiento multi-tenant. Tu trabajo es sustituir el origen de las métricas por JDBC parametrizado y definir un contrato donde **tenant, rol y doctor se derivan del usuario autenticado**, no del navegador.

El cambio aporta JDBC explícito a A3 y corrige una brecha del A1/A2. Mantén los flujos que hoy tiene `DashboardPage`: carga, error, 4 KPI y acciones por rol, hasta que el nuevo contrato esté acordado con William.

**Rúbrica directa:** JDBC/CRUD (1.0 compartido), capas (0.8), cohesión/acoplamiento (0.5 compartido) y evolución A1/A2.

## 🛠️ Preparación

Abre Git Bash. Una rama aísla un cambio; un commit registra una unidad lógica; el PR permite revisión. En tu clon:

```bash
cd /c/Users/hlopez/medisuite
java --version
mvn --version
git fetch origin
git switch -c b1-dashboard-backend origin/feature/avance3-fase-b
git branch --show-current
```

Si hay trabajo local, preserva cambios antes de cambiar de rama. Si la rama ya existe, `git switch b1-dashboard-backend`. No uses `git reset --hard` ni reescribas el código de William.

## ✍️ Paso a paso del código

### A. Contrato API acordado antes del DAO

Comparte con [William](William_Melgar_Rivas.md) @wmelgar una respuesta JSON exacta. Propuesta, sujeta a los datos reales:

```json
{
  "appointmentsToday": 0,
  "activePatients": 0,
  "prescriptionsThisWeek": 0,
  "criticalAlerts": 0,
  "waitingRoom": [],
  "occupancyByHour": []
}
```

`waitingRoom` contiene solo pacientes con cita/estado de espera que la BD realmente representa; `occupancyByHour` cuenta citas por hora y no inventa consultorios o porcentaje de capacidad. Si no existe un estado de sala de espera confiable, acuerda con William una etiqueta honesta y un array vacío, o registra una mejora de esquema con dueño. Nunca muestres datos clínicos sensibles en un dashboard amplio sin autorización. Congela la forma de `WaitingRoomEntry`, `CriticalAlert` y `OccupancySlot` antes del frontend; define si todos los roles pueden recibir cada lista.

### B. Autorización desde el servidor

`DashboardController.java` actual está en `backend/src/main/java/com/sv/grupo7/medisuite/controller/api/`. Retira `@RequestParam tenantId`. Usa el `Authentication` existente, cuyo principal es un `Long userId`, y `TenantContext.currentTenantId()` para tenant. Resuelve el rol desde la identidad del servidor y, para DOCTOR, su `doctor_id` por `DoctorRepository.findByUserId(userId)`. No aceptes `doctorId` de un query string sin comprobar que corresponda al usuario.

Matriz mínima:

| Rol | Puede consultar | Filtro del servidor |
|---|---|---|
| ADMIN | Agregados del tenant | `tenant_id` obligatorio. |
| DOCTOR | Sus citas/recetas/alertas permitidas | `tenant_id` y `doctor_id` obligatorio. |
| NURSE | Triaje y pacientes autorizados | `tenant_id`, sin campos de receta no necesarios. |
| RECEPTIONIST | Citas y pacientes de recepción | `tenant_id`, sin detalle clínico. |

Aunque el frontend oculte una tarjeta, el backend debe impedir exposición. Prueba dos tenants distintos y usuario DOCTOR de otro médico.

### C. DAO y DTO

Crea `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java`: extiende la base B4 o usa sus operaciones protegidas, implementa `MetricsPort`, recibe `@Qualifier("jdbcDataSource")`. Cada sentencia lleva `tenant_id = ?`; donde aplique agrega `doctor_id = ?`. Usa `PreparedStatement`, `ResultSet` y cierre de recursos real. Calcula inicio/fin del día en `America/El_Salvador` y pasa límites como timestamps; no mezcles UTC y fecha local de forma accidental.

Crea DTOs en `dto/dashboard/`: `DashboardResponse`, `WaitingRoomEntry`, `CriticalAlert`, `OccupancySlot`. Preferir `record` inmutable si encaja con Spring/Jackson. No serialices entidades JPA completas. Documenta un contrato de tiempos/zonas y qué significa cada métrica.

> **🔒 Decisión cerrada (09/10/2026, commit `f78e73a`):** la prioridad clínica usa `NORMAL`, `URGENTE`, `EMERGENCIA` — es el constraint efectivo en BD y los seeds de V8. El archivo `database/schema.sql` (que menciona `LOW/MEDIUM/HIGH/CRITICAL`) está desactualizado y se corrige en un PR aparte. En `criticalAlerts` filtra por `priority = 'EMERGENCIA'`.

`prescriptionsThisWeek` debe filtrar fecha y tenant. `activePatients` no puede usar `COUNT(*)` global. `alerts` del viejo dashboard cuenta logs de auditoría y no es equivalente a alertas clínicas: cambia nombre y lógica con transparencia.

### D. Service y controller

Refactoriza `DashboardMetricsService.java` para depender del puerto `MetricsPort`. Mantén la lógica de autorización/alcance en el service y la respuesta HTTP en `DashboardController`. No uses `CompletableFuture.supplyAsync()` con `TenantContext` ThreadLocal esperando que se propague; pasa `tenantId`, `userId` y `doctorId` explícitamente a cada tarea. Si conservas paralelismo, usa un executor acotado con shutdown y test de errores, o explica en el documento por qué consultas JDBC en paralelo no aportan valor con el pool de cuatro conexiones.

No rompas la función concurrente previa del Avance 2 sin dejar otra evidencia A2 o explicar su reemplazo. Los nuevos recordatorios y códigos de B3a/B3b cubrirán la concurrencia A3; documenta la evolución.

### E. Tests significativos

Crea `DashboardMetricsServiceTest.java` y test del controller. Casos:

1. ADMIN del tenant 1 no recibe conteos del tenant 2.
2. DOCTOR solo ve sus citas, aun si cambia el request.
3. NURSE y RECEPTIONIST reciben exactamente campos autorizados.
4. Error de conexión resulta en respuesta segura del B5.
5. `waitingRoom` y `criticalAlerts` vacíos se serializan de forma estable.
6. Conteo semanal atraviesa el domingo/lunes con zona `America/El_Salvador`.

El test de controller debe comprobar al menos una respuesta 200 del rol autorizado y una 403 del rol no autorizado. Si usas `MockMvc`, aplica la configuración de seguridad real o un slice que incluya explícitamente la autorización; un test con seguridad deshabilitada no prueba ese criterio.

Incluye test JDBC con PostgreSQL descartable para al menos una query real. Los mocks del service no prueban SQL ni aislamiento.

### F. Coordinación del proyecto completo

Abre una tabla de seguimiento con enlace a los once PR, pruebas, revisor y criterio rubricado. Asegúrate de que Nicole reciba el contrato A1→A2→A3, que Carlos actualice UML y que cada integrante aporte una conclusión y 5 respuestas de defensa. Reserva 18/10 para demo y 19/10 para correcciones, no para código nuevo grande. El documento final debe nombrar el objetivo específico final OE3 y explicar cambios respecto al Avance 1.

## ✅ Cómo verificar

```bash
cd backend
mvn -q -DskipTests compile
mvn -q -Dtest=DashboardMetricsServiceTest test
cd ..
```

En QA con dos tenants, consulta `GET /api/dashboard/metrics` usando sesiones de ADMIN, DOCTOR, NURSE y RECEPTIONIST. No pegues JWT en PR. Comprueba que el endpoint ya no acepta `tenantId` para cambiar de clínica. Resultado esperado: mismo tenant del token y solo información permitida.

**Autovalidación propuesta:** `scripts/validate-b1-dashboard.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/DashboardResponse.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/DashboardMetricsServiceTest.java
(cd backend && mvn -q -DskipTests compile && mvn -q -Dtest=DashboardMetricsServiceTest test)
printf 'OK B1 dashboard backend\n'
```

Ejecuta `bash scripts/validate-b1-dashboard.sh`. Adjunta además evidencia manual de dos tenants: el script no la sustituye.

## 📤 Commit, push y PR — acciones tuyas

Revisa el staging antes de commitear. Para tu proyecto el commit/push los ejecutas tú desde Git Bash; no incluyas coautoría de IA.

```bash
git status --short
git diff --check
git add backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/DashboardResponse.java backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/WaitingRoomEntry.java backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/CriticalAlert.java backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/OccupancySlot.java backend/src/main/java/com/sv/grupo7/medisuite/service/DashboardMetricsService.java backend/src/main/java/com/sv/grupo7/medisuite/controller/api/DashboardController.java backend/src/test/java/com/sv/grupo7/medisuite/service/DashboardMetricsServiceTest.java scripts/validate-b1-dashboard.sh
git diff --cached --check
git diff --cached
git commit -m "feat(dashboard): calcula metricas JDBC con aislamiento por rol y tenant"
git push -u origin b1-dashboard-backend
```

Agrega tests de controller con su ruta exacta si los creaste. PR base `feature/avance3-fase-b`; solicita review de @wmelgar y @aavigil. Comunica contrato final a William antes de mergear.

### Checklist para el PR

```markdown
## B1 — Héctor Napoleón López Ruiz (@hlopez)
- [ ] B2, B4 y B5 estaban integrados.
- [ ] El endpoint no permite elegir tenant por parámetro.
- [ ] Todas las queries filtran tenant; DOCTOR también filtra doctor.
- [ ] Conteos globales de recetas/auditoría fueron sustituidos o justificados.
- [ ] Tests unitarios y JDBC real en BD de QA pasan.
- [ ] `bash scripts/validate-b1-dashboard.sh` termina OK.
- [ ] William recibió tipos JSON y permisos por rol.
- [ ] Ningún secreto, token o dato clínico real está staged.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| `Tenant no resuelto` | Verifica que la ruta atraviesa `JwtAuthenticationFilter` y que el contexto se usa en hilo del request; en workers pásalo explícito. |
| Conteos distintos entre roles | Compara filtros `tenant_id`, `doctor_id`, fecha y zona en cada SQL. |
| Frontend queda vacío | Compara JSON real con `frontend/src/api/dashboard.ts` y coordina con William. |
| Pool agotado | Revisa cierre de conexiones y número de consultas concurrentes. |
| PR compila pero filtra datos | No lo merges; reproduce con dos tenants y corrige SQL. |

## 📚 Cinco preguntas de defensa

1. **¿Por qué cambiar el controlador?** Antes aceptaba un tenant arbitrario del request. Ahora el tenant proviene del token verificado por el servidor, lo que evita consultar otra clínica.
2. **¿Qué demuestra JDBC aquí?** El DAO crea `PreparedStatement`, ejecuta consultas y mapea `ResultSet` a DTO. Cada operación cierra recursos y usa el pool designado.
3. **¿Qué significa “alerta crítica”?** Es un conteo/lista basado en prioridad clínica persistida, no en logs de auditoría. Debe estar filtrado por clínica y permisos del rol.
4. **¿Cómo se comporta DOCTOR?** Su identidad del token se relaciona con un `doctor_id` autorizado. Las consultas agregan ese filtro; ocultar tarjetas en React no basta.
5. **¿Cómo se integra A1/A2/A3?** A1 aporta roles y tenant, A2 el dashboard y sus módulos, A3 acceso JDBC, arquitectura y pruebas. La nueva implementación conserva el flujo anterior y corrige el aislamiento.

Antes de cerrar, lee todas las otras guías y asegúrate de que los once puedan explicar el proyecto completo en la defensa.
