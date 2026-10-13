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

### Revalidación de V11 — 12/10/2026

- Se ejecutó `mvn -Dtest=ReminderMigrationIT test` con Docker activo.
- Resultado: 2 pruebas, 0 fallos, 0 errores y 0 omitidas; BUILD SUCCESS.
- Se mantienen el alcance y las limitaciones documentados para V11.
- V12 continúa ausente en la rama local; su validación queda pendiente.

### Pruebas manuales en QA local — 12/10/2026

- Responsable: Walter Flores.
- Tenant: clinica-san-rafael.
- Entorno: PostgreSQL 16 descartable en Docker, backend Java 21
  y frontend local en http://127.0.0.1:5173.
- Datos exclusivamente sintéticos.
- Estos resultados actualizan parcialmente los pendientes anteriores.

| ID | Rol | Datos y pasos | Esperado | Observado | Estado |
|---|---|---|---|---|---|
| B8-M01 | ADMIN | Iniciar sesión con admin.a@medisuite.test | Acceder al sistema | Login y dashboard visibles | PASS |
| B8-M02 | ADMIN | Buscar A1 por nombre y DUI con y sin guion | Encontrar al paciente | Las tres búsquedas funcionaron | PASS |
| B8-M03 | ADMIN | Crear A3 Sintetico QA, DUI 00000004-4, nacimiento 1990-01-01 | Guardar paciente | Creado con ID 4 | PASS |
| B8-M04 | ADMIN | Repetir alta con DUI 00000004-4 | Rechazar con mensaje claro, sin duplicar | HTTP 500 genérico; SQL confirmó un solo paciente | FAIL |
| B8-M05 | ADMIN | Crear cita de A3 con Doctor A1 QA, 15/10/2026 a las 09:00 | Persistir cita y código | SQL confirmó ID 1, PENDING, COD-9829 | PASS |
| B8-M06 | ADMIN | Consultar horarios del mismo doctor y fecha | Excluir horario ocupado | Las 09:00 no se ofrecieron; rechazo directo por API pendiente | PASS parcial |
| B8-M07 | ADMIN | Reprogramar cita ID 1 al 16/10/2026 a las 09:00 | Actualizar fecha y conservar código | Interfaz mostró SCHEDULED y COD-9829 | PASS |
| B8-M08 | ADMIN | Cancelar ID 1 con motivo "No ire porque no quiero ir" | Persistir cancelación y motivo | Interfaz y SQL confirmaron CANCELLED, motivo y cancelled_at | PASS |
| B8-M09 | NURSE | Registrar triaje de A3 y abrir expediente: 36.7 °C, pulso 72, presión 120/80, peso 70 kg, talla 165 cm | Conservar signos vitales | Datos visibles en ambas pantallas | PASS |
| B8-M10 | NURSE | Seleccionar Normal y consultar expediente y SQL | Guardar NORMAL según contrato | Se guardó LOW y se mostró LOW | FAIL |

#### Evidencias

Capturas compartidas en la sesión; pendiente copiarlas al repositorio.

- B8-M03: image(20261013-022554).png.
- B8-M05: image(20261013-024056).png.
- B8-M07: image(20261013-025310).png.
- B8-M08: image(20261013-025614).png y image(20261013-025754).png.
- B8-M09: image(20261013-030541).png y image(20261013-030945).png.
- B8-M10: image(20261013-031200).png y búsqueda de priority en el código.

#### Hallazgo de prioridad

Triaje.tsx envía LOW/MEDIUM/HIGH/CRITICAL.
Expediente.tsx utiliza NORMAL/URGENTE/EMERGENCIA.
La opción "Normal" de Triaje envía LOW, confirmado en PostgreSQL.

Pendiente coordinar la corrección con Zair y el responsable del backend,
definir qué ocurre con "Urgente alto" y repetir las pruebas.

#### Preparación y límites

La BD local se inicializó con database/schema.sql.
Solo en esta BD descartable se eliminó vital_signs_priority_check,
que contenía la escala antigua. No se ejecutó DDL en la BD compartida
ni se verificó su constraint efectivo.

Se modificó frontend/src/lib/tenant.ts para evitar enviar tenantSlug "127"
al acceder mediante 127.0.0.1. Pendiente revisar y validar antes de publicar.

### Continuación de permisos — 12/10/2026

- Enfermería abrió el expediente de A3 y pudo ver el triaje.
- La interfaz muestra Registrar signos vitales y oculta Nueva receta.
- Expediente.tsx permite crear recetas únicamente al rol DOCTOR.
- PrescriptionService comprueba que el usuario tenga un registro de doctor
  y que pertenezca al tenant actual.
- Pendiente ejecutar POST /api/prescriptions con Enfermería,
  registrar el estado HTTP y comprobar que no se guarda una receta.
- Paciente A3: ID 4. Expediente: ID 1.
- La solicitud de prueba fue preparada, pero su ejecución no está confirmada.

Siguen pendientes los demás casos manuales, V12 y los bloqueos de lint.