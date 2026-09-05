# Proceso probado: carga masiva de tareas a Planner (tenant educativo)

> Documentado el 2026-08-31 tras cargar las 88 tareas del Avance 2.
> Este proceso funciona con el tenant `cvirtualuees.edu.sv` que **bloquea
> admin consent** para apps de terceros (Graph CLI, Graph Explorer, etc.).

---

## Contexto y restricciones del tenant

| Herramienta | Resultado en cvirtualuees.edu.sv |
|---|---|
| `Connect-MgGraph` (Graph SDK) | ✗ Requiere admin consent |
| Graph Explorer | ✗ Requiere admin consent |
| Script con token del navegador | ✓ Funciona |
| Planner UI (crear buckets) | ✓ Funciona |
| Network tab del navegador | ✓ Para obtener IDs y tokens |

---

## Archivos del proceso

```
docs/planner/
├── tareas_avance2.csv              ← Fuente de verdad de las tareas
├── bucket_map_avance2.json         ← IDs de los 5 buckets (ya llenado)
├── crear_tareas_avance2.ps1        ← Crea las 88 tareas
├── agregar_notas_avance2.ps1       ← Agrega descripcion a cada tarea
└── PROCESO_CARGA_MASIVA.md         ← Este archivo
```

---

## Paso a paso completo

### 1. Crear los buckets en el Planner UI

Abrir el plan en `planner.cloud.microsoft` → **Agregar nuevo depósito** y crear:
`Semana 1`, `Semana 2`, `Semana 3`, `Buffer`, `Continuo`

### 2. Obtener los bucket IDs desde el navegador

1. F12 → Network → filtro `buckets` → recargar página
2. Click en la petición que devuelve el JSON de buckets
3. Response → copiar el `id` de cada bucket
4. Editar `bucket_map_avance2.json` con los IDs reales

> Para el Avance 2 estos ya están en `bucket_map_avance2.json`.

### 3. Obtener el Bearer token del navegador

1. F12 → Network → filtro `tasks` → recargar página
2. Click en petición `tasks?$expand=checklist,links,userAssignments` (tipo fetch)
3. Headers → Authorization → copiar **solo el JWT** (texto que empieza con `eyJ0eXAi`)

> El token dura ~1 hora. Si expira, recargar Planner y repetir.

### 4. Correr el script de creación

```powershell
cd "c:\Users\hlopez\medisuite\docs\planner"
$token = "eyJ0eXAi..."    # solo el JWT, sin "Bearer" ni "authorization"

.\crear_tareas_avance2.ps1 -Token $token
```

Tiempo: ~3 minutos para 88 tareas (600 ms delay × 2 fases).

### 5. Agregar notas (descripción) a las tareas

```powershell
.\agregar_notas_avance2.ps1 -Token $token
```

Tiempo: ~1.5 minutos adicionales.

> Si el token expiró entre el paso 4 y 5, obtener uno nuevo del navegador.

### 6. Asignar responsables (manual)

El tenant bloquea la resolución automática de emails a userIds de Azure AD.
**Cada integrante abre el Planner, filtra sus tareas y se auto-asigna.**

Alternativa futura: si alguien obtiene los GUIDs de Azure AD de los 11 integrantes
(pedirle al admin de la universidad), agregar un mapa `cifToUserId` en
`crear_tareas_avance2.ps1` y añadir el campo `assignments` al body del POST.

---

## Diferencias API v4.0 vs Graph API clásica

| Concepto | Graph API (`graph.microsoft.com`) | Planner v4.0 (`api.planner.svc.cloud.microsoft`) |
|---|---|---|
| Header obligatorio en PATCH/POST | no requerido explícito | `OData-Version: 4.0` |
| Nombre del campo title | `title` | `displayName` |
| Formato de fechas | `"2026-08-31T00:00:00Z"` (string) | `{ "date": "2026-08-31" }` (objeto) |
| Descripción | `/tasks/{id}/details` PATCH | `/tasks/{id}` PATCH campo `notes` |
| ETag para PATCH | `@odata.etag` del sub-recurso | `@odata.etag` del task mismo |
| Buckets | `name` | `displayName` |
| Endpoint base | `https://graph.microsoft.com/v1.0/planner` | `https://api.planner.svc.cloud.microsoft/taskapi/v4.0` |

---

## Cómo reutilizar para el Avance 3 / siguiente entrega

1. Preparar nuevo CSV con las columnas: `Task Name, Bucket Name, Assigned To, CIF, Start Date, Due Date, Progress, Priority, Labels, Description`
2. Crear nuevos buckets en el plan si se necesitan y actualizar `bucket_map_avance2.json` (o crear `bucket_map_avance3.json`)
3. Copiar `crear_tareas_avance2.ps1` a `crear_tareas_avance3.ps1` y cambiar `-CsvPath` y `-BucketMapPath`
4. Obtener token fresco del navegador y correr los scripts

El `PlanId` del plan MediSuite es: **`IMmXgJ4IQU-U8Vq-MIdvN2QACMUM`**
(permanece igual, no cambia entre avances)
