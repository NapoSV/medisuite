# MediSuite · Guías individuales para la entrega final

**Versión de coordinación:** 5 de octubre de 2026. **Cierre interno confirmado por Héctor:** lunes 19 de octubre de 2026, 23:59 (El Salvador). **Entrega externa indicada por Héctor como fecha del ingeniero:** domingo 25 de octubre de 2026. El PDF final no imprime esa fecha; la guía original del proyecto dice 26/10 y el Avance 1 dice 31/10. Para este plan prevalece la fecha comunicada por Héctor el 5/10.

Este índice y las once guías son instrucciones de trabajo. **No son evidencia de que la Fase B ya esté implementada o probada.** Cada responsable debe aportar código, pruebas, revisión y evidencia real. La guía final del docente está en [AVANCE3_ENTREGA_FINAL.md](../AVANCE3_ENTREGA_FINAL.md); también se conservan [Avance 1](../AVANCE1_MEDISUITE.md) y [Avance 2](../../AVANCE2_MEDISUITE.md).

## 1. Leer esto antes de repartir tareas

1. Cada estudiante abre su guía, comprueba las dependencias y confirma al grupo si puede iniciar.
2. Un PR es una unidad revisable de trabajo. Cada PR debe incluir la prueba del comportamiento y los cambios de documentación de su módulo.
3. La rama integradora es `feature/avance3-fase-b`. Héctor la publicó el 06/10/2026 desde Fase A (`a1b6dc0`); cada estudiante debe ejecutar `git fetch origin` y confirmar el ref remoto antes de crear su rama personal. La existencia de la rama no significa que B2/B4/B5 ni B1 estén mergeados.
4. `develop` sigue siendo la rama de integración general del proyecto y `main` la de entrega. **Todo PR individual de Fase B debe tener como base exacta `feature/avance3-fase-b`;** un PR contra `develop` o `main` se corrige antes de revisión. Nadie hace push directo a `develop` ni a `main`.
5. Las guías dan contratos y pasos concretos. Los fragmentos son ejemplos de implementación, sujetos a compilación, pruebas y revisión de seguridad; no se deben pegar como sustituto de entender las entidades y el esquema reales.
6. Nunca se incluyen contraseñas, tokens, datos clínicos reales ni contenido de `.env` en PR, capturas, comentarios o documentación.

**Puerta de revisión:** Carlos Mario y Bayron usan la [guía de revisión y merge](GUIA_REVISION_Y_MERGE_PR_CARLOS_BAYRON.md). Verifican base, diff, pruebas realmente ejecutadas y dependencias antes de aprobar; una casilla marcada en el cuerpo del PR no es evidencia. GitHub confirmó el 07/10/2026 que sus cuentas con permiso `write` son `@mdealerdude` y `@crislomsu`, respectivamente; las menciones antiguas `@cventura` y `@borellana` deben tratarse como errata de contacto hasta distribuir guías corregidas.

**🔒 Decisiones cerradas por coordinación (09/10/2026):** varias ambigüedades y bugs del Avance 2 ya están resueltos antes de que arranquen las ramas. **Antes de abrir tu rama, haz `git pull origin feature/avance3-fase-b`.** Resumen:

| Qué quedó cerrado | Dónde | Impacto |
|---|---|---|
| Firmas exactas de `MetricsPort`, `ReminderPort`, `ReservationCodePort` | Guía de Carlos §A | B4 arranca sin negociación |
| Prioridad clínica = `NORMAL / URGENTE / EMERGENCIA` (commit `f78e73a`) | Guías de Héctor §C, Zair, William | B6 y dashboard sin bloqueador |
| Contrato TS exacto del `DashboardMetrics` + visibilidad por rol | Guía de William §A | B1b arranca sin esperar aclaraciones de B1 |
| Dueño único de `AppointmentServiceTest.java` = @wflores | Guías de Flores §A y Merino §D | Sin conflictos de merge |
| V11 = Bayron, V12 = Merino, V13+ libre | Guía de Bayron | Numeración Flyway sin negociación |
| Política de reintentos UNIQUE_VIOLATION (3 reintentos, @Transactional per-request) | Guía de Merino §C | B3b arranca sin coordinación |
| Lenguaje visual = Tailwind + componentes del Avance 2 | Guías de William §B y Zair §A | Sin reuniones de diseño |
| Fugas multi-tenant corregidas (`MedicalRecordService`, `PrescriptionService`, `AppointmentService`, `DoctorService`, `PatientService`) + `findByIdAndTenantId` en 5 repos + validación de ítems vacíos en recetas | Backend `feature/avance3-fase-b` | B8 y B9 validan comportamiento ya enforced, no "revelan" bugs |

