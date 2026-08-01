# Guía de corrida local — MediSuite
## Cómo clonar, configurar y probar la aplicación desde cero

> **Audiencia:** cualquier integrante del equipo que quiera levantar el sistema en su computadora.
> **Fecha:** 31/07/2026 · **Versión:** 1.0
>
> Esta guía documenta exactamente lo que el equipo hizo para tener el sistema corriendo:
> backend Spring Boot en `localhost:8080` + frontend React en `localhost:5173`.

---

## Stack tecnológico

| Componente | Tecnología | Versión | Obligatorio para |
|---|---|---|---|
| Lenguaje backend | Java (Temurin LTS) | 21 | Backend |
| Build backend | Apache Maven | 3.9.16 | Backend |
| Framework backend | Spring Boot | 3.3.2 | Backend |
| Base de datos | PostgreSQL 16 en Neon | — | Backend |
| Runtime frontend | Node.js (LTS) | 20.x | Frontend |
| Gestor de paquetes | pnpm | 9.x | Frontend |
| Framework frontend | React + TypeScript | 19 + 6 | Frontend |
| Bundler | Vite | 8.1 | Frontend |
| Estilos | Tailwind CSS | 3.4 | Frontend |
| Estado global | Zustand | 5.0 | Frontend |
| Control de versiones | Git | 2.46+ | Todos |

---

## Parte 1 — Herramientas a instalar (hacer UNA sola vez)

### 1.1 Git for Windows

1. Descarga desde https://git-scm.com/download/win
2. Ejecuta el instalador. En el wizard:
   - **PATH environment:** selecciona `"Git from the command line and also from 3rd-party software"`
   - **Credential Manager:** `"Git Credential Manager"` ← importante
   - El resto: valores por defecto
3. Verifica:
   ```powershell
   git --version
   # git version 2.46.0.windows.1
   ```

### 1.2 Java 21 — Eclipse Temurin (solo backend)

1. Descarga **Eclipse Temurin JDK 21 LTS** desde https://adoptium.net/temurin/releases/?version=21
   - OS: Windows · Architecture: x64 · Package Type: **JDK** · Version: 21 LTS · Formato: `.msi`
2. Ejecuta el instalador. Marca:
   - ✅ **Set JAVA_HOME variable**
   - ✅ **Add to PATH**
3. Verifica en PowerShell:
   ```powershell
   java -version
   # openjdk version "21.0.x" ... Temurin
   javac -version
   # javac 21.0.x
   ```

### 1.3 Apache Maven 3.9.x (solo backend)

> El equipo usa Maven 3.9.16 descargado manualmente desde Apache. NO instalar Maven desde winget u otros gestores porque dan versiones antiguas.

1. Descarga el ZIP binario desde https://maven.apache.org/download.cgi
   - Elige: `apache-maven-3.9.x-bin.zip`
