# Casos de Prueba Manuales — S-05

## Información general

**Proyecto:** MediSuite  
**Historia/Tarea:** S-05 — Pruebas manuales  
**Fecha de ejecución:** 19/09/2026  
**Cantidad de casos:** 20

## Resultados generales

| Resultado | Cantidad |
|---|---:|
| PASS | 14 |
| FAIL | 6 |
| Total | 20 |

---

## Casos de prueba

| ID | Módulo | Caso de prueba | Pasos / Datos de prueba | Resultado esperado | Resultado real | Estado |
|---|---|---|---|---|---|---|
| CP-01 | Autenticación | Inicio de sesión correcto | Ingresar tenant, correo y contraseña válidos | El sistema autentica al usuario y devuelve un token | Se obtuvo el token correctamente | PASS |
| CP-02 | Autenticación | Inicio de sesión sin tenant | Enviar correo y contraseña válidos sin `tenantSlug` | El sistema rechaza la solicitud por datos requeridos | Respondió HTTP 400 | PASS |
| CP-03 | Usuarios | Consultar usuario autenticado | Realizar `GET /api/users/me` con token válido | Mostrar información del usuario autenticado | Se obtuvo la información de Beatriz Reyes correctamente | PASS |
| CP-04 | Seguridad | Consultar usuario sin autenticación | Realizar `GET /api/users/me` sin token | El acceso debe ser rechazado | Respondió HTTP 403 | PASS |
| CP-05 | Pacientes | Listar pacientes | Realizar `GET /api/patients` con token válido | Mostrar la lista de pacientes | Respondió HTTP 500 | FAIL |
| CP-06 | Pacientes | Buscar pacientes | Realizar búsqueda mediante `search=Beatriz` | Mostrar resultados de búsqueda sin error | Respondió correctamente con una lista vacía | PASS |
| CP-07 | Pacientes | Consultar paciente por ID | Realizar `GET /api/patients/1` con token válido | Mostrar los datos del paciente | Respondió HTTP 500 | FAIL |
| CP-08 | Médicos | Listar médicos | Realizar `GET /api/doctors` con token válido | Mostrar la lista de médicos | Respondió HTTP 500 | FAIL |
| CP-09 | Dashboard | Consultar métricas | Realizar `GET /api/dashboard/metrics` con token válido | Mostrar las métricas del dashboard | Se obtuvieron las métricas correctamente | PASS |
| CP-10 | Citas | Listar citas | Realizar `GET /api/appointments` con token válido | Mostrar las citas registradas | Respondió correctamente con una lista vacía | PASS |
| CP-11 | Citas | Consultar horarios disponibles | Consultar `/api/appointments/doctors/1/slots` para una fecha válida | Mostrar horarios disponibles | Se obtuvieron horarios de 08:00 a 16:00 | PASS |
| CP-12 | Expediente médico | Consultar expediente de paciente | Realizar `GET /api/patients/1/medical-record` con token válido | Mostrar el expediente médico | Respondió HTTP 500 | FAIL |
| CP-13 | Recetas | Consultar receta | Realizar `GET /api/prescriptions/1` con token válido | Mostrar la receta solicitada | Respondió HTTP 500 | FAIL |
| CP-14 | Autenticación | Cerrar sesión | Realizar logout con token válido | La sesión debe cerrarse correctamente | Se recibió respuesta de cierre de sesión | PASS |
| CP-15 | Seguridad | Acceder después de cerrar sesión | Reutilizar el token después del logout | El token debe ser rechazado | Respondió HTTP 403 | PASS |
| CP-16 | Autenticación | Cambio de contraseña inválida | Enviar una contraseña con menos de 10 caracteres | El sistema debe rechazar la contraseña | Respondió HTTP 400 por validación | PASS |
| CP-17 | Autenticación | Inicio de sesión con contraseña incorrecta | Intentar iniciar sesión con contraseña incorrecta | Mostrar un error de autenticación controlado | Respondió HTTP 500 | FAIL |
| CP-18 | Autenticación | Inicio de sesión con correo inválido | Enviar un correo con formato inválido | El sistema debe rechazar los datos | Respondió HTTP 400 | PASS |
| CP-19 | Autenticación | Inicio de sesión con contraseña vacía | Enviar contraseña vacía | El sistema debe rechazar la solicitud | Respondió HTTP 400 | PASS |
| CP-20 | Seguridad | Consultar dashboard sin autenticación | Realizar `GET /api/dashboard/metrics` sin token | El acceso debe ser rechazado | Respondió HTTP 403 | PASS |

