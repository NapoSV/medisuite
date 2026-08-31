<#
.SYNOPSIS
    Agrega notas (descripcion) a las tareas ya creadas en Planner v4.0.
    Usar despues de haber corrido crear_tareas_avance2.ps1.

.NOTES
    En la API v4.0 las notas van en el task mismo (PATCH /tasks/{id}),
    no en un sub-recurso /details como en la Graph API clasica.
    Requiere el mismo Bearer token del navegador.
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Token,

    [string]$CsvPath = "$PSScriptRoot\tareas_avance2.csv",
    [string]$PlanId  = "IMmXgJ4IQU-U8Vq-MIdvN2QACMUM",
    [int]   $DelayMs = 600
)

$baseUrl = "https://api.planner.svc.cloud.microsoft/taskapi/v4.0"
$headers = @{
    Authorization  = "Bearer $Token"
    "Content-Type" = "application/json"
    Accept         = "application/json"
    "OData-Version" = "4.0"
}

# ─── 1. Cargar CSV (nombre → descripcion) ────────────────────────────────────
$csv = Import-Csv $CsvPath -Encoding UTF8
$nameToDesc = @{}
foreach ($row in $csv) {
    $name = $row.'Task Name'.Trim()
    $desc = $row.'Description'.Trim()
    if ($name -and $desc) { $nameToDesc[$name] = $desc }
}
Write-Host "→ Tareas con descripcion en CSV: $($nameToDesc.Count)" -ForegroundColor Cyan

# ─── 2. Obtener todas las tareas del plan (con paginacion) ───────────────────
Write-Host "→ Obteniendo tareas del plan..." -ForegroundColor Cyan
$allTasks = [System.Collections.Generic.List[object]]::new()
$url = "$baseUrl/plans('$PlanId')/tasks"

do {
    $resp = Invoke-RestMethod -Method GET -Uri $url -Headers $headers
    foreach ($t in $resp.value) { $allTasks.Add($t) }
    $url = $resp.'@odata.nextLink'
} while ($url)

Write-Host "→ Tareas encontradas en el plan: $($allTasks.Count)" -ForegroundColor Cyan

# ─── 3. PATCH de cada tarea con su nota ──────────────────────────────────────
Write-Host "`n→ Agregando notas..." -ForegroundColor Cyan
$ok = 0; $errors = 0

foreach ($task in $allTasks) {
    $name = $task.displayName
    $desc = $nameToDesc[$name]
    if ([string]::IsNullOrWhiteSpace($desc)) { continue }

    $etag = $task.'@odata.etag'
    $patchHeaders = $headers.Clone()
    $patchHeaders['If-Match'] = $etag

    $body = @{ notes = $desc } | ConvertTo-Json

    try {
        Invoke-RestMethod -Method PATCH `
            -Uri "$baseUrl/tasks/$($task.id)" `
            -Headers $patchHeaders `
            -Body $body | Out-Null
        Write-Host "  ✓ $name" -ForegroundColor Green
        $ok++
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        Write-Warning "  ✗ [$status] $name — $_"
        if ($status -eq 401) {
            Write-Host "`n[!] Token expirado. Recarga Planner, copia nuevo token y vuelve a correr." -ForegroundColor Red
            break
        }
        $errors++
    }

    Start-Sleep -Milliseconds $DelayMs
}

Write-Host "`n══════════════════════════════════════════════" -ForegroundColor Yellow
Write-Host " Notas agregadas : $ok" -ForegroundColor Green
if ($errors -gt 0) { Write-Host " Errores         : $errors" -ForegroundColor Red }
Write-Host "══════════════════════════════════════════════" -ForegroundColor Yellow
