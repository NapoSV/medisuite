# Guía Avance 3 Fase B — Zair Benett Díaz Santos

> Responsable: Zair Benett Díaz Santos · CIF 2026010796 · GitHub @zsantos  
> Tarea: B6, expediente clínico, triaje y regresión visual  
> Rama personal: `b-expediente-ui` · PR listo: miércoles 14/10/2026, 23:59  
> Cierre interno: lunes 19/10/2026 · entrega externa: domingo 25/10/2026

## 🚦 Chequeo de desbloqueo

Tu rama puede comenzar cuando Héctor @hlopez confirme `feature/avance3-fase-b`. No dependes de B1 para el API de expediente. **No necesitas coordinar estilo con William** — ambos usan las clases Tailwind y componentes del Avance 2 ya mergeados; el lenguaje visual está cerrado (ver guía de William §B). Antes de rediseñar, abre `Expediente.tsx` y enumera todo lo que ya funciona: carga, edad, alergias, receta, triaje, historial ordenado y errores.

```bash
git fetch origin
git ls-remote --exit-code --heads origin feature/avance3-fase-b
git status --short
```

Si falta la rama, avisa a @hlopez. Si el árbol tiene cambios tuyos, preserva esos cambios antes de cambiar de rama. No borres componentes existentes por no aparecer en una imagen de referencia.

## 🎯 Qué vas a hacer y por qué

El expediente forma parte del objetivo original A1 y fue ampliado en A2 con signos vitales y recetas. Tu trabajo mejora su claridad para A3, pero debe conservar cada flujo clínico y permisos: DOCTOR puede crear recetas; NURSE/DOCTOR registran signos; otros roles solo lo que el servidor permita. El rediseño no modifica la semántica del API sin un acuerdo con backend.

> **🔒 Decisión cerrada (09/10/2026, commit `f78e73a`):** la prioridad clínica es **`NORMAL`, `URGENTE`, `EMERGENCIA`** — es el constraint efectivo en BD y lo que validan los seeds V8. Trabaja con esos tres valores en tu UI (selector, labels, mapping de color). El archivo `database/schema.sql` que menciona `LOW/MEDIUM/HIGH/CRITICAL` está desactualizado y se corrige por separado — no es tu bloqueador. No escondas errores con un `catch` genérico.

**Rúbrica directa:** integración y evolución A1/A2 (0.5), legibilidad/reutilización, arquitectura y evidencia funcional.

## 🛠️ Preparación

En Git Bash:

```bash
cd /c/Users/hlopez/medisuite
node --version
pnpm --version
git fetch origin
git switch -c b-expediente-ui origin/feature/avance3-fase-b
git branch --show-current
```

El proyecto usa React 19/Vite 8/TypeScript 6. `frontend/package.json` tiene `build` y `lint`, pero no `test`; no incluyas `pnpm test` en evidencia. Una rama aísla tu cambio, un commit guarda un paso lógico y el PR permite revisión.

## ✍️ Paso a paso del código

### A. Fija el contrato y el estado anterior

Lee `frontend/src/api/medicalRecords.ts`: `getMedicalRecord(patientId)` retorna expediente con `patient`, `prescriptions` y `vitalSigns`; `createVitalSign` hace POST a `/api/patients/{id}/vital-signs`. El enlace de receta actual navega a `/recetas/${rec.id}`; verifica esa ruta en `App.tsx` antes de cambiarla. En `Expediente.tsx`, `events` se ordenan por fecha de receta/signos. Haz una lista de regresión antes de editar y tómala como criterio de aceptación.

Usa la cabecera y cards del Avance 2 como base, con las clases Tailwind existentes. Las referencias visuales en `frontend/src/assets/pantallas/` orientan, no mandan — la UI final representa datos que el API realmente entrega. No inventes un campo de "prescripción activa" si no existe indicador de estado.

### B. Componentes de propiedad de Zair

Crea `frontend/src/components/expediente/`:

| Archivo | Responsabilidad | Estado que debe cubrir |
|---|---|---|
| `PatientHeader.tsx` | Nombre, DUI, edad, sangre, alergias y acciones permitidas | `birthDate`/alergias nulos, textos largos, móvil. |
| `ClinicalTabs.tsx` | Navegación Resumen, Historial, Signos, Recetas, Documentos | teclado, tab seleccionado, documentos ausentes. |
| `VitalsCard.tsx` | Lectura de signos y prioridad | unidades, nulos, prioridad coherente. |
| `PrescriptionPanel.tsx` | Recetas existentes y navegación a impresión | lista vacía, orden, medicación/dosis. |

La pestaña “Documentos” debe indicar “No disponible en esta fase” si no hay API, sin botón falso de subida. El panel de receta activa no debe inferir actividad a partir de “última receta” salvo que se rotule así. Ningún componente debe serializar o loguear datos clínicos al navegador más allá de la UI necesaria.

### C. Refactor de `Expediente.tsx`

Conserva `LoadingSpinner`, estado de error, `useAuth`, `useParams`, formulario de triaje y `getMedicalRecord`. Al extraer componentes, mantén una fuente única de datos `rec`. Evita repetir fetch por cada tab. Si el `id` de ruta no es numérico/positivo, muestra error claro sin llamar al API.

El formulario usa números opcionales. Comprueba rangos y mensajes (ejemplo: frecuencia cardíaca entera; campos vacíos no se convierten en `NaN`). Después de guardar, actualiza signos sin borrar lo que el usuario escribió si hubo error. Un 401 debe seguir el comportamiento de `apiFetch` de la Fase A; un 403 por rol no debe mostrarse como “expediente no encontrado”. Mantén color + texto en prioridades.

