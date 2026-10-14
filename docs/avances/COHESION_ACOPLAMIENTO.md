# Cohesión y acoplamiento en MediSuite

**Responsable:** Nicole Nohemy Sánchez Menjívar
**Tarea:** B7 — Avance 3, Fase B
**Rama:** `b7-nicole-docs-y-tests`
**Fecha:** 12 de octubre de 2026

## 1. Objetivo

Este documento analiza dos decisiones de diseño presentes en el código integrado: la inversión de dependencias mediante `MetricsPort` y la reutilización de operaciones JDBC mediante `BaseJdbcDao`. Se distingue entre código revisado y resultados de pruebas que todavía deben registrarse mediante ejecución real.

## 2. Ejemplo 1: inversión de dependencias en el dashboard

### Responsabilidades actuales

- `DashboardMetricsService` valida autenticación, usuario, rol y clínica; calcula el intervalo temporal y construye un `DashboardQuery`.
- `MetricsPort` define el contrato `getMetrics(DashboardQuery query)`.
- `DashboardJdbcDao` implementa `MetricsPort` y ejecuta consultas SQL para construir `DashboardResponse`.

Archivos:
- `backend/src/main/java/com/sv/grupo7/medisuite/service/DashboardMetricsService.java`
- `backend/src/main/java/com/sv/grupo7/medisuite/service/port/MetricsPort.java`
- `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java`

### Antes y después

La guía individual de B7 describe el estado anterior como un servicio de métricas que dependía directamente de varios repositorios JPA y calculaba las métricas. El código anterior completo no se ha verificado aquí; el diagrama representa la descripción de la guía, no una comparación línea por línea.

**Antes — esquema conceptual descrito en la guía:**

```text
DashboardMetricsService
  ├── repositorios JPA de métricas
  └── cálculo/obtención de métricas
```

**Después — código integrado revisado:**

```text
DashboardMetricsService
  ├── UserRepository       (validación de usuario)
  ├── DoctorRepository     (validación del médico)
  └── MetricsPort
        ▲
        │ implementa
  DashboardJdbcDao
        └── consultas JDBC
```

`DashboardMetricsService` conserva dependencias con `UserRepository` y `DoctorRepository` para autorización y validación del médico. Por tanto, no está totalmente desacoplado de todos los repositorios. La mejora específica es que la obtención de métricas se delega a una abstracción y el servicio no necesita conocer la implementación JDBC concreta.

### Evidencia en el código

El contrato es pequeño:

```java
public interface MetricsPort {
    DashboardResponse getMetrics(DashboardQuery query);
}
```

La implementación declara `public class DashboardJdbcDao implements MetricsPort` y sobrescribe `getMetrics(DashboardQuery q)`. En `DashboardMetricsService`, la obtención de métricas se delega mediante `return metrics.getMetrics(query);`.

La interfaz separa la necesidad del servicio —obtener métricas para una consulta— de los detalles de acceso a datos. Facilita sustituir la implementación o utilizar un mock de `MetricsPort` en pruebas.

### Cohesión y acoplamiento

La responsabilidad de autorización y construcción de la consulta permanece en `DashboardMetricsService`; ejecutar SQL y ensamblar los datos del dashboard corresponde a `DashboardJdbcDao`. `MetricsPort` mantiene un contrato orientado a una tarea.

El cambio reduce el acoplamiento directo entre el servicio y la implementación de las métricas, pero no elimina dependencias necesarias para comprobar usuario y médico.

### Pruebas relacionadas

- `backend/src/test/java/com/sv/grupo7/medisuite/service/DashboardMetricsServiceTest.java`
- `backend/src/test/java/com/sv/grupo7/medisuite/controller/api/DashboardControllerTest.java`

Estas pruebas existen en la rama integrada. Sus resultados deben registrarse tras ejecutarlas; no se afirma aquí que hayan pasado.

## 3. Ejemplo 2: reutilización JDBC con `BaseJdbcDao`

Archivo: `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDao.java`

`BaseJdbcDao` centraliza operaciones JDBC comunes:

