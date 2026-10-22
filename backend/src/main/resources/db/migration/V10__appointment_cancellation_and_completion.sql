-- V10: Trazabilidad de cancelacion y validacion de completado
-- Fase A Avance 3 (bugs reportados por Ing. Guevara):
--  * Cancelacion debe registrar motivo obligatorio y fecha
--  * Completado debe quedar auditado con fecha real

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS cancel_reason VARCHAR(500);

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMPTZ;

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS completed_at TIMESTAMPTZ;
