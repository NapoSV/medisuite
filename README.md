# MediSuite — Plataforma de Gestión Clínica SaaS

Sistema web multi-empresa (SaaS) para gestión de citas médicas, expediente clínico,
triaje, recetas, inventario y activos físicos.

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| **Backend** | Java 21 · Spring Boot 3.3 · Spring Security · JWT |
| **Frontend** | React 19 · Tailwind CSS · Vite |
| **Base de datos** | PostgreSQL 16 (Neon) |
| **Infraestructura** | Docker · Docker Compose |

---

## Cómo correr el proyecto

**Requisito único: Docker Desktop instalado y corriendo.**

```bash
# Desde la carpeta raíz del proyecto
docker compose up --build
```

La primera vez tarda ~3–5 minutos (descarga imágenes y compila).  
Las siguientes veces es mucho más rápido.

Una vez levantado:

| Servicio | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8097 |
| Swagger UI | http://localhost:8097/swagger-ui/index.html |

---

## Cuentas de acceso

Ver [CUENTAS_DEMO.md](CUENTAS_DEMO.md) para usuarios y contraseñas de prueba.

---

## Estructura del Proyecto

```
medisuite/
├── backend/          ← Spring Boot (Java 21)
├── frontend/         ← React + Tailwind + Vite
├── database/         ← Scripts SQL (schema + seed)
├── docker-compose.yml
├── .env.example      ← Plantilla de variables de entorno
└── CUENTAS_DEMO.md   ← Instrucciones de ejecución y credenciales
```
#Equipo Avance 2"