# Respuestas del Ing. Guevara al Grupo 7 — Avance 2

**Fecha de consulta:** semana del 25/08/2026
**Fecha de respuesta:** 30/08/2026
**Registrado por:** Lopez Ruiz Hector Napoleon (Scrum Master)
**Documento base:** `docs/AVANCE2_ENTREGA.pdf`
**Entrega oficial:** 27/09/2026 13:00

Este documento consolida las tres decisiones del docente que definen el
alcance tecnico del Avance 2. Es evidencia para todo el equipo y sirve de
insumo para el documento final (`AVANCE2_MEDISUITE.pdf`).

---

## 1. Persistencia — PostgreSQL + capa paralela en archivos `.dat`

**Consulta:** ¿Debemos migrar toda la persistencia del Avance 1 (PostgreSQL)
a archivos `.dat` para cumplir el criterio de persistencia con archivos, o
podemos mantener PostgreSQL y agregar `.dat` solo en algun modulo?

**Decision del profesor:** mantener **PostgreSQL** como base principal
(la que ya se entrego en el Avance 1) y agregar una **capa paralela en
archivos `.dat`** para un modulo especifico. **NO migrar todo.**

**Modulo elegido por el equipo:** respaldo asincrono del expediente medico
(`MedicalRecord`) y del log de auditoria (`AuditLog`) a archivos `.dat`.

**Implicaciones tecnicas:**

- Se mantiene el schema Postgres actual y todos los repositorios JPA.
- Se agrega `DatFileDao<T extends Serializable>` (clase abstracta) que
  serializa entidades a `data/backup/*.dat`.
- Las entidades `MedicalRecord`, `Prescription` y `AuditLog` implementan
  `Serializable`.
- Cumple los criterios de evaluacion:
  - Persistencia con archivos `.dat` (1.20 pts).
  - Herencia + clase abstracta (`DatFileDao<T>` como base).

**Responsables:** Merino (M-02, M-03, M-04) y Ventura (VT-07 para
integracion con `MedicalRecord`).

---

## 2. Frontend — el profesor acepta minimo, el equipo entrega modulos pulidos

**Consulta:** ¿El frontend del Avance 2 debe cubrir todas las historias de
usuario o basta con lo minimo funcional?

**Decision del profesor:** acepta un **frontend minimo** siempre y cuando
demuestre integracion con el backend. **No es requisito pulir todos los
modulos.**

**Decision del equipo:** aunque el minimo es aceptado, entregamos los
**modulos importantes pulidos** para maximizar los puntos extra por
"sistema funcional completo" y para tener una defensa mas solida.

**Alcance de frontend acordado:**

1. Login (ya entregado en Avance 1, se pule).
2. Dashboard con 4 KPIs (consume `GET /api/dashboard/metrics`).
3. Pacientes (CRUD).
4. Doctores (CRUD).
5. Citas (calendario + creacion).
6. Expediente medico (consulta por paciente).
7. Recetas (creacion y listado).

**Responsables:** Diaz (D-01 a D-09), Flores (F-01 a F-07),
Melgar (MR-01 a MR-08), Fuentes/Erika (D-08, D-09 si commitea antes del 14/09).

---

## 3. Concurrencia — opciones B + C combinadas

**Consulta:** El PDF ofrece tres opciones de concurrencia (A: pool de hilos
para procesar solicitudes; B: backup asincrono con `ScheduledExecutorService`;
C: metricas del dashboard en paralelo con `CompletableFuture`). ¿Cual
implementamos?

**Decision del profesor:** implementar **opcion B + opcion C**.

**Opcion B — backup asincrono a `.dat`:**

- Usa `ScheduledExecutorService` con periodo configurable (por defecto
  cada 5 minutos).
- Serializa `MedicalRecord` y `AuditLog` a archivos `.dat` sin bloquear
  el request HTTP.
- Es la misma feature que cumple el criterio de persistencia con archivos
  (item 1 de este documento) — combina 2 criterios pesados en un solo
  desarrollo.

**Opcion C — metricas del dashboard en paralelo:**

- Endpoint `GET /api/dashboard/metrics` calcula 4 KPIs en paralelo usando
  `CompletableFuture.supplyAsync(...)` + `CompletableFuture.allOf(...)`.
- Cada KPI ejecuta su query en un hilo separado; el endpoint responde
  cuando todos completan.
- Reduce el tiempo de respuesta del dashboard en factor ~4x vs. calculo
  secuencial.

**Responsables:**

- Opcion B: Merino (M-05, M-06 con `ScheduledExecutorService`).
- Opcion C: Merino (M-06 endpoint) + Lopez (D-04 pantalla frontend).

---

## Trazabilidad en el plan de tareas

Las 3 decisiones estan reflejadas en:

- `docs/planner/tareas_avance2.csv` — 88 tareas del Planner.
- `docs/EJECUCION_AVANCE2_EQUIPO.md` — guia paso a paso por integrante.
- `~/.claude/plans/keen-squishing-thacker.md` — plan estrategico interno
  del Scrum Master.

## Historial de consultas futuras

Si surgen nuevas consultas al docente durante el Avance 2, se agregan aqui
como seccion 4, 5, etc., con el mismo formato: consulta + decision +
implicaciones + responsables.
