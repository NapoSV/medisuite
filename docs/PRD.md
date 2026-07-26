# PRD — MediSuite

> **Product Requirements Document** — Qué se va a construir y para quién.
> Versión: 1.0 · Fecha: 26/07/2026 · Owner: Héctor López (Product Owner)

Este documento consolida el problema, la propuesta de valor, los usuarios objetivo, el alcance funcional y las métricas de éxito de MediSuite. Es la fuente única para toda decisión de producto. La definición técnica (cómo se construye) vive en [TRD.md](TRD.md); el diseño visual en [DISENO_UI_UX.md](DISENO_UI_UX.md); los flujos de pantalla en [APPFLOW.md](APPFLOW.md).

---

## 1. Visión del producto

**MediSuite** es una plataforma web SaaS multi-empresa para la gestión integral de clínicas y hospitales pequeños/medianos, que digitaliza el ciclo completo de atención: **agenda → triaje → consulta → expediente clínico → recetas → inventario**.

El sistema evolucionará a un **SaaS comercial multi-tenant** después de la entrega académica: cada clínica cliente tiene sus propios usuarios, datos, configuración e identidad, aislados entre sí. Esto obliga a construir con criterios de producción real desde el día 1 (multi-tenancy, seguridad OWASP, i18n, escalabilidad).

---

## 2. Problema

Las clínicas y hospitales pequeños/medianos operan hoy con procesos manuales que generan pérdida de valor real:

- **Agendas en papel o Excel** → doble reserva, sin recordatorios, ausentismo del paciente.
- **Expedientes físicos** → se pierden, no se pueden consultar desde otra sede, no hay historial buscable.
- **Triaje sin registro digital** → la enfermera anota en papel; la información no llega al médico ordenada.
- **Recetas manuscritas** → ilegibles, sin historial, difícil auditar tratamientos recurrentes.
- **Sin reportes de ocupación ni de atención** → el administrador no sabe qué médico está subutilizado o qué franjas horarias están vacías.
- **Sin control de insumos** → medicamentos que caducan, faltantes en consulta, sin trazabilidad de compras.

---

## 3. Propuesta de valor

Una plataforma web accesible desde cualquier navegador que:

1. **Elimina la doble reserva** y envía recordatorios (WhatsApp / correo simulado en MVP).
2. **Centraliza el expediente clínico** buscable por CIF o nombre.
3. **Digitaliza el triaje y las recetas** → trazabilidad completa por paciente.
4. **Da reportes de ocupación** al administrador para tomar decisiones.
5. **Controla el inventario** de medicamentos, insumos y activos físicos.
6. **Aísla los datos por clínica (multi-tenant)** — cada organización opera como si fuera la única en el sistema.

---

## 4. Usuarios objetivo (personas)

| Persona | Descripción | Job-to-be-done principal |
|---|---|---|
| **Paciente** | Persona que recibe atención médica. Rango de edad y familiaridad tecnológica amplia. | "Quiero agendar mi cita sin llamar por teléfono y ver mi historial cuando lo necesite." |
| **Médico** | Profesional de la salud con agenda saturada. Tiempo por consulta 15–30 min. | "Quiero llegar al consultorio con el expediente ya cargado y firmar recetas sin salirme del sistema." |
| **Enfermera** | Realiza triaje al inicio de la consulta. Alta rotación de pacientes por turno. | "Quiero registrar signos vitales en segundos y que el médico los vea sin re-preguntar." |
| **Recepcionista** | Primer contacto con el paciente. Gestiona la sala de espera. | "Quiero agendar, confirmar y buscar pacientes rápido, sin cambiar de pantalla." |
| **Administrador de clínica** | Dueño o gerente de la clínica. Necesita datos para decidir. | "Quiero saber qué médicos están subutilizados y qué franjas horarias venden más." |
| **Jefe de almacén** | Controla inventario de medicamentos e insumos. | "Quiero saber qué me falta antes de que se acabe y qué está por caducar." |
| **Contador / Financiero** | Extensión SaaS. Reportes económicos de la clínica. | "Quiero exportar el consumo de insumos por médico y por período." |
| **Super-Admin del SaaS** | Opera la plataforma sobre todos los tenants (rol interno del proveedor). | "Quiero dar de alta una clínica nueva, cambiar su plan y monitorear su uso." |

---

## 5. Alcance funcional

### 5.1. Módulos IN (dentro del alcance del ciclo)

