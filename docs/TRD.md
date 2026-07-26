# TRD — MediSuite

> **Technical Requirements Document** — Cómo se va a construir MediSuite.
> Versión: 1.0 · Fecha: 26/07/2026 · Owner: Carlos Ventura (Architect)

Este documento define el stack, la arquitectura, los requisitos no funcionales y las decisiones técnicas del proyecto. Complementa a [PRD.md](PRD.md) (qué se construye) y a [ESQUEMA_BACKEND.md](ESQUEMA_BACKEND.md) (detalle de capas y esquema de BD).

---

## 1. Stack tecnológico

| Capa | Tecnología | Versión | Justificación |
|---|---|---|---|
| **Lenguaje backend** | Java | 21 LTS | Requisito del profesor; soporte a records, patrones, virtual threads. |
| **Framework backend** | Spring Boot | 3.3.x | Estándar de industria, DI, seguridad, JPA, actuator. |
| **Seguridad** | Spring Security + JWT (jjwt) | 6.x / 0.12.x | Stateless auth, integración nativa con Spring. |
| **ORM** | Spring Data JPA + Hibernate | 3.x / 6.x | Repositorios sin boilerplate, migraciones controladas. |
| **Migraciones** | Flyway | 10.x | Versionado del esquema BD, ejecución al arranque. |
| **Base de datos** | PostgreSQL | 16 | Requisito del profesor; JSONB, índices parciales, extensiones. |
| **Hosting BD** | Neon (serverless) | — | Pooler, ramificación, tier free suficiente para el ciclo. Ver [memoria de conexión](../.claude/projects/c--Users-hlopez-medisuite/memory/neon_connection.md). |
| **Frontend** | React | 18.x | Stack decidido por el equipo (ver PLAN_DE_TRABAJO nota 1). |
| **Lenguaje frontend** | TypeScript | 5.x | Tipado estático para reducir bugs. |
| **Estado global frontend** | Zustand | 4.x | Ligero, sin boilerplate, ideal para MVP. |
| **Estilos** | Tailwind CSS | 3.x | Utility-first, consistente con [Identidad Visual](Identidad_Visual_Design_System.md). |
| **Componentes UI** | shadcn/ui | latest | Personalizable, integra bien con Tailwind y tokens MedCore Clay. |
| **Iconos** | Lucide Icons | latest | Definido en Identidad Visual. |
| **Gráficos** | Recharts | 2.x | Ligero, tokens de color propios. |
| **Formularios** | React Hook Form + Zod | 7.x / 3.x | Performance y validación con esquemas TypeScript. |
| **i18n frontend** | react-i18next | 14.x | Es/en, JSON por idioma. |
| **i18n backend** | Spring MessageSource | — | `messages_es.properties`, `messages_en.properties`. |
| **Bundler** | Vite | 5.x | Fast HMR, TypeScript nativo. |
| **Testing backend** | JUnit 5 + Mockito + Spring Test | 5.x / 5.x | Estándar; H2 solo para unit tests aislados. |
| **Testing frontend** | Vitest + React Testing Library | 1.x / 14.x | Rápido, API familiar. |
| **CI/CD** | GitHub Actions | — | Gratis para el repo, workflows por rama. |
| **Contenedores (opcional local)** | Docker Compose | — | Postgres local para quien no quiera Neon. |
| **Build backend** | Maven Wrapper (`./mvnw`) | 3.9.x | Cero deps globales. |

---

## 2. Arquitectura de alto nivel

