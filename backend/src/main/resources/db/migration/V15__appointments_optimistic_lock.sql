-- V15: bloqueo optimista para citas.
--
-- Dos usuarios pueden cancelar, completar o reprogramar la misma cita al mismo
-- tiempo. Sin control, gana la última escritura y la otra se pierde en silencio.
-- Con esta columna, JPA (@Version en Appointment) incrementa la versión en cada
-- UPDATE y lo condiciona a la versión leída: si otro usuario ya la cambió, el
-- UPDATE no afecta filas, Hibernate lanza OptimisticLockException y el backend
-- responde 409 para que el usuario recargue.
--
-- Las filas existentes empiezan en 0; ADD COLUMN con DEFAULT constante no
-- reescribe la tabla en PostgreSQL 11+.

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
