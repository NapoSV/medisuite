# AVANCE 1 — DOCUMENTO DE ENTREGA
## MediSuite · Plataforma de Gestión de Citas Médicas Multi-Clínica
### Universidad Evangélica de El Salvador — Materia: Programación II
### Versión 1.0 · 31/07/2026

> **USO INTERNO DEL EQUIPO.**
> Este archivo es el borrador para copiar al Word compartido en Teams.
> El Word oficial está en: https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH
>
> **Pendientes antes de imprimir/entregar:**
> - [ ] Insertar fotos individuales en la portada (cada integrante sube la suya).
> - [ ] Insertar captura del Gantt (abrir `gantt_proyecto_completo.md` en GitHub y tomar screenshot).
> - [ ] Insertar capturas de pantalla del sistema (LoginPage funcionando, BD en Neon, etc.).
> - [ ] Verificar CIF de Merino (aparece como `—` en la tabla; él debe confirmarlo).
> - [ ] Agregar conclusiones individuales (sección 13 — una por integrante).
> - [ ] Completar bibliografía APA (sección 14).

---

## 1. PORTADA

| Fotografía | Nombre completo | CIF | ¿Participó? |
|:---:|---|:---:|:---:|
| [FOTO — insertar imagen cuadrada 300×300] | LOPEZ RUIZ HECTOR NAPOLEON | 79360441 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | VIGIL RAMIREZ ALEJANDRO ANTONIO | 60111191 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | ORELLANA ROJAS BAYRON ALEXANDER | 75699490 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | DIAZ SANTOS ZAIR BENETT | 74528330 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | FLORES HERNANDEZ WALTER ALEJANDRO | 75497362 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | MELGAR RIVAS WILLIAM ARIEL | 63167135 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | MERINO VENTURA ALEJANDRO SEBASTIAN | — | SI |
| [FOTO — insertar imagen cuadrada 300×300] | FUENTES ORTIZ ERIKA ALEXANDRA | 76590699 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | VASQUEZ AMAYA WALTER AMILCAR | 74869704 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | VENTURA VELASQUEZ CARLOS MARIO | 60127297 | SI |
| [FOTO — insertar imagen cuadrada 300×300] | SANCHEZ MENJIVAR NICOLE NOHEMY | 74243942 | SI |

---

## 2. OBJETIVO GENERAL

Desarrollar una plataforma de gestión de citas médicas multi-empresa (SaaS) que permita optimizar la administración de agendas, el registro de pacientes y el historial clínico en una clínica u hospital, mejorando la eficiencia y calidad de la atención sanitaria mediante control de acceso por roles, digitalización del triaje y trazabilidad del expediente clínico.

---

## 3. OBJETIVOS ESPECÍFICOS

**OE1 (Avance 1):** Diseñar e implementar el módulo de registro y autenticación de usuarios (pacientes, médicos, enfermeras, administradores, recepcionistas) con control de roles y permisos basado en JWT, sobre una arquitectura multi-tenant compatible con la evolución del proyecto a SaaS comercial.

> *Nota: En el Avance 2 se agregará OE2 y en la entrega final OE3, acumulando 3 objetivos específicos en total, tal como indica el formato del curso.*

---

## 4. DISTRIBUCIÓN DEL EQUIPO (SCRUM)

| N° | Nombre completo | Rol Scrum | Rol Técnico |
|----|-----------------|-----------|-------------|
| 1 | LOPEZ RUIZ HECTOR NAPOLEON | PRODUCT OWNER | Analista de Negocio / Project Manager |
| 2 | ORELLANA ROJAS BAYRON ALEXANDER | SCRUM MASTER | Desarrollador Fullstack |
| 3 | VIGIL RAMIREZ ALEJANDRO ANTONIO | DEVELOPER | Desarrollador Backend |
| 4 | DIAZ SANTOS ZAIR BENETT | DEVELOPER | Desarrollador Frontend |
| 5 | FLORES HERNANDEZ WALTER ALEJANDRO | DEVELOPER | Desarrollador Backend |
| 6 | MELGAR RIVAS WILLIAM ARIEL | DEVELOPER | Desarrollador Frontend |
| 7 | MERINO VENTURA ALEJANDRO SEBASTIAN | DEVELOPER | Base de Datos |
| 8 | FUENTES ORTIZ ERIKA ALEXANDRA | QA | Analista de Pruebas |
| 9 | VASQUEZ AMAYA WALTER AMILCAR | QA | Analista de Pruebas |
| 10 | VENTURA VELASQUEZ CARLOS MARIO | ARCHITECT | Arquitecto de Software |
| 11 | SANCHEZ MENJIVAR NICOLE NOHEMY | BUSINESS ANALYST | Analista de Negocio |

