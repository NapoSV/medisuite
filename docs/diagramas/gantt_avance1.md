# Cronograma Gantt — Avance 1 · MediSuite

> **Cómo usar este archivo (Tarea 9.1):** abre esta página en GitHub con tu navegador —
> https://github.com/NapoSV/medisuite/blob/main/docs/diagramas/gantt_avance1.md —
> y GitHub dibuja el diagrama automáticamente (no necesitas instalar ni pagar nada).
> Luego toma una captura con `Win + Shift + S` y pégala en el Word del equipo,
> sección "Cronograma Gantt".
>
> El Gantt **oficial** vive en Microsoft Planner del equipo; esta versión es el
> respaldo académico para el documento.
>
> **⚠️ Actualización 27/07/2026:** el ingeniero aclaró que el Gantt del documento
> debe cubrir **todo el proyecto** (hasta la entrega final). Para la sección 9 del
> Word usa ahora [`gantt_proyecto_completo.md`](gantt_proyecto_completo.md);
> este archivo queda como detalle interno del Avance 1.

```mermaid
gantt
    title Avance 1 · MediSuite · 15/07 – 10/08/2026
    dateFormat  YYYY-MM-DD
    axisFormat  %d/%m
    section Documento
    Portada, objetivos, equipo Scrum            :doc1, 2026-07-15, 6d
    Roles, HU, alcances, planificación          :doc2, 2026-07-17, 9d
    Cronograma Gantt                            :doc3, 2026-07-21, 5d
    Entradas/Salidas + Entidades                :doc4, 2026-07-22, 7d
    Conclusiones + Bibliografía                 :doc5, 2026-08-03, 5d
    Consolidación y PDF final                   :doc6, 2026-08-08, 2d
    Entrega                                     :milestone, entrega, 2026-08-10, 1d
    section Base de datos
    Ejecutar schema.sql y seed.sql en Neon      :db1, 2026-07-22, 3d
    section Backend
    Init Spring Boot + config Neon              :be1, 2026-07-23, 6d
    Estructura de paquetes + Entidades JPA      :be2, 2026-07-28, 5d
    Repositories JPA                            :be3, 2026-08-01, 3d
    DTOs + AuthService                          :be4, 2026-08-02, 3d
    Spring Security + JWT + /api/auth/login     :be5, 2026-08-04, 3d
    section Frontend
    Init Vite + React + Tailwind + Zustand      :fe1, 2026-07-25, 6d
    Pantalla LoginPage.tsx                      :fe2, 2026-08-04, 4d
    section QA
    Casos de prueba de login                    :qa1, 2026-08-05, 3d
```
