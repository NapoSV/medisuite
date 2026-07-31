# PLAN_FASES_2_3 — MediSuite · Plan de desarrollo de las etapas 2 y 3

> Versión: 1.0 · Fecha: 27/07/2026 · Owner: Héctor López (PM) + Alejandro Vigil (Scrum Master)
>
> **Este documento es el plan oficial y completo de lo que sigue después del Avance 1.**
> Complementa a [PLAN_IMPLEMENTACION.md](PLAN_IMPLEMENTACION.md) (roadmap por fases técnicas)
> con el detalle operativo de las dos etapas académicas restantes.

---

## 0. Contexto y decisiones que originan este plan

**Fechas confirmadas por el equipo (27/07/2026):**

| Hito académico | Fecha |
|---|---|
| Entrega Avance 1 | 10/08/2026 |
| **Etapa 2 — Entrega Avance 2** | **semana del 21 al 26 de septiembre de 2026** |
| **Etapa 3 — Entrega final y defensa** | **semana del 26 al 31 de octubre de 2026** |

**Aclaraciones del ingeniero (consulta respondida el 27/07/2026):**

1. **Portada con foto:** obligatoria y de **cada integrante** (le facilita reconocer el rostro de cada quien al revisar). Aplica a todos los documentos entregables.
2. **Gantt y planificación:** cubren **todo el proyecto hasta la entrega final**; tareas genéricas aceptadas para fases futuras. Ver [diagramas/gantt_proyecto_completo.md](diagramas/gantt_proyecto_completo.md).
3. **Estructura base (punto 12):** desde el Avance 1 ya deben existir las entidades en código y una versión inicial de la base de datos (no necesariamente la final).

**Decisión sobre Planner:** con las tareas ya cargadas se cierra la fase 1. **El resto de tareas de las etapas 2 y 3 (sección 5 de este documento) se cargan al Planner al terminar la fase 1** — no antes, para no saturar el tablero.

---

## 1. Calendario general

```
11/08 ──────────── 13/09   Desarrollo Etapa 2 (citas + seguridad)
14/09 ──────────── 20/09   QA final + documento Avance 2
21/09 ──────────── 26/09   ★ SEMANA DE ENTREGA AVANCE 2
21/09 ──────────── 04/10   Triaje y expediente clínico (F3)
05/10 ──────────── 15/10   Recetas y reportes (F4)
12/10 ──────────── 20/10   Inventario y compras (F5)
19/10 ──────────── 25/10   Deploy + demo + documento final (F6)
26/10 ──────────── 31/10   ★ SEMANA DE ENTREGA FINAL Y DEFENSA
```

Las fases F3–F5 se traslapan a propósito: son equipos de trabajo distintos (backend/frontend/BD) avanzando en paralelo, igual que en la etapa 1.

---

## 2. Etapa 2 — Avance 2 (11/08 → semana del 21–26/09)

**Objetivo académico:** módulo de citas médicas completo y aplicación blindada en seguridad, con documento formal actualizado (2 objetivos específicos acumulados).

### 2.1 Sprints

| Sprint | Fechas | Meta |
|---|---|---|
| S2.1 | 11/08 – 23/08 | Backend de citas: entidades `Appointment`/`Specialty`, endpoints `/api/appointments/**`, slots por médico, índice anti doble-reserva |
| S2.2 | 24/08 – 06/09 | Frontend calendario (día/semana/mes), modal "Nueva cita", "Mis citas" (paciente), "Mi agenda" (médico) + **arranque del blindaje de seguridad S1–S4** |
| S2.3 | 07/09 – 20/09 | Seguridad S5–S7, rate limiting, refresh token rotativo, QA integral, documento Avance 2 y PDF |

### 2.2 Distribución por frente

| Frente | Responsables | Entregables |
|---|---|---|
| Backend citas | ORELLANA + VENTURA | Endpoints de citas/slots/agenda, anti doble-reserva (409), cancelación con regla de 24 h (422) |
| Frontend citas | DIAZ + MELGAR | Calendario, modal de cita con búsqueda de paciente y slots dinámicos, vistas por rol |
| Seguridad (S1–S7) | ORELLANA + VIGIL + LOPEZ | Blindaje completo según [PLAN_IMPLEMENTACION.md §4](PLAN_IMPLEMENTACION.md) — BCrypt, JPA parametrizado, JWT endurecido, RBAC + anti-IDOR multi-tenant, headers, `audit_log`, mapa OWASP |
| Base de datos | MERINO + VENTURA | Migraciones de `appointments`, `refresh_tokens`, `audit_log`; índices |
| QA | FUENTES + VASQUEZ | Casos E2E de citas, prueba de doble-reserva concurrente, test cruzado de tenants, verificación de headers |
| Documento | LOPEZ + SANCHEZ + FLORES | Actualización de los 14 puntos, 2.º objetivo específico, Gantt actualizado, conclusiones y bibliografía del avance |