**Scrum Master:** Bayron Alexander Orellana Rojas
**Product Owner:** Héctor Napoleón López Ruiz

---

## 5. DEFINICIÓN DE ROLES Y FUNCIONES POR ROL DEL SISTEMA

Los siguientes son los roles de **usuario dentro del sistema MediSuite** (no los roles Scrum del equipo):

| Rol | Descripción | Funciones en el sistema |
|-----|-------------|-------------------------|
| **Médico** | Profesional de la salud encargado del diagnóstico, tratamiento y seguimiento clínico del paciente. | Ver agenda de citas asignadas · Acceder al expediente clínico · Registrar diagnóstico, recetas y evolución · Modificar o cancelar citas con justificación · Generar reportes de atención |
| **Paciente** | Persona que recibe atención médica. Es una entidad de datos del sistema — **no tiene acceso directo a la plataforma**. Su información es gestionada por la enfermera o el recepcionista. | (Sin acceso al sistema — entidad de datos gestionada por personal clínico) |
| **Enfermera** | Profesional que realiza triaje, toma signos vitales y apoya directamente al médico durante la atención. | Registrar signos vitales (peso, talla, presión, temperatura, frecuencia cardíaca) · Completar datos de pacientes en espera · Clasificar prioridad de atención (triaje) · Consultar agenda y estado de citas |
| **Administrador** | Encargado de la gestión general del sistema, los usuarios y la configuración de la clínica. | Crear, modificar y eliminar usuarios (médicos, enfermeras, etc.) · Configurar horarios y disponibilidad de consultorios · Generar reportes administrativos · Gestionar auditoría de accesos y cambios |
| **Recepcionista** | Personal de recepción que gestiona la entrada y salida de pacientes y el agendamiento de citas. | Registrar pacientes nuevos · Asignar citas según disponibilidad médica · Confirmar asistencia de pacientes · Buscar pacientes por nombre o CIF |

---

## 6. REQUERIMIENTOS DEL SISTEMA (HISTORIAS DE USUARIO)

Las siguientes Historias de Usuario (HU) siguen el formato Scrum:
**"Como [rol], quiero [funcionalidad], para [beneficio]."**

