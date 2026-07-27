# Plan de Trabajo — Proyecto Ciclo II (Programación II)

## Contexto

Equipo de 11 personas (10 activos + 1 pendiente: MERINO VENTURA) del Ciclo 2 de la UEES en la materia de Programación II. Todos con experiencia técnica variada, sin especialización exclusiva por rol.

El proyecto es una **Plataforma de Gestión Clínica multi-empresa (SaaS)** con módulos de citas médicas, expediente clínico, triaje, recetas, inventario, compras y activos físicos. Se entregará al profesor en **3 avances académicos** con un documento progresivo (formato definido en `AVANCE1_DOCUMENTO_ENTREGAR.pdf`) más el código base. En paralelo corren **Retos Semanales** independientes del proyecto.

**Visión de largo plazo:** el sistema **evolucionará a un SaaS comercial multi-empresa** después de la entrega académica — cada clínica cliente tiene sus propios usuarios, datos, configuración e identidad, aislados entre sí. Esto obliga a construir con criterios de producción reales desde el día 1: multi-tenancy, seguridad, escalabilidad, mantenibilidad y valor real de negocio — no solo "se ve bonito y funciona en clase".

**Realismo de alcance:** probablemente no terminemos todas las funcionalidades del SaaS durante el ciclo, pero al cierre debemos tener el **MVP funcional** que resuelve el problema central (ver sección "MVP de Cierre de Ciclo"). Vamos a trabajar al máximo para terminar todo lo posible.

---

## Problema, Valor y Métricas de Éxito

> Antes de codificar, el equipo debe tener claro **qué problema real resolvemos** y **cómo sabemos que lo resolvimos**.

### Problema (dolor de una clínica pequeña/mediana)

- **Agendas en papel o Excel** → doble reserva, no hay recordatorios, pacientes que no se presentan.
- **Expedientes físicos** → se pierden, no se pueden consultar desde otra sede, no hay historial buscable.
- **Triaje sin registro digital** → la enfermera anota en papel y la información no llega al médico ordenada.
- **Recetas manuscritas** → ilegibles, sin historial, difícil auditar tratamientos recurrentes.
- **No hay reportes de ocupación ni de atención** → el administrador no sabe qué médico está subutilizado, qué franjas horarias están vacías.

### Propuesta de valor

Un sistema web accesible desde cualquier navegador que:
1. Elimina la doble reserva y avisa por recordatorios (WhatsApp/correo simulado).
2. Centraliza el expediente clínico buscable por CIF o nombre.
3. Digitaliza triaje y recetas → trazabilidad completa.
4. Da reportes de ocupación al administrador para tomar decisiones.

### Métricas de éxito (más allá de "funciona")

Definir baseline y meta para cada una. Se validan en la presentación final:

| Métrica | Cómo se mide | Meta |
|---------|--------------|------|
| Tiempo para agendar una cita | Desde login hasta confirmación | < 60 segundos |
| Cero doble reserva | Prueba de estrés con 2 usuarios simultáneos | 0 conflictos |
| Búsqueda de paciente | Tiempo desde CIF hasta expediente en pantalla | < 2 segundos |
| Auditoría | Todo cambio de datos médicos queda registrado con usuario + timestamp | 100% cobertura |
| Adopción hipotética | Encuesta a ≥ 3 personas del rubro (enfermera/recepcionista real si es posible) preguntando si lo usarían | ≥ 2 de 3 dicen que sí |

### Validación temprana

- **Investigación de campo (Nicole + Héctor):** entrevistar por WhatsApp/llamada a al menos 2 personas que trabajen en una clínica real (familiar, conocido). 5 preguntas guía:
  1. ¿Cómo agendan citas hoy?
  2. ¿Qué es lo más molesto de ese proceso?
  3. ¿Qué información necesitan tener a mano cuando llega un paciente?
  4. ¿Han perdido información de pacientes? ¿Cómo?
  5. Si les damos una app gratuita que resuelva X, ¿la usarían?
- **Deadline: 25/07/2026.** Resultados van al Sección de Requisitos.

**Fechas clave** (✅ confirmadas el 27/07/2026 — sustituyen a las estimadas originalmente de `intruccionesProyecto.md`):
- Avance 1 → entrega 10/08/2026 (documento + código base del módulo de autenticación)
- Avance 2 → entrega en la **semana del 21 al 26/09/2026** (módulo de citas + blindaje de seguridad)
- Entrega final y defensa → **semana del 26 al 31/10/2026** (sistema completo desplegado)

> **Nota:** el plan operativo vigente de las etapas 2 y 3 es [PLAN_FASES_2_3.md](PLAN_FASES_2_3.md); las secciones de sprints de este documento con las fechas antiguas (~14/09, ~19/10) quedan como registro histórico.

---

## Alcance del Proyecto (Confirmado)

El proyecto seguirá el **alcance ampliado** definido en `descripcionImagenSaaSDental.md`:

- **4 objetivos específicos, 12 HU, 15 entidades.**
- 3 primeros objetivos = los 3 avances académicos que pide el profesor.
- 4º objetivo (**Inventario, Compras y Activos Físicos**) = extensión de valor agregado que se muestra en la presentación final y que consolida la visión SaaS.
- **Dashboard "TOTAL ASSET VALUE"** inspirado en el diseño Zendenta.
- **Soporte multilenguaje (i18n)** desde el diseño: `messages_es.properties`, `messages_en.properties` (portugués opcional).
- **Identidad visual definida:**
  - Paleta: azul profundo `#1A2B4C`, verde esmeralda `#10B981`, naranja `#F59E0B`, gris oscuro `#1E293B`.
  - Tipografía: Inter o Roboto (Google Fonts). 14px cuerpo, 24px títulos.
  - Badges de estado: `PENDING` (naranja), `COMPLETE` (verde), `PARTIALLY_RECEIVED` (azul).
- **Multi-empresa (multi-tenant)** desde el diseño — cada clínica es un tenant aislado. Ver Fase 7 (Arquitectura Multi-Empresa).

**Nombre propuesto: "MediSuite"** o **"ClinicaOS"** — cerrar en primera reunión.

**Roles del sistema (usuarios finales) según el nuevo alcance:**
Médico, Paciente, Enfermera, Administrador de clínica, Recepcionista, **Jefe de Almacén**, **Contador/Financiero**, **Super-Admin del SaaS** (opera todos los tenants).

**Asignaciones técnicas revisadas por el nuevo alcance:**
- **FLORES** (Backend) → Inventario, Compras, Activos Físicos.
- **BAYRON** (Backend / Scrum Master) → Citas y Expediente clínico.
- **MELGAR** (Frontend) → Dashboard, Reportes, gestión de inventario.
- **DIAZ** (Frontend) → Pantallas clínicas (pacientes, triaje, recetas).

**Paquete Java raíz:** `com.sv.grupo.hospital.citas` con sub-paquetes `model.users`, `model.clinical`, `model.inventory`, `model.tenant`, `config`, `service`, `controller.api`, `dto`, `util`, `security`.

---

## Estado del Documento del Avance 1

El archivo `intruccionesProyecto.md` cubre las Secciones 1–11 del formato oficial. **Faltan:**

| Sección | Contenido | Responsable | Fecha |
|---------|-----------|-------------|-------|
| **12** — Estructura de código | Paquetes Java (`com.sv.grupo.clinica.model`, `.dao`, `.service`, `.repository`, `.util`, `.exception`) + al menos 2 clases del modelo (`Paciente.java`, `Medico.java`) | VENTURA CARLOS | 20/07 |
| **13** — Conclusiones | 2–3 líneas por integrante sobre lo aprendido en el Avance 1 | TODOS (recopila Héctor) | 22/07 |
| **14** — Bibliografía APA | Usar referencias de la sección siguiente | NICOLE + HECTOR | 22/07 |

**Formato de entrega:** el documento `.md` se exporta a PDF y se sube al aula virtual antes de la fecha del profesor (~10/08).

---

## Bibliografía Base (APA)

Para la Sección 14 del documento y para citar durante la investigación:

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

### Fuentes Web (APA)
- Oracle. (2024). *Java SE Documentation*. https://docs.oracle.com/en/java/
- Spring. (2024). *Spring Framework Reference Documentation*. https://spring.io/projects/spring-framework
- React. (2024). *React Documentation*. https://react.dev

> **Dónde buscar:** Google Scholar, biblioteca virtual UEES, Z-Library, Internet Archive.

---

## Roles del Equipo — Descripción Ampliada

> Todos son nuevos. Los roles son orientaciones, no compartimentos. Cada persona tiene un **rol principal** y una **cobertura secundaria** (backup) para evitar bloqueos por ausencias.

| Nombre | Rol Scrum | Rol Técnico | Qué hace en la práctica | Backup de |
|--------|-----------|-------------|-------------------------|-----------|
| **HECTOR** | Product Owner | Analista de Negocio | Define qué se construye y en qué orden. Aprueba entregables. Coordina con el profesor. Escribe/revisa HU. Administra repo y Planner. Sube retos semanales. | Business Analyst (Nicole) |
| **ORELLANA BAYRON** | Scrum Master | Backend | Facilita comunicación diaria. Revisa PRs. Reporta bloqueos al PM. Codifica endpoints Spring Boot (Cita, Médico, Paciente). | Backend (Flores) |
| **VIGIL** | Developer | Fullstack | Codifica en backend y frontend según necesidad. Suele ser el que más conoce el sistema completo. Apoya al Scrum Master en revisión de PRs cuando toca frontend. | Backend + Frontend |
| **FLORES** | Developer | Backend | Módulos Expediente, SignoVital, Receta. | Orellana |
| **DIAZ** | Developer | Frontend | Pantallas React de pacientes y citas. Consume APIs. | Melgar |
| **MELGAR** | Developer | Frontend | Dashboard, vistas de médico/enfermera. | Díaz |
| **MERINO** ⚠️ | Developer | BD | DER, scripts SQL, datos de prueba. **Si no confirma antes del 25/07 → responsabilidad pasa a Ventura Carlos + Orellana.** | Ventura Carlos |
| **VENTURA CARLOS** | Architect | Arquitecto | Estructura del proyecto (capas, paquetes). Decisiones tecnológicas. Revisa calidad de código. | BD (backup de Merino) |
| **FUENTES** | QA | Analista Pruebas | Casos de prueba desde criterios de aceptación. Prueba manual. Reporta bugs con pasos reproducibles. | Vásquez |
| **VASQUEZ** | QA | Analista Pruebas | Pruebas de regresión e integración. | Fuentes |
| **SANCHEZ NICOLE** | Business Analyst | Analista de Negocio | Documenta procesos del negocio (flujos de una clínica). Redacción de HU y manual de usuario. Investiga dominio. | PO (Héctor) |

---

## Metodología Scrum — Sprints y Ceremonias

### Sprints

- **Duración: 2 semanas.** Cada avance = 2 sprints.
- **Sprint 1**: 20/07 – 02/08 (Avance 1 – parte 1)
- **Sprint 2**: 03/08 – 16/08 (Avance 1 – parte 2 + entrega)
- **Sprint 3**: 17/08 – 30/08 (Avance 2 – parte 1)
- **Sprint 4**: 31/08 – 13/09 (Avance 2 – parte 2 + entrega)
- **Sprint 5**: 14/09 – 27/09 (Avance 3 – parte 1)
- **Sprint 6**: 28/09 – 11/10 (Avance 3 – parte 2)
- **Sprint 7 (corto)**: 12/10 – 26/10 (Ajustes finales + presentación)

