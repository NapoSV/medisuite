CREATE TABLE IF NOT EXISTS appointments (
    id                BIGSERIAL PRIMARY KEY,
    tenant_id         BIGINT NOT NULL REFERENCES tenants(id),
    patient_id        BIGINT NOT NULL REFERENCES patients(id),
    doctor_id         BIGINT NOT NULL REFERENCES doctors(id),
    scheduled_at      TIMESTAMPTZ NOT NULL,
    status            VARCHAR(20) NOT NULL,
    reason            VARCHAR(200),
    office            VARCHAR(50),
    reservation_code  VARCHAR(10) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_appt_doctor_slot
    ON appointments (doctor_id, scheduled_at)
    WHERE status <> 'CANCELLED';

CREATE INDEX IF NOT EXISTS idx_appt_patient
    ON appointments (patient_id);

CREATE INDEX IF NOT EXISTS idx_appt_tenant_date
    ON appointments (tenant_id, scheduled_at);