- `query`: consulta y mapea varias filas.
- `queryOne`: consulta que puede devolver una fila o ninguna.
- `update`: ejecuta una actualización.
- `countBy`: obtiene un conteo.
- `bind`: enlaza parámetros mediante `PreparedStatement.setObject`.
- `try-with-resources`: cierra conexiones, sentencias y resultados.
- Traducción de `SQLException` a `DataAccessException` con un `JdbcErrorCode`.

La clase abstracta recibe un `DataSource`; sus métodos son `protected`, para que los DAO concretos reutilicen las operaciones comunes y conserven sus propias consultas.

### Antes y después

La guía B7 identifica la posible duplicación de acceso JDBC entre DAO como el problema que debe evaluarse. Para una comparación histórica exacta, se deben cotejar los commits anteriores a la incorporación de `BaseJdbcDao`; este documento no atribuye líneas concretas al estado anterior que no se hayan comprobado.

**Antes — riesgo de repetición descrito en la guía:**

```text
DAO A: conexión + parámetros + ejecución + cierre de recursos
DAO B: conexión + parámetros + ejecución + cierre de recursos
DAO C: conexión + parámetros + ejecución + cierre de recursos
```

**Después — estructura integrada:**

```text
DAO concreto
  ├── SQL y reglas específicas de su consulta
  └── operaciones comunes heredadas de BaseJdbcDao
        ├── conexión y PreparedStatement
        ├── enlace de parámetros
        ├── mapeo de resultados
        ├── cierre de recursos
        └── traducción de errores SQL
```

El esquema anterior expresa el riesgo de duplicación señalado por la guía, no una afirmación de que todos los DAO anteriores tuvieran exactamente el mismo código repetido.

### Cohesión, reutilización y límites

La clase base agrupa tareas técnicas que suelen repetirse al acceder a una base de datos. Así, los DAO concretos pueden centrarse en las consultas y la transformación de resultados de su responsabilidad.

La abstracción también tiene un límite: si `BaseJdbcDao` absorbiera reglas específicas del dominio o consultas particulares de un DAO, podría aumentar el acoplamiento. Debe mantenerse enfocada en operaciones JDBC compartidas.

### Pruebas relacionadas

- `backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDaoTest.java`
- `backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDaoIT.java`

La primera contiene pruebas unitarias y la segunda es una prueba de integración con PostgreSQL mediante Testcontainers, según la estructura de la rama. Ejecutarlas y registrar el resultado real es necesario antes de marcar esta evidencia como aprobada.

## 4. Comparación de decisiones

| Decisión | Responsabilidad | Beneficio esperado | Límite |
|---|---|---|---|
| `MetricsPort` | Definir el contrato de métricas | El servicio no conoce la clase JDBC concreta | El servicio conserva dependencias de autorización |
| `DashboardJdbcDao` | Ejecutar consultas del dashboard | Concentra el acceso JDBC de las métricas | Debe conservar filtros por clínica y rol |
| `BaseJdbcDao` | Reutilizar operaciones JDBC comunes | Reduce repetición de recursos, parámetros y errores | No debe absorber reglas específicas del dominio |

## 5. Conclusión

El código integrado muestra dos mecanismos diferentes. `MetricsPort` aplica inversión de dependencias al separar el contrato de métricas de su implementación JDBC. `BaseJdbcDao` promueve reutilización al centralizar el manejo común de conexiones, sentencias, parámetros, resultados y errores.

La existencia de interfaces o clases base, por sí sola, no demuestra que todo el sistema tenga bajo acoplamiento. La conclusión se limita a las responsabilidades y dependencias observadas en las clases descritas.

## 6. Pendientes de evidencia

- [ ] Ejecutar `DashboardMetricsServiceTest` y registrar el resultado real.
- [ ] Ejecutar `BaseJdbcDaoTest` y registrar el resultado real.
- [ ] Ejecutar `BaseJdbcDaoIT` con Docker/Testcontainers y registrar el resultado real.
- [ ] Si se exige una comparación histórica línea por línea, revisar los commits anteriores a `MetricsPort` y `BaseJdbcDao`.
- [ ] Vincular los PR integrados de B1/B4 en la versión final.