| Prioridad | HU | Historia de Usuario | Criterios de Aceptación |
|-----------|-----|---------------------|--------------------------|
| Alta | **HU-001** | Como **enfermera / recepcionista**, quiero un formulario para registrar pacientes y consultar su expediente, para atenderlos rápidamente. | El paciente queda registrado con nombre, CIF, fecha de nacimiento y contacto · Se puede buscar por nombre o CIF · El expediente muestra historial de citas, diagnósticos y signos vitales |
| Alta | **HU-002** | Como **médico**, quiero registrar una receta electrónica, para llevar el control del tratamiento del paciente. | Receta almacenada en el expediente · Asociada a paciente y médico · Muestra fecha y hora · Incluye medicamentos, dosis y duración del tratamiento |
| Alta | **HU-003** | Como **recepcionista**, quiero agendar una cita para un paciente eligiendo médico y horario disponible, para gestionar la agenda de la clínica sin doble reserva. | Búsqueda de paciente por nombre o CIF · Selección de médico, especialidad, fecha y hora disponible · El sistema impide doble reserva en el mismo bloque horario · Genera código de reserva único (ej. COD-0001) · Registro del motivo de consulta |
| Media | **HU-004** | Como **enfermera**, quiero realizar el triaje de pacientes en espera y registrar sus signos vitales, para que el médico los reciba preparado. | Búsqueda por CIF o nombre · Registro de peso, talla, presión, temperatura, FC y síntomas · Asignación de prioridad (bajo / medio / alto / crítico) · Se guarda en el expediente clínico |
| Media | **HU-005** | Como **administrador**, quiero gestionar los horarios de los médicos y la disponibilidad de consultorios, para optimizar la ocupación de la clínica. | Crear, modificar y eliminar bloques horarios · Asignar consultorios a médicos · Visualizar ocupación en tiempo real |
| Alta | **HU-007** | Como **usuario del sistema**, quiero iniciar sesión de forma segura con mi rol y el código de mi clínica, para acceder únicamente a las funciones que me corresponden. | Login con correo electrónico, contraseña y código de clínica · JWT emitido al autenticar · Redirección según rol (médico → agenda, admin → dashboard, etc.) · Bloqueo tras 5 intentos fallidos en 15 minutos |
| Alta | **HU-008** | Como **administrador de clínica**, quiero crear usuarios del sistema (médicos, enfermeras, recepcionistas, otros administradores) con su rol asignado, para controlar quién accede al sistema. | Alta con nombre, correo, rol (DOCTOR / NURSE / RECEPTIONIST / ADMIN) y clínica · Contraseña temporal asignada · Usuario obligado a cambiar contraseña en el primer inicio de sesión |

> **HU obligatoria del Avance 1:** HU-007 — cubre el OE1 "Módulo de autenticación y roles". Esta HU está **implementada y funcionando** (ver sección 12).

---

## 7. ALCANCES Y LIMITACIONES DEL SISTEMA

### 7.1. Qué SÍ hace el sistema (alcance)

- Autenticación segura multi-tenant con JWT y control de roles por clínica.
- Registro y búsqueda de pacientes con expediente clínico básico.
- Gestión de citas médicas (crear, modificar, cancelar) con código de reserva y anti-doble-reserva.
- Triaje digital: registro de signos vitales con clasificación de prioridad.
- Expediente clínico: historial de consultas, diagnósticos, recetas y alergias.
- Inventario básico de productos, órdenes de compra y activos físicos.
- Dashboard con KPIs para el administrador de la clínica.
- Aislamiento de datos por clínica (arquitectura multi-tenant).
- Interfaz web responsive (desktop, tablet y móvil).

### 7.2. Qué NO hace el sistema (limitaciones / fuera de alcance)

- **No hay facturación ni pasarela de pago** de servicios médicos.
- **No integra sistemas externos** de laboratorio, farmacia ni aseguradoras.
- **No incluye telemedicina** (videollamadas o consultas remotas).
- **No emite firma electrónica certificada** para recetas.
- **No hay aplicación móvil nativa** (solo web responsive).
- **El paciente no puede modificar su expediente** — solo lectura.
- **No hay módulo financiero ni contabilidad** más allá de conteos operativos.
- Los datos de la base de datos son ficticios (ningún dato real de pacientes — norma académica y regulación de salud).

---

## 8. PLANIFICACIÓN (DISTRIBUCIÓN DE ACTIVIDADES POR AVANCE)

