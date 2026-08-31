# Carga masiva de tareas Avance 2 — Microsoft Planner

> 89 tareas del sprint 31/08–26/09/2026.
> Dos vías: **Power Automate** (§1) o **script PowerShell** (§2, más rápido).
> Owner: Héctor López (PM).

---

## Opción A — Script PowerShell (recomendado, ~5 min)

### Prerrequisitos

```powershell
# Solo la primera vez
Install-Module Microsoft.Graph -Scope CurrentUser -Force
```

### Paso 1 — Crear el plan y los 5 buckets en Planner

Ir a `tasks.office.com` → Nuevo plan → **"MediSuite - Avance 2"** (en el grupo M365 del equipo).

Crear 5 buckets en este orden (el orden importa para la vista del tablero):

| # | Nombre del bucket |
|---|---|
| 1 | Semana 1 |
| 2 | Semana 2 |
| 3 | Semana 3 |
| 4 | Buffer |
| 5 | Continuo |

### Paso 2 — Obtener los IDs del plan y de los buckets

1. Abrir el plan recién creado en Planner.
2. Copiar el `planId` desde la URL:
   ```
   https://tasks.office.com/.../Home/Planner/#/plantaskboard?groupId=<GID>&planId=<PLANID>
   ```
3. Abrir **Graph Explorer** (`https://developer.microsoft.com/en-us/graph/graph-explorer`):
   - Iniciar sesión con tu cuenta M365 del equipo.
   - Llamar: `GET https://graph.microsoft.com/v1.0/planner/plans/<PLANID>/buckets`
   - La respuesta es un array de objetos con `id` y `name`.
4. Editar `docs/planner/bucket_map_avance2.json` y reemplazar los `REEMPLAZAR-GUID-*` con los `id` reales:
   ```json
   {
     "Semana 1": "abc123...",
     "Semana 2": "def456...",
     "Semana 3": "ghi789...",
     "Buffer":   "jkl012...",
     "Continuo": "mno345..."
   }
   ```

### Paso 3 — Correr el script

Abrir PowerShell 7 como administrador en la carpeta `docs/planner/`:

```powershell
cd "c:\Users\hlopez\medisuite\docs\planner"

.\graph_bulk_import_avance2.ps1 `
    -CsvPath      ".\tareas_avance2.csv" `
    -PlanId       "PEGAR-AQUI-EL-PLANID" `
    -BucketMapPath ".\bucket_map_avance2.json"
