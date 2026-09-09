# Estado del equipo — Avance 2

**Corte:** 04/09/2026 (fin de Semana 1)
**Próxima revisión:** domingo 07/09/2026
**Scrum Master:** Héctor López

## Progreso del sprint

- **Tareas totales:** 88
- **Completadas:** 9 (10%)
- **Días al code freeze (dom 21/09):** 17
- **Entrega oficial:** dom 27/09/2026 13:00

## Avance por integrante

| # | Integrante | Rol | Asignadas | Hechas | % | Rama | Estado |
|---|---|---|---:|---:|---:|---|---|
| 1 | López (SM) | Arquitecto | 13 | 7 | 54% | `develop` (directo) | Adelantado — S1 code + U-01 (S2) + D-04 (S3) |
| 2 | Vigil | Backend/Seguridad | 8 | 1 | 13% | `feature/avance2-vigil` | VG-08 hecho, falta abrir PR |
| 3 | Sánchez | QA/Tests | 7 | 1 | 14% | `feature/S-01-...` | S-01 hecho, falta abrir PR |
| 4 | Merino | Fullstack/.dat | 9 | 0 | 0% | `feature/avance2-merino` | POC de .dat — M-06 sin arrancar |
| 5 | Vásquez | PO/Documento | 8 | 0 | 0% | `feature/avance2-vasquez` | Solo commit inicial |
| 6 | Flores | QA/BD | 7 | 0 | 0% | `feature/avance2-floreshernandez` | Sin commits |
| 7 | Orellana | Backend/Citas | 7 | 0 | 0% | ❌ sin rama | Sin arrancar |
| 8 | Díaz | Frontend | 7 | 0 | 0% | ❌ sin rama | Sin arrancar |
| 9 | Melgar | Frontend | 7 | 0 | 0% | ❌ sin rama | Sin arrancar |
| 10 | Ventura | Backend/Herencia | 7 | 0 | 0% | ❌ sin rama | Sin arrancar — VT-01 bloquea a otros |
| 11 | Fuentes (Erika) | QA contingente | 8 | 0 | 0% | ❌ sin rama | Fecha límite 14/09 |

## Tareas completadas hoy (detalle)

### Héctor
- **H-01** `.github/workflows/commit-trailers-check.yml` + `docs/POLITICA_COMMITS.md`
- **H-02** validación `JwtTokenProvider` — secret ≥ 32 bytes UTF-8
- **H-03** Swagger UI + api-docs detrás de `hasRole('ADMIN')`
- **O-05** `registerFailedAttempt` atómico con `@Modifying @Query`
- **H-05** `docs/RESPUESTAS_PROFESOR.md`
- **U-01** `GET /api/users/me` + `POST /api/auth/change-password`
- **D-04** `frontend/src/pages/DashboardPage.tsx` con 4 KPIs (Citas hoy, Pacientes activos, Alertas, Recetas emitidas)

### Vigil (integrado a develop, falta PR formal)
- **VG-08** `JwtAuthenticationFilter` + `TenantContext` (ThreadLocal) + `JwtBlacklist`

### Sánchez (integrado a develop, falta PR formal)
- **S-01** Setup JUnit 5 + Mockito + smoke test aprobado

## Contrato acordado Dashboard (D-04 ↔ M-06)

`GET /api/dashboard/metrics` debe devolver:

```json
{
  "appointmentsToday": 0,
  "activePatients": 0,
  "alerts": 0,
  "prescriptionsIssued": 0
}
```

**Merino:** alinearse a estos nombres o coordinar cambio con Héctor.

## Cambios estructurales del repo (04/09)

1. Merge `main` → `develop` — develop ahora tiene la guía completa.
2. Integrada VG-08 de Vigil a develop.
3. Cherry-pick S-01 de Sánchez a develop.
4. `code-review-graph` inicializado (MCP + hook pre-commit).
5. Política de commits publicada en `docs/POLITICA_COMMITS.md` — CI rechaza `Co-authored-by:` de IA.

## Ritmo requerido

- Tareas restantes: **79**
- Días restantes al freeze: **17**
- Necesario: **~4.6 tareas/día del equipo entero**
- Ritmo actual del equipo: **<0.5 tareas/día**

## Riesgos abiertos

- **BLOQUEANTE:** Merino no ha arrancado **M-06** — sin ello, la pantalla del Dashboard se ve rota.
- **BLOQUEANTE:** Ventura no ha arrancado **VT-01** (`BaseEntity`) — bloquea VT-02 y feature `.dat` de Merino.
- **CRÍTICO:** 5 integrantes sin rama después de 5 días de sprint.
- **DOCUMENTO:** Vásquez no ha arrancado el documento (30% de la nota final).

## Antes de arrancar hoy: actualizar repo local

```bash
git fetch origin --prune
git checkout develop
git pull origin develop

# Si ya tienes rama de trabajo:
git checkout feature/avance2-<tu-apellido>
git rebase develop

# Si no tienes rama todavia:
git checkout -b feature/avance2-<tu-apellido>
```