```
┌─────────────────────────────────────────────────────────────────┐
│                       Cliente (Navegador)                        │
│  React + TS + Tailwind + shadcn/ui + Zustand + React Router     │
└──────────────────────────┬──────────────────────────────────────┘
                           │  HTTPS / REST-JSON
                           │  Authorization: Bearer <JWT>
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                  API Gateway lógico (Spring)                     │
│  ┌───────────────┐  ┌──────────────┐  ┌──────────────────┐     │
│  │ Filtros:      │  │ Controllers  │  │ ExceptionHandler │     │
│  │ JWT + Tenant  │→ │ /api/**      │→ │ (global)         │     │
│  └───────────────┘  └──────┬───────┘  └──────────────────┘     │
│                            ▼                                    │
│                     ┌──────────────┐                            │
│                     │ Services     │  ← Lógica de negocio       │
│                     └──────┬───────┘                            │
│                            ▼                                    │
│                     ┌──────────────┐                            │
│                     │ Repositories │  ← Spring Data JPA         │
│                     └──────┬───────┘                            │
└────────────────────────────┼────────────────────────────────────┘
                             ▼
                    ┌──────────────────┐
                    │ PostgreSQL 16    │
                    │ (Neon pooler)    │
                    │ Multi-tenant     │
                    └──────────────────┘
```

Detalles y diagrama de capas → [ESQUEMA_BACKEND.md](ESQUEMA_BACKEND.md).

---

## 3. Requisitos no funcionales

### 3.1. Seguridad (OWASP)

| Riesgo OWASP | Mitigación |
|---|---|
| A01 Broken Access Control | Spring Security + `@PreAuthorize` por rol + filtro de tenant obligatorio. |
| A02 Cryptographic Failures | Password con **BCrypt** (cost=12). Secretos vía `.env` (nunca commiteados). |
| A03 Injection | Solo JPA/JPQL parametrizado; **prohibido** SQL concatenado. |
| A05 Security Misconfiguration | `application-prod.yml` con actuator restringido; CORS explícito. |
| A07 Identification & Auth Failures | JWT con expiración corta (15 min) + refresh token (7 días). Bloqueo tras 5 intentos fallidos en 15 min. |
| A08 Software & Data Integrity | Solo dependencias oficiales de Maven Central; verificar hashes en CI. |
| A09 Logging & Monitoring | Log de todo cambio de dato médico con `user_id` + `tenant_id` + `timestamp`. |

### 3.2. Rendimiento

- **Búsqueda de paciente < 2 s** (índice por CIF, tenant_id, nombre).
- **API p95 < 500 ms** para endpoints CRUD.
- **Anti doble-reserva:** índice único parcial en `appointments(doctor_id, appointment_date, appointment_time) WHERE status IN ('PENDING','CONFIRMED')`.
- Paginación obligatoria en listados (`page`, `size`, `sort`).

### 3.3. Disponibilidad

- Backend stateless (JWT) → escalable horizontalmente.
- Neon con conexión pooler; retry con backoff en errores transitorios.
- Objetivo demo: **>99% durante ventana de presentación**.

### 3.4. Multi-tenancy