---

## Resumen de casos fallidos

Los casos que presentaron fallos durante la ejecución fueron:

- **CP-05:** Listado de pacientes — HTTP 500.
- **CP-07:** Consulta de paciente por ID — HTTP 500.
- **CP-08:** Listado de médicos — HTTP 500.
- **CP-12:** Consulta de expediente médico — HTTP 500.
- **CP-13:** Consulta de receta — HTTP 500.
- **CP-17:** Inicio de sesión con contraseña incorrecta — HTTP 500.

Los primeros cinco errores estuvieron relacionados con errores de inicialización diferida (`LazyInitializationException`) al serializar información relacionada con `tenant`.

El caso CP-17 presentó un error HTTP 500 durante la prueba de credenciales incorrectas.

---

## Conclusión

Se ejecutaron 20 casos de prueba manuales sobre los módulos de autenticación, seguridad, usuarios, pacientes, médicos, citas, expediente médico, recetas y dashboard.

De los 20 casos ejecutados, 14 finalizaron correctamente y 6 presentaron errores que requieren revisión. Los resultados obtenidos sirven como base para el reporte de bugs correspondiente a la siguiente tarea de QA.

---

## Avance 3 — B8: registro de validación

### Entorno y versión

- Responsable: Walter Flores.
- Fecha: 11/10/2026.
- Rama: `b-flores-tests-y-migration-helper`.
- Commit base: `2fa126e`, con cambios locales de B8 sin commit.
- Entorno: macOS ARM64, Java 21, Node 22.23.3 y pnpm 11.18.0.

### Validaciones automatizadas

Estos resultados no sustituyen las pruebas manuales ni la revisión de migraciones.

| Validación | Resultado observado | Estado |
|---|---|---|
| AppointmentServiceTest, PatientServiceTest y MedicalRecordServiceTest | 22 pruebas, 0 fallos, 0 errores y 0 omitidas | PASS |
| Script validate-b8-regression.sh | Finalizó con `OK B8 regresion` | PASS |
| Pruebas completas del backend | 73 pruebas, 0 fallos, 0 errores y 0 omitidas; BUILD SUCCESS | PASS |
| Construcción del frontend dentro del script integral | El script avanzó hasta la ejecución de lint | PASS |
| Lint del frontend | 15 errores y 3 advertencias; código de salida 1 | FAIL |
| Script validate-all.sh | Se detuvo en lint del frontend; no emitió el mensaje final de éxito | FAIL |

### Bloqueo de validación integral

El frontend no presenta modificaciones locales según `git status --short`.
El reporte de ESLint identifica problemas en:

- `src/auth/useAuth.tsx`
- `src/pages/CitaNueva.tsx`
- `src/pages/Citas.tsx`
- `src/pages/DashboardPage.tsx`
- `src/pages/Doctores.tsx`
- `src/pages/Expediente.tsx`
- `src/pages/LoginPage.tsx`
- `src/pages/Pacientes.tsx`
- `src/pages/Perfil.tsx`
- `src/pages/RecetaPrint.tsx`
- `src/pages/Triaje.tsx`

Incluye reglas sobre estado dentro de efectos, dependencias de hooks,
tipos `any`, exportaciones para Fast Refresh, uso previo a declaración
y asignaciones innecesarias.