| N° | Actividad | Responsable(s) | Fecha inicio | Fecha fin | Avance |
|----|-----------|---------------|:---:|:---:|:---:|
| 1 | Portada individual (foto, nombre, CIF) | Todos (11) | 15/07 | 20/07 | 1 |
| 2 | Objetivo general | Héctor López | 15/07 | 16/07 | 1 |
| 3 | Objetivo específico OE1 | Héctor López | 15/07 | 16/07 | 1 |
| 4 | Distribución del equipo Scrum | Héctor López | 15/07 | 16/07 | 1 |
| 5 | Roles y funciones del sistema | Nicole Sánchez | 17/07 | 20/07 | 1 |
| 6 | Historias de Usuario (HU) | Nicole Sánchez | 17/07 | 23/07 | 1 |
| 7 | Alcances y límites del sistema | Héctor López | 21/07 | 22/07 | 1 |
| 8 | Planificación (tabla) | Héctor López | 21/07 | 22/07 | 1 |
| 9 | Cronograma Gantt (todo el proyecto) | Héctor López | 21/07 | 22/07 | 1 |
| 10 | Entradas/salidas por HU | Nicole Sánchez | 23/07 | 25/07 | 1 |
| 11 | Declaración de entidades | Carlos Ventura | 23/07 | 25/07 | 1 |
| 12 | Esquema de base de datos en Neon (17 tablas) | Alejandro Merino | 22/07 | 24/07 | 1 |
| 13 | Backend base: Spring Boot, entidades JPA, repositorios | Bayron Orellana / Carlos Ventura / Walter Flores | 23/07 | 04/08 | 1 |
| 14 | Seguridad: Spring Security + JWT + endpoint /login | Alejandro Vigil | 26/07 | 04/08 | 1 |
| 15 | Frontend base: Vite + React + TypeScript + Tailwind | Zair Díaz / William Melgar | 25/07 | 04/08 | 1 |
| 16 | Pantalla de login (LoginPage.tsx) | Zair Díaz / William Melgar | 28/07 | 07/08 | 1 |
| 17 | Casos de prueba QA — módulo de login | Erika Fuentes / Walter Vásquez | 05/08 | 07/08 | 1 |
| 18 | Conclusiones individuales | Todos (11) | 07/08 | 09/08 | 1 |
| 19 | Bibliografía APA | Nicole Sánchez | 07/08 | 09/08 | 1 |
| 20 | Consolidación y PDF del documento | Héctor López | 09/08 | 10/08 | 1 |
| 21 | **Entrega Avance 1** | Héctor López (entrega) | — | **10/08/2026** | 1 |
| 22 | Módulo de citas (backend + calendario) | Equipo backend | 11/08 | 07/09 | 2 |
| 23 | Blindaje de seguridad OWASP (S1–S7) | Alejandro Vigil + equipo | 24/08 | 13/09 | 2 |
| 24 | QA de citas y seguridad | Erika Fuentes / Walter Vásquez | 07/09 | 15/09 | 2 |
| 25 | **Entrega Avance 2** | Héctor López (entrega) | — | **26/09/2026** | 2 |
| 26 | Triaje, expediente clínico y recetas | Equipo backend + frontend | 21/09 | 16/10 | 3 |
| 27 | Reportes, inventario y dashboard | Equipo backend + frontend | 05/10 | 24/10 | 3 |
| 28 | Deploy, demo y documento final | Todo el equipo | 19/10 | 31/10 | 3 |
| 29 | **Entrega final y defensa** | Todo el equipo | — | **31/10/2026** | 3 |

---

## 9. CRONOGRAMA (DIAGRAMA DE GANTT)

> **INSTRUCCIÓN:** Insertar aquí la captura de pantalla del Gantt.
>
> **Cómo obtenerla:**
> 1. Abrir en el navegador: https://github.com/NapoSV/medisuite/blob/main/docs/diagramas/gantt_proyecto_completo.md
> 2. GitHub dibuja el diagrama automáticamente (Mermaid).
> 3. Presionar `Win + Shift + S` → seleccionar el diagrama → pegar en Word con `Ctrl + V`.
>
> El Gantt cubre **todo el proyecto (Avances 1, 2 y entrega final)** desde el 15/07/2026 hasta el 31/10/2026.

**[INSERTAR CAPTURA DEL GANTT AQUÍ]**

**Hitos principales:**

| Hito | Fecha |
|------|-------|
| Entrega Avance 1 | 10/08/2026 |
| Entrega Avance 2 | semana del 21–26/09/2026 |
| Entrega Final y Defensa | semana del 26–31/10/2026 |

---

## 10. ENTRADAS Y SALIDAS DEL SISTEMA POR HISTORIA DE USUARIO

**HU-001 — Registro y consulta de pacientes**

- **Entradas:** nombre completo, CIF (documento de identidad), fecha de nacimiento, teléfono, dirección, contacto de emergencia, tipo de sangre, alergias conocidas.
- **Salidas:** perfil del paciente registrado, confirmación de guardado, pantalla de expediente con historial, resultado de búsqueda por nombre o CIF.

