# Carga masiva de tareas a Microsoft Planner con Power Automate

> Instructivo paso a paso para cargar las ~25 tareas del Avance 1 en un plan existente de Microsoft Planner **sin crear tarea por tarea manualmente**.
>
> Owner: Alejandro Vigil (Scrum Master).
> Tiempo estimado: **30–45 minutos** de setup + 2 minutos de ejecución.

---

## 1. Contexto

Microsoft Planner **no tiene import nativo** en licencia M365 estándar (sí en Planner Premium con el nuevo Import Wizard, pero el equipo no lo tiene). La vía práctica es:

- **Fuente:** un Excel en OneDrive/SharePoint con una tabla nombrada.
- **Motor:** un flujo de Power Automate con el conector Planner.
- **Alternativa scripted:** ver `graph_bulk_import.ps1` en esta misma carpeta.

---

## 2. Prerrequisitos

- [ ] Plan de Planner creado en el grupo del equipo (por ejemplo "MediSuite · Avance 1").
- [ ] 5 buckets creados en el plan: `Documento`, `Backend`, `Frontend`, `QA`, `BD`.
- [ ] `planId` copiado desde la URL del Planner:
      `https://tasks.office.com/.../Home/Planner/#/plantaskboard?groupId=<GID>&planId=<PLANID>`
- [ ] `bucketIds` copiados: se obtienen desde Graph Explorer con `GET /planner/plans/{planId}/buckets` (los agrega el asistente del flujo también, pero es más rápido tenerlos a mano).
- [ ] Correos M365 de los 11 integrantes (para asignar `assignedTo`).
- [ ] Excel con las tareas subido a OneDrive/SharePoint (ver §3).
- [ ] Cuenta con acceso a `powerautomate.microsoft.com`.

---

## 3. Preparar el Excel

1. Abrir `docs/planner/tareas_avance1.csv` en Excel.
2. Menú **Archivo → Guardar como** → formato `.xlsx` → nombre `tareas_avance1.xlsx`.
3. Seleccionar el rango completo (Ctrl+A) → menú **Insertar → Tabla** → marcar "Mi tabla tiene encabezados".
4. Nombrar la tabla: menú **Diseño de tabla → Nombre de la tabla: `TareasAvance1`**.
5. Subir el archivo a **OneDrive** (carpeta compartida del equipo) o a **SharePoint** del grupo.
6. Verificar que las columnas coincidan:

| Columna | Tipo | Ejemplo |
|---|---|---|
| `Title` | texto | `12.7 Spring Security + JWT + /api/auth/login` |
| `Bucket` | texto (exacto al nombre en Planner) | `Backend` |
| `AssigneeEmails` | texto (`;` separado si son varios) | `bayron@x.com;carlos@x.com` |
| `StartDate` | fecha (YYYY-MM-DD) | `2026-08-04` |
| `DueDate` | fecha (YYYY-MM-DD) | `2026-08-05` |
| `Priority` | número (1=Urgent, 3=Important, 5=Medium, 9=Low) | `3` |
| `Description` | texto largo | `Ver Tarea 12.7 en PLAN_EJECUCION_AVANCE1.md` |
| `Checklist` | texto (items separados por `|`) | `Endpoint responde 200|Test unitario pasa|PR abierto` |

**Importante:** el Excel debe estar guardado en un formato que Power Automate pueda leer. `.xlsx` con tabla nombrada funciona; `.csv` **no**.

---

## 4. Crear el flujo en Power Automate

### 4.1. Nuevo flujo instantáneo

1. Ir a https://make.powerautomate.com
2. Menú lateral **Crear** → **Flujo de nube instantáneo**.
3. Nombre: `MediSuite · Carga masiva Avance 1`.
4. Desencadenador: **Desencadenar un flujo manualmente**.
5. Click en **Crear**.

### 4.2. Paso 1 — Listar filas de la tabla Excel

