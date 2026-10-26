# Base de datos

La opción recomendada para desarrollo es `compose.local.yml`, descrita en el
[README principal](../README.md). Inicializa una base PostgreSQL local y aplica
estos pasos automáticamente, sin conectarse a Neon.

- `00_create_database.sql`: crea `clinica_dev`; requiere un usuario PostgreSQL
  autorizado y no crea el usuario de aplicación ni su contraseña.
- `schema.sql`: esquema base equivalente a Flyway V9.
- `seed.sql`: datos sintéticos y cuentas de demostración, para una base recién
  creada. Debe ejecutarse después del esquema y antes del primer arranque del backend.
- `backend/src/main/resources/db/migration`: migraciones V10–V15, ejecutadas por Flyway.

Para instalación manual: crea la base y un usuario de desarrollo propio; conectado
a esa base, ejecuta `schema.sql`, después `seed.sql` y luego inicia el backend.
No repitas el seed sobre una base existente: usa identificadores iniciales y las
prioridades del esquema V9. V14 convierte esas prioridades a `NORMAL/URGENTE/EMERGENCIA`.

Las migraciones agregan, entre otros elementos, recordatorios (V11), secuencia de
reservas (V12), permisos (V13), prioridades (V14) e índices del dashboard (V15).
El usuario de migraciones debe tener permisos para aplicar esos cambios.

`DemoInstallationIT` comprueba el recorrido esquema → seed → migraciones, las
cuentas demo y los datos clínicos. Las pruebas usan una base PostgreSQL desechable.