2. Extrae el ZIP en `C:\tools\maven\` de forma que quede:
   ```
   C:\tools\maven\apache-maven-3.9.x\bin\mvn.cmd
   ```
   *(Si al extraer aparece una carpeta extra `apache-maven-3.9.x-bin`, entra en ella y mueve `apache-maven-3.9.x` a `C:\tools\maven\`)*
3. Agrega al PATH del sistema:
   - Inicio → "Editar las variables de entorno del sistema" → Variables del sistema → `Path` → Nuevo:
   ```
   C:\tools\maven\apache-maven-3.9.x\bin
   ```
4. Cierra y vuelve a abrir PowerShell. Verifica:
   ```powershell
   mvn -version
   # Apache Maven 3.9.x
   # Java version: 21.x.x
   ```

### 1.4 Node.js 20 LTS (solo frontend)

1. Descarga desde https://nodejs.org/en/download → LTS 20.x, Windows Installer 64-bit
2. Ejecuta el instalador con opciones por defecto
3. Verifica:
   ```powershell
   node -v
   # v20.x.x
   ```

### 1.5 pnpm (gestor de paquetes — reemplaza npm)

```powershell
npm install -g pnpm
pnpm -v
# 9.x.x
```

---

## Parte 2 — Clonar el repositorio

```powershell
cd C:\Users\$env:USERNAME
git clone https://github.com/NapoSV/medisuite.git
cd medisuite
git checkout develop
git pull origin develop
```

> **Importante:** el código funcional está en la rama `develop`. La rama `main` es para entregas estables.

---

## Parte 3 — Configurar y levantar el backend

### 3.1 Variables de entorno

El backend usa variables de entorno para las credenciales. **Nunca pongas contraseñas directamente en el código.** Copia y pega este bloque en PowerShell antes de correr el servidor (reemplaza los valores `<...>` con los reales del equipo):

```powershell
$env:DB_HOST="ep-nameless-water-avvbs1vv-pooler.c-11.us-east-1.aws.neon.tech"
$env:DB_PORT="5432"
$env:DB_NAME="clinica_dev"
$env:DB_USERNAME="clinica_app"
$env:DB_PASSWORD="<pedir a Héctor por Teams>"
$env:DB_SSLMODE="require"
$env:JWT_SECRET="medisuite-dev-secret-key-32chars-ok!!"
$env:JWT_EXPIRATION_MS="86400000"
```

> Las credenciales reales están en el `.env` de Héctor López. Pídelas por Teams — no se publican en el repo.

### 3.2 Levantar el servidor

```powershell
cd C:\Users\$env:USERNAME\medisuite\backend
mvn spring-boot:run
```

**Primera ejecución:** Maven descarga todas las dependencias (puede tardar 2–5 minutos). Las siguientes son mucho más rápidas.

**El servidor está listo cuando ves:**
```
Started MediSuiteApplication in X.XXX seconds
Tomcat started on port 8080
```

También verás en el log: `Found 17 JPA repository interfaces` — eso confirma que las entidades y la BD están bien conectadas.

### 3.3 Verificar que funciona

Abre otra terminal de PowerShell y ejecuta:

```powershell
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/api/auth/login" `
  -ContentType "application/json" `
  -Body '{"tenantSlug":"clinica-san-rafael","email":"beatriz.reyes.demo@medisuite.test","password":"Demo2026!"}'
```

**Respuesta esperada:**
```
accessToken  : eyJhbGciOiJIUzI1NiJ9...
tokenType    : Bearer
expiresIn    : 86400
user         : @{id=3; fullName=Beatriz Reyes; role=ADMIN; tenantId=1}
```

Si ves `accessToken` con un JWT largo → **backend funcionando correctamente**.

Si ves `Credenciales inválidas` → verificar que corriste el UPDATE de contraseñas en Neon (ver sección 5).

### 3.4 Usuarios de prueba

Todos los usuarios de demostración tienen la misma contraseña: `Demo2026!`

| Email | Rol | Clínica |
|-------|-----|---------|
| `ana.martinez.demo@medisuite.test` | DOCTOR | clinica-san-rafael |
| `carlos.gomez.demo@medisuite.test` | NURSE | clinica-san-rafael |
| `beatriz.reyes.demo@medisuite.test` | ADMIN | clinica-san-rafael |
| `jorge.alas.demo@medisuite.test` | RECEPTIONIST | clinica-san-rafael |
| `roberto.cruz.demo@medisuite.test` | DOCTOR | clinica-santa-lucia |

---

## Parte 4 — Configurar y levantar el frontend

### 4.1 Instalar dependencias (primera vez)

```powershell
cd C:\Users\$env:USERNAME\medisuite\frontend
pnpm install
```

> **¿Por qué pnpm y no npm?** El proyecto migró de npm a pnpm para mayor velocidad y menor uso de disco. El archivo `pnpm-lock.yaml` (en el repo) garantiza que todos usen exactamente las mismas versiones.

### 4.2 Levantar el servidor de desarrollo

```powershell
pnpm dev
```

**El frontend está listo cuando ves:**
```
VITE v8.x.x  ready in XXX ms
➜  Local:   http://localhost:5173/
```

### 4.3 Probar el flujo completo en el navegador