### Ceremonias

| Ceremonia | Frecuencia | Duración | Canal | Quiénes |
|-----------|------------|----------|-------|---------|
| **Sprint Planning** | Inicio de cada sprint (lunes) | 45 min | Teams (videollamada) | Todo el equipo |
| **Daily async standup** | Diario, antes de las 10 AM | 5 min c/u escribiendo | Canal Teams `📢 General` (hilo diario) | Todo el equipo |
| **Weekly sync** | Miércoles | 30 min | Teams (videollamada) | Todo el equipo |
| **Sprint Review** | Fin de cada sprint (viernes) | 45 min | Teams | Todo el equipo + demo del profesor si aplica |
| **Retrospectiva** | Fin de cada sprint (viernes) | 30 min | Teams | Todo el equipo |

**Formato del Daily Async** (cada miembro responde en el hilo del día):
1. ¿Qué hice ayer?
2. ¿Qué haré hoy?
3. ¿Tengo bloqueos? (mencionar a @BAYRON o @HECTOR)

> Si alguien no puede asistir a una ceremonia sync, debe dejar su update en Teams antes.

---

## Definición de Terminado (Definition of Done)

Una tarea/HU se considera **Terminada** cuando cumple **todos** estos criterios:

1. ✅ El código está en la rama `develop` vía Pull Request aprobado por BAYRON o HECTOR
2. ✅ El PR incluye descripción clara y referencia a la HU (`HU-XXX`)
3. ✅ El código compila y corre localmente sin errores
4. ✅ **Unit tests escritos y pasando** para la lógica de negocio nueva (services, validators, utilidades). Ver Fase 6 — Pruebas.
5. ✅ QA (Fuentes o Vásquez) validó los criterios de aceptación de la HU (pruebas manuales)
6. ✅ No hay `TODO`, `console.log`, o código comentado sin justificación
7. ✅ Si se agregaron dependencias → auditoría limpia (sin high/critical)
8. ✅ Si toca endpoints o datos → checklist de Seguridad (Fase 4.9) marcado
9. ✅ La tarea del Planner se movió a `✅ Completado` con enlace al PR

---

## Fase 0 — Setup del Entorno de Desarrollo

> Antes de codificar, cada miembro debe tener este stack instalado y verificado.

### Versiones fijadas (evitar conflictos de versión)

| Herramienta | Versión | Descarga |
|-------------|---------|----------|
| **Java JDK** | 21 LTS | https://adoptium.net |
| **Maven** | 3.9.x (viene con Spring Boot mvnw) | Incluido en el repo |
| **Node.js** | 20 LTS | https://nodejs.org |
| **Git** | Última estable | https://git-scm.com/download/win |
| **Docker Desktop** | Última estable | https://www.docker.com/products/docker-desktop |
| **GitHub Desktop** (opcional) | Última | https://desktop.github.com |

### Docker — Sí, es necesario

**Por qué:**
- **Homologar entornos** entre los 10 miembros del equipo (nadie se pelea con "en mi máquina sí funciona").
- **Levantar la base de datos** (PostgreSQL) sin instalar nada localmente: un solo `docker compose up`.
- **CI/CD desde el día 1** — GitHub Actions usa imágenes Docker.
- **Despliegue del SaaS** — cuando pase a producción, todo es Docker.

**Qué se dockeriza:**
- **Base de datos** (PostgreSQL 16) → `docker-compose.yml` en la raíz del repo.
- **Backend** (Spring Boot) → `backend/Dockerfile` con multi-stage build (Maven build + JRE 21 slim).
- **Frontend** (React + Vite) → `frontend/Dockerfile` con multi-stage build (Node build + Nginx serve).
- **(Futuro)** Redis para cache, MailHog para pruebas de correo.

**Ejemplo mínimo de `docker-compose.yml`:**
```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: clinica_dev
      POSTGRES_USER: ${DB_USERNAME:-clinica_app}
    ports: ["5432:5432"]
    volumes: [db_data:/var/lib/postgresql/data]
volumes:
  db_data:
```

**Regla mínima:** cada miembro debe poder correr `docker compose up -d db` y tener la BD lista sin instalar Postgres localmente.

### IDEs recomendados

> **Cada miembro puede usar el IDE que prefiera** — no imponemos uno. Lo que importa es que el código compile, corra y siga los estándares de `docs/ESTANDARES_CODIGO.md`. Estas son las opciones más comunes:

- **Backend Java**:
  - **IntelliJ IDEA Community** (gratis) — usado por Héctor. Excelente autocompletado para Spring Boot.
  - **VS Code** + extensión "Extension Pack for Java" (Microsoft) — más liviano.
  - **Eclipse** — funciona pero más pesado.
- **Frontend**:
  - **VS Code** + extensiones ESLint, Prettier, Tailwind CSS IntelliSense — recomendado.
  - **WebStorm** (JetBrains, no gratis salvo licencia de estudiante).
- **Base de Datos**:
  - **DBeaver Community** (gratis, funciona con PostgreSQL).
  - **pgAdmin 4** (cliente oficial de PostgreSQL, alternativa a DBeaver).
- **Diagramas**:
  - **draw.io** (gratis, web o desktop) — para DER, UML, arquitectura.

**Nota:** IntelliJ IDEA y VS Code pueden abrir el mismo repo sin problemas. Solo asegurarse de que los archivos de configuración del IDE (`.idea/`, `.vscode/settings.json`) estén en `.gitignore` para no romper el entorno de los demás.

### Motor de Base de Datos

**Decidido: PostgreSQL 16** — recomendación del ingeniero de la materia (stack: Java 21 + Spring Boot / Angular / PostgreSQL / REST API + JSON, la combinación más común en ambientes productivos). Reemplaza la recomendación anterior de MySQL.

### Base de Datos Colaborativa Pública (para pruebas en equipo)

> ✅ **Validado el 22/07/2026.** Se probó extremo a extremo antes de comprometer el desarrollo a esta opción: conexión, creación de rol/BD dedicados y una prueba de humo (tabla + datos ficticios + `SELECT`) tanto en local como en la instancia en la nube.

**Decidido: Neon (free tier), región AWS US East 1 (N. Virginia)** — PostgreSQL administrado, elegido por ser la región con menor latencia esperada desde El Salvador (no hay región AWS en Centroamérica; N. Virginia es el hub con mejor conectividad hacia la región frente a Ohio/Oregon/São Paulo/Asia/Europa).

**Cómo quedó organizado:**
1. Proyecto creado en Neon por HECTOR. **`Neon Auth` quedó desactivado a propósito** — el módulo de autenticación (JWT + RBAC) lo construye el equipo en Spring Security, es parte del entregable académico (OE1), no algo para tercerizar.
2. Rol dedicado `clinica_app` (no el rol dueño del proyecto `neondb_owner`) con su propia base `clinica_dev` — principio de mínimo privilegio, igual que en producción (Fase 4.3).
3. **Host de conexión para la app: el *pooler* de Neon** (el host que termina en `-pooler`), no el host directo — mejor manejo de conexiones concurrentes desde Spring Boot (HikariCP) con varios miembros probando a la vez.
4. **Credenciales:** se comparten **solo por Teams (canal `🗄️ Base de Datos`)**, nunca en el repo. Cada miembro las pega en su propio `.env` local (ya en `.gitignore`).
5. El script de seed con datos ficticios (`database/seed.sql`) se corre una sola vez contra esta instancia; todos apuntan ahí durante desarrollo y pruebas — una sola fuente de verdad.
6. Docker Compose local se mantiene documentado como alternativa offline (ver `SETUP_ENTORNO.md`), pero **no es obligatorio** — se confirmó que se puede trabajar solo con PostgreSQL nativo o directo contra Neon, sin Docker.
7. **Dato ficticio, no real:** confirmado con esta prueba — nadie debe cargar información de pacientes reales en esta instancia pública.

### Verificación del setup

Cada miembro debe poder ejecutar en su máquina:
```bash
java -version    # → 21.x
node -v          # → 20.x
git --version    # → cualquier reciente
psql --version
```

---

## Fase 1 — Microsoft Teams + Planner (Arrancar YA)

### 1.1 Teams — Estructura Detallada

**Equipo (Team):**
- **Nombre:** `Prog II — Clínica 2026`
- **Descripción:** `Equipo del proyecto Ciclo II de Programación II (UEES 2026). Plataforma de gestión de citas médicas. PM: Héctor López. Comunicación, coordinación de sprints y retos semanales.`
- **Privacidad:** Privado (solo miembros invitados)
- **Owner:** Héctor. **Miembros:** los 10 restantes.

> **Nota sobre sub-canales:** Teams no tiene "sub-canales" como concepto. Solo hay **canales** (tipo Estándar, Privado o Compartido) y dentro de cada canal **publicaciones/hilos**. Para nuestro tamaño de equipo (11 personas) **no se necesitan canales privados** — todo lo relevante lo puede ver todo el equipo. Un canal Estándar por área es suficiente.

**Canales a crear** (todos tipo **Estándar**, sin sub-canales):

| # | Nombre del canal | Descripción (copiar tal cual al crearlo) | Tipo | Sub-canal |
|---|------------------|------------------------------------------|------|-----------|
| 1 | `📢 General` (viene por defecto) | Anuncios del PM, daily async standup, decisiones que afectan al equipo. Cada mañana antes de las 10 AM se abre el hilo del día. | Estándar | No |
| 2 | `📋 Avances del Proyecto` | Revisión de entregables por avance académico. Feedback del profesor. Coordinación de los 3 avances. | Estándar | No |
| 3 | `📚 Retos Semanales` | Coordinación de los retos individuales de cada semana. Instrucciones, dudas y evidencias de cada miembro. | Estándar | No |
| 4 | `🔧 Backend` | Discusión técnica de Spring Boot, APIs, entidades, autenticación. Miembros clave: Bayron, Flores, Vigil, Ventura. | Estándar | No |
| 5 | `🎨 Frontend` | Discusión de React, componentes shadcn/Tailwind, integración con APIs. Miembros clave: Díaz, Melgar, Vigil. | Estándar | No |
| 6 | `🗄️ Base de Datos` | Diseño de DER, scripts SQL sobre PostgreSQL. Miembros clave: Merino, Ventura. | Estándar | No |
| 7 | `🧪 QA y Pruebas` | Casos de prueba, reporte de bugs, cobertura de unit tests, pruebas manuales. Miembros clave: Fuentes, Vásquez. | Estándar | No |
| 8 | `📖 Investigación` | Bibliografía, entrevistas de campo con personal de clínica, glosario del dominio, análisis del rubro. Miembros clave: Nicole, Héctor. | Estándar | No |
| 9 | `📌 Ceremonias Scrum` | Actas de sprint planning, sprint review, retrospectivas y sync semanales. Cada acta como una publicación fijada. | Estándar | No |
| 10 | `🔒 Seguridad y Arquitectura` | Decisiones OWASP, revisión de PRs sensibles, gestión de secretos, dependencias, arquitectura del SaaS. Responsable: Ventura (Security Champion). | Estándar | No |

