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
--
-- Compatibilidad con entornos de prueba (Testcontainers / PostgreSQL
-- descartable): esos entornos corren con un unico superuser y no tienen
-- los roles clinica_app / neondb_owner. En ese caso la migracion es no-op:
-- registra un NOTICE y termina sin fallar.

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'clinica_app') THEN
        RAISE NOTICE 'V13: skip (rol clinica_app no existe; entorno de pruebas).';
        RETURN;
    END IF;

    -- (1) Correctivo retroactivo para V11.
    EXECUTE 'GRANT SELECT, INSERT, UPDATE, DELETE ON appointment_reminders TO clinica_app';
    EXECUTE 'GRANT USAGE, SELECT ON SEQUENCE appointment_reminders_id_seq TO clinica_app';

    -- (2) Politica permanente: aplica a tablas y secuencias creadas desde ahora
    -- por neondb_owner en el schema public. Si por alguna razon neondb_owner no
    -- existe, caemos a un ALTER DEFAULT PRIVILEGES sin FOR ROLE que cubre al
    -- usuario que ejecuta la migracion.
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'neondb_owner') THEN
        EXECUTE 'ALTER DEFAULT PRIVILEGES FOR ROLE neondb_owner IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO clinica_app';
        EXECUTE 'ALTER DEFAULT PRIVILEGES FOR ROLE neondb_owner IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO clinica_app';
    ELSE
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO clinica_app';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO clinica_app';
    END IF;
END $$;
