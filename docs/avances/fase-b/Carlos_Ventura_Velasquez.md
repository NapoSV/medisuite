# Guía Avance 3 Fase B — Carlos Mario Ventura Velásquez

> Responsable: Carlos Mario Ventura Velásquez · CIF 2026011585 · GitHub @mdealerdude (cuenta verificada el 07/10/2026)  
> Tarea: B4, DAO JDBC abstracto, interfaces y pruebas de persistencia  
> Rama personal: `b4-abstract-dao-interfaces` · PR listo: sábado 10/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

Debes esperar los PR de [Vigil B2](Alejandro_Vigil_Ramirez.md) @aavigil y [Vásquez B5](Walter_Vasquez_Amaya.md) @wvasquez. Necesitas el bean `jdbcDataSource` y el constructor/enum de `DataAccessException` acordados. Puedes diseñar contratos en una nota personal mientras tanto, pero no abras PR que importe clases que aún no están integradas.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git log --oneline origin/feature/avance3-fase-b -20
```

Confirma con @hlopez números de PR y verifica que ambos aparecen como mergeados en la rama. El texto del commit no basta si el PR está abierto. Tu trabajo desbloquea a Héctor, Bayron y Merino.

## 🎯 Qué vas a hacer y por qué

El Avance 2 ya tiene `BaseEntity` abstracta y `DatFileDao<T extends Serializable>` para respaldos `.dat`; el Avance 3 exige persistencia relacional **con JDBC explícito**. Crearás una base genérica reutilizable que use `Connection`, `PreparedStatement`, `ResultSet`, `executeQuery()` y `executeUpdate()` con try-with-resources. Los DAO de las funciones nuevas la extenderán o usarán sus métodos protegidos.

La clase abstracta y las interfaces deben representar necesidades reales, no figuras creadas solo para la rúbrica. Diseñarás tres puertos mínimos con Héctor/Bayron/Merino antes de codificar las implementaciones. También entregarás el diagrama UML actualizado de esta capa para el documento final.

**Rúbrica directa:** clases abstractas/interfaces (0.5), colecciones/genéricos (0.5), arquitectura (0.8 compartido), JDBC (1.0 compartido).

## 🛠️ Preparación

Abre Git Bash. Una rama separa tu trabajo; un commit agrupa una modificación lógica; un PR permite revisión. Desde tu clon:

```bash
cd /c/Users/hlopez/medisuite
java --version
mvn --version
git status --short
git fetch origin
git switch -c b4-abstract-dao-interfaces origin/feature/avance3-fase-b
git branch --show-current
```

Si tu clon tiene otra ruta, cambia solo la primera línea. Si hay cambios locales, no uses `git reset --hard`. Si la rama ya existe, usa `git switch b4-abstract-dao-interfaces` y sincroniza tras revisar el estado.

## ✍️ Paso a paso del código

### A. Contratos de puertos — firmas cerradas

> **🔒 Decisión cerrada (09/10/2026 — H. López, coordinación):** las firmas de los tres puertos están congeladas. No se negocian con los consumidores; se implementan tal cual. Si un consumidor necesita algo distinto, abre un issue aparte después de B4; no bloquea este PR.

Crea los tres archivos en `backend/src/main/java/com/sv/grupo7/medisuite/service/port/` con exactamente este contenido:

**`MetricsPort.java`**

```java
package com.sv.grupo7.medisuite.service.port;

import com.sv.grupo7.medisuite.dto.dashboard.DashboardQuery;
import com.sv.grupo7.medisuite.dto.dashboard.DashboardResponse;

public interface MetricsPort {
    DashboardResponse getMetrics(DashboardQuery query);
}
```

**`ReminderPort.java`**

```java
package com.sv.grupo7.medisuite.service.port;

import com.sv.grupo7.medisuite.dto.reminder.AppointmentReminderCandidate;
import java.time.Instant;
import java.util.List;

public interface ReminderPort {
    List<AppointmentReminderCandidate> findCandidates(Long tenantId, Instant windowStart, Instant windowEnd, int limit);
    int insertIfAbsent(Long tenantId, Long appointmentId, Instant scheduledAt);
}
```

**`ReservationCodePort.java`**

```java
package com.sv.grupo7.medisuite.service.port;

public interface ReservationCodePort {
    String nextCode(Long tenantId);
}
```

Reglas aplicadas en estas firmas: sin entidades JPA en el contrato, `tenantId` explícito como primer parámetro cuando corresponde, nombres de métodos en inglés verbo+objeto.

Dependencias de DTO:
- `DashboardQuery` y `DashboardResponse` ya existen en `dto/dashboard/` (B1 mergeado). Solo importar.
- `AppointmentReminderCandidate` lo crea Bayron (B3a) en `dto/reminder/`. Si al momento de abrir tu PR aún no está en develop, crea un DTO mínimo tú mismo (`tenantId`, `appointmentId`, `scheduledAt`, `patientPhone`) para que compile y márcalo en el PR como "DTO temporal, Bayron lo extiende en B3a".

Pega este bloque tal cual en la descripción de tu PR:

```markdown
## Contratos de puertos (cerrados por coordinación, 09/10/2026)

