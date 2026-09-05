<#
.SYNOPSIS
    Renombra la tarea contingente de Erika (ERK-1) a EK-01 con los valores correctos.
    Busca la tarea por nombre parcial y hace PATCH en la API Planner v4.0.

.NOTES
    El token se obtiene de F12 -> Network -> peticion a planner -> Headers -> Authorization.
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Token,

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

# ─── Bucket Semana 2 (donde debe quedar EK-01) ───────────────────────────────
$bucketSemana2 = "h4ZyC8qB40io-r8MBEERvWQAAEkQ"

# ─── 1. Obtener todas las tareas del plan ────────────────────────────────────
Write-Host "-> Buscando tarea contingente de Erika..." -ForegroundColor Cyan
$allTasks = [System.Collections.Generic.List[object]]::new()
$url = "$baseUrl/plans('$PlanId')/tasks"
do {
    $resp = Invoke-RestMethod -Method GET -Uri $url -Headers $headers
    foreach ($t in $resp.value) { $allTasks.Add($t) }
    $url = $resp.'@odata.nextLink'
} while ($url)

# ─── 2. Encontrar la tarea (busca por texto en el nombre) ────────────────────
$target = $allTasks | Where-Object {
    $_.displayName -match "ERK|Contingent|contingente|activar si commitea"
}

if (-not $target) {
    Write-Host "[!] No se encontro la tarea contingente. Verifica el nombre en Planner." -ForegroundColor Red
    Write-Host "    Tareas actuales de Erika:" -ForegroundColor Yellow
    $allTasks | Where-Object { $_.displayName -match "Erika|EK|D-09|V-09|V-10|V-11|V-12|V-13|PIC-01" } |
        Select-Object displayName, id | Format-Table
    exit 1
}

if ($target.Count -gt 1) {
    Write-Host "[!] Se encontraron $($target.Count) tareas. Usando la primera:" -ForegroundColor Yellow
    $target | ForEach-Object { Write-Host "    - $($_.displayName)" }
    $target = $target[0]
}

Write-Host "-> Tarea encontrada: '$($target.displayName)' [id: $($target.id)]" -ForegroundColor Green

# ─── 3. PATCH: renombrar + mover a Semana 2 + actualizar fechas y prioridad ──
$etag = $target.'@odata.etag'
$patchHeaders = $headers.Clone()
$patchHeaders['If-Match'] = $etag

$patchBody = @{
    displayName   = "EK-01 Verificacion QA pantallas frontend (smoke test manual)"
    bucketId      = $bucketSemana2
    priority      = 3   # Important
    startDateTime = @{ date = "2026-09-07" }
    dueDateTime   = @{ date = "2026-09-13" }
} | ConvertTo-Json -Depth 5

try {
    Invoke-RestMethod -Method PATCH `
        -Uri "$baseUrl/tasks/$($target.id)" `
        -Headers $patchHeaders `
        -Body $patchBody | Out-Null
    Write-Host "  OK - Tarea renombrada a EK-01" -ForegroundColor Green
} catch {
    $status = $_.Exception.Response.StatusCode.value__
    Write-Warning "  ERROR [$status]: $_"
    if ($status -eq 401) {
        Write-Host "`n[!] Token expirado. Recarga Planner, copia nuevo token y vuelve a correr." -ForegroundColor Red
    }
    exit 1
}

Start-Sleep -Milliseconds $DelayMs

# ─── 4. PATCH: agregar descripcion/notas ─────────────────────────────────────
$etag2 = (Invoke-RestMethod -Method GET -Uri "$baseUrl/tasks/$($target.id)" -Headers $headers).'@odata.etag'
$noteHeaders = $headers.Clone()
$noteHeaders['If-Match'] = $etag2

$noteBody = @{
    notes = "Verificar login + dashboard + pacientes + citas en Chrome y Firefox; reportar bugs"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Method PATCH `
        -Uri "$baseUrl/tasks/$($target.id)" `
        -Headers $noteHeaders `
        -Body $noteBody | Out-Null
    Write-Host "  OK - Notas actualizadas" -ForegroundColor Green
} catch {
    Write-Warning "  Nota no se pudo actualizar: $_"
}

Write-Host "`n============================================" -ForegroundColor Yellow
Write-Host " EK-01 lista en Semana 2 (07/09 - 13/09)" -ForegroundColor Green
Write-Host " Abre el Planner y asignala a Erika (2026011709)." -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Yellow
