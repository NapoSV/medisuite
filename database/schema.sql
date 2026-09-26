-- MediSuite — Esquema real PostgreSQL (exportado desde Neon, clinica_dev)
-- Última sincronización: 2026-09-26
-- Usuario owner: clinica_dev
-- Para levantar desde cero: psql -U clinica_dev -d clinica_dev -f schema.sql

-- ─── tenants ─────────────────────────────────────────────────────────────────
CREATE TABLE "tenants" (
    "id"                BIGSERIAL PRIMARY KEY,
    "slug"              VARCHAR(50) NOT NULL CONSTRAINT "tenants_slug_key" UNIQUE,
    "commercial_name"   VARCHAR(150) NOT NULL,
    "legal_name"        VARCHAR(150),
    "tax_id"            VARCHAR(30),
    "country"           VARCHAR(60),
    "timezone"          VARCHAR(50) NOT NULL DEFAULT 'America/El_Salvador',
    "default_language"  VARCHAR(5) NOT NULL DEFAULT 'es',
    "plan"              VARCHAR(20) NOT NULL DEFAULT 'FREE',
    "status"            VARCHAR(20) NOT NULL DEFAULT 'TRIAL',
    "trial_expires_at"  DATE,
    "logo_url"          VARCHAR(255),
    "primary_color"     VARCHAR(7),
    "max_users"         INTEGER NOT NULL DEFAULT 10,
    "max_patients"      INTEGER NOT NULL DEFAULT 500,
    "max_doctors"       INTEGER NOT NULL DEFAULT 10,
    "created_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT "tenants_plan_check"   CHECK (plan   IN ('FREE','STARTER','PRO','ENTERPRISE')),
    CONSTRAINT "tenants_status_check" CHECK (status IN ('TRIAL','ACTIVE','SUSPENDED','CANCELLED'))
);

-- ─── users ───────────────────────────────────────────────────────────────────
CREATE TABLE "users" (
    "id"                      BIGSERIAL PRIMARY KEY,
    "tenant_id"               BIGINT NOT NULL REFERENCES "tenants"("id"),
    "first_name"              VARCHAR(80) NOT NULL,
    "last_name"               VARCHAR(80) NOT NULL,
    "cif"                     VARCHAR(20) NOT NULL,
    "email"                   VARCHAR(150) NOT NULL,
    "password_hash"           VARCHAR(255) NOT NULL,
    "role"                    VARCHAR(20) NOT NULL,
    "active"                  BOOLEAN NOT NULL DEFAULT true,
    "failed_login_attempts"   INTEGER NOT NULL DEFAULT 0,
    "locked_until"            TIMESTAMPTZ,
    "created_at"              TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"              TIMESTAMPTZ NOT NULL DEFAULT now(),
    "must_change_password"    BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT "users_tenant_id_cif_key"   UNIQUE ("tenant_id", "cif"),
    CONSTRAINT "users_tenant_id_email_key" UNIQUE ("tenant_id", "email"),
    CONSTRAINT "users_role_check" CHECK (role IN ('DOCTOR','NURSE','ADMIN','RECEPTIONIST'))
);

-- ─── specialties ─────────────────────────────────────────────────────────────
CREATE TABLE "specialties" (
    "id"          BIGSERIAL PRIMARY KEY,
    "tenant_id"   BIGINT NOT NULL REFERENCES "tenants"("id"),
    "name"        VARCHAR(100) NOT NULL,
    "active"      BOOLEAN NOT NULL DEFAULT true,
    "created_at"  TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT "specialties_tenant_id_name_key" UNIQUE ("tenant_id", "name")
);

-- ─── patients ────────────────────────────────────────────────────────────────
-- Nota: la columna se llama "dui" en la BD (renombrada desde "cif" por V2).
-- El constraint mantiene el nombre "patients_tenant_id_cif_key" por compatibilidad.
CREATE TABLE "patients" (
    "id"                BIGSERIAL PRIMARY KEY,
    "tenant_id"         BIGINT NOT NULL REFERENCES "tenants"("id"),
    "user_id"           BIGINT CONSTRAINT "patients_user_id_key" UNIQUE REFERENCES "users"("id"),
    "birth_date"        DATE NOT NULL,
    "phone"             VARCHAR(20),
    "address"           VARCHAR(200),
    "emergency_contact" VARCHAR(100),
    "blood_type"        VARCHAR(5),
    "allergies"         TEXT,
    "created_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    "first_name"        VARCHAR(80) NOT NULL,
    "last_name"         VARCHAR(80) NOT NULL,
    "dui"               VARCHAR(20) NOT NULL,
    CONSTRAINT "patients_tenant_id_cif_key" UNIQUE ("tenant_id", "dui")
);
CREATE INDEX "idx_patients_tenant" ON "patients" ("tenant_id");