**Total: 10 canales** (el `📢 General` viene incluido al crear el equipo — no se crea aparte).

**Configuración adicional recomendada:**
- **Publicación fijada en `📢 General`:** enlace al repo GitHub + al Planner + a este plan.
- **Pestañas (Tabs) en `📌 Ceremonias Scrum`:** agregar el Planner como pestaña + un OneNote para las actas.
- **Pestaña de Planner en cada canal técnico** (Backend, Frontend, BD, QA) para filtrar tareas por área.

### 1.2 Planner — Estructura Detallada

**Tablero (Plan) principal:**
- **Nombre:** `Proyecto Clínica — Prog II 2026`
- **Descripción:** `Gestión de tareas del proyecto de Ciclo II. Cada tarea tiene responsable, fecha límite y etiqueta por avance. Buckets por estado (Kanban). Etiquetas por avance/tipo. Sin sub-buckets.`
- **Ubicación:** dentro del equipo de Teams `Prog II — Clínica 2026` (Planner queda vinculado).

> **Nota sobre sub-buckets:** Planner **no tiene sub-buckets**. Solo tiene: **Buckets** (columnas de estado) + **Etiquetas de colores** (hasta 25 etiquetas) + **Checklists dentro de cada tarea** (subtareas). Para organizar por avance/módulo usamos **etiquetas**, no buckets extra.

**Buckets a crear** (columnas Kanban por estado):

| # | Nombre del bucket | Descripción / Cuándo mover una tarea aquí |
|---|-------------------|-------------------------------------------|
| 1 | `🟡 Por Hacer` | Backlog. Tareas planificadas pero aún no comenzadas. Aquí caen todas al crearse. |
| 2 | `🔵 En Progreso` | Alguien la tomó y está trabajando activamente. Solo mover cuando de verdad se está ejecutando. |
| 3 | `👀 En Revisión` | PR abierto en GitHub esperando aprobación, o QA validando la HU. |
| 4 | `✅ Completado` | Cumple la Definición de Terminado (DoD). PR mergeado a `develop`. |
| 5 | `🔴 Bloqueado` | Hay un impedimento externo (esperando decisión, dependencia de otra tarea, dependencia externa). Mover aquí obliga a mencionar a Héctor y Bayron. |

**Etiquetas de colores a crear** (Planner permite hasta 25 etiquetas — se pintan en cada tarea):

| # | Nombre de etiqueta | Color sugerido | Cuándo usarla |
|---|-------------------|----------------|---------------|
| 1 | `Avance 1` | Verde | Tareas del entregable del 10/08 |
| 2 | `Avance 2` | Azul | Tareas del entregable del 14/09 |
| 3 | `Avance 3` | Rojo | Tareas del entregable del 19/10 |
| 4 | `Documento` | Amarillo | Redacción del documento académico |
| 5 | `Backend` | Púrpura | Código Java/Spring Boot |
| 6 | `Frontend` | Rosa | Código React |
| 7 | `Base de Datos` | Gris | Scripts SQL, DER, seed |
| 8 | `QA / Pruebas` | Naranja | Unit tests, pruebas manuales, casos de prueba |
| 9 | `Seguridad` | Marrón (o el disponible) | Tareas ligadas a OWASP, secretos, auth |
| 10 | `Investigación` | Turquesa | Bibliografía, entrevistas, glosario |
| 11 | `Setup / Infra` | Cian | Repo, entorno, CI/CD, configuraciones iniciales |

**Regla:** cada tarea debe tener **al menos una etiqueta de avance** (1, 2 o 3) y **al menos una etiqueta de área** (5–10).

### Segundo Tablero: Retos Semanales (separado)

- **Nombre:** `Retos Semanales — Prog II 2026`
- **Descripción:** `Coordinación de los retos individuales de cada semana. Buckets por semana. Sin relación con el proyecto principal.`
- **Buckets** (uno por semana, se agregan sobre la marcha):
  - `Semana 2 — Constructores y Encapsulado (ACTIVA)`
  - `Semana 3 — [tema a definir]`
  - `Semana 4 — ...` etc.
- **Etiquetas simples:** `Individual`, `Coordinador (Héctor)`, `Entregado`.

### 1.3 Tareas del Proyecto — Planner Principal (Avance 1, Avance 2 y Entrega Final)

> Estas son las tareas que se cargan al tablero `Proyecto Clínica — Prog II 2026` (el principal, no el de Retos Semanales). Cada fila = 1 tarjeta en Planner, con la etiqueta de avance correspondiente (`Avance 1`, `Avance 2`, `Avance 3`) más su etiqueta de área.

> Formato de cada tarea: Prioridad, fechas, etiquetas, asignados, notas y **lista de comprobación** — listo para copiar directo a una tarjeta de Planner. Cada checklist se revisó para que sus ítems pertenezcan estrictamente a esa tarea (ej. "actualizar docker-compose.yml" vive en la tarea de Docker, no en la de "decidir motor de BD" — son cosas distintas).

---

## AVANCE 1 — SOLO documento, sin código de MVP (Sprint 1 y 2, entrega ~10/08)

> Reestructurado el 22/07/2026: el Avance 1 es únicamente el documento académico (formato del ingeniero). El código de autenticación, tenant, tests y frontend NO va aquí — se movió a Avance 2. Este es el error que se venía arrastrando en la versión anterior de este plan.

#### Tarea — Cerrar nombre del producto (MediSuite vs ClinicaOS)
- **Prioridad:** Alta
- **Fecha de inicio:** 18/07
- **Fecha de vencimiento:** 20/07
- **Etiquetas:** `Avance 1`, `Investigación`
- **Asignados:** HECTOR, NICOLE
- **Estado:** Pendiente

**Notas:** Decidir el nombre final del producto entre las dos opciones propuestas antes de seguir usándolo en documentos y repo.

**Lista de comprobación:**
- [ ] Revisar las dos opciones de nombre (MediSuite, ClinicaOS) con el equipo
- [ ] Decidir en la reunión de kickoff
- [ ] Confirmar el nombre elegido por escrito en el canal `📢 General`
- [ ] Verificar que el nombre coincida en `README.md` y en el documento de Avance 1

---

#### Tarea — Motor de base de datos: PostgreSQL 16
- **Prioridad:** Alta
- **Fecha de inicio:** 20/07
- **Fecha de vencimiento:** 22/07
- **Etiquetas:** `Avance 1`, `Base de Datos`
- **Asignados:** MERINO, VENTURA (validado por HECTOR)
- **Estado:** ✅ Completado

**Notas:** Decisión de motor de base de datos, siguiendo la recomendación del ingeniero. Esta tarea es solo la **decisión y validación de conectividad** — la actualización de `docker-compose.yml` y demás archivos de infraestructura tiene su propia tarea aparte (ver Avance 2).

**Lista de comprobación:**
- [x] Revisar la recomendación del ingeniero (Java + Spring Boot / Angular / PostgreSQL / REST+JSON)
- [x] Validar PostgreSQL localmente (conexión + datos ficticios de prueba)
- [x] Validar PostgreSQL en la base compartida (Neon)
- [x] Documentar la decisión en este plan

---

#### Tarea — Setup del repositorio GitHub + invitaciones
- **Prioridad:** Alta
- **Fecha de inicio:** 17/07
- **Fecha de vencimiento:** 18/07
- **Etiquetas:** `Avance 1`, `Setup / Infra`
- **Asignados:** HECTOR
- **Estado:** ✅ Completado

**Notas:** Repositorio base del proyecto, con protecciones mínimas desde el primer commit.

**Lista de comprobación:**
- [x] Crear repositorio privado en GitHub
- [x] Invitar a los 10 integrantes restantes
- [x] Agregar `.gitignore` y `.env.example`
- [x] Subir README inicial

---

#### Tarea — Crear `docs/INSTRUCTIVO_GIT.md` y `SETUP_ENTORNO.md`
- **Prioridad:** Media
- **Fecha de inicio:** 17/07
- **Fecha de vencimiento:** 18/07
- **Etiquetas:** `Avance 1`, `Setup / Infra`
- **Asignados:** HECTOR
- **Estado:** ✅ Completado

**Notas:** Documentación base para que todo el equipo tenga el mismo flujo de trabajo desde el día 1.

**Lista de comprobación:**
- [x] Redactar `INSTRUCTIVO_GIT.md` con el flujo de ramas y comandos básicos
- [x] Redactar `SETUP_ENTORNO.md` con la instalación paso a paso
- [x] Compartir el enlace de ambos documentos en Teams

---

#### Tarea — Investigar dominio: flujos de clínica (2 entrevistas)
- **Prioridad:** Alta
- **Fecha de inicio:** 20/07
- **Fecha de vencimiento:** 22/07
- **Etiquetas:** `Avance 1`, `Investigación`
- **Asignados:** NICOLE
- **Estado:** Pendiente

**Notas:** Insumo real de negocio para las HU y la sección de requisitos — evita diseñar "a ciegas".

**Lista de comprobación:**
- [ ] Identificar 2 personas que trabajen en una clínica real (conocidos/familiares)
- [ ] Aplicar las 5 preguntas guía (cómo agendan citas hoy, qué es lo más molesto, qué información necesitan a mano, si han perdido información de pacientes, si usarían una app gratuita)
- [ ] Documentar las respuestas
- [ ] Incorporar los hallazgos a la sección de Requisitos del documento

---

#### Tarea — Diagramar el DER (15 entidades) en draw.io
- **Prioridad:** Alta
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 24/07
- **Etiquetas:** `Avance 1`, `Base de Datos`
- **Asignados:** MERINO (backup: VENTURA)
- **Estado:** Pendiente (DDL ya existe)

**Notas:** El DDL de las 17 tablas (v2 auditada) ya existe (`database/schema.sql`) — esta tarea es **solo el diagrama visual** para el documento, no el diseño desde cero.

**Lista de comprobación:**
- [ ] Usar `database/schema.sql` como referencia de las 17 tablas y sus relaciones
- [ ] Diagramar las entidades y sus llaves foráneas en draw.io
- [ ] Marcar explícitamente `tenant_id` en las tablas que lo llevan
- [ ] Exportar el diagrama como `docs/diagramas/DER.png`
- [ ] Revisar con el equipo el pendiente de `specialties` (ver `docs/fases/ESQUEMA_BASE_DATOS.md`) antes de darlo por final

---

#### Tarea — Diseño de arquitectura multi-tenant (documento)
- **Prioridad:** Media
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 24/07
- **Etiquetas:** `Avance 1`, `Seguridad`
- **Asignados:** VENTURA
- **Estado:** Pendiente

**Notas:** Documento de diseño (texto/diagrama), no implementación — el código del filtro multi-tenant va en Avance 2.

**Lista de comprobación:**
- [ ] Redactar el enfoque de aislamiento elegido (discriminador por columna `tenant_id`)
- [ ] Describir a nivel de diseño el flujo de `TenantContext` + filtro Hibernate
- [ ] Guardar el documento en `docs/fases/MULTI_TENANT.md`
- [ ] Compartir para revisión en el canal `🔒 Seguridad y Arquitectura`

---

#### Tarea — Sección 12 del documento (paquetes Java + 2 clases modelo)
- **Prioridad:** Alta
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 24/07
- **Etiquetas:** `Avance 1`, `Documento`
- **Asignados:** HECTOR
- **Estado:** ✅ Completado

