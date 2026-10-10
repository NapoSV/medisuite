# B1 — Protocolo de QA manual del dashboard

Responsable: Héctor López. Ejecutar **después** de integrar B2/B4/B5 en la rama integradora y antes de retirar el estado de borrador del [PR #41](https://github.com/NapoSV/medisuite/pull/41). Este protocolo no afirma que la QA ya se haya realizado.

## Preparación segura

- Usar una instancia de QA y PostgreSQL aislado con **datos sintéticos**. No crear, modificar ni borrar datos en Neon compartido para esta prueba.
- Preparar dos clínicas distintas (T1 y T2). En T1, preparar dos médicos distintos (D1 y D2), además de cuentas `ADMIN`, `NURSE` y `RECEPTIONIST`; en T2, al menos un `ADMIN` y un médico. Registrar citas, recetas y signos vitales sintéticos con conteos intencionalmente diferentes entre clínicas y entre D1/D2.
- Verificar que la prioridad guardada que significa emergencia coincide con el constraint efectivo (`CRITICAL` en el esquema exportado) y con el contrato de triaje de Zair/Flores. Si no coincide, registrar bloqueo: un contador `0` no prueba que no haya alertas.
- No colocar JWT, contraseñas, nombres de pacientes, capturas clínicas ni respuestas JSON completas en el PR. Registrar únicamente resultado, rol, clínica, conteos no identificables y evidencia de prueba anonimizada.

## Matriz de ejecución

En cada fila, iniciar sesión con la cuenta indicada y consultar `GET /api/dashboard/metrics`. Repetir con `?tenantId=<id-de-la-otra-clinica>&doctorId=<id-de-otro-medico>`: esos parámetros no deben cambiar la respuesta ni ampliar el alcance.

| Sesión | Comprobación obligatoria | Campos que deben omitirse |
|---|---|---|
| ADMIN T1 | Conteos solo de T1; `waitingRoom` y `occupancyByHour` limitados a T1. | `clinicalAlerts`. |
| DOCTOR D1 de T1 | Citas/recetas solo de D1; alertas solo de pacientes vinculados a sus citas. No incluir datos de D2 ni T2. | `activePatients`. |
| DOCTOR D2 de T1 | Resultado distinto de D1 según los datos preparados; nunca datos de T2. | `activePatients`. |
| NURSE T1 | Triaje y pacientes de T1; lista `clinicalAlerts` sin síntomas ni notas. | `prescriptionsIssued`, `prescriptionsThisWeek`, `occupancyByHour`. |
| RECEPTIONIST T1 | Citas y pacientes de T1; no datos clínicos ni recetas. | `alerts`, `criticalAlerts`, `clinicalAlerts`, `prescriptionsIssued`, `prescriptionsThisWeek`. |
| ADMIN T2 | Conteos de T2, distintos de T1 por el fixture; no nombres ni IDs de T1. | `clinicalAlerts`. |

Casos adicionales:

1. Sin JWT o con uno inválido, el endpoint no debe devolver métricas. Con rol del token distinto al rol vigente del usuario, debe responder `403` sin SQL, stacktrace ni datos clínicos.
2. Para una clínica sin citas en espera ni distribución horaria, las listas autorizadas deben serializarse como `[]`; los campos no autorizados deben estar **ausentes**, no como listas vacías.
3. Preparar citas a ambos lados de medianoche en `America/El_Salvador` y recetas a ambos lados del domingo/lunes; verificar intervalos `[inicio, fin)` y semana local de lunes a lunes.
4. Confirmar que `appointmentsToday` incluye canceladas por compatibilidad con A2; si el producto decide excluirlas, exigir cambio de contrato y prueba antes de actualizar el texto visible.
5. Comprobar que el DataSource usado por `DashboardJdbcDao` es `jdbcDataSource` de B2 y que JPA/Flyway siguen en Hikari. Verificar el comportamiento de B5 al fallar una conexión **solo en la instancia aislada**, nunca apagando una base compartida.

## Evidencia mínima para cerrar B1

| Caso | Resultado esperado | Resultado observado | Evidencia anonimizada / incidencia |
|---|---|---|---|
| ADMIN T1 frente a ADMIN T2 | Conteos separados | pendiente | pendiente |
| D1 frente a D2 en T1 | Cada médico ve lo suyo | pendiente | pendiente |
| Parámetros de URL manipulados | No alteran alcance | pendiente | pendiente |
| NURSE y RECEPTIONIST | Campos permitidos exactos | pendiente | pendiente |
| Vacíos, fechas y zona | Contrato estable | pendiente | pendiente |
| Error de conexión aislado | Respuesta segura B5 | pendiente | pendiente |

Completar esta tabla con resultados reales y enlazarla desde el PR. Un test automatizado que pasa no reemplaza las sesiones manuales ni demuestra por sí solo que el frontend de William usa el contrato final.
