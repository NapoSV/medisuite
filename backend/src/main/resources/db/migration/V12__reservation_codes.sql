-- V12: Códigos de reserva únicos respaldados por secuencia PostgreSQL.
-- Fase B, tarea B3b (Alejandro Merino).
-- Formato: 'R-' + 8 dígitos (10 caracteres, cabe en VARCHAR(10)). Los códigos
-- históricos 'COD-####' no se reescriben. La secuencia no cicla: al superar
-- 99999999 nextval() falla en lugar de truncar y generar colisiones.

-- Prevalidación: si hay duplicados se aborta sin borrar ni modificar citas.
-- Comunicar los IDs del mensaje a Héctor para una reparación aprobada.
DO $$
DECLARE
    duplicados TEXT;
BEGIN
    SELECT string_agg(
               'tenant_id=' || tenant_id || ' appointment_ids=' || ids, '; ')
      INTO duplicados
      FROM (
          SELECT tenant_id,
                 string_agg(id::TEXT, ',' ORDER BY id) AS ids
            FROM appointments
           GROUP BY tenant_id, reservation_code
          HAVING COUNT(*) > 1
      ) d;

    IF duplicados IS NOT NULL THEN
        RAISE EXCEPTION 'V12 abortada: códigos de reserva duplicados. %', duplicados;
    END IF;
END $$;

-- Unicidad por tenant: se añade solo si V5 la dejó sin crear (ver V3).
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'appointments'
          AND c.contype = 'u'
          AND array_to_string(
              ARRAY(SELECT a.attname FROM pg_attribute a
                    WHERE a.attrelid = c.conrelid
                      AND a.attnum = ANY(c.conkey)
                    ORDER BY a.attnum), ',')
              IN ('tenant_id,reservation_code','reservation_code,tenant_id')
    ) THEN
        ALTER TABLE appointments
            ADD CONSTRAINT appointments_tenant_id_reservation_code_key
            UNIQUE (tenant_id, reservation_code);
    END IF;
END $$;

CREATE SEQUENCE IF NOT EXISTS reservation_code_seq
    AS BIGINT
    MINVALUE 1
    MAXVALUE 99999999
    START WITH 1
    NO CYCLE;

-- Si ya existen códigos 'R-########', la secuencia continúa después del mayor.
DO $$
DECLARE
    mayor BIGINT;
BEGIN
    SELECT MAX(substring(reservation_code FROM 3)::BIGINT)
      INTO mayor
      FROM appointments
     WHERE reservation_code ~ '^R-[0-9]{8}$';

    IF mayor IS NOT NULL THEN
        PERFORM setval('reservation_code_seq', mayor, TRUE);
    END IF;
END $$;
