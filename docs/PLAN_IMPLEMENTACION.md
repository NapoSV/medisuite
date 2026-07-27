# PLAN_IMPLEMENTACION — MediSuite

> Roadmap ejecutable de todo el proyecto, fases y dependencias.
> Versión: 1.0 · Fecha: 26/07/2026 · Owner: Alejandro Vigil (Scrum Master) + Héctor López (PM)

Este documento traduce el alcance funcional del [PRD](PRD.md) y la arquitectura del [TRD](TRD.md) en una **secuencia ejecutable de fases** con criterios go-live claros. Para el detalle día-por-día del Avance 1, ver [PLAN_EJECUCION_AVANCE1.md](PLAN_EJECUCION_AVANCE1.md). Para el plan completo de las etapas 2 y 3, ver [PLAN_FASES_2_3.md](PLAN_FASES_2_3.md).

> **📌 Actualización 27/07/2026 — fechas confirmadas y aclaraciones del ingeniero:**
> - Hitos académicos definitivos: **Avance 1 → 10/08** · **Avance 2 → semana del 21 al 26/09** · **Entrega final y defensa → semana del 26 al 31/10**.
> - El Gantt y la planificación del documento cubren **todo el proyecto** (tareas genéricas para fases futuras): [diagramas/gantt_proyecto_completo.md](diagramas/gantt_proyecto_completo.md).
> - La portada lleva **foto de cada integrante** (obligatorio) y el Avance 1 ya exige BD inicial + entidades en código.
> - **Planner:** con las tareas actuales se cierra la fase 1; el resto de tareas de las fases 2 y 3 se cargan al Planner **al terminar la fase 1** (listas en [PLAN_FASES_2_3.md](PLAN_FASES_2_3.md)).

---

## 1. Fases del proyecto → Avance académico

| Fase | Nombre | Módulos | Duración | Avance académico |
|---|---|---|---|---|
| **F0** | Setup y fundación | Repos, ambientes, BD, CI/CD | 08/07 – 21/07 | Avance 1 (pre-work) |
| **F1** | Autenticación & Roles | HU-007, HU-008, gestión de pacientes básica (HU-001), documento formal | 15/07 – 07/08 | **Avance 1 (10/08)** |
| **F2** | Citas médicas + blindaje de seguridad | HU-003, HU-005, HU-009, calendario, anti doble-reserva, seguridad S1-S7 | 11/08 – 20/09 | **Avance 2 (semana 21–26/09)** |
| **F3** | Triaje y expediente | HU-004, expediente clínico completo | 21/09 – 04/10 | Entrega final (parte) |
| **F4** | Recetas y reportes | HU-002, HU-006, HU-010 | 05/10 – 15/10 | Entrega final (parte) |
| **F5** | Inventario y compras | HU-011 (extensión SaaS) | 12/10 – 20/10 | Entrega final (parte) |
| **F6** | Deploy, demo y presentación | Ajustes, documentación final, ensayo | 19/10 – 25/10 | **Entrega final y defensa (semana 26–31/10)** |

---

## 2. Fase 0 — Setup y fundación (pre-Avance 1)

**Objetivo:** todo el equipo con entorno listo, BD accesible, repo clonado y build inicial funcionando.

### Criterios de go-live (F0 → F1)

- [ ] Todos con Java 21 LTS, Node 20, Docker, Git, DBeaver instalados. Ver [SETUP_ENTORNO.md](SETUP_ENTORNO.md).
- [ ] Repo `medisuite` clonado. Ramas `main` y `develop` configuradas.
- [ ] Cada integrante puede conectarse a Neon `clinica_dev` desde DBeaver. Ver [MANUAL_AVANCE1_EQUIPO.md](MANUAL_AVANCE1_EQUIPO.md).
- [ ] `schema.sql` ejecutado en Neon; las 17 tablas visibles en DBeaver.
- [ ] `seed.sql` ejecutado; 2 tenants demo + datos ficticios cargados.
- [ ] Todos leyeron y firmaron [ESTANDARES_CODIGO.md](ESTANDARES_CODIGO.md) y [INSTRUCTIVO_GIT.md](INSTRUCTIVO_GIT.md).
- [ ] Canal de Teams del equipo activo con los 10 canales definidos en el kickoff.
- [ ] Plan de Planner creado con 5 buckets: **Backlog**, **En progreso**, **En revisión**, **QA**, **Hecho**.

