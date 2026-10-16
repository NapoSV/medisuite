#!/usr/bin/env bash
# Verifica que toda tabla aplicativa en el schema public de Neon tenga los 4
# permisos basicos (SELECT, INSERT, UPDATE, DELETE) para clinica_app.
#
# Uso:
#   set -a; source .env; set +a
#   bash scripts/validate-grants.sh
#
# Falla con exit 1 si alguna tabla aplicativa esta sin permisos.
# Se omite flyway_schema_history (tabla interna de Flyway, no la usa la app).

set -euo pipefail

: "${DB_HOST:?falta DB_HOST en el entorno (source .env)}"
: "${DB_PORT:?falta DB_PORT}"
: "${DB_NAME:?falta DB_NAME}"
: "${DB_FLYWAY_USERNAME:?falta DB_FLYWAY_USERNAME}"
: "${DB_FLYWAY_PASSWORD:?falta DB_FLYWAY_PASSWORD}"
DB_SSLMODE="${DB_SSLMODE:-require}"

RESULT=$(PGPASSWORD="$DB_FLYWAY_PASSWORD" psql \
  "host=$DB_HOST port=$DB_PORT dbname=$DB_NAME user=$DB_FLYWAY_USERNAME sslmode=$DB_SSLMODE" \
  -t -A -c "
    SELECT t.table_name
    FROM information_schema.tables t
    LEFT JOIN information_schema.role_table_grants g
         ON g.table_schema = t.table_schema
         AND g.table_name  = t.table_name
         AND g.grantee     = 'clinica_app'
    WHERE t.table_schema = 'public'
      AND t.table_type   = 'BASE TABLE'
      AND t.table_name  <> 'flyway_schema_history'
    GROUP BY t.table_name
    HAVING COUNT(g.privilege_type) < 4
    ORDER BY t.table_name;")

if [ -z "$RESULT" ]; then
  echo "OK: todas las tablas aplicativas en public tienen los 4 permisos para clinica_app."
  exit 0
fi

echo "ERROR: tablas sin los 4 permisos (SELECT/INSERT/UPDATE/DELETE) para clinica_app:" >&2
echo "$RESULT" >&2
echo "" >&2
echo "Desde V13 el ALTER DEFAULT PRIVILEGES cubre automaticamente objetos nuevos" >&2
echo "creados por neondb_owner. Si una tabla aparece aqui, revisa:" >&2
echo "  1) Si fue creada por otro usuario (no owner), moverla a owner o GRANT manual." >&2
echo "  2) Si existia antes de V13, agregar GRANT retroactivo en una nueva migracion." >&2
exit 1
