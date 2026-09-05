# Tareas Avance 2 — Tabla para Microsoft Planner

> Una fila por tarea. **CIF** = número de estudiante universitario del asignado.
> **CSV compatible con Planner:** [`tareas_avance2.csv`](tareas_avance2.csv).

**Nota sobre CIF vs DUI:** en esta tabla, CIF es el ID de estudiante para
identificar al integrante en Planner. En el sistema MediSuite los **pacientes**
se buscan por **DUI** (Documento Único de Identidad de El Salvador), no por CIF.

## Convenciones

- **Bucket:** en qué sprint semanal cae la tarea (Semana 1, 2, 3, Buffer).
- **Prioridad:** Urgent (bloquea), Important (normal), Low (opcional).
- **Progreso inicial:** todas empiezan en "Not started".

---

| # | Tarea | Bucket | Asignado | CIF | Inicio | Fin | Prioridad |
|---|---|---|---|---|---|---|---|
| H-01 | CI Action bloquea commits con IA co-author | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 04/09 | Urgent |
| H-02 | Fix JWT secret longitud ≥ 32 bytes UTF-8 | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 06/09 | Urgent |
| H-03 | Cerrar Swagger UI detrás de rol ADMIN | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 06/09 | Urgent |
| H-05 | Documentar respuestas del profesor | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 03/09 | Important |
| H-04 | Mergear PRs de compañeros (continuo) | Continuo | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 20/09 | Urgent |
| H-06 | Verificar Docker levanta todo el sistema | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 18/09 | 20/09 | Urgent |
| H-07 | Tag v2.0.0-avance2 y push | Buffer | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 22/09 | 26/09 | Urgent |
| V-14 | Exportar a PDF y versionar en raíz | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 20/09 | 20/09 | Urgent |
| **VG-08** | **`JwtAuthenticationFilter` + `TenantContext` (BLOQUEANTE)** | Semana 1 | VIGIL RAMÍREZ ALEJANDRO ANTONIO | 2026010204 | 31/08 | 03/09 | Urgent |
| VG-01 | Rate limiting Bucket4j en /api/auth/login | Semana 1 | VIGIL RAMÍREZ ALEJANDRO ANTONIO | 2026010204 | 03/09 | 06/09 | Urgent |
| VG-04 | GlobalExceptionHandler sin leak de stacktrace | Semana 1 | VIGIL RAMÍREZ ALEJANDRO ANTONIO | 2026010204 | 31/08 | 06/09 | Important |
| VG-03 | CORS parametrizado por env var | Semana 2 | VIGIL RAMÍREZ ALEJANDRO ANTONIO | 2026010204 | 07/09 | 13/09 | Important |
| VG-05 | Security headers OWASP (HSTS, XFO, etc.) | Semana 2 | VIGIL RAMÍREZ ALEJANDRO ANTONIO | 2026010204 | 07/09 | 13/09 | Important |
| VG-06 | Endpoint /api/auth/logout con blacklist JWT | Semana 3 | VIGIL RAMÍREZ ALEJANDRO ANTONIO | 2026010204 | 14/09 | 20/09 | Important |
| VG-07 | Apoyar feature C con 2 queries paralelas | Semana 3 | VÁSQUEZ AMAYA WALTER AMÍLCAR | 2026010068 | 14/09 | 15/09 | Important |
| O-05 | Fix race condition registerFailedAttempt | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 06/09 | Urgent |
| O-01 | Entidad Appointment extends BaseEntity | Semana 2 | ORELLANA ROJAS BAYRON ALEXANDER | 2026011707 | 07/09 | 13/09 | Important |
| O-02 | AppointmentRepository con queries multi-tenant | Semana 2 | ORELLANA ROJAS BAYRON ALEXANDER | 2026011707 | 07/09 | 13/09 | Important |
| O-03 | AppointmentService validaciones + código reserva | Semana 2 | ORELLANA ROJAS BAYRON ALEXANDER | 2026011707 | 07/09 | 13/09 | Important |
| O-04 | Endpoints REST /api/appointments | Semana 2 | ORELLANA ROJAS BAYRON ALEXANDER | 2026011707 | 07/09 | 13/09 | Important |
| O-06 | Endpoint slots disponibles por doctor y fecha | Semana 3 | ORELLANA ROJAS BAYRON ALEXANDER | 2026011707 | 14/09 | 20/09 | Important |
| D-00 | Pantalla `/login` con formulario (email + password + tenantSlug) | Semana 1 | DÍAZ SANTOS ZAIR BENETT | 2026010796 | 31/08 | 05/09 | Urgent |
| D-01 | Setup React Router con rutas protegidas | Semana 1 | DÍAZ SANTOS ZAIR BENETT | 2026010796 | 31/08 | 06/09 | Urgent |
| D-02 | Layout base (navbar + sidebar) | Semana 1 | DÍAZ SANTOS ZAIR BENETT | 2026010796 | 31/08 | 06/09 | Urgent |
| D-03 | Persistencia JWT + hook useAuth | Semana 1 | DÍAZ SANTOS ZAIR BENETT | 2026010796 | 31/08 | 06/09 | Urgent |
| D-04 | Pantalla Dashboard con 4 KPIs de Merino | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 14/09 | 17/09 | Important |
| D-05 | Pantalla Pacientes con búsqueda DUI | Semana 2 | DÍAZ SANTOS ZAIR BENETT | 2026010796 | 07/09 | 13/09 | Important |
| D-06 | Modal crear/editar paciente con validaciones | Semana 3 | VÁSQUEZ AMAYA WALTER AMÍLCAR | 2026010068 | 14/09 | 17/09 | Important |
| D-07 | Pantalla Doctores CRUD (solo ADMIN) | Semana 3 | DÍAZ SANTOS ZAIR BENETT | 2026010796 | 14/09 | 20/09 | Important |
| F-01 | Habilitar Flyway y ddl-auto validate | Semana 1 | FLORES HERNÁNDEZ WALTER ALEJANDRO | 2026011012 | 31/08 | 04/09 | Urgent |
| F-02 | Migraciones V5-V7 (appointments, records, prescriptions) | Semana 2 | FLORES HERNÁNDEZ WALTER ALEJANDRO | 2026011012 | 07/09 | 13/09 | Important |
| F-03 | Constraints multi-tenant en todas las tablas | Semana 3 | FLORES HERNÁNDEZ WALTER ALEJANDRO | 2026011012 | 14/09 | 15/09 | Important |
| F-04 | Seed demo (5 doctores, 20 pacientes, 30 citas) | Semana 3 | FLORES HERNÁNDEZ WALTER ALEJANDRO | 2026011012 | 14/09 | 17/09 | Important |
| F-05 | Tests integración con Testcontainers | Semana 3 | VÁSQUEZ AMAYA WALTER AMÍLCAR | 2026010068 | 14/09 | 19/09 | Important |
| F-06 | Verificar generación de data/*.dat | Semana 3 | VÁSQUEZ AMAYA WALTER AMÍLCAR | 2026010068 | 18/09 | 20/09 | Important |
| MR-01 | Componente reutilizable DataTable | Semana 1 | MELGAR RIVAS WILLIAM ARIEL | 2026011736 | 31/08 | 06/09 | Urgent |
| MR-02 | Pantalla Citas (agenda + tabla) | Semana 2 | MELGAR RIVAS WILLIAM ARIEL | 2026011736 | 07/09 | 13/09 | Important |
| MR-03 | Modal Nueva Cita con slots dinámicos | Semana 2 | MELGAR RIVAS WILLIAM ARIEL | 2026011736 | 07/09 | 13/09 | Important |
| MR-04 | Acciones de cita (cancelar/reprogramar/completar) | Semana 3 | MELGAR RIVAS WILLIAM ARIEL | 2026011736 | 14/09 | 17/09 | Important |
| MR-05 | Pantalla Expediente Paciente con timeline | Semana 3 | MELGAR RIVAS WILLIAM ARIEL | 2026011736 | 14/09 | 18/09 | Important |
| MR-06 | Pantalla Recetas (crear con items dinámicos) | Semana 3 | MELGAR RIVAS WILLIAM ARIEL | 2026011736 | 14/09 | 19/09 | Important |
| MR-07 | Vista imprimible de receta (@media print) | Semana 3 | VÁSQUEZ AMAYA WALTER AMÍLCAR | 2026010068 | 18/09 | 20/09 | Important |
| MR-08 | Verificar responsive Tailwind en móvil | Semana 3 | FUENTES ORTIZ ERIKA ALEXANDRA | 2026011709 | 19/09 | 20/09 | Important |
| M-01 | POC BackupDatSchedulerPoc + DashboardParallelPoc | Semana 1 | MERINO VENTURA ALEJANDRO SEBASTIÁN | 2026020122 | 31/08 | 03/09 | Urgent |
| M-02 | Clase abstracta DatFileDao<T> genérica thread-safe | Semana 2 | MERINO VENTURA ALEJANDRO SEBASTIÁN | 2026020122 | 07/09 | 08/09 | Urgent |
| M-03 | AuditBackupScheduler cada 60s (feature B) | Semana 2 | MERINO VENTURA ALEJANDRO SEBASTIÁN | 2026020122 | 07/09 | 12/09 | Urgent |
| M-04 | Backup diario de expedientes a .dat | Semana 2 | MERINO VENTURA ALEJANDRO SEBASTIÁN | 2026020122 | 10/09 | 13/09 | Important |
| M-05 | DashboardMetricsService con CompletableFuture.allOf | Semana 2 | MERINO VENTURA ALEJANDRO SEBASTIÁN | 2026020122 | 08/09 | 13/09 | Urgent |
| M-06 | DashboardController GET /api/dashboard/metrics | Semana 2 | MERINO VENTURA ALEJANDRO SEBASTIÁN | 2026020122 | 12/09 | 13/09 | Urgent |
| M-07 | Documentar arquitectura híbrida PostgreSQL + .dat | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 14/09 | 17/09 | Important |
| M-08 | Speech técnico 3 min para defensa (B + C) | Buffer | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 22/09 | 25/09 | Urgent |
| EK-01 | Tests unitarios PatientService (5 casos) | Semana 2 | FUENTES ORTIZ ERIKA ALEXANDRA | 2026011709 | 07/09 | 13/09 | Important |
| D-09 | Estados loading/error consistentes | Semana 3 | FUENTES ORTIZ ERIKA ALEXANDRA | 2026011709 | 18/09 | 20/09 | Important |
| PIC-01 | Recolectar fotos de portada de los 11 integrantes | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 04/09 | Urgent |
| V-09 | Sección 10 entradas/salidas por HU | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 14/09 | 16/09 | Important |
| V-10 | Sección 11 entidades del sistema | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 14/09 | 17/09 | Important |
| V-11 | Sección 12 proyecto (11 subpuntos técnicos) | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 14/09 | 19/09 | Important |
| V-12 | Sección 13 conclusiones del avance | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 18/09 | 20/09 | Important |
| V-13 | Sección 14 bibliografía APA | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 18/09 | 20/09 | Important |
| V-01 | Portada con foto y CIF de los 11 | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 06/09 | Urgent |
| V-02 | Objetivo general + 3 objetivos específicos | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 06/09 | Urgent |
| V-03 | Sección 4 tabla equipo Scrum (10 roles) | Semana 1 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 31/08 | 06/09 | Urgent |
| V-04 | Sección 5 roles del sistema con funciones | Semana 2 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 07/09 | 08/09 | Important |
| V-05 | Sección 6 HU-001 a HU-010 con criterios | Semana 2 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 07/09 | 12/09 | Important |
| V-06 | Sección 7 alcances y limitaciones | Semana 2 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 07/09 | 12/09 | Important |
| V-07 | Sección 8 planificación | Semana 2 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 11/09 | 13/09 | Important |
| V-08 | Sección 9 cronograma diagrama Gantt | Semana 3 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 14/09 | 15/09 | Important |
| VT-01 | BaseEntity abstracta (herencia + abstracta) | Semana 1 | VENTURA VELÁSQUEZ CARLOS MARIO | 2026011585 | 31/08 | 04/09 | Urgent |
| VT-02 | Refactorizar 8+ entidades para extender BaseEntity | Semana 1 | VENTURA VELÁSQUEZ CARLOS MARIO | 2026011585 | 31/08 | 06/09 | Urgent |
| VT-03 | Módulo MedicalRecord (entity + repo + service + ctrl) | Semana 2 | VENTURA VELÁSQUEZ CARLOS MARIO | 2026011585 | 07/09 | 12/09 | Important |
| VT-04 | Módulo Prescription con List<PrescriptionItem> | Semana 3 | VENTURA VELÁSQUEZ CARLOS MARIO | 2026011585 | 14/09 | 14/09 | Important |
| VT-05 | JavaDoc justificando List vs Set | Semana 3 | FUENTES ORTIZ ERIKA ALEXANDRA | 2026011709 | 14/09 | 15/09 | Important |
| VT-06 | Fix audit_log rechaza user_id/tenant_id null | Semana 3 | VENTURA VELÁSQUEZ CARLOS MARIO | 2026011585 | 14/09 | 17/09 | Important |
| VT-07 | Serializable en MedicalRecord y Prescription | Semana 3 | FUENTES ORTIZ ERIKA ALEXANDRA | 2026011709 | 14/09 | 15/09 | Important |
| S-01 | Setup JUnit 5 + Mockito + smoke test | Semana 1 | SÁNCHEZ MENJÍVAR NICOLE NOHEMY | 2026010813 | 31/08 | 04/09 | Urgent |
| S-02 | Tests AuthService (5 casos) | Semana 2 | SÁNCHEZ MENJÍVAR NICOLE NOHEMY | 2026010813 | 07/09 | 08/09 | Important |
| S-03 | Tests Appointment/Patient/MedicalRecord Service | Semana 2 | SÁNCHEZ MENJÍVAR NICOLE NOHEMY | 2026010813 | 07/09 | 13/09 | Important |
| S-04 | Test integración backup .dat de Merino | Semana 3 | SÁNCHEZ MENJÍVAR NICOLE NOHEMY | 2026010813 | 14/09 | 17/09 | Important |
| S-05 | Casos manuales HU-001 a HU-010 (mín. 20 casos) | Semana 3 | SÁNCHEZ MENJÍVAR NICOLE NOHEMY | 2026010813 | 14/09 | 19/09 | Important |
| S-06 | Reporte de bugs con priorización | Semana 3 | SÁNCHEZ MENJÍVAR NICOLE NOHEMY | 2026010813 | 18/09 | 20/09 | Important |
| H-08 | Ensayo de defensa (viernes 25/09) | Buffer | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 25/09 | 25/09 | Urgent |
| D-08 | Pantalla Perfil con cambio de password | Semana 3 | FUENTES ORTIZ ERIKA ALEXANDRA | 2026011709 | 14/09 | 20/09 | Important |
| P-01 | Renombrar campo `Patient.cif` → `Patient.dui` + migración Flyway | Semana 1 | FLORES HERNÁNDEZ WALTER ALEJANDRO | 2026011012 | 31/08 | 05/09 | Urgent |
| P-02 | `PatientService` + `PatientController` con búsqueda `?search=<dui\|nombre>` | Semana 2 | ORELLANA ROJAS BAYRON ALEXANDER | 2026011707 | 07/09 | 11/09 | Urgent |
| DR-01 | `DoctorService` + `DoctorController` CRUD | Semana 2 | VÁSQUEZ AMAYA WALTER AMÍLCAR | 2026010068 | 08/09 | 13/09 | Urgent |
| U-01 | `UserController` `/me` + `POST /api/auth/change-password` | Semana 2 | LÓPEZ RUIZ HÉCTOR NAPOLEÓN | 2026010132 | 09/09 | 13/09 | Urgent |
| VT-04b | `PrescriptionController` REST (POST/GET) | Semana 3 | VENTURA VELÁSQUEZ CARLOS MARIO | 2026011585 | 15/09 | 18/09 | Urgent |
| F-07 | Migración `must_change_password` en `users` | Semana 2 | FLORES HERNÁNDEZ WALTER ALEJANDRO | 2026011012 | 09/09 | 11/09 | Urgent |

---

## Cómo cargarlo al Planner

**Opción A (recomendada):** importar el CSV [`tareas_avance2.csv`](tareas_avance2.csv)
usando Power Automate → "List rows CSV" → "Create task in Planner". El CSV ya
trae la columna `Assigned To` con el nombre completo tal como aparece en el
tenant M365; Planner resuelve el user por nombre + correo.

**Opción B (manual):** crear los 4 buckets ("Semana 1", "Semana 2", "Semana 3",
"Buffer") y agregar cada tarea a mano copiando fila por fila.

El correo M365 de Merino coincide con su CIF: `2026020122@cvirtualuees.edu.sv`.