**Notas:** Ya redactada en `intruccionesProyecto.md` — estructura de paquetes, tabla de mapeo entidad→clase, y las 2 clases (`Patient.java`, `Doctor.java`).

**Lista de comprobación:**
- [x] Definir estructura de paquetes Java
- [x] Redactar mapeo de entidades (español → inglés)
- [x] Escribir las 2 clases mínimas requeridas
- [x] Incluir la sección en `intruccionesProyecto.md`

---

#### Tarea — Redactar Conclusiones (Sección 13)
- **Prioridad:** Media
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 25/07
- **Etiquetas:** `Avance 1`, `Documento`
- **Asignados:** TODOS (recopila HECTOR)
- **Estado:** Plantilla lista, falta el aporte de cada quien

**Notas:** Cada integrante aporta su reflexión personal — no se puede redactar por ellos.

**Lista de comprobación:**
- [ ] Cada integrante escribe 2–3 líneas sobre lo aprendido en el Avance 1
- [ ] Enviar el texto a HECTOR por Teams
- [ ] HECTOR consolida las 11 conclusiones en `intruccionesProyecto.md`

---

#### Tarea — Redactar Bibliografía (Sección 14)
- **Prioridad:** Baja
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 25/07
- **Etiquetas:** `Avance 1`, `Investigación`
- **Asignados:** NICOLE, HECTOR
- **Estado:** ✅ Completado

**Notas:** Referencias APA ya incorporadas al documento.

**Lista de comprobación:**
- [x] Confirmar referencias de ingeniería de software, Scrum, Java y bases de datos
- [x] Incluir fuentes web (Oracle, Spring, PostgreSQL)
- [x] Incorporar la sección en `intruccionesProyecto.md`

---

#### Tarea — Validar motor de BD + BD colaborativa (Neon) + roles individuales
- **Prioridad:** Alta
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 22/07
- **Etiquetas:** `Avance 1`, `Base de Datos`, `Seguridad`
- **Asignados:** HECTOR
- **Estado:** ✅ Completado

**Notas:** Validación de punta a punta antes de comprometer desarrollo — ver detalle completo en `docs/MANUAL_AVANCE1_EQUIPO.md`.

**Lista de comprobación:**
- [x] Probar PostgreSQL local (sin depender de Docker)
- [x] Crear proyecto compartido en Neon (región us-east-1, más cercana a El Salvador)
- [x] Crear roles individuales por integrante con privilegios mínimos (sin superusuario)
- [x] Confirmar que un rol individual puede conectarse y leer datos reales

---

#### Tarea — Cada integrante configura DBeaver → Neon y crea su tabla de práctica
- **Prioridad:** Alta
- **Fecha de inicio:** 22/07
- **Fecha de vencimiento:** 26/07
- **Etiquetas:** `Avance 1`, `Base de Datos`, `Individual`
- **Asignados:** TODOS (ver asignación nominal en `docs/MANUAL_AVANCE1_EQUIPO.md`)
- **Estado:** Nueva

**Notas:** Ejercicio práctico de `CREATE TABLE` con llaves foráneas reales, conectados con su propio usuario — instrucciones completas en `docs/MANUAL_AVANCE1_EQUIPO.md`.

**Lista de comprobación:**
- [ ] Instalar DBeaver
- [ ] Configurar la conexión a Neon con tu usuario individual
- [ ] Esperar tu turno según el orden de dependencias del ejercicio
- [ ] Crear tu tabla asignada
- [ ] Confirmar en el canal `🗄️ Base de Datos` que tu tabla quedó creada

---

#### Tarea — Consolidar documento del Avance 1 y exportar a PDF
- **Prioridad:** Alta
- **Fecha de inicio:** 05/08
- **Fecha de vencimiento:** 08/08
- **Etiquetas:** `Avance 1`, `Documento`
- **Asignados:** HECTOR
- **Estado:** Pendiente

**Notas:** Cierre del entregable — con margen de 2 días antes de la fecha oficial del ingeniero (~10/08).

**Lista de comprobación:**
- [ ] Verificar que las 14 secciones del documento estén completas
- [ ] Incluir las conclusiones de los 11 integrantes (Sección 13)
- [ ] Revisar ortografía y formato general
- [ ] Exportar `intruccionesProyecto.md` a PDF
- [ ] Subir el PDF al aula virtual antes de la fecha del ingeniero

---

## AVANCE 2 — fecha aún no confirmada por el ingeniero (referencia interna: Sprint 3–4, ~17/08 a 13/09)

#### Tarea — Setup de Docker Compose para la BD (opcional)
- **Prioridad:** Baja
- **Fecha de inicio:** 17/08
- **Fecha de vencimiento:** 20/08
- **Etiquetas:** `Avance 2`, `Setup / Infra`
- **Asignados:** VENTURA, MERINO
- **Estado:** Opcional, ya no bloqueante

**Notas:** Se confirmó que se puede trabajar sin Docker (Postgres nativo o Neon directo) — esta tarea solo aplica si el equipo decide igual usarlo como entorno offline.

**Lista de comprobación:**
- [ ] Confirmar si el equipo realmente necesita Docker para desarrollo local
- [ ] Si se decide usarlo: actualizar `docker-compose.yml` con la imagen elegida (PostgreSQL 16 — ya está hecho, falta validar en máquinas del equipo)
- [ ] Probar `docker compose up -d db` en al menos 2 máquinas distintas del equipo

---

#### Tarea — Setup del proyecto Spring Boot base
- **Prioridad:** Alta
- **Fecha de inicio:** 17/08
- **Fecha de vencimiento:** 21/08
- **Etiquetas:** `Avance 2`, `Backend`, `Setup / Infra`
- **Asignados:** BAYRON, VENTURA

**Notas:** Proyecto base sobre el que se construye todo el backend del Avance 2.

**Lista de comprobación:**
- [ ] Crear el proyecto Spring Boot (Spring Initializr, Java 21)
- [ ] Crear la estructura de paquetes definida en `ESTANDARES_CODIGO.md`
- [ ] Configurar conexión a PostgreSQL (Neon) en `application.yml`
- [ ] Crear `Dockerfile` multi-stage para el backend
- [ ] Verificar que el proyecto compila y arranca (`./mvnw spring-boot:run`)

---

#### Tarea — Setup del proyecto Frontend (React o Angular — decisión pendiente)
- **Prioridad:** Alta
- **Fecha de inicio:** 17/08
- **Fecha de vencimiento:** 21/08
- **Etiquetas:** `Avance 2`, `Frontend`, `Setup / Infra`
- **Asignados:** DIAZ, MELGAR

**Notas:** Ver nota en `README.md` — la decisión final entre React y Angular debe cerrarse antes de iniciar esta tarea, no durante.

**Lista de comprobación:**
- [ ] Confirmar la decisión final: React o Angular
- [ ] Inicializar el proyecto (Vite si es React, Angular CLI si es Angular)
- [ ] Configurar el sistema de estilos (Tailwind/shadcn o Angular Material)
- [ ] Configurar i18n inicial (es/en)
- [ ] Crear `Dockerfile` multi-stage para el frontend

---

#### Tarea — Recrear/ajustar el esquema real en la BD compartida
- **Prioridad:** Alta
- **Fecha de inicio:** 17/08
- **Fecha de vencimiento:** 22/08
- **Etiquetas:** `Avance 2`, `Base de Datos`
- **Asignados:** MERINO, VENTURA

**Notas:** Se parte de `database/schema.sql` (probado en el ejercicio de práctica del Avance 1), incorporando las mejoras que el equipo decida en el DER oficial.

**Lista de comprobación:**
- [ ] Revisar `database/schema.sql` como punto de partida
- [ ] Resolver el pendiente de `specialties` (tabla separada vs texto libre)
- [ ] Aplicar los ajustes acordados por el equipo al esquema
- [ ] Volver a aplicar `schema.sql` (ajustado) + `seed.sql` en la base compartida

---

#### Tarea — Módulo de autenticación (backend) con JWT y `tenant_id` en claims
- **Prioridad:** Alta
- **Fecha de inicio:** 22/08
- **Fecha de vencimiento:** 30/08
- **Etiquetas:** `Avance 2`, `Backend`, `Seguridad`
- **Asignados:** BAYRON, VIGIL

**Notas:** Corresponde al OE1 del proyecto académico — es una de las entregas que se evalúan directamente.

**Lista de comprobación:**
- [ ] Configurar Spring Security
- [ ] Implementar generación y validación de JWT (incluyendo `tenant_id` en los claims)
- [ ] Implementar hash de contraseñas con BCrypt
- [ ] Implementar RBAC con `@PreAuthorize` por rol
- [ ] Implementar bloqueo tras 5 intentos fallidos de login

---

#### Tarea — Entidad `Tenant` + filtro Hibernate + `TenantContext`
- **Prioridad:** Alta
- **Fecha de inicio:** 22/08
- **Fecha de vencimiento:** 30/08
- **Etiquetas:** `Avance 2`, `Backend`, `Seguridad`
- **Asignados:** VENTURA, BAYRON

**Notas:** Implementación del diseño ya documentado en `docs/fases/MULTI_TENANT.md` (Avance 1).

**Lista de comprobación:**
- [ ] Crear la entidad `Tenant`
- [ ] Implementar `TenantContext` (ThreadLocal)
- [ ] Implementar el filtro Hibernate `@Filter` para `tenant_id`
- [ ] Crear la clase base `TenantAwareEntity`
- [ ] Probar que las consultas filtran automáticamente por tenant

---

#### Tarea — Pantalla de login + registro de pacientes
- **Prioridad:** Alta
- **Fecha de inicio:** 25/08
- **Fecha de vencimiento:** 03/09
- **Etiquetas:** `Avance 2`, `Frontend`
- **Asignados:** DIAZ, MELGAR

**Notas:** HU-001 — depende de que el módulo de autenticación backend ya tenga los endpoints listos.

**Lista de comprobación:**
- [ ] Crear la pantalla de login (consumo del endpoint de autenticación)
- [ ] Crear el formulario de registro de pacientes
- [ ] Validar el formulario con `zod`
- [ ] Agregar soporte i18n a los textos visibles

---

#### Tarea — Setup JUnit 5 + Mockito + H2 en backend
- **Prioridad:** Media
- **Fecha de inicio:** 17/08
- **Fecha de vencimiento:** 22/08
- **Etiquetas:** `Avance 2`, `QA / Pruebas`, `Backend`
- **Asignados:** BAYRON

**Notas:** Base de testing del backend, necesaria antes de escribir los unit tests de autenticación.

**Lista de comprobación:**
- [ ] Confirmar que `spring-boot-starter-test` está en el proyecto
- [ ] Configurar el perfil `test` con H2 en memoria
- [ ] Escribir un test de ejemplo que corra exitosamente
- [ ] Verificar que `./mvnw test` pasa

---

#### Tarea — Setup Vitest + Testing Library en frontend
- **Prioridad:** Media
- **Fecha de inicio:** 17/08
- **Fecha de vencimiento:** 22/08
- **Etiquetas:** `Avance 2`, `QA / Pruebas`, `Frontend`
- **Asignados:** DIAZ

**Notas:** Base de testing del frontend.

