# Guía Avance 3 Fase B — William Ariel Melgar Rivas

> Responsable: William Ariel Melgar Rivas · CIF 2026011736 · GitHub @wmelgar  
> Tarea: B1b, dashboard frontend con contrato nuevo y pruebas de roles  
> Rama personal: `b1b-dashboard-frontend` · PR listo: sábado 17/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

La implementación final depende del PR [B1 de Héctor](Hector_Lopez_Ruiz.md) @hlopez. Puedes construir componentes visuales puros desde el día 1, pero no integrar `fetchDashboardMetrics()` al contrato nuevo hasta que Héctor publique el JSON y permisos por rol. Coordina estilo con [Zair](Zair_Diaz_Santos.md) @zsantos.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git log --oneline origin/feature/avance3-fase-b -20
git status --short
```

Pide a @hlopez el enlace al PR B1 mergeado. Un screenshot o una firma verbal no confirma la forma del API.

## 🎯 Qué vas a hacer y por qué

El dashboard actual ya tiene cuatro KPI, acciones rápidas, carga y error. La propuesta agrega lista de sala de espera, alertas y ocupación. Tu tarea es presentar **datos reales del backend** de manera útil y accesible sin romper navegación, roles y estados existentes. No muestres un indicador de “mantenimiento” o porcentaje de ocupación si el backend no puede calcularlo.

Tu contribución transversal a A1/A2 es comprobar que los cuatro roles ven rutas y tarjetas coherentes y que `/dashboard` no expone datos de otra clínica. La autorización real es del backend; el frontend también evita presentar acciones que el rol no puede usar.

**Rúbrica directa:** integración de avances, legibilidad, separación de responsabilidades y evidencia de funcionamiento.

## 🛠️ Preparación

En Git Bash, desde tu clon:

```bash
cd /c/Users/hlopez/medisuite
node --version
pnpm --version
git fetch origin
git switch -c b1b-dashboard-frontend origin/feature/avance3-fase-b
git branch --show-current
```

`frontend/package.json` usa React 19, Vite 8 y TypeScript 6; sus scripts actuales son `dev`, `build`, `lint`, `preview`, **no `test`**. Una rama aísla tu modificación y un PR la somete a revisión. Si falta `pnpm`, consulta la versión usada por el equipo antes de instalarla. No uses `npm install` y `pnpm install` indistintamente en el mismo árbol.

## ✍️ Paso a paso del código

### A. Congela el contrato

> **🔒 Decisión cerrada (09/10/2026 — H. López):** el contrato del dashboard ya está publicado por B1 en `backend/src/main/java/com/sv/grupo7/medisuite/dto/dashboard/`. Usa estos campos exactos — no hay negociación pendiente. Los campos que no aplican a un rol llegan `null` y los omite Jackson (`@JsonInclude(NON_NULL)`); tu `TypeScript` los declara **opcionales**.

Reemplaza `DashboardMetrics` en `frontend/src/api/dashboard.ts` por este tipo:

```ts
export interface WaitingRoomEntry {
  appointmentId: number;
  patientName: string;
  scheduledAt: string;   // ISO OffsetDateTime
  status: string;        // SCHEDULED, CHECKED_IN, etc.
}

export interface CriticalAlert {
  vitalSignId: number;
  patientId: number;
  priority: 'NORMAL' | 'URGENTE' | 'EMERGENCIA';
  recordedAt: string;    // ISO OffsetDateTime
}

export interface OccupancySlot {
  hour: number;          // 0-23 (America/El_Salvador)
  appointments: number;
}