- **Nuevo paso** → buscar `Excel Online (Business)` → acción **Enumerar filas presentes en una tabla**.
- Ubicación: OneDrive for Business (o SharePoint).
- Biblioteca de documentos: `OneDrive`.
- Archivo: navegar a `tareas_avance1.xlsx`.
- Tabla: seleccionar `TareasAvance1` del dropdown.
- **Configuración avanzada → Paginación:** activar, umbral **1000** (por si el equipo crece).

### 4.3. Paso 2 — Aplicar a cada fila

- **Nuevo paso** → **Aplicar a cada uno**.
- Salida: `value` del paso anterior.

Dentro del bucle:

### 4.4. Paso 2a — Convertir emails a UserIds (opcional pero recomendado)

Planner necesita `userIds` (GUIDs Azure AD), no correos. Dos opciones:

- **Opción A (fácil):** dejar los correos en el campo `AssigneeEmails` y **antes de correr el flujo**, ejecutar una sola vez el script `resolve_user_ids.ps1` (adjunto abajo) que reemplaza los correos por sus UserIds en el Excel.

- **Opción B (integrada en el flujo):**
  1. Añadir acción **Office 365 Users → Buscar usuarios (V2)**.
  2. Query: `mail eq '<AssigneeEmail>'` (usar `Compose` para dividir el campo por `;` primero).
  3. Extraer `id` de cada resultado.

Para simplicidad se recomienda **Opción A** en el primer uso.

### 4.5. Paso 2b — Crear la tarea

- **Nuevo paso** dentro del `Apply to each` → conector **Planner** → **Create a task**.
- Parámetros:
  - **Group Id:** GUID del grupo M365 (dropdown).
  - **Plan Id:** dropdown al plan `MediSuite · Avance 1`.
  - **Title:** expression `items('Apply_to_each')?['Title']`.
  - **Bucket Id:** expression para mapear texto → GUID. Solución simple: agregar antes una acción **Compose** con un diccionario:
    ```
    {
      "Documento": "AAAA-bucket-id",
      "Backend": "BBBB-bucket-id",
      "Frontend": "CCCC-bucket-id",
      "QA": "DDDD-bucket-id",
      "BD": "EEEE-bucket-id"
    }
    ```
    Luego usar: `outputs('BucketMap')[items('Apply_to_each')?['Bucket']]`.
  - **Start Date Time:** `items('Apply_to_each')?['StartDate']`.
  - **Due Date Time:** `items('Apply_to_each')?['DueDate']`.
  - **Priority:** `int(items('Apply_to_each')?['Priority'])`.
  - **Assigned User Ids:** `split(items('Apply_to_each')?['AssigneeEmails'], ';')` (o los GUIDs si aplicaste Opción A).

### 4.6. Paso 2c — Actualizar detalles (descripción + checklist)

`Create a task` **no acepta descripción ni checklist**. Hay que hacerlo en un segundo paso:

- **Nuevo paso** dentro del `Apply to each` → **Planner → Get task details**.
  - Task Id: expression `body('Create_a_task')?['id']`.
- **Nuevo paso** → **Planner → Update task details**.
  - Task Id: `body('Create_a_task')?['id']`.
  - ETag: `body('Get_task_details')?['@odata.etag']` (obligatorio para evitar 409).
  - Description: `items('Apply_to_each')?['Description']`.
  - Checklist: construir el array. Fórmula:
    ```
    json(concat('{',
      join(
        select(
          split(items('Apply_to_each')?['Checklist'], '|'),
          concat('"', guid(), '": { "@odata.type": "microsoft.graph.plannerChecklistItem", "title": "', item(), '", "isChecked": false }')
        ),
        ','
      ),
    '}'))
    ```
    (Fórmula compleja; alternativa: hacer un bucle interno `Apply to each` sobre `split(...,'|')` y usar la acción "Add a checklist item" del conector Planner si existe en tu tenant.)

### 4.7. Paso 3 — Delay anti-throttling

- **Nuevo paso** dentro del `Apply to each`, al final → **Programar → Retraso** → **1 segundo**.

