-- V13: correctivo de permisos y politica permanente.
--
-- Contexto del incidente (12/10/2026, reportado por Bayron):
-- V11 (B3a) creo appointment_reminders sin GRANT para clinica_app. El worker
-- fallo con "permission denied for table appointment_reminders" en cada ciclo.
-- Al revisar Neon se confirmo que las 14 tablas iniciales (appointments,
-- patients, doctors, etc.) tienen GRANTs porque alguien corrio
-- database/00_create_database.sql por fuera de Flyway. Ninguna migracion
-- V1..V11 incluye GRANTs. Cualquier migracion futura iba a reproducir el
-- mismo hueco (V12 de Merino, V__ siguientes de Flores, etc.).
--
-- Esta migracion:
--   1) Repara appointment_reminders (V11) sin editar su migracion ya aplicada.
--   2) Establece ALTER DEFAULT PRIVILEGES para que todo objeto que cree
--      neondb_owner en public desde ahora auto-conceda a clinica_app.
-- Resultado: ninguna migracion futura necesita acordarse de GRANTs; los
-- autores solo escriben el DDL del dominio.

-- (1) Correctivo retroactivo para V11.
GRANT SELECT, INSERT, UPDATE, DELETE ON appointment_reminders TO clinica_app;
GRANT USAGE, SELECT ON SEQUENCE appointment_reminders_id_seq TO clinica_app;

-- (2) Politica permanente: aplica a tablas y secuencias creadas desde ahora
-- por neondb_owner en el schema public.
ALTER DEFAULT PRIVILEGES FOR ROLE neondb_owner IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO clinica_app;
ALTER DEFAULT PRIVILEGES FOR ROLE neondb_owner IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO clinica_app;