---

## 3. Fase 1 — Autenticación & Roles (Avance 1)

**Objetivo académico:** entregar el módulo de autenticación como código base + documento formal (14 puntos).

**Duración:** 15/07 – 07/08 (24 días naturales) + margen 08–09/08.

**Detalle día por día → [PLAN_EJECUCION_AVANCE1.md](PLAN_EJECUCION_AVANCE1.md).**

### Dependencias técnicas dentro de la fase

```
schema.sql en Neon
    ↓
Entidades Java (User, Patient, Doctor, ...)
    ↓
Repositories (Spring Data JPA)
    ↓
Services (AuthService, UserService, PatientService)
    ↓
Controllers (AuthController, UserController, PatientController)
    ↓
Frontend LoginPage.tsx consume /api/auth/login
    ↓
Tests QA (login OK, login fail, bloqueo tras 5 intentos)
```

### Criterios de go-live (F1 → F2)

- [ ] Los 14 puntos del documento oficial completados en el PDF entregable.
- [ ] Endpoint `POST /api/auth/login` responde 200 con JWT válido para usuarios seed.
- [ ] Frontend con login funcional que redirige a dashboard según rol.
- [ ] Alta de paciente (HU-001) funcional en frontend + backend.
- [ ] Bloqueo tras 5 intentos fallidos verificado por QA.
- [ ] CI GitHub Actions ejecuta `mvnw test` sin errores en el PR a `develop`.
- [ ] Documento del Avance 1 subido y compilado sin errores el 10/08.

---

## 4. Fase 2 — Gestión de citas (Avance 2)

**Objetivo académico:** módulo de agendamiento completo con anti doble-reserva y calendario.

**Duración:** 11/08 – 20/09 (desarrollo hasta 13/09; documento y QA final 14–20/09; entrega en la semana del 21 al 26/09).

### Módulos y HU

- HU-003 (Paciente/Recepción agenda cita)
- HU-005 (Admin gestiona horarios)
- HU-009 (Médico ve su agenda)

### Backend entregables

- Endpoints `/api/appointments/**`, `/api/doctors/{id}/slots`, `/api/doctors/{id}/agenda`.
- Entidades `Appointment`, `Specialty` con relaciones.
- Índice único parcial anti doble-reserva verificado.
- Notificaciones simuladas (log en consola / tabla `notifications` opcional).

### Frontend entregables

- Página `Calendar` con vista día/semana/mes.
- Modal "Nueva cita" con búsqueda de paciente, selector de médico y slots dinámicos.
- Página "Mis citas" (paciente).
- Página "Mi agenda" (médico).

### Seguridad — Blindaje de la aplicación (obligatorio en F2)

> **Meta:** al cerrar la Fase 2 la app queda "blindada": ninguna credencial en texto plano, ninguna query concatenada, ningún endpoint sin autorización, ningún dato de un tenant visible desde otro. Este bloque se audita como parte de los criterios de go-live. Las políticas formales que respaldan estos controles viven en `docs/politicas/` (POL-01 a POL-08).

#### S1. Contraseñas y credenciales

- **Hashing:** todas las contraseñas se almacenan con **BCrypt cost 12** (ya definido en `SecurityConfig` de F1). Prohibido MD5/SHA-1/SHA-256 sin salt, y prohibido cualquier almacenamiento reversible.
- **Verificación:** siempre `passwordEncoder.matches()`; nunca comparar hashes ni strings manualmente.
- **Política de contraseñas (backend valida):** mínimo 10 caracteres, al menos 1 mayúscula, 1 minúscula, 1 dígito. Passwords temporales expiran al primer login (flag `must_change_password`).
- **Nunca en logs:** el logger no imprime `password`, `passwordHash` ni el JWT completo. Revisar `logging.level` antes de cada release.
- **Secretos de aplicación:** `JWT_SECRET`, `DB_PASSWORD`, etc. viven SOLO en `.env` (gitignored) y en variables de entorno del despliegue. Prohibido en código, en `application.yml` con valores literales, en docs versionados o en capturas de pantalla (lección aprendida en F1).

#### S2. Inyección SQL y validación de entradas