```

Se abrirá el popup de login de Microsoft — iniciar sesión con tu cuenta M365.
El script:
1. Resuelve los 11 CIFs a UserIds de Azure AD.
2. Crea las 89 tareas con nombre, bucket, prioridad, fechas y asignado.
3. Actualiza la descripción de cada tarea.
4. Muestra resumen al final.

Tiempo estimado: **~3 minutos** (89 tareas × 600 ms delay × 2 llamadas/tarea).

### Paso 4 — Verificar

1. Abrir el plan en Planner.
2. Verificar que las 89 tareas aparecen en sus buckets correctos.
3. Revisar unas pocas al azar para confirmar que la descripción cargó.
4. El campo "Assigned To" se asigna automáticamente por CIF.

> **Nota:** El CIF del integrante en el CSV mapea directamente a su correo M365
> `{CIF}@cvirtualuees.edu.sv`. Si alguien no tiene cuenta activa en el tenant,
> la tarea se crea igualmente sin asignación — asignarla manualmente después.

---

## Opción B — Power Automate (UI, sin PowerShell)

Si prefieres no usar scripts, el flujo de Power Automate funciona igual pero
requiere preparar el CSV como tabla Excel en OneDrive.

### Paso 1 — Preparar Excel

1. Abrir `docs/planner/tareas_avance2.csv` en Excel.
2. Guardar como `.xlsx` → nombre `tareas_avance2.xlsx`.
3. Seleccionar todo → **Insertar → Tabla** → marcar "Mi tabla tiene encabezados".
4. Nombre de la tabla (pestaña "Diseño de tabla"): `TareasAvance2`.
5. Subir el archivo a tu **OneDrive** (carpeta del equipo).

### Paso 2 — Crear el flujo

1. Ir a `make.powerautomate.com`.
2. **Crear → Flujo de nube instantáneo**.
3. Nombre: `MediSuite - Carga masiva Avance 2`.
4. Desencadenador: **Desencadenar manualmente**.

#### Paso 2a — Leer el Excel

- **Nuevo paso** → `Excel Online (Business)` → **Enumerar filas presentes en una tabla**.
- Ubicación: `OneDrive for Business`.
- Archivo: `tareas_avance2.xlsx`.
- Tabla: `TareasAvance2`.
- Configuración avanzada → **Paginación: ON, umbral 1000**.

#### Paso 2b — Diccionario de buckets

- **Nuevo paso** → **Redactar (Compose)**.
- Nombre del paso: `BucketMap`.
- Entradas:
  ```json
  {
    "Semana 1": "GUID-SEMANA1",
    "Semana 2": "GUID-SEMANA2",
    "Semana 3": "GUID-SEMANA3",
    "Buffer":   "GUID-BUFFER",
    "Continuo": "GUID-CONTINUO"
  }
  ```
  (Reemplazar GUIDs según lo obtenido en Graph Explorer.)

#### Paso 2c — Diccionario CIF → email

- **Nuevo paso** → **Redactar (Compose)**.
- Nombre: `CifToEmail`.
- Entradas:
  ```json
  {
    "2026010132": "2026010132@cvirtualuees.edu.sv",
    "2026010068": "2026010068@cvirtualuees.edu.sv",
    "2026010204": "2026010204@cvirtualuees.edu.sv",
    "2026010796": "2026010796@cvirtualuees.edu.sv",
    "2026010813": "2026010813@cvirtualuees.edu.sv",
    "2026011012": "2026011012@cvirtualuees.edu.sv",
    "2026011585": "2026011585@cvirtualuees.edu.sv",
    "2026011707": "2026011707@cvirtualuees.edu.sv",
    "2026011709": "2026011709@cvirtualuees.edu.sv",
    "2026011736": "2026011736@cvirtualuees.edu.sv",
    "2026020122": "2026020122@cvirtualuees.edu.sv"
  }
  ```

#### Paso 2d — Bucle por fila

- **Nuevo paso** → **Aplicar a cada uno**.
- Salida: `value` del paso Excel.

Dentro del bucle:

**2d-i. Resolver userId del asignado**

- Acción: **Office 365 Users → Buscar usuarios (V2)**.
- Query: `mail eq '@{outputs('CifToEmail')?[items('Apply_to_each')?['CIF']]}'`

**2d-ii. Crear la tarea**

- Acción: **Planner → Create a task**.
- Group Id: (seleccionar el grupo del equipo desde el dropdown).
- Plan Id: pegar el planId.
- Title: `@{items('Apply_to_each')?['Task Name']}`
- Bucket Id: `@{outputs('BucketMap')?[items('Apply_to_each')?['Bucket Name']]}`
- Start DateTime: `@{items('Apply_to_each')?['Start Date']}`
- Due DateTime: `@{items('Apply_to_each')?['Due Date']}`
- Assigned User Ids: `@{first(body('Search_for_users_(V2)')?['value'])?['id']}`

**2d-iii. Obtener ETag de detalles**

- Acción: **Planner → Get task details**.
- Task Id: `@{body('Create_a_task')?['id']}`

**2d-iv. Actualizar descripcion**

- Acción: **Planner → Update task details**.
- Task Id: `@{body('Create_a_task')?['id']}`
- ETag: `@{body('Get_task_details')?['@odata.etag']}`
- Description: `@{items('Apply_to_each')?['Description']}`

**2d-v. Delay anti-throttling**

- Acción: **Programar → Retraso** → **1 segundo**.

### Paso 3 — Ejecutar

- **Guardar** el flujo.
- **Probar → Manualmente → Ejecutar flujo**.
- Esperar ~4 minutos para 89 tareas.

---

## Solución de problemas comunes

| Error | Causa | Solución |
|---|---|---|
| `409 Conflict` en Update details | ETag desactualizado entre pasos | Siempre obtener ETag de `Get task details` justo antes del PATCH. |
| `429 Too Many Requests` | Throttling de Graph API | En el script: aumentar `-DelayMs 1200`. En el flujo: subir el delay a 2 s. |
| Tarea sin asignado | CIF no resuelto a userId | Verificar que el correo `{CIF}@cvirtualuees.edu.sv` existe y esta activo en el tenant M365 del equipo. |
| Bucket no encontrado | Nombre no coincide exactamente | El nombre en `bucket_map_avance2.json` debe ser byte-por-byte igual al nombre creado en Planner (mayúsculas, tilde incluida). |
| Fechas desplazadas 1 día | Timezone UTC vs local | El script ya usa `T06:00:00Z` (UTC-6 = mediodia SV). Si las fechas siguen mal, ajustar el offset. |

---

## Referencias

- [Graph API — Create plannerTask](https://learn.microsoft.com/en-us/graph/api/planner-post-tasks)
- [Graph API — Update plannerTaskDetails](https://learn.microsoft.com/en-us/graph/api/plannertaskdetails-update)
- [Microsoft Graph Explorer](https://developer.microsoft.com/graph/graph-explorer)
- [Power Automate — Conector Planner](https://learn.microsoft.com/en-us/connectors/planner/)