export interface DashboardMetrics {
  appointmentsToday?: number;
  activePatients?: number;
  prescriptionsIssued?: number;    // legado del Avance 2, aún presente
  alerts?: number;                 // legado del Avance 2, aún presente
  prescriptionsThisWeek?: number;  // nuevo A3
  criticalAlerts?: number;         // nuevo A3, conteo de alertas EMERGENCIA
  waitingRoom?: WaitingRoomEntry[];
  occupancyByHour?: OccupancySlot[];
  clinicalAlerts?: CriticalAlert[];
}
```

Actualiza `fetchDashboardMetrics()` en el mismo archivo. Debe seguir usando `apiFetch` para adjuntar JWT y manejar 401/403 como el resto del frontend. Evita `fetch` directo y URLs absolutas adicionales.

**Visibilidad por rol** (así entrega el backend, tu UI no filtra):
- `ADMIN`: todos los campos.
- `DOCTOR`: campos de su alcance (`appointmentsToday`, `prescriptionsThisWeek`, `criticalAlerts`, `clinicalAlerts`, `waitingRoom` propio).
- `NURSE`: triaje y espera (`waitingRoom`, `criticalAlerts`, `clinicalAlerts`); sin recetas.
- `RECEPTIONIST`: citas y espera (`appointmentsToday`, `waitingRoom`, `occupancyByHour`); sin detalle clínico.

Si un campo llega `undefined`, muestra estado vacío apropiado (skeleton o "Sin datos"), nunca `NaN` o `undefined` en pantalla.

### B. Extrae componentes, preservando comportamiento

Crea `frontend/src/components/dashboard/`:

| Archivo | Datos de entrada | Aceptación |
|---|---|---|
| `KpiCard.tsx` | etiqueta, valor, icono, loading | Número formateado, skeleton y etiqueta semántica. |
| `WaitingRoomTable.tsx` | filas permitidas | Encabezados, vacío, fecha/hora local, prioridad clara. |
| `CriticalAlertPanel.tsx` | alertas autorizadas | Texto legible además de color; no muestra datos privados a recepción. |
| `OccupancyChart.tsx` | conteos por hora | Barras CSS con escala, leyenda y tabla/labels accesibles. |

Si un componente solo se usa una vez y ocupa pocas líneas, evalúa si extraerlo realmente mejora cohesión. No crees un componente por cada `div` para aparentar arquitectura. Mantén clases Tailwind existentes y coordina con Zair el lenguaje visual; los diseños de referencia no justifican ocultar datos o enlaces funcionales.

### C. Actualiza `DashboardPage.tsx`

Conserva `useAuth`, `ErrorAlert`, botón de actualizar y acciones rápidas. Define por rol cuáles KPI, alertas o listas aparecen. Si el backend omite un campo por rol, el tipo debe admitirlo y la vista mostrar un estado vacío apropiado, no `undefined` o `NaN`. Nunca asumas `ADMIN` cuando `user` es nulo: el componente debe esperar la carga de autenticación o redirigir según rutas existentes.

Revisa la ruta exacta de botones: el proyecto usa `/pacientes`, `/doctores`, `/citas/nueva` y otras rutas en español. No cambies a `/patients` o `/appointments` del texto del Avance 2 si no existen en `App.tsx`.

### D. Interacciones y accesibilidad

Prueba en 360px, 768px y desktop: cards sin recorte, tabla con scroll o diseño adaptado, alertas legibles y foco visible en refresh. El gráfico debe tener alternativas textuales; el color no puede ser el único indicador de prioridad. Evita mostrar nombre/DUI en alertas a roles sin permiso. No guardes PHI en consola.

### E. Evidencia A1/A2 y documento final

Toma capturas de cuatro roles en el mismo tenant y un segundo tenant de prueba. Oculta credenciales/tokens. Entrega a Nicole una explicación de antes/después del dashboard y a Héctor una tabla de campos por rol. Aporta tu conclusión personal y un resumen de cómo se conectan React, API JWT, backend y JDBC para la defensa.

## ✅ Cómo verificar

```bash
cd frontend
pnpm install --frozen-lockfile
pnpm build
pnpm lint
pnpm dev
```

`pnpm build` debe compilar TypeScript. `pnpm lint` debe terminar sin errores de tu código. Abre la URL mostrada por Vite y prueba cuatro roles con datos demo. Si no hay `pnpm-lock.yaml` o el lock está desactualizado, comunica el bloqueo antes de quitar `--frozen-lockfile`.

**Autovalidación propuesta:** `scripts/validate-b1b-dashboard-ui.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f frontend/src/api/dashboard.ts
test -f frontend/src/pages/DashboardPage.tsx
for file in KpiCard WaitingRoomTable CriticalAlertPanel OccupancyChart; do
  test -f "frontend/src/components/dashboard/${file}.tsx"
