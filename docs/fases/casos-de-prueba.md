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

### Validación del frontend — 13/10/2026

Commit de correcciones: 49c78ba.

- pnpm lint: PASS, sin errores ni advertencias.
- TypeScript (tsc --noEmit -p tsconfig.app.json): PASS.
- pnpm build: PASS.
- git diff --check: PASS.
- Se corrigieron los 15 errores y las 3 advertencias iniciales de lint.
- Se eliminaron las citas y los horarios ficticios usados como respaldo
  cuando fallaban las consultas en la pantalla Citas.

Pendiente repetir los flujos modificados en el navegador.
La impresión de recetas sigue pendiente de integrar los datos del paciente.
V12 y V13 no estaban presentes en la última revisión de esta rama.
B8 continúa en progreso.

### Validación integral — 13/10/2026

- Rama validada en commit 9004b98.
- Maven ejecutado con Java 21.0.12.1.
- bash scripts/validate-all.sh: PASS.
- Backend: 84 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Build y lint del frontend: PASS.
- Salida final: OK compilacion, pruebas unitarias y frontend.

El intento previo con Java 26 falló durante la compilación del backend.
Al seleccionar Java 21, el script terminó correctamente.

Esta ejecución no certifica V12 ni completa las pruebas manuales pendientes.
### Validación manual: enfermería no puede crear recetas — 15/10/2026

- Entorno: QA local, contenedor medisuite-b8-qa.
- Usuario: Enfermería A QA, rol NURSE.
- Paciente A3: patient_id=4, medical_record_id=1.
- Interfaz: muestra Registrar signos vitales y oculta Nueva receta.
- Prueba directa: POST /api/prescriptions con sesión de enfermería y datos sintéticos.
- Respuesta: HTTP 422, "El usuario autenticado no es un doctor".
- Base de datos: 0 recetas del expediente antes y 0 después.
- Resultado: el intento fue rechazado sin crear una receta.
- Observación: el rechazo usa una regla de negocio (422); no demuestra una restricción explícita por rol con HTTP 403.

### B8-M06: rechazo directo de doble reserva — 15/10/2026

- Entorno: QA local. Rol: ADMIN.
- Reserva inicial creada desde el formulario: ID 2, COD-6088, PENDING.
- Paciente: 4. Doctor: 2.
- Horario: 19/10/2026 a las 08:00 de El Salvador (14:00 UTC).
- Se envió un segundo POST /api/appointments con el mismo doctor y horario.
- Respuesta: HTTP 422, "El doctor ya tiene una cita en ese horario".
- SQL posterior confirmó una sola fila: ID 2, COD-6088.
- Resultado: PASS para el rechazo secuencial de doble reserva.
- Alcance: no se probaron solicitudes concurrentes.

 ### B8-M07: revalidación de reprogramación — 15/10/2026

- Entorno: QA local. Rol: ADMIN.
- Desde Citas se reprogramó la cita ID 2, COD-6088.
- Fecha anterior: 19/10/2026 a las 08:00 de El Salvador.
- Fecha nueva: 20/10/2026 a las 08:00 de El Salvador.
- Interfaz: mostró la nueva fecha, estado SCHEDULED y el mismo código.
- SQL confirmó scheduled_at=2026-10-20 14:00:00+00,
  status=SCHEDULED y reservation_code=COD-6088.
- Resultado: PASS.

### B8-M08: revalidación de cancelación — 15/10/2026

- Entorno: QA local. Rol: ADMIN.
- Cita: ID 2, COD-6088.
- Se canceló desde la interfaz con el motivo:
  "Cancelación sintética para revalidación QA B8".
- Interfaz y SQL confirmaron estado CANCELLED.
- SQL confirmó cancel_reason con el texto ingresado,
  cancelled_at=2026-10-16 04:28:45.427983+00
  y conservación del código COD-6088.