## 2. Equipo, entregables y dependencias

| ID | Integrante · GitHub · CIF | Guía | Entrega técnica principal | Depende de | PR listo |
|---|---|---|---|---|---|
| B1 | Héctor Napoleón López Ruiz · @hlopez · 2026010132 | [Guía](Hector_Lopez_Ruiz.md) | Dashboard JDBC y contrato seguro de métricas | B2, B4, B5 | 15/10 |
| B2 | Alejandro Antonio Vigil Ramírez · @aavigil · 2026010204 | [Guía](Alejandro_Vigil_Ramirez.md) | Pool JDBC separado, 15 parámetros y pruebas | ninguna | 08/10 |
| B3a | Bayron Alexander Orellana Rojas · @crislomsu · 2026011707 | [Guía](Bayron_Orellana_Rojas.md) | Recordatorios concurrentes persistidos, migración V11 | B2, B4, B5 | 15/10 |
| B6 | Zair Benett Díaz Santos · @zsantos · 2026010796 | [Guía](Zair_Diaz_Santos.md) | Expediente y triaje accesibles, contrato de prioridad | ninguna; coordina B1/B6 frontend | 14/10 |
| B8 | Walter Alejandro Flores Hernández · @wflores · 2026011012 | [Guía](Walter_Flores_Hernandez.md) | Regresión JUnit de citas/pacientes/expediente y validación | B5 para errores JDBC | 16/10 |
| B1b | William Ariel Melgar Rivas · @wmelgar · 2026011736 | [Guía](William_Melgar_Rivas.md) | Dashboard frontend según contrato B1 | B1 | 17/10 |
| B3b | Alejandro Sebastián Merino Ventura · @amerino · 2026020122 | [Guía](Alejandro_Merino_Ventura.md) | Código de reserva concurrente y restricción de BD, V12 | B2, B4, B5; coordina V11 | 15/10 |
| B9 | Erika Alexandra Fuentes Ortiz · @efuentes · 2026011709 | [Guía](Erika_Fuentes_Ortiz.md) | Pruebas de recetas y README ejecutable | ninguna | 14/10 |
| B5 | Walter Amílcar Vásquez Amaya · @wvasquez · 2026010068 | [Guía](Walter_Vasquez_Amaya.md) | Excepciones JDBC, handler y pruebas | ninguna | 08/10 |
| B4 | Carlos Mario Ventura Velásquez · @mdealerdude · 2026011585 | [Guía](Carlos_Ventura_Velasquez.md) | DAO abstracto genérico, puertos y pruebas | B2, B5 | 10/10 |
| B7 | Nicole Nohemy Sánchez Menjívar · @nsanchez · 2026010813 | [Guía](Nicole_Sanchez_Menjivar.md) | Evidencia de cohesión/legibilidad, pruebas y ensamblaje documental | B1 para ejemplo final | 17/10 |

Cada fila incluye trabajo técnico verificable. El documento final, UML, pruebas integrales y defensa se reparten transversalmente en la sección 5. La estimación es por resultados, no por número de archivos ni puntos de rúbrica: la puntuación es grupal y no se suma por persona.

## 3. Calendario real y puertas de integración