---

**HU-002 — Registro de receta electrónica**

- **Entradas:** medicamentos (nombre, dosis, unidad), duración del tratamiento, indicaciones especiales, identificador del médico, identificador del paciente.
- **Salidas:** receta guardada en el expediente, fecha y hora de emisión, lista de recetas del paciente ordenadas cronológicamente.

---

**HU-003 — Agendamiento de cita por recepcionista**

- **Entradas:** búsqueda de paciente (nombre o CIF), médico seleccionado, especialidad, fecha y hora disponible, motivo de consulta.
- **Salidas:** código de reserva único (ej. COD-0001), cita registrada con estado "SCHEDULED", actualización de la agenda del médico, confirmación visible en el dashboard de recepción.

---

**HU-004 — Triaje y signos vitales**

- **Entradas:** búsqueda de paciente por CIF o nombre, peso (kg), talla (cm), presión arterial (mmHg), temperatura (°C), frecuencia cardíaca (bpm), síntomas descritos, nivel de prioridad.
- **Salidas:** registro de signos vitales guardado en expediente, paciente clasificado con prioridad (BAJO / MEDIO / ALTO / CRÍTICO), historial de triajes visible por el médico.

---

**HU-007 — Inicio de sesión seguro**

- **Entradas:** código de clínica (slug), correo electrónico, contraseña.
- **Salidas:** token JWT con tiempo de expiración, datos del usuario autenticado (nombre, rol, ID de clínica), redirección a la pantalla correspondiente al rol; en caso de error: mensaje de credenciales inválidas; tras 5 intentos: bloqueo por 15 minutos.

---

**HU-008 — Alta de usuarios por el administrador**

- **Entradas:** nombre completo, correo electrónico, rol asignado (DOCTOR / NURSE / ADMIN / RECEPTIONIST), clínica asociada.
- **Salidas:** usuario creado y activo en el sistema, contraseña temporal asignada, confirmación de registro al administrador.

---

## 11. DECLARACIÓN DE ENTIDADES DEL SISTEMA

Las siguientes entidades corresponden a las tablas de la base de datos implementada en Neon PostgreSQL (`database/schema.sql`):

| Entidad | Descripción | Atributos principales |
|---------|-------------|----------------------|
| **tenants** | Clínica u organización que usa el sistema (raíz del modelo multi-tenant). | id, slug, commercial_name, legal_name, tax_id, plan, status, max_users, max_patients |
| **users** | Usuario del sistema con credenciales y rol asignado. | id, tenant_id, first_name, last_name, cif, email, password_hash, role, active, failed_login_attempts, locked_until |
| **specialties** | Especialidades médicas disponibles por clínica. | id, tenant_id, name, active |
| **patients** | Información clínica adicional del paciente (extiende users). | id, tenant_id, user_id, birth_date, phone, address, emergency_contact, blood_type, allergies |
| **doctors** | Información profesional del médico (extiende users). | id, tenant_id, user_id, specialty_id, license_number, available_schedule (JSON) |
| **nurses** | Datos de la enfermera (extiende users). | id, tenant_id, user_id, shift, assigned_area |
| **administrators** | Datos del administrador (extiende users). | id, tenant_id, user_id, permission_level |
| **receptionists** | Datos del recepcionista (extiende users). | id, tenant_id, user_id, shift, assigned_office |
| **appointments** | Citas médicas agendadas. | id, tenant_id, patient_id, doctor_id, scheduled_at, status, reason, office, reservation_code |
| **medical_records** | Expediente clínico del paciente. | id, tenant_id, patient_id, general_notes |
| **vital_signs** | Signos vitales registrados en triaje. | id, tenant_id, medical_record_id, weight_kg, height_cm, blood_pressure, temperature_c, heart_rate, symptoms, priority |
| **prescriptions** | Recetas emitidas por el médico. | id, tenant_id, medical_record_id, doctor_id, medications, dosage, duration, instructions |
| **products** | Productos del inventario de la clínica. | id, tenant_id, name, category, unit_of_measure, current_stock, min_stock, unit_price |
| **purchase_orders** | Órdenes de compra a proveedores. | id, tenant_id, supplier, status, total_amount, ordered_on |
| **purchase_order_items** | Líneas de detalle de una orden de compra. | id, tenant_id, purchase_order_id, product_id, quantity, unit_price, received_quantity |
| **physical_assets** | Activos físicos de la clínica (equipos, mobiliario). | id, tenant_id, name, category, acquisition_value, acquired_on, status, location |
| **audit_logs** | Registro de auditoría de acciones críticas. | id, tenant_id, user_id, action, entity_name, entity_id, data_before (JSON), data_after (JSON), ip_address, created_at |

