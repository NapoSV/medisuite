# B1 — Contrato del dashboard JDBC para integración

Responsable backend: Héctor López. Consumidor frontend: William Melgar. Este contrato describe el código local de B1; **no implica que B2/B4/B5/B6 ya estén integrados**.

`GET /api/dashboard/metrics` exige JWT. El servidor obtiene `userId` y rol de la autenticación y el `tenantId` de `TenantContext`; vuelve a comprobar usuario activo, tenant y rol en la BD. Para `DOCTOR`, resuelve `doctorId` mediante su usuario y comprueba que sea del mismo tenant. `tenantId` o `doctorId` enviados por URL se ignoran. Un usuario sin permiso recibe `403` con `{"error":"forbidden","message":"Acceso denegado"}`. Un fallo JDBC llega al manejador global como error interno sin exponer SQL; B5 debe confirmar su respuesta final.

## JSON y permisos

| Campo | ADMIN | DOCTOR | NURSE | RECEPTIONIST | Significado |
|---|---:|---:|---:|---:|---|
| `appointmentsToday` | sí | solo propias | sí | sí | Todas las citas cuya hora cae en el día local; conserva la semántica anterior, incluso canceladas. |
| `activePatients` | sí | no | sí | sí | **Pacientes registrados**, porque `patients` no tiene bandera `active`. William debe cambiar la etiqueta visible; no afirmar que son activos. |
| `prescriptionsIssued` | sí | solo propias | no | no | Alias temporal del conteo de recetas de esta semana; antes era un total global inseguro. |
| `alerts` | sí | solo pacientes atendidos | sí | no | Alias temporal de `criticalAlerts`; ya no cuenta `audit_logs`. |
| `prescriptionsThisWeek` | sí | solo propias | no | no | `issued_on` desde lunes inclusivo a lunes siguiente exclusivo. |
| `criticalAlerts` | sí | solo pacientes atendidos | sí | no | Pacientes cuyo **último** signo vital registrado tiene prioridad persistida `CRITICAL`. |
| `waitingRoom` | sí | solo citas propias | sí | sí | Máximo 20 citas del día en estado `WAITING` o `IN_WAITING`; solo ID, nombre, hora y estado. |
| `occupancyByHour` | sí | solo citas propias | no | sí | Citas no canceladas/no-show por hora local; no representa capacidad ni porcentaje de ocupación. |
| `clinicalAlerts` | no | solo pacientes atendidos | sí | no | Máximo 20 alertas actuales: ID de signo vital, ID de paciente, prioridad y fecha. Sin síntomas ni notas clínicas. |

Los campos no permitidos se omiten del JSON (`null` en el DTO); las listas autorizadas sin datos son `[]`. `waitingRoom` contiene `appointmentId`, `patientName`, `scheduledAt`, `status`; `occupancyByHour` contiene `hour` (0–23) y `appointments`; `clinicalAlerts` contiene `vitalSignId`, `patientId`, `priority`, `recordedAt`.

Zona de los límites diarios: `America/El_Salvador`, con intervalo `[inicio, fin)` convertido a `timestamptz`. La semana local inicia el lunes. `issued_on` es `DATE` en la BD. El esquema exportado contiene `CRITICAL`; Zair/Flores deben comprobar el constraint efectivo porque la UI de triaje usa `EMERGENCIA`. Hasta resolver esa discrepancia, el conteo de alertas podría omitir prioridades que se hayan guardado bajo otro valor.

El dashboard A2 ejecutaba cuatro `CompletableFuture.supplyAsync` sobre el pool común y contaba recetas/auditoría globalmente. B1 usa lecturas JDBC secuenciales con una conexión cerrada al terminar; evita depender de `TenantContext` en workers y mantiene el paralelismo del proyecto como una capacidad separada de los módulos de B3, no como una propiedad de este endpoint.

## Coordinación pendiente

- **Vigil/B2:** comprobar que su bean `jdbcDataSource` es seleccionado por `DashboardJdbcDao` y que Hikari sigue siendo `@Primary` para JPA/Flyway. El DAO usa ese bean si existe y, antes del merge, recurre al `DataSource` principal; no afirmar que ya usa el pool B2.
- **Ventura/B4:** acordar la firma de su futuro `service.port.MetricsPort` y sustituir el adaptador temporal `dao.jdbc.DashboardMetricsReader` sin alterar los filtros del DAO. No se ha creado su archivo. La implementación actual cierra conexión, sentencias y resultados.
- **Vásquez/B5:** confirmar el mapeo estable y seguro de `DataAccessResourceFailureException`; el controlador B1 ya devuelve `403` sin detalles internos.
- **William/B6:** migrar `DashboardPage` a los campos con nombres correctos, cambiar «Pacientes activos» por «Pacientes registrados», mostrar «Recetas de esta semana», y respetar campos ausentes por rol. Los cuatro nombres anteriores existen solo como compatibilidad.
- **Nicole:** incluir en el documento final la evolución A1→A2→A3: identidad/tenant, dashboard A2 y sustitución de agregados globales por JDBC filtrado; no atribuir a B1 concurrencia nueva.

No se deben usar datos clínicos reales ni JWT en capturas o PR. `DashboardJdbcDaoTest` pasó localmente con PostgreSQL descartable y Testcontainers 1.21.4. El `backend/pom.xml` administra 1.19.8, versión que en este equipo no pudo detectar Docker Engine 29; el script B1 eleva **solo para su ejecución** la versión de prueba sin modificar el POM que otros trabajan. Aún se necesita QA manual con dos tenants y al menos dos médicos en el mismo tenant antes del merge.