- **Estrategia:** columna `tenant_id BIGINT NOT NULL` en toda tabla de negocio.
- **Enforcement:** filtro Hibernate + `TenantContext` (ThreadLocal) alimentado desde el JWT.
- Cada query lleva `WHERE tenant_id = :currentTenantId` automáticamente.
- El `super-admin` puede consultar cross-tenant explícitamente.
- Ver esquema en [ESQUEMA_BACKEND.md § Multi-tenancy](ESQUEMA_BACKEND.md#multi-tenancy).

### 3.5. Internacionalización (i18n)

- **Backend:** `messages_es.properties` (default), `messages_en.properties`. Mensajes de error y validación centralizados.
- **Frontend:** `react-i18next` con `es.json` y `en.json` por dominio (auth, patients, appointments, etc.).
- Zona horaria por tenant (`tenants.timezone`, default `America/El_Salvador`).

### 3.6. Accesibilidad

- WCAG 2.1 nivel AA como objetivo.
- Contraste texto ≥ 4.5:1 (validar contra paleta de [DISENO_UI_UX.md](DISENO_UI_UX.md)).
- Navegación por teclado en formularios y modales.
- Alt-text en todo icono con significado.

### 3.7. Responsive design

- Mobile-first (breakpoints Tailwind: `sm 640`, `md 768`, `lg 1024`, `xl 1280`).
- Sidebar colapsable en tablet; bottom navigation en mobile.
- Tablas con scroll horizontal en mobile.

---

## 4. Integraciones

| Integración | Tipo | Estado |
|---|---|---|
| **Correo (SMTP)** | Simulado (log de consola) en Avance 1–3 | Real en presentación final si tiempo lo permite. |
| **WhatsApp** | Simulado | No hay pasarela real; solo endpoint mock. |
| **Pasarela de pagos** | — | Fuera de alcance (ver [PRD § 5.2](PRD.md#52-módulos-out-fuera-del-alcance)). |
| **Laboratorio / farmacia externa** | — | Fuera de alcance. |

---

## 5. Decisiones de arquitectura (ADRs resumidos)

### ADR-01 · Multi-tenancy por columna, no por schema
**Contexto:** ¿Cada tenant en su schema, en su BD, o compartiendo tablas?
**Decisión:** Compartir tablas con columna `tenant_id`.
**Razones:** simplicidad de migraciones, menor costo en Neon (una sola BD), backup unificado.
**Costo:** filtros obligatorios en cada query; riesgo de leak si se olvida el `WHERE tenant_id`. Se mitiga con filtro Hibernate global.

### ADR-02 · JWT stateless en lugar de sesión
**Decisión:** JWT firmado (HS256) + refresh token.
**Razones:** backend escalable sin sticky-session, compatible con múltiples clientes futuros.
**Costo:** invalidación anticipada requiere blacklist (out-of-scope MVP).

### ADR-03 · Layered architecture (Controller → Service → Repository)
**Decisión:** Capas clásicas Spring. Sin hexagonal ni CQRS en MVP.
**Razones:** curva de aprendizaje del equipo (11 personas mixtas), tiempo limitado.

### ADR-04 · Flyway para migraciones desde el día 1
**Decisión:** todo cambio de esquema pasa por un `Vxx__descripcion.sql` en `db/migration`.
**Razones:** BD compartida en Neon; sin control de versión el equipo choca.

### ADR-05 · Neon en lugar de PostgreSQL local por defecto
**Decisión:** Neon compartido como fuente única de dev; Docker Compose queda como opción.
**Razones:** el profesor evalúa desde su máquina; menos fricción de setup.

### ADR-06 · React sobre Angular
**Decisión:** React + TS + Tailwind + Zustand.
**Razones:** curva más baja, mejor ecosistema shadcn/ui alineado con Identidad Visual; Angular queda descartado formalmente.

---

## 6. Estándares de código y convenciones

Ver [ESTANDARES_CODIGO.md](ESTANDARES_CODIGO.md) — fuente única. Resumen operativo:

- **Idioma:** código y schema en inglés; comentarios y documentación en español.
- **Java:** PascalCase clases, camelCase métodos/atributos, paquete raíz `com.sv.grupo.hospital.citas`.
- **React:** componentes en `PascalCase.tsx`, hooks en `useX.ts`.
- **SQL:** snake_case, `tenant_id` obligatorio, `created_at`/`updated_at` con trigger.
- **Commits:** Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`, `test:`, `refactor:`).
- **Ramas:** `feature/HU-XXX-descripcion`, `chore/xxx`, `docs/xxx` (ver [INSTRUCTIVO_GIT.md](INSTRUCTIVO_GIT.md)).

---

## 7. Entornos

| Entorno | BD | Uso |
|---|---|---|
| **Local dev** | Neon `clinica_dev` (compartida) o Postgres local | Desarrollo diario. |
| **Test** | H2 en memoria | Tests unitarios de Spring; se descarta si el test requiere feature Postgres. |
| **Producción académica** | Neon `clinica_dev` misma instancia | Se usa para la demo del profesor. |

---

## 8. Referencias

- [PRD.md](PRD.md) — visión de producto y HU.
- [ESQUEMA_BACKEND.md](ESQUEMA_BACKEND.md) — capas, esquema BD, endpoints.
- [DISENO_UI_UX.md](DISENO_UI_UX.md) — identidad visual.
- [ESTANDARES_CODIGO.md](ESTANDARES_CODIGO.md) — convenciones.
- [SETUP_ENTORNO.md](SETUP_ENTORNO.md) — instalación.
- [Identidad_Visual_Design_System.md](Identidad_Visual_Design_System.md) — fuente única de UI tokens.