**Total: 17 entidades — todas implementadas como clases Java (`@Entity`) y tablas PostgreSQL.**

---

## 12. PROYECTO BASE — ESTRUCTURA Y CÓDIGO

### 12.1. Paquete principal

```
com.sv.grupo.hospital.citas
```

### 12.2. Estructura de paquetes (backend)

```
backend/src/main/java/com/sv/grupo/hospital/citas/
├── MediSuiteApplication.java          ← Punto de entrada Spring Boot
├── config/
│   └── SecurityConfig.java            ← Spring Security + CORS + JWT stateless
├── controller/
│   └── api/
│       └── AuthController.java        ← POST /api/auth/login
├── dao/                               ← 17 repositorios JPA
│   ├── TenantRepository.java
│   ├── UserRepository.java
│   ├── SpecialtyRepository.java
│   ├── PatientRepository.java
│   ├── DoctorRepository.java
│   ├── NurseRepository.java
│   ├── AdministratorRepository.java
│   ├── ReceptionistRepository.java
│   ├── AppointmentRepository.java
│   ├── MedicalRecordRepository.java
│   ├── VitalSignRepository.java
│   ├── PrescriptionRepository.java
│   ├── ProductRepository.java
│   ├── PurchaseOrderRepository.java
│   ├── PurchaseOrderItemRepository.java
│   ├── PhysicalAssetRepository.java
│   └── AuditLogRepository.java
├── dto/
│   └── auth/
│       ├── LoginRequest.java          ← Payload: tenantSlug, email, password
│       └── LoginResponse.java         ← accessToken, tokenType, expiresIn, user
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── model/                             ← 17 entidades JPA
│   ├── tenant/
│   │   └── Tenant.java
│   ├── users/
│   │   └── User.java
│   ├── medical/
│   │   ├── Specialty.java
│   │   ├── Patient.java
│   │   ├── Doctor.java
│   │   ├── Nurse.java
│   │   ├── Administrator.java
│   │   ├── Receptionist.java
│   │   ├── Appointment.java
│   │   ├── MedicalRecord.java
│   │   ├── VitalSign.java
│   │   └── Prescription.java
│   ├── inventory/
│   │   ├── Product.java
│   │   ├── PurchaseOrder.java
│   │   ├── PurchaseOrderItem.java
│   │   └── PhysicalAsset.java
│   └── audit/
│       └── AuditLog.java
├── security/
│   └── JwtTokenProvider.java          ← Generación y validación de JWT (JJWT 0.12.6)
└── service/
    └── AuthService.java               ← Lógica: tenant → usuario → BCrypt → JWT
```

### 12.3. Estructura del frontend

```
frontend/src/
├── api/
│   └── auth.ts                        ← loginRequest() → POST /api/auth/login
├── pages/
│   └── LoginPage.tsx                  ← Pantalla de login (MedCore Clay design)
├── store/
│   └── authStore.ts                   ← Zustand: token, user, loading, login(), logout()
├── App.tsx
├── main.tsx
└── index.css                          ← Tailwind + tokens MedCore Clay + Inter font
```

