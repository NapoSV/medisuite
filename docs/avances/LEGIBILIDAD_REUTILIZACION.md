# Legibilidad y reutilización en MediSuite

**Responsable:** Nicole Nohemy Sánchez Menjívar
**Tarea:** B7 — Avance 3, Fase B
**Rama:** `b7-nicole-docs-y-tests`
**Fecha:** 12 de octubre de 2026

## 1. Objetivo

Este documento describe ejemplos verificables relacionados con legibilidad y reutilización en el código integrado de MediSuite. Se distingue entre el código actual que se revisó y el estado anterior descrito en la guía B7. Cuando no se dispone del fragmento anterior o de un resultado de prueba ejecutado, se deja constancia en lugar de inventar evidencia.

## 2. Ejemplo 1: contrato explícito para las métricas del dashboard

### Archivos revisados

- `backend/src/main/java/com/sv/grupo7/medisuite/service/port/MetricsPort.java`
- `backend/src/main/java/com/sv/grupo7/medisuite/service/DashboardMetricsService.java`
- `backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/DashboardJdbcDao.java`

### Código actual

`MetricsPort` expresa la operación requerida con una firma breve:

```java
public interface MetricsPort {
    DashboardResponse getMetrics(DashboardQuery query);
}
```

`DashboardMetricsService` prepara el `DashboardQuery` después de validar el usuario, el rol y el contexto de clínica, y delega la consulta:

```java
return metrics.getMetrics(query);
```

`DashboardJdbcDao` implementa la interfaz y concentra las consultas JDBC que producen la respuesta del dashboard.

### Cómo ayuda a la legibilidad

El nombre `MetricsPort` comunica que se trata de un contrato para acceder a métricas. La firma `getMetrics(DashboardQuery query)` deja claro qué dato se solicita y qué resultado se devuelve, sin exponer SQL en la interfaz. Al leer el servicio, la llamada `metrics.getMetrics(query)` permite identificar la intención sin recorrer las consultas SQL.

La mejora principal que puede afirmarse con el código actual es la separación explícita del contrato y de la implementación. No se presenta aquí un fragmento “antes” literal porque el estado anterior completo no se ha cotejado en este documento; la guía B7 describe ese estado como un servicio que dependía directamente de varios repositorios JPA para calcular métricas.

### Pruebas relacionadas

- `backend/src/test/java/com/sv/grupo7/medisuite/service/DashboardMetricsServiceTest.java`
- `backend/src/test/java/com/sv/grupo7/medisuite/controller/api/DashboardControllerTest.java`

Estas clases existen en la rama. Antes de afirmar que protegen el comportamiento tras el cambio, se debe ejecutar la prueba y registrar el resultado real.

## 3. Ejemplo 2: reutilización de operaciones JDBC con `BaseJdbcDao`

### Archivo revisado

`backend/src/main/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDao.java`

### Código actual

La clase abstracta proporciona operaciones compartidas con nombres que expresan su propósito:

```java
protected <T> List<T> query(String sql, RowMapper<T> mapper, Object... params)
protected <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params)
protected int update(String sql, Object... params)
protected long countBy(String sql, Object... params)
```

El método `bind` enlaza los parámetros mediante `PreparedStatement.setObject`. Los recursos JDBC se administran con `try-with-resources`, y los errores `SQLException` se convierten en `DataAccessException` con un código de error.

### Cómo ayuda a la legibilidad y reutilización

Los nombres `query`, `queryOne`, `update` y `countBy` describen la operación que se realiza. Los DAO que extiendan esta clase pueden reutilizar el manejo común de conexiones, parámetros, resultados y errores en vez de volver a implementar esa mecánica en cada operación. Esto deja el código específico de cada DAO más centrado en su consulta y su propósito.

La clase base debe mantenerse limitada a operaciones JDBC comunes. Si absorbiera reglas de negocio o consultas particulares de un DAO, se volvería más difícil entenderla y podría aumentar el acoplamiento.

### Antes y después conceptual

La guía B7 identifica como riesgo la posible duplicación del acceso JDBC entre varios DAO. Como no se ha cotejado aquí el contenido exacto de cada archivo antes del cambio, el diagrama anterior es conceptual y no una transcripción del código histórico.

**Antes — riesgo de repetición:**

```text
DAO A: obtener conexión, enlazar parámetros, ejecutar y cerrar recursos
DAO B: obtener conexión, enlazar parámetros, ejecutar y cerrar recursos
```

**Después — código integrado:**

```text
DAO concreto
  └── reutiliza BaseJdbcDao
        ├── query / queryOne
        ├── update / countBy
        ├── bind de parámetros
        ├── cierre de recursos
        └── traducción de errores JDBC
```

### Pruebas relacionadas

- `backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDaoTest.java`
- `backend/src/test/java/com/sv/grupo7/medisuite/dao/jdbc/BaseJdbcDaoIT.java`

Las clases de prueba existen en el repositorio integrado. Se debe ejecutar `BaseJdbcDaoTest` y `BaseJdbcDaoIT` para registrar los resultados reales antes de dar por cerrada esta evidencia.

## 4. Reglas de legibilidad aplicadas en los ejemplos

1. **Nombres con intención:** `getMetrics`, `queryOne`, `countBy` y `MetricsPort` expresan el propósito de las operaciones.
2. **Contratos pequeños:** `MetricsPort` expone la operación necesaria sin filtrar detalles JDBC.
3. **Responsabilidades separadas:** el servicio valida y prepara la consulta; el DAO ejecuta SQL.
4. **Reutilización sin mezclar dominios:** `BaseJdbcDao` contiene mecánica JDBC, no reglas clínicas.
5. **Evidencia verificable:** las afirmaciones deben asociarse a archivos y pruebas existentes, y los resultados de ejecución se registran solo después de ejecutar las pruebas.

## 5. Conclusión

El código actual aporta legibilidad al separar el contrato de métricas de su implementación y al usar nombres explícitos para las operaciones JDBC compartidas. `BaseJdbcDao` favorece la reutilización de mecánica técnica común, mientras que los DAO concretos mantienen las consultas de su responsabilidad.

Estas conclusiones se limitan al código revisado. Para cerrar la evidencia B7 se deben ejecutar las pruebas indicadas y, si se exige un antes/después literal, cotejar los commits que introdujeron `MetricsPort` y `BaseJdbcDao`.

## 6. Pendientes de evidencia

- [ ] Ejecutar `DashboardMetricsServiceTest` y registrar el resultado real.
- [ ] Ejecutar `BaseJdbcDaoTest` y registrar el resultado real.
- [ ] Ejecutar `BaseJdbcDaoIT` con Docker/Testcontainers y registrar el resultado real.
- [ ] Añadir referencias a los PR/commits integrados de B1 y B4 al informe final.
- [ ] Revisar que los ejemplos históricos coincidan con el diff real antes de presentarlos como fragmentos literales “antes”.