- Resultado: PASS para cancelación con sesión recién iniciada.
- Incidencia: el intento anterior devolvió HTTP 403.
  Tras volver a iniciar sesión funcionó; causa pendiente de confirmar.

  ### Receta e impresión — 15/10/2026

- Entorno: QA local. Rol: DOCTOR, Doctor A1 QA.
- Paciente A3: ID 4. Expediente: ID 1.
- Se emitió la receta ID 1 desde el formulario.
- SQL confirmó tenant_id=1, medical_record_id=1, doctor_id=2
  e issued_on=2026-10-15.
- Se guardaron diagnóstico e indicaciones, además del medicamento
  sintético, dosis, frecuencia y duración de 3 días.
- Creación y persistencia: PASS.
- El historial muestra la receta, el médico, medicamento y dosis.
- Hallazgo: historial y vista de impresión muestran 14/10/2026,
  aunque issued_on contiene 15/10/2026.
- No se observó acceso a impresión desde la tarjeta del historial.
- Se abrió directamente /receta-print/1.
- Paciente y DUI aparecen vacíos; duración e indicaciones no se muestran.
- El botón Imprimir abre una vista previa de una página y no sale impreso.
- Vista de impresión: FAIL por datos del paciente ausentes y fecha incorrecta.
- No se imprimió en papel ni se guardó un PDF.

### Aislamiento: clínica B consulta recursos de A — 15/10/2026

- Entorno: QA local, datos sintéticos.
- Sesión temporal: doctor.b@medisuite.test, tenant_id=2.
- GET /api/users/me: HTTP 200; confirmó usuario de clínica B.
- GET /api/patients/4/medical-record: HTTP 422,
  "Paciente no existe", sin devolver el expediente de A.
- GET /api/prescriptions/1: HTTP 500,
  "Error interno del servidor", sin devolver datos de la receta de A.
- PrescriptionService.findById utiliza findByIdAndTenantId.
  Si no encuentra la receta, lanza RuntimeException genérica;
  posible causa del 500, pendiente de confirmar con logs.
- No se observaron datos clínicos de A en estas dos respuestas.
- Manejo de error al consultar receta de otro tenant: FAIL.
- Alcance: dos consultas de lectura B→A; no certifica todo el aislamiento.
### Dashboard por rol: revisión visual — 15/10/2026

- Entorno: QA local. Tenant: clinica-san-rafael.
- DOCTOR: 0 citas hoy, 0 alertas y 1 receta.
  Acciones visibles: Mis citas y Pacientes.
- RECEPTIONIST: 0 citas hoy y 3 pacientes.
  Acciones: Agendar cita, Nuevo paciente y Ver agenda.
  Sin tarjetas de recetas ni alertas.
- ADMIN: 0 citas hoy, 3 pacientes, 0 alertas y 1 receta.
  Acciones: Gestionar pacientes, Gestionar doctores y Nueva cita.
- NURSE: 0 citas hoy, 3 pacientes y 0 alertas.
  Acciones: Pacientes y Citas del día. Sin tarjeta de recetas.
- SQL confirmó 3 pacientes, una receta del doctor 2 del 15/10,
  dos citas para fechas posteriores y un triaje LOW.
- Resultado: PASS para visualización y valores observados con estos datos.
- El DAO cuenta recetas de la semana; la tarjeta no indica ese período.
- Pendientes: confirmar Actualizar, navegación de acciones,
  alerta positiva y separación de métricas entre médicos y tenants.
- Esta revisión no certifica permisos de API por ocultar tarjetas.

### Dashboard: comparación entre médicos — 16/10/2026

- Entorno: QA local, datos sintéticos.
- Se consultó GET /api/dashboard/metrics con tres cuentas.
- Doctor A1, clínica A: HTTP 200; prescriptionsIssued=1,
  prescriptionsThisWeek=1 y appointmentsToday=1.
- Doctor A2, clínica A: HTTP 200; ambos contadores de recetas=0
  y appointmentsToday=0.
