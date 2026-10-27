#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

if [ -f .env.local ]; then
  printf 'Ya existe .env.local — no se sobreescribe.\n'
  printf 'Borralo manualmente con: rm .env.local\n'
  exit 0
fi

printf 'LOCAL_DB_PASSWORD=%s\nLOCAL_JWT_SECRET=%s\n' \
  "$(openssl rand -hex 24)" \
  "$(openssl rand -hex 32)" > .env.local

printf 'Archivo .env.local creado correctamente.\n'
printf 'Ejecuta: docker compose --env-file .env.local -f compose.local.yml up --build -d --wait\n'
