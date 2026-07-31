<#
.SYNOPSIS
    Carga masiva de tareas del Avance 1 de MediSuite a Microsoft Planner
    usando Microsoft Graph directamente (alternativa a Power Automate).

.DESCRIPTION
    - Lee tareas_avance1.csv (o .xlsx exportado a CSV).
    - Resuelve correos M365 a UserIds (Azure AD GUIDs).
    - Crea cada tarea con POST /planner/tasks.
    - Actualiza descripción y checklist con PATCH /planner/tasks/{id}/details.
    - Usa JSON batching (hasta 20 requests por batch) para eficiencia.

.PREREQUISITES
    - PowerShell 7+.
    - Módulo Microsoft.Graph.Authentication:
        Install-Module Microsoft.Graph -Scope CurrentUser
    - Permisos delegados: Tasks.ReadWrite + Group.ReadWrite.All + User.Read.All.
    - PlanId y BucketIds ya obtenidos del Planner (ver README).

.EXAMPLE
    .\graph_bulk_import.ps1 `
        -CsvPath ".\tareas_avance1.csv" `
        -PlanId "AAAAAAAA-BBBB-CCCC-DDDD-EEEEEEEEEEEE" `
        -BucketMapPath ".\bucket_map.json"

    Donde bucket_map.json es:
    {
      "Documento": "bucket-guid-1",
      "Backend":   "bucket-guid-2",
      "Frontend":  "bucket-guid-3",
      "QA":        "bucket-guid-4",
      "BD":        "bucket-guid-5"
    }

.NOTES
    Autor: MediSuite team · 2026-07-26
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$CsvPath,

    [Parameter(Mandatory=$true)]
    [string]$PlanId,

    [Parameter(Mandatory=$true)]
    [string]$BucketMapPath,

    [int]$DelayMs = 500
)

# ─── 1. Conectar a Graph ─────────────────────────────────────────────────────
Write-Host "→ Conectando a Microsoft Graph..." -ForegroundColor Cyan
Import-Module Microsoft.Graph.Authentication -ErrorAction Stop
Connect-MgGraph -Scopes "Tasks.ReadWrite","Group.ReadWrite.All","User.Read.All" -NoWelcome

# ─── 2. Cargar bucket map ────────────────────────────────────────────────────
if (-not (Test-Path $BucketMapPath)) {
    throw "No se encontró el archivo de bucket map: $BucketMapPath"
}
$bucketMap = Get-Content $BucketMapPath -Raw | ConvertFrom-Json -AsHashtable
Write-Host "→ Buckets cargados: $($bucketMap.Keys -join ', ')" -ForegroundColor Cyan

# ─── 3. Cargar CSV ───────────────────────────────────────────────────────────
if (-not (Test-Path $CsvPath)) {
    throw "No se encontró el CSV: $CsvPath"
}
$tasks = Import-Csv $CsvPath -Encoding UTF8
Write-Host "→ Tareas a cargar: $($tasks.Count)" -ForegroundColor Cyan

# ─── 4. Resolver correos a UserIds (cache) ───────────────────────────────────
$emailToUserId = @{}
$allEmails = $tasks | ForEach-Object { $_.AssigneeEmails -split ';' } | Sort-Object -Unique

foreach ($email in $allEmails) {
    $trim = $email.Trim()
    if ([string]::IsNullOrWhiteSpace($trim)) { continue }
    if ($trim -eq 'todos@medisuite.local') { continue }  # placeholder — se ignora
    if ($trim -like '*@medisuite.local') {
        Write-Warning "  Placeholder '$trim' — reemplazar por correo M365 real antes de correr."
        continue
    }

    try {
        $resp = Invoke-MgGraphRequest -Method GET -Uri "https://graph.microsoft.com/v1.0/users/$trim"
        $emailToUserId[$trim] = $resp.id
        Write-Host "  ✓ $trim → $($resp.id)"
    } catch {
        Write-Warning "  ✗ No se pudo resolver $trim : $_"
    }
}

# ─── 5. Crear tareas (una por una para simplicidad; batching opcional) ──────
$created = @()

foreach ($t in $tasks) {
    $bucketId = $bucketMap[$t.Bucket]
    if (-not $bucketId) {
        Write-Warning "Bucket '$($t.Bucket)' no encontrado en bucket map. Saltando: $($t.Title)"
        continue
    }

    # Construir assignments
    $assignments = @{}
    foreach ($e in ($t.AssigneeEmails -split ';')) {
        $trim = $e.Trim()
        if ($emailToUserId.ContainsKey($trim)) {
            $uid = $emailToUserId[$trim]
            $assignments[$uid] = @{
                "@odata.type"    = "#microsoft.graph.plannerAssignment"
                orderHint        = " !"
            }
        }
    }

    $body = @{
        planId       = $PlanId
        bucketId     = $bucketId
        title        = $t.Title
        priority     = [int]$t.Priority
        startDateTime = "$($t.StartDate)T00:00:00Z"
        dueDateTime   = "$($t.DueDate)T23:59:59Z"
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

        Write-Host "  ✓ Creada: $($t.Title)" -ForegroundColor Green
        $created += @{ Task = $taskResp; Row = $t }

    } catch {
        Write-Warning "  ✗ Error creando '$($t.Title)': $_"
    }

    Start-Sleep -Milliseconds $DelayMs
}

# ─── 6. Actualizar descripción y checklist ──────────────────────────────────
Write-Host "`n→ Actualizando descripción y checklist..." -ForegroundColor Cyan

foreach ($c in $created) {
    $task = $c.Task
    $row  = $c.Row

    # Get details primero para obtener el ETag
    try {
        $detailResp = Invoke-MgGraphRequest -Method GET `
            -Uri "https://graph.microsoft.com/v1.0/planner/tasks/$($task.id)/details" `
            -OutputType HttpResponseMessage

        $etag = $detailResp.Headers.ETag.Tag

        # Construir checklist
        $checklistItems = @{}
        if (-not [string]::IsNullOrWhiteSpace($row.Checklist)) {
            foreach ($item in ($row.Checklist -split '\|')) {
                $itemGuid = [guid]::NewGuid().ToString()
                $checklistItems[$itemGuid] = @{
                    "@odata.type" = "#microsoft.graph.plannerChecklistItem"
                    title         = $item.Trim()
                    isChecked     = $false
                }
            }
        }

        $patchBody = @{
            description = $row.Description
        }
        if ($checklistItems.Count -gt 0) {
            $patchBody.checklist = $checklistItems
        }

        $patchHeaders = @{
            "If-Match" = $etag
        }

        Invoke-MgGraphRequest -Method PATCH `
            -Uri "https://graph.microsoft.com/v1.0/planner/tasks/$($task.id)/details" `
            -Headers $patchHeaders `
            -Body ($patchBody | ConvertTo-Json -Depth 10) `
            -ContentType "application/json" | Out-Null

        Write-Host "  ✓ Detalles actualizados: $($row.Title)" -ForegroundColor Green

    } catch {
        Write-Warning "  ✗ Error actualizando detalles de '$($row.Title)': $_"
    }

    Start-Sleep -Milliseconds $DelayMs
}

# ─── 7. Resumen ──────────────────────────────────────────────────────────────
Write-Host "`n════════════════════════════════════════════" -ForegroundColor Yellow
Write-Host " ✅ Carga completada: $($created.Count) / $($tasks.Count) tareas" -ForegroundColor Green
Write-Host " Revisa el Planner del equipo para verificar." -ForegroundColor Yellow
Write-Host "════════════════════════════════════════════" -ForegroundColor Yellow

Disconnect-MgGraph | Out-Null