- MetricsPort.getMetrics(DashboardQuery) -> DashboardResponse
  Implementación: DashboardJdbcDao (B1 — @hlopez)
- ReminderPort.findCandidates(Long, Instant, Instant, int) -> List<AppointmentReminderCandidate>
- ReminderPort.insertIfAbsent(Long, Long, Instant) -> int
  Implementación: ReminderJdbcDao (B3a — @borellana)
- ReservationCodePort.nextCode(Long) -> String
  Implementación: ReservationCodeJdbcDao (B3b — @amerino)
```

### B. `RowMapper<T>`

Crea `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/RowMapper.java`. Es una `@FunctionalInterface` con un método `T map(ResultSet row) throws SQLException`. Su responsabilidad única es convertir la fila actual en objeto. El `ResultSet` se recorre en el DAO, por lo que `map` no debe llamar a `next()`.

### C. `BaseJdbcDao<T, ID>`

Crea `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDao.java`. Debe ser `abstract` y recibir `javax.sql.DataSource` por constructor; la subclase inyecta `@Qualifier("jdbcDataSource")`. Mantén `protected final` el DataSource. Si el parámetro `ID` no se usa en ningún método o contrato, elimínalo o dale uso real: un genérico decorativo no prueba reutilización.

Métodos protegidos mínimos:

| Método | Entrada | Salida | JDBC obligatorio |
|---|---|---|---|
| `query` | SQL, parámetros, mapper | `List<T>` | `executeQuery`, bucle `rs.next()` |
| `queryOne` | SQL, parámetros, mapper | `Optional<T>` | `executeQuery`, máximo una fila |
| `update` | SQL, parámetros | `int` filas | `executeUpdate` |
| `countBy` | SQL, parámetros | `long` | `executeQuery` y `rs.getLong(1)` |

No construyas SQL concatenando un dato de request. Usa `PreparedStatement` con índices 1..N y `setObject`/tipos precisos. Identificadores de tabla/columna no aceptan parámetros: no expongas un `tableName()` que tome texto del usuario. Separa métodos para un único statement de una operación multi-statement. En el segundo caso, usa una sola `Connection`, `setAutoCommit(false)`, `commit` y `rollback` en `catch`, con cierre en `finally`/try-with-resources. Documenta dónde se usa realmente `finally`; try-with-resources ya resuelve cierres ordinarios.

Si capturas `SQLException`, conviértela mediante `JdbcErrorCode.from(ex)` y `DataAccessException` de B5; conserva la causa. No registres SQL con parámetros sensibles ni datos médicos. El límite de transacción JPA no garantiza automáticamente que tu segundo pool JDBC participe: las operaciones mixtas requieren diseño de consistencia explícito.

### D. Interfaces de puerto

Crea en `backend/src/main/java/com/sv/grupo7/medisuite/service/port/`:

- `MetricsPort.java`.
- `ReminderPort.java`.
- `ReservationCodePort.java`.

Cada interface debe mencionar el tipo que realmente necesita el servicio, sin dependencia circular con clases de implementación. Si un DTO es necesario, acuerda su ubicación con el dueño antes de escribir importaciones. El principio de bajo acoplamiento se demuestra cuando el servicio inyecta el puerto y el test usa un fake/mock.

Para evidenciar **polimorfismo** de la rúbrica, muestra dos usos del mismo contrato: implementación real `DashboardJdbcDao implements MetricsPort` en producción y fake/mock `MetricsPort` en `DashboardMetricsServiceTest`. Explica qué método se invoca a través de la interfaz y por qué el servicio no necesita conocer la clase concreta. No añadas un `override` vacío únicamente para cumplir una palabra del enunciado.

### E. Pruebas de la base abstracta

Crea `backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDaoTest.java`. Una subclase de prueba mínima puede exponer los métodos protegidos; usa `DataSource`, `Connection`, `PreparedStatement` y `ResultSet` simulados con Mockito para verificar cierre, `setObject`, `executeQuery` y `executeUpdate`. Añade al menos una prueba de integración en PostgreSQL descartable/Testcontainers para confirmar SQL y recursos reales; la suite existente ya tiene esa dependencia.

Pruebas concretas:

1. `query` devuelve dos objetos en el mismo orden de filas.
2. `queryOne` devuelve `Optional.empty()` cuando no hay filas.
3. `update` devuelve el número de filas afectadas, incluido cero.
4. La tercera consulta fallida preserva `SQLException` y código en `DataAccessException`.
5. Se cierran `ResultSet`, `PreparedStatement` y `Connection` en éxito y error.
6. No se imprime SQL ni datos de prueba sensibles en la respuesta HTTP.

### F. UML y herencia acumulada

Actualiza el diagrama editable a partir de `docs/diagramas/DIAGRAMA_CLASES_MEDISUITE_AVANCE2.drawio`; no borres el original. Entrega una versión final `.drawio` y una exportación legible `.png`/`.svg` que muestre `BaseEntity`, `DatFileDao`, `BaseJdbcDao`, los puertos y los DAO concretos cuando estén integrados. Coordina con Héctor para evitar dibujar clases que aún no compilan. Explica `List<T>`, `Map<K,V>` y herencia con ejemplos reales para el apartado 8–9 del documento final.

## ✅ Cómo verificar

```bash
cd backend
mvn -q -DskipTests compile
mvn -q -Dtest=BaseJdbcDaoTest test
cd ..
```

Resultado esperado: compilación y tests verdes. Un test con mocks prueba lógica de cierre, pero no prueba la conexión real; adjunta el resultado de integración separado. Si Docker/Testcontainers no está disponible, reporta esa validación como pendiente.

**Autovalidación propuesta:** `scripts/validate-b4-jdbc-base.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
for file in RowMapper BaseJdbcDao; do
  test -f "backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/${file}.java"