| Ola | Fechas reales de 2026 | Resultado comprobable |
|---|---|---|
| Preparación | lun 05/10 | Confirmar rama, responsables, fecha interna y contratos B1/B2/B4/B5. |
| Fundaciones | mar 06/10 a sáb 10/10 | B5 y B2 revisados antes del 08/10; B4 integrado antes del 10/10. |
| Funcionalidad | dom 11/10 a jue 15/10 | B1, B3a, B3b, B6, B9 funcionales con pruebas; contratos frontend/backend firmados. |
| Calidad y documentación | vie 16/10 a sáb 17/10 | B8, B1b, B7; documento final y UML completos para revisión. |
| Integración y contingencia | dom 18/10 | Pruebas cruzadas, base aislada, capturas, revisión de secretos y demo. |
| Cierre interno | lun 19/10, 23:59 | Código, README, documento, UML y evidencias aprobados. |

**Secuencia crítica:** `B2 + B5 → B4 → B1/B3a/B3b → B1b/B7 → cierre`. Zair, Erika y Flores preparan trabajo paralelo. Ninguna dependencia se da por resuelta por un mensaje: se verifica PR mergeado y build de la rama integradora.

## 4. Decisiones técnicas que corrigen el prompt recibido

| Punto del prompt | Hallazgo en el repositorio o PDF | Decisión para el equipo |
|---|---|---|
| “Domingo 19/10/2026” | 19/10/2026 es **lunes**. | Mantener la fecha confirmada, corregir el día. |
| Migración V11 en B3a y Walter Flores | Dos propietarios de un mismo número de Flyway chocan. | Bayron posee V11. Flores valida migración, no crea otra V11. Merino reserva V12 si necesita secuencia/constraint. |
| Nueve anotaciones “del profesor” | El PDF pide JUnit/Mockito y asserts; no enumera anotaciones concretas. | Usarlas cuando aporten valor; nunca crear pruebas `@Disabled` ficticias para sumar etiquetas. |
| Código `R-20261005-00042` | `appointments.reservation_code` es `VARCHAR(10)` en entidad y esquema. | Preservar longitud ≤10 o ampliar columna mediante migración y entidad con revisión. La guía B3b propone código de 10 caracteres respaldado por secuencia de BD. |
| `AtomicLong` para unicidad | Solo coordina hilos de una JVM; el sistema puede tener varias instancias. | La secuencia/constraint en PostgreSQL garantizan unicidad. Un test concurrente demuestra el comportamiento. |
| `@RequestParam tenantId=1` del dashboard | El controlador actual permite elegir tenant y dos conteos son globales. | B1 deriva tenant y rol de autenticación y filtra **todas** las consultas. B1b solo muestra datos que el servidor autoriza. |
| Acceso a expediente/recetas por ID | `MedicalRecordService` usa `findById(patientId)` en dos rutas; `PrescriptionService` busca expediente/receta por ID sin filtro de tenant. | B8 y B9 escriben pruebas con dos tenants y coordinan corrección backend antes de aprobar el cierre. |
| Triaje `NORMAL/URGENTE/EMERGENCIA` | La interfaz usa esos valores; el esquema base muestra `LOW/MEDIUM/HIGH/CRITICAL`. | B6 verifica constraint efectivo y acuerda un único contrato con backend antes de tocar opciones. |
| Reescribir pantallas enteras | Avance 2 ya tiene funcionalidades de receta, signos, roles y errores. | Extraer componentes preservando esos flujos; cada PR demuestra regresión y navegación. |
| Pool Tomcat para JDBC | JPA usa Hikari y Flyway depende del DataSource principal. | B2 mantiene Hikari como primario, crea bean JDBC con `@Qualifier` y documenta parámetros reales, sin secretos. |
| “JDBC puro” | JPA actual no muestra uso explícito de `PreparedStatement`, `ResultSet` y `executeUpdate()`. | B4 y los DAO nuevos deben ejecutarlos realmente, con inserción, consulta y actualización/eliminación demostrables. |
| Publicación automática | Este proyecto opera con Git manual para Codex. | Estas guías contienen comandos para que **cada estudiante** haga sus commits y PR. Codex solo redacta documentación. |

## 5. Matriz de cobertura acumulada: Avances 1, 2 y 3

Marcar una fila como cumplida exige enlace a código, prueba o evidencia de demo en el documento final. Si no hay evidencia, queda pendiente; la presencia de una sección en un PDF anterior no prueba que el flujo funcione hoy.

