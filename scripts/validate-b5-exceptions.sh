#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f backend/src/main/java/com/sv/grupo7/medisuite/exception/JdbcErrorCode.java
test -f backend/src/main/java/com/sv/grupo7/medisuite/exception/DataAccessException.java
test -f backend/src/test/java/com/sv/grupo7/medisuite/exception/JdbcErrorCodeTest.java
(cd backend && mvn -q -DskipTests compile && mvn -q -Dtest=JdbcErrorCodeTest test)
printf 'OK B5 excepciones\n'