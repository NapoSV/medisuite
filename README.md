# MediSuite — Plataforma de Gestión Clínica SaaS

> Proyecto Ciclo II · Programación II · UEES 2026

Sistema web multi-empresa (SaaS) para gestión de citas médicas, expediente clínico,
triaje, recetas, inventario y activos físicos. Diseñado con criterios de producción real:
multi-tenancy, seguridad OWASP, escalabilidad y valor de negocio.

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| **Backend** | Java 21 LTS + Spring Boot 3.3.x + Spring Security + JWT |
| **Frontend** | React + shadcn/ui + Tailwind CSS + Vite (Angular en evaluación — ver nota) |
| **Base de datos** | PostgreSQL 16 |
| **Infraestructura** | Docker + Docker Compose |
| **Testing backend** | JUnit 5 + Mockito + H2 |
| **Testing frontend** | Vitest + React Testing Library |
| **CI/CD** | GitHub Actions |

> **Nota sobre el frontend:** el ingeniero de la materia recomienda Angular 20 para el stack productivo. La decisión de mantener React o migrar a Angular queda pendiente — no bloquea el Avance 1 (documento) y se resuelve antes de iniciar código de frontend en el Avance 2.

---

## Prerequisitos

| Herramienta | Versión |
|-------------|---------|
| Java JDK | 21 LTS |
| Node.js | 20 LTS |
| Docker Desktop | Última estable |
| Git | Última estable |

Verificar instalación:
```bash
java -version   # → 21.x
node -v         # → 20.x
docker --version
git --version
```

---

## Setup Rápido

```bash
# 1. Clonar el repo
git clone https://github.com/NapoSV/medisuite.git
cd medisuite

# 2. Copiar variables de entorno
cp .env.example .env
# Editar .env con tus valores locales (pedir a Héctor o Bayron los valores de dev)

# 3. Levantar la base de datos
docker compose up -d db

# 4. Levantar el backend (en una terminal)
cd backend
./mvnw spring-boot:run

# 5. Levantar el frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

El frontend estará en http://localhost:5173  
El backend estará en http://localhost:8080

---

## Estructura del Proyecto

```
medisuite/
├── backend/          ← Spring Boot (Java 21)
├── frontend/         ← React + shadcn/ui + Vite
├── database/         ← Scripts SQL (DDL + seed)
├── docs/
│   ├── INSTRUCTIVO_GIT.md
│   ├── SETUP_ENTORNO.md
│   ├── ESTANDARES_CODIGO.md
│   ├── diagramas/    ← DER, UML, arquitectura, mockups
│   └── fases/        ← Documentación por fase
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Flujo de Trabajo (Git Flow)

```
main          → Solo entregas académicas (merge aprobado por PM)
develop       → Integración diaria vía Pull Request
feature/HU-XXX-descripcion
```

Ver [docs/INSTRUCTIVO_GIT.md](docs/INSTRUCTIVO_GIT.md) para el paso a paso completo.

---

## Documentación

- [Instructivo Git](docs/INSTRUCTIVO_GIT.md) — cómo trabajar con el repo
- [Setup del entorno](docs/SETUP_ENTORNO.md) — instalación detallada
- [Estándares de código](docs/ESTANDARES_CODIGO.md) — convenciones del equipo
- [Manual de BD compartida (Avance 1)](docs/MANUAL_AVANCE1_EQUIPO.md) — DBeaver, conexión a Neon, y ejercicio de práctica del equipo
- [Esquema de base de datos](docs/fases/ESQUEMA_BASE_DATOS.md) — las 17 tablas (v2 auditada), relaciones e índices
- [Guía maestra de desarrollo](docs/fases/GUIA_DESARROLLO_BACKEND.md) — hoja de ruta completa del Avance 1 a la presentación final: todas las tareas con responsable y fecha, código de cada módulo, API REST, frontend mínimo, deploy y flujo Git

---

## Equipo

| Nombre | Rol Scrum | Rol Técnico |
|--------|-----------|-------------|
| LOPEZ RUIZ HECTOR NAPOLEON | Product Owner | Analista de Negocio / PM |
| ORELLANA BAYRON | Scrum Master | Backend |
| VIGIL | Developer | Fullstack |
| FLORES | Developer | Backend |
| DIAZ | Developer | Frontend |
| MELGAR | Developer | Frontend |
| MERINO VENTURA ALEJANDRO SEBASTIAN | Developer | Base de Datos |
| VENTURA VELASQUEZ CARLOS MARIO | Developer | Arquitecto |
| FUENTES | QA | Analista de Pruebas |
| VASQUEZ | QA | Analista de Pruebas |
| SANCHEZ MENJIVAR NICOLE NOHEMY | Business Analyst | Analista de Negocio |

**Contacto:** HECTOR (PM/repo) · BAYRON (PRs/bloqueos técnicos)
