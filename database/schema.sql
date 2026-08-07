-- MediSuite — Esquema PostgreSQL 16 — 17 tablas

-- ─── Función genérica para mantener updated_at ──────────────────────────
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ─── 1. tenants ──────────────────────────────────────────────────────────
CREATE TABLE tenants (
    id                      BIGSERIAL PRIMARY KEY,
    slug                    VARCHAR(50) UNIQUE NOT NULL,
    commercial_name         VARCHAR(150) NOT NULL,
    legal_name              VARCHAR(150),
    tax_id                  VARCHAR(30),
    country                 VARCHAR(60),
    timezone                VARCHAR(50) NOT NULL DEFAULT 'America/El_Salvador',
    default_language        VARCHAR(5) NOT NULL DEFAULT 'es',
    plan                    VARCHAR(20) NOT NULL DEFAULT 'FREE'
                                CHECK (plan IN ('FREE','STARTER','PRO','ENTERPRISE')),
    status                  VARCHAR(20) NOT NULL DEFAULT 'TRIAL'
                                CHECK (status IN ('TRIAL','ACTIVE','SUSPENDED','CANCELLED')),
    trial_expires_at        DATE,
    logo_url                VARCHAR(255),
    primary_color           VARCHAR(7),
    max_users               INT NOT NULL DEFAULT 10,
    max_patients            INT NOT NULL DEFAULT 500,
    max_doctors             INT NOT NULL DEFAULT 10,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TRIGGER trg_tenants_updated_at BEFORE UPDATE ON tenants
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 2. users (Usuario) ──────────────────────────────────────────────────
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT NOT NULL REFERENCES tenants(id),
    first_name      VARCHAR(80) NOT NULL,
    last_name       VARCHAR(80) NOT NULL,
    cif             VARCHAR(20) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20) NOT NULL
                        CHECK (role IN ('DOCTOR','NURSE','ADMIN','RECEPTIONIST')),
    active          BOOLEAN NOT NULL DEFAULT true,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, cif),
    UNIQUE (tenant_id, email)
);
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 3. specialties (Especialidad — NUEVA en v2) ─────────────────────────
-- Normaliza la especialidad médica: antes era texto libre en doctors.specialty,
-- lo que permitía errores de tipeo ("Pediatria" vs "pediatría") e impedía reportes.
CREATE TABLE specialties (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT NOT NULL REFERENCES tenants(id),
    name            VARCHAR(100) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name)
);
CREATE TRIGGER trg_specialties_updated_at BEFORE UPDATE ON specialties
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 4. patients (Paciente) ──────────────────────────────────────────────
-- El paciente NO tiene acceso al sistema (MVP). Es una entidad de datos
-- gestionada por el personal clínico (enfermera / recepcionista).
-- user_id es nullable: reservado para una fase futura si se habilita portal.
CREATE TABLE patients (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    first_name          VARCHAR(80) NOT NULL,
    last_name           VARCHAR(80) NOT NULL,
    cif                 VARCHAR(20) NOT NULL,
    user_id             BIGINT UNIQUE REFERENCES users(id),
    birth_date          DATE NOT NULL,
    phone               VARCHAR(20),
    address             VARCHAR(200),
    emergency_contact   VARCHAR(100),
    blood_type          VARCHAR(5),
    allergies           TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, cif)
);
CREATE INDEX idx_patients_tenant ON patients(tenant_id);
CREATE TRIGGER trg_patients_updated_at BEFORE UPDATE ON patients
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 5. doctors (Medico) — v2: specialty_id FK en vez de texto libre ─────
CREATE TABLE doctors (
    id                      BIGSERIAL PRIMARY KEY,
    tenant_id               BIGINT NOT NULL REFERENCES tenants(id),
    user_id                 BIGINT NOT NULL UNIQUE REFERENCES users(id),
    specialty_id            BIGINT NOT NULL REFERENCES specialties(id),
    license_number          VARCHAR(30) NOT NULL,
    available_schedule      JSONB,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, license_number)
);
CREATE INDEX idx_doctors_specialty ON doctors(specialty_id);
CREATE TRIGGER trg_doctors_updated_at BEFORE UPDATE ON doctors
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 6. nurses (Enfermera) ───────────────────────────────────────────────
CREATE TABLE nurses (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT NOT NULL REFERENCES tenants(id),
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id),
    shift           VARCHAR(20),
    assigned_area   VARCHAR(100),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_nurses_tenant ON nurses(tenant_id);
