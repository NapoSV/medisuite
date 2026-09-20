-- F-03: Restricciones de unicidad multi-tenant
-- Añade las constraints que no existan; las que ya están (del schema original)
-- se detectan y se omiten. Idempotente.

-- users: email único por tenant
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'users'
          AND c.contype = 'u'
          AND array_to_string(
              ARRAY(SELECT a.attname FROM pg_attribute a
                    WHERE a.attrelid = c.conrelid
                      AND a.attnum = ANY(c.conkey)
                    ORDER BY a.attnum), ',')
              IN ('tenant_id,email','email,tenant_id')
    ) THEN
        ALTER TABLE users ADD CONSTRAINT ux_users_email_tenant UNIQUE (tenant_id, email);
    END IF;
END $$;

-- patients: DUI único por tenant (puede existir como patients_tenant_id_cif_key)
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'patients'
          AND c.contype = 'u'
          AND array_to_string(
              ARRAY(SELECT a.attname FROM pg_attribute a
                    WHERE a.attrelid = c.conrelid
                      AND a.attnum = ANY(c.conkey)
                    ORDER BY a.attnum), ',')
              IN ('tenant_id,dui','dui,tenant_id')
    ) THEN
        ALTER TABLE patients ADD CONSTRAINT ux_patients_dui_tenant UNIQUE (tenant_id, dui);
    END IF;
END $$;

-- doctors: número de licencia único por tenant
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'doctors'
          AND c.contype = 'u'
          AND array_to_string(
              ARRAY(SELECT a.attname FROM pg_attribute a
                    WHERE a.attrelid = c.conrelid
                      AND a.attnum = ANY(c.conkey)
                    ORDER BY a.attnum), ',')
              IN ('tenant_id,license_number','license_number,tenant_id')
    ) THEN
        ALTER TABLE doctors ADD CONSTRAINT ux_doctors_license_tenant UNIQUE (tenant_id, license_number);
    END IF;
END $$;

-- specialties: nombre único por tenant
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'specialties'
          AND c.contype = 'u'
          AND array_to_string(
              ARRAY(SELECT a.attname FROM pg_attribute a
                    WHERE a.attrelid = c.conrelid
                      AND a.attnum = ANY(c.conkey)
                    ORDER BY a.attnum), ',')
              IN ('tenant_id,name','name,tenant_id')
    ) THEN
        ALTER TABLE specialties ADD CONSTRAINT ux_specialties_name_tenant UNIQUE (tenant_id, name);
    END IF;
END $$;