done
(cd frontend && pnpm build && pnpm lint)
printf 'OK B1b dashboard UI\n'
```

Ejecuta `bash scripts/validate-b1b-dashboard-ui.sh`. Este script no sustituye la prueba en navegador.

## 📤 Commit, push y PR — acciones tuyas

Revisa staged antes de commit. Un commit debe describir una unidad lógica y no incluir coautoría de IA.

```bash
git status --short
git diff --check
git add frontend/src/api/dashboard.ts frontend/src/pages/DashboardPage.tsx frontend/src/components/dashboard/KpiCard.tsx frontend/src/components/dashboard/WaitingRoomTable.tsx frontend/src/components/dashboard/CriticalAlertPanel.tsx frontend/src/components/dashboard/OccupancyChart.tsx scripts/validate-b1b-dashboard-ui.sh
git diff --cached --check
git diff --cached
git commit -m "feat(dashboard-ui): muestra metricas clinicas por rol"
git push -u origin b1b-dashboard-frontend
```

PR con base `feature/avance3-fase-b`; pide review a @hlopez y @zsantos. Adjunta capturas sin datos reales y tabla del contrato. No subas `.env`, tokens, IP privadas, `node_modules` ni `dist`.

### Checklist para el PR

```markdown
## B1b — William Ariel Melgar Rivas (@wmelgar)
- [ ] B1 mergeado y tipos sincronizados con el JSON real.
- [ ] Los cuatro roles tienen vistas coherentes y el servidor filtra datos.
- [ ] Carga, vacío, error, reintento y responsive probados.
- [ ] Rutas de acciones rápidas funcionan.
- [ ] `pnpm build` y `pnpm lint` pasan.
- [ ] `bash scripts/validate-b1b-dashboard-ui.sh` termina OK.
- [ ] Capturas sin datos sensibles adjuntas.
- [ ] No hay secretos ni IP privadas staged.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| `Property does not exist` | Compara el tipo TS con JSON del PR B1; no agregues casts `any` para ocultarlo. |
| Tabla sin filas | Comprueba rol, respuesta API y significado de “sala de espera” acordado. |
| 401/403 | Verifica sesión y permisos; no copies JWT al chat del grupo. |
| Gráfico no cabe en móvil | Reduce anchura, permite scroll o cambia a lista accesible; vuelve a probar 360px. |
| `pnpm lint` falla | Corrige el archivo indicado sin desactivar reglas globales. |

## 📚 Cinco preguntas de defensa

1. **¿Qué consume el dashboard?** Consume `GET /api/dashboard/metrics` con el JWT actual. Los tipos TS documentan el contrato que el backend realmente devuelve.
2. **¿Cómo cambian las vistas por rol?** La interfaz presenta solo los campos y acciones pertinentes, pero el servidor sigue siendo quien autoriza. Probé los cuatro roles y un segundo tenant.
3. **¿Qué haces cuando falla la red?** Muestro un error legible con opción de reintento y conservo un estado de carga claro. No muestro ceros como si fueran datos comprobados.
4. **¿Qué hace accesible al gráfico?** Tiene etiquetas y valores textuales, no depende solo de barras o colores. La información puede leerse en móvil y con lector de pantalla.
5. **¿Qué preservaste del Avance 2?** Los KPI, acciones rápidas, autenticación, navegación y estados de carga/error existentes. Las nuevas vistas se añadieron sin romper esos flujos.

Lee [Héctor](Hector_Lopez_Ruiz.md) para el contrato de backend y [Zair](Zair_Diaz_Santos.md) para consistencia clínica del frontend.