CREATE TRIGGER trg_nurses_updated_at BEFORE UPDATE ON nurses
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 7. administrators (Administrador) ──────────────────────────────────
CREATE TABLE administrators (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    user_id             BIGINT NOT NULL UNIQUE REFERENCES users(id),
    permission_level    VARCHAR(20) NOT NULL DEFAULT 'STANDARD',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_administrators_tenant ON administrators(tenant_id);
CREATE TRIGGER trg_administrators_updated_at BEFORE UPDATE ON administrators
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 8. receptionists (Recepcionista) ────────────────────────────────────
CREATE TABLE receptionists (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    user_id             BIGINT NOT NULL UNIQUE REFERENCES users(id),
    shift               VARCHAR(20),
    assigned_office     VARCHAR(50),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_receptionists_tenant ON receptionists(tenant_id);
CREATE TRIGGER trg_receptionists_updated_at BEFORE UPDATE ON receptionists
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 9. appointments (Cita) — v2: reservation_code + anti doble-reserva ──
CREATE TABLE appointments (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    patient_id          BIGINT NOT NULL REFERENCES patients(id),
    doctor_id           BIGINT NOT NULL REFERENCES doctors(id),
    scheduled_at        TIMESTAMPTZ NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
                            CHECK (status IN ('SCHEDULED','CANCELLED','COMPLETED','WAITING')),
    reason              VARCHAR(200),
    office              VARCHAR(50),
    reservation_code    VARCHAR(10) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, reservation_code)
);
-- Regla de negocio "cero doble reserva" garantizada a nivel de BD:
-- un médico no puede tener dos citas activas en el mismo horario exacto.
-- Las canceladas no bloquean el slot (por eso el índice es parcial).
CREATE UNIQUE INDEX uq_appointments_doctor_slot
    ON appointments(doctor_id, scheduled_at)
    WHERE status <> 'CANCELLED';
CREATE INDEX idx_appointments_tenant_date ON appointments(tenant_id, scheduled_at);
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE TRIGGER trg_appointments_updated_at BEFORE UPDATE ON appointments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 10. medical_records (Expediente) ─────────────────────────────────────
CREATE TABLE medical_records (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    patient_id          BIGINT NOT NULL UNIQUE REFERENCES patients(id),
    created_on          DATE NOT NULL DEFAULT current_date,
    general_notes       TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_medical_records_tenant ON medical_records(tenant_id);
CREATE TRIGGER trg_medical_records_updated_at BEFORE UPDATE ON medical_records
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 11. vital_signs (SignoVital) ────────────────────────────────────────
CREATE TABLE vital_signs (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    medical_record_id   BIGINT NOT NULL REFERENCES medical_records(id),
    recorded_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    weight_kg           NUMERIC(5,2),
    height_cm           NUMERIC(5,2),
    blood_pressure      VARCHAR(15),
    temperature_c       NUMERIC(4,1),
    heart_rate          INT,
    symptoms            TEXT,
    priority            VARCHAR(10) CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
-- Índice compuesto: el timeline del expediente se consulta siempre
-- "signos de este expediente, del más reciente al más viejo".
CREATE INDEX idx_vital_signs_record_time ON vital_signs(medical_record_id, recorded_at DESC);
CREATE INDEX idx_vital_signs_tenant ON vital_signs(tenant_id);
CREATE TRIGGER trg_vital_signs_updated_at BEFORE UPDATE ON vital_signs
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 12. prescriptions (Receta) ──────────────────────────────────────────
CREATE TABLE prescriptions (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id),
    medical_record_id   BIGINT NOT NULL REFERENCES medical_records(id),
    doctor_id           BIGINT NOT NULL REFERENCES doctors(id),
    issued_on           DATE NOT NULL DEFAULT current_date,
    medications         TEXT NOT NULL,
    dosage              VARCHAR(150),
    duration            VARCHAR(50),
    instructions        TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_prescriptions_record ON prescriptions(medical_record_id);
CREATE INDEX idx_prescriptions_doctor ON prescriptions(doctor_id);
CREATE INDEX idx_prescriptions_tenant ON prescriptions(tenant_id);
CREATE TRIGGER trg_prescriptions_updated_at BEFORE UPDATE ON prescriptions
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ─── 13. audit_logs (AuditLog) ───────────────────────────────────────────
-- Tabla de solo escritura (append-only): no lleva updated_at ni trigger,
-- un registro de auditoría no debe modificarse después de creado.
CREATE TABLE audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    tenant_id       BIGINT REFERENCES tenants(id),
    user_id         BIGINT REFERENCES users(id),
    action          VARCHAR(20) NOT NULL,
    entity_name     VARCHAR(60) NOT NULL,
    entity_id       BIGINT,
    data_before     JSONB,
    data_after      JSONB,
    ip_address      VARCHAR(45),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_logs_tenant_time ON audit_logs(tenant_id, created_at DESC);