Pendiente: coordinar la corrección con el responsable del frontend y
reejecutar `bash scripts/validate-all.sh`. No se desactivaron reglas
ni se omitió lint para obtener un resultado aprobado.

### Pruebas manuales A1/A2/A3 pendientes de reejecución

Los resultados históricos del 19/09/2026 se conservan sin cambios.
Todavía no se han reejecutado ni aprobado los siguientes casos para
esta versión del Avance 3:

- Login y bloqueo.
- Alta y búsqueda de pacientes.
- Citas y doble reserva.
- Cancelación con motivo.
- Triaje.
- Expediente médico.
- Receta e impresión.
- Dashboard por rol.
- Separación de tenants.
- Recordatorios y códigos.
- Manejo de error de base de datos.
- Arranque con Docker.

Cada ejecución deberá registrar fecha, rol, tenant, datos sintéticos,
pasos, resultado esperado, resultado observado, evidencia y estado.

### Migraciones y contrato de priority

- V11: PASS en PostgreSQL 16 descartable mediante `ReminderMigrationIT`.
  Se ejecutaron 2 pruebas, sin fallos, errores ni omitidas:
  inicialización con `schema.sql` y baseline V9 hasta V11, y actualización
  por etapas desde V10 hasta V11. Se verificaron conservación de la cita,
  índice, rechazo de duplicados con SQLState 23505 y nombre del constraint,
  claves foráneas con SQLState 23503, estados permitidos y borrado en cascada.
  También se verificó que reejecutar Flyway no aplica nuevas migraciones.
  No se probó una cadena V1–V11 partiendo de una base completamente vacía.
- V12: pendiente; el archivo no está disponible en esta copia de la rama.
- Contrato de `priority`: resuelto documentalmente el 09/10/2026.
  La escala vigente es `NORMAL/URGENTE/EMERGENCIA`, según
  `docs/avances/fase-b/SEGUIMIENTO_PR_FASE_B.md`.
  Ese documento indica que las migraciones V1–V10 no imponen un CHECK
  sobre esta columna y que `database/schema.sql` es un export obsoleto
  que contiene `LOW/MEDIUM/HIGH/CRITICAL`.
  Se propone regenerar el export desde el esquema vigente, revisando
  sus consumidores, o eliminarlo si deja de ser necesario.
  Pendiente: verificar el constraint efectivo en QA y la alineación
  del frontend con Zair. No se modificó el export ni la BD compartida.
- No se ejecutó DDL sobre la base de datos compartida.

### Actualización de regresión — 12/10/2026

- Commit probado: `2f544b6`.
- Se incorporó la integradora `origin/feature/avance3-fase-b`
  hasta `77db605`.
- `AppointmentServiceTest`: 16 pruebas, 0 fallos, 0 errores
  y 0 omitidas.
- Las tres clases de servicios: 27 pruebas, 0 fallos, 0 errores
  y 0 omitidas.
- Se añadieron cinco casos: creación con paciente de otro tenant,
  completar y reprogramar una cita de otro tenant, reprogramación
  exitosa y rechazo de cancelación con menos de 24 horas.
- Son pruebas unitarias con mocks; no certifican la regresión funcional
  ni el aislamiento contra una base de datos real.
- Los resultados de backend completo, lint y migraciones del 11/10
  corresponden a la ejecución anterior; no se han revalidado después
  de esta actualización.
  ### Revalidación integral — 12/10/2026

- Commit ejecutado: `f4abc63`.
- Backend completo: 84 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Build del frontend: aprobado; el script avanzó hasta lint.
- Lint: 15 errores y 3 advertencias en 11 archivos del frontend.
- `scripts/validate-all.sh`: FAIL; se detuvo en lint con código 1.
- Persisten los problemas registrados en la ejecución del 11/10.
- Pendiente: coordinar su corrección con los responsables del frontend
  y repetir la validación integral.
- No se modificaron reglas de ESLint ni se omitió lint.