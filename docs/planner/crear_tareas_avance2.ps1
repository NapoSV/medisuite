<#
.SYNOPSIS
    Carga masiva de 89 tareas Avance 2 usando el token de sesion del navegador.
    No requiere Graph SDK ni permisos de administrador.

.NOTES
    El token se obtiene de F12 -> Network -> cualquier peticion fetch -> Headers -> Authorization.
    Expira en ~1 hora; si falla con 401, recargar Planner y copiar el token nuevo.
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Token,

    [string]$CsvPath       = "$PSScriptRoot\tareas_avance2.csv",
    [string]$BucketMapPath = "$PSScriptRoot\bucket_map_avance2.json",
    [string]$PlanId        = "IMmXgJ4IQU-U8Vq-MIdvN2QACMUM",
    [int]   $DelayMs       = 800
)

$ErrorActionPreference = 'Continue'
$baseUrl = "https://api.planner.svc.cloud.microsoft/taskapi/v4.0"
$headers = @{
    Authorization  = "Bearer $Token"
    "Content-Type" = "application/json"
    Accept         = "application/json"
    "OData-Version" = "4.0"
}

$priorityMap = @{ "Urgent"=1; "Important"=3; "Medium"=5; "Low"=9 }

# ─── Cargar archivos ──────────────────────────────────────────────────────────
$bucketMap = Get-Content $BucketMapPath -Raw | ConvertFrom-Json -AsHashtable
$tasks     = Import-Csv $CsvPath -Encoding UTF8
Write-Host "→ Tareas a crear: $($tasks.Count)" -ForegroundColor Cyan
Write-Host "→ Buckets: $($bucketMap.Keys -join ', ')" -ForegroundColor Cyan

# ─── Crear tareas ─────────────────────────────────────────────────────────────
$created  = [System.Collections.Generic.List[hashtable]]::new()
$skipped  = 0

foreach ($t in $tasks) {
    $name      = $t.'Task Name'.Trim()
    $bucketKey = $t.'Bucket Name'.Trim()
    $startDate = $t.'Start Date'.Trim()
    $dueDate   = $t.'Due Date'.Trim()
    $priority  = $priorityMap[$t.'Priority'.Trim()]
    if (-not $priority) { $priority = 5 }

    $bucketId = $bucketMap[$bucketKey]
    if (-not $bucketId) {
        Write-Warning "  Bucket '$bucketKey' no encontrado. Saltando: $name"
        $skipped++
        continue
    }

    $body = @{
        planId      = $PlanId
        bucketId    = $bucketId
        displayName = $name
        priority    = $priority
        startDateTime = @{ date = $startDate }
        dueDateTime   = @{ date = $dueDate }
    } | ConvertTo-Json -Depth 5

    try {
        $resp = Invoke-RestMethod -Method POST -Uri "$baseUrl/tasks" `
            -Headers $headers -Body $body
        Write-Host "  ✓ $name" -ForegroundColor Green
        $created.Add(@{ Id = $resp.id; Desc = $t.'Description'.Trim(); Name = $name })
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        Write-Warning "  ✗ [$status] $name — $_"
        if ($status -eq 401) {
            Write-Host "`n[!] Token expirado. Recargar Planner, copiar nuevo token y volver a correr." -ForegroundColor Red
            break
        }
        $skipped++
    }

    Start-Sleep -Milliseconds $DelayMs
}

# ─── Actualizar descripciones ─────────────────────────────────────────────────
Write-Host "`n→ Actualizando descripciones ($($created.Count) tareas)..." -ForegroundColor Cyan
$descErrors = 0

foreach ($c in $created) {
    if ([string]::IsNullOrWhiteSpace($c.Desc)) { continue }

    try {
        # Obtener ETag
        $detailResp = Invoke-WebRequest -Method GET -Uri "$baseUrl/tasks/$($c.Id)/details" `
            -Headers $headers
        $etag = ($detailResp.Headers['ETag'] ?? $detailResp.Headers['etag'])

        $patchHeaders = $headers.Clone()
        $patchHeaders['If-Match'] = $etag

        $patchBody = @{ notes = $c.Desc } | ConvertTo-Json

        Invoke-RestMethod -Method PATCH -Uri "$baseUrl/tasks/$($c.Id)/details" `
            -Headers $patchHeaders -Body $patchBody | Out-Null

        Write-Host "  ✓ Desc: $($c.Name)" -ForegroundColor Green
    } catch {
        Write-Warning "  ✗ Desc '$($c.Name)': $_"
        $descErrors++
    }

    Start-Sleep -Milliseconds $DelayMs
}

# ─── Resumen ──────────────────────────────────────────────────────────────────
Write-Host "`n══════════════════════════════════════════════" -ForegroundColor Yellow
Write-Host " Creadas  : $($created.Count) / $($tasks.Count)" -ForegroundColor Green
if ($skipped -gt 0)    { Write-Host " Saltadas : $skipped" -ForegroundColor Red }
if ($descErrors -gt 0) { Write-Host " Sin desc : $descErrors" -ForegroundColor Red }
Write-Host " Abre el Planner para verificar." -ForegroundColor Yellow
Write-Host "══════════════════════════════════════════════" -ForegroundColor Yellow
