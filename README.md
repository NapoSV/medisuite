# MediSuite â€” Plataforma de GestiÃ³n ClÃ­nica SaaS

Sistema web multi-empresa (SaaS) para gestiÃ³n de citas mÃ©dicas, expediente clÃ­nico,
triaje, recetas, inventario y activos fÃ­sicos.

---

## Stack TecnolÃ³gico

| Capa | TecnologÃ­a |
|------|-----------|
| **Backend** | Java 21 Â· Spring Boot 3.3 Â· Spring Security Â· JWT |
| **Frontend** | React 19 Â· Tailwind CSS Â· Vite |
| **Base de datos** | PostgreSQL 16 (Neon) |
| **Infraestructura** | Docker Â· Docker Compose |

---

## CÃ³mo correr el proyecto

**Requisito Ãºnico: Docker Desktop instalado y corriendo.**

```bash
# Desde la carpeta raÃ­z del proyecto
docker compose up --build
```

La primera vez tarda ~3â€“5 minutos (descarga imÃ¡genes y compila).  
Las siguientes veces es mucho mÃ¡s rÃ¡pido.

Una vez levantado:

| Servicio | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8097 |
| Swagger UI | http://localhost:8097/swagger-ui/index.html |

---

## Cuentas de acceso

Ver [CUENTAS_DEMO.md](CUENTAS_DEMO.md) para usuarios y contraseÃ±as de prueba.

---

## Estructura del Proyecto

```
medisuite/
â”œâ”€â”€ backend/          â† Spring Boot (Java 21)
â”œâ”€â”€ frontend/         â† React + Tailwind + Vite
â”œâ”€â”€ database/         â† Scripts SQL (schema + seed)
â”œâ”€â”€ docker-compose.yml
â”œâ”€â”€ .env.example      â† Plantilla de variables de entorno
â””â”€â”€ CUENTAS_DEMO.md   â† Instrucciones de ejecuciÃ³n y credenciales
```
# Equipo Avance 2"

# Tarea H-06 Â· Verificar Docker levanta todo el sistema