| Módulo | Descripción | Avance |
|---|---|---|
| **Autenticación & Roles** | Login, registro, JWT, control por rol, multi-tenant. | Avance 1 |
| **Gestión de Pacientes** | Alta, búsqueda, edición, expediente básico. | Avance 1 |
| **Gestión de Citas** | Crear, modificar, cancelar, calendario por médico, anti doble-reserva, código de reserva. | Avance 2 |
| **Triaje / Signos vitales** | Registro de peso, talla, presión, temperatura, FC, síntomas, prioridad. | Avance 3 |
| **Expediente clínico** | Historial de consultas, diagnósticos, alergias. | Avance 3 |
| **Recetas** | Emisión, historial, medicamentos, dosis, indicaciones. | Avance 3 |
| **Reportes** | Ocupación de médicos, franjas horarias, atención mensual. | Avance 3 |
| **Inventario, Compras y Activos** | Productos, órdenes de compra, activos físicos, alertas de stock. | Extensión / presentación final |
| **Multi-tenancy** | Aislamiento por `tenant_id`, selector de organización. | Transversal desde Avance 1 |
| **i18n** | Español + inglés (portugués opcional). | Transversal desde Avance 2 |

### 5.2. Módulos OUT (fuera del alcance)

- **Facturación y pagos** de servicios médicos (no habrá pasarela de pago).
- **Integración** con sistemas externos de laboratorio o farmacia.
- **Telemedicina** (videollamadas).
- **Historial de pagos o seguros médicos**.
- **Modificación de datos médicos por parte del paciente** (solo lectura de su expediente).
- **Reportes financieros o de facturación** (más allá de conteos operativos).
- **Firma electrónica certificada** para recetas.
- **Aplicación móvil nativa** (solo web responsive).

---

## 6. Backlog completo de Historias de Usuario

Formato oficial exigido por el profesor:

> **Como** [rol], **quiero** [funcionalidad], **para** [beneficio].

| HU | Prioridad | Historia de Usuario | Avance | Criterios de aceptación |
|---|---|---|---|---|
| **HU-001** | Alta | Como **enfermera/recepcionista**, quiero un formulario para registrar pacientes y consultar su expediente, para atenderlos rápido. | 1 | Registro con nombre, CIF, fecha nacimiento, contacto · Búsqueda por nombre o CIF · Expediente muestra historial de citas, diagnósticos y signos vitales. |
| **HU-002** | Alta | Como **médico**, quiero registrar una receta para llevar el control del tratamiento del paciente. | 3 | Receta almacenada en expediente · Asociada a paciente y médico · Fecha y hora · Medicamentos, dosis y duración. |
| **HU-003** | Alta | Como **paciente**, quiero solicitar una cita médica en línea y elegir horario disponible. | 2 | Muestra horarios disponibles del médico · Confirma con código de reserva · Notifica a paciente y médico · Cancela/reprograma con 24 h de anticipación. |
| **HU-004** | Media | Como **enfermera**, quiero realizar el triaje de pacientes en espera y registrar signos vitales. | 3 | Búsqueda por CIF o nombre · Registra peso, talla, presión, temperatura, FC, síntomas · Asigna prioridad (bajo/medio/alto/crítico) · Se guarda en expediente. |
| **HU-005** | Media | Como **administrador**, quiero gestionar los horarios de los médicos y la disponibilidad de consultorios. | 2 | Crear/modificar/eliminar bloques horarios · Asignar consultorios · Visualizar ocupación en tiempo real. |
| **HU-006** | Baja | Como **recepcionista**, quiero imprimir órdenes de atención para los pacientes. | 3 | PDF con datos de cita y médico · Código de barras. |
| **HU-007** | Alta | Como **usuario del sistema**, quiero iniciar sesión de forma segura con mi rol y clínica. | 1 | Login con email/password · JWT emitido · Redirección según rol · Fallo tras 5 intentos en 15 min. |
| **HU-008** | Alta | Como **administrador de clínica**, quiero crear usuarios (médicos, enfermeras, recepcionistas) con su rol. | 1 | Alta con nombre, email, rol · Password temporal enviado · Usuario obligado a cambiar password en primer login. |
| **HU-009** | Media | Como **médico**, quiero consultar mi agenda del día en una pantalla. | 2 | Lista ordenada por hora · Estado por cita (pendiente/en espera/atendida) · Un clic al expediente. |
| **HU-010** | Media | Como **administrador**, quiero un dashboard con KPIs de la clínica (pacientes registrados, citas del mes, ocupación). | 3 | KPIs en tiempo real · Filtro por período · Gráfico de tendencia. |
| **HU-011** | Baja | Como **jefe de almacén**, quiero registrar productos e ingresar órdenes de compra. | Ext. | Alta de producto con SKU, categoría, unidad · Orden de compra con proveedor y líneas · Estados PENDING/PARTIALLY_RECEIVED/COMPLETE. |
| **HU-012** | Baja | Como **super-admin del SaaS**, quiero dar de alta clínicas nuevas y gestionar su plan. | Ext. | Alta de tenant con slug único · Plan FREE/STARTER/PRO/ENTERPRISE · Suspender/reactivar. |

