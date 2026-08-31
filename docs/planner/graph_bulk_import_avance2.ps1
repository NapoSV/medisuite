<#
.SYNOPSIS
    Carga masiva de tareas del Avance 2 de MediSuite a Microsoft Planner
    usando Microsoft Graph directamente.

.DESCRIPTION
    - Lee tareas_avance2.csv.
    - Mapea CIF -> correo M365 (@cvirtualuees.edu.sv) -> userId (Azure AD GUID).
    - Crea cada tarea con POST /planner/tasks.
    - Actualiza descripcion con PATCH /planner/tasks/{id}/details.
    - Delay de 600 ms entre requests para evitar throttling (Graph Planner = 300 req/min).

.PREREQUISITES
    - PowerShell 7+
    - Install-Module Microsoft.Graph -Scope CurrentUser
    - Permisos delegados: Tasks.ReadWrite, Group.ReadWrite.All, User.Read.All
    - PlanId y BucketIds del plan "MediSuite - Avance 2" en Planner.

.HOW TO GET PlanId AND BucketIds
    1. Abrir el plan en Planner y copiar la URL:
       https://tasks.office.com/.../Home/Planner/#/plantaskboard?groupId=<GID>&planId=<PLANID>
    2. Buckets: en Graph Explorer autenticado, llamar:
       GET https://graph.microsoft.com/v1.0/planner/plans/<PLANID>/buckets
       Copiar los 'id' de cada bucket al archivo bucket_map_avance2.json.

.EXAMPLE
    .\graph_bulk_import_avance2.ps1 `
        -CsvPath ".\tareas_avance2.csv" `
        -PlanId "AAAAAAAA-BBBB-CCCC-DDDD-EEEEEEEEEEEE" `
        -BucketMapPath ".\bucket_map_avance2.json"

    bucket_map_avance2.json:
    {
      "Semana 1": "bucket-guid-semana1",
      "Semana 2": "bucket-guid-semana2",
      "Semana 3": "bucket-guid-semana3",
      "Buffer":   "bucket-guid-buffer",
      "Continuo": "bucket-guid-continuo"
    }

.NOTES
    Autor: MediSuite Grupo 7 - 2026-08-31
    El CSV usa columnas: Task Name, Bucket Name, Assigned To, CIF, Start Date,
    Due Date, Progress, Priority, Labels, Description
    El correo M365 de cada integrante es: {CIF}@cvirtualuees.edu.sv
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$CsvPath,

    [Parameter(Mandatory=$true)]
    [string]$PlanId,

    [Parameter(Mandatory=$true)]
    [string]$BucketMapPath,

    [int]$DelayMs = 600
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Continue'

# ─── Mapa fijo CIF -> correo M365 ────────────────────────────────────────────
$cifToEmail = @{
    "2026010132" = "2026010132@cvirtualuees.edu.sv"  # Héctor López
    "2026010068" = "2026010068@cvirtualuees.edu.sv"  # Walter Vásquez
    "2026010204" = "2026010204@cvirtualuees.edu.sv"  # Alejandro Vigil
    "2026010796" = "2026010796@cvirtualuees.edu.sv"  # Zair Díaz
    "2026010813" = "2026010813@cvirtualuees.edu.sv"  # Nicole Sánchez
    "2026011012" = "2026011012@cvirtualuees.edu.sv"  # Walter Flores
    "2026011585" = "2026011585@cvirtualuees.edu.sv"  # Carlos Ventura
    "2026011707" = "2026011707@cvirtualuees.edu.sv"  # Bayron Orellana
    "2026011709" = "2026011709@cvirtualuees.edu.sv"  # Erika Fuentes
    "2026011736" = "2026011736@cvirtualuees.edu.sv"  # William Melgar
    "2026020122" = "2026020122@cvirtualuees.edu.sv"  # Alejandro Merino
}

# Mapeo de prioridad texto -> número Graph (1=Urgent,3=Important,5=Medium,9=Low)
$priorityMap = @{
    "Urgent"    = 1
    "Important" = 3
    "Medium"    = 5
    "Low"       = 9
}

# ─── 1. Conectar a Graph ─────────────────────────────────────────────────────
Write-Host "`n→ Conectando a Microsoft Graph..." -ForegroundColor Cyan
Import-Module Microsoft.Graph.Authentication -ErrorAction Stop
Connect-MgGraph -Scopes "Tasks.ReadWrite","Group.ReadWrite.All","User.Read.All" -NoWelcome

# ─── 2. Cargar bucket map ────────────────────────────────────────────────────
if (-not (Test-Path $BucketMapPath)) {
    throw "No se encontro bucket map: $BucketMapPath. Ver .EXAMPLE en este script."
}
$bucketMap = Get-Content $BucketMapPath -Raw | ConvertFrom-Json -AsHashtable
Write-Host "→ Buckets cargados: $($bucketMap.Keys -join ', ')" -ForegroundColor Cyan

# ─── 3. Cargar CSV ───────────────────────────────────────────────────────────
if (-not (Test-Path $CsvPath)) {
    throw "No se encontro CSV: $CsvPath"
}
$tasks = Import-Csv $CsvPath -Encoding UTF8
Write-Host "→ Tareas a cargar: $($tasks.Count)" -ForegroundColor Cyan

