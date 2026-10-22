-- V14: alinea la escala de prioridad de triaje con el contrato del sistema.
--
-- Contexto (22/10/2026): la decision de coordinacion del 09/10 fijo la escala
-- NORMAL / URGENTE / EMERGENCIA (frontend, MedicalRecordService y
-- DashboardJdbcDao ya la usan). Esa decision asumio que ninguna base imponia
-- un CHECK sobre vital_signs.priority, pero database/schema.sql (baseline V9)
-- crea vital_signs_priority_check con LOW / MEDIUM / HIGH / CRITICAL, y esa
-- restriccion esta activa en Neon. Resultado: registrar signos vitales fallaba
-- y el dashboard nunca contaba alertas porque los datos decian CRITICAL.
--
-- Esta migracion:
--   1) Quita la restriccion antigua (si existe; las bases de QA no la tienen).
--   2) Convierte los valores existentes a la escala vigente.
--   3) Crea la restriccion con la escala vigente.
-- HIGH se lleva a URGENTE: la escala nueva tiene un solo nivel entre normal y
-- emergencia. Valores nulos se conservan (el servicio asigna NORMAL al crear).

ALTER TABLE vital_signs DROP CONSTRAINT IF EXISTS vital_signs_priority_check;

UPDATE vital_signs
SET priority = CASE UPPER(priority)
        WHEN 'LOW'      THEN 'NORMAL'
        WHEN 'MEDIUM'   THEN 'URGENTE'
        WHEN 'HIGH'     THEN 'URGENTE'
        WHEN 'CRITICAL' THEN 'EMERGENCIA'
        ELSE UPPER(priority)
    END
WHERE priority IS NOT NULL;

ALTER TABLE vital_signs
    ADD CONSTRAINT vital_signs_priority_check
    CHECK (priority IN ('NORMAL', 'URGENTE', 'EMERGENCIA'));
