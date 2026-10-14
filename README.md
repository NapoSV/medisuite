# MediSuite — Plataforma de Gestión Clínica SaaS

# MediSuite — Plataforma de Gestión Clínica SaaS

Sistema web multi-empresa (SaaS) para gestión de citas médicas, expediente clínico,
triaje, recetas, inventario y activos físicos.

Repositorio: https://github.com/NapoSV/medisuite

---

## Integrantes

| # | Nombre | CIF | Rol técnico actual |
|---|--------|-----|--------------------|
| 1 | Erika Alexandra Fuentes Ortiz | 2026011709 | Pruebas de recetas, README y QA clínica (B9) |
| 2 | [completar] | [completar] | [completar] |
| 3 | [completar] | [completar] | [completar] |
| 4 | [completar] | [completar] | [completar] |
| 5 | [completar] | [completar] | [completar] |
| 6 | [completar] | [completar] | [completar] |
| 7 | [completar] | [completar] | [completar] |
| 8 | [completar] | [completar] | [completar] |
| 9 | [completar] | [completar] | [completar] |
| 10 | [completar] | [completar] | [completar] |
| 11 | [completar] | [completar] | [completar] |

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| **Backend** | Java 21 · Spring Boot 3.3 · Spring Security · JWT |
| **Frontend** | React 19 · Tailwind CSS · Vite |
| **Base de datos** | PostgreSQL 16 (Neon) |
| **Infraestructura** | Docker · Docker Compose |
| **Pruebas** | JUnit 5 · Mockito · MockMvc |

---

## Requisitos

- **Docker Desktop** instalado y corriendo (es el único requisito para ejecutar la aplicación).
- Para correr las pruebas fuera de Docker: **Java 21** y **Maven** (backend), **pnpm** (frontend).

---

## Configuración

1. Copia la plantilla de variables de entorno:

```bash
   cp .env.example .env
```

2. Abre `.env` y completa los valores. Los nombres de las variables están en `.env.example`.
3. Usa una base de datos de **desarrollo o QA**, no la base compartida del equipo.
4. Nunca subas `.env` ni credenciales al repositorio.

---

## Cómo correr el proyecto

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

Para comprobar que el backend responde:

```bash
curl http://localhost:8097/actuator/health
```

---

## Cómo correr las pruebas

**Backend** (JUnit + Mockito):

```bash
cd backend
mvn test
```

Para correr solo las pruebas de recetas:

```bash
cd backend
mvn -Dtest=PrescriptionServiceTest,PrescriptionControllerTest test
```

Si alguna prueba usa Testcontainers, necesita Docker Desktop corriendo.

**Frontend**:

```bash
cd frontend
pnpm build
pnpm lint
```

---

## Cuentas de acceso

Ver [CUENTAS_DEMO.md](CUENTAS_DEMO.md) para usuarios de prueba. Las credenciales
no se repiten en este README.

---

## Concurrencia

- **Respaldo programado:** existe un scheduler que genera respaldos en archivos `.dat`.
- **Cola de recordatorios (B3a/B3b):** *pendiente de integración*. Se documentará aquí
  cuando esté mergeada en `feature/avance3-fase-b`, diferenciando la cola de
  recordatorios del envío real.

---

## Estructura del Proyecto

```
medisuite/
├── backend/          ← Spring Boot (Java 21)
├── frontend/         ← React + Tailwind + Vite
├── database/         ← Scripts SQL (schema + seed)
├── docs/             ← Documentación y casos de prueba por fase
├── docker-compose.yml
├── .env.example      ← Plantilla de variables de entorno
└── CUENTAS_DEMO.md   ← Instrucciones de ejecución y credenciales
```

---

## Solución de problemas

| Síntoma | Qué revisar |
|---------|-------------|
| Docker dice que falta una variable | Compara los nombres de tu `.env` con `.env.example`. |
| Falla una prueba con Testcontainers | Verifica que Docker Desktop esté corriendo. |
| El puerto 5173 u 8097 está ocupado | Cierra el proceso que lo usa o cambia el puerto en `docker-compose.yml`. |