# ─── 4. Resolver CIFs a UserIds (cache) ─────────────────────────────────────
Write-Host "`n→ Resolviendo CIFs a UserIds de Azure AD..." -ForegroundColor Cyan
$cifToUserId = @{}

$allCifs = $tasks | ForEach-Object { $_.'CIF' } | Sort-Object -Unique
foreach ($cif in $allCifs) {
    $cif = $cif.Trim()
    if ([string]::IsNullOrWhiteSpace($cif)) { continue }

    $email = $cifToEmail[$cif]
    if (-not $email) {
        Write-Warning "  CIF '$cif' no esta en el mapa fijo. Saltando asignacion."
        continue
    }

    try {
        $resp = Invoke-MgGraphRequest -Method GET `
            -Uri "https://graph.microsoft.com/v1.0/users/$email"
        $cifToUserId[$cif] = $resp.id
        Write-Host "  ✓ $cif ($email) -> $($resp.id)" -ForegroundColor Green
    } catch {
        Write-Warning "  ✗ No se pudo resolver CIF $cif ($email): $_"
    }
}

# ─── 5. Crear tareas ────────────────────────────────────────────────────────
Write-Host "`n→ Creando tareas en Planner..." -ForegroundColor Cyan
$created = [System.Collections.Generic.List[hashtable]]::new()
$skipped = 0

foreach ($t in $tasks) {
    $taskName  = $t.'Task Name'.Trim()
    $bucketKey = $t.'Bucket Name'.Trim()
    $cif       = $t.'CIF'.Trim()
    $startDate = $t.'Start Date'.Trim()
    $dueDate   = $t.'Due Date'.Trim()
    $priorityText = $t.'Priority'.Trim()
    $description  = $t.'Description'.Trim()

    # Bucket
    $bucketId = $bucketMap[$bucketKey]
    if (-not $bucketId) {
        Write-Warning "  Bucket '$bucketKey' no encontrado en bucket map. Saltando: $taskName"
        $skipped++
        continue
    }

    # Prioridad
    $priorityNum = $priorityMap[$priorityText]
    if ($null -eq $priorityNum) { $priorityNum = 5 }

    # Assignment
    $assignments = @{}
    $userId = $cifToUserId[$cif]
    if ($userId) {
        $assignments[$userId] = @{
            "@odata.type" = "#microsoft.graph.plannerAssignment"
            orderHint     = " !"
        }
    }

    $body = @{
        planId        = $PlanId
        bucketId      = $bucketId
        title         = $taskName
        priority      = $priorityNum
        startDateTime = "${startDate}T06:00:00Z"
        dueDateTime   = "${dueDate}T23:59:59Z"
    }
    if ($assignments.Count -gt 0) {
        $body.assignments = $assignments
    }

    try {
        $taskResp = Invoke-MgGraphRequest `
            -Method POST `
            -Uri "https://graph.microsoft.com/v1.0/planner/tasks" `
            -Body ($body | ConvertTo-Json -Depth 10) `
            -ContentType "application/json"

        Write-Host "  ✓ $taskName" -ForegroundColor Green
        $created.Add(@{ Task = $taskResp; Description = $description; Name = $taskName })
    } catch {
        Write-Warning "  ✗ Error creando '$taskName': $_"
        $skipped++
    }

    Start-Sleep -Milliseconds $DelayMs
}

# ─── 6. Actualizar descripcion ───────────────────────────────────────────────
Write-Host "`n→ Actualizando descripcion de cada tarea..." -ForegroundColor Cyan
$detailErrors = 0

foreach ($c in $created) {
    $taskId = $c.Task.id
    $desc   = $c.Description
    $name   = $c.Name

    if ([string]::IsNullOrWhiteSpace($desc)) { continue }

    try {
        # Obtener ETag obligatorio para PATCH
        $detailResp = Invoke-MgGraphRequest `
            -Method GET `
            -Uri "https://graph.microsoft.com/v1.0/planner/tasks/$taskId/details" `
            -OutputType HttpResponseMessage

        $etag = $detailResp.Headers.ETag.Tag

        $patchBody = @{ description = $desc }

        Invoke-MgGraphRequest `
            -Method PATCH `
            -Uri "https://graph.microsoft.com/v1.0/planner/tasks/$taskId/details" `
            -Headers @{ "If-Match" = $etag } `
            -Body ($patchBody | ConvertTo-Json -Depth 5) `
            -ContentType "application/json" | Out-Null

        Write-Host "  ✓ Descripcion: $name" -ForegroundColor Green
    } catch {
        Write-Warning "  ✗ Error descripcion '$name': $_"
        $detailErrors++
    }

    Start-Sleep -Milliseconds $DelayMs
}

# ─── 7. Resumen ──────────────────────────────────────────────────────────────
Write-Host "`n════════════════════════════════════════════════════════" -ForegroundColor Yellow
Write-Host " Tareas creadas : $($created.Count) / $($tasks.Count)" -ForegroundColor Green
if ($skipped -gt 0)      { Write-Host " Saltadas        : $skipped" -ForegroundColor Red }
if ($detailErrors -gt 0) { Write-Host " Errores detalle : $detailErrors (descripcion)" -ForegroundColor Red }
Write-Host " Revisa el Planner del equipo para verificar." -ForegroundColor Yellow
Write-Host "════════════════════════════════════════════════════════" -ForegroundColor Yellow

Disconnect-MgGraph | Out-Null