### 2.3 Documento del Avance 2 — cambios respecto al Avance 1

- Portada: **foto de cada integrante** (obligatorio), nombre, CIF, participó SI/NO.
- Objetivos específicos: **2 acumulados** (el del Avance 1 + uno nuevo sobre el módulo de citas/seguridad).
- HU: se añaden HU-003, HU-005, HU-009 con criterios de aceptación.
- Planificación y Gantt: versión del proyecto completo, marcando lo ya ejecutado.
- Entradas/salidas y entidades: se suman las del módulo de citas.
- Punto 12: estructura de código actualizada con los paquetes/clases nuevos.
- Conclusiones y bibliografía: propias de este avance.

### 2.4 Criterios de cierre de la Etapa 2

- [ ] Todos los criterios go-live F2 → F3 de [PLAN_IMPLEMENTACION.md](PLAN_IMPLEMENTACION.md) (incluidos los 5 checks de seguridad S1–S6).
- [ ] Documento Avance 2 en PDF entregado dentro de la semana del 21 al 26/09.
- [ ] Tareas de la Etapa 3 cargadas al Planner (ver sección 5).

---

## 3. Etapa 3 — Entrega final y defensa (21/09 → semana del 26–31/10)

**Objetivo académico:** sistema completo (triaje, expediente, recetas, reportes, inventario), desplegado y demostrable, con documento final (3 objetivos específicos) y defensa preparada.

### 3.1 Bloques de trabajo

| Bloque | Fechas | Responsables | Entregables |
|---|---|---|---|
| F3 Triaje y expediente | 21/09 – 04/10 | ORELLANA + DIAZ + MERINO | `/api/vital-signs`, `/api/patients/{id}/medical-record`, pantalla de triaje, expediente con tabs, alerta "Crítica" en agenda |
| F4 Recetas y reportes | 05/10 – 15/10 | VENTURA + MELGAR | `/api/prescriptions/**`, `/api/reports/**`, PDF de receta, dashboard KPIs (Recharts) |
| F5 Inventario y compras | 12/10 – 20/10 | ORELLANA + DIAZ + MERINO | `/api/inventory/**`, `/api/purchase-orders/**`, estados de orden, stock automático |
| F6 Deploy y demo | 19/10 – 25/10 | VIGIL + LOPEZ + todos | Backend en Railway/Render, frontend en Vercel, BD Neon; smoke test completo sobre HTTPS |
| QA continuo | 21/09 – 24/10 | FUENTES + VASQUEZ | Regresión por bloque + pruebas de usabilidad (triaje < 30 s) |
| Documento final | 12/10 – 24/10 | LOPEZ + SANCHEZ + FLORES | 14 puntos completos, 3 objetivos específicos, Gantt final "as-built", conclusiones finales |

### 3.2 Preparación de la defensa (semana del 26 al 31/10)

- **Guion de demo por rol** (5 min c/u): login → recepción agenda cita → enfermera triaje → médico atiende y receta → admin ve dashboard → almacén recibe orden.
- **Ensayo general** con toma de tiempo: mínimo 2 corridas completas antes del día de la defensa.
- **Plan B de demo:** video de respaldo grabado por si falla la conexión o el free-tier de Neon (mitigación ya prevista: cron ping cada 4 h desde GitHub Actions).
- **Reparto de preguntas probables:** cada integrante domina su módulo + seguridad básica (todos deben poder explicar BCrypt, JWT y multi-tenant a nivel conceptual).

### 3.3 Criterios de cierre de la Etapa 3

- [ ] Flujo completo de negocio demostrable end-to-end en el ambiente desplegado.
- [ ] Documento final en PDF entregado en la semana del 26 al 31/10.
- [ ] Defensa presentada con participación de todo el equipo.

---

## 4. Seguridad y temas transversales

- El **blindaje de seguridad** (S1–S7, mapa OWASP) es parte de la Etapa 2 y se audita en sus criterios de cierre — el detalle vive en [PLAN_IMPLEMENTACION.md §4](PLAN_IMPLEMENTACION.md).
- **Regla permanente:** cero credenciales en el repo o en documentos versionados; host y accesos de Neon solo en el `.env` de cada quien y por Teams DM.
- **i18n, design tokens (MedCore Clay) y CI** siguen siendo pre-requisitos transversales (ver [PLAN_IMPLEMENTACION.md §9](PLAN_IMPLEMENTACION.md)).
- Datos siempre ficticios: nada de pacientes reales en la BD compartida.

---

## 5. Tareas para cargar al Planner al cerrar la fase 1

> **No cargar todavía.** Cuando se entregue el Avance 1 (10/08), estas tareas se convierten a CSV
> (mismo formato de `docs/planner/tareas_avance1.csv`) y se suben al Planner.