- Doctor B, clínica B: HTTP 200; ambos contadores de recetas=0
  y appointmentsToday=0.
- Resultado: PASS para separación de recetas entre las cuentas probadas.
- Alcance: no certifica todo el aislamiento entre tenants.
- Observación: appointmentsToday incluye la cita cancelada de A1.

### Validación tras integrar V12 y V13 — 16/10/2026

- Validación integral tras el merge: 115 pruebas, 0 fallos,
  0 errores y 0 omitidas; build y lint del frontend aprobados.
- Se agregó un caso a ReminderMigrationIT.
- Ejecución con Java 21 y PostgreSQL 16 descartable:
  3 pruebas, 0 fallos, 0 errores y 0 omitidas; BUILD SUCCESS.
- Preparación: schema.sql con baseline V9; avance hasta V11
  y posterior aplicación de V12 y V13.
- V12: conservó COD-6088 y R-00000042; siguiente valor
  de secuencia=43; máximo=99999999 y sin ciclo.
- Se rechazó un código duplicado del mismo tenant con SQLState 23505.
- Reejecutar Flyway no aplicó migraciones nuevas.
- V13: confirmó la ruta de omisión sin rol clinica_app.
  No se validaron sus permisos efectivos ni la política por owner.
- No se probó la cadena V1–V13 desde una base vacía.
- No se modificó la base compartida.

### Estado consolidado de B8 — 16/10/2026

Este resumen actualiza los pendientes históricos de las secciones anteriores.

- Lint del frontend: resuelto; build y validación integral aprobados.
- Suite general tras integrar fase-b: 115 pruebas aprobadas.
- ReminderMigrationIT ampliado: 3 pruebas aprobadas.
- V12: validada localmente con baseline V9 y actualización V11→V13.
- V13: verificada únicamente su omisión sin clinica_app.
- Manuales ejecutadas: login válido, alta y búsqueda de pacientes,
  citas, doble reserva secuencial, reprogramación, cancelación,
  triaje, consulta de expediente, restricciones de enfermería,
  creación e impresión de recetas y dashboard por rol.
- Seguridad: dos consultas B→A sin exposición observada de datos;
  comparación de recetas entre médicos A1, A2 y B aprobada.
  Esta cobertura no certifica todo el aislamiento multi-tenant.

Hallazgos abiertos:
- Alta con DUI duplicado devuelve HTTP 500.
- Triaje guarda LOW en lugar del contrato NORMAL.
- Impresión de receta con paciente ausente y fecha incorrecta.
- Consulta de receta de otro tenant devuelve HTTP 500.
- Cancelación con HTTP 403 previo: causa sin confirmar.
- Dashboard incluye citas canceladas en appointmentsToday;
  comportamiento pendiente de contrastar con el contrato.

Sin ejecución manual confirmada:
- Bloqueo de login, flujo de recordatorios, fallo de base de datos
  y arranque completo mediante Docker.
- Dashboard: Actualizar, navegación de acciones y alerta positiva.
- Permisos efectivos de V13 con los roles presentes.

El export database/schema.sql está documentado como obsoleto.
La aprobación de B8 y el tratamiento de los pendientes requieren revisión.

### Bloqueo de login por cuenta — 16/10/2026

- Entorno: QA local. Tenant: clinica-san-rafael.
- Cuenta sintética: doctor.a2@medisuite.test, usuario ID 3.
- Estado inicial: failed_login_attempts=0 y locked_until=NULL.
- Se enviaron cinco contraseñas incorrectas, con pausas de 15 segundos.
- Las cinco respuestas fueron HTTP 401, "Credenciales inválidas",
  sin token. No se recibieron respuestas HTTP 429.
- SQL posterior: failed_login_attempts=0 y locked_until=NULL.
- Un intento posterior con contraseña correcta respondió HTTP 200
  y entregó token, sin imprimirlo ni guardarlo en la evidencia.
