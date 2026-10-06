# Seguimiento de integración — Avance 3 Fase B

Coordinación: Héctor López. Cierre interno: **19/10/2026**. Entrega externa informada por el equipo: **25/10/2026**. Anotar el enlace real y evidencia solo cuando existan; una casilla vacía no significa terminado.

| B | Responsable | Entregable principal | PR / enlace | Pruebas o evidencia | Revisor sugerido | Estado |
|---|---|---|---|---|---|---|
| B1 | Héctor López | Dashboard JDBC y alcance por rol/tenant | pendiente | HTTP/servicio y PostgreSQL descartable pasan; QA manual de dos tenants pendiente | William, Vigil | En desarrollo |
| B2 | Alejandro Vigil | Pool `jdbcDataSource` y parámetros | pendiente | Configuración, conexión y carga | Héctor | Pendiente |
| B3a | Bayron Orellana | Recordatorios concurrentes y V11 | pendiente | Concurrencia, idempotencia y migración | Flores | Pendiente |
| B3b | Alejandro Merino | Códigos de reserva y V12 | pendiente | Unicidad bajo carrera y migración | Flores | Pendiente |
| B4 | Carlos Ventura | DAO genérico, puertos y UML | pendiente | Tests de contrato/DAO y diagrama | Héctor | Pendiente |
| B5 | Walter Vásquez | Excepciones JDBC seguras | pendiente | SQLState, HTTP y sanitización | Héctor | Pendiente |
| B6 | William Melgar | Dashboard UI | pendiente | Estados de carga/error, roles y contrato | Héctor | Pendiente |
| B7 | Zair Díaz | Expediente UI | pendiente | Navegación y prioridades de triaje | Flores | Pendiente |
| B8 | Walter Flores | Regresión y seguridad multi-tenant | pendiente | Suite y reporte de hallazgos | Héctor | Pendiente |
| B9 | Erika Fuentes | Pruebas de recetas y README | pendiente | Pruebas entre tenants y guía ejecutable | Flores | Pendiente |
| B10 | Nicole Sánchez | Cohesión, legibilidad y documento final | pendiente | Documento de 18 secciones, defensa y evidencias | Héctor | Pendiente |

## Puertas de integración

1. Confirmar rama base `feature/avance3-fase-b` y PR de cada dueño; no asumir merge por nombre de commit.
2. B2/B4/B5 antes de dar B1 por integrado; B1/B6 deben validar juntos JSON y permisos.
3. Reservar 18/10 para demo transversal y 19/10 para correcciones; cada persona aporta una conclusión y cinco respuestas de defensa.
4. Verificar A1/A2/A3 contra código, tests, migraciones, documento, video y repositorio público. Un PR abierto o un script presente no equivale a evidencia pasada.

Estado observado al iniciar B1 el 06/10/2026: la rama remota base no existía, la búsqueda de PR por `avance3` estaba vacía y B2/B4/B5 no estaban en el checkout. Actualizar esta línea cuando cambie la situación.
