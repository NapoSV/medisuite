#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/PrescriptionServiceTest.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/controller/api/PrescriptionControllerTest.java
test -f README.md
(cd backend && mvn -q -Dtest=PrescriptionServiceTest,PrescriptionControllerTest test)
printf 'OK B9 recetas y README\n'