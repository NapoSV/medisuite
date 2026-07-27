# Manual del Equipo — Base de Datos Compartida (Avance 1)

> Guía paso a paso para: instalar DBeaver, conectarte a la base de datos compartida en Neon, y hacer tu ejercicio de práctica (crear 1 tabla del proyecto). Léelo completo antes de empezar.

---

## 1. Instalar DBeaver

1. Ir a https://dbeaver.io/download/
2. Descargar **DBeaver Community** (gratis) para tu sistema operativo (Windows/Mac/Linux)
3. Instalar con las opciones por defecto
4. Abrir DBeaver

---

## 2. Conectar DBeaver a la base de datos compartida (Neon)

1. Click en **"Nueva conexión"** (ícono de enchufe con `+`) → elegir **PostgreSQL**
2. Pestaña **Main**, completar:
   - **Host:** el host *pooler* de Neon que te compartió HECTOR **por mensaje directo (DM)** en Teams — no se publica en este documento ni en el repo por seguridad
   - **Port:** `5432`
   - **Database:** el nombre que te confirme HECTOR en Teams (ver sección 4 — la base se crea desde cero en este avance)
   - **Username:** tu usuario individual (ver tabla abajo)
   - **Password:** la que te compartió HECTOR **por mensaje directo (DM)** en Teams — no se publica en este documento ni en el repo por seguridad
3. Pestaña **SSL**: marcar **"Use SSL"** → SSL Mode: `require`
4. Click **"Test Connection"** → debe decir "Connected"
5. Click **"Finish"**

### Tabla de usuarios (referencia — la contraseña te llega aparte)

| Integrante | Usuario DBeaver | Contraseña |
|---|---|---|
| Héctor | `hector` | *(te la compartes tú mismo, ya la tienes)* |
| Bayron | `bayron` | Por Teams DM |
| Vigil | `vigil` | Por Teams DM |
| Flores | `flores` | Por Teams DM |
| Díaz | `diaz` | Por Teams DM |
| Melgar | `melgar` | Por Teams DM |
| Merino | `merino` | Por Teams DM |
| Ventura | `ventura` | Por Teams DM |
| Fuentes | `fuentes` | Por Teams DM |
| Vásquez | `vasquez` | Por Teams DM |
| Nicole | `nicole` | Por Teams DM |

**Qué puede hacer tu usuario:** leer, insertar, actualizar y borrar datos (filas) en las tablas del proyecto, y crear tus propias bases de datos nuevas para practicar. **No puede:** borrar la base de datos compartida ni las tablas que no te pertenecen — eso requiere ser dueño del objeto o superusuario, y ninguno de los usuarios del equipo lo es.

**Vigencia:** tu acceso está configurado hasta el **31/12/2026** — después de esa fecha habría que renovarlo si el proyecto sigue.

---

## 3. Dónde viven los scripts SQL en el repositorio

Todo el esquema de base de datos está versionado en el repo, en la carpeta `database/`:

| Archivo | Qué contiene |
|---------|--------------|
| [`database/00_create_database.sql`](../database/00_create_database.sql) | Crea la base `clinica_dev` y da permiso de `CREATE` a los 11 usuarios del equipo |
| [`database/schema.sql`](../database/schema.sql) | Las 17 tablas del proyecto v2 (`CREATE TABLE`), con `tenant_id`, `created_at`/`updated_at`, triggers e índices |
| [`database/seed.sql`](../database/seed.sql) | Datos ficticios de ejemplo (2 clínicas demo) para probar que todo funciona |

**Cómo se usan:** cualquiera con acceso a una base de datos PostgreSQL (local o Neon) puede aplicar estos scripts así:

```bash
psql "<tu connection string>" -f database/schema.sql
psql "<tu connection string>" -f database/seed.sql
```

Ver también [`docs/fases/ESQUEMA_BASE_DATOS.md`](fases/ESQUEMA_BASE_DATOS.md) para la descripción completa de cada tabla y los pendientes de revisión (ej. normalizar especialidades de médicos).

> **Importante:** estos archivos **no tienen contraseñas ni datos reales** — son seguros de subir al repo tal cual. Lo que nunca se sube es el `.env` de cada quien (ya está en `.gitignore`) ni ningún archivo con credenciales reales.

---

## 4. Paso 0 — Crear la base de datos compartida (lo hace HECTOR una sola vez)

Antes de que cada quien cree su tabla, tiene que existir una base de datos donde todos trabajen. El script ya está listo en el repo: [`database/00_create_database.sql`](../database/00_create_database.sql).

**Desde terminal (psql):**
```bash
psql "host=<HOST_NEON> user=hector dbname=neondb sslmode=require" -f database/00_create_database.sql
# <HOST_NEON> = el host pooler de Neon (lo tienes en tu .env como DB_HOST)
```