**Lista de comprobación:**
- [ ] Instalar Vitest y React/Angular Testing Library según la decisión de stack
- [ ] Configurar `msw` (u otro mock) para simular llamadas a la API
- [ ] Escribir un test de ejemplo que corra exitosamente
- [ ] Verificar que `npm test` pasa

---

#### Tarea — Configurar GitHub Actions (build + tests + audit)
- **Prioridad:** Media
- **Fecha de inicio:** 22/08
- **Fecha de vencimiento:** 25/08
- **Etiquetas:** `Avance 2`, `Setup / Infra`
- **Asignados:** HECTOR, VIGIL

**Notas:** El PR no se puede mergear si algún paso falla — rama `develop` protegida.

**Lista de comprobación:**
- [ ] Crear el workflow que corra `./mvnw test` en el backend
- [ ] Agregar el paso `npm test` en el frontend
- [ ] Agregar `npm audit --audit-level=high`
- [ ] Confirmar que el PR se bloquea si algún paso falla

---

#### Tarea — Unit tests del módulo de autenticación
- **Prioridad:** Alta
- **Fecha de inicio:** 30/08
- **Fecha de vencimiento:** 03/09
- **Etiquetas:** `Avance 2`, `QA / Pruebas`, `Backend`
- **Asignados:** BAYRON, VIGIL

**Notas:** Parte de la Definición de Terminado del módulo de autenticación.

**Lista de comprobación:**
- [ ] Escribir tests del servicio de autenticación (login, hash, JWT)
- [ ] Escribir tests de los validadores de entrada
- [ ] Verificar cobertura ≥ 70% en el service de autenticación

---

#### Tarea — Test de aislamiento multi-tenant (crítico)
- **Prioridad:** Alta
- **Fecha de inicio:** 30/08
- **Fecha de vencimiento:** 05/09
- **Etiquetas:** `Avance 2`, `QA / Pruebas`, `Seguridad`
- **Asignados:** VENTURA, FUENTES

**Notas:** El error más grave posible en un SaaS multi-tenant es que un tenant vea datos de otro — esta prueba no es opcional.

**Lista de comprobación:**
- [ ] Crear 2 tenants de prueba con datos distintos
- [ ] Escribir un test que confirme que el Tenant A no puede leer datos del Tenant B
- [ ] Confirmar que la respuesta es 404 (no 403) para no revelar existencia
- [ ] Documentar el resultado en `docs/fases/MULTI_TENANT.md`

---

#### Tarea — Casos de prueba Auth + HU-001 (manual)
- **Prioridad:** Media
- **Fecha de inicio:** 03/09
- **Fecha de vencimiento:** 08/09
- **Etiquetas:** `Avance 2`, `QA / Pruebas`
- **Asignados:** FUENTES, VASQUEZ

**Notas:** Pruebas manuales complementarias a los unit tests, no un reemplazo de ellos.

**Lista de comprobación:**
- [ ] Redactar casos de prueba a partir de los criterios de aceptación de HU-001
- [ ] Ejecutar las pruebas manuales de registro/login
- [ ] Documentar resultados en `casos-de-prueba.md`
- [ ] Reportar bugs encontrados con pasos de reproducción

---

#### Tarea — Desarrollo del módulo de gestión de citas (backend + frontend)
- **Prioridad:** Alta
- **Fecha de inicio:** 05/09
- **Fecha de vencimiento:** 13/09
- **Etiquetas:** `Avance 2`, `Backend`, `Frontend`
- **Asignados:** ORELLANA (Bayron), DIAZ, MELGAR

**Notas:** HU-003 — el núcleo funcional del Avance 2.

**Lista de comprobación:**
- [ ] Implementar el backend de citas (crear, modificar, cancelar, consultar)
- [ ] Implementar la vista de agenda por médico y fecha (frontend)
- [ ] Validar disponibilidad de horario antes de confirmar una cita
- [ ] Confirmar la cita con un código de reserva

---

#### Tarea — Integración con el calendario y disponibilidad
- **Prioridad:** Media
- **Fecha de inicio:** 08/09
- **Fecha de vencimiento:** 13/09
- **Etiquetas:** `Avance 2`, `Backend`
- **Asignados:** VIGIL, MERINO

**Notas:** Depende de que el módulo de citas ya tenga su modelo base implementado.

**Lista de comprobación:**
- [ ] Definir el modelo de horarios disponibles por médico
- [ ] Implementar la consulta de disponibilidad en tiempo real
- [ ] Validar que no se permitan dobles reservas

---

#### Tarea — Pruebas funcionales de citas
- **Prioridad:** Media
- **Fecha de inicio:** 10/09
- **Fecha de vencimiento:** 13/09
- **Etiquetas:** `Avance 2`, `QA / Pruebas`
- **Asignados:** FUENTES, VASQUEZ

**Notas:** Corresponde a la métrica de éxito "cero doble reserva" definida en este plan.

**Lista de comprobación:**
- [ ] Ejecutar pruebas de creación/cancelación/reprogramación de citas
- [ ] Probar el caso de doble reserva (debe rechazarse)
- [ ] Documentar resultados en `casos-de-prueba.md`

---

## ENTREGA FINAL / PRESENTACIÓN — MVP funcional (referencia interna: Sprint 5–7, ~14/09 a 26/10)

#### Tarea — Módulo de expediente clínico y triaje
- **Prioridad:** Alta
- **Fecha de inicio:** 14/09
- **Fecha de vencimiento:** 27/09
- **Etiquetas:** `Avance 3`, `Backend`, `Frontend`
- **Asignados:** FLORES, DIAZ

**Notas:** HU-004.

**Lista de comprobación:**
- [ ] Implementar la entidad y CRUD de expediente clínico
- [ ] Implementar el registro de signos vitales (triaje)
- [ ] Implementar la clasificación de prioridad (bajo/medio/alto/crítico)
- [ ] Implementar la búsqueda de expediente por CIF/nombre

---

#### Tarea — Recetas médicas
- **Prioridad:** Alta
- **Fecha de inicio:** 14/09
- **Fecha de vencimiento:** 27/09
- **Etiquetas:** `Avance 3`, `Backend`
- **Asignados:** FLORES

**Notas:** HU-002.

**Lista de comprobación:**
- [ ] Implementar el CRUD de recetas
- [ ] Asociar la receta al paciente y al médico
- [ ] Registrar fecha/hora de emisión
- [ ] Incluir medicamentos, dosis y duración del tratamiento

---

#### Tarea — Generación de reportes y estadísticas
- **Prioridad:** Media
- **Fecha de inicio:** 28/09
- **Fecha de vencimiento:** 11/10
- **Etiquetas:** `Avance 3`, `Backend`, `Frontend`
- **Asignados:** ORELLANA (Bayron), MELGAR

**Notas:** Reportes de ocupación y atención para el administrador.

**Lista de comprobación:**
- [ ] Definir los reportes clave (ocupación, atención por médico)
- [ ] Implementar los endpoints de reportes
- [ ] Implementar las vistas de reportes en el frontend

---

#### Tarea — Módulo de inventario básico (deseable)
- **Prioridad:** Baja
- **Fecha de inicio:** 28/09
- **Fecha de vencimiento:** 11/10
- **Etiquetas:** `Avance 3`, `Backend`
- **Asignados:** FLORES

**Notas:** HU-007 — parte del "4º objetivo" de valor agregado, no bloquea el MVP crítico si falta tiempo.

**Lista de comprobación:**
- [ ] Implementar CRUD de productos
- [ ] Implementar control de stock actual/mínimo
- [ ] Implementar alerta de stock bajo (opcional)

---

#### Tarea — Dashboard "TOTAL ASSET VALUE" (deseable)
- **Prioridad:** Baja
- **Fecha de inicio:** 28/09
- **Fecha de vencimiento:** 11/10
- **Etiquetas:** `Avance 3`, `Frontend`
- **Asignados:** MELGAR

**Notas:** Depende de que existan datos de inventario y activos físicos.

**Lista de comprobación:**
- [ ] Diseñar la vista del dashboard
- [ ] Conectar el dashboard a los datos de activos físicos e inventario
- [ ] Mostrar el valor total consolidado

---

#### Tarea — i18n Español/Inglés funcional (deseable)
- **Prioridad:** Baja
- **Fecha de inicio:** 28/09
- **Fecha de vencimiento:** 11/10
- **Etiquetas:** `Avance 3`, `Backend`, `Frontend`
- **Asignados:** DIAZ, MELGAR

**Notas:** Deseable, no crítico — se corta primero si falta tiempo.

**Lista de comprobación:**
- [ ] Completar `messages_es.properties` y `messages_en.properties` (backend)
- [ ] Completar los archivos de traducción del frontend (es/en)
- [ ] Verificar el cambio de idioma en al menos 3 pantallas clave

---

#### Tarea — Deploy real: frontend en Vercel, backend en Render/Railway, BD en Neon
- **Prioridad:** Alta
- **Fecha de inicio:** 12/10
- **Fecha de vencimiento:** 18/10
- **Etiquetas:** `Avance 3`, `Setup / Infra`
- **Asignados:** HECTOR, VIGIL

**Notas:** Neon ya está validado — falta desplegar frontend y backend para la demo en vivo.

**Lista de comprobación:**
- [ ] Desplegar el frontend en Vercel
- [ ] Desplegar el backend en Render o Railway
- [ ] Confirmar que el backend desplegado se conecta a Neon
- [ ] Probar el flujo completo en el ambiente desplegado (no solo local)

---

#### Tarea — Pruebas integrales y ajustes finales
- **Prioridad:** Alta
- **Fecha de inicio:** 12/10
- **Fecha de vencimiento:** 23/10
- **Etiquetas:** `Avance 3`, `QA / Pruebas`
- **Asignados:** TODOS

**Notas:** Última pasada antes de la presentación — prioridad a bugs críticos.

**Lista de comprobación:**
- [ ] Ejecutar pruebas de regresión de todos los módulos
- [ ] Corregir los bugs críticos encontrados
- [ ] Confirmar que las métricas de éxito definidas en este plan se cumplen

---

#### Tarea — Manual de usuario, manual técnico y presentación
- **Prioridad:** Alta
- **Fecha de inicio:** 18/10
- **Fecha de vencimiento:** 26/10
- **Etiquetas:** `Avance 3`, `Documento`
- **Asignados:** TODOS

**Notas:** Entregable final del ciclo.

**Lista de comprobación:**
- [ ] Redactar manual de usuario (PDF)
- [ ] Redactar manual técnico (PDF)
- [ ] Preparar la presentación (PowerPoint)
- [ ] Ensayar la demo del MVP

### 1.4 Planner — Tablero "Retos Semanales"

Buckets por semana: `Semana 2 (actual)`, `Semana 3`, `Semana 4`...

**Semana 2 activa** (Métodos, Constructores y Encapsulado):
- Reto 1–5 (láminas 14–15) → cada miembro individualmente
- Consolidar ZIP + PDF de capturas → HECTOR (coordinador)
- Cada miembro sube su propio PDF con listado del grupo

> Plantilla estándar: al inicio de cada semana, Héctor crea el bucket y asigna tareas basadas en `Retos semanales/SX/instrucciones.md`.

---

## Fase 2 — Repositorio GitHub

### Estructura de carpetas