### 12.4. Tecnologías utilizadas

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Backend | Java | 21 (Temurin LTS) |
| Backend | Spring Boot | 3.3.2 |
| Backend | Spring Security | 6.x |
| Backend | JJWT | 0.12.6 |
| Backend | Hibernate / JPA | 6.5.2 |
| Base de datos | PostgreSQL (Neon) | 16 |
| Frontend | React | 19 |
| Frontend | TypeScript | 6.0 |
| Frontend | Vite | 8.1 |
| Frontend | Tailwind CSS | 3.4 |
| Frontend | Zustand | 5.0 |
| Frontend | Lucide React | 1.27 |
| Repositorio | Git / GitHub | — |
| Gestión de tareas | Microsoft Planner | — |

### 12.5. Evidencia del sistema funcionando

> **INSTRUCCIÓN:** Insertar aquí capturas de pantalla que evidencien el sistema.
>
> Capturas recomendadas:
> - [ ] Pantalla de login en `localhost:5173` (LoginPage con diseño MedCore Clay).
> - [ ] Respuesta del endpoint `/api/auth/login` en Postman o PowerShell (JWT retornado).
> - [ ] Consola de Neon con las 17 tablas del esquema.
> - [ ] Log de Spring Boot: "Started MediSuiteApplication in 7.378 seconds" y "Found 17 JPA repository interfaces".

**[INSERTAR CAPTURAS DE PANTALLA AQUÍ]**

---

## 13. CONCLUSIONES

> **INSTRUCCIÓN PARA CADA INTEGRANTE:**
> Redactar UNA conclusión personal (3–5 oraciones) sobre lo aprendido en el Avance 1.
> La conclusión debe relacionarse con la actividad que realizaste (ej. si hiciste el backend,
> habla de Spring Boot / JPA; si hiciste frontend, habla de React / Tailwind; si hiciste QA,
> habla de casos de prueba; si hiciste documento, habla de metodología Scrum y HU).
>
> Formato: **Nombre completo:** seguido del texto.

**LOPEZ RUIZ HECTOR NAPOLEON:**
*(pendiente)*

**VIGIL RAMIREZ ALEJANDRO ANTONIO:**
*(pendiente)*

**ORELLANA ROJAS BAYRON ALEXANDER:**
*(pendiente)*

**DIAZ SANTOS ZAIR BENETT:**
*(pendiente)*

**FLORES HERNANDEZ WALTER ALEJANDRO:**
*(pendiente)*

**MELGAR RIVAS WILLIAM ARIEL:**
*(pendiente)*

**MERINO VENTURA ALEJANDRO SEBASTIAN:**
*(pendiente)*

**FUENTES ORTIZ ERIKA ALEXANDRA:**
*(pendiente)*

**VASQUEZ AMAYA WALTER AMILCAR:**
*(pendiente)*

**VENTURA VELASQUEZ CARLOS MARIO:**
*(pendiente)*

**SANCHEZ MENJIVAR NICOLE NOHEMY:**
*(pendiente)*

---

## 14. BIBLIOGRAFÍA (APA 7ª edición)

> **INSTRUCCIÓN:** Agregar las fuentes consultadas durante el Avance 1.
> Usar formato APA 7ª edición. A continuación se listan las fuentes base del proyecto;
> cada integrante debe agregar las adicionales que consultó para su tarea.

VMware, Inc. (2024). *Spring Boot Reference Documentation* (versión 3.3.2). https://docs.spring.io/spring-boot/docs/3.3.2/reference/html/

VMware, Inc. (2024). *Spring Security Reference Documentation* (versión 6.x). https://docs.spring.io/spring-security/reference/

Neon Technologies, Inc. (2024). *Neon Serverless PostgreSQL Documentation*. https://neon.tech/docs

Meta Open Source. (2024). *React Documentation* (versión 19). https://react.dev/

Vercel. (2024). *Vite Build Tool Documentation* (versión 8.x). https://vitejs.dev/

Tailwind Labs. (2024). *Tailwind CSS Documentation* (versión 3.4). https://tailwindcss.com/docs

pmndrs. (2024). *Zustand — State management for React*. https://github.com/pmndrs/zustand

> *(Agregar aquí las fuentes adicionales de cada integrante en formato APA.)*

---

*Documento preparado por: Héctor Napoleón López Ruiz (PM) — 31/07/2026*
*Revisión final: pendiente antes del 10/08/2026*