**Desde DBeaver:** abre el archivo `database/00_create_database.sql` en un editor SQL conectado con el usuario `hector`, y ejecútalo línea por línea (el `\c clinica_dev` es un comando de `psql`, no funciona igual en el editor de DBeaver — ahí simplemente ejecuta primero `CREATE DATABASE clinica_dev;`, luego abre un nuevo editor SQL apuntando ya a la base `clinica_dev`, y corre el `GRANT` desde ahí).

Avisar en el canal `🗄️ Base de Datos` de Teams cuando esto esté listo — a partir de ahí cada quien puede seguir el Paso 5.

> **Si te aparece `ERROR: permission denied for schema public` al crear tu tabla:** significa que el `GRANT CREATE` del Paso 0 no se aplicó (típicamente porque se corrió todo el script de un jalón en DBeaver, incluyendo el `\c`, que ahí no funciona). Solución: pedirle a HECTOR que vuelva a correr **solo** la línea `GRANT CREATE ON SCHEMA public TO ...` conectado directamente a `clinica_dev`.

---

## 5. Ejercicio de práctica — cada integrante crea 1 tabla

**Objetivo:** que todos practiquen `CREATE TABLE` con llaves foráneas de verdad, conectados a la base compartida con su propio usuario. Se hace **en orden** — cada tabla depende de que la anterior ya exista (por las llaves foráneas), así que revisa el orden antes de empezar.

> ⚠️ **Actualización v2 (23/07/2026):** el esquema fue auditado y ahora son **17 tablas** — se agregaron `specialties` (debe existir **antes** que `doctors`) y `purchase_order_items` (después de `purchase_orders` y `products`). Si ya habías creado `doctors` con la versión anterior, hay que recrearla con la columna `specialty_id`.

### Ronda 1 — obligatoria (una tabla por persona; VIGIL crea 2 porque van juntas)

| Orden | Tabla | Depende de | Asignado |
|-------|-------|------------|----------|
| 1 | `tenants` | (ninguna) | **HECTOR** |
| 2 | `users` | `tenants` | **BAYRON** |
| 3 | `patients` | `users` | **DIAZ** |
| 4 | `specialties` *(v2)* | `tenants` | **VIGIL** |
| 5 | `doctors` | `users`, `specialties` | **VIGIL** |
| 6 | `nurses` | `users` | **MELGAR** |
| 7 | `administrators` | `users` | **FUENTES** |
| 8 | `receptionists` | `users` | **VASQUEZ** |
| 9 | `appointments` | `patients`, `doctors` | **MERINO** |
| 10 | `medical_records` | `patients` | **VENTURA** |
| 11 | `vital_signs` | `medical_records` | **NICOLE** |
| 12 | `prescriptions` | `medical_records`, `doctors` | **FLORES** |

### Ronda 2 — bonus (opcional, para quien termine rápido)

| Tabla | Depende de | Sugerido |
|-------|------------|----------|
| `products` | (ninguna) | FLORES o quien se ofrezca |
| `purchase_orders` | (ninguna) | quien se ofrezca |
| `purchase_order_items` *(v2)* | `purchase_orders`, `products` | quien haya hecho las 2 anteriores |
| `physical_assets` | (ninguna) | quien se ofrezca |
| `audit_logs` | `users` | HECTOR (cierre del ejercicio) |

### Cómo hacer tu parte

1. Conéctate a `clinica_dev` en Neon con tu usuario (sección 2)
2. Abre [`database/schema.sql`](../database/schema.sql) en el repo y **busca el bloque de tu tabla asignada** (viene comentado con el número, ej. `-- 3. patients (Paciente)`)
3. Copia **solo ese bloque** (el `CREATE TABLE` + su trigger, si tiene) en el editor SQL de DBeaver
4. Espera tu turno según el orden de la tabla (no puedes crear `patients` antes de que exista `users`) — coordinen en el canal de Teams quién ya terminó
5. Ejecuta el script (▶ en DBeaver)
6. Verifica que tu tabla aparezca: click derecho en la base → Refresh, expande **Schemas → public → Tables**
7. Avisa en el canal `🗄️ Base de Datos` cuando termines, para que la siguiente persona en la cadena pueda continuar

### Al terminar todos

Cuando las 11 (o 15) tablas existan, correr el seed de datos ficticios para probar que todo conecta bien:
```bash
psql "<connection string de clinica_dev>" -f database/seed.sql
```
(lo puede correr HECTOR o quien tenga el rol con más contexto, una sola vez)

---

## 6. Checklist final

- [ ] Los 11 tienen DBeaver instalado y conectado a `clinica_dev` en Neon
- [ ] `CREATE DATABASE clinica_dev` + `GRANT CREATE` ejecutado (Paso 0, HECTOR)
- [ ] Las 11 tablas de la Ronda 1 creadas en orden, sin errores de llave foránea
- [ ] (Opcional) Ronda 2 completada
- [ ] `seed.sql` corrido y datos ficticios visibles para todos
- [ ] Nadie subió contraseñas ni connection strings al repositorio de GitHub