Esto evita `429 Too Many Requests` del Graph API cuando hay muchas tareas.

---

## 5. Ejecución

1. Guardar el flujo.
2. Click en **Probar** → **Manualmente** → **Ejecutar flujo**.
3. Confirmar credenciales si el conector lo pide.
4. Esperar (~2 min para 25 tareas).
5. Revisar el historial de ejecución: verde ✔ en todos los pasos.
6. Abrir el Planner y verificar que las 25 tareas aparecen con sus buckets, fechas, responsables, descripción y checklist.

---

## 6. Solución de problemas

| Error | Causa | Solución |
|---|---|---|
| `429 Too Many Requests` | Throttling de Graph | Aumentar delay a 2–3 s. |
| `409 Conflict` en Update task details | ETag desactualizado | Asegurar que `ETag` viene de la respuesta de `Get task details`, no reusar entre iteraciones. |
| Fechas aparecen desplazadas 1 día | Timezone offset | En Excel usar formato `YYYY-MM-DDT00:00:00.000Z` y forzar UTC. |
| `AssigneeEmails` no se asigna | El conector necesita `userId` GUID, no email | Aplicar Opción A (script `resolve_user_ids.ps1`) antes de correr el flujo. |
| Bucket "no encontrado" | Mismatch mayúsculas/minúsculas | Verificar que el texto en Excel coincide EXACTAMENTE con el nombre del bucket en Planner. |
| Checklist vacío | Fórmula mal armada | Simplificar: en la primera versión no cargar checklist, solo descripción. Luego mejorar. |

---

## 7. Script `resolve_user_ids.ps1` (opcional, Opción A)

Ejecutar una sola vez antes de correr el flujo para reemplazar correos por UserIds:

```powershell
# resolve_user_ids.ps1
# Requiere: Install-Module Microsoft.Graph -Scope CurrentUser

Import-Module Microsoft.Graph.Users
Connect-MgGraph -Scopes "User.Read.All"

$emailMap = @{
    "hlopez@disna.com.sv"       = ""  # se rellena abajo
    "vigil@x.com"               = ""
    # ... completar con los 11 correos
}

foreach ($email in $emailMap.Keys) {
    $u = Get-MgUser -Filter "mail eq '$email'"
    $emailMap[$email] = $u.Id
    Write-Host "$email → $($u.Id)"
}

# Guardar como JSON para referencia rápida
$emailMap | ConvertTo-Json | Out-File ".\email_to_userid.json" -Encoding utf8
```

Luego, en Excel, reemplazar cada correo por su GUID (buscar/reemplazar con los datos del JSON).

---

## 8. Alternativa: script completo con Microsoft Graph

Si prefieres saltarte Power Automate, ver `graph_bulk_import.ps1` en esta misma carpeta. Es un script PowerShell que:

- Lee el mismo Excel.
- Resuelve los UserIds automáticamente.
- Crea tareas usando `POST /planner/tasks` con **JSON batching** (20 tareas por request).
- Actualiza descripción y checklist con `PATCH /planner/tasks/{id}/details`.

Más rápido si sabes PowerShell y ya tienes el módulo Graph instalado.

---

## 9. Referencias

- [Planner API overview - Microsoft Graph](https://learn.microsoft.com/en-us/graph/planner-concept-overview)
- [Create plannerTask - Graph v1.0](https://learn.microsoft.com/en-us/graph/api/planner-post-tasks?view=graph-rest-1.0)
- [Update plannerTaskDetails](https://learn.microsoft.com/en-us/graph/api/plannertaskdetails-update?view=graph-rest-1.0)
- [Power Automate: Excel Online Business connector](https://learn.microsoft.com/en-us/connectors/excelonlinebusiness/)
- [Power Automate: Planner connector](https://learn.microsoft.com/en-us/connectors/planner/)
- [DamoBird365 — Bulk import Planner tasks](https://damobird365.com/bulk-import-tasks-into-planner/)