- **Acceso a datos exclusivamente vía Spring Data JPA / consultas parametrizadas.** Prohibida la concatenación de strings para armar SQL o JPQL. Si se necesita query nativa: `@Query(nativeQuery = true)` con parámetros nombrados (`:param`), nunca interpolación.
- **Bean Validation en todos los DTOs de entrada:** `@NotBlank`, `@Email`, `@Size`, `@Pattern`, `@Min/@Max`. El controller siempre usa `@Valid`.
- **Límites de tamaño:** requests JSON con límite (`server.max-http-request-header-size`, `spring.servlet.multipart.max-file-size` cuando aplique) para prevenir abuso.
- **Salida de errores segura:** `GlobalExceptionHandler` nunca devuelve stack traces, SQL ni nombres de clases internas al cliente. Mensajes genéricos al usuario, detalle solo en logs del servidor.

#### S3. Autenticación y JWT endurecido

- Secret de **mínimo 256 bits (32+ caracteres aleatorios)**, generado con generador criptográfico, distinto por ambiente, rotado si se sospecha exposición.
- Expiración corta del access token (**15 min**) + **refresh token** (7 días) con rotación: cada refresh emite un token nuevo e invalida el anterior (tabla `refresh_tokens` con hash del token, no el token en claro).
- Claims mínimos: `sub`, `role`, `tenant_id`, `iat`, `exp`. **Nunca** datos personales ni de salud dentro del JWT.
- Bloqueo por fuerza bruta ya implementado en F1 (5 intentos → 15 min); en F2 se agrega **rate limiting** por IP en `/api/auth/**` (Bucket4j o filtro propio: máx. 10 requests/min).
- Logout: el frontend borra el token y el backend invalida el refresh token asociado.

#### S4. Autorización y aislamiento multi-tenant

- **RBAC en cada endpoint:** `@PreAuthorize("hasRole('...')")` o reglas equivalentes en `SecurityFilterChain`. Ningún endpoint nuevo se mergea sin regla de autorización explícita (checklist de PR).
- **Anti-IDOR:** toda consulta filtra por el `tenant_id` del JWT, nunca por el que venga en la URL o el body. Un usuario del tenant A que pida `/api/appointments/{id}` de un registro del tenant B recibe **404** (no 403, para no revelar existencia).
- Los repositorios exponen métodos que incluyen `tenantId` en la firma (`findByIdAndTenantId`) para que el aislamiento sea estructural, no opcional.

#### S5. Protección del frontend y del canal

- **XSS:** React escapa por defecto — prohibido `dangerouslySetInnerHTML`. Ningún dato del servidor se inserta como HTML crudo.
- **CORS:** origins explícitos por ambiente (dev: `http://localhost:5173`; producción: solo el dominio real). Nunca `*` con credenciales.
- **CSRF:** la API es stateless con JWT en header `Authorization` (no cookies de sesión), lo que neutraliza CSRF clásico; se mantiene `csrf.disable()` documentado con esta justificación.
- **Headers de seguridad** (Spring Security): `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: no-referrer`, y `Strict-Transport-Security` cuando haya dominio con TLS.
- **TLS en todo:** conexión a Neon siempre `sslmode=require` (ya activo); el deploy de F6 se publica solo sobre HTTPS.
- **Token en el navegador:** el access token se mantiene en memoria/localStorage con expiración corta; el refresh token nunca se expone a JavaScript si el deploy permite cookie `HttpOnly` (evaluar en F6).

#### S6. Auditoría, datos de salud y dependencias

- **Tabla `audit_log`:** registra login exitoso/fallido, creación/cambio/borrado de usuarios, y acceso a expedientes clínicos (quién, qué, cuándo, desde qué IP). Sin datos clínicos dentro del log.
- **Datos de salud = dato sensible:** solo datos ficticios en dev (regla ya vigente); minimización de campos; el rol Recepcionista no ve diagnósticos ni signos vitales (enforcement en backend, no solo en UI).
- **Dependencias:** activar **Dependabot** en GitHub + revisión mensual de CVEs de Spring Boot, jjwt y librerías npm. Actualizar parches de seguridad dentro de la semana de publicados.
- **Backups:** Neon mantiene point-in-time recovery; antes de cada migración destructiva se documenta el punto de restauración en el canal de Teams.