```
medisuite/
├── backend/                       ← Spring Boot (Java 21)
│   ├── Dockerfile
│   └── src/main/java/com/sv/grupo/hospital/citas/
│       ├── config/                ← Security, Locale, DB, MultiTenant
│       ├── model/
│       │   ├── tenant/            ← Tenant, Plan, Feature
│       │   ├── users/             ← Usuario, Medico, Enfermera, Paciente
│       │   ├── clinical/          ← Cita, Expediente, SignoVital, Receta
│       │   └── inventory/         ← Producto, OrdenCompra, ActivoFisico
│       ├── dao/                   ← Repositories (Spring Data JPA)
│       ├── service/               ← Lógica de negocio
│       ├── controller/api/        ← Endpoints REST
│       ├── dto/                   ← Data Transfer Objects
│       ├── security/              ← JWT, TenantContext, filtros
│       ├── util/                  ← Validators, PDFGenerator, i18n helper
│       └── exception/
│   └── src/main/resources/
│       ├── application.yml
│       ├── messages_es.properties
│       ├── messages_en.properties
│       └── db/migration/          ← Scripts Flyway/Liquibase (opcional)
├── frontend/                      ← React + shadcn + Tailwind + Vite
│   ├── Dockerfile
│   └── src/
│       ├── components/
│       ├── pages/
│       ├── hooks/
│       ├── locales/               ← es.json, en.json
│       └── lib/
├── database/                      ← Scripts SQL (DDL + seed inicial)
├── docs/
│   ├── INSTRUCTIVO_GIT.md
│   ├── SETUP_ENTORNO.md
│   ├── ESTANDARES_CODIGO.md
│   ├── decisiones-profesor.md
│   ├── diagramas/                 ← DER, UML, arquitectura, mockups
│   └── fases/
│       ├── SEGURIDAD.md
│       ├── MULTI_TENANT.md
│       ├── PLAN_PRUEBAS.md
│       └── ...
├── docker-compose.yml             ← BD (+ backend/frontend en Sprint 4+)
├── .env.example
├── .gitignore
└── README.md
```

### Estrategia de ramas (Git Flow simplificado)

```
main          → Solo entregas (merge supervisado por PM)
develop       → Integración diaria vía PR
feature/HU-XXX-descripcion
hotfix/XXX    → Correcciones urgentes en main
```

**Nombres de ramas:** `feature/HU-001-registro-pacientes`, `feature/HU-002-recetas`, etc.

**Flujo del developer:**
1. `git checkout develop && git pull`
2. `git checkout -b feature/HU-XXX-descripcion`
3. Trabajar → commit (formato conventional: `feat:`, `fix:`, `docs:`) → push
4. Abrir PR en GitHub hacia `develop`
5. Esperar aprobación de BAYRON o HECTOR
6. Mover tarea del Planner a `👀 En Revisión` y luego a `✅ Completado`

**Entregas:** PR de `develop` → `main` con tag: `v1.0-avance1`, `v2.0-avance2`, `v3.0-avance3`.

**Protecciones de rama** (en GitHub Settings):
- `main`: requiere PR + aprobación del PM
- `develop`: requiere PR + al menos 1 aprobación

### Instructivo Git para el equipo (contenido de `docs/INSTRUCTIVO_GIT.md`)

**Instalación (una vez):**
- Descargar Git para Windows: https://git-scm.com/download/win
- Alternativa GUI: **GitHub Desktop** — recomendado para nivel básico
- Configurar identidad: `git config --global user.name "Tu Nombre"` + `git config --global user.email "tu@correo.com"`

**Clonar:**
```bash
git clone https://github.com/[owner]/clinica-gestion-citas.git
cd clinica-gestion-citas
```

**Flujo diario:**
```bash
git checkout develop
git pull
git checkout -b feature/HU-001-mi-tarea
# ...trabajas...
git add .
git commit -m "feat: implementar registro de pacientes HU-001"
git push origin feature/HU-001-mi-tarea
# → Abrir Pull Request en GitHub hacia develop
```

**Resolver conflicto (si al hacer pull hay conflictos):**
1. Git te dirá qué archivos tienen conflicto
2. Abrir el archivo, buscar `<<<<<<<` y `=======` y `>>>>>>>`
3. Escoger qué versión conservar
4. `git add [archivo]` y `git commit`
5. Pedir ayuda a BAYRON (Scrum Master) o VIGIL (Fullstack) si no estás seguro

**Ejecutar backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Ejecutar frontend:**
```bash
cd frontend
npm install
npm run dev
```

---

## Fase 3 — Estándares de Código

> Reglas mínimas para que el código de todos se lea igual.

