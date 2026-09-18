CREATE TABLE IF NOT EXISTS medical_records (
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      BIGINT NOT NULL REFERENCES tenants(id),
    patient_id     BIGINT NOT NULL UNIQUE REFERENCES patients(id),
    created_on     DATE NOT NULL DEFAULT CURRENT_DATE,
    general_notes  TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