#### S7. Mapa OWASP Top 10 (2021) → control MediSuite

| OWASP | Control en MediSuite |
|---|---|
| A01 Broken Access Control | RBAC + anti-IDOR por `tenant_id` (S4) |
| A02 Cryptographic Failures | BCrypt 12, TLS, secret JWT 256 bits (S1, S3, S5) |
| A03 Injection | JPA parametrizado + Bean Validation (S2) |
| A04 Insecure Design | Revisión de seguridad en cada PR + este plan |
| A05 Security Misconfiguration | Headers, CORS explícito, errores genéricos (S2, S5) |
| A06 Vulnerable Components | Dependabot + revisión mensual (S6) |
| A07 Identification & Auth Failures | Bloqueo 5 intentos, rate limit, refresh rotativo (S3) |
| A08 Software & Data Integrity | PRs revisados, rama protegida, sin dependencias fuera de registries oficiales |
| A09 Logging & Monitoring Failures | `audit_log` + logs sin secretos (S1, S6) |
| A10 SSRF | El backend no hace requests a URLs provistas por el usuario (regla de diseño) |

### Criterios de go-live (F2 → F3)

- [ ] Test E2E: recepción crea cita → paciente la ve en "Mis citas".
- [ ] Doble-reserva probada (2 usuarios): el segundo recibe 409 Conflict.
- [ ] Cancelación con < 24 h retorna 422 con mensaje claro.
- [ ] **Seguridad S1-S2:** cero passwords/queries concatenadas — verificado con grep en el repo (`SELECT.*\+`, `password` en logs) y revisión de PRs.
- [ ] **Seguridad S3:** rate limiting activo en `/api/auth/**` y refresh token rotativo funcionando.
- [ ] **Seguridad S4:** test cruzado de tenants: usuario del tenant demo NO puede leer citas del tenant 2 (responde 404).
- [ ] **Seguridad S5:** headers de seguridad presentes (verificados con `curl -I`).
- [ ] **Seguridad S6:** tabla `audit_log` registrando logins y accesos a expediente.
- [ ] Documento del Avance 2 entregado en la **semana del 21 al 26/09** (con 2 objetivos específicos acumulados y portada con foto de cada integrante).

---

## 5. Fase 3 — Triaje y expediente clínico

**Objetivo:** módulo de triaje operativo + expediente clínico consultable.

**Duración:** 21/09 – 04/10.

### Módulos y HU

- HU-004 (Enfermera hace triaje).
- Expediente clínico buscable por paciente (extensión de HU-001).

### Entregables

- Endpoints `/api/vital-signs`, `/api/patients/{id}/medical-record`, `/api/patients/{id}/vital-signs`.
- Pantalla de triaje (formulario de signos vitales).
- Vista de expediente con tabs: Resumen, Consultas, Signos vitales, Recetas.
- Alerta visual en agenda del médico para prioridad "Crítica".

### Criterios de go-live (F3 → F4)

- [ ] Enfermera registra triaje en < 30 s en pruebas de usabilidad.
- [ ] Médico ve triaje al abrir expediente del paciente.
- [ ] Historial de signos vitales ordenado del más reciente al más antiguo.

---

## 6. Fase 4 — Recetas y reportes

**Duración:** 05/10 – 15/10.

### Módulos y HU

- HU-002 (Médico emite receta).
- HU-006 (Recepcionista imprime orden de atención).
- HU-010 (Admin ve dashboard con KPIs).

### Entregables

- Endpoints `/api/prescriptions/**`, `/api/reports/**`.
- Generación de PDF (biblioteca: `openhtmltopdf` o similar).
- Dashboard con KPI cards y gráficos (Recharts).
- Filtros por rango de fechas en reportes.

### Criterios de go-live (F4 → F5)

- [ ] Médico emite receta y descarga PDF sin salir del expediente.
- [ ] Admin ve dashboard con datos reales de la BD demo.
- [ ] Contenido de recetas y reportes incorporado al documento final (3 objetivos específicos acumulados).

---

## 7. Fase 5 — Inventario y compras (extensión SaaS)

**Duración:** 12/10 – 20/10.

### Módulos y HU

