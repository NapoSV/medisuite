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