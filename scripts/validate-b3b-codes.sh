#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/main/resources/db/migration/V12__reservation_codes.sql
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReservationCodeJdbcDao.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGenerator.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/ReservationCodeGeneratorTest.java
(cd backend && mvn -q -Dtest=ReservationCodeGeneratorTest,AppointmentServiceTest test)
printf 'OK B3b codigos de reserva\n'
