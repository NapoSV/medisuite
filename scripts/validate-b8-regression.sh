#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f scripts/validate-all.sh
for file in AppointmentServiceTest PatientServiceTest MedicalRecordServiceTest; do
  test -f "backend/src/test/java/com/sv/grupo7/medisuite/service/${file}.java"
done
(cd backend && mvn -q -Dtest=AppointmentServiceTest,PatientServiceTest,MedicalRecordServiceTest test)
printf 'OK B8 regresion\n'