- HU-011 (Jefe de almacén: productos, órdenes de compra).
- Alertas de stock crítico.
- Registro de activos físicos.

### Entregables

- Endpoints `/api/inventory/**`, `/api/purchase-orders/**`.
- Pantallas de inventario y órdenes con estados PENDING/PARTIALLY_RECEIVED/COMPLETE.
- Actualización automática de stock al recibir orden.

### Criterios de go-live (F5 → F6)

- [ ] Flujo completo: crear producto → crear orden → recibir parcial → recibir total.
- [ ] Stock actualiza correctamente en cada recepción.

---

## 8. Fase 6 — Deploy, demo y presentación

**Duración:** 19/10 – 25/10 (defensa en la semana del 26 al 31/10).

### Actividades

- Deploy a entorno estable (Neon + backend en Railway/Render + frontend en Vercel — decisión final del arquitecto).
- Consolidación del documento final (14 puntos completos, 3 objetivos específicos, portada con fotos).
- Ensayo de demo con guion por rol (5 min c/u).
- **Entrega final y defensa en la semana del 26 al 31/10/2026.**

---

## 9. Dependencias transversales

| Dependencia | Debe estar lista antes de |
|---|---|
| BD con `schema.sql` aplicado | Cualquier código backend |
| Multi-tenancy (filtro Hibernate) | Cualquier endpoint que use datos |
| JWT emitido y validado | Cualquier endpoint autenticado |
| i18n backend + frontend | Cualquier UI con textos user-facing (Avance 2) |
| CI en GitHub Actions | Cualquier PR mergeable a `develop` |
| Design tokens de MedCore Clay aplicados | Cualquier página que salga a demo |

---

## 10. Métricas de seguimiento (para el PM)

Reunión de Scrum diaria (daily 15 min) y semanal (review + retro 45 min). Métricas a mirar:

| Métrica | Frecuencia | Umbral verde |
|---|---|---|
| PRs abiertos vs. merged | Semanal | Ratio ≥ 0.7 (no acumular) |
| Cobertura de tests backend | Semanal | ≥ 60% en clases críticas |
| Bugs abiertos en QA | Semanal | ≤ 3 abiertos por sprint |
| Cumplimiento de fechas de fase | Al cierre de cada fase | 100% |
| Riesgo BD Neon (queries lentas) | Semanal | p95 < 500 ms |

---

## 11. Riesgos identificados y mitigaciones

| Riesgo | Impacto | Mitigación |
|---|---|---|
| Neon free-tier suspende la BD por inactividad | Alto (demo falla) | Cron ping cada 4 h desde GitHub Action; contactar Neon para bump temporal antes de la demo. |
| Miembro cae enfermo o abandona | Medio | Cross-training: cada tarea tiene un backup asignado (documentar en Planner). |
| Estimación optimista de HU-003 (calendario) | Alto (retrasa Avance 2) | Bayron trabaja este módulo desde Sprint 1 de F2; feature flag para versión simplificada si falta tiempo. |
| Cambio de decisión React → Angular | Alto | Ya decidido: React. Cualquier reapertura requiere aprobación de PM + Architect. |
| Merge conflicts frecuentes en `develop` | Medio | Rebase diario obligatorio; PRs pequeños (< 500 líneas). |

---

## 12. Referencias

- [PRD.md](PRD.md), [TRD.md](TRD.md), [DISENO_UI_UX.md](DISENO_UI_UX.md), [APPFLOW.md](APPFLOW.md), [ESQUEMA_BACKEND.md](ESQUEMA_BACKEND.md).
- [PLAN_EJECUCION_AVANCE1.md](PLAN_EJECUCION_AVANCE1.md) — detalle diario de la fase actual.
- [PLAN_FASES_2_3.md](PLAN_FASES_2_3.md) — plan completo de las etapas 2 y 3 (fechas, sprints, tareas para Planner).
- [SETUP_ENTORNO.md](SETUP_ENTORNO.md), [INSTRUCTIVO_GIT.md](INSTRUCTIVO_GIT.md), [MANUAL_AVANCE1_EQUIPO.md](MANUAL_AVANCE1_EQUIPO.md).
- [PLAN_DE_TRABAJO.md](PLAN_DE_TRABAJO.md) — detalle histórico de sprints y Scrum del equipo.