### D. Prioridad y aislamiento multi-tenant

> **🔒 Decisión cerrada:** prioridad = `NORMAL / URGENTE / EMERGENCIA` (ver aviso al inicio de la guía). Tu UI usa esos tres valores en selector, validación, label y mapping de color. No hagas migración de BD por tu cuenta. Si notas que `database/schema.sql` tiene valores viejos, menciónalo en el PR como observación — Vásquez/Flores corrigen el archivo aparte.

Haz una prueba de dos tenants: un usuario de clínica A no debe abrir el expediente de clínica B solo cambiando el ID de URL. Si el backend devuelve datos de B, este es un fallo de seguridad que debe detener el PR integrador y asignarse a Flores/backend con prueba. El frontend no puede reparar aislamiento ocultando una sección.

### E. Evidencia para el documento final

Entrega a Nicole capturas antes/después de cabecera, signos, recetas y estado vacío; describe el beneficio de extraer componentes. Añade tu conclusión individual. Confirma navegación de Avance 2: crear paciente → abrir expediente → registrar signos → emitir receta → imprimir. No incluyas datos de paciente real.

## ✅ Cómo verificar

```bash
cd frontend
pnpm install --frozen-lockfile
pnpm build
pnpm lint
pnpm dev
```

Prueba el navegador con DOCTOR, NURSE y RECEPTIONIST en 360px y desktop. Repite con expediente vacío y con recetas/signos. `build` y `lint` deben pasar; el smoke test manual valida las interacciones que TypeScript no cubre.

**Autovalidación propuesta:** `scripts/validate-b6-expediente.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
test -f frontend/src/pages/Expediente.tsx
for file in PatientHeader ClinicalTabs VitalsCard PrescriptionPanel; do
  test -f "frontend/src/components/expediente/${file}.tsx"
done
(cd frontend && pnpm build && pnpm lint)
printf 'OK B6 expediente\n'
```

Ejecuta `bash scripts/validate-b6-expediente.sh`. Anota resultados manuales de prioridad y dos tenants por separado.

## 📤 Commit, push y PR — acciones tuyas

Revisa staged, secretos y capturas antes de commit; no incluyas coautoría de IA.

```bash
git status --short
git diff --check
git add frontend/src/pages/Expediente.tsx frontend/src/components/expediente/PatientHeader.tsx frontend/src/components/expediente/ClinicalTabs.tsx frontend/src/components/expediente/VitalsCard.tsx frontend/src/components/expediente/PrescriptionPanel.tsx scripts/validate-b6-expediente.sh
git diff --cached --check
git diff --cached
git commit -m "feat(expediente-ui): organiza historial clinico y signos vitales"
git push -u origin b-expediente-ui
```

Abre PR a `feature/avance3-fase-b` y solicita review de @hlopez, @wmelgar y @wflores. Si hay cambio adicional de prioridad, inclúyelo en otro commit/PR coordinado con backend.

### Checklist para el PR

```markdown
## B6 — Zair Benett Díaz Santos (@zsantos)
- [ ] El contrato real de prioridad quedó confirmado con QA/backend.
- [ ] Crear receta y signos conserva permisos del Avance 2.
- [ ] Carga, vacío, error, 401 y 403 muestran mensajes correctos.
- [ ] Historial sigue ordenado, receta sigue imprimible.
- [ ] Navegación y responsive probados en móvil/desktop.
- [ ] `pnpm build`, `pnpm lint` y script B6 terminan OK.
- [ ] Caso de ID de otro tenant revisado con backend.
- [ ] No hay PHI real, secretos o IP privadas staged.
```

## 🆘 Qué hacer si algo falla

| Síntoma | Acción concreta |
|---|---|
| Guardar triaje devuelve constraint | Compara `priority` enviado con CHECK real; coordina cambio con backend. |
| Receta abre página equivocada | Comprueba ruta actual en `App.tsx`; conserva `rec.id` como parámetro. |
| Pantalla vacía por 403 | Mira status de `ApiError` y rol; no lo traduzcas a “no encontrado”. |
| Se mezclan pacientes de clínicas | Detén integración y abre incidencia backend con IDs de prueba anonimizados. |
| Build falla al extraer componentes | Revisa imports/tipos exportados y JSX; no uses `any` para ocultar errores. |

## 📚 Cinco preguntas de defensa

1. **¿Qué se conserva del Avance 2?** El expediente carga signos y recetas, permite registrar triaje al rol adecuado e inicia recetas para DOCTOR. El rediseño reorganiza esas funciones sin eliminarlas.
2. **¿Qué es el contrato de prioridad?** Es el conjunto de valores aceptados por frontend, servicio y BD. Si difieren, el usuario ve una opción que la base puede rechazar.
3. **¿Cómo manejas datos ausentes?** Muestras estados vacíos y campos opcionales con claridad. No inventas documentos, alergias o recetas activas.
4. **¿Basta ocultar una acción por rol?** No; el backend debe autorizarla. La UI evita confusión, pero la seguridad se prueba intentando acceder directamente al endpoint.
5. **¿Qué mejora la cohesión?** Cada componente muestra una parte relacionada del expediente y la página coordina la carga/navegación. Esto facilita probar y mantener cambios sin duplicar lógica.

Lee [William](William_Melgar_Rivas.md), [Flores](Walter_Flores_Hernandez.md) y [Erika](Erika_Fuentes_Ortiz.md) para la demo clínica completa.
