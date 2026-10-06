#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
test -f backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/DashboardResponse.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/service/DashboardMetricsServiceTest.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/controller/api/DashboardControllerTest.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDaoTest.java
test "$(docker info --format '{{.OSType}}')" = linux

(cd backend && mvn -q -DskipTests compile && mvn -q -Dtestcontainers.version=1.21.4 '-Dtest=DashboardMetricsServiceTest,DashboardControllerTest,DashboardJdbcDaoTest' test)

grep -q 'skipped="0"' backend/target/surefire-reports/TEST-com.sv.grupo7.medisuite.dao.jdbc.DashboardJdbcDaoTest.xml
printf 'OK B1: compilación, seguridad HTTP, servicio y PostgreSQL descartable. Falta QA manual de dos tenants.\n'