### Idioma
- **Nombres de variables, funciones y clases: en inglés** (`Patient`, `getAppointmentById`, `createUser`)
- **Comentarios y documentación: en español** (para consistencia con las HU y el profesor)
- **Mensajes de commit: en español** con prefijo estándar (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`)

### Java (backend)
- Nombres de clases: `PascalCase` (`PatientService`, `AppointmentController`)
- Métodos y variables: `camelCase` (`findPatientById`, `patientList`)
- Constantes: `UPPER_SNAKE_CASE` (`MAX_RETRIES`)
- 1 clase por archivo. Nombre del archivo = nombre de la clase.
- Getters/setters generados por Lombok (`@Getter`, `@Setter`) o el IDE

### React (frontend)
- Componentes: `PascalCase.jsx` o `.tsx` (`PatientForm.jsx`)
- Hooks personalizados: `useAlgo.js`
- CSS: usar clases de Tailwind, evitar CSS custom salvo casos justificados

### SQL
- Tablas en `snake_case` plural (`patients`, `appointments`)
- Columnas en `snake_case` (`patient_id`, `birth_date`)

---

## Fase 4 — Seguridad (Enfoque SaaS desde el Día 1)

> Este proyecto **evolucionará a un SaaS** que maneja datos médicos sensibles. La seguridad no es un extra, es parte del entregable. Toda decisión de diseño debe considerarla.

### 4.1 Marco de referencia

Basarse en **OWASP Top 10 (2021)** — https://owasp.org/Top10 — como checklist mínimo:
1. Broken Access Control
2. Cryptographic Failures
3. Injection (SQL, XSS)
4. Insecure Design
5. Security Misconfiguration
6. Vulnerable and Outdated Components
7. Identification and Authentication Failures
8. Software and Data Integrity Failures
9. Security Logging and Monitoring Failures
10. Server-Side Request Forgery

Cada HU debe pasar una revisión de las categorías aplicables antes de moverse a `✅ Completado`.

### 4.2 Autenticación y Autorización

- **Contraseñas hasheadas con BCrypt** (Spring Security default, `strength = 12`). **Nunca** en texto plano ni MD5/SHA1.
- **JWT firmado con clave secreta** de mínimo 256 bits, cargada desde variable de entorno (nunca hardcoded).
- **Expiración corta del access token** (15 min) + **refresh token** (7 días).
- **Control de acceso por roles (RBAC)**: `ADMIN`, `MEDICO`, `ENFERMERA`, `RECEPCIONISTA`, `PACIENTE`. Cada endpoint decorado con `@PreAuthorize`.
- **Los pacientes solo ven su propio expediente** (row-level filtering en el service).
- **Bloqueo tras 5 intentos fallidos** de login (contador en BD, desbloqueo por tiempo o admin).
- **Cambio de contraseña obligatorio en el primer login** para usuarios creados por admin.

### 4.3 Datos Sensibles

- **Cifrado en tránsito: HTTPS obligatorio** en producción (Let's Encrypt gratis). En desarrollo local, tolerado HTTP.
- **Cifrado en reposo para PII crítica**: DPI/CIF, teléfono, dirección, historial médico → usar `@Convert` de JPA con AES-256 al menos para los campos más sensibles.
- **Ley de mínimo privilegio en BD**: la app conecta con un usuario `clinica_app` que **NO** es root. Solo permisos `SELECT/INSERT/UPDATE/DELETE` sobre las tablas necesarias, sin `DROP` ni `ALTER`.
- **Backups automáticos** (a diseñar en fase SaaS): mínimo diarios, retención 30 días.

### 4.4 Gestión de Secretos

- **Prohibido subir secretos al repo** (claves JWT, credenciales BD, API keys).
- **Archivo `.env` en el `.gitignore` desde el commit inicial.**
- **Archivo `.env.example`** en el repo con los nombres de variables pero sin valores.
- **Verificación pre-commit**: usar `git-secrets` o revisión manual del diff antes de push.
- Si alguien sube un secreto por error → **rotar la credencial inmediatamente**, no basta con borrar el commit (queda en el historial).

### 4.5 Validación de Entrada (Anti-Injection)

- **Backend:** usar `@Valid` + Bean Validation (`@NotNull`, `@Size`, `@Email`, `@Pattern`) en TODOS los DTOs de entrada.
- **SQL:** solo Spring Data JPA / consultas parametrizadas. **Prohibido concatenar strings** para armar SQL.
- **Frontend:** validación con `zod` antes de enviar al backend (defensa en profundidad, no reemplaza validación del backend).
- **XSS:** React escapa por defecto en JSX, pero **nunca** usar `dangerouslySetInnerHTML` con contenido de usuario.
- **CSRF:** si se usan cookies para sesión → tokens CSRF. Con JWT en `Authorization` header → riesgo mitigado.

### 4.6 Configuración Segura

- **CORS restrictivo**: solo permitir el dominio del frontend, no `*`.
- **Headers de seguridad** en respuestas: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Strict-Transport-Security` (en HTTPS), `Content-Security-Policy`.
- **Rate limiting** en endpoints sensibles: login, recuperación de contraseña, registro. Usar Bucket4j o similar.
- **Deshabilitar** endpoints de debug/actuator en producción (o protegerlos con auth admin).
- **Mensajes de error genéricos** al usuario final (no exponer stack traces ni queries).

### 4.7 Dependencias de Terceros

**Proceso obligatorio antes de agregar cualquier dep (npm o Maven):**
1. Revisar en https://socket.dev (npm) o https://ossindex.sonatype.org (Maven)
2. Verificar mantenimiento activo (< 6 meses desde último release)
3. Verificar licencia compatible (MIT, Apache 2.0 OK; GPL requiere revisión)
4. Instalar → correr auditoría:
   - Frontend: `npm audit --audit-level=high`
   - Backend: `./mvnw org.owasp:dependency-check-maven:check`
5. Si hay high/critical → resolver antes de mergear.

**Paquetes ya autorizados (npm):** `react`, `react-dom`, `react-router-dom`, `axios`, `zod`, `tailwindcss`, `shadcn/ui` (via CLI oficial).

**Automatización:** activar **Dependabot** en GitHub Settings → alertas semanales de vulnerabilidades.

### 4.8 Auditoría y Trazabilidad

- **Tabla `audit_log`** en BD: cada operación que crea/modifica/elimina datos médicos registra: `usuario_id`, `accion`, `entidad`, `entidad_id`, `timestamp`, `datos_antes`, `datos_despues`, `ip`.
- **Logs de aplicación:** SLF4J + Logback. Nivel `INFO` en prod, sin datos sensibles en el log (nunca loguear contraseñas ni tokens completos).
- **Logs de acceso:** cada login exitoso y fallido queda registrado con IP y user-agent.
- **Retención mínima:** 90 días.

### 4.9 Checklist de Seguridad por PR

Todo PR que toque endpoints o datos debe marcar:
- [ ] `@PreAuthorize` con el rol correcto en el endpoint
- [ ] DTO de entrada con Bean Validation
- [ ] Sin secretos hardcoded
- [ ] Sin logs de datos sensibles
- [ ] Si toca dependencias → auditoría limpia adjunta

### 4.10 Responsable de Seguridad

**Rol adicional para VENTURA CARLOS (Architect):** revisar el checklist 4.9 en cada PR sensible y mantener el documento `docs/fases/SEGURIDAD.md` actualizado con las decisiones tomadas.

---

## Fase 4B — Arquitectura Multi-Empresa (SaaS Multi-Tenant)

> El sistema se diseña como un SaaS donde **cada clínica cliente es un "tenant" completamente aislado**: sus usuarios, pacientes, citas, expedientes, inventario y configuración son suyos y de nadie más. Esto tiene implicaciones profundas en el diseño, y hay que resolverlas desde el día 1 para no reescribir después.

### 4B.1 Modelo de aislamiento

Existen tres estrategias comunes de multi-tenancy. Nuestra decisión:

| Estrategia | Aislamiento | Costo | Recomendado |
|------------|-------------|-------|-------------|
| **Discriminador por columna** (`tenant_id` en cada tabla) | Medio | Bajo | ✅ **Elegida** para MVP |
| **Schema por tenant** (schemas separados en la misma BD) | Alto | Medio | Fase futura si hay clientes grandes |
| **BD por tenant** (una BD por cliente) | Máximo | Alto | Solo para clientes enterprise/regulados |

**Decisión:** `tenant_id` en TODAS las tablas de dominio. Es reversible (migrable a schemas después si crece).

### 4B.2 Entidad `Tenant` (nueva)

Crear tabla `tenants` con:
- `id` (BIGINT PK)
- `slug` (único, `disna-clinica`) → usado en subdominios `disna-clinica.medisuite.app`
- `nombre_comercial`, `razon_social`, `nit`, `pais`, `zona_horaria`, `idioma_default`
- `plan` (FREE, STARTER, PRO, ENTERPRISE)
- `estado` (TRIAL, ACTIVE, SUSPENDED, CANCELLED)
- `fecha_creacion`, `fecha_expiracion_trial`, `logo_url`, `color_primario`
- `max_usuarios`, `max_pacientes`, `max_medicos` (según plan)

### 4B.3 Enforcement de aislamiento (crítico)

**El error más grave posible en un SaaS multi-tenant es que un cliente vea los datos de otro.** Prevención:

1. **`TenantContext` (ThreadLocal):** al iniciar cada request HTTP, un filtro extrae el `tenant_id` del JWT y lo coloca en el contexto. Todas las consultas lo leen desde ahí.
2. **Hibernate Filter con `@Filter`:** filtro automático que agrega `WHERE tenant_id = :tenantId` a TODAS las consultas de entidades de dominio.
3. **Base entity `TenantAwareEntity`:** clase abstracta con `@Column tenantId` que heredan todas las entidades excepto `Tenant`, `User` (con matices) y `AuditLog`.
4. **Nunca aceptar `tenant_id` desde el cliente** (frontend) — siempre desde el token JWT del usuario autenticado.
5. **Tests obligatorios de aislamiento:** por cada endpoint, un test que verifique que un usuario del Tenant A no puede leer/escribir datos del Tenant B (devuelve 404, no 403, para no revelar existencia).

### 4B.4 Autenticación y roles con tenants

- **JWT contiene:** `user_id`, `tenant_id`, `roles[]`, `exp`.
- **Login por slug de tenant:** endpoint `/api/{tenant-slug}/auth/login` o subdominio `disna.medisuite.app/login`.
- **Roles jerárquicos:**
  - `SUPER_ADMIN` (nuestra empresa) — puede ver/administrar todos los tenants. Fuera del filtro multi-tenant.
  - `TENANT_ADMIN` (cliente) — gestiona su propio tenant (usuarios, config, plan).
  - `MEDICO`, `ENFERMERA`, `RECEPCIONISTA`, `PACIENTE`, `JEFE_ALMACEN`, `CONTADOR` — todos scoped al tenant.

### 4B.5 Onboarding de nuevos tenants (self-service)

Flujo cuando alguien crea una nueva clínica:
1. Formulario público: nombre, correo del admin, país, plan elegido.
2. Se crea el `Tenant` (estado = TRIAL, 14 días).
3. Se crea el usuario `TENANT_ADMIN` inicial con contraseña temporal.
4. Correo de verificación al admin.
5. Al primer login, el admin configura horarios, especialidades, moneda, idioma default.

### 4B.6 Personalización por tenant

- **Marca:** logo (subido por el admin), color primario del tema.
- **Idioma default:** cada tenant elige (ES, EN, PT).
- **Zona horaria:** cada tenant tiene su tz; las citas se muestran en la tz del tenant.
- **Moneda:** para inventario y activos (`USD`, `SVC`, `GTQ`, etc.).
- **Catálogos maestros:** especialidades médicas, categorías de productos, pueden ser globales (compartidos) o custom por tenant.

### 4B.7 Límites, planes y feature flags

- **Enforcement de límites del plan:** al crear un médico, contar los actuales; si supera el límite del plan → 402 Payment Required.
- **Feature flags por plan:** el módulo de inventario podría estar solo en plan PRO+; el módulo de reportes avanzados solo en ENTERPRISE.
- **Tabla `tenant_features`** o un simple mapa por plan.

### 4B.8 Aislamiento operacional

- **Backups por tenant:** al exportar backup, filtrar por `tenant_id`. Al restaurar, poder restaurar solo un tenant.
- **Suspensión:** un tenant `SUSPENDED` no puede loguear pero sus datos se preservan.
- **Right to be forgotten (GDPR-like):** endpoint admin para borrar todos los datos de un tenant (soft-delete inicial, purge después de N días).
- **Auditoría filtrada:** `audit_log` incluye `tenant_id`; los admins de un tenant solo ven auditoría de su tenant.

### 4B.9 Rate limiting por tenant

- Bucket4j configurado con clave = `tenant_id` (no por IP).
- Cada plan tiene su cuota de requests/minuto.

### 4B.10 Métricas y observabilidad

- Todas las métricas del sistema (Prometheus futuro) etiquetadas con `tenant_id`.
- Dashboard de "salud" por tenant para el super-admin.

### 4B.11 Impacto inmediato en el proyecto académico

Aunque en el ciclo académico probablemente operemos con **1–2 tenants de demo**, todo el código debe estar preparado:
- Todas las entidades de dominio heredan de `TenantAwareEntity`.
- El JWT ya incluye `tenant_id`.
- El filtro Hibernate ya está activo.
- Existe un test de aislamiento por cada módulo entregado.

Esto **no** duplica el trabajo — solo obliga a hacerlo bien desde el principio.

---

## Fase 5 — Pruebas (Unitarias, Integración, Manuales)

> Toda la lógica no trivial debe estar cubierta por pruebas automatizadas. QA no reemplaza los unit tests; los complementa.

### 5.1 Pirámide de pruebas

```
        /\      E2E (pocos, manuales por QA)
       /  \
      /----\    Integración (endpoints REST con MockMvc)
     /      \
    /--------\  Unit tests (mayoría — services, validators, utilidades)
```

### 5.2 Backend — JUnit 5 + Mockito

- **Framework:** JUnit 5 (`spring-boot-starter-test` ya lo incluye)
- **Mocks:** Mockito (`@Mock`, `@InjectMocks`)
- **Integración de endpoints:** `MockMvc` con `@SpringBootTest`
- **Base de datos en pruebas:** H2 en memoria (perfil `test`) — nunca contra la BD real
- **Ubicación:** `backend/src/test/java/com/sv/grupo/clinica/...` — espejo de la estructura de `main`
- **Nombramiento:** `PatientServiceTest.java`, `AppointmentControllerIntegrationTest.java`
- **Cobertura mínima:** 70% en `service/` y `util/`. Endpoints públicos deben tener al menos 1 test de integración por método HTTP.
- **Ejecución:** `./mvnw test` (debe pasar antes de aprobar cualquier PR)

**Ejemplo de test unitario esperado:**
```java
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock private PatientRepository repo;
    @InjectMocks private PatientService service;

    @Test
    void findByCif_returnsPatient_whenExists() {
        // given
        when(repo.findByCif("2026011012")).thenReturn(Optional.of(new Patient()));
        // when
        var result = service.findByCif("2026011012");
        // then
        assertThat(result).isPresent();
    }
}
```

### 5.3 Frontend — Vitest + React Testing Library

- **Framework:** Vitest (más rápido que Jest, integrado con Vite)
- **Testing lib:** `@testing-library/react` para renderizar componentes y simular interacciones
- **Mocks de API:** `msw` (Mock Service Worker) para simular endpoints
- **Ubicación:** al lado de cada componente (`PatientForm.test.jsx`) o en `frontend/src/__tests__/`
- **Qué probar:**
  - Componentes de formulario: que renderizan, que validan, que llaman al submit correcto
  - Hooks personalizados
  - **No** probar librerías (React, shadcn) — asumir que funcionan
- **Ejecución:** `npm test`

### 5.4 Ejecución automatizada (CI)

- **GitHub Actions**: workflow que corre en cada PR:
  1. Backend: `./mvnw test`
  2. Frontend: `npm test`
  3. `npm audit --audit-level=high`
- **Si falla algún paso → el PR no se puede mergear.** (Rama `develop` protegida)

### 5.5 Pruebas manuales (QA)

Responsables: **FUENTES + VASQUEZ**
- Casos de prueba en `docs/fases/casos-de-prueba.md` (formato tabla por HU)
- Pruebas de humo antes de cada entrega de avance
- Pruebas de regresión al inicio de cada sprint (probar HU de sprints anteriores)
- Reporte de bugs con: severidad, pasos para reproducir, evidencia (captura), PR de fix

### 5.6 Responsabilidad de escribir tests

- **Cada developer escribe los unit tests de su propio código** — parte del DoD.
- **QA no escribe unit tests**, pero sí revisa que existan y sean significativos.
- **Bayron (Scrum Master)** verifica cobertura al aprobar PRs.

---

## Fase 6 — Documentación por Fase (en `docs/fases/`)

### Fase de Planificación y Requisitos
Responsables: **NICOLE + HECTOR**
- `ERS.md` — Especificación de Requisitos del Software (amplía las HU)
- `casos-de-uso.md` — Diagramas de casos de uso por actor
- `glosario.md` — Términos del dominio (expediente, triaje, CIF...)
- `flujos-de-negocio.md` — Diagrama de flujo principal (cita → atención)

### Fase de Diseño
Responsables: **VENTURA CARLOS + MERINO + DIAZ/MELGAR**
- `DER.png` — Diagrama Entidad-Relación (draw.io)
- `diagrama-clases.png` — UML de clases del modelo
- `arquitectura.png` — Diagrama de capas (Frontend → API → Service → Repository → BD)
- `wireframes/` — Mockups de pantallas principales

### Fase de Desarrollo
Responsables: **VIGIL + VENTURA CARLOS**
- `INSTRUCTIVO_GIT.md` (ya cubierto arriba)
- `SETUP_ENTORNO.md` — Instrucciones detalladas de instalación
- `ESTANDARES_CODIGO.md` — Convenciones (referenciado arriba)
- `API.md` — Endpoints REST documentados

### Fase de Pruebas
Responsables: **FUENTES + VASQUEZ**
- `plan-de-pruebas.md` — Estrategia general
- `casos-de-prueba.md` — Tabla por HU (pasos, datos, esperado vs real)
- `reporte-bugs.md` — Log de bugs (severidad, estado, PR de fix)

### Entrega Final
Responsables: **TODOS**
- Manual de usuario (PDF)
- Manual técnico (PDF)
- Presentación (PowerPoint)

---

## Interacción con el Profesor de la Materia

- **Héctor (PM) actúa como enlace principal del equipo con el profesor** — consolida y canaliza. No es una autoridad sobre los demás, solo un punto único de contacto para evitar mensajes duplicados.
- **Preguntas del equipo al profesor:** primero se discuten en el canal `📢 General` de Teams. Si nadie del equipo tiene la respuesta, se lleva al aula virtual/profesor a través de Héctor.
- **Feedback del profesor sobre entregas:** se publica en `📋 Avances del Proyecto` y se crean tareas de ajuste en el Planner.
- **Registro de decisiones del profesor:** guardarlas en `docs/decisiones-profesor.md` con fecha y contexto (evita repetir la misma pregunta más adelante).
- **Cada miembro puede escribir al profesor por su cuenta** para dudas individuales; las decisiones que afectan al equipo pasan por Héctor.

---

## Riesgos y Mitigación

| # | Riesgo | Impacto | Probabilidad | Mitigación |
|---|--------|---------|--------------|------------|
| 1 | MERINO no confirma participación | Alto (BD sin dueño) | Alta | Deadline 25/07. Contingencia: Ventura + Bayron asumen |
| 2 | Miembro no sabe Git y bloquea PRs | Medio | Alta | Instructivo + GitHub Desktop + sesión de 30 min de Bayron/Vigil |
| 3 | Cambios de scope del profesor | Alto | Media | Documentar todo el feedback. Renegociar deadlines si aplica |
| 4 | Retos semanales bloquean el proyecto | Medio | Alta | Bucket separado en Planner. Retos son individuales, proyecto es en equipo |
| 5 | Conflictos de merge en `develop` | Medio | Media | PR pequeños y frecuentes. Bayron revisa a diario |
| 6 | Ausencia de un miembro por semana | Bajo/Medio | Media | Sistema de backup en la tabla de roles |
| 7 | Paquete npm comprometido | Alto | Baja | Proceso obligatorio de validación (Fase 4) |
| 8 | Diferencias de versión Java/Node | Medio | Media | Versiones fijadas en Fase 0 |
| 9 | Documento del Avance atrasado | Alto | Media | Buffer de 3–5 días entre fecha interna y fecha oficial del profesor |
| 10 | Secreto subido al repo | Crítico | Media | `.gitignore` desde commit 1, `.env.example`, revisión de PR, rotación inmediata si ocurre |
| 11 | Diseño hoy que impide multi-tenancy mañana | Alto | Alta si no se planifica | Incluir `tenant_id` en DER desde el inicio (aunque hardcoded a 1 en fase académica) |
| 12 | Solución "bonita" que no resuelve problema real | Alto | Media | Validación temprana con 2+ personas del rubro (ver sección "Problema, Valor y Métricas") |

---

## MVP de Cierre de Ciclo (26/10/2026)

> Realista: es posible que no lleguemos a todas las 12 HU y todos los módulos. Estos son los mínimos que **sí** debemos entregar para considerar el proyecto exitoso al cierre del semestre. Ordenados por prioridad — si falta tiempo, se corta desde abajo.

### Debe estar sí o sí (MVP crítico)
- ✅ Autenticación con JWT + RBAC por rol
- ✅ Multi-tenant funcional con 2 tenants demo (aislamiento probado con test)
- ✅ CRUD de Pacientes (HU-001)
- ✅ CRUD de Médicos + gestión de horarios
- ✅ Solicitud de cita (HU-003) con validación de horario disponible
- ✅ Triaje / signos vitales (HU-004)
- ✅ Receta médica (HU-002) — con validación de stock si el módulo de inventario está listo
- ✅ Expediente clínico consultable por CIF
- ✅ Documento académico de los 3 avances completo y aprobado por el profesor
- ✅ Presentación final funcional
- ✅ Deploy con Docker en un entorno demo (aunque sea local o VPS gratuito tipo Railway/Fly.io)

### Debería estar (deseable)
- 🟡 Módulo de Inventario básico (HU-007): CRUD de productos
- 🟡 Dashboard con "TOTAL ASSET VALUE" (versión simple)
- 🟡 i18n Español/Inglés funcional
- 🟡 Reportes básicos en PDF (una receta imprimible)
- 🟡 Notificaciones simuladas (correo o WhatsApp mock)

### Podría estar (bonus)
- 🔵 Órdenes de compra (HU-008, HU-009)
- 🔵 Módulo de activos físicos (HU-011)
- 🔵 Reportes avanzados (HU-012)
- 🔵 CI/CD con GitHub Actions
- 🔵 Onboarding self-service de nuevos tenants

### Fuera de alcance del ciclo (post-graduación)
- ❌ Facturación electrónica
- ❌ Telemedicina con videollamadas
- ❌ Pasarelas de pago reales
- ❌ Multi-región / edge deployment
- ❌ Integración con laboratorios/farmacias externas
- ❌ App móvil nativa

**Regla de oro:** si vamos atrasados a la mitad del ciclo, se corta el "Debería estar" antes que el "Debe estar sí o sí". Nunca sacrificar el aislamiento multi-tenant ni la seguridad.

---

## Roadmap SaaS (Post-Entrega Académica)

> Estas decisiones no son parte del entregable académico, pero **sí condicionan cómo diseñamos hoy**.

### Multi-tenancy (varios clientes en una sola instancia)

- **Enfoque recomendado: discriminador por columna** (`tenant_id` en cada tabla).
- Alternativas: base de datos por cliente (más aislamiento, más costo operativo) o schema por cliente.
- **Impacto en el diseño hoy:** todas las tablas deben incluir `tenant_id` desde el DER, aunque en la fase académica se use un único tenant hardcoded.

### Escalabilidad

- **Stateless backend**: sin estado en memoria del servidor. Todo lo que necesite compartirse va a BD o cache (Redis en el futuro).
- **Consultas eficientes**: índices en las columnas de búsqueda frecuente (CIF de paciente, fecha de cita, id de médico).
- **Paginación obligatoria** en cualquier endpoint que liste (nunca `findAll()` sin límite).

### Despliegue (cuando salga a producción)

- **Contenedores Docker** para backend y frontend.
- **Base de datos gestionada** (RDS de AWS, Cloud SQL de GCP, o Neon/Supabase para MVP).
- **CI/CD desde el día 1**: GitHub Actions que corra build + tests en cada PR.
- **Ambientes separados**: `dev` (local), `staging` (pre-prod), `prod`.

### Modelo de negocio (a explorar por Nicole + Héctor)

- **Freemium**: 1 médico, hasta 50 pacientes/mes → gratis. Más allá → pago.
- **Por médico activo/mes**: modelo SaaS clásico ($/médico/mes).
- **Por clínica (tier)**: básico / profesional / enterprise.

### Cumplimiento (referencial, aplicable si se comercializa)

- **Ley de Protección de Datos Personales de El Salvador** (Decreto 91-2023).
- **HIPAA-like** para expedientes clínicos si se apunta a mercados regulados.
- **Términos y Condiciones + Política de Privacidad** obligatorios antes de aceptar clientes reales.

### Impacto inmediato en el proyecto académico

Aunque no lleguemos a implementar todo esto en el ciclo, estas decisiones **sí** aplican ahora:
- `tenant_id` en el DER (aunque sea con valor fijo).
- Stateless desde el diseño.
- Paginación en listados.
- Docker-friendly (Spring Boot + React compilan a artefactos portables).

---

## Acciones Inmediatas (17–20/07/2026)

1. [ ] **Convocar reunión de kickoff** (Teams, 60 min) — presentar plan, roles, sprints, herramientas, visión SaaS, expectativas de seguridad
2. [ ] **Alinear al equipo con la visión SaaS y las métricas de éxito** (no es un proyecto solo académico)
3. [ ] **Crear equipo en Teams** con los 9 canales definidos
4. [ ] **Crear tablero "Proyecto Clínica"** en Planner y cargar tareas del Sprint 1
5. [ ] **Crear tablero "Retos Semanales"** en Planner y cargar Semana 2
6. [ ] **Crear repositorio GitHub privado** e invitar a los 10 miembros — con `.gitignore` y `.env.example` desde el commit 1
7. [ ] **Activar Dependabot** en el repo (Settings → Security → Dependabot alerts)
8. [ ] **Crear `docs/INSTRUCTIVO_GIT.md`, `docs/SETUP_ENTORNO.md` y `docs/fases/SEGURIDAD.md`** y compartir enlaces en Teams
9. [x] **Motor de BD decidido:** PostgreSQL 16 (recomendación del ingeniero). Pendiente: cerrar tipo de clínica (nombre del producto)
10. [ ] **Iniciar investigación de campo** (Nicole + Héctor): 2 entrevistas a personal de clínica antes del 25/07
11. [ ] **Ultimátum a MERINO VENTURA** — confirmar antes del 25/07
12. [ ] **Publicar la bibliografía APA + OWASP Top 10** en el canal `📖 Investigación`
13. [ ] **Programar las ceremonias recurrentes** en Teams Calendar (planning, weekly sync, review, retro)

---

## Verificación del Setup (Sprint 1 Review)

- [ ] Los 10 miembros clonaron el repo y crearon al menos 1 rama sin ayuda
- [ ] Todos ven sus tareas en el Planner y saben moverlas de estado
- [ ] Todos ejecutaron `java -version`, `node -v` y `docker --version` con las versiones correctas
- [ ] Todos pueden levantar la BD local con `docker compose up -d db`
- [ ] Semana 2 de retos tiene responsable y fecha en el Planner
- [ ] Documento del Avance 1 tiene 14 secciones completas antes del 25/07
- [ ] `npm audit` y `dependency-check` pasan limpios (sin high/critical)
- [ ] Al menos 1 sync semanal y 1 retrospectiva ejecutados con acta en `📌 Ceremonias Scrum`
- [ ] `.env.example` creado, `.env` en `.gitignore`, Dependabot activado
- [ ] DER incluye `Tenant` + `tenant_id` en todas las tablas de dominio
- [ ] Se realizaron ≥ 2 entrevistas de validación con personal de clínica
- [ ] `docs/fases/SEGURIDAD.md` con las decisiones OWASP tomadas
- [ ] `docs/fases/MULTI_TENANT.md` con la arquitectura de aislamiento
- [ ] JUnit 5 + Mockito + H2 configurados; `./mvnw test` corre exitoso
- [ ] Vitest + Testing Library configurados; `npm test` corre exitoso
- [ ] GitHub Actions bloquea PRs si los tests o el audit fallan
- [ ] Existe al menos 1 test de aislamiento multi-tenant funcionando
- [ ] i18n: existen `messages_es.properties` y `messages_en.properties` con al menos 20 claves