| Área y fuente | Responsable primario | Revisor cruzado | Prueba o evidencia de aceptación |
|---|---|---|---|
| A1: portada, alcance, OE1 y requisitos HU-001 a HU-008 originales | Nicole | Héctor | Documento final conserva alcance y explica cambios de numeración HU frente a A2. |
| A1: multi-tenant, JWT, roles, bloqueo y registro de usuarios | Vigil | Erika | Dos tenants, cuatro roles y casos 401/403/429; captura sin datos sensibles. |
| A1: aislamiento del expediente y recetas por ID | Flores y Erika | Vigil | Intentos cross-tenant rechazan lectura, creación de signos y prescripción; nunca se confirma por ocultación visual. |
| A1: pacientes, DUI, búsqueda, expediente, triaje | Zair | Walter Flores | Flujo de paciente a expediente y signos; prioridad persistida según constraint. |
| A1: citas, disponibilidad, código único, cancelación con motivo | Bayron y Merino | Walter Flores | Doble reserva rechazada, código sin colisión, cancelación auditable. |
| A1: objetivos, cronograma y límites excluidos | Héctor | Nicole | OE1/OE2/OE3 coherentes, cronograma actualizado con 19/10 interno y 25/10 externo. |
| A2: módulos pacientes/doctores/citas/recetas/impresión | Erika y Zair | William | Demo de crear, consultar, actualizar y caso de error por rol. |
| A2: `.dat`, `ScheduledExecutorService`, JPA y Flyway V1–V10 | Merino y Ventura | Bayron | Explicar diferencia entre respaldo `.dat` y persistencia operacional; validar migraciones en BD descartable. |
| A2: dashboard y consultas paralelas | Héctor y William | Nicole | Contrato de métricas estable, filtros de tenant/rol, estados de carga/error. |
| A2: pruebas manuales y bugs del profesor/Fase A | Walter Flores y Erika | Héctor | Reejecutar casos en `docs/fases/`, añadir resultados fechados, no asumir Fase A por texto. |
| A3: POO, abstracta, interfaces, colecciones y genéricos | Ventura | Nicole | Clases reales + explicación UML + prueba de implementación. |
| A3: polimorfismo de contrato a implementación | Ventura y Héctor | Nicole | `MetricsPort` real y mock/fake usados por el mismo service; llamada verificada por test. |
| A3: capas, JDBC CRUD, try-with-resources, errores SQL | Ventura, Héctor, Bayron, Merino y Vásquez | Vigil | Queries parametrizadas y transacciones correctas; prueba con PostgreSQL aislado. |
| A3: pool y 15 parámetros justificados | Vigil | Ventura | Bean inyectable, valores y razón documentados; test de conexión sin BD compartida. |
| A3: concurrencia con BD y control de colisiones | Bayron y Merino | Walter Flores | Tareas concurrentes leen/escriben BD mediante DataSource; shutdown y fallo observables. |
| A3: cohesión, acoplamiento y antes/después | Nicole | Héctor | Ejemplos citan diff real y efecto sobre dependencias. |
| A3: excepciones de negocio/JDBC y HTTP | Vásquez | Erika | `SQLException` causal preservada, respuesta segura y pruebas de códigos. |
| A3: JUnit/Mockito: service, controller, util, validación, éxito/error | Walter Flores, Erika, Nicole y cada autor | Héctor | Assertions pertinentes, cobertura de controlador y falsos positivos evitados. |
| A3: README, scripts/Compose, URL pública y participación GitHub | Erika y Héctor | Nicole | Arranque reproducible, pruebas, 11 contribuciones reales, acceso público confirmado antes de entrega. |
| A3: documento final de 18 apartados, UML y defensa | Todos, armado Nicole/Héctor | Todos | PDF visualmente revisado, UML actualizado y simulacro de cinco preguntas en cinco minutos. |

