# Troubleshooting — MediSuite

Registro de errores encontrados por el equipo durante el desarrollo, con su causa raíz y solución. Consultar antes de reportar un problema nuevo.

---

## 2026-09-09 — Backend no compila: "cannot find symbol" en getters/setters (Lombok)

**Reportado por:** Carlos Mario (macOS, Apple Silicon).

### Síntoma

`mvn clean package -DskipTests` falla con ~26 errores del tipo:

```
[ERROR] .../SecurityConfig.java:[23,43] variable jwtAuthenticationFilter not initialized in the default constructor
[ERROR] .../AuthService.java:[37,78] cannot find symbol
[ERROR]   symbol:   method getId()
[ERROR]   location: variable tenant of type com.sv.grupo7.medisuite.model.tenant.Tenant
[ERROR] .../UserService.java:[22,21] cannot find symbol
[ERROR]   symbol:   method getId()
[ERROR]   location: variable user of type com.sv.grupo7.medisuite.model.users.User
...
```

Todos los símbolos "faltantes" (`getId`, `getEmail`, `getRole`, `setPasswordHash`, etc.) son métodos que Lombok debería generar a partir de `@Getter @Setter` / `@RequiredArgsConstructor`.

### Causa raíz

El Maven del entorno estaba usando **Java 26** (instalado con Homebrew), pero Lombok no soporta versiones tan nuevas del JDK. Al no ejecutarse el annotation processor de Lombok, ninguno de los getters/setters ni el constructor generado existen en tiempo de compilación.

Diagnóstico clave:

```
$ mvn -version
Apache Maven 3.9.16
Java version: 26.0.2.1, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk/26.0.2.1/...
```

El proyecto requiere **Java 21** (`<java.version>21</java.version>` en `pom.xml`).

### Solución

Forzar que Maven use Java 21 en lugar de la versión más reciente del sistema.

**En macOS (zsh):**

```bash
# 1. Verificar que Java 21 esté instalado
/usr/libexec/java_home -V

# 2. Si no aparece 21, instalarlo
brew install openjdk@21
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# 3. Fijar JAVA_HOME permanentemente
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc

# 4. Verificar
mvn -version   # debe decir "Java version: 21..."

# 5. Recompilar
cd backend
mvn clean package -DskipTests   # → BUILD SUCCESS
```

**En Windows (PowerShell):** verificar con `java -version` y `mvn -version`. Si Maven usa una versión distinta de 21, ajustar `JAVA_HOME`:

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21", "User")
```

Cerrar y volver a abrir la terminal para que tome efecto.

### Reglas para el equipo

- **Java 21 es obligatorio.** No usar 22, 23, 24, 25, 26 aunque estén instaladas.
- Antes de reportar errores raros de compilación, correr `mvn -version` y confirmar que reporte Java 21.
- Si se compila desde IntelliJ (no solo CLI), además hay que:
  1. Instalar el plugin **Lombok** desde el Marketplace.
  2. Activar `Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing`.
  3. Panel derecho de Maven → **Reload All Maven Projects**.
  4. `Build → Rebuild Project`.

### Cómo reconocer este error rápido

Cualquier error `cannot find symbol` sobre `getX()`/`setX()` en clases marcadas con `@Getter`/`@Setter`, o `not initialized in the default constructor` en clases con `@RequiredArgsConstructor`, **es Lombok que no está corriendo**. La causa casi siempre es la versión de Java o la falta del plugin en IntelliJ.

---

## 2026-09-17 — Docker: backend `unhealthy`, frontend no arranca (`Schema-validation: missing table`)

**Reportado por:** Erika (Windows, Postgres local).

### Síntoma

`docker compose up -d --build` termina sin errores de compilación, pero el backend queda `unhealthy` y el frontend nunca arranca:

```
container medisuite-backend-1 is unhealthy
dependency failed to start: container medisuite-frontend-1 is unhealthy
```

`docker compose logs backend` muestra el mismo ciclo de error cada ~6 segundos:

```
BeanCreationException: Error creating bean with name 'entityManagerFactory'
  Caused by: PersistenceException: Unable to build Hibernate SessionFactory
    Caused by: SchemaManagementException: Schema-validation: missing table [administrators]
```

### Causa raíz

El perfil `development` (activado por `APP_ENV=development` en `.env`) deshabilita Flyway mediante `application-development.yml`. Si la base de datos local está vacía (recién creada o nunca migrada), Hibernate intenta validar el schema (`ddl-auto: validate`) y falla porque las tablas no existen. El servidor Tomcat nunca levanta, el health check `/actuator/health` no recibe respuesta, y Docker marca el contenedor como `unhealthy`. El frontend depende de `condition: service_healthy`, por eso tampoco arranca.

**Afecta solo a quienes usan Postgres local** (`DB_HOST=host.docker.internal`). Quienes apuntan a la BD compartida de Neon no tienen este problema porque el schema ya está aplicado allí.

### Solución

Consiste en ejecutar las migraciones Flyway una sola vez para crear el schema, luego volver al perfil normal.

**Paso 1 — En `.env`, cambiar el perfil temporalmente:**

```env
# Cambiar:
APP_ENV=development

# Por:
APP_ENV=docker
```

> No existe `application-docker.yml`, así que Spring Boot usa únicamente `application.yml` base, donde `flyway.enabled: true`. Flyway correrá y creará todas las tablas.

**Paso 2 — Reiniciar los contenedores (no hace falta rebuild):**

```bash
docker compose down
docker compose up -d
```

Esperar ~60 segundos. El backend debe quedar `healthy` y el frontend en `http://localhost:5173`.

**Paso 3 — Volver al perfil development:**

Una vez que las tablas existen en la BD local, el perfil `development` ya no falla. Restaurar el `.env`:

```env
APP_ENV=development
```

Y reiniciar de nuevo:

```bash
docker compose down && docker compose up -d
```

### Reglas para el equipo

- Si usas **Postgres local**, ejecuta el paso 1-2 al menos una vez para inicializar el schema.
- Si usas la **BD compartida de Neon**, no necesitas hacer nada: el schema ya está aplicado.
- Nunca cambiar `ddl-auto` a `create` o `create-drop` en desarrollo — borraría datos existentes.

### Cómo reconocer este error rápido

Cualquier `SchemaManagementException: Schema-validation: missing table [X]` en los logs de Docker **significa que la BD está vacía o le falta esa tabla**. No es un error de código. Verificar primero si `APP_ENV=development` está activo y si la BD es local o remota.
