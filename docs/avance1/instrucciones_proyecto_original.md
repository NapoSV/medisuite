# Plataforma de Gestión de Citas Médicas (Clínica/Hospital)

## 1. Portada

| Fotografía | Nombre completo | CIF | ¿Participó? |
|------------|-----------------|-----|-------------|
| ![Héctor](https://via.placeholder.com/50) | LOPEZ RUIZ HECTOR NAPOLEON | 79360441 | SI |
| ![Alejandro](https://via.placeholder.com/50) | VIGIL RAMIREZ ALEJANDRO ANTONIO | 60111191 | SI |
| ![Bayron](https://via.placeholder.com/50) | ORELLANA ROJAS BAYRON ALEXANDER | 75699490 | SI |
| ![Zair](https://via.placeholder.com/50) | DIAZ SANTOS ZAIR BENETT | 74528330 | SI |
| ![Walter](https://via.placeholder.com/50) | FLORES HERNANDEZ WALTER ALEJANDRO | 75497362 | SI |
| ![William](https://via.placeholder.com/50) | MELGAR RIVAS WILLIAM ARIEL | 63167135 | SI |
| ![Sebastian](https://via.placeholder.com/50) | ALEJANDRO SEBASTIAN MERINO VENTURA | — | SI |
| ![Erika](https://via.placeholder.com/50) | FUENTES ORTIZ ERIKA ALEXANDRA | 76590699 | SI |
| ![Walter Amilcar](https://via.placeholder.com/50) | VASQUEZ AMAYA WALTER AMILCAR | 74869704 | SI |
| ![Carlos](https://via.placeholder.com/50) | VENTURA VELASQUEZ CARLOS MARIO | 60127297 | SI |
| ![Nicole](https://via.placeholder.com/50) | SANCHEZ MENJIVAR NICOLE NOHEMY | 74243942 | SI |

---

## 2. Objetivo General

Desarrollar una plataforma de gestión de citas médicas que permita optimizar la administración de agendas, el registro de pacientes y el historial clínico en una clínica u hospital, mejorando la eficiencia y calidad de la atención sanitaria.

---

## 3. Objetivos Específicos

**(Primer avance – objetivo específico 1)**
- **OE1:** Diseñar e implementar el módulo de registro y autenticación de usuarios (pacientes, médicos, enfermeras, administradores) con control de roles y permisos.

**(Segundo avance – objetivo específico 2)**
- **OE2:** Desarrollar el sistema de gestión de citas que permita programar, modificar, cancelar y consultar citas médicas, así como la visualización de agendas por médico y fecha.

**(Tercer avance – objetivo específico 3)**
- **OE3:** Implementar el módulo de expediente clínico que incluya el registro de signos vitales (triaje), diagnóstico, recetas médicas y evolución del paciente, con capacidad de búsqueda y generación de reportes.

---

## 4. Distribución del Equipo (Scrum)

| N° | Nombre completo | Rol Scrum | Rol Técnico |
|----|-----------------|-----------|-------------|
| 1  | LOPEZ RUIZ HECTOR NAPOLEON | PRODUCT OWNER | Analista de Negocio |
| 2  | VIGIL RAMIREZ ALEJANDRO ANTONIO | SCRUM MASTER | Desarrollador Fullstack |
| 3  | ORELLANA ROJAS BAYRON ALEXANDER | DEVELOPER | Desarrollador Backend |
| 4  | DIAZ SANTOS ZAIR BENETT | DEVELOPER | Desarrollador Frontend |
| 5  | FLORES HERNANDEZ WALTER ALEJANDRO | DEVELOPER | Desarrollador Backend |
| 6  | MELGAR RIVAS WILLIAM ARIEL | DEVELOPER | Desarrollador Frontend |
| 7  | MERINO VENTURA ALEJANDRO SEBASTIAN | DEVELOPER | Base de Datos |
| 8  | FUENTES ORTIZ ERIKA ALEXANDRA | QA | Analista de Pruebas |
| 9  | VASQUEZ AMAYA WALTER AMILCAR | QA | Analista de Pruebas |
| 10 | VENTURA VELASQUEZ CARLOS MARIO | ARCHITECT | Arquitecto de Software |
| 11 | SANCHEZ MENJIVAR NICOLE NOHEMY | BUSSINES ANALYST | Analista de Negocio |

---

## 5. Definición de Roles y Funciones por Rol del Sistema

| Rol | Descripción | Funciones en el sistema |
|-----|-------------|--------------------------|
| **Médico** | Profesional de la salud encargado del diagnóstico, tratamiento y seguimiento del paciente. | - Ver agenda de citas asignadas. <br>- Acceder al expediente clínico del paciente. <br>- Registrar diagnóstico, recetas y evolución. <br>- Modificar o cancelar citas (con justificación). <br>- Generar reportes de atención. |
| **Paciente** | Persona que recibe atención médica y es el centro del sistema. | - Registrarse y gestionar su perfil. <br>- Solicitar y cancelar citas. <br>- Visualizar su historial clínico y recetas. <br>- Recibir notificaciones de citas y recordatorios. |
| **Enfermera** | Profesional que realiza triaje, toma signos vitales y apoya en la atención. | - Registrar signos vitales del paciente (peso, talla, presión, temperatura, etc.). <br>- Completar datos de pacientes en espera. <br>- Clasificar prioridad de atención (triaje). <br>- Consultar agenda y estado de citas. |
| **Administrador** | Encargado de la gestión general del sistema y de los usuarios. | - Crear, modificar y eliminar usuarios (médicos, enfermeras, etc.). <br>- Configurar horarios y disponibilidad de consultorios. <br>- Generar reportes administrativos (estadísticas de atención, ocupación, etc.). <br>- Gestionar auditoría de accesos. |
| **Recepcionista** | Personal de recepción que maneja la entrada y salida de pacientes. | - Registrar pacientes nuevos. <br>- Asignar citas de acuerdo a disponibilidad. <br>- Confirmar asistencia de pacientes. <br>- Imprimir órdenes de atención. |

---

## 6. Requerimientos del Sistema (Historias de Usuario)

| Prioridad | Historia de Usuario (HU) | Criterios de Aceptación |
|-----------|---------------------------|--------------------------|
| **Alta** | **HU-001** Como enfermera, necesito un formulario para registrar pacientes y consultar su expediente. | - El paciente debe quedar registrado con todos sus datos (nombre, CIF, fecha de nacimiento, contacto). <br>- Se debe poder buscar al paciente por nombre o CIF. <br>- El expediente debe mostrar el historial de citas, diagnósticos y signos vitales. |
| **Alta** | **HU-002** Como médico, quiero registrar una receta para llevar el control del tratamiento del paciente. | - La receta debe quedar almacenada en el expediente. <br>- Debe asociarse al paciente y al médico que la emite. <br>- Debe mostrar fecha y hora de emisión. <br>- Debe incluir medicamentos, dosis y duración del tratamiento. |
| **Alta** | **HU-003** Como paciente, quiero solicitar una cita médica en línea y elegir horario disponible. | - El sistema debe mostrar los horarios disponibles del médico seleccionado. <br>- La cita se confirma con un código de reserva. <br>- Se envía notificación al paciente y al médico. <br>- El paciente puede cancelar o reprogramar la cita con al menos 24 horas de anticipación. |
| **Media** | **HU-004** Como enfermera, quiero realizar el triaje de pacientes en espera y registrar signos vitales. | - El sistema debe permitir buscar al paciente por CIF o nombre. <br>- Registrar peso, talla, presión arterial, temperatura, frecuencia cardíaca y síntomas. <br>- Asignar un nivel de prioridad (bajo, medio, alto, crítico). <br>- La información se guarda en el expediente. |
| **Media** | **HU-005** Como administrador, quiero gestionar los horarios de los médicos y la disponibilidad de consultorios. | - Crear, modificar y eliminar bloques horarios para cada médico. <br>- Asignar consultorios a los médicos en cada turno. <br>- Visualizar la ocupación de consultorios en tiempo real. |
| **Baja** | **HU-006** Como recepcionista, quiero imprimir órdenes de atención para los pacientes. | - Seleccionar un paciente y generar un documento PDF con los datos de la cita y el médico asignado. <br>- La orden debe incluir un código de barras para identificación rápida. |

---

## 7. Alcances / Limitaciones / Límites (Qué no hará el sistema)

- **No** incluirá módulo de facturación o pagos de servicios médicos (solo se centra en gestión de citas y expediente).
- **No** se conectará con sistemas externos de laboratorio o farmacia.
- **No** tendrá funcionalidad de telemedicina (videollamadas) en esta versión.
- **No** manejará historial de pagos o seguros médicos.
- **No** permitirá la modificación de datos médicos por parte del paciente.
- **No** generará reportes financieros o de facturación.

---

## 8. Planificación (Distribución de Actividades por Avance)

| Avance | Actividad | Responsable | Fecha estimada |
|--------|-----------|-------------|----------------|
| **1** | Definición de requisitos y diseño de base de datos. | MERINO VENTURA (BD) + VENTURA (Arq.) | 20/07/2026 |
| **1** | Implementación del módulo de autenticación y roles. | VIGIL (Fullstack) + ORELLANA (Backend) | 27/07/2026 |
| **1** | Creación de la interfaz de registro de pacientes (frontend). | DIAZ (Frontend) + MELGAR (Frontend) | 03/08/2026 |
| **1** | Pruebas iniciales del módulo de usuarios. | FUENTES (QA) + VASQUEZ (QA) | 10/08/2026 |
| **2** | Desarrollo del módulo de gestión de citas (back y front). | ORELLANA + DIAZ + MELGAR | 24/08/2026 |
| **2** | Integración con el calendario y disponibilidad. | VIGIL (Fullstack) + MERINO (BD) | 07/09/2026 |
| **2** | Pruebas funcionales de citas. | FUENTES + VASQUEZ | 14/09/2026 |
| **3** | Desarrollo del módulo de expediente clínico y triaje. | FLORES (Backend) + DIAZ (Frontend) | 28/09/2026 |
| **3** | Generación de reportes y estadísticas. | ORELLANA + MELGAR | 12/10/2026 |
| **3** | Pruebas integrales y ajustes finales. | Todos los Developers + QA | 19/10/2026 |
| **Entrega final** | Documentación y presentación. | Todo el equipo | 26/10/2026 |

---

## 9. Cronograma (Diagrama de Gantt)

| Actividad | Semana 1 | Semana 2 | Semana 3 | Semana 4 | Semana 5 | Semana 6 | Semana 7 | Semana 8 | Semana 9 | Semana 10 |
|-----------|----------|----------|----------|----------|----------|----------|----------|----------|----------|------------|
| **Avance 1** (Requisitos, BD, Autenticación, Frontend, Pruebas) | ████████ | ████████ | ████████ | ████████ | | | | | | |
| **Avance 2** (Citas, Disponibilidad, Pruebas) | | | | | ████████ | ████████ | ████████ | | | |
| **Avance 3** (Expediente, Triaje, Reportes) | | | | | | | | ████████ | ████████ | |
| **Entrega Final** (Documentación, Presentación) | | | | | | | | | | ████████ |

---

## 10. Entradas / Salidas del Sistema (según HU)

| HU | Entradas | Salidas |
|----|----------|---------|
| **HU-001** (Registro de paciente) | Nombre, apellido, CIF, fecha de nacimiento, teléfono, dirección, correo electrónico. | Confirmación de registro, visualización de expediente con datos y citas previas. |
| **HU-002** (Registro de receta) | ID del paciente, ID del médico, fecha, medicamentos, dosis, duración, indicaciones adicionales. | Receta almacenada en el expediente, notificación al paciente, lista de medicamentos. |
| **HU-003** (Solicitud de cita) | ID del paciente, ID del médico, fecha deseada, hora preferida, motivo de consulta. | Confirmación de cita, código de reserva, notificación por correo/WhatsApp (simulado). |
| **HU-004** (Triaje) | ID del paciente, peso, talla, presión arterial, temperatura, frecuencia cardíaca, síntomas, nivel de prioridad. | Registro de signos vitales en expediente, actualización de estado de cita (en espera, atendido). |
| **HU-005** (Gestión de horarios) | ID del médico, día de la semana, hora de inicio, hora de fin, consultorio asignado. | Visualización de agenda actualizada, disponibilidad en calendario. |
| **HU-006** (Orden de atención) | ID del paciente, ID de cita. | Documento PDF con datos del paciente, médico, fecha y código de barras. |

---

## 11. Declaración de Entidades del Sistema

| Entidad | Descripción | Atributos |
|---------|-------------|-----------|
| **Usuario** | Persona que accede al sistema (base para roles). | idUsuario, nombre, apellido, CIF, correo, contraseña, rol, fechaRegistro, activo. |
| **Paciente** | Persona que recibe atención médica. | idPaciente, idUsuario (FK), fechaNacimiento, telefono, direccion, contactoEmergencia, grupoSanguineo, alergias. |
| **Medico** | Profesional de salud. | idMedico, idUsuario (FK), especialidad, numeroLicencia, horarioDisponible (JSON). |
| **Enfermera** | Profesional que realiza triaje y apoyos. | idEnfermera, idUsuario (FK), turno, areaAsignada. |
| **Administrador** | Usuario con permisos de gestión. | idAdmin, idUsuario (FK), nivelPermiso. |
| **Cita** | Registro de una consulta programada. | idCita, idPaciente (FK), idMedico (FK), fechaHora, estado (programada, cancelada, completada, en espera), motivo, consultorio. |
| **Expediente** | Historial clínico del paciente. | idExpediente, idPaciente (FK), fechaCreacion, observacionesGenerales. |
| **SignoVital** | Registro de triaje. | idSigno, idExpediente (FK), fechaHora, peso, talla, presionArterial, temperatura, frecuenciaCardiaca, sintomas, prioridad. |
| **Receta** | Prescripción médica. | idReceta, idExpediente (FK), idMedico (FK), fecha, medicamentos, dosis, duracion, indicaciones. |

---

## 12. Proyecto Base con la Estructura (Código y Clases)

### 12.1 Stack confirmado

Siguiendo la recomendación del ingeniero de la materia: **Java 21 + Spring Boot** (backend), **PostgreSQL** (base de datos), **REST API + JSON** (contrato de comunicación). El frontend (React o Angular) queda en evaluación y no afecta la lógica de negocio en Java, que es el foco de esta sección.

### 12.2 Estructura de paquetes

```
com.sv.grupo.hospital.citas/
├── config/           ← Beans de configuración (Security, Locale, DB)
├── model/
│   ├── tenant/       ← Tenant, Plan, Feature
│   ├── users/        ← User, Doctor, Nurse, Patient
│   ├── clinical/     ← Appointment, MedicalRecord, VitalSign, Prescription
│   └── inventory/    ← Product, PurchaseOrder, PhysicalAsset
├── dao/              ← Repositories (Spring Data JPA)
├── service/          ← Lógica de negocio
├── controller/api/   ← Endpoints REST
├── dto/              ← Data Transfer Objects (entrada/salida de APIs)
├── security/         ← JWT, TenantContext, filtros
├── util/             ← Validadores, helpers, generadores
└── exception/        ← Excepciones personalizadas
```

### 12.3 Mapeo de entidades (Sección 11) a clases Java

Por estándar de código del equipo (`docs/ESTANDARES_CODIGO.md`), las clases, atributos y métodos se nombran en **inglés**; las entidades de la Sección 11 se describieron en español para facilitar la lectura del documento. Tabla de equivalencia:

| Entidad (Sección 11) | Clase Java | Paquete |
|-----------------------|-----------|---------|
| Usuario | `User` | `model.users` |
| Paciente | `Patient` | `model.users` |
| Medico | `Doctor` | `model.users` |
| Enfermera | `Nurse` | `model.users` |
| Administrador | `Administrator` | `model.users` |
| Cita | `Appointment` | `model.clinical` |
| Expediente | `MedicalRecord` | `model.clinical` |
| SignoVital | `VitalSign` | `model.clinical` |
| Receta | `Prescription` | `model.clinical` |

### 12.4 Clases del modelo (mínimo 2 requeridas)

**`Patient.java`**

```java
package com.sv.grupo.hospital.citas.model.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(columnDefinition = "TEXT")
    private String allergies;
}
```

**`Doctor.java`**

```java
package com.sv.grupo.hospital.citas.model.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String specialty;

    @Column(name = "license_number", nullable = false, unique = true, length = 30)
    private String licenseNumber;

    @Column(name = "available_schedule", columnDefinition = "jsonb")
    private String availableSchedule;
}
```

> Ambas clases siguen la convención del equipo: 1 clase por archivo, atributos `private`, getters/setters vía Lombok, sin lógica de negocio (esa vive en `service/`). `PurchaseOrder`, `VitalSign` y el resto de entidades se implementan bajo el mismo patrón en fases posteriores.

---

## 13. Conclusiones

> Cada integrante redacta 2–3 líneas sobre lo aprendido durante el Avance 1 (planificación, diseño de BD, arquitectura, o el proceso de equipo). Pendiente de recopilar — Héctor consolida antes de exportar el PDF final.

- **LOPEZ RUIZ HECTOR NAPOLEON:** _[pendiente]_
- **VIGIL RAMIREZ ALEJANDRO ANTONIO:** _[pendiente]_
- **ORELLANA ROJAS BAYRON ALEXANDER:** _[pendiente]_
- **DIAZ SANTOS ZAIR BENETT:** _[pendiente]_
- **FLORES HERNANDEZ WALTER ALEJANDRO:** _[pendiente]_
- **MELGAR RIVAS WILLIAM ARIEL:** _[pendiente]_
- **MERINO VENTURA ALEJANDRO SEBASTIAN:** _[pendiente]_
- **FUENTES ORTIZ ERIKA ALEXANDRA:** _[pendiente]_
- **VASQUEZ AMAYA WALTER AMILCAR:** _[pendiente]_
- **VENTURA VELASQUEZ CARLOS MARIO:** _[pendiente]_
- **SANCHEZ MENJIVAR NICOLE NOHEMY:** _[pendiente]_

---

## 14. Bibliografía (formato APA)

### Ingeniería de Software
1. Sommerville, I. (2011). *Ingeniería del software* (9.ª ed.). Pearson Educación.
2. Pressman, R. S., & Maxim, B. R. (2015). *Ingeniería del software: un enfoque práctico* (8.ª ed.). McGraw-Hill.
3. Larman, C. (2004). *UML y patrones: introducción al análisis y diseño orientado a objetos* (2.ª ed.). Prentice Hall.

### Metodología Ágil / Scrum
4. Schwaber, K., & Sutherland, J. (2020). *The Scrum Guide*. Scrum.org. https://scrumguides.org
5. Sutherland, J. (2014). *Scrum: el arte de hacer el doble de trabajo en la mitad de tiempo*. Paidós Empresa.

### Java / Backend
6. Horstmann, C. S. (2019). *Core Java, Volume I: Fundamentals* (11.ª ed.). Prentice Hall.
7. Bloch, J. (2018). *Effective Java* (3.ª ed.). Addison-Wesley.
8. Walls, C. (2019). *Spring in Action* (5.ª ed.). Manning Publications.

### Bases de Datos
9. Silberschatz, A., Korth, H. F., & Sudarshan, S. (2019). *Fundamentos de bases de datos* (7.ª ed.). McGraw-Hill.

### Frontend
10. Banks, A., & Porcello, E. (2020). *Learning React* (2.ª ed.). O'Reilly Media.

### Fuentes Web
- Oracle. (2024). *Java SE Documentation*. https://docs.oracle.com/en/java/
- Spring. (2024). *Spring Framework Reference Documentation*. https://spring.io/projects/spring-framework
- PostgreSQL Global Development Group. (2024). *PostgreSQL Documentation*. https://www.postgresql.org/docs/