1. Abre http://localhost:5173
2. Deberías ver la pantalla de login (diseño MedCore Clay: fondo gris claro, card blanca, estetoscopio azul).
3. Ingresa:
   - **Código de clínica:** `clinica-san-rafael`
   - **Correo:** `beatriz.reyes.demo@medisuite.test`
   - **Contraseña:** `Demo2026!`
4. Click en **Iniciar sesión**.
5. Deberías ver la pantalla de bienvenida: `Bienvenido, Beatriz Reyes` · `ADMIN`.

---

## Parte 5 — Solución a errores comunes

### Error: "mvn no se reconoce como comando interno"

Maven no está en el PATH. Cierra PowerShell, vuélvelo a abrir y verifica que el PATH del sistema incluya la carpeta `bin` de Maven (ver sección 1.3). Si sigue fallando, usa la ruta completa:

```powershell
C:\tools\maven\apache-maven-3.9.x\bin\mvn.cmd spring-boot:run
```

### Error: "Credenciales inválidas" al hacer login

Los hashes de contraseña en la base de datos pueden ser los ficticios originales. Ejecuta este UPDATE en la consola SQL de Neon:

```sql
UPDATE users
SET password_hash = '$2a$12$bDYB/iG64e7v8J1vqnT9g.RuF0G62xSyyFtFuaIP//IXLoZqn2MW2'
WHERE email LIKE '%@medisuite.test';
```

Ese hash corresponde a la contraseña `Demo2026!`.

### Error: "HikariPool — Connection refused" o similar

El backend no puede conectarse a Neon. Causas posibles:
- Las variables de entorno no están definidas (cerraste PowerShell y las perdiste — redefínelas).
- La contraseña de `clinica_app` cambió.
- El host de Neon está incorrecto.

Verifica ejecutando en PowerShell:
```powershell
echo $env:DB_HOST
echo $env:DB_USERNAME
```
Si salen vacíos, vuelve a pegar el bloque de variables (sección 3.1).

### Error: "Failed to load tsconfig for 'src/main.tsx'"

Falta el archivo `tsconfig.app.json`. Debe existir en `frontend/`. Si lo borraste accidentalmente, pídelo al equipo o sácalo del último commit de `develop`.

### Error: "pnpm no se reconoce"

pnpm no está instalado. Ejecuta:
```powershell
npm install -g pnpm
```

### Error al arrancar: "No property 'lessThanEqualMinStock' found"

Este bug ya fue corregido en `develop`. Si te aparece, asegúrate de que tu rama esté actualizada:
```powershell
git pull origin develop
```

---

## Parte 6 — Flujo de trabajo diario (para cada tarea nueva)

```powershell
# 1. Antes de empezar: actualizar develop
git checkout develop
git pull origin develop

# 2. Crear tu rama de trabajo
git checkout -b feature/mi-tarea

# 3. Trabajar... hacer cambios...

# 4. Guardar cambios
git add ruta/archivo1 ruta/archivo2
git commit -m "feat: descripción de lo que hice"
git push origin feature/mi-tarea

# 5. Abrir Pull Request en GitHub hacia develop
# https://github.com/NapoSV/medisuite/pulls → New pull request
# base: develop ← compare: feature/mi-tarea
# Reviewer: NapoSV (Héctor)
```

---

## Parte 7 — Migración de npm a pnpm (ya realizada)

> Este paso ya fue ejecutado. Se documenta para referencia histórica.

El proyecto originalmente usaba npm (había `package-lock.json`). Se migró a pnpm porque:
- Instalación hasta 3× más rápida.
- Usa enlaces simbólicos → menos espacio en disco.
- `pnpm-lock.yaml` es más legible y estable en equipos.

**Lo que se hizo:**

```powershell
# En frontend/
Remove-Item package-lock.json
Remove-Item -Recurse -Force node_modules
pnpm install
# → genera pnpm-lock.yaml
```

Se agregó al `.gitignore` raíz:
```
package-lock.json
```

Se creó `frontend/.npmrc` con:
```
shamefully-hoist=true
```

---

*Documento elaborado por: Héctor Napoleón López Ruiz — 31/07/2026*
