# Base de datos

- `schema.sql`: esquema base del proyecto (equivale a la versión 9 de Flyway).
  Las pruebas de integración lo usan como punto de partida.
- `seed.sql`: datos de demostración (tenants, usuarios, pacientes y citas sintéticas).
- `00_create_database.sql`: creación de la base y del usuario de la aplicación.

Los cambios posteriores viven en las migraciones Flyway de
`backend/src/main/resources/db/migration` (V10 a V14) y se aplican solos al
arrancar el backend. Por eso `schema.sql` no incluye, por ejemplo,
`appointment_reminders` (V11), la secuencia de códigos de reserva (V12) ni la
escala de prioridad `NORMAL/URGENTE/EMERGENCIA` (V14).

Instalación desde cero: ejecutar `00_create_database.sql`, `schema.sql` y
`seed.sql` en ese orden, y luego arrancar el backend para que Flyway aplique
V10 a V14.