- Esperado según AuthService: bloqueo de 15 minutos tras cinco fallos.
- Resultado: FAIL para el bloqueo por cuenta.
- Hipótesis: la transacción de login revierte el incremento al lanzar
  BadCredentialsException; pendiente confirmar con una prueba específica.
- Esta ejecución no valida el límite de solicitudes por IP.

### Dashboard: actualización y navegación ADMIN — 16/10/2026

- Entorno: QA local, tenant clinica-san-rafael, rol ADMIN.
- Al pulsar Actualizar aumentaron las solicitudes GET
  /api/dashboard/metrics en Network.
- La solicitud inspeccionada respondió HTTP 200.
- Las tarjetas terminaron mostrando sus valores:
  1 cita hoy, 3 pacientes, 0 alertas y 1 receta.
- Gestionar pacientes abrió Pacientes.
- Gestionar doctores abrió Doctores.
- Nueva cita abrió el formulario /citas/nueva.
- Resultado: PASS para actualización y navegación ADMIN.
- No se verificaron aquí las acciones de los demás roles
  ni una alerta positiva.

### Recordatorios: generación y reprogramación — 16/10/2026

- Entorno: QA local, base con baseline V9 y migraciones V10/V11.
- Rol ADMIN, tenant_id=1, datos sintéticos.
- Cola inicialmente vacía.
- Se creó cita ID 3, COD-5809, para 17/10/2026 08:00 de El Salvador.
- El worker generó recordatorio ID 1, PENDING,
  scheduled_for=2026-10-16 14:00:00+00: 24 horas antes de la cita.
- Cancelar la cita fue rechazado por la regla de 24 horas de anticipación.
- Se reprogramó al 19/10/2026 08:00, conservando COD-5809.
- SQL confirmó cita SCHEDULED y recordatorio anterior CANCELLED,
  processed_at=2026-10-17 00:57:07.263099+00.
- Resultado: PASS para generación y cancelación del recordatorio
  obsoleto por reprogramación.
- No se certifican envío real, cancelación directa de la cita,
  idempotencia entre ciclos ni permisos de clinica_app.

### Arranque con Docker en QA aislado — 17/10/2026

- Se construyeron las imágenes backend y frontend sin errores.
- Se utilizó un Compose temporal de QA con PostgreSQL 16,
  backend Java 21 y frontend Nginx.
- La BD se inicializó con una copia de medisuite_qa.
- PostgreSQL y backend alcanzaron estado healthy;
  GET /actuator/health respondió {"status":"UP"}.
- Frontend disponible en http://localhost:15173.
- Login con ADMIN de clinica-san-rafael: correcto.
- Dashboard cargó y Pacientes mostró los tres pacientes sintéticos.
- Flyway confirmó baseline V9 y V10–V13 con success=true.
- Resultado: PASS para este arranque con configuración aislada.
- No certifica el Compose original con su .env ni instalación desde cero.
- No se comprobaron permisos efectivos de V13 con clinica_app.

### Dashboard: caída y recuperación de BD — 17/10/2026

- Entorno: Compose aislado medisuite-b8-docker.
- Sesión: ADMIN de clinica-san-rafael.
- Se detuvo únicamente el servicio db de la copia de QA.
- Con la BD detenida, Actualizar mostró:
  "No se pudieron cargar las métricas — Error 500".
- La interfaz conservó los valores anteriores y ofreció Reintentar.
- No se observaron detalles SQL ni credenciales en el mensaje visible.
- Se arrancó nuevamente db y se pulsó Reintentar.
- El error desapareció y se mostraron las métricas:
  0 citas hoy, 3 pacientes, 0 alertas y 1 receta.
- No fue necesario reiniciar el backend.
- Resultado: PASS para aviso visual de fallo y recuperación.
- Alcance: no se inspeccionó el cuerpo HTTP completo del error.
- La base original medisuite-b8-qa permaneció encendida.