**HU obligatoria del Avance 1** (mínimo académico): **HU-007** (login) — cubre OE1 "Módulo de autenticación y roles".

---

## 7. Métricas de éxito (SMART)

Definidas para validar en la presentación final del 26/10/2026.

| Métrica | Cómo se mide | Baseline | Meta |
|---|---|---|---|
| Tiempo para agendar una cita | Desde login hasta confirmación con código de reserva | Papel: ~3 min | **< 60 segundos** |
| Doble reserva | Prueba de estrés con 2 usuarios agendando la misma franja | Papel: ocurre | **0 conflictos** |
| Búsqueda de paciente | Tiempo desde CIF hasta expediente en pantalla | Papel: 30 s – 5 min | **< 2 segundos** |
| Cobertura de auditoría | Todo cambio de dato médico queda registrado (usuario + timestamp) | Papel: 0% | **100%** |
| Adopción hipotética | Encuesta a ≥ 3 personas del rubro (enfermera/recepcionista real) | — | **≥ 2 de 3 dirían que sí lo usarían** |
| Uptime del backend en demo | Minutos activos durante la presentación | — | **> 99% de la ventana de demo** |

---

## 8. Validación temprana con usuarios reales

- **Investigación de campo (Nicole + Héctor):** entrevistar por WhatsApp/llamada a **al menos 2 personas** que trabajen en una clínica real.
- **Guion de 5 preguntas:**
  1. ¿Cómo agendan citas hoy?
  2. ¿Qué es lo más molesto de ese proceso?
  3. ¿Qué información necesitan tener a mano cuando llega un paciente?
  4. ¿Han perdido información de pacientes? ¿Cómo?
  5. Si les damos una app gratuita que resuelva X, ¿la usarían?
- **Deadline:** 30/07/2026. Resultados alimentan la sección de Requisitos del entregable académico.

---

## 9. Supuestos y restricciones

- **Licencia académica:** el equipo trabaja con licencia Microsoft 365 estándar (sin Planner Premium).
- **Infraestructura de BD:** PostgreSQL en Neon (tier gratuito compartido). Ver [memoria persistente](../../.claude/projects/c--Users-hlopez-medisuite/memory/neon_connection.md).
- **Idioma:** el sistema es bilingüe (ES/EN) pero el equipo documenta en español; el código y los identificadores están en inglés (ver [ESTANDARES_CODIGO.md](ESTANDARES_CODIGO.md)).
- **Cero datos reales de pacientes:** todo dato en la BD compartida es ficticio (regulación de salud).
- **Roles del equipo:** 11 integrantes, dedicación part-time (materia universitaria). Ver [PLAN_DE_TRABAJO.md](PLAN_DE_TRABAJO.md).

---

## 10. Roadmap de producto

| Fase | Objetivo Específico académico | Módulos | Fecha entrega |
|---|---|---|---|
| **Avance 1** | OE1 · Módulo de autenticación y roles | Auth, gestión de usuarios, gestión de pacientes básica | 10/08/2026 |
| **Avance 2** | OE2 · Gestión de citas | Citas, calendario, disponibilidad, notificaciones | 14/09/2026 |
| **Avance 3** | OE3 · Expediente clínico | Triaje, expediente, recetas, reportes | 19/10/2026 |
| **Presentación final** | Consolidación + extensión SaaS | Inventario, compras, activos físicos, dashboard multi-tenant | 26/10/2026 |

---

## 11. Referencias

- [intruccionesProyecto.md](intruccionesProyecto.md) — proyecto base según el profesor.
- [AVANCE1_DOCUMENTO_ENTREGAR.md](AVANCE1_DOCUMENTO_ENTREGAR.md) — formato oficial de entrega.
- [PLAN_DE_TRABAJO.md](PLAN_DE_TRABAJO.md) — plan de equipo, sprints, roles.
- [TRD.md](TRD.md) — decisiones técnicas.
- [DISENO_UI_UX.md](DISENO_UI_UX.md) — identidad visual y wireframes.
- [APPFLOW.md](APPFLOW.md) — flujos de navegación.
