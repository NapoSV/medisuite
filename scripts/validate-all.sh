#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
(cd backend && mvn -B -ntp '-Dtest=*Test,*Tests,*IT' test)
(cd frontend && pnpm typecheck && pnpm build && pnpm lint)
printf 'OK compilacion, pruebas unitarias, integracion PostgreSQL y frontend\n'