done
for file in MetricsPort ReminderPort ReservationCodePort; do
  test -f "backend/src/main/java/com/sv/grupo7/medisuite/service/port/${file}.java"
done
(cd backend && mvn -q -DskipTests compile && mvn -q -Dtest=BaseJdbcDaoTest test)
printf 'OK B4 JDBC base\n'
```

Ejecuta `bash scripts/validate-b4-jdbc-base.sh`. Mantén el script pequeño y comprobable; no reemplaza la prueba de integración.

## 📤 Commit, push y PR — comandos para que tú ejecutes

Revisa el diff y el staging antes de registrar el commit; el diagrama final se agrega cuando ya represente el código integrado.

```bash
git status --short
git diff --check
git add backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/RowMapper.java backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDao.java backend/src/main/java/com/sv/grupo7/medisuite/service/port/MetricsPort.java backend/src/main/java/com/sv/grupo7/medisuite/service/port/ReminderPort.java backend/src/main/java/com/sv/grupo7/medisuite/service/port/ReservationCodePort.java backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDaoTest.java scripts/validate-b4-jdbc-base.sh
git diff --cached --check
git diff --cached
git commit -m "feat(jdbc): agrega base generica y puertos de persistencia"
git push -u origin b4-abstract-dao-interfaces
```

Abre PR con base `feature/avance3-fase-b`; pide review a @hlopez y @aavigil. Para diagramas, usa otro commit pequeño cuando estén comprobados. No incluyas `.env`, token, IP privada ni atribución de IA.

### Checklist para el PR

```markdown
## B4 — Carlos Mario Ventura Velásquez (@mdealerdude)
- [ ] B2 y B5 estaban integrados antes de compilar mi PR.
- [ ] `BaseJdbcDao` ejecuta `executeQuery` y `executeUpdate` reales.
- [ ] PreparedStatement parametriza todos los valores externos.
- [ ] Las conexiones y ResultSet se cierran en éxito/error.
- [ ] Los tres puertos usan las firmas cerradas en §A (sin modificaciones).
- [ ] UML/documento muestran polimorfismo real de puerto a implementación y prueba.
- [ ] `mvn -Dtest=BaseJdbcDaoTest test` pasa; integración PostgreSQL documentada.
- [ ] UML final actualizado y revisado con @hlopez.
- [ ] No hay secretos, tokens o IP privadas staged.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| `No qualifying bean` | Verifica B2 integrado y `@Qualifier("jdbcDataSource")`. |
| Constructor de excepción no coincide | Confirma firma del B5 mergeado; no inventes otro tipo. |
| Consulta devuelve vacío inesperado | Comprueba `tenant_id`, fechas UTC y parámetros en orden. |
| Test Mockito no prueba cierre | Verifica llamadas `close()` o usa try-with-resources observable. |
| UML difiere del código | Regenera después de mergear implementaciones; señala clases futuras como propuestas, no actuales. |

## 📚 Cinco preguntas de defensa

1. **¿Por qué una clase abstracta?** Agrupa la mecánica JDBC común y deja que cada DAO concreto defina su consulta y mapeo. Reduce duplicación sin mezclar reglas clínicas con SQL.
2. **¿Para qué sirve `RowMapper<T>`?** Convierte una fila del `ResultSet` en un tipo concreto. El DAO controla el recorrido y el cierre del recurso.
3. **¿Qué diferencia hay entre JPA y JDBC directo?** JPA mapea entidades y gestiona gran parte del SQL. Aquí se ejecutan explícitamente `PreparedStatement`, `ResultSet` y actualizaciones requeridas por el Avance 3.
4. **¿Cómo evitas inyección SQL?** Los datos entran como parámetros del `PreparedStatement`. No concateno valores del usuario en el texto SQL.
5. **¿Qué prueba el bajo acoplamiento?** Los servicios dependen de `MetricsPort`/otros puertos; los DAO implementan esos contratos. Un test puede reemplazar el DAO sin cambiar el servicio.

Lee [Vigil](Alejandro_Vigil_Ramirez.md), [Vásquez](Walter_Vasquez_Amaya.md), [Héctor](Hector_Lopez_Ruiz.md), [Bayron](Bayron_Orellana_Rojas.md) y [Merino](Alejandro_Merino_Ventura.md) para cerrar firmas y dependencias.
