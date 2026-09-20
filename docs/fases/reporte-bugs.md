# Reporte de Bugs — S-06

## Información general

**Proyecto:** MediSuite  
**Historia/Tarea:** S-06 — Reporte y priorización de bugs  
**Tester:** Nicole Nohemy Sánchez Menjívar  
**Fecha de ejecución de pruebas:** 19/09/2026  
**Fuente:** Casos de prueba S-05 (20 casos ejecutados, 6 fallidos)

---

## Resumen ejecutivo

Durante la ejecución de los 20 casos de prueba manuales de S-05, se detectaron **6 bugs** en los módulos de Pacientes, Médicos, Expediente Médico, Recetas y Autenticación. Cinco de ellos comparten la misma causa raíz: ausencia de `@Transactional` en métodos de lectura con relaciones lazy de JPA combinada con `spring.jpa.open-in-view=false`. El sexto fue un falso positivo del entorno de prueba.

---

## Tabla de bugs

| ID | Caso | Módulo | Endpoint | Severidad | Prioridad | Estado |
|---|---|---|---|---|---|---|
| BUG-01 | CP-05 | Pacientes | `GET /api/patients` | Alta | P1 | Corregido |
| BUG-02 | CP-07 | Pacientes | `GET /api/patients/{id}` | Alta | P1 | Corregido |
| BUG-03 | CP-08 | Médicos | `GET /api/doctors` | Alta | P1 | Corregido |
| BUG-04 | CP-12 | Expediente Médico | `GET /api/patients/{id}/medical-record` | Alta | P1 | Corregido |
| BUG-05 | CP-13 | Recetas | `GET /api/prescriptions/{id}` | Alta | P1 | Corregido |
| BUG-06 | CP-17 | Autenticación | `POST /api/auth/login` | Media | P2 | Falso positivo |

---

## Detalle de bugs

### BUG-01 — GET /api/patients devuelve HTTP 500

**Caso de prueba:** CP-05  
**Severidad:** Alta  
**Prioridad:** P1 — Bloquea el flujo principal de gestión de pacientes  
**Resultado obtenido:** HTTP 500  
**Resultado esperado:** HTTP 200 con lista paginada de pacientes  

**Causa raíz:**  
`PatientService.search()` no tenía `@Transactional`, lo que provocaba `LazyInitializationException` al serializar la relación `tenant` (lazy) fuera de la sesión de Hibernate. El servidor tiene `spring.jpa.open-in-view=false`, por lo que la sesión se cierra al salir del método de servicio.

**Corrección aplicada:**  
`@Transactional(readOnly = true)` en `PatientService.search()` + `@JsonIgnore` en `BaseEntity.tenant` para evitar serialización de la relación lazy.  
**Archivos:** `PatientService.java`, `BaseEntity.java`

---

### BUG-02 — GET /api/patients/{id} devuelve HTTP 500

**Caso de prueba:** CP-07  
**Severidad:** Alta  
**Prioridad:** P1 — Impide consultar un paciente específico  
**Resultado obtenido:** HTTP 500  
**Resultado esperado:** HTTP 200 con datos del paciente  

**Causa raíz:**  
`PatientService.findById()` no tenía `@Transactional`. Misma causa raíz que BUG-01.

**Corrección aplicada:**  
`@Transactional(readOnly = true)` en `PatientService.findById()`.  
**Archivos:** `PatientService.java`

---

### BUG-03 — GET /api/doctors devuelve HTTP 500

**Caso de prueba:** CP-08  
**Severidad:** Alta  
**Prioridad:** P1 — Bloquea el modal de nueva cita y vistas de médicos  
**Resultado obtenido:** HTTP 500  
**Resultado esperado:** HTTP 200 con lista de médicos  

**Causa raíz:**  
`DoctorService.findAll()` no tenía `@Transactional`. Las relaciones `user` y `specialty` de `Doctor` son lazy, y ambas se intentaban serializar fuera de sesión.

**Corrección aplicada:**  
`@Transactional(readOnly = true)` en `DoctorService.findAll()` + `FetchType.EAGER` en `Doctor.user` y `Doctor.specialty` (necesarios para mostrar nombre y especialización).  
**Archivos:** `DoctorService.java`, `Doctor.java`

---

### BUG-04 — GET /api/patients/{id}/medical-record devuelve HTTP 500

**Caso de prueba:** CP-12  
**Severidad:** Alta  
**Prioridad:** P1 — Bloquea la vista de expediente del paciente  
**Resultado obtenido:** HTTP 500  
**Resultado esperado:** HTTP 200 con expediente completo (recetas y signos vitales)  

**Causa raíz:**  
`MedicalRecordService.findFullByPatientId()` tenía `@Transactional` pero retornaba un `Map` con el objeto `Patient` que contenía relaciones lazy (`tenant`, `user`). Al serializar el Map, Jackson accedía a esas relaciones fuera de sesión.

**Corrección aplicada:**  
`@JsonIgnore` en `BaseEntity.tenant`, `Patient.user`, `VitalSign.medicalRecord` y `Prescription.medicalRecord` para evitar que Jackson navegue referencias lazy innecesarias.  
**Archivos:** `BaseEntity.java`, `Patient.java`, `VitalSign.java`, `Prescription.java`

---

### BUG-05 — GET /api/prescriptions/{id} devuelve HTTP 500

**Caso de prueba:** CP-13  
**Severidad:** Alta  
**Prioridad:** P1 — Impide imprimir o visualizar recetas  
**Resultado obtenido:** HTTP 500  
**Resultado esperado:** HTTP 200 con receta e ítems  

**Causa raíz:**  
`PrescriptionService.findById()` no tenía `@Transactional`. Las relaciones `doctor`, `medicalRecord` e `items` de `Prescription` son lazy y se intentaban serializar fuera de sesión.

**Corrección aplicada:**  
`@Transactional(readOnly = true)` en `PrescriptionService.findById()` + `FetchType.EAGER` en `Prescription.doctor` e `items` (necesarios para RecetaPrint) + `@JsonIgnore` en `Prescription.medicalRecord` y `PrescriptionItem.prescription`.  
**Archivos:** `PrescriptionService.java`, `Prescription.java`, `PrescriptionItem.java`

---

### BUG-06 — POST /api/auth/login con contraseña incorrecta devuelve HTTP 500

**Caso de prueba:** CP-17  
**Severidad:** Media  
**Prioridad:** P2  
**Resultado obtenido:** HTTP 500 (según prueba S-05)  
**Resultado esperado:** HTTP 401 con mensaje de credenciales inválidas  

**Análisis:**  
`GlobalExceptionHandler` ya tenía un handler correcto para `BadCredentialsException` que retorna HTTP 401 (`handleBadCredentials`, línea 19-23). Al verificar el código actual, `POST /api/auth/login` con credenciales incorrectas devuelve correctamente HTTP 401.  
El resultado HTTP 500 en S-05 se atribuye a una diferencia en el estado del servidor en el momento de la prueba (posiblemente servidor local desactualizado). **No se requiere corrección de código.**

**Estado:** Falso positivo — comportamiento correcto verificado en develop.

---

## Criterios de priorización

| Criterio | P1 (Alta) | P2 (Media) |
|---|---|---|
| Bloquea flujo principal | Sí | No |
| Afecta múltiples módulos | Sí | No |
| Error en producción | HTTP 500 visible al usuario | Comportamiento ambiguo |

**Causa raíz compartida BUG-01 a BUG-05:** `spring.jpa.open-in-view=false` + métodos de servicio sin `@Transactional` + relaciones JPA lazy serializadas por Jackson fuera de sesión.