**Brechas a decidir con evidencia:** el Avance 1 menciona horarios/consultorios y dashboard; el Avance 2 renumeró HU y describe algunos módulos de manera distinta. El equipo debe mapear historia original → función actual → prueba, y corregir o justificar lo no implementado. No agregar inventario, finanzas o reportes complejos por mera mención histórica sin confirmar que eran alcance obligatorio vigente.

## 6. Documento final: 18 apartados distribuidos

| Apartados del PDF | Autor del borrador | Insumo concreto |
|---|---|
| 1–5: portada, introducción, problema, objetivos, requerimientos | Nicole + Héctor | A1/A2 consolidados; OE3 verificable. |
| 6–7: arquitectura, UML | Carlos + Héctor | Capas actuales, nuevos DAO/puertos, diagrama editable y exportado. |
| 8–9: POO, colecciones y genéricos | Carlos + Merino | `BaseEntity`, `BaseJdbcDao`, `List`, interfaces y especialización real. |
| 10: concurrencia | Bayron + Merino | Worker, conexión por tarea, límite e idempotencia. |
| 11–12: JDBC y DataSource | Vigil + Héctor | SQL parametrizado, CRUD, 15 parámetros y justificación. |
| 13–14: cohesión/acoplamiento y legibilidad | Nicole + Walter Vásquez | Comparaciones antes/después tomadas de PR reales. |
| 15–16: excepciones y pruebas | Walter Vásquez + Walter Flores + Erika | Casos exitosos y errores, reportes de ejecución. |
| 17: Git/GitHub | Héctor + William | Historial real de los 11, URL pública, PR revisados. |
| 18: conclusiones | Cada integrante; Nicole compila | Una conclusión personal basada en trabajo y evidencia. |

El editor no inventa resultados: solicita a cada autor su texto, pruebas, capturas y PR. William y Zair entregan capturas de frontend; Erika prepara README y demo; todos revisan el PDF final y participan en la defensa.

## 7. Evidencias y puertas de calidad

- Un único entorno de pruebas descartable: PostgreSQL local/Testcontainers o base de QA autorizada. La BD compartida de Neon no es banco de pruebas para migraciones destructivas ni cargas concurrentes.
- Compilación backend y frontend, pruebas unitarias/integración aplicables, prueba de migración desde esquema limpio y desde V10, y smoke test de cuatro roles.
- En cada DAO: SQL parametrizado, `tenant_id` explícito, conexión cerrada, transacción cuando se escriban varias tablas, error trazable sin filtrar SQL o datos clínicos al cliente.
- En cada pantalla: carga, vacío, error, permisos, responsividad, navegación y regresión del flujo anterior.
- En cada PR: autoría real, revisión cruzada, rutas exactas, evidencia de pruebas y ningún secreto. Los comandos Git de las guías están dirigidos a los integrantes, no son operaciones realizadas por Codex.
- Si se despliega públicamente la demo, antes de dar el deploy por terminado verificar HTTPS y los seis headers obligatorios (HSTS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy, Permissions-Policy y CSP) en el dominio público; revisar también registro abierto y puertos expuestos. El cierre del documento no equivale a deploy completado.
- En documento final: ruta exacta de clase/test por criterio, capturas legibles, tablas y UML actualizados. Ningún “100 %” se declarará hasta cerrar esta lista con evidencias.

## 8. Puntos de contacto y escalamiento

- Contratos backend/frontend, rama integradora, revisión final: **Héctor @hlopez**.
- Esquema y pool: **Vigil @aavigil** y **Ventura @mdealerdude**.
- Citas, recordatorios y códigos: **Bayron @crislomsu** y **Merino @amerino**.
- Contrato de triaje y pantallas: **Zair @zsantos** y **William @wmelgar**.
- Pruebas y evidencia: **Flores @wflores** y **Erika @efuentes**.
- Excepciones: **Vásquez @wvasquez**. Ensamblaje del documento: **Nicole @nsanchez**.

Al bloquearse, publicar en el grupo el nombre de la tarea, PR requerido, comando exacto, salida completa **sin secretos**, y próximo paso. No cambiar una migración ajena ni reescribir una pantalla de otro responsable para “desbloquearse” sin coordinación.
