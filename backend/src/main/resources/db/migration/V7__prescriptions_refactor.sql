-- Prescriptions ya existe con columnas viejas; hacer refactor idempotente
ALTER TABLE prescriptions DROP COLUMN IF EXISTS medications;
ALTER TABLE prescriptions DROP COLUMN IF EXISTS dosage;
ALTER TABLE prescriptions DROP COLUMN IF EXISTS duration;
-- issued_on y instructions se mantienen

CREATE TABLE IF NOT EXISTS prescription_items (
    id                BIGSERIAL PRIMARY KEY,
    prescription_id   BIGINT NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    order_idx         INTEGER NOT NULL DEFAULT 0,
    medication        VARCHAR(200) NOT NULL,
    dose              VARCHAR(100),
    frequency         VARCHAR(100),
    duration_days     INTEGER
);

CREATE INDEX IF NOT EXISTS idx_prescription_items_rx
    ON prescription_items (prescription_id, order_idx);
