#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

test -f backend/src/main/resources/db/migration/V11__appointment_reminders.sql
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReminderJdbcDao.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/ReminderReader.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/dto/reminders/AppointmentReminder.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderService.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/concurrent/AppointmentReminderServiceTest.java

(cd backend && mvn -q -DskipTests compile)
(cd backend && mvn -q -Dtest=AppointmentReminderServiceTest test)

printf 'OK B3a recordatorios: V11, DAO JDBC, worker concurrente y 10 tests.\n'