-- ─── doctors ─────────────────────────────────────────────────────────────────
CREATE TABLE "doctors" (
    "id"                 BIGSERIAL PRIMARY KEY,
    "tenant_id"          BIGINT NOT NULL REFERENCES "tenants"("id"),
    "user_id"            BIGINT NOT NULL CONSTRAINT "doctors_user_id_key" UNIQUE REFERENCES "users"("id"),
    "specialty_id"       BIGINT NOT NULL REFERENCES "specialties"("id"),
    "license_number"     VARCHAR(30) NOT NULL,
    "available_schedule" JSONB,
    "created_at"         TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"         TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT "doctors_tenant_id_license_number_key" UNIQUE ("tenant_id", "license_number")
);
CREATE INDEX "idx_doctors_specialty" ON "doctors" ("specialty_id");

-- ─── nurses ──────────────────────────────────────────────────────────────────
CREATE TABLE "nurses" (
    "id"            BIGSERIAL PRIMARY KEY,
    "tenant_id"     BIGINT NOT NULL REFERENCES "tenants"("id"),
    "user_id"       BIGINT NOT NULL CONSTRAINT "nurses_user_id_key" UNIQUE REFERENCES "users"("id"),
    "shift"         VARCHAR(20),
    "assigned_area" VARCHAR(100),
    "created_at"    TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX "idx_nurses_tenant" ON "nurses" ("tenant_id");

-- ─── administrators ───────────────────────────────────────────────────────────
CREATE TABLE "administrators" (
    "id"               BIGSERIAL PRIMARY KEY,
    "tenant_id"        BIGINT NOT NULL REFERENCES "tenants"("id"),
    "user_id"          BIGINT NOT NULL CONSTRAINT "administrators_user_id_key" UNIQUE REFERENCES "users"("id"),
    "permission_level" VARCHAR(20) NOT NULL DEFAULT 'STANDARD',
    "created_at"       TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX "idx_administrators_tenant" ON "administrators" ("tenant_id");

-- ─── receptionists ────────────────────────────────────────────────────────────
CREATE TABLE "receptionists" (
    "id"              BIGSERIAL PRIMARY KEY,
    "tenant_id"       BIGINT NOT NULL REFERENCES "tenants"("id"),
    "user_id"         BIGINT NOT NULL CONSTRAINT "receptionists_user_id_key" UNIQUE REFERENCES "users"("id"),
    "shift"           VARCHAR(20),
    "assigned_office" VARCHAR(50),
    "created_at"      TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX "idx_receptionists_tenant" ON "receptionists" ("tenant_id");

-- ─── appointments ─────────────────────────────────────────────────────────────
-- Estados válidos del flujo clínico completo (actualizado 2026-09-26 via V9).
CREATE TABLE "appointments" (
    "id"               BIGSERIAL PRIMARY KEY,
    "tenant_id"        BIGINT NOT NULL REFERENCES "tenants"("id"),
    "patient_id"       BIGINT NOT NULL REFERENCES "patients"("id"),
    "doctor_id"        BIGINT NOT NULL REFERENCES "doctors"("id"),
    "scheduled_at"     TIMESTAMPTZ NOT NULL,
    "status"           VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    "reason"           VARCHAR(200),
    "office"           VARCHAR(50),
    "reservation_code" VARCHAR(10) NOT NULL,
    "created_at"       TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT "appointments_tenant_id_reservation_code_key" UNIQUE ("tenant_id", "reservation_code"),
    CONSTRAINT "appointments_status_check" CHECK (status IN (
        'PENDING', 'SCHEDULED', 'CONFIRMED',
        'IN_WAITING', 'IN_CONSULTATION',
        'COMPLETED', 'CANCELLED', 'NO_SHOW', 'WAITING'
    ))
);
-- Índice parcial: un médico no puede tener dos citas activas en el mismo horario.
CREATE UNIQUE INDEX "uq_appointments_doctor_slot"
    ON "appointments" ("doctor_id", "scheduled_at")
    WHERE status <> 'CANCELLED';
CREATE INDEX "idx_appointments_tenant_date" ON "appointments" ("tenant_id", "scheduled_at");
CREATE INDEX "idx_appointments_patient"     ON "appointments" ("patient_id");

-- ─── medical_records ──────────────────────────────────────────────────────────
CREATE TABLE "medical_records" (
    "id"            BIGSERIAL PRIMARY KEY,
    "tenant_id"     BIGINT NOT NULL REFERENCES "tenants"("id"),
    "patient_id"    BIGINT NOT NULL CONSTRAINT "medical_records_patient_id_key" UNIQUE REFERENCES "patients"("id"),
    "created_on"    DATE NOT NULL DEFAULT CURRENT_DATE,
    "general_notes" TEXT,
    "created_at"    TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX "idx_medical_records_tenant" ON "medical_records" ("tenant_id");

-- ─── vital_signs ──────────────────────────────────────────────────────────────
CREATE TABLE "vital_signs" (
    "id"                BIGSERIAL PRIMARY KEY,
    "tenant_id"         BIGINT NOT NULL REFERENCES "tenants"("id"),
    "medical_record_id" BIGINT NOT NULL REFERENCES "medical_records"("id"),
    "recorded_at"       TIMESTAMPTZ NOT NULL DEFAULT now(),
    "weight_kg"         NUMERIC(5,2),
    "height_cm"         NUMERIC(5,2),
    "blood_pressure"    VARCHAR(15),
    "temperature_c"     NUMERIC(4,1),
    "heart_rate"        INTEGER,
    "symptoms"          TEXT,
    "priority"          VARCHAR(10),
    "created_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT "vital_signs_priority_check" CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL'))
);
CREATE INDEX "idx_vital_signs_record_time" ON "vital_signs" ("medical_record_id", "recorded_at");
CREATE INDEX "idx_vital_signs_tenant"      ON "vital_signs" ("tenant_id");

-- ─── prescriptions ────────────────────────────────────────────────────────────
-- Refactorizado en V7: los medicamentos viven en prescription_items (1:N).
CREATE TABLE "prescriptions" (
    "id"                BIGSERIAL PRIMARY KEY,
    "tenant_id"         BIGINT NOT NULL REFERENCES "tenants"("id"),
    "medical_record_id" BIGINT NOT NULL REFERENCES "medical_records"("id"),
    "doctor_id"         BIGINT NOT NULL REFERENCES "doctors"("id"),
    "issued_on"         DATE NOT NULL DEFAULT CURRENT_DATE,
    "instructions"      TEXT,
    "created_at"        TIMESTAMPTZ NOT NULL DEFAULT now(),
    "updated_at"        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX "idx_prescriptions_record" ON "prescriptions" ("medical_record_id");
CREATE INDEX "idx_prescriptions_doctor" ON "prescriptions" ("doctor_id");
CREATE INDEX "idx_prescriptions_tenant" ON "prescriptions" ("tenant_id");

-- ─── prescription_items ───────────────────────────────────────────────────────
CREATE TABLE "prescription_items" (
    "id"              BIGSERIAL PRIMARY KEY,
    "prescription_id" BIGINT NOT NULL REFERENCES "prescriptions"("id") ON DELETE CASCADE,
    "order_idx"       INTEGER NOT NULL DEFAULT 0,
    "medication"      VARCHAR(200) NOT NULL,
    "dose"            VARCHAR(100),
    "frequency"       VARCHAR(100),
    "duration_days"   INTEGER
);
CREATE INDEX "idx_prescription_items_rx" ON "prescription_items" ("prescription_id", "order_idx");

-- ─── audit_logs ───────────────────────────────────────────────────────────────
-- Append-only: sin updated_at, sin trigger de actualización.
CREATE TABLE "audit_logs" (
    "id"          BIGSERIAL PRIMARY KEY,
    "tenant_id"   BIGINT REFERENCES "tenants"("id"),
    "user_id"     BIGINT REFERENCES "users"("id"),
    "action"      VARCHAR(20) NOT NULL,
    "entity_name" VARCHAR(60) NOT NULL,
    "entity_id"   BIGINT,
    "data_before" JSONB,
    "data_after"  JSONB,
    "ip_address"  VARCHAR(45),
    "created_at"  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX "idx_audit_logs_entity"      ON "audit_logs" ("entity_name", "entity_id");
CREATE INDEX "idx_audit_logs_tenant_time" ON "audit_logs" ("tenant_id", "created_at");
