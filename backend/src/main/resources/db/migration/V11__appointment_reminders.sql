-- V11: Cola persistida de recordatorios de citas.
-- Fase B, tarea B3a (Bayron Orellana). Propietario único: @borellana.
-- Zona horaria de cálculo: America/El_Salvador.
-- Idempotencia garantizada por UNIQUE (tenant_id, appointment_id, scheduled_for).
-- El worker NO hereda TenantContext: cada fila lleva su tenant_id explícito.
-- No se marca SENT porque no existe canal de envío real en esta fase.

CREATE TABLE IF NOT EXISTS appointment_reminders (
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      BIGINT NOT NULL REFERENCES tenants(id),
    appointment_id BIGINT NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    scheduled_for  TIMESTAMPTZ NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at   TIMESTAMPTZ,
    CONSTRAINT appointment_reminders_status_check
        CHECK (status IN ('PENDING', 'CANCELLED')),
    CONSTRAINT ux_appt_reminder_tenant_appt_when
        UNIQUE (tenant_id, appointment_id, scheduled_for)
);

CREATE INDEX IF NOT EXISTS idx_appt_reminders_pending
    ON appointment_reminders (tenant_id, status, scheduled_for);