| # | Tarea | Bucket | Responsable | Inicio | Fin |
|---|---|---|---|---|---|
| E2-01 | Entidades y migraciones de citas (`Appointment`, `Specialty`) | Backlog | ORELLANA | 11/08 | 16/08 |
| E2-02 | Endpoints `/api/appointments/**` + slots por médico | Backlog | ORELLANA + VENTURA | 17/08 | 23/08 |
| E2-03 | Índice anti doble-reserva + prueba concurrente | Backlog | VENTURA + MERINO | 20/08 | 23/08 |
| E2-04 | Frontend: calendario día/semana/mes | Backlog | DIAZ | 24/08 | 04/09 |
| E2-05 | Frontend: modal "Nueva cita" + "Mis citas" + "Mi agenda" | Backlog | MELGAR + DIAZ | 28/08 | 06/09 |
| E2-06 | Seguridad S1–S2: credenciales y anti-inyección (auditoría + fixes) | Backlog | ORELLANA | 24/08 | 31/08 |
| E2-07 | Seguridad S3: refresh token rotativo + rate limiting | Backlog | ORELLANA + VENTURA | 01/09 | 08/09 |
| E2-08 | Seguridad S4: anti-IDOR multi-tenant + tests cruzados | Backlog | ORELLANA + FUENTES | 05/09 | 12/09 |
| E2-09 | Seguridad S5–S6: headers, CORS, `audit_log`, Dependabot | Backlog | VIGIL + VENTURA | 07/09 | 14/09 |
| E2-10 | QA integral de citas + seguridad | Backlog | FUENTES + VASQUEZ | 07/09 | 18/09 |
| E2-11 | Documento Avance 2 (14 puntos, 2.º objetivo, fotos, Gantt) | Documento | LOPEZ + SANCHEZ + FLORES | 07/09 | 19/09 |
| E2-12 | Entrega Avance 2 | Documento | LOPEZ | 21/09 | 26/09 |
| E3-01 | Triaje: `/api/vital-signs` + pantalla de signos vitales | Backlog | ORELLANA + DIAZ | 21/09 | 30/09 |
| E3-02 | Expediente clínico con tabs + alerta crítica | Backlog | DIAZ + MERINO | 28/09 | 04/10 |
| E3-03 | Recetas: endpoints + PDF | Backlog | VENTURA | 05/10 | 12/10 |
| E3-04 | Reportes: dashboard KPIs + filtros de fecha | Backlog | MELGAR | 08/10 | 15/10 |
| E3-05 | Inventario y órdenes de compra (flujo completo) | Backlog | ORELLANA + DIAZ + MERINO | 12/10 | 20/10 |
| E3-06 | Deploy backend + frontend + smoke test HTTPS | Backlog | VIGIL + LOPEZ | 19/10 | 23/10 |
| E3-07 | QA de regresión final | QA | FUENTES + VASQUEZ | 12/10 | 24/10 |
| E3-08 | Documento final (3 objetivos, Gantt as-built, conclusiones) | Documento | LOPEZ + SANCHEZ + FLORES | 12/10 | 24/10 |
| E3-09 | Guion de demo + 2 ensayos + video plan B | Documento | Todos | 19/10 | 25/10 |
| E3-10 | Entrega final y defensa | Documento | Todos | 26/10 | 31/10 |

---

## 6. Riesgos específicos de estas etapas

| Riesgo | Impacto | Mitigación |
|---|---|---|
| El calendario (HU-003) se atrasa y arrastra el Avance 2 | Alto | DIAZ arranca el calendario desde S2.1 en paralelo al backend; versión simplificada (solo vista semanal) como plan B |
| El blindaje de seguridad se deja para el final del sprint | Alto | S1–S4 arrancan en S2.2, no en S2.3; checklist de seguridad en cada PR desde el 24/08 |
| Traslape F4/F5 satura a ORELLANA y DIAZ | Medio | MELGAR y VENTURA absorben F4 completo; PM revisa carga en la weekly |
| Fotos de integrantes faltantes en la portada | Bajo pero bloquea nota | Recolectar las 11 fotos en Teams la semana del 04/08, de una vez para los 2 documentos restantes |
| Neon free-tier dormido en plena defensa | Alto | Cron ping cada 4 h + video de respaldo (E3-09) |

---

## 7. Referencias

- [PLAN_IMPLEMENTACION.md](PLAN_IMPLEMENTACION.md) — fases técnicas, seguridad S1–S7, criterios go-live.
- [PLAN_EJECUCION_AVANCE1.md](PLAN_EJECUCION_AVANCE1.md) — etapa 1 (en curso).
- [diagramas/gantt_proyecto_completo.md](diagramas/gantt_proyecto_completo.md) — Gantt del proyecto completo (para el documento).
- [AVANCE1_DOCUMENTO_ENTREGAR.md](AVANCE1_DOCUMENTO_ENTREGAR.md) — los 14 puntos + aclaraciones del ingeniero.
