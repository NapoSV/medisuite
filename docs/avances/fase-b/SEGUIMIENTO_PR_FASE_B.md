# Seguimiento de integración — Avance 3 Fase B

Coordinación: Héctor López. Cierre interno: **19/10/2026**. Entrega externa informada por el equipo: **25/10/2026**. Anotar el enlace real y evidencia solo cuando existan; una casilla vacía no significa terminado.

| B | Responsable | Entregable principal | PR / enlace | Pruebas o evidencia | Revisor sugerido | Estado |
|---|---|---|---|---|---|---|
| B1 | Héctor López | Dashboard JDBC y alcance por rol/tenant | [PR #41](https://github.com/NapoSV/medisuite/pull/41) | 12 pruebas B1 pasan; `DashboardJdbcDao` validado contra PostgreSQL 16 con Testcontainers | William, Vigil | ✅ Mergeado 10/10 (BROJAS) |
| B2 | Alejandro Vigil | Pool `jdbcDataSource` y 15 parámetros | [PR #43](https://github.com/NapoSV/medisuite/pull/43) | `JdbcPoolConfigTest` con `ApplicationContextRunner`; Hikari sigue `@Primary` | Héctor | ✅ Mergeado 11/10 (Héctor) |
| B3a | Bayron Orellana | Recordatorios concurrentes y V11 | [PR #49](https://github.com/NapoSV/medisuite/pull/49) | 10 pruebas (3 mock + 7 Testcontainers); V11 con `UNIQUE(tenant_id, appointment_id, scheduled_for)`; worker `ScheduledExecutorService` con lifecycle | Carlos | ✅ Mergeado 11/10. ⚠️ V11 omitió GRANT a `clinica_app`; corregido retroactivamente por V13 (ver sección "Infraestructura de permisos") |
| B3b | Alejandro Merino | Códigos de reserva y V12 | [PR #54](https://github.com/NapoSV/medisuite/pull/54) | V12 con `reservation_code_seq` + check de duplicados; validaciones de concurrencia (100 tareas) | Bayron, Héctor | 🟠 Request changes 13/10 por Héctor: (1) `AppointmentServiceTest.create_exitoso_...` falla con NPE porque el mock de `ReservationCodeGenerator` falta — responsabilidad del autor, no delegable; (2) V12 debe traer `GRANT USAGE,SELECT ON reservation_code_seq` como cinturón doble (desde V13 ya habría default); (3) verificado en Neon: 0 duplicados en `reservation_code`, el check de abort pasa limpio |
| B4 | Carlos Ventura | DAO abstracto genérico, puertos y UML | [PR #48](https://github.com/NapoSV/medisuite/pull/48) | `BaseJdbcDao` + 3 puertos `MetricsPort`/`ReminderPort`/`ReservationCodePort`; UML `.drawio`+`.png`; §D polimorfismo cerrado (`DashboardJdbcDao implements MetricsPort`, mock de puerto en los tests, adaptador temporal `DashboardMetricsReader` eliminado) | Héctor + Bayron | ✅ Mergeado 11/10 |
| B5 | Walter Vásquez | Excepciones JDBC seguras | [PR #45](https://github.com/NapoSV/medisuite/pull/45) | SQLState mapeado, respuestas HTTP sanitizadas | Héctor | ✅ Mergeado 09/10 17:59 |
| B6 | William Melgar | Dashboard UI | pendiente | Estados de carga/error, roles y contrato | Héctor | 🔴 **Sin iniciar** — rama no existe al 13/10. Contrato `B1_CONTRATO_DASHBOARD.md` publicado desde el 06/10 |
| B7 | Zair Díaz | Expediente UI | pendiente | Navegación y prioridades de triaje | Flores | 🟠 Rama `b-expediente-ui` tiene un commit (`df0db75` 11/10) con **4 archivos vacíos** de scaffolding. PR #51 abierto el 11/10 pero mal direccionado (fase-b → main con 90+ archivos) y **cerrado por Héctor el 13/10**. Falta abrir PR correcto `b-expediente-ui → feature/avance3-fase-b` |
| B8 | Walter Flores | Regresión y seguridad multi-tenant | [PR #53](https://github.com/NapoSV/medisuite/pull/53) | 22 pruebas de servicios + `ReminderMigrationIT` (2 casos); scripts `validate-b8-regression.sh` y `validate-all.sh`; detectó la necesidad de validar V12 cuando esté disponible | Héctor | 🟡 Borrador 12/10 — lint del frontend falla (15 errores + 3 warnings); pendientes manuales A1/A2/A3 y validar V12 |
| B9 | Erika Fuentes | Pruebas de recetas y README | pendiente | Pruebas entre tenants y guía ejecutable | Flores | 🔴 **Sin iniciar** — sin rama ni PR al 13/10 |
| B10 | Nicole Sánchez | Cohesión, legibilidad y documento final | [PR en rama `b7-nicole-docs-y-tests`](https://github.com/NapoSV/medisuite/tree/b7-nicole-docs-y-tests) | Documentación arquitectura real + pruebas de aislamiento | Héctor | 🟡 Rama activa al 13/10 con commit "test(docs): verifica aislamiento y documenta diseno real"; sin PR abierto todavía |

## Merges adicionales a `feature/avance3-fase-b` ya aplicados

- **[PR #42](https://github.com/NapoSV/medisuite/pull/42)** — frontend `axios` 1.20 y migración de `npm` a `pnpm` en Docker. Mergeado 09/10 17:40.
- **[PR #46](https://github.com/NapoSV/medisuite/pull/46)** — workflow CI de PR (backend + frontend + PostgreSQL) con Ubuntu 24.04. Mergeado 09/10 16:33.
- **[PR #40](https://github.com/NapoSV/medisuite/pull/40)** — bump `axios` 1.19→1.20 de Dependabot contra `main`. **Cerrado 09/10 sin mergear**: duplicaba el cambio ya resuelto por #42 en fase-b. El upgrade llegará a `main` por la vía oficial cuando se mergee Fase B.

## Puertas de integración

1. Confirmar rama base `feature/avance3-fase-b` y PR de cada dueño; no asumir merge por nombre de commit.
2. B2, B4 y B5 antes de dar B1 por integrado; B1 y B6 deben validar juntos JSON y permisos. **B5 ya está**; B2 y B4 continúan abiertos.
3. Reservar 18/10 para demo transversal y 19/10 para correcciones; cada persona aporta una conclusión y cinco respuestas de defensa.
4. Verificar A1/A2/A3 contra código, tests, migraciones, documento, video y repositorio público. Un PR abierto o un script presente no equivale a evidencia pasada.
5. Flujo de merges al cierre: `PRs individuales → feature/avance3-fase-b → develop → main`. Ningún merge directo a `develop` o `main`.

## Contrato de prioridad clínica — resuelto 09/10/2026

**Decisión**: la escala vigente de `vital_signs.priority` es **`NORMAL / URGENTE / EMERGENCIA`** (español).

**Fundamento verificado contra el repositorio**:
- Las migraciones Flyway activas (V1–V10) **no imponen CHECK** sobre `vital_signs.priority`. La columna es `VARCHAR(10)` sin restricción.
- Los seeds reales de `backend/src/main/resources/db/migration/V8__seed_medical_records.sql` insertan `NORMAL` y `URGENTE`.
- `MedicalRecordService` usa `NORMAL` como default.
- `frontend/src/pages/Expediente.tsx` ya opera con `NORMAL/URGENTE/EMERGENCIA`.
- El archivo `database/schema.sql` que listaba `LOW/MEDIUM/HIGH/CRITICAL` es un **export desactualizado**, no es migración activa.

**Resuelto en B1 (PR #41)**:
- `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java:126,135` → filtra `priority = 'EMERGENCIA'`.
- `backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDaoTest.java:62–67` → fixtures alineados a `NORMAL/EMERGENCIA`.
- `docs/avances/fase-b/B1_CONTRATO_DASHBOARD.md` → contrato de prioridad declarado explícitamente.

**Pendiente ajeno a B1 (sin bloquear a nadie, trabajo independiente de cada autor)**:
- **Zair Díaz** (B6/B7): alinear `frontend/src/pages/Triaje.tsx:25,36` a la escala `NORMAL/URGENTE/EMERGENCIA`. No bloquea B1 porque Expediente ya está correcto y es la pantalla principal de triaje.
- **Walter Flores** (B8): en el reporte de regresión, documentar `database/schema.sql` como export obsoleto y proponer su regeneración o eliminación. No bloquea B1.

## Infraestructura de permisos (resuelto 13/10/2026 vía PR #56)

**Incidente detectado por Bayron el 12/10**: su worker de recordatorios (B3a) fallaba en cada ciclo con `permission denied for table appointment_reminders`. La V11 creó la tabla pero no concedió permisos a `clinica_app` (el usuario de la app).

**Diagnóstico contra Neon**:
- `V9` baseline (26/09) · `V10` + `V11` aplicadas por `neondb_owner` el 12/10 a las 19:52 SV al arrancar el backend con el `.env` nuevo (owner-level).
- Las 14 tablas iniciales (`appointments`, `patients`, `doctors`, `prescriptions`, etc.) tienen GRANTs porque alguien corrió `database/00_create_database.sql` por fuera de Flyway. La línea 8 de ese archivo tiene un `GRANT` comentado.
- **Ninguna migración V1–V11 incluye GRANTs**. Era cuestión de tiempo que V12 (Merino) o cualquier migración futura (Flores B8) reprodujera el hueco.
- Verificado en `appointments.reservation_code`: 43/43 códigos distintos, 0 duplicados → el check de abort de V12 pasa sin problema cuando se mergee.

**Solución en PR #56** (ya con CI verde, pendiente de review de Bayron):
1. **Correctivo retroactivo**: `GRANT SELECT/INSERT/UPDATE/DELETE ON appointment_reminders TO clinica_app` + `GRANT USAGE,SELECT` sobre la secuencia asociada. Desbloquea el worker sin editar la V11 ya aplicada.
2. **Política permanente**: `ALTER DEFAULT PRIVILEGES FOR ROLE neondb_owner IN SCHEMA public GRANT ... TO clinica_app`. Todo objeto futuro creado por el owner auto-concede permisos. **Ninguna migración futura necesita acordarse de GRANTs**.
3. **Compatibilidad con pruebas**: la migración se envuelve en `DO $$ ... $$` que detecta la presencia de los roles y hace no-op en Testcontainers (superuser único).
4. **Script de verificación**: `scripts/validate-grants.sh` conecta como owner y lista tablas sin los 4 permisos para `clinica_app`. Se puede correr antes de cada merge:
   ```bash
   set -a; source .env; set +a
   bash scripts/validate-grants.sh
   ```

**Lección permanente**: las migraciones Flyway no deben requerir GRANTs explícitos a partir de V13. El default aplica automáticamente a objetos creados por `neondb_owner` en `public`.

## Historial de verificaciones

- **06/10/2026** — `feature/avance3-fase-b` publicada desde Fase A (`a1b6dc0`); B1 publicado en `b1-dashboard-backend` (`85b82b3`) y abierto como PR borrador #41. B2, B4 y B5 no integrados.
- **09/10/2026** — B5 mergeado (#45). PR #41 rebaseado sobre fase-b con B5; CI del PR verde. `DashboardJdbcDao` validado contra PostgreSQL real con Testcontainers. CI de Fase B activo (#46). Axios 1.20 + pnpm en fase-b (#42). PR #40 Dependabot cerrado como superseded. B2 (#43) en corrección por el autor. B4 pendiente con vencimiento 10/10. Detectada discrepancia transversal de prioridad clínica.
- **10/10/2026** — B1 mergeado a `feature/avance3-fase-b` (#41, por BROJAS).
- **11/10/2026** — Sprint de merges del coordinador:
  - **B2 mergeado** (#43) tras sincronizar la rama con fase-b; CI verde.
  - **B3a mergeado** (#49): 10 pruebas Testcontainers; `ReminderReader` local hasta que B4 entregara `ReminderPort`.
  - **B4 mergeado** (#48) con corrección del test `BaseJdbcDaoTest` (`verify` default `times(1)` no coincidía con doble invocación sobre el mismo mock) + **cierre de §D polimorfismo** encima de la rama: `DashboardJdbcDao implements MetricsPort`, mock de `MetricsPort` en `DashboardMetricsServiceTest`/`DashboardControllerTest`, `DashboardJdbcDaoTest` renombrado a `getMetrics`, `DashboardMetricsReader` eliminado. 60/60 tests verdes. Review doble: Héctor + Bayron.
  - **Smoke test consolidado**: `mvn test` en fase-b con los tres merges aplicados → **65 pruebas, 0 fallos, BUILD SUCCESS**.
  - **Dependabot limpio**: 10 alertas de axios dismissadas como `inaccurate` (axios@1.20.0 ya es latest y contiene los fixes; GHSA sin `patched_versions` explícito).
- **12/10/2026** — Bayron levanta el stack con el `.env` nuevo; Flyway aplica V10 y V11 automáticamente a Neon al bootear (usuario `neondb_owner`, 19:52 SV). Bayron detecta `permission denied for appointment_reminders` y reporta.
- **13/10/2026** — Jornada de corrección sistémica:
  - **PR #51** (Djmaanco/Zair) **cerrado por mal direccionado**: apuntaba a `main` con cambios de todo fase-b, no al revés. Pendiente que Zair abra PR correcto.
  - **PR #55** (copilot bot) cerrado: diagnóstico sin cambios, lo cubre Merino en #54.
  - **PR #54** (Merino B3b) con `request-changes` por Héctor: NPE del mock + GRANT secuencia (opcional tras V13).
  - **PR #56** (fix/v13-grants-and-default-policy) abierto: correctivo retroactivo + política permanente. CI verde, revisión solicitada a Bayron. **Elimina el patrón de origen del incidente**.
