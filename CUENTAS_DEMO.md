# MediSuite — Cuentas de demostración

Tenant activo: **clinica-san-rafael**

| Rol | Email | Contraseña |
|-----|-------|-----------|
| Administrador | beatriz.reyes.demo@medisuite.test | Demo2026! |
| Doctor | ana.martinez.demo@medisuite.test | Demo2026! |
| Enfermero | carlos.gomez.demo@medisuite.test | Demo2026! |
| Recepcionista | jorge.alas.demo@medisuite.test | Demo2026! |

> Tenant de aislamiento (solo para pruebas multi-tenant):
> - Doctor: roberto.cruz.demo@medisuite.test / Demo2026! (tenant: clinica-santa-lucia)

---

## Cómo correr el proyecto

Requisito único: **Docker Desktop** instalado y corriendo.

```bash
# 1. Descomprimir el ZIP y abrir una terminal en la carpeta raíz
# 2. Levantar todo con un solo comando:
docker compose up --build

# La primera vez tarda ~3-5 minutos (descarga imágenes y compila).
# Las siguientes veces es mucho más rápido.
```

Una vez levantado:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
