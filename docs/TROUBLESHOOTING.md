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

---

## 2026-09-18 — Backend no inicia: `Schema-validation: missing column [updated_at] in table [audit_logs]`

**Reportado por:** Nicole Sánchez.

### Síntoma

El backend arranca con Docker pero Hibernate falla en validación:

```
Schema-validation: missing column [updated_at] in table [audit_logs]
```

### Causa raíz

`AuditLog` fue modificada para `extends BaseEntity` en un commit de resolución de conflicto. `BaseEntity` tiene `updated_at`, pero la tabla `audit_logs` no la tiene y nunca debe tenerla: `AuditLog` es una entidad **append-only** (solo se inserta, nunca se actualiza). Agregar `updated_at` a un log de auditoría es semánticamente incorrecto.

### Solución

`AuditLog` **no debe extender `BaseEntity`**. Debe declarar sus propios campos (`id`, `tenant`, `tenantId`, `createdAt` con `@PrePersist`) e implementar `Serializable` (requerido por `AuditLogDatDao` para serializar a `.dat`).

**Regla general:** solo extienden `BaseEntity` las entidades mutables. Las tablas append-only (como `audit_logs`) solo tienen `created_at`.

### Cómo evitarlo

Ver `docs/ESTANDARES_CODIGO.md` — sección SQL, regla de `created_at`/`updated_at` y su excepción para tablas append-only.

---

## 2026-09-18 — Backend no inicia: `ConflictingBeanDefinitionException: authController`

**Reportado por:** Nicole Sánchez (Windows).

### Síntoma

El backend falla al arrancar con:

```
ConflictingBeanDefinitionException: Annotation-specified bean name 'authController'
for bean class [com.sv.grupo7.medisuite.controller.AuthController]
conflicts with existing, non-compatible bean definition of same name and class
[com.sv.grupo7.medisuite.controller.api.AuthController]
```

### Causa raíz

Existían dos clases llamadas `AuthController` en paquetes distintos (`controller/` y `controller/api/`), ambas anotadas con `@RestController`. Spring las registra con el mismo nombre de bean `authController` y falla al iniciar el contexto.

### Solución

Se fusionó el endpoint `/logout` (que estaba en `controller/AuthController`) dentro de `controller/api/AuthController`, que ya tenía `/login` y `/change-password`. El archivo `controller/AuthController.java` quedó vacío (solo declaración de paquete).

**Regla general:** no puede haber dos clases con el mismo nombre simple en paquetes distintos si ambas son `@RestController` o cualquier componente de Spring. Si se necesita separar endpoints, usar nombres de clase distintos o un único controlador consolidado.

---

## 2026-09-18 — Backend no inicia en Neon: `Schema-validation: missing column [dui]` y tablas faltantes

**Reportado por:** Nicole Sánchez (Windows, BD compartida Neon).

### Síntoma

El backend arranca con Docker apuntando a Neon pero Hibernate falla en validación:

```
Schema-validation: missing column [dui] in table [patients]
```

Luego de corregirlo, aparecen errores similares para `prescription_items`, `appointments`, `medical_records`.

### Causa raíz

`application-development.yml` tiene `flyway.enabled: false`, así que Flyway **nunca corre en modo development**. Las migraciones V2 (rename `cif` → `dui`), V5 (appointments), V6 (medical_records) y V7 (prescription_items) nunca se aplicaron en Neon. La BD se inicializó manualmente en un estado anterior y quedó desincronizada con las entidades del código.

### Solución

Aplicar las migraciones pendientes manualmente desde el **SQL Editor de Neon** (`console.neon.tech`):

```sql
-- V1
ALTER TABLE users ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT TRUE;

-- V2 (requiere permisos de owner — usar SQL Editor de Neon, no psql con clinica_app)
ALTER TABLE patients RENAME COLUMN cif TO dui;

-- V5
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    scheduled_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(200),
    office VARCHAR(50),
    reservation_code VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_appt_doctor_slot ON appointments (doctor_id, scheduled_at) WHERE status <> 'CANCELLED';
CREATE INDEX IF NOT EXISTS idx_appt_patient ON appointments (patient_id);
CREATE INDEX IF NOT EXISTS idx_appt_tenant_date ON appointments (tenant_id, scheduled_at);

-- V6
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    patient_id BIGINT NOT NULL UNIQUE REFERENCES patients(id),
    created_on DATE NOT NULL DEFAULT CURRENT_DATE,
    general_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- V7
ALTER TABLE prescriptions DROP COLUMN IF EXISTS medications;
ALTER TABLE prescriptions DROP COLUMN IF EXISTS dosage;
ALTER TABLE prescriptions DROP COLUMN IF EXISTS duration;
CREATE TABLE IF NOT EXISTS prescription_items (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    order_idx INTEGER NOT NULL DEFAULT 0,
    medication VARCHAR(200) NOT NULL,
    dose VARCHAR(100),
    frequency VARCHAR(100),
    duration_days INTEGER
);
CREATE INDEX IF NOT EXISTS idx_prescription_items_rx ON prescription_items (prescription_id, order_idx);
```

> El usuario `clinica_app` no tiene permisos de `ALTER TABLE ... RENAME COLUMN`. Usar siempre el SQL Editor del dashboard de Neon para este tipo de operaciones.

### Reglas para el equipo

- Flyway está desactivado en `development`. Si agregas una nueva migración SQL, **debes aplicarla manualmente en Neon** y avisar al equipo.
- Ante cualquier `Schema-validation: missing column/table`, verificar primero si existe la migración correspondiente en `db/migration/` y si fue aplicada en Neon.

---

## 2026-09-18 — Backend no inicia en Docker: `AccessDeniedException: /app/data`

**Reportado por:** Héctor López (validación local tras fix de schema).

### Síntoma

```
java.nio.file.AccessDeniedException: /app/data
at com.sv.grupo7.medisuite.dat.DatFileDao.<init>(DatFileDao.java:15)
```

El backend no levanta porque `DatFileDao` intenta crear el directorio `data/` (relativo al working directory `/app`) pero el usuario `appuser` no tiene permisos de escritura en `/app`.

### Causa raíz

El `Dockerfile` copiaba el JAR y asignaba permisos solo sobre `app.jar`, pero no creaba ni otorgaba permisos sobre `/app/data`. Al correr como `appuser` (usuario sin privilegios), la creación del directorio falla.

### Solución

En el `Dockerfile`, crear `/app/data` y dar ownership completo a `appuser` antes de cambiar de usuario:

```dockerfile
RUN mkdir -p /app/data && chown -R appuser:appgroup /app
```

En `docker-compose.yml`, agregar un volumen para que los archivos `.dat` persistan entre reinicios del contenedor:

```yaml
services:
  backend:
    volumes:
      - backend_data:/app/data

volumes:
  backend_data:
```
