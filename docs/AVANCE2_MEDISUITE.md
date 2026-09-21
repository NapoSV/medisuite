# MediSuite — Avance 2
## Sistema de Gestión Clínica Multi-Tenant

**Universidad Evangélica de El Salvador**
**Fecha de entrega:** 27 de septiembre de 2026

| Campo | Detalle |
|---|---|
| **Materia** | Programación II |
| **Docente** | Ing. Guevara |
| **Grupo** | 7 |
| **Entrega** | Avance 2 |

**Integrantes:**

| Nombre | CIF | Rol |
|---|---|---|
| López Ruiz Héctor Napoleón | 2026010132 | Scrum Master / Arquitecto |
| Vigil Ramírez Alejandro Antonio | 2026010204 | Backend / Seguridad |
| Orellana Rojas Bayron Alexander | 2026011707 | Backend / Módulo Citas |
| Díaz Santos Zair Benett | 2026010796 | Frontend |
| Flores Hernández Walter Alejandro | 2026011012 | QA / Base de datos |
| Melgar Rivas William Ariel | 2026011736 | Frontend |
| Merino Ventura Alejandro Sebastián | 2026020122 | Feature Crítico / Concurrencia |
| Fuentes Ortiz Erika Alexandra | 2026011709 | QA |
| Vásquez Amaya Walter Amílcar | 2026010068 | Backend / Frontend |
| Ventura Velásquez Carlos Mario | 2026011585 | Backend / Entidades |
| Sánchez Menjívar Nicole Nohemy | 2026010813 | QA / Documentación |

---

## Índice

