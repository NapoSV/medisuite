# Seguimiento de integración — Avance 3 Fase B

Coordinación: Héctor López. Cierre interno: **19/10/2026**. Entrega externa informada por el equipo: **25/10/2026**. Anotar el enlace real y evidencia solo cuando existan; una casilla vacía no significa terminado.

| B | Responsable | Entregable principal | PR / enlace | Pruebas o evidencia | Revisor sugerido | Estado |
|---|---|---|---|---|---|---|
| B1 | Héctor López | Dashboard JDBC y alcance por rol/tenant | [PR #41](https://github.com/NapoSV/medisuite/pull/41) | 12 pruebas B1 pasan; `DashboardJdbcDao` validado contra PostgreSQL 16 con Testcontainers 09/10; QA manual de dos tenants pendiente | William, Vigil | Borrador rebaseado 09/10 sobre fase-b con B5 integrado; espera B2 y B4 |
| B2 | Alejandro Vigil | Pool `jdbcDataSource` y 15 parámetros | [PR #43](https://github.com/NapoSV/medisuite/pull/43) | Configuración, conexión y carga | Héctor | En corrección por el autor: duplicado de archivo (`backend/backend/...`), credenciales hardcodeadas, 15 parámetros a mover a YAML, `app.default-tenant-id` fuera de `jwt`, `_secondary` sin justificar, rebase sobre fase-b pendiente |
| B3a | Bayron Orellana | Recordatorios concurrentes y V11 | pendiente | Concurrencia, idempotencia y migración | Flores | Pendiente — vence 15/10; depende de B2 y B4 |
| B3b | Alejandro Merino | Códigos de reserva y V12 | pendiente | Unicidad bajo carrera y migración | Flores | Pendiente — vence 15/10; depende de B2 y B4 |
| B4 | Carlos Ventura | DAO abstracto genérico, puertos y UML | pendiente | Tests de contrato/DAO y diagrama | Héctor | Pendiente — **vence 10/10** (dependencia dura para B1, B3a, B3b) |
| B5 | Walter Vásquez | Excepciones JDBC seguras | [PR #45](https://github.com/NapoSV/medisuite/pull/45) | SQLState mapeado, respuestas HTTP sanitizadas | Héctor | ✅ Mergeado 09/10 17:59 |
| B6 | William Melgar | Dashboard UI | pendiente | Estados de carga/error, roles y contrato | Héctor | Pendiente — vence 17/10; contrato publicado en `B1_CONTRATO_DASHBOARD.md` esperando firma del autor |
| B7 | Zair Díaz | Expediente UI | pendiente | Navegación y prioridades de triaje | Flores | Pendiente — vence 14/10; coautor del contrato de prioridad clínica |
| B8 | Walter Flores | Regresión y seguridad multi-tenant | pendiente | Suite y reporte de hallazgos | Héctor | Pendiente — vence 16/10 |
| B9 | Erika Fuentes | Pruebas de recetas y README | pendiente | Pruebas entre tenants y guía ejecutable | Flores | Pendiente — vence 14/10; sin dependencias técnicas, puede arrancar ya |
| B10 | Nicole Sánchez | Cohesión, legibilidad y documento final | pendiente | Documento de 18 secciones, defensa y evidencias | Héctor | Pendiente — vence 17/10 |

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

## Bloqueador transversal detectado 09/10 — prioridad clínica

En el código conviven **tres valores distintos** de `priority` que deben unificarse antes del 14/10:

| Lugar | Valor usado | Archivo |
|---|---|---|
| Backend dashboard DAO | `CRITICAL` | `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java:126,135` |
| Frontend Expediente (triaje inline) | `EMERGENCIA` | `frontend/src/pages/Expediente.tsx:14,164` |
| Frontend Triaje (página dedicada) | `LOW` | `frontend/src/pages/Triaje.tsx:25,36` |

Zair (B7) y Flores (B8) deben confirmar el constraint efectivo de `vital_signs.priority` en el esquema y acordar un conjunto único de valores. Mientras no se resuelva: el conteo de `criticalAlerts` del dashboard quedará en cero si los triajes se guardan con `EMERGENCIA`, y el frontend podría insertar valores incompatibles con el constraint. **Decisión bloqueante** para cerrar B1, B6, B7 y B8.

## Historial de verificaciones

- **06/10/2026** — `feature/avance3-fase-b` publicada desde Fase A (`a1b6dc0`); B1 publicado en `b1-dashboard-backend` (`85b82b3`) y abierto como PR borrador #41. B2, B4 y B5 no integrados.
- **09/10/2026** — B5 mergeado (#45). PR #41 rebaseado sobre fase-b con B5; CI del PR verde (backend-tests, frontend-build). `DashboardJdbcDao` validado contra PostgreSQL real con Testcontainers. CI de Fase B activo (#46). Axios 1.20 + pnpm en fase-b (#42). PR #40 Dependabot cerrado como superseded. B2 (#43) en corrección por el autor con conflicto de base y archivo duplicado. B4 pendiente con vencimiento 10/10. Resto del equipo sin PR abierto. Detectada discrepancia transversal de prioridad clínica (ver sección anterior).
