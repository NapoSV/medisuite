# Cronograma Gantt — Proyecto completo · MediSuite

> **Este es el Gantt que va en el documento entregable (sección 9).**
> Aclaración del ingeniero (27/07/2026): el Gantt y la planificación deben cubrir
> **todo el proyecto hasta la entrega final**, no solo el Avance 1. Las fases
> futuras pueden representarse con tareas genéricas.
>
> **Cómo usarlo:** abre esta página en GitHub con tu navegador —
> https://github.com/NapoSV/medisuite/blob/main/docs/diagramas/gantt_proyecto_completo.md —
> GitHub dibuja el diagrama automáticamente. Toma una captura con `Win + Shift + S`
> y pégala en el Word del equipo, sección "Cronograma Gantt".
>
> El Gantt **oficial** vive en Microsoft Planner del equipo; esta versión es el
> respaldo académico. El detalle día-a-día del Avance 1 está en
> [`gantt_avance1.md`](gantt_avance1.md); el plan completo de las fases 2 y 3 en
> [`../PLAN_FASES_2_3.md`](../PLAN_FASES_2_3.md).

**Hitos académicos:**

| Hito | Fecha |
|---|---|
| Entrega Avance 1 | 10/08/2026 |
| Entrega Avance 2 | semana del 21 al 26/09/2026 |
| Entrega final y defensa | semana del 26 al 31/10/2026 |

```mermaid
gantt
    title MediSuite · Proyecto completo · 15/07 – 31/10/2026
    dateFormat  YYYY-MM-DD
    axisFormat  %d/%m

    section Fase 1 · Avance 1
    Documento (portada, HU, planificacion, Gantt)   :f1doc, 2026-07-15, 25d
    Base de datos inicial en Neon (17 tablas)       :f1db, 2026-07-22, 3d
    Backend base (entidades, JWT, login)            :f1be, 2026-07-23, 15d
    Frontend base (React + LoginPage)               :f1fe, 2026-07-25, 14d
    QA de login                                     :f1qa, 2026-08-05, 3d
    Entrega Avance 1                                :milestone, m1, 2026-08-10, 1d

    section Fase 2 · Avance 2
    Modulo de citas (backend + calendario)          :f2dev, 2026-08-11, 27d
    Blindaje de seguridad (S1-S7)                   :f2sec, 2026-08-24, 20d
    QA de citas y seguridad                         :f2qa, 2026-09-07, 8d
    Documento Avance 2 (2do objetivo especifico)    :f2doc, 2026-09-07, 12d
    Semana de entrega Avance 2                      :f2ent, 2026-09-21, 6d
    Entrega Avance 2                                :milestone, m2, 2026-09-26, 1d

    section Fase 3 · Entrega final
    Triaje y expediente clinico                     :f3tri, 2026-09-21, 14d
    Recetas y reportes (PDF + dashboard)            :f3rec, 2026-10-05, 11d
    Inventario y compras (extension SaaS)           :f3inv, 2026-10-12, 9d
    Deploy, demo y documento final                  :f3dep, 2026-10-19, 7d
    Semana de entrega final y defensa               :f3def, 2026-10-26, 6d
    Entrega final y defensa                         :milestone, m3, 2026-10-31, 1d
```
