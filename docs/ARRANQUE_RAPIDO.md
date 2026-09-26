# Arranque rápido — MediSuite
> Cómo levantar el sistema en tu computadora en menos de 5 minutos.
> Versión: 2.1 · Actualizado: 26/09/2026

---

## Opción A — Docker (recomendada, la más simple)

### Requisitos
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y **corriendo**
- El archivo `.env` en la raíz del repo (pídelo a Héctor si no lo tienes)

> **Si ya tenías el `.env` de antes del 26/09:** agrega estas dos líneas al final del archivo
> (los valores te los comparte Héctor por Teams):
> ```
> DB_FLYWAY_USERNAME=neondb_owner
> DB_FLYWAY_PASSWORD=<pedirlo a Héctor>
> ```
> Sin ellas el backend falla al arrancar con `permission denied for table flyway_schema_history`.

### Pasos

```powershell
# 1. Clonar el repo (solo si es la primera vez)
git clone https://github.com/NapoSV/medisuite.git
cd medisuite

# Si ya lo tienes clonado, solo actualiza:
git checkout develop
git pull origin develop

# 2. Asegúrate de que Docker Desktop esté abierto (ícono en la barra de tareas)

# 3. Levantar backend + frontend de una sola vez
docker compose up --build
```

Espera hasta ver en los logs:
```
backend-1  | Started MediSuiteApplication in X.X seconds
```

### URLs
| Servicio | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend (health) | http://localhost:8097/actuator/health |

### Detener todo
```powershell
# Ctrl+C en la terminal donde corre docker compose, luego:
docker compose down
```

### Levantar sin recompilar (cuando no hay cambios de código)
```powershell
docker compose up
```

---

## Opción B — Desarrollo local (para hacer cambios al código)

### Requisitos
- Java 21 (Eclipse Temurin) — [descargar](https://adoptium.net/temurin/releases/?version=21)
- Apache Maven 3.9.x — [descargar](https://maven.apache.org/download.cgi)
- Node.js 20 LTS — [descargar](https://nodejs.org/en/download)
- pnpm: `npm install -g pnpm`
- El archivo `.env` en la raíz del repo (con `DB_FLYWAY_USERNAME` y `DB_FLYWAY_PASSWORD` — ver nota arriba)

### Backend

```powershell
# 1. Cargar variables de entorno (hacerlo cada vez que abres una nueva terminal)
cd C:\Users\$env:USERNAME\medisuite
Get-Content .\.env | ForEach-Object {
    if ($_ -match '^([^#=]+)=(.+)$') {
        [System.Environment]::SetEnvironmentVariable($Matches[1].Trim(), $Matches[2].Trim())
    }
}

# 2. Levantar el backend
cd backend
mvn spring-boot:run
```

Listo cuando aparece: `Started MediSuiteApplication` · puerto `8097`

### Frontend (en otra terminal)

```powershell
cd C:\Users\$env:USERNAME\medisuite\frontend
pnpm install        # solo la primera vez
pnpm dev
```

Listo cuando aparece: `Local: http://localhost:5173/`

---

## Credenciales demo

> Contraseña para **todos** los usuarios: `Demo2026!`

| Email | Rol | Clínica |
|---|---|---|
| `beatriz.reyes.demo@medisuite.test` | Administrador | clinica-san-rafael |
| `ana.martinez.demo@medisuite.test` | Doctor | clinica-san-rafael |
| `carlos.gomez.demo@medisuite.test` | Enfermero/a | clinica-san-rafael |
| `jorge.alas.demo@medisuite.test` | Recepcionista | clinica-san-rafael |
| `roberto.cruz.demo@medisuite.test` | Doctor | clinica-santa-lucia |

> **Nota:** `roberto.cruz` pertenece a `clinica-santa-lucia`. El frontend apunta a `clinica-san-rafael`, por lo que este usuario no puede hacer login aquí — es correcto, el aislamiento de tenant funciona.

---

## Problemas frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| `permission denied for table flyway_schema_history` | Faltan `DB_FLYWAY_USERNAME/PASSWORD` en el `.env` | Agregar las dos variables (ver nota al inicio de Opción A) · Detalle en `docs/TROUBLESHOOTING.md` |
| `Port 8097 is already in use` | Hay otro backend corriendo | `Get-Process java \| Stop-Process -Force` |
| `Credenciales inválidas` al login | Hash de contraseña en BD | Ver `docs/GUIA_CORRIDA_LOCAL.md` sección 5 |
| Docker no levanta | Docker Desktop no está corriendo | Abre Docker Desktop, espera a que el Engine diga "Running" |
| Variables de entorno vacías | Cerraste la terminal | Vuelve a cargar el `.env` (Opción B, paso 1) |
| `mvn: command not found` | Maven no está en el PATH | Usa la ruta completa o reinstala Maven (ver GUIA_CORRIDA_LOCAL.md) |
