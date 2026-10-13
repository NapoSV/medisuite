#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
for file in RowMapper BaseJdbcDao; do
  test -f "backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/${file}.java"
done
for file in MetricsPort ReminderPort ReservationCodePort; do
  test -f "backend/src/main/java/com/sv/grupo7/medisuite/service/port/${file}.java"
done
test -f backend/src/main/java/com/sv/grupo7/medisuite/dto/reminder/AppointmentReminderCandidate.java

(cd backend && mvn -q -DskipTests compile && mvn -q -Dtest=BaseJdbcDaoTest test)
printf 'OK B4 JDBC base\n'