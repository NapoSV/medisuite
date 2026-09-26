-- V9: Actualizar CHECK constraint de appointments para incluir PENDING
-- El constraint original no incluía PENDING como estado válido al crear una cita.
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check;

ALTER TABLE appointments
    ADD CONSTRAINT appointments_status_check
    CHECK (status IN (
        'PENDING',
        'SCHEDULED',
        'CONFIRMED',
        'IN_WAITING',
        'IN_CONSULTATION',
        'COMPLETED',
        'CANCELLED',
        'NO_SHOW'
    ));
