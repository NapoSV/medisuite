#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
(cd backend && mvn test)
(cd frontend && pnpm build && pnpm lint)
printf 'OK compilacion, pruebas unitarias y frontend\n'