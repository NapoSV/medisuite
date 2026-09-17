-- F-07: agregar indicador de cambio obligatorio de contraseña
ALTER TABLE users
    ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT TRUE;