1. [Introducción](#1-introducción)
2. [Justificación](#2-justificación)
3. [Objetivos](#3-objetivos)
4. [Marco Teórico](#4-marco-teórico)
5. [Metodología](#5-metodología)
6. [Historias de Usuario](#6-historias-de-usuario)
7. [Diseño del Sistema](#7-diseño-del-sistema)
8. [Arquitectura del Sistema](#8-arquitectura-del-sistema)
9. [Diagrama Entidad-Relación](#9-diagrama-entidad-relación)
10. [Entradas y Salidas por Historia de Usuario](#10-entradas-y-salidas-por-historia-de-usuario)
11. [Plan de Pruebas y Resultados](#11-plan-de-pruebas-y-resultados)
12. [El Proyecto — Descripción Técnica](#12-el-proyecto--descripción-técnica)
13. [Conclusiones](#13-conclusiones)
14. [Bibliografía](#14-bibliografía)

---

## 1. Introducción

MediSuite es un sistema de gestión clínica multi-tenant desarrollado como proyecto integrador de la asignatura Programación II. El sistema permite a múltiples clínicas (tenants) gestionar de forma independiente y segura sus pacientes, médicos, citas, expedientes médicos y recetas, todo sobre una infraestructura compartida con estricto aislamiento de datos por tenant.

El Avance 2 representa la implementación funcional del sistema completo, partiendo de la base arquitectónica establecida en el Avance 1. En esta entrega se integran los módulos de seguridad basada en JWT, gestión de citas con control de concurrencia, expedientes médicos, recetas con impresión A4, un dashboard con métricas en tiempo real y una capa de persistencia híbrida que combina PostgreSQL con archivos binarios `.dat` para respaldo de auditoría.

El desarrollo siguió una metodología ágil Scrum con sprints semanales, integración continua mediante GitHub Actions y revisión de código con Pull Requests obligatorios hacia la rama `develop`.

---

## 2. Justificación

La digitalización de los procesos clínicos representa una necesidad crítica para las instituciones de salud en El Salvador. Los sistemas actuales en muchas clínicas medianas y pequeñas consisten en registros en papel o hojas de cálculo, lo que genera:

- Pérdida de historial clínico al extraviar documentos físicos.
- Imposibilidad de acceso concurrente y remoto al expediente del paciente.
- Ausencia de trazabilidad en la prescripción de medicamentos.
- Dificultad para generar reportes y métricas operativas.

MediSuite resuelve estos problemas con una arquitectura moderna, segura y escalable. La decisión de implementar multi-tenancy permite que la misma plataforma sirva a varias clínicas con garantía de aislamiento total de datos, reduciendo el costo de infraestructura respecto a sistemas separados por cliente.

La incorporación de una capa de respaldo en archivos `.dat` (además de PostgreSQL) responde al requisito académico de demostrar persistencia de objetos Java con serialización nativa, integrando así los conceptos de concurrencia y persistencia estudiados durante el ciclo.

---

## 3. Objetivos

### 3.1 Objetivo General

Desarrollar e integrar los módulos funcionales del sistema MediSuite en su Avance 2, implementando la gestión completa de citas, expedientes médicos, recetas, seguridad JWT multi-tenant y una capa de persistencia híbrida con respaldo en archivos `.dat`.

### 3.2 Objetivos Específicos

1. Implementar autenticación y autorización basadas en JWT con soporte multi-tenant, incluyendo rate limiting y blacklist de tokens revocados.
2. Desarrollar el módulo de citas con control de concurrencia para evitar conflictos de agenda entre doctores.
3. Crear el expediente médico digital con registro de signos vitales y prescripciones por paciente.
4. Implementar la funcionalidad de recetas médicas con vista de impresión en formato A4.
5. Construir un dashboard con métricas clínicas obtenidas mediante consultas paralelas.
6. Desarrollar una capa de persistencia híbrida que respalda registros de auditoría en archivos `.dat` mediante un scheduler asíncrono.
7. Implementar el frontend en React con todos los módulos operativos y estados de carga consistentes.
8. Garantizar la calidad mediante pruebas unitarias, de integración con Testcontainers y casos manuales.

---

## 4. Marco Teórico

### 4.1 Arquitectura Multi-Tenant

El patrón multi-tenant permite que múltiples organizaciones (tenants) compartan la misma instancia de aplicación y base de datos, con aislamiento lógico de datos. MediSuite implementa el enfoque *shared schema* con discriminador `tenant_id` en cada tabla, filtrando automáticamente las consultas mediante el contexto de seguridad del JWT.

### 4.2 JSON Web Tokens (JWT)

JWT es un estándar abierto (RFC 7519) que define una forma compacta y autocontenida de transmitir información entre partes como un objeto JSON firmado. En MediSuite, el payload del token incluye el identificador del usuario y del tenant, permitiendo resolver ambos contextos sin consultas adicionales a la base de datos en cada request.

### 4.3 Principios SOLID y Herencia con JPA

La entidad base `BaseEntity` implementa el principio de responsabilidad única al centralizar los campos comunes (`id`, `tenant`, `createdAt`, `updatedAt`) que heredan todas las entidades del dominio. Esto facilita el mantenimiento y garantiza consistencia en el esquema de datos.

### 4.4 Programación Concurrente en Java

Java proporciona múltiples mecanismos para la programación concurrente. En este proyecto se utilizan dos enfoques complementarios: `ScheduledExecutorService` para la ejecución periódica del respaldo asíncrono a archivos `.dat`, y `CompletableFuture.allOf` para la obtención paralela de métricas del dashboard, reduciendo la latencia de respuesta al ejecutar las consultas de forma simultánea.

### 4.5 Flyway — Migraciones de Base de Datos

Flyway es una herramienta de control de versiones para esquemas de bases de datos relacionales. Cada cambio en el esquema se codifica como un script SQL versionado (`V1__`, `V2__`, etc.) que Flyway aplica de forma ordenada e idempotente. Esto garantiza que todos los entornos (desarrollo, QA, producción) tengan exactamente el mismo esquema.

---

## 5. Metodología

El desarrollo del Avance 2 siguió el marco ágil **Scrum** con los siguientes artefactos:

- **Product Backlog:** 88 tareas cargadas en Microsoft Planner, organizadas por módulo y prioridad.
- **Sprints:** 3 sprints semanales (31/08 → 20/09), cada uno con objetivo definido y checkpoint de revisión.
- **Rama de integración:** `develop`. Toda integración requirió Pull Request revisado por el Scrum Master.
- **Integración continua:** GitHub Actions verifica que ningún commit incluya co-autoría de IA y que el build compile.
- **Control de calidad:** pruebas unitarias con JUnit 5 + Mockito, integración con Testcontainers y 20 casos manuales documentados.

| Sprint | Fechas | Foco principal |
|---|---|---|
| S1 | 31/08 – 06/09 | Infraestructura base: BaseEntity, JWT, Flyway, ramas personales |
| S2 | 07/09 – 13/09 | Módulos core: Citas, Expediente, Dashboard, persistencia `.dat` |
| S3 | 14/09 – 20/09 | Integración: Recetas, frontend completo, QA, corrección de bugs |

---

## 6. Historias de Usuario

| ID | Título | Rol | Prioridad |
|---|---|---|---|
| HU-001 | Autenticación con JWT | Todos los usuarios | Alta |
| HU-002 | Gestión de pacientes | Recepcionista / Admin | Alta |
| HU-003 | Gestión de citas | Recepcionista / Doctor | Alta |
| HU-004 | Expediente médico del paciente | Doctor | Alta |
| HU-005 | Prescripción de recetas | Doctor | Alta |
| HU-006 | Dashboard de métricas clínicas | Admin | Media |
| HU-007 | Gestión de doctores | Admin | Media |
| HU-008 | Cambio de contraseña obligatorio en primer login | Todos | Media |
| HU-009 | Respaldo de auditoría en archivo `.dat` | Sistema | Baja |
| HU-010 | Vista imprimible de receta médica | Doctor | Media |

---

## 7. Diseño del Sistema

### 7.1 Diagrama de Casos de Uso

Los actores del sistema son: **Administrador**, **Doctor**, **Recepcionista** y **Sistema** (para procesos automáticos como el respaldo `.dat`).

- El Administrador gestiona usuarios, doctores y tiene acceso a la documentación Swagger.
- El Doctor consulta expedientes, registra signos vitales y emite recetas.
- La Recepcionista registra pacientes, agenda citas y gestiona el estado de las mismas.
- El Sistema ejecuta el scheduler de respaldo cada 60 segundos de forma autónoma.

### 7.2 Flujo principal — Ciclo de una cita

```
Login → Dashboard → Nueva Cita → (seleccionar doctor, fecha, slot)
  → Cita creada (RSV-XXXX) → Paciente llega → Expediente médico
  → Signos vitales → Receta → Impresión A4
```

---

## 8. Arquitectura del Sistema

### 8.1 Visión general

MediSuite sigue una arquitectura en tres capas desacopladas:

| Capa | Tecnología | Responsabilidad |
|---|---|---|
| Frontend | React 18 + TypeScript + Tailwind CSS | Interfaz de usuario, consumo REST |
| Backend | Spring Boot 3.x + Java 21 | Lógica de negocio, seguridad, persistencia |
| Base de datos | PostgreSQL 16 (Neon serverless) | Almacenamiento relacional multi-tenant |

El backend expone una API REST bajo `/api/**`, protegida por un filtro JWT que extrae el `tenant_id` y el `user_id` del token en cada request, publicándolos en el `TenantContext` (ThreadLocal) para su uso transparente en toda la cadena de servicio.

### 8.2 Estructura de paquetes del backend

```
com.sv.grupo7.medisuite
├── config/          SecurityConfig, CORS
├── controller/api/  Controladores REST por módulo
├── dao/             Repositorios JPA (Spring Data)
├── dat/             Capa de persistencia .dat (DatFileDao, schedulers)
├── dto/             Objetos de transferencia
├── exception/       GlobalExceptionHandler
├── model/           Entidades JPA (medical/, users/, tenant/, audit/)
├── security/        JWT, filtros, TenantContext, RateLimitFilter
└── service/         Lógica de negocio
```

---

## 9. Diagrama Entidad-Relación

Las entidades principales y sus relaciones son:

```
tenants (1) ──< users (N)
tenants (1) ──< patients (N)
tenants (1) ──< doctors (N)
users   (1) ──  doctors (1)        [OneToOne]
doctors (N) >── specialties (1)
patients (1) ── medical_records (1)
medical_records (1) ──< vital_signs (N)
medical_records (1) ──< prescriptions (N)
prescriptions (N) >── doctors (1)
prescriptions (1) ──< prescription_items (N)
doctors (1) ──< appointments (N)
patients (1) ──< appointments (N)
users (1) ──< audit_logs (N)
```

Todas las tablas incluyen los campos heredados de `BaseEntity`: `id` (BIGSERIAL), `tenant_id` (FK a tenants), `created_at` y `updated_at` (TIMESTAMPTZ con zona horaria).

---

## 10. Entradas y Salidas por Historia de Usuario

> *Sección a completar por Walter Vásquez (tarea V-09).*
> *Mínimo 5 HUs con tabla de entradas y salidas.*

---

## 11. Plan de Pruebas y Resultados

### 11.1 Pruebas unitarias

Se implementaron pruebas unitarias con JUnit 5 y Mockito para los servicios principales:

| Clase de prueba | Servicio bajo prueba | Casos cubiertos |
|---|---|---|
| `AuthServiceTest` | `AuthService` | Login correcto, contraseña incorrecta, cuenta bloqueada |
| `PatientServiceTest` | `PatientService` | Búsqueda por DUI, paginación, not found |
| `AppointmentServiceTest` | `AppointmentService` | Crear cita, conflicto de slot, cancelar |
| `MedicalRecordServiceTest` | `MedicalRecordService` | Consulta expediente completo |

### 11.2 Prueba de integración con Testcontainers

`PatientRepositoryIT` arranca un contenedor real de PostgreSQL 16 Alpine mediante Testcontainers, verificando que los repositorios JPA funcionan con una base de datos real (no mocks). Tres aserciones: contenedor activo, repositorio inyectado, `findAll()` no lanza excepción.

### 11.3 Test de integración backup `.dat`

`AuditLogDatDaoIntegrationTest` verifica el ciclo completo de serialización y deserialización del `AuditLogDatDao`: guarda una lista de `AuditLog` en el archivo binario y la recupera, comprobando integridad de los datos.

### 11.4 Casos de prueba manuales

Se ejecutaron 20 casos de prueba manuales (ver `docs/fases/casos-de-prueba.md`) cubriendo los flujos principales de HU-001 a HU-010. De los 20 casos, 14 pasaron en la primera ejecución; 6 presentaron fallos que fueron documentados y corregidos (ver `docs/fases/reporte-bugs.md`).

### 11.5 Resumen de bugs encontrados y corregidos

| ID | Endpoint | Causa raíz | Estado |
|---|---|---|---|
| BUG-01 | GET /api/patients | `LazyInitializationException` — falta `@Transactional` | Corregido |
| BUG-02 | GET /api/patients/{id} | Misma causa raíz | Corregido |
| BUG-03 | GET /api/doctors | `LazyInitializationException` relaciones lazy | Corregido |
| BUG-04 | GET /api/patients/{id}/medical-record | Serialización de relaciones lazy en `Map` | Corregido |
| BUG-05 | GET /api/prescriptions/{id} | `LazyInitializationException` en items | Corregido |
| BUG-06 | POST /api/auth/login (password incorrecta) | Falso positivo de entorno | No requiere corrección |

---

## 12. El Proyecto — Descripción Técnica

Esta sección describe los aspectos técnicos fundamentales de la implementación de MediSuite en el Avance 2, cubriendo desde la infraestructura de seguridad hasta los mecanismos de concurrencia y persistencia.

### 12.1 Infraestructura de Seguridad

La seguridad del sistema se construye en capas, aplicando el principio de defensa en profundidad.

**Autenticación con JWT:** El endpoint `POST /api/auth/login` valida las credenciales del usuario contra el hash BCrypt almacenado. Al autenticarse correctamente, el servidor genera un token JWT firmado con HS256, cuyo payload incluye el `userId`, el `tenantId` y el rol del usuario. El secreto de firma se valida al arrancar la aplicación para garantizar una longitud mínima de 256 bits (32 bytes UTF-8), cerrando la vulnerabilidad OWASP A02 de secretos débiles.

**Filtro JWT (`JwtAuthenticationFilter`):** Intercepta cada request bajo `/api/**`, extrae el token del header `Authorization: Bearer <token>`, lo verifica y publica el `userId` y `tenantId` en el `SecurityContext` y en el `TenantContext` (ThreadLocal). Esto permite que cualquier capa del sistema acceda al tenant actual sin necesidad de pasarlo como parámetro.

**Rate Limiting (`RateLimitFilter`):** Implementado con la librería Bucket4j. Limita las solicitudes al endpoint de login a 10 intentos por minuto por IP, devolviendo HTTP 429 al superarse el umbral. Esto previene ataques de fuerza bruta contra la autenticación.

**Blacklist de tokens (`JwtBlacklistService`):** Al hacer logout (`POST /api/auth/logout`), el JTI (identificador único) del token se almacena en memoria. El `JwtAuthenticationFilter` consulta la blacklist en cada request, rechazando tokens revocados aunque no hayan expirado aún.

**Bloqueo de cuenta:** Después de 5 intentos fallidos de login, la cuenta se bloquea por 15 minutos. La operación de incremento de intentos fallidos es atómica (`UPDATE ... SET failedLoginAttempts = failedLoginAttempts + 1`) para prevenir race conditions bajo carga concurrente.

**Headers de seguridad OWASP:** El `SecurityConfig` configura automáticamente los headers HTTP de seguridad: `Strict-Transport-Security`, `X-Frame-Options`, `X-Content-Type-Options` y `Referrer-Policy`, protegiendo contra ataques de clickjacking, MIME sniffing y downgrade de protocolo.

### 12.2 Multi-Tenancy con Aislamiento por `tenant_id`

El patrón elegido es *shared database, shared schema* con discriminador por columna. Cada tabla del dominio contiene una columna `tenant_id` (FK a `tenants`) que identifica a qué clínica pertenece cada registro.

El `TenantContext` es una clase de utilidad con un `ThreadLocal<Long>` que almacena el `tenant_id` del usuario autenticado durante el ciclo de vida del request. Los servicios obtienen este valor con `TenantContext.currentTenantId()` antes de cualquier operación de escritura, asignándolo a la entidad antes del `save()`.

Las restricciones de unicidad multi-tenant se implementan como índices `UNIQUE (tenant_id, campo)` para garantizar que, por ejemplo, dos pacientes del mismo DUI en distintas clínicas sean registros válidos e independientes.

### 12.3 Capa de Entidades con `BaseEntity`

Todas las entidades del dominio extienden la clase abstracta `BaseEntity`, que centraliza:

- `id`: clave primaria `BIGSERIAL` generada por la base de datos.
- `tenant`: relación `@ManyToOne` lazy hacia `Tenant`, anotada con `@JsonIgnore` para evitar su serialización.
- `createdAt` / `updatedAt`: marcas de tiempo en `TIMESTAMPTZ` con zona horaria, gestionadas automáticamente con `@PrePersist` y `@PreUpdate`.
- Implementación de `Serializable` con `serialVersionUID`, necesario para la capa de persistencia `.dat`.

Este diseño aplicó el principio DRY (Don't Repeat Yourself): los campos comunes se escriben una sola vez y se heredan, garantizando consistencia entre las 10+ entidades del sistema.

### 12.4 Control de Versiones de Base de Datos con Flyway

El esquema de la base de datos se gestiona completamente con Flyway. Cada cambio estructural es un script SQL versionado que se aplica de forma ordenada e idempotente:

| Versión | Contenido |
|---|---|
| V1 | Columna `must_change_password` en `users` |
| V2 | Renombrar `patients.cif` → `patients.dui` |
| V3 | Restricciones UNIQUE multi-tenant (idempotente con DO-blocks) |
| V4 | Seed de datos demo (5 doctores, 20 pacientes, 30 citas) |
| V5 | Tabla `appointments` con índice parcial anti-colisión de slots |
| V6 | Tablas `medical_records` y `vital_signs` |
| V7 | Refactorización `prescriptions` y `prescription_items` con `order_idx` |

La propiedad `spring.jpa.hibernate.ddl-auto=validate` en producción garantiza que Hibernate nunca modifique el esquema automáticamente; solo Flyway tiene autoridad sobre la estructura de la base de datos.

### 12.5 Módulo de Citas con Control de Concurrencia

El módulo de citas (`Appointment`) gestiona la agenda de los doctores con las siguientes garantías de integridad:

**Detección de conflictos:** El `AppointmentService` verifica antes de crear una cita que el doctor no tenga otra cita en el mismo slot horario y que el estado no sea `CANCELLED`. Este chequeo usa una consulta JPQL atómica dentro de una transacción `@Transactional`.

**Índice único parcial:** La migración V5 define un índice parcial `UNIQUE (doctor_id, scheduled_at) WHERE status <> 'CANCELLED'` que actúa como guardia de última línea a nivel de base de datos, impidiendo duplicados incluso bajo carga concurrente.

**Endpoint de slots disponibles:** `GET /api/appointments/doctors/{doctorId}/slots?date=YYYY-MM-DD` calcula los slots del `available_schedule` (JSONB) del doctor y descuenta los ya ocupados, devolviendo solo los horarios disponibles.

### 12.6 Expediente Médico y Recetas

**MedicalRecord:** Cada paciente tiene un único expediente (`@OneToOne`). El expediente contiene una colección de signos vitales (`VitalSign`) y prescripciones (`Prescription`), accesibles mediante `GET /api/patients/{id}/medical-record`.

**Prescriptions:** Una receta está asociada a un expediente y un doctor. Contiene una lista ordenada de ítems (`PrescriptionItem`) usando `@OrderColumn(name = "order_idx")` para preservar el orden de los medicamentos tal como fueron ingresados. Se usa `List` en lugar de `Set` para permitir el mismo medicamento con distintas dosis y para garantizar el orden de impresión.

**Vista imprimible:** La ruta `/prescriptions/{id}/print` carga la receta completa y aplica estilos `@media print` que ocultan el navbar y el sidebar, presentando un documento A4 limpio listo para imprimir directamente desde el navegador con `window.print()`.

### 12.7 Persistencia Híbrida — PostgreSQL + Archivos `.dat`

El sistema implementa una capa de persistencia dual:

**PostgreSQL** es la fuente de verdad para todos los datos operacionales. Todas las entidades del dominio persisten aquí con integridad referencial y transaccionalidad ACID.

**Archivos `.dat`** son una capa secundaria de respaldo para datos de auditoría y expedientes, basada en serialización binaria de objetos Java. Su propósito es demostrar la persistencia de objetos con las APIs nativas de Java.

La clase genérica `DatFileDao<T extends Serializable>` abstrae la lógica de serialización/deserialización usando `ObjectOutputStream` y `ObjectInputStream` sobre un archivo en el sistema de ficheros. Dos implementaciones concretas la utilizan:

- `AuditLogDatDao`: respalda los registros de `AuditLog` en `data/audit_backup.dat`.
- `MedicalRecordDatDao`: respalda los expedientes médicos en `data/medical_record_backup.dat`.

### 12.8 Concurrencia — Dos Patrones Implementados

**Feature B — Scheduler asíncrono (`AuditBackupScheduler`):** Implementado con `ScheduledExecutorService` mediante `@PostConstruct`. Al arrancar la aplicación, se inicia un hilo daemon llamado `audit-backup` que ejecuta el método `backup()` cada 60 segundos con `scheduleAtFixedRate`. Este hilo consulta todos los `AuditLog` de la base de datos y los serializa al archivo `.dat`, sin bloquear el hilo principal de la aplicación.

**Feature C — Consultas paralelas con `CompletableFuture` (`DashboardMetricsService`):** El endpoint `GET /api/dashboard/metrics` debe devolver cuatro métricas en tiempo real: citas del día, pacientes activos, alertas y recetas emitidas. En lugar de ejecutar las cuatro consultas de forma secuencial (lo que acumularía sus latencias), se lanza cada consulta en un hilo del pool con `CompletableFuture.supplyAsync()` y se espera que las cuatro terminen con `CompletableFuture.allOf(...).join()`. El tiempo de respuesta total es igual al de la consulta más lenta, no a la suma de todas.

### 12.9 Frontend — React con TypeScript y Tailwind CSS

La interfaz de usuario fue desarrollada con React 18, TypeScript y Tailwind CSS, estructurada en los siguientes módulos:

| Ruta | Componente | Descripción |
|---|---|---|
| `/login` | `LoginPage` | Formulario de autenticación con manejo de errores |
| `/dashboard` | `DashboardPage` | 4 tarjetas KPI con métricas en tiempo real |
| `/patients` | `Pacientes` | Listado con búsqueda por DUI o nombre |
| `/doctors` | `Doctores` | CRUD de doctores (solo ADMIN) |
| `/appointments` | `Citas` | Agenda con tabla y modal de nueva cita |
| `/appointments/new` | `CitaNueva` | Selector de doctor, fecha y slot disponible |
| `/patients/:id/record` | `Expediente` | Timeline del expediente médico del paciente |
| `/prescriptions/new` | `Recetas` | Formulario de receta con ítems dinámicos |
| `/prescriptions/:id/print` | `RecetaPrint` | Vista imprimible A4 con `@media print` |
| `/profile` | `Perfil` | Datos del usuario con cambio de contraseña |

La autenticación usa un `AuthContext` (`useAuth`) que persiste el JWT en `localStorage` y lo adjunta automáticamente a cada request mediante `api/client.ts`. Las rutas protegidas se implementan con `ProtectedRoute`, que redirige a `/login` si no hay token válido.

Los estados de carga y error se manejan de forma consistente en todas las páginas mediante los componentes `LoadingSpinner` y `ErrorAlert`, siguiendo el mismo patrón: estado `loading`, estado `error` y estado `data`.

### 12.10 Pruebas y Aseguramiento de Calidad

La estrategia de pruebas del Avance 2 cubre tres niveles:

**Nivel unitario:** Pruebas con JUnit 5 y Mockito sobre los servicios de lógica de negocio (`AuthService`, `PatientService`, `AppointmentService`, `MedicalRecordService`). Las dependencias externas (repositorios, servicios auxiliares) se simulan con mocks, garantizando que cada prueba evalúe únicamente la lógica del servicio bajo prueba.

**Nivel integración:** `PatientRepositoryIT` usa Testcontainers para levantar un contenedor real de PostgreSQL durante las pruebas, verificando que los repositorios JPA funcionan correctamente con la base de datos real. `AuditLogDatDaoIntegrationTest` verifica el ciclo completo de serialización y deserialización del mecanismo de persistencia `.dat`.

**Nivel sistema (manuales):** 20 casos de prueba exploratorios cubriendo los flujos end-to-end de las 10 historias de usuario, ejecutados en el ambiente de desarrollo. Los resultados se documentaron en `docs/fases/casos-de-prueba.md` y los 6 bugs encontrados en `docs/fases/reporte-bugs.md`.

La causa raíz común de 5 de los 6 bugs fue la configuración `spring.jpa.open-in-view=false` combinada con métodos de servicio sin `@Transactional` y relaciones JPA lazy. La corrección sistemática consistió en agregar `@Transactional(readOnly = true)` a todos los métodos de lectura y `@JsonIgnore` a las relaciones lazy que Jackson no debía serializar.

### 12.11 Despliegue con Docker

El sistema se despliega mediante `docker compose` con dos servicios:

- **`backend`**: imagen Java 21 (Eclipse Temurin), construida desde el JAR generado por Maven, expuesta en el puerto 8097. Incluye el volumen `backend_data` montado en `/app/data` para persistir los archivos `.dat` entre reinicios del contenedor.
- **`db`** (opcional en entorno de desarrollo): el ambiente de desarrollo usa la base de datos compartida en Neon (PostgreSQL serverless). En producción o QA local, `docker compose` levanta un contenedor PostgreSQL 16.

Las variables de entorno sensibles (`SPRING_DATASOURCE_URL`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`) se configuran en un archivo `.env` que no se versiona. El archivo `docker-compose.yml` referencia estas variables con la sintaxis `${VARIABLE}`, garantizando que ningún secreto quede expuesto en el repositorio.

---

## 13. Conclusiones

1. **Autenticación y estructura base del frontend:** se implementaron `LoginPage` y `MainLayout`, y se validó su funcionamiento con QA de humo en Chrome y Firefox (EK-01).
2. **Gestión de perfil:** se completó la pantalla de perfil y el flujo de cambio de contraseña, conectado al endpoint `change-password` del backend (D-08).
3. **Experiencia de usuario consistente:** las pantallas manejan de forma uniforme los estados de carga y error (D-09).
4. **Modelo de dominio clínico:** se implementaron `MedicalRecord` y `Prescription` como clases serializables (VT-07).
5. **Decisión de diseño documentada:** se documentó en JavaDoc la diferencia entre lista y conjunto en `Prescription` (VT-05).
6. **Diseño adaptable a móvil:** las pantallas de listas se adaptaron a móviles con Tailwind (MR-08).

---

## 14. Bibliografía



Docker Inc. (s.f.). *Docker documentation*. Docker. https://docs.docker.com

Internet Engineering Task Force. (2015). *JSON Web Token (JWT)* (RFC 7519). https://doi.org/10.17487/RFC7519

PostgreSQL Global Development Group. (2024). *PostgreSQL 16 documentation*. https://www.postgresql.org/docs/16/

Spring. (s.f.). *Spring Boot reference documentation*. VMware. https://docs.spring.io/spring-boot/index.html

Tailwind Labs. (s.f.). *Tailwind CSS documentation*. https://tailwindcss.com/docs
