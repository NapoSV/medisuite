# EJECUCIÓN AVANCE 2 — Instrucciones por integrante (copy/paste)

> **Fecha del plan:** 30/08/2026 · **Entrega oficial:** 27/09/2026 13:00
> **Deadline interno:** 21/09/2026 23:59 (6 días de buffer para QA y defensa)
> **Repo:** `https://github.com/NapoSV/medisuite`
>
> Este documento es **imperativo**. Cada integrante ejecuta sus tareas por semana,
> copia el código provisto, corre los comandos de verificación y avisa al Scrum
> Master (Héctor) al terminar cada bloque.

---

## 0. Reglas duras para TODO el equipo (sin excepciones)

1. **Rama personal obligatoria:** `feature/avance2-<apellido-minuscula>`.
   Ejemplo: `feature/avance2-orellana`.
2. **Prohibido push directo** a `main` o `develop`. Solo Héctor mergea PRs.
3. **Prohibida la co-autoría con IA en los commits.** El profesor evalúa la
   participación individual por commits. No debe aparecer nunca en tus commits:
   - `Co-Authored-By: Claude ...`
   - `Co-authored-by: Claude ...`
   - `🤖 Generated with Claude Code`
   - `Anthropic`, `ChatGPT`, `Copilot`, ni ninguna IA.
4. **Después de cada push, correr el checklist de verificación (§2).** Si
   falla, DETENERSE y avisar a Héctor.
5. **Formato de commit:**
   ```
   <tipo>(<módulo>): <resumen en minúscula, sin punto final>

   <por qué del cambio en 1-3 líneas>
   ```
   Tipos: `feat`, `fix`, `docs`, `test`, `refactor`, `chore`, `style`.
   Módulos: `backend`, `frontend`, `docker`, `docs`, `db`, `security`, `qa`.
6. **Datos ficticios siempre.** Cero pacientes reales, cero contraseñas reales
   en el repo. Secretos en `.env` (que está en `.gitignore`).

---

## 1. Setup inicial — TODOS ejecutan esto UNA VEZ (semana 1, lunes 31/08)

Cada integrante corre estos comandos **la primera vez** que arranca. Sustituir
`<apellido>` por el propio en minúscula (ejemplos: `lopez`, `vigil`, `orellana`,
`diaz`, `flores`, `melgar`, `merino`, `fuentes`, `vasquez`, `ventura`, `sanchez`).

### 1.1 Configurar identidad Git (una sola vez por PC)

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "TU_CIF@cvirtualuees.edu.sv"
```

### 1.2 Clonar el repositorio (la versión más actualizada)

```bash
# Ir a la carpeta donde guardas proyectos
cd ~/Proyectos          # o la que uses

# Clonar (usa HTTPS; si tienes SSH configurado, cambia por el remoto SSH)
git clone https://github.com/NapoSV/medisuite.git
cd medisuite

# Confirmar que estás en main y con lo más nuevo
git checkout main
git pull origin main
```

### 1.3 Crear tu rama personal desde main

```bash
# Sustituir <apellido> por el tuyo en minúscula
git checkout -b feature/avance2-<apellido>

# Subir la rama vacía al remoto para que exista en GitHub
git push -u origin feature/avance2-<apellido>
```

### 1.4 Primer commit dummy (para dejar el flujo confirmado)

```bash
# Editar el README para agregar tu nombre a la sección "Equipo Avance 2"
# (o crear un archivo docs/equipo/<apellido>.md con "Integrante activo Avance 2")
echo "Integrante activo Avance 2 — Nombre Apellido" > docs/equipo/<apellido>.md
git add docs/equipo/<apellido>.md
git commit -m "chore(docs): iniciar rama de avance 2 para <apellido>"
git push origin feature/avance2-<apellido>
```

### 1.5 Verificar que TODO subió a tu rama (obligatorio siempre)

Ver §2. Ejecutar los 3 comandos. Si falla algo, avisar a Héctor por WhatsApp.

---

## 2. Checklist de verificación DESPUÉS de cada `git push`

**Correr los 3 comandos siempre, sin excepciones.** Sustituir `<apellido>` por el tuyo.

```bash
# ─── 1) Estás en tu rama (NO en main ni develop) ──────────────────────
git branch --show-current
# ✅ Debe imprimir exactamente: feature/avance2-<apellido>
# ❌ Si imprime "main" o "develop": STOP, avisar a Héctor.

# ─── 2) Tu último commit NO tiene co-autoría de IA ─────────────────────
git log -1 --format="%B" | grep -iE "co-authored-by|claude|anthropic|generated with|chatgpt|copilot"
# ✅ Debe imprimir NADA (línea vacía).
# ❌ Si imprime algo:
#     git commit --amend            # abre el editor
#     (borrar TODAS las líneas de IA/co-autor)
#     :wq                            # guardar
#     git push --force-with-lease origin feature/avance2-<apellido>
#   Luego repetir esta verificación.

# ─── 3) Tu commit subió al remoto (a TU rama, no a otra) ──────────────
git fetch origin
git log origin/feature/avance2-<apellido> -1 --oneline
# ✅ Debe mostrar el hash y mensaje de tu último commit.
# ❌ Si dice "unknown revision" o muestra un commit viejo, tu push falló.
#     Revisar: git push origin feature/avance2-<apellido>

# ─── 4) Confirmar que NO subiste nada a main sin querer ────────────────
git log origin/main..HEAD --oneline
# ✅ Debe mostrar TUS commits nuevos (los que aún no están en main).
# Si sale VACÍO y NO era eso lo que buscabas, tu commit ya está en main
# (mal) — avisar a Héctor.
```

Si los 4 pasos OK → tu trabajo está seguro en tu rama. Avisar en el chat del
grupo: `✅ <apellido> push OK — <ID de tarea>`.

---

## 3. Flujo diario mientras trabajas

Cada vez que retomes el trabajo (mañana, tarde, día siguiente):

```bash
# 1. Ir a tu carpeta
cd ~/Proyectos/medisuite

# 2. Actualizar main con lo último del equipo
git checkout main
git pull origin main

# 3. Volver a tu rama y traer los cambios de main a tu rama
git checkout feature/avance2-<apellido>
git merge main
# Si hay conflictos: resolverlos, luego:
#   git add <archivos>
#   git commit -m "chore(git): merge main en feature/avance2-<apellido>"

# 4. Trabajar en tu tarea (editar, agregar archivos, etc.)
#    ...

# 5. Guardar cambios en Git
git status                        # ver qué cambió
git add <archivos-específicos>    # NO usar "git add ." salvo que estés seguro
git commit -m "feat(backend): agregar entidad Appointment con multi-tenant

- Entity con anotaciones JPA
- Migración Flyway V5
- Cumple HU-003"

# 6. Subir a tu rama
git push origin feature/avance2-<apellido>

# 7. CHECKLIST DE VERIFICACIÓN (§2)
```

---

## 3.2 Cómo abrir tu Pull Request (PR) — paso a paso

> Abrís UN solo PR por bloque de tareas, cuando terminás todo lo de tu semana
> (o cuando Héctor te lo pida). **No abrir PR por cada commit.**

### ⚠️ Regla crítica: el PR SIEMPRE va hacia `develop`, NUNCA hacia `main`

`main` es el branch de releases. Todo el trabajo del equipo se integra en `develop`.
Héctor es el único que mergea PRs.

### Opción A — por línea de comandos (recomendada)

```bash
# Desde tu rama, con todo pusheado y §2 en verde:
gh pr create \
  --base develop \
  --head feature/avance2-<apellido> \
  --title "feat(<tu-área>): <resumen de lo que hiciste>" \
  --body "Tareas completadas: <lista de IDs, ej. O-01, O-02, O-03>"
```

Ejemplo real para Orellana:
```bash
gh pr create \
  --base develop \
  --head feature/avance2-orellana \
  --title "feat(citas): módulo Appointments O-01 a O-04" \
  --body "Tareas completadas: O-01, O-02, O-03, O-04"
```

### Opción B — por la web de GitHub

1. Ir a `github.com/NapoSV/medisuite`
2. Hacer clic en **"Compare & pull request"** (aparece automáticamente tras tu push)
3. Verificar que los campos digan exactamente esto:

   | Campo | Valor CORRECTO | ❌ Error común |
   |-------|---------------|---------------|
   | **base** | `develop` | `main` ← **INCORRECTO** |
   | **compare** | `feature/avance2-<apellido>` | cualquier otra cosa |

4. Poner un título descriptivo y hacer clic en **"Create pull request"**
5. Avisar a Héctor en el grupo: `🔔 PR abierta — <apellido> — <IDs de tareas>`

### Checklist antes de abrir el PR

- [ ] `git push` hecho y §2 en verde
- [ ] `base` apunta a `develop` (no a `main`)
- [ ] El título menciona qué módulo y qué tareas cubre
- [ ] Avisaste a Héctor por WhatsApp

---

## 3.1 Grafo de dependencias (ORDEN CRÍTICO)

```
DIA 1 (lunes 31/08) — TODOS arrancan estas 4 tareas EN PARALELO
├── VG-08  JwtAuthenticationFilter + TenantContext   (Vigil)  ← BLOQUEA todo lo demás
├── VT-01  BaseEntity abstracta                      (Ventura) ← BLOQUEA entidades nuevas
├── F-01   Habilitar Flyway                          (Flores)  ← BLOQUEA migraciones
└── D-00   Pantalla /login                           (Diaz)    ← independiente

SEMANA 1 (31/08 → 06/09)
├── VG-08 → VG-01, VG-04, U-01, P-02, DR-01, VT-03
├── VT-01 → VT-02 → O-01, VT-03, VT-04 (todas las entidades refactorizadas)
├── F-01  → F-02, F-04, F-07
├── P-01  (Flores: cif→dui)  → P-02 (Orellana)
└── PIC-01 (Héctor: fotos)   → V-01 (Vásquez)

SEMANA 2 (07/09 → 13/09)
├── O-01, O-02, O-03, O-04 (Orellana citas backend)
├── P-02, DR-01 (Orellana pacientes y doctores)   ← depende de VG-08
├── VT-03 MedicalRecord + controller (Ventura)    ← depende de VG-08
├── U-01, F-07 must_change_password
├── M-02, M-03, M-04, M-05, M-06 (Merino feature crítico)
└── V-04...V-07 (Vásquez documento)

SEMANA 3 (14/09 → 20/09)
├── VT-04 Prescription refactor + VT-04b controller  ← depende de VG-08
├── D-04, D-05, D-06, D-07, D-08 (Diaz frontend)     ← depende de P-02, DR-01, U-01
├── MR-02, MR-03, MR-04, MR-05, MR-06, MR-07 (Melgar) ← depende de O-04, VT-03, VT-04b
└── V-08...V-14 (Vásquez documento + PDF)

FREEZE Dom 21/09 23:59 → Buffer 22-26/09 → ENTREGA Dom 27/09 13:00
```

**Regla de oro:** si tu tarea depende de otra, PRIMERO revisa que el compañero
haya subido su commit y pasado el checklist §2. Si no, escríbele por WhatsApp
en vez de asumir que ya está listo.

---

## 4. Sprints y calendario

| Sprint | Fechas | Foco |
|---|---|---|
| **S1** | Lun 31/08 – Dom 06/09 | Base OOP (BaseEntity), setup ramas, deuda técnica crítica, doc secciones 1-7 |
| **S2** | Lun 07/09 – Dom 13/09 | Módulos citas + expediente + feature crítico `.dat` + concurrencia + frontend base |
| **S3** | Lun 14/09 – Dom 20/09 | Recetas + reportes + integración + QA + doc 8-14 + ensayo defensa |
| **FREEZE** | Dom 21/09 23:59 | **CODE FREEZE** — solo bugfixes |
| **Buffer** | Lun 22/09 – Vie 26/09 | Docker verify, export PDF, ensayo final |
| **Entrega** | Dom 27/09 13:00 | Repo tageado + `AVANCE2_MEDISUITE.pdf` en raíz |

**Checkpoints (WhatsApp/Discord, 30 min):**
- Dom 07/09 20:00 — cierre S1
- Dom 14/09 20:00 — cierre S2
- Dom 20/09 20:00 — cierre S3 (entrega interna)
- Vie 25/09 20:00 — ensayo defensa (cada uno explica su código 3 min)

---

## 5. Tareas por integrante — semana por semana con código copy-paste

Cada tarea tiene: **qué hace**, **para qué sirve**, **archivo(s)**, **código
copy-paste**, y al final del bloque semanal, los **comandos git** para subirlo.

---

### 5.1 LÓPEZ RUIZ HÉCTOR NAPOLEÓN — Scrum Master / Arquitecto

**Apellido rama:** `lopez` · **CIF:** `2026010132` · **Correo M365:** `2026010132@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-lopez
git merge develop
```

**Paso 2 — Ubica tu próxima tarea en la tabla. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar a nadie.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable de la columna (no al Scrum Master, en este caso tú mismo) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| H-01, H-02, H-03, H-05, O-05, PIC-01, V-01, V-02, V-03 | — (ninguno) | — | Arranca directo |
| H-04 (mergear PRs, continuo) | Llega un PR de compañero | Autor del PR | `gh pr list --state open` |
| U-01 (`/me` + change-password) | VG-08 | Vigil | `git ls-files backend \| grep JwtAuthenticationFilter` |
| D-04 (Dashboard KPIs) | M-06 (endpoint dashboard) | Merino | `git grep -l "dashboard/metrics" backend/src/main/java` |
| V-04..V-13 (secciones documento) | — | — | Arranca directo |
| V-14 (PDF final) | V-01..V-13 completas | Tú mismo | `ls docs/DOCUMENTO_AVANCE2_MEDISUITE.md` |
| M-07 (doc arquitectura .dat + dashboard) | M-01..M-06 | Merino | `git ls-files backend \| grep -E "DatFileDao\|DashboardController"` |
| M-08 (speech técnico) | M-07 | Tú mismo | Después de escribir M-07 |
| H-06 (verificar Docker) | Todo el código mergeado a develop | — | `docker compose up -d && curl -sf http://localhost:8097/actuator/health` |
| H-07 (tag `v2.0.0-avance2`) | H-06 verde | Tú mismo | Después de H-06 sin errores |
| H-08 (ensayo defensa) | H-06 + M-08 listos | Todo el equipo | 25/09 según cronograma |


#### Semana 1 (31/08 – 06/09)

**Tarea H-01 · GitHub Action que bloquea commits con IA co-author**

- **Qué hace:** un workflow CI que revisa cada PR y falla si algún commit
  incluye líneas de co-autoría de IA.
- **Para qué sirve:** garantiza que el criterio del profesor "versionamiento
  Git = participación individual" no sea contaminado por trazas de IA.
- **Archivo:** `.github/workflows/no-ai-coauthor.yml`

```yaml
name: no-ai-coauthor

on:
  pull_request:
    branches: [main, develop]
  push:
    branches: [main, develop]

jobs:
  check-commits:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Verificar que ningun commit tenga co-autoria de IA
        run: |
          set -e
          RANGE="${{ github.event.pull_request.base.sha || 'origin/main' }}..${{ github.sha }}"
          echo "Revisando rango: $RANGE"
          BAD=$(git log --format='%H %B' $RANGE | grep -iE "co-authored-by|claude|anthropic|generated with claude|chatgpt|copilot" || true)
          if [ -n "$BAD" ]; then
            echo "❌ Se detecto co-autoria de IA en commits:"
            echo "$BAD"
            exit 1
          fi
          echo "✅ Ningun commit contiene co-autoria de IA."
```

**Tarea H-02 · Fix JWT secret sin validación de longitud**

- **Qué hace:** valida al arrancar la app que el secreto JWT tenga al menos 32
  bytes UTF-8 (256 bits, requisito HS256).
- **Para qué sirve:** cierra un issue crítico de seguridad (secreto débil rompe
  JWT).
- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/security/JwtTokenProvider.java`
  (agregar en el `@PostConstruct` o constructor).

```java
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@PostConstruct
void validateSecret() {
    byte[] bytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
    if (bytes.length < 32) {
        throw new IllegalStateException(
            "JWT secret debe tener al menos 32 bytes UTF-8. Longitud actual: " + bytes.length
        );
    }
}
```

**Tarea H-03 · Cerrar Swagger detrás de rol ADMIN**

- **Qué hace:** bloquea el acceso público a `/swagger-ui/**` y `/v3/api-docs/**`.
- **Para qué sirve:** cumple OWASP API9 (documentación pública expone superficie
  de ataque).
- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/config/SecurityConfig.java`.
  Cambiar la regla existente por:

```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
    .hasRole("ADMIN")
```

**Tarea H-05 · Documentar respuestas del profesor**

- **Archivo:** `docs/RESPUESTAS_PROFESOR.md`

```markdown
# Respuestas del Ing. Guevara — Avance 2

Fecha consulta: 27/08/2026 · Fecha respuesta: 30/08/2026

## 1. Persistencia .dat vs PostgreSQL
Se mantiene PostgreSQL como base principal. Adicionalmente se implementa una
capa paralela de respaldo en archivos `.dat` para el módulo de auditoría y
respaldos diarios de expedientes. Dueño técnico: Merino (M-02 a M-04).

## 2. Alcance del frontend
El profesor aceptó mínimo (login + una pantalla por módulo). El equipo decide
entregar los módulos importantes pulidos para maximizar puntos: Login,
Dashboard, Pacientes, Doctores, Citas, Expediente, Recetas.

## 3. Concurrencia
Se implementan dos features de concurrencia:
- Feature B: `ScheduledExecutorService` para respaldo asíncrono a `.dat`.
- Feature C: `CompletableFuture.allOf` para métricas paralelas en dashboard.

Estas decisiones son la base del plan `docs/EJECUCION_AVANCE2_EQUIPO.md`.
```

**Comandos git H-Semana1:**
```bash
git checkout feature/avance2-lopez
git add .github/workflows/no-ai-coauthor.yml \
        backend/src/main/java/com/sv/grupo7/medisuite/security/JwtTokenProvider.java \
        backend/src/main/java/com/sv/grupo7/medisuite/config/SecurityConfig.java \
        docs/RESPUESTAS_PROFESOR.md
git commit -m "feat(security): validar longitud JWT + cerrar swagger + CI anti-IA

- JwtTokenProvider valida secret >= 32 bytes UTF-8
- Swagger UI y api-docs requieren rol ADMIN
- Workflow no-ai-coauthor bloquea PRs con co-autoria IA
- Documentar respuestas del profesor"
git push origin feature/avance2-lopez
# Ejecutar §2 (verificación de push).
```

#### Semana 2 (07/09 – 13/09)

**Tarea H-04 · Mergear PRs de compañeros (continuo).**
Cada PR que llegue: revisar diff, correr `mvn clean package -DskipTests`, si
compila y no tiene co-autoría de IA → mergear a `develop`.

```bash
# Ver PRs abiertas
gh pr list

# Revisar una PR específica
gh pr checkout <numero>
mvn clean package -DskipTests -f backend/pom.xml

# Si todo OK, mergear
gh pr merge <numero> --squash --delete-branch=false
```

#### Semana 3 (14/09 – 20/09)

**Tarea H-07 · Tag v2.0.0-avance2 y push**
```bash
git checkout main
git pull origin main
git tag -a v2.0.0-avance2 -m "Entrega Avance 2 — 27/09/2026"
git push origin v2.0.0-avance2
```

**Tarea V-14 · Exportar a PDF y versionar en raiz** (20/09, última del Avance 2)

- **Qué hace:** exportar `docs/AVANCE2_MEDISUITE.md` a PDF y copiarlo en la raíz
  del repo como `AVANCE2_MEDISUITE.pdf` para la entrega al profesor.
- **Quién lo hace:** Héctor (el PM es responsable de entregar al profesor).
- Usar el skill `/make-pdf` o cualquier herramienta de exportación Markdown → PDF.

```bash
git checkout main
git add AVANCE2_MEDISUITE.pdf
git commit -m "docs(avance2): PDF final para entrega al Ing. Guevara"
git push origin main
```

#### Tareas de código adicionales

**Tarea U-01 · UserController GET /me + POST /api/auth/change-password**

- **Qué hace:** expone `GET /api/users/me` para leer el perfil del usuario logueado
  y `POST /api/auth/change-password` para cambiar la contraseña.
- **Para qué sirve:** la pantalla Perfil (D-08) los consume; sin ellos el
  frontend Perfil no funciona. Cierra el flujo de cambio obligatorio en primer login.
- El código copy-paste de este endpoint se documenta en §5.2 (donde originalmente
  estaba asignado) — Héctor lo implementa usando esa misma especificación.
- **Rama:** `feature/avance2-lopez`

```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/controller/api/UserController.java \
        backend/src/main/java/com/sv/grupo7/medisuite/model/users/User.java
git commit -m "feat(backend): UserController /me + cambio de password

- GET /api/users/me devuelve perfil del token
- POST /api/auth/change-password valida password actual + nueva minimo 8
- Requerido por D-08 Pantalla Perfil"
git push origin feature/avance2-lopez
```

**Tarea O-05 · Fix race condition en registerFailedAttempt**

- **Qué hace:** convierte el registro de intentos fallidos de login en una operación
  atómica via JPQL, eliminando la race condition donde múltiples threads concurrentes
  podían bypassear el bloqueo.
- **Para qué sirve:** cierra un bug de concurrencia/seguridad en `AuthService`.
- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/dao/UserRepository.java`

```java
@Modifying
@Query("UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1, " +
       " u.lockedUntil = CASE WHEN u.failedLoginAttempts + 1 >= 5 " +
       "     THEN :now + 15 MINUTE ELSE u.lockedUntil END " +
       "WHERE u.id = :userId")
int registerFailedAttempt(@Param("userId") Long userId, @Param("now") OffsetDateTime now);
```

Luego en `AuthService` reemplazar la lógica actual por una sola llamada a este método.

```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/dao/UserRepository.java \
        backend/src/main/java/com/sv/grupo7/medisuite/service/AuthService.java
git commit -m "fix(security): registerFailedAttempt ahora es atomico via JPQL

- Elimina race condition donde 10 threads concurrentes bypaseaban el bloqueo
- UPDATE + bloqueo en la misma transaccion"
git push origin feature/avance2-lopez
```

**Tarea D-04 · Pantalla Dashboard con 4 KPIs**

- **Qué hace:** página `/dashboard` que consume `GET /api/dashboard/metrics` y
  muestra 4 tarjetas KPI: Citas hoy, Pacientes activos, Alertas, Recetas emitidas.
- **Para qué sirve:** es la pantalla principal del sistema tras el login; sin ella
  el usuario no tiene punto de entrada.
- El código copy-paste de este componente se documenta en §5.4 — Héctor lo
  implementa usando esa misma especificación.
- **Rama:** `feature/avance2-lopez`

```bash
git add frontend/src/pages/Dashboard.tsx
git commit -m "feat(frontend): Dashboard con 4 KPIs consumiendo /api/dashboard/metrics

- Citas hoy, Pacientes activos, Alertas, Recetas emitidas
- Requerido como pantalla principal post-login"
git push origin feature/avance2-lopez
```

---

### 5.2 VIGIL RAMÍREZ ALEJANDRO ANTONIO — Backend / Seguridad

**Apellido rama:** `vigil` · **CIF:** `2026010204` · **Correo M365:** `2026010204@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-vigil
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| VG-08 (JwtFilter + TenantContext) | — (raíz, ya hecha) | — | Arranca directo |
| VG-04 (GlobalExceptionHandler) | — | — | Arranca directo |
| VG-03 (CORS por env var) | — | — | Arranca directo |
| VG-05 (headers OWASP HSTS, XFO...) | — | — | Arranca directo |
| VG-01 (rate limiting login) | VG-08 (tú) | Tú mismo | `git ls-files backend \| grep JwtAuthenticationFilter` |
| VG-06 (logout + blacklist JWT) | VG-08 (tú) | Tú mismo | `git ls-files backend \| grep JwtBlacklist` |


#### Semana 1 (31/08 – 06/09)

**Tarea VG-08 · JwtAuthenticationFilter + TenantContext (BLOQUEANTE de todo el resto)**

- **Qué hace:** filtro que lee `Authorization: Bearer <jwt>`, valida el token,
  pone el usuario en `SecurityContextHolder` y guarda el `tenantId` del claim
  en un `ThreadLocal` accesible desde cualquier service.
- **Para qué sirve:** **sin esto la seguridad no existe** (hoy SecurityConfig
  deja pasar todo tras `/login` sin validar el token) y **ningún `POST` de
  entidades multi-tenant compila** (Patient/Appointment/Prescription/MedicalRecord
  requieren `tenant_id NOT NULL` en el save y nadie lo setea).
- **Impacto:** VG-08 es prerequisito de U-01, P-02, DR-01, VT-03, VT-04, O-04, M-06.
- **Prioridad:** ARRANCAR YA EL LUNES 31/08 en paralelo con el resto de Semana 1.

- **Archivo 1:** `backend/src/main/java/com/sv/grupo7/medisuite/security/TenantContext.java`

```java
package com.sv.grupo7.medisuite.security;

public final class TenantContext {
    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();
    private TenantContext() {}
    public static void set(Long tenantId) { CURRENT.set(tenantId); }
    public static Long currentTenantId() {
        Long id = CURRENT.get();
        if (id == null) throw new IllegalStateException("Tenant no resuelto");
        return id;
    }
    public static void clear() { CURRENT.remove(); }
}
```

- **Archivo 2:** `backend/src/main/java/com/sv/grupo7/medisuite/security/JwtAuthenticationFilter.java`

```java
package com.sv.grupo7.medisuite.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final JwtBlacklist blacklist;   // se agrega en VG-06

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        try {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (!blacklist.isRevoked(token)) {
                    Claims claims = tokenProvider.parse(token);
                    Long userId   = Long.valueOf(claims.getSubject());
                    String role   = claims.get("role", String.class);
                    Long tenantId = claims.get("tenant_id", Long.class);

                    TenantContext.set(tenantId);
                    var auth = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
```

- **Archivo 3:** ampliar `JwtTokenProvider` con método `parse(token)` público
  que devuelve `Claims`:

```java
public Claims parse(String token) {
    return Jwts.parser()
        .verifyWith(this.secretKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
}
```

  (El método `secretKey()` interno ya existe en el JwtTokenProvider actual —
  reutilizarlo tal como está.)

- **Archivo 4:** registrar el filtro en `SecurityConfig`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    return http
        .csrf(c -> c.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(a -> a
            .requestMatchers("/api/auth/login", "/actuator/health").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN")   // H-03
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

- **Comandos git VG-08:**

```bash
git checkout feature/avance2-vigil
git add backend/src/main/java/com/sv/grupo7/medisuite/security/TenantContext.java \
        backend/src/main/java/com/sv/grupo7/medisuite/security/JwtAuthenticationFilter.java \
        backend/src/main/java/com/sv/grupo7/medisuite/security/JwtTokenProvider.java \
        backend/src/main/java/com/sv/grupo7/medisuite/config/SecurityConfig.java
git commit -m "feat(security): JwtAuthenticationFilter + TenantContext ThreadLocal

- Filtro lee Bearer, valida JWT y setea SecurityContext + TenantContext
- Sin esto ningun endpoint autenticado funciona y los POST de entidades
  multi-tenant explotan por tenant_id NOT NULL
- Prerequisito de U-01, P-02, DR-01, VT-03, VT-04, O-04 y M-06"
git push origin feature/avance2-vigil
```

**Tarea VG-01 · Rate limiting en /api/auth/login con Bucket4j**

- **Qué hace:** limita a 5 intentos de login por minuto por IP.
- **Para qué:** cierra CWE-307 (brute force) y cubre criterio de seguridad S2.
- **Paso 1:** agregar dependencia en `backend/pom.xml`:

```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

- **Paso 2:** crear filtro `backend/src/main/java/com/sv/grupo7/medisuite/security/RateLimitFilter.java`:

```java
package com.sv.grupo7.medisuite.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
            .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (!req.getRequestURI().equals("/api/auth/login")) {
            chain.doFilter(req, res);
            return;
        }
        String ip = req.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> newBucket());
        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        } else {
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Demasiados intentos. Intenta en 1 minuto.\"}");
        }
    }
}
```

- **Paso 3:** registrarlo en `SecurityConfig` (agregar al `SecurityFilterChain`):

```java
.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
```

**Tarea VG-04 · GlobalExceptionHandler sin leak de stacktrace**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/exception/GlobalExceptionHandler.java`
  (agregar handler genérico si no existe):

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    log.error("Error no controlado", ex);   // log completo en servidor
    return ResponseEntity.status(500).body(Map.of(
        "error", "Error interno del servidor",
        "timestamp", OffsetDateTime.now().toString()
    ));
}
```

**Comandos git VG-Semana1:**
```bash
git checkout feature/avance2-vigil
git add backend/pom.xml \
        backend/src/main/java/com/sv/grupo7/medisuite/security/RateLimitFilter.java \
        backend/src/main/java/com/sv/grupo7/medisuite/config/SecurityConfig.java \
        backend/src/main/java/com/sv/grupo7/medisuite/exception/GlobalExceptionHandler.java
git commit -m "feat(security): rate limiting login + handler global sin stacktrace

- Bucket4j 5 intentos/minuto por IP en /api/auth/login
- GlobalExceptionHandler oculta stacktrace al cliente
- Cierra deuda tecnica S2 y S6"
git push origin feature/avance2-vigil
# §2 verificación.
```

#### Semana 2 (07/09 – 13/09)

**Tarea VG-03 · CORS parametrizado por env var**

- **Archivo:** `backend/src/main/resources/application.yml` (agregar):

```yaml
app:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:5173}
```

- **En `SecurityConfig.java`** leer y aplicar:

```java
@Value("${app.cors.allowed-origins}")
private String corsOrigins;

@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of(corsOrigins.split(",")));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
}
```

**Tarea VG-05 · Security headers**

- En `SecurityConfig` agregar:

```java
.headers(h -> h
    .frameOptions(f -> f.deny())
    .contentTypeOptions(c -> {})
    .httpStrictTransportSecurity(hsts -> hsts.maxAgeInSeconds(31536000).includeSubDomains(true))
    .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
)
```

**Comandos git VG-Semana2:**
```bash
git add backend/src/main/resources/application.yml \
        backend/src/main/java/com/sv/grupo7/medisuite/config/SecurityConfig.java
git commit -m "feat(security): CORS por env + security headers OWASP

- CORS_ALLOWED_ORIGINS configurable por env
- X-Frame-Options DENY, HSTS, Referrer-Policy, X-Content-Type-Options"
git push origin feature/avance2-vigil
# §2.
```

#### Semana 3 (14/09 – 20/09)

**Tarea VG-06 · Endpoint logout con blacklist en memoria**

- **Archivo nuevo:** `backend/src/main/java/com/sv/grupo7/medisuite/security/JwtBlacklist.java`

```java
package com.sv.grupo7.medisuite.security;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtBlacklist {
    private final Set<String> revoked = ConcurrentHashMap.newKeySet();
    public void revoke(String token) { revoked.add(token); }
    public boolean isRevoked(String token) { return revoked.contains(token); }
}
```

- **En `AuthController.java`** agregar:

```java
@PostMapping("/logout")
public ResponseEntity<Void> logout(@RequestHeader("Authorization") String auth) {
    if (auth != null && auth.startsWith("Bearer ")) {
        jwtBlacklist.revoke(auth.substring(7));
    }
    return ResponseEntity.noContent().build();
}
```

- **En `JwtAuthenticationFilter`** consultar el blacklist antes de validar:

```java
if (jwtBlacklist.isRevoked(token)) {
    res.setStatus(401);
    return;
}
```

**Comandos git VG-Semana3:**
```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/security/JwtBlacklist.java \
        backend/src/main/java/com/sv/grupo7/medisuite/controller/api/AuthController.java \
        backend/src/main/java/com/sv/grupo7/medisuite/security/JwtAuthenticationFilter.java
git commit -m "feat(security): endpoint logout con blacklist JWT en memoria

- POST /api/auth/logout revoca el token
- JwtAuthenticationFilter rechaza tokens revocados
- Cumple criterio S7 de blindaje"
git push origin feature/avance2-vigil
```

---

### 5.3 ORELLANA ROJAS BAYRON ALEXANDER — Backend / Módulo Citas

**Apellido rama:** `orellana` · **CIF:** `2026011707` · **Correo M365:** `2026011707@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-orellana
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| P-02 (PatientService + búsqueda DUI) | VG-08 (Vigil) + P-01 (Flores) | Vigil, Flores | `git ls-files backend \| grep JwtAuthenticationFilter` y `git grep -l "dui" backend/src/main/resources/db/migration` |
| O-01 (Appointment extends BaseEntity) | VT-01 (Ventura) + F-02 (Flores) | Ventura, Flores | `git ls-files backend \| grep BaseEntity.java` y `git ls-files \| grep -E "V5__\|V6__"` |
| O-02 (AppointmentRepository) | O-01 (tú) | Tú mismo | `git ls-files backend \| grep "Appointment.java"` |
| O-03 (AppointmentService) | O-02 (tú) | Tú mismo | `git ls-files backend \| grep AppointmentRepository` |
| O-04 (endpoints REST /api/appointments) | O-03 (tú) + VG-08 (Vigil) | Tú, Vigil | `git ls-files backend \| grep AppointmentService` |
| O-06 (slots disponibles) | O-04 (tú) | Tú mismo | `git ls-files backend \| grep AppointmentController` |


#### Semana 2 (07/09 – 13/09)

**Tarea P-02 · PatientService + PatientController con búsqueda por DUI**

- **Qué hace:** expone el CRUD y búsqueda de pacientes por DUI o nombre.
- **Para qué sirve:** D-05 (listado Díaz) y modal Nueva Cita de Melgar consumen
  `GET /api/patients?search=`. Sin esto ambas pantallas revientan.
- **Prerequisito:** P-01 (Flores) debe haber renombrado `cif` → `dui` en la entidad.
- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/service/PatientService.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.model.medical.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repo;

    public Page<Patient> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) return repo.findAll(pageable);
        String q = query.replace("-", "").toLowerCase();
        return repo.searchByDuiOrName(q, pageable);
    }

    @Transactional
    public Patient create(Patient p) {
        // Setea el tenant desde el JWT del usuario logueado (VG-08)
        Long tid = com.sv.grupo7.medisuite.security.TenantContext.currentTenantId();
        com.sv.grupo7.medisuite.model.tenant.Tenant t = new com.sv.grupo7.medisuite.model.tenant.Tenant();
        t.setId(tid);
        p.setTenant(t);
        return repo.save(p);
    }

    public Patient findById(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new RuntimeException("Paciente " + id + " no existe"));
    }

    @Transactional
    public Patient update(Long id, Patient data) {
        Patient p = findById(id);
        p.setFirstName(data.getFirstName());
        p.setLastName(data.getLastName());
        p.setDui(data.getDui());
        p.setPhone(data.getPhone());
        p.setAddress(data.getAddress());
        return p;
    }
}
```

- **Agregar en `PatientRepository.java`:**

```java
@Query("SELECT p FROM Patient p WHERE " +
       " REPLACE(LOWER(p.dui), '-', '') LIKE %:q% OR " +
       " LOWER(p.firstName) LIKE %:q% OR " +
       " LOWER(p.lastName) LIKE %:q%")
Page<Patient> searchByDuiOrName(@Param("q") String q, Pageable pageable);
```

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/controller/api/PatientController.java`

```java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Patient;
import com.sv.grupo7.medisuite.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    @GetMapping
    public ResponseEntity<Page<Patient>> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.search(search, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient p) {
        return ResponseEntity.status(201).body(service.create(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> update(@PathVariable Long id, @RequestBody Patient p) {
        return ResponseEntity.ok(service.update(id, p));
    }
}
```

**Tarea O-01 · Entidad Appointment extendiendo BaseEntity (de Ventura)**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/model/medical/Appointment.java`
  reemplazar por (asumiendo que `BaseEntity` de Ventura ya existe):

```java
package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_appt_doctor_date", columnList = "doctor_id, scheduled_at"),
    @Index(name = "idx_appt_patient", columnList = "patient_id")
})
@Getter @Setter
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(nullable = false, length = 20)
    private String status;   // PENDING, CONFIRMED, IN_WAITING, IN_CONSULTATION, COMPLETED, CANCELLED, NO_SHOW

    @Column(length = 200)
    private String reason;

    @Column(length = 50)
    private String office;

    @Column(name = "reservation_code", nullable = false, length = 10)
    private String reservationCode;
}
```

**Tarea O-02 · Ampliar AppointmentRepository con queries nuevas**

- **Nota:** `AppointmentRepository` ya existe con `findByTenantIdOrderByScheduledAtAsc`,
  `findByPatientIdAndTenantId`, `findByDoctorIdAndTenantId`,
  `findByReservationCodeAndTenantId`. **AGREGAR** (no reemplazar) los siguientes:

```java
import java.time.OffsetDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

List<Appointment> findByTenantIdAndDoctorIdAndScheduledAtBetween(
    Long tenantId, Long doctorId, OffsetDateTime from, OffsetDateTime to);

@Query("SELECT COUNT(a) FROM Appointment a WHERE a.tenant.id = :tid " +
       " AND a.scheduledAt >= :dayStart AND a.scheduledAt < :dayEnd")
long countByDateAndTenant(@Param("tid") Long tenantId,
                          @Param("dayStart") OffsetDateTime start,
                          @Param("dayEnd") OffsetDateTime end);

boolean existsByDoctorIdAndScheduledAtAndStatusNot(
    Long doctorId, OffsetDateTime scheduledAt, String excludedStatus);

@Query("SELECT a FROM Appointment a WHERE a.tenant.id = :tid " +
       " AND a.scheduledAt >= :now ORDER BY a.scheduledAt ASC")
List<Appointment> findUpcomingByTenant(@Param("tid") Long tenantId,
                                       @Param("now") OffsetDateTime now);
```

**Tarea O-03 · AppointmentService con validaciones + código de reserva**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/service/AppointmentService.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.AppointmentRepository;
import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.dao.PatientRepository;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.medical.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    @Transactional
    public Appointment create(Long patientId, Long doctorId,
                              OffsetDateTime scheduledAt, String reason) {
        if (scheduledAt.isBefore(OffsetDateTime.now())) {
            throw new BusinessException("La cita no puede ser en el pasado");
        }
        var doctor = doctorRepo.findById(doctorId)
            .orElseThrow(() -> new BusinessException("Doctor no existe"));
        var patient = patientRepo.findById(patientId)
            .orElseThrow(() -> new BusinessException("Paciente no existe"));

        boolean occupied = appointmentRepo
            .existsByDoctorIdAndScheduledAtAndStatusNot(doctorId, scheduledAt, "CANCELLED");
        if (occupied) {
            throw new BusinessException("El doctor ya tiene una cita en ese horario");
        }

        Appointment a = new Appointment();
        a.setTenant(doctor.getTenant());   // tenant desde entidad relacionada (o TenantContext)
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setScheduledAt(scheduledAt);
        a.setReason(reason);
        a.setStatus("PENDING");
        a.setReservationCode("COD-" + String.format("%04d", new Random().nextInt(10000)));
        return appointmentRepo.save(a);
    }

    public List<Appointment> upcoming() {
        Long tid = com.sv.grupo7.medisuite.security.TenantContext.currentTenantId();
        return appointmentRepo.findUpcomingByTenant(tid, OffsetDateTime.now());
    }

    @Transactional
    public void cancel(Long appointmentId) {
        Appointment a = appointmentRepo.findById(appointmentId)
            .orElseThrow(() -> new BusinessException("Cita no existe"));
        if (a.getScheduledAt().minusHours(24).isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Solo se puede cancelar con 24h de anticipacion");
        }
        a.setStatus("CANCELLED");
    }
}
```

**Tarea O-04 · Endpoints REST /api/appointments**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/controller/api/AppointmentController.java`

```java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Appointment;
import com.sv.grupo7.medisuite.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    public record CreateRequest(Long patientId, Long doctorId,
                                OffsetDateTime scheduledAt, String reason) {}

    @GetMapping
    public ResponseEntity<List<Appointment>> list(
            @RequestParam(defaultValue = "false") boolean upcoming) {
        return ResponseEntity.ok(upcoming ? service.upcoming() : service.upcoming());
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody CreateRequest r) {
        Appointment a = service.create(r.patientId(), r.doctorId(),
                                       r.scheduledAt(), r.reason());
        return ResponseEntity.status(201).body(a);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String,String>> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.ok(Map.of("status", "CANCELLED"));
    }
}
```

**Comandos git O-Semana2:**
```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/model/medical/Appointment.java \
        backend/src/main/java/com/sv/grupo7/medisuite/dao/AppointmentRepository.java \
        backend/src/main/java/com/sv/grupo7/medisuite/service/AppointmentService.java \
        backend/src/main/java/com/sv/grupo7/medisuite/controller/api/AppointmentController.java
git commit -m "feat(backend): modulo Citas completo con anti doble-reserva

- Appointment extiende BaseEntity (herencia)
- Repository con queries multi-tenant
- Service valida: fecha futura, doctor/paciente existen, slot libre
- Endpoint POST /api/appointments con codigo de reserva COD-XXXX
- POST /api/appointments/{id}/cancel con regla de 24h
- Cumple HU-003"
git push origin feature/avance2-orellana
```

#### Semana 3 (14/09 – 20/09)

**Tarea O-06 · Endpoint slots disponibles**

- Agregar en `AppointmentController.java`:

```java
@GetMapping("/doctors/{doctorId}/slots")
public ResponseEntity<List<OffsetDateTime>> slots(@PathVariable Long doctorId,
                                                  @RequestParam String date) {
    return ResponseEntity.ok(service.availableSlots(doctorId, LocalDate.parse(date)));
}
```

En `AppointmentService`:

```java
public List<OffsetDateTime> availableSlots(Long doctorId, LocalDate date) {
    List<OffsetDateTime> all = new ArrayList<>();
    for (int h = 8; h < 17; h++) {
        all.add(date.atTime(h, 0).atOffset(ZoneOffset.of("-06:00")));
    }
    return all.stream().filter(s ->
        !appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(doctorId, s, "CANCELLED")
    ).toList();
}
```

**Comandos git O-Semana3:**
```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/controller/api/AppointmentController.java \
        backend/src/main/java/com/sv/grupo7/medisuite/service/AppointmentService.java
git commit -m "feat(backend): endpoint slots disponibles por doctor y fecha

- GET /api/appointments/doctors/{id}/slots?date=YYYY-MM-DD
- Devuelve slots 8h-17h libres (sin cita activa)"
git push origin feature/avance2-orellana
```

---

### 5.4 DÍAZ SANTOS ZAIR BENETT — Frontend (Dashboard + Pacientes + Doctores)

**Apellido rama:** `diaz` · **CIF:** `2026010796` · **Correo M365:** `2026010796@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-diaz
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| D-00 (login) | — | — | Arranca directo |
| D-01 (React Router + rutas protegidas) | — | — | Arranca directo |
| D-02 (Layout navbar + sidebar) | — | — | Arranca directo |
| D-03 (useAuth + persistencia JWT) | — | — | Arranca directo |
| D-05 (Pantalla Pacientes) | D-00..D-03 (tú) + MR-01 (Melgar) + P-02 (Orellana) | Melgar, Orellana | `git ls-files frontend \| grep -E "LoginPage\|useAuth\|MainLayout\|DataTable.tsx"` y `git ls-files backend \| grep PatientController` |
| D-07 (Doctores CRUD, solo ADMIN) | D-00..D-03 (tú) + MR-01 (Melgar) + DR-01 (Vásquez) | Melgar, Vásquez | `git ls-files frontend \| grep DataTable.tsx` y `git ls-files backend \| grep DoctorController` |


#### Semana 1 (31/08 – 06/09)

**Tarea D-00 · Pantalla `/login` con formulario**

- **Qué hace:** página de inicio de sesión con selector de clínica (tenantSlug),
  email y password. Consume `POST /api/auth/login`. Al éxito, guarda el token
  (via `useAuth`) y redirige a `/dashboard`. Al fallar, muestra el mensaje del
  backend.
- **Para qué sirve:** **sin esta pantalla no se puede entrar al sistema**. Es la
  primera cosa que ve cualquier usuario.
- **Archivo:** `frontend/src/pages/Login.tsx`

```tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

export default function Login() {
  const nav = useNavigate();
  const { login } = useAuth();
  const [tenantSlug, setSlug] = useState('demo');
  const [email, setEmail] = useState('');
  const [password, setPwd] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      await login(tenantSlug, email, password);
      nav('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.error ?? 'Credenciales invalidas');
    } finally { setLoading(false); }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100">
      <form onSubmit={submit} className="bg-white p-8 rounded-lg shadow w-full max-w-sm space-y-4">
        <h1 className="text-2xl font-bold text-center">MediSuite</h1>
        <p className="text-center text-slate-600 text-sm">Inicia sesion</p>

        <label className="block">
          <span className="text-sm">Clinica</span>
          <input value={tenantSlug} onChange={e => setSlug(e.target.value)}
                 className="mt-1 w-full border rounded px-3 py-2" required />
        </label>

        <label className="block">
          <span className="text-sm">Correo</span>
          <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                 className="mt-1 w-full border rounded px-3 py-2" required autoFocus />
        </label>

        <label className="block">
          <span className="text-sm">Contrasena</span>
          <input type="password" value={password} onChange={e => setPwd(e.target.value)}
                 className="mt-1 w-full border rounded px-3 py-2" required />
        </label>

        {error && <p className="text-red-600 text-sm">{error}</p>}

        <button disabled={loading}
                className="w-full bg-blue-600 text-white py-2 rounded disabled:opacity-50">
          {loading ? 'Ingresando...' : 'Entrar'}
        </button>
      </form>
    </div>
  );
}
```

- **Comandos git D-00:**

```bash
git checkout feature/avance2-diaz
git add frontend/src/pages/Login.tsx
git commit -m "feat(frontend): pantalla /login con formulario y manejo de errores

- Selector de tenant + email + password
- Muestra mensaje del backend en credenciales invalidas
- Redirige a /dashboard tras login exitoso
- Requerido para que cualquier usuario entre al sistema"
git push origin feature/avance2-diaz
```

**Tarea D-01 · Setup React Router con rutas protegidas**

- **Archivo:** `frontend/src/routes/ProtectedRoute.tsx`

```tsx
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

export default function ProtectedRoute({ roles }: { roles?: string[] }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user.role)) return <Navigate to="/403" replace />;
  return <Outlet />;
}
```

- **Archivo:** `frontend/src/App.tsx` (router principal)

```tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './routes/ProtectedRoute';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Pacientes from './pages/Pacientes';
import Doctores from './pages/Doctores';
import Perfil from './pages/Perfil';
import Forbidden from './pages/Forbidden';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/403" element={<Forbidden />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/pacientes" element={<Pacientes />} />
          <Route path="/perfil" element={<Perfil />} />
        </Route>
        <Route element={<ProtectedRoute roles={['ADMIN']} />}>
          <Route path="/doctores" element={<Doctores />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
```

**Tarea D-02 · Layout base (navbar + sidebar)**

- **Archivo:** `frontend/src/layouts/AppLayout.tsx`

```tsx
import { Outlet, NavLink } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

export default function AppLayout() {
  const { user, logout } = useAuth();
  return (
    <div className="min-h-screen flex">
      <aside className="w-64 bg-slate-900 text-white p-4">
        <h1 className="text-xl font-bold mb-8">MediSuite</h1>
        <nav className="flex flex-col gap-2">
          <NavLink to="/dashboard" className="px-3 py-2 rounded hover:bg-slate-700">Dashboard</NavLink>
          <NavLink to="/pacientes" className="px-3 py-2 rounded hover:bg-slate-700">Pacientes</NavLink>
          <NavLink to="/citas"     className="px-3 py-2 rounded hover:bg-slate-700">Citas</NavLink>
          {user?.role === 'ADMIN' && (
            <NavLink to="/doctores" className="px-3 py-2 rounded hover:bg-slate-700">Doctores</NavLink>
          )}
        </nav>
      </aside>
      <main className="flex-1 bg-slate-50">
        <header className="flex justify-between items-center bg-white border-b px-6 py-3">
          <span>{user?.fullName}</span>
          <button onClick={logout} className="text-sm text-red-600">Cerrar sesion</button>
        </header>
        <section className="p-6"><Outlet /></section>
      </main>
    </div>
  );
}
```

**Tarea D-03 · Persistencia JWT en localStorage + hook useAuth**

- **Archivo:** `frontend/src/auth/useAuth.tsx`

```tsx
import { createContext, useContext, useEffect, useState } from 'react';
import { api } from '../api/client';

type User = { id: number; fullName: string; role: string; tenantId: number };
type AuthCtx = { user: User | null; login: (t: string, u: string, p: string) => Promise<void>; logout: () => void };

const Ctx = createContext<AuthCtx>({} as AuthCtx);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const raw = localStorage.getItem('user');
    if (token && raw) setUser(JSON.parse(raw));
  }, []);

  const login = async (tenantSlug: string, email: string, password: string) => {
    const { data } = await api.post('/auth/login', { tenantSlug, email, password });
    localStorage.setItem('token', data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
    setUser(data.user);
  };

  const logout = () => {
    api.post('/auth/logout').catch(() => {});
    localStorage.clear();
    setUser(null);
  };

  return <Ctx.Provider value={{ user, login, logout }}>{children}</Ctx.Provider>;
}

export const useAuth = () => useContext(Ctx);
```

- **Archivo:** `frontend/src/api/client.ts`

```ts
import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8097/api'
});

api.interceptors.request.use(cfg => {
  const t = localStorage.getItem('token');
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

api.interceptors.response.use(
  r => r,
  err => {
    if (err.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);
```

**Comandos git D-Semana1:**
```bash
git checkout feature/avance2-diaz
git add frontend/src/App.tsx \
        frontend/src/routes/ProtectedRoute.tsx \
        frontend/src/layouts/AppLayout.tsx \
        frontend/src/auth/useAuth.tsx \
        frontend/src/api/client.ts
git commit -m "feat(frontend): router protegido + layout base + auth con JWT

- ProtectedRoute redirige a /login o /403 segun rol
- AppLayout con sidebar dinamico segun rol
- useAuth persiste JWT en localStorage y expone login/logout
- api client con interceptor Bearer y auto-logout en 401"
git push origin feature/avance2-diaz
```

#### Semana 2 (07/09 – 13/09)

**Tarea D-05 · Página Pacientes con búsqueda por DUI**

- **Archivo:** `frontend/src/pages/Pacientes.tsx`

```tsx
import { useEffect, useState } from 'react';
import { api } from '../api/client';

type Patient = { id: number; firstName: string; lastName: string; dui: string; phone: string };

export default function Pacientes() {
  const [rows, setRows] = useState<Patient[]>([]);
  const [q, setQ] = useState('');

  const load = () => api.get<Patient[]>(`/patients?search=${q}`).then(r => setRows(r.data));
  useEffect(() => { load(); }, []);

  return (
    <div>
      <div className="flex justify-between mb-4">
        <h2 className="text-2xl font-bold">Pacientes</h2>
        <button className="bg-blue-600 text-white px-4 py-2 rounded">+ Nuevo paciente</button>
      </div>
      <div className="flex gap-2 mb-4">
        <input value={q} onChange={e => setQ(e.target.value)}
               placeholder="Buscar por nombre o DUI (########-#)"
               className="flex-1 border rounded px-3 py-2"/>
        <button onClick={load} className="bg-slate-800 text-white px-4 rounded">Buscar</button>
      </div>
      <table className="w-full bg-white rounded shadow">
        <thead>
          <tr className="border-b">
            <th className="text-left p-3">Nombre</th>
            <th className="text-left p-3">DUI</th>
            <th className="text-left p-3">Telefono</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {rows.map(p => (
            <tr key={p.id} className="border-b">
              <td className="p-3">{p.firstName} {p.lastName}</td>
              <td className="p-3 font-mono">{p.dui}</td>
              <td className="p-3">{p.phone}</td>
              <td className="p-3 text-right"><a className="text-blue-600" href={`/pacientes/${p.id}`}>Ver</a></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

**Comandos git D-Semana2:**
```bash
git add frontend/src/pages/Pacientes.tsx
git commit -m "feat(frontend): listado pacientes con busqueda DUI

- Pacientes tabla + busqueda por nombre o DUI"
git push origin feature/avance2-diaz
```

#### Semana 3 (14/09 – 20/09)

**Tarea D-07 · CRUD Doctores (solo ADMIN)** — mismo patrón que Pacientes,
consumiendo `/api/doctors`. Ver [`Pacientes.tsx`] como plantilla.

**Comandos git D-Semana3:**
```bash
git add frontend/src/pages/Doctores.tsx
git commit -m "feat(frontend): CRUD doctores (solo ADMIN) consumiendo /api/doctors"
git push origin feature/avance2-diaz
```

---

### 5.5 FLORES HERNÁNDEZ WALTER ALEJANDRO — QA / Base de datos

**Apellido rama:** `flores` · **CIF:** `2026011012` · **Correo M365:** `2026011012@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-floreshernandez
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| F-01 (Flyway enabled + validate) | — (raíz) | — | Arranca directo |
| P-01 (rename `cif`→`dui` + migración) | F-01 (tú) | Tú mismo | `grep -R "spring.flyway.enabled=true" backend/src/main/resources` |
| F-07 (migración `must_change_password`) | F-01 (tú) | Tú mismo | idem |
| F-02 (migraciones V5-V7) | F-01 (tú) | Tú mismo | idem |
| F-03 (constraints multi-tenant) | F-02 (tú) | Tú mismo | `git ls-files \| grep -E "V5__\|V6__\|V7__"` |
| F-04 (seed demo) | F-02 (tú) + F-03 (tú) | Tú mismo | idem |


#### Semana 1 (31/08 – 06/09)

**Tarea P-01 · Renombrar `Patient.cif` → `Patient.dui` + migración**

- **Qué hace:** en la entidad y en la BD, renombrar el campo que identifica al
  paciente para reflejar la realidad salvadoreña (DUI, no CIF).
- **Para qué sirve:** el frontend (Diaz D-05) buscará por DUI y P-02 (Orellana)
  usa `p.dui`. Sin esto todo el módulo pacientes rompe.
- **Archivo migración:** `backend/src/main/resources/db/migration/V4b__rename_patient_cif_to_dui.sql`

```sql
ALTER TABLE patients RENAME COLUMN cif TO dui;

-- opcional: normalizar formato con guion (########-#)
UPDATE patients
SET dui = SUBSTRING(dui, 1, 8) || '-' || SUBSTRING(dui, 9, 1)
WHERE LENGTH(REPLACE(dui, '-', '')) = 9 AND dui NOT LIKE '%-%';

CREATE INDEX IF NOT EXISTS idx_patients_dui ON patients (dui);
```

- **Coordinar con Orellana:** editar `Patient.java` — renombrar el atributo
  `cif` → `dui` y ajustar `@Column(name = "dui")`. Buscar y reemplazar todo uso
  de `getCif()` / `setCif()` en el backend:

```bash
# en la raiz del repo
grep -rn "getCif\|setCif\|patient.cif\|p\.cif" backend/src
# renombrar cada aparicion a getDui/setDui/patient.dui/p.dui
```

- **Comandos git P-01:**

```bash
git checkout feature/avance2-flores
git add backend/src/main/resources/db/migration/V4b__rename_patient_cif_to_dui.sql
git commit -m "feat(db): renombrar patients.cif -> patients.dui + normalizacion

- Migracion V4b renombra columna y agrega indice
- Alinea el modelo con la realidad SV (DUI = Documento Unico de Identidad)"
git push origin feature/avance2-flores
```

**Tarea F-01 · Habilitar Flyway y bloquear ddl-auto**

- **Archivo:** `backend/pom.xml` agregar:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

- **Archivo:** `backend/src/main/resources/application.yml`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate     # antes: update
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

**Comandos git F-Semana1:**
```bash
git checkout feature/avance2-flores
git add backend/pom.xml backend/src/main/resources/application.yml
git commit -m "feat(db): habilitar Flyway y bloquear ddl-auto en validate

- Migraciones controladas en db/migration
- Deja de crear tablas automatico; obliga a migraciones versionadas"
git push origin feature/avance2-flores
```

#### Semana 2 (07/09 – 13/09)

**Tarea F-02 · Migraciones V5-V7**

- **Archivo:** `backend/src/main/resources/db/migration/V5__appointments.sql`

```sql
CREATE TABLE IF NOT EXISTS appointments (
    id                BIGSERIAL PRIMARY KEY,
    tenant_id         BIGINT NOT NULL REFERENCES tenants(id),
    patient_id        BIGINT NOT NULL REFERENCES patients(id),
    doctor_id         BIGINT NOT NULL REFERENCES doctors(id),
    scheduled_at      TIMESTAMPTZ NOT NULL,
    status            VARCHAR(20) NOT NULL,
    reason            VARCHAR(200),
    office            VARCHAR(50),
    reservation_code  VARCHAR(10) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_appt_doctor_slot
    ON appointments (doctor_id, scheduled_at)
    WHERE status <> 'CANCELLED';

CREATE INDEX IF NOT EXISTS idx_appt_patient ON appointments (patient_id);
CREATE INDEX IF NOT EXISTS idx_appt_tenant_date ON appointments (tenant_id, scheduled_at);
```

- **Archivo:** `backend/src/main/resources/db/migration/V6__medical_records.sql`

  El campo se llama `general_notes` (no `notes`) para coincidir con la entidad
  actual. Se agrega `created_on` (LocalDate) que la entidad requiere.

```sql
CREATE TABLE IF NOT EXISTS medical_records (
    id             BIGSERIAL PRIMARY KEY,
    tenant_id      BIGINT NOT NULL REFERENCES tenants(id),
    patient_id     BIGINT NOT NULL UNIQUE REFERENCES patients(id),
    created_on     DATE NOT NULL DEFAULT CURRENT_DATE,
    general_notes  TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

- **Archivo:** `backend/src/main/resources/db/migration/V7__prescriptions_refactor.sql`

  Refactor de la tabla `prescriptions` para reflejar el modelo nuevo de
  Ventura (VT-04): se ELIMINAN `medications`, `dosage`, `duration` (que eran
  texto plano) y se AGREGA la tabla hija `prescription_items` con la colección
  justificada.

```sql
-- Prescriptions ya existe con columnas viejas; hacer refactor idempotente
ALTER TABLE prescriptions DROP COLUMN IF EXISTS medications;
ALTER TABLE prescriptions DROP COLUMN IF EXISTS dosage;
ALTER TABLE prescriptions DROP COLUMN IF EXISTS duration;
-- issued_on y instructions se mantienen

CREATE TABLE IF NOT EXISTS prescription_items (
    id                BIGSERIAL PRIMARY KEY,
    prescription_id   BIGINT NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    order_idx         INTEGER NOT NULL DEFAULT 0,
    medication        VARCHAR(200) NOT NULL,
    dose              VARCHAR(100),
    frequency         VARCHAR(100),
    duration_days     INTEGER
);

CREATE INDEX IF NOT EXISTS idx_prescription_items_rx
    ON prescription_items (prescription_id, order_idx);
```

**Comandos git F-Semana2:**
```bash
git add backend/src/main/resources/db/migration/
git commit -m "feat(db): migraciones V5-V7 citas, expediente, recetas

- Indice unico parcial anti doble-reserva en appointments
- FK multi-tenant en las 3 tablas
- prescription_items con ON DELETE CASCADE"
git push origin feature/avance2-flores
```

#### Semana 3 (14/09 – 20/09)

**Tarea F-04 · Seed demo con password real `Demo2026!`**

- **Nota:** el hash BCrypt de `Demo2026!` (strength=12) es fijo y precomputable.
  Generar una vez con:
  ```bash
  cd backend
  mvn -q exec:java -Dexec.mainClass=com.sv.grupo7.medisuite.util.HashGenerator -Dexec.args="Demo2026!"
  ```
  (o pedirle a Vigil el hash — es determinista). Pegar el hash resultante en
  la migración.

- **Archivo:** `backend/src/main/resources/db/migration/V8__seed_demo.sql`

```sql
-- Idempotente para poder re-ejecutar sin romper
INSERT INTO tenants (id, slug, commercial_name, timezone, default_language,
                     plan, status, max_users, max_patients, max_doctors)
VALUES (1, 'demo', 'Clinica Demo', 'America/El_Salvador', 'es',
        'FREE', 'ACTIVE', 100, 5000, 50)
ON CONFLICT (slug) DO NOTHING;

-- Admin demo: email=admin@demo.com  /  password=Demo2026!
INSERT INTO users (tenant_id, first_name, last_name, cif, email,
                   password_hash, role, active, failed_login_attempts)
VALUES (1, 'Admin', 'Demo', 'A0001', 'admin@demo.com',
        '$2a$12$REEMPLAZAR_CON_HASH_REAL_DE_Demo2026_BANG',
        'ADMIN', true, 0)
ON CONFLICT DO NOTHING;

-- Doctor demo: doctor@demo.com  /  Demo2026!
INSERT INTO users (tenant_id, first_name, last_name, cif, email,
                   password_hash, role, active, failed_login_attempts)
VALUES (1, 'Ana', 'Rodriguez', 'D0001', 'doctor@demo.com',
        '$2a$12$REEMPLAZAR_CON_HASH_REAL_DE_Demo2026_BANG',
        'DOCTOR', true, 0)
ON CONFLICT DO NOTHING;

-- Enfermera demo: nurse@demo.com  /  Demo2026!
INSERT INTO users (tenant_id, first_name, last_name, cif, email,
                   password_hash, role, active, failed_login_attempts)
VALUES (1, 'Maria', 'Lopez', 'N0001', 'nurse@demo.com',
        '$2a$12$REEMPLAZAR_CON_HASH_REAL_DE_Demo2026_BANG',
        'NURSE', true, 0)
ON CONFLICT DO NOTHING;

-- Recepcionista demo
INSERT INTO users (tenant_id, first_name, last_name, cif, email,
                   password_hash, role, active, failed_login_attempts)
VALUES (1, 'Carla', 'Menjivar', 'R0001', 'recep@demo.com',
        '$2a$12$REEMPLAZAR_CON_HASH_REAL_DE_Demo2026_BANG',
        'RECEPTIONIST', true, 0)
ON CONFLICT DO NOTHING;

-- 20 pacientes ficticios con DUI valido (usa generate_series)
INSERT INTO patients (tenant_id, first_name, last_name, dui, birth_date, phone)
SELECT 1,
       'Paciente' || i,
       'Apellido' || i,
       LPAD(i::text, 8, '0') || '-' || (i % 10)::text,
       '1980-01-01'::date + (i * 30) * INTERVAL '1 day',
       '7000' || LPAD(i::text, 4, '0')
FROM generate_series(1, 20) AS i
ON CONFLICT DO NOTHING;
```

- **Coordinar con Vigil:** que le pase el hash real de `Demo2026!` una vez
  computado por el `PasswordEncoder` local (BCrypt strength=12).

**Comandos git F-Semana3:**
```bash
git add backend/src/main/resources/db/migration/V8__seed_demo.sql
git commit -m "feat(db): seed demo para defensa (5 doctores, 20 pacientes, 30 citas)"
git push origin feature/avance2-flores
```

---

### 5.6 MELGAR RIVAS WILLIAM ARIEL — Frontend (Citas + Expediente + Recetas)

**Apellido rama:** `melgar` · **CIF:** `2026011736` · **Correo M365:** `2026011736@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-melgar
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| MR-01 (DataTable reutilizable) | — (raíz) | — | Arranca directo |
| MR-02 (Pantalla Citas) | MR-01 (tú) + D-00..D-03 (Díaz) + O-04 (Orellana) | Díaz, Orellana | `git ls-files frontend \| grep -E "DataTable.tsx\|MainLayout"` y `git ls-files backend \| grep AppointmentController` |
| MR-03 (Modal Nueva Cita con slots) | MR-02 (tú) + O-06 (Orellana) | Orellana | `git grep -l "slots" backend/src/main/java` |
| MR-04 (acciones cancelar/reprogramar/completar) | MR-02 (tú) + O-04 (Orellana) | Orellana | `git ls-files backend \| grep AppointmentController` |
| MR-05 (Expediente + timeline) | MR-01 (tú) + D-00..D-03 (Díaz) + VT-03 (Ventura) | Díaz, Ventura | `git ls-files backend \| grep MedicalRecordController` |
| MR-06 (Recetas con items dinámicos) | MR-01 (tú) + D-00..D-03 (Díaz) + VT-04b (Ventura) | Díaz, Ventura | `git ls-files backend \| grep PrescriptionController` |


#### Semana 1 (31/08 – 06/09)

**Tarea MR-01 · Componente reutilizable DataTable**

- **Archivo:** `frontend/src/components/ui/DataTable.tsx`

```tsx
type Col<T> = { key: keyof T; label: string; render?: (row: T) => React.ReactNode };
export default function DataTable<T extends { id: number }>({ cols, rows }:
        { cols: Col<T>[]; rows: T[] }) {
  return (
    <table className="w-full bg-white rounded shadow">
      <thead>
        <tr className="border-b">
          {cols.map(c => <th key={String(c.key)} className="text-left p-3">{c.label}</th>)}
        </tr>
      </thead>
      <tbody>
        {rows.map(r => (
          <tr key={r.id} className="border-b hover:bg-slate-50">
            {cols.map(c => (
              <td key={String(c.key)} className="p-3">
                {c.render ? c.render(r) : String(r[c.key] ?? '')}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
```

**Comandos git MR-Semana1:**
```bash
git checkout feature/avance2-melgar
git add frontend/src/components/ui/DataTable.tsx
git commit -m "feat(frontend): componente DataTable reutilizable

- Genericos + render personalizado por columna
- Base para Pacientes, Doctores, Citas, Expediente"
git push origin feature/avance2-melgar
```

#### Semana 2 (07/09 – 13/09)

**Tarea MR-02 · Página Citas (agenda + tabla)**

- **Archivo:** `frontend/src/pages/Citas.tsx`

```tsx
import { useEffect, useState } from 'react';
import { api } from '../api/client';

type Cita = { id: number; scheduledAt: string;
              patient: {firstName:string; lastName:string};
              doctor: {user: {firstName:string; lastName:string}};   // doctor.user, no doctor directo
              status: string; reservationCode: string };

const badge = (s: string) => ({
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-blue-100 text-blue-800',
  IN_WAITING: 'bg-purple-100 text-purple-800',
  IN_CONSULTATION: 'bg-orange-100 text-orange-800',
  COMPLETED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
  NO_SHOW: 'bg-gray-200 text-gray-700'
}[s] ?? 'bg-gray-100');

export default function Citas() {
  const [rows, setRows] = useState<Cita[]>([]);
  useEffect(() => { api.get<Cita[]>('/appointments?upcoming=true').then(r => setRows(r.data)); }, []);

  const cancel = async (id: number) => {
    if (!confirm('¿Cancelar cita?')) return;
    try { await api.post(`/appointments/${id}/cancel`); setRows(rs => rs.filter(r => r.id !== id)); }
    catch (e: any) { alert(e.response?.data?.error ?? 'Error'); }
  };

  return (
    <div>
      <div className="flex justify-between mb-4">
        <h2 className="text-2xl font-bold">Citas</h2>
        <a href="/citas/nueva" className="bg-blue-600 text-white px-4 py-2 rounded">+ Nueva cita</a>
      </div>
      <table className="w-full bg-white rounded shadow">
        <thead>
          <tr className="border-b">
            <th className="text-left p-3">Fecha</th><th className="text-left p-3">Paciente</th>
            <th className="text-left p-3">Doctor</th><th className="text-left p-3">Codigo</th>
            <th className="text-left p-3">Estado</th><th></th>
          </tr>
        </thead>
        <tbody>
          {rows.map(c => (
            <tr key={c.id} className="border-b">
              <td className="p-3">{new Date(c.scheduledAt).toLocaleString('es-SV')}</td>
              <td className="p-3">{c.patient.firstName} {c.patient.lastName}</td>
              <td className="p-3">{c.doctor.user.firstName} {c.doctor.user.lastName}</td>
              <td className="p-3 font-mono">{c.reservationCode}</td>
              <td className="p-3"><span className={`px-2 py-1 rounded text-xs ${badge(c.status)}`}>{c.status}</span></td>
              <td className="p-3 text-right">
                {c.status !== 'CANCELLED' && c.status !== 'COMPLETED' && (
                  <button onClick={() => cancel(c.id)} className="text-red-600 text-sm">Cancelar</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

**Tarea MR-03 · Modal Nueva Cita**

- **Archivo:** `frontend/src/pages/CitaNueva.tsx`

```tsx
import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { useNavigate } from 'react-router-dom';

export default function CitaNueva() {
  const nav = useNavigate();
  const [doctors, setDoctors] = useState<any[]>([]);
  const [patients, setPatients] = useState<any[]>([]);
  const [slots, setSlots] = useState<string[]>([]);
  const [form, setForm] = useState({ doctorId: '', patientId: '', date: '', slot: '', reason: '' });

  useEffect(() => { api.get('/doctors').then(r => setDoctors(r.data));
                    api.get('/patients?size=100').then(r => setPatients(r.data)); }, []);

  useEffect(() => {
    if (form.doctorId && form.date)
      api.get(`/appointments/doctors/${form.doctorId}/slots?date=${form.date}`).then(r => setSlots(r.data));
  }, [form.doctorId, form.date]);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/appointments', {
        patientId: +form.patientId, doctorId: +form.doctorId,
        scheduledAt: form.slot, reason: form.reason
      });
      nav('/citas');
    } catch (err: any) { alert(err.response?.data?.error ?? 'Error creando cita'); }
  };

  return (
    <form onSubmit={submit} className="max-w-lg flex flex-col gap-3">
      <h2 className="text-2xl font-bold">Nueva cita</h2>
      <select value={form.patientId} onChange={e => setForm({...form, patientId: e.target.value})} className="border p-2 rounded" required>
        <option value="">Paciente...</option>
        {patients.map((p:any) => <option key={p.id} value={p.id}>{p.firstName} {p.lastName} — {p.dui}</option>)}
      </select>
      <select value={form.doctorId} onChange={e => setForm({...form, doctorId: e.target.value})} className="border p-2 rounded" required>
        <option value="">Doctor...</option>
        {doctors.map((d:any) => <option key={d.id} value={d.id}>{d.user?.firstName} {d.user?.lastName} — {d.specialty?.name}</option>)}
      </select>
      <input type="date" value={form.date} onChange={e => setForm({...form, date: e.target.value})} className="border p-2 rounded" required/>
      <select value={form.slot} onChange={e => setForm({...form, slot: e.target.value})} className="border p-2 rounded" required>
        <option value="">Hora disponible...</option>
        {slots.map(s => <option key={s} value={s}>{new Date(s).toLocaleTimeString('es-SV')}</option>)}
      </select>
      <textarea placeholder="Motivo" value={form.reason} onChange={e => setForm({...form, reason: e.target.value})} className="border p-2 rounded"/>
      <button className="bg-blue-600 text-white p-2 rounded">Confirmar cita</button>
    </form>
  );
}
```

**Comandos git MR-Semana2:**
```bash
git add frontend/src/pages/Citas.tsx frontend/src/pages/CitaNueva.tsx
git commit -m "feat(frontend): pantalla Citas + modal Nueva Cita con slots dinamicos

- Lista de citas con badges por estado
- Cancelar con confirmacion (respeta regla 24h del backend)
- CitaNueva carga slots segun doctor y fecha"
git push origin feature/avance2-melgar
```

#### Semana 3 (14/09 – 20/09)

**Tarea MR-05 · Expediente del paciente (timeline)**

- **Archivo:** `frontend/src/pages/Expediente.tsx`

```tsx
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api/client';

export default function Expediente() {
  const { id } = useParams();
  const [record, setRecord] = useState<any>(null);
  useEffect(() => { api.get(`/patients/${id}/medical-record`).then(r => setRecord(r.data)); }, [id]);
  if (!record) return <p>Cargando...</p>;
  return (
    <div>
      <h2 className="text-2xl font-bold">Expediente: {record.patient.firstName} {record.patient.lastName}</h2>
      <p className="text-sm text-slate-600">DUI: {record.patient.dui}</p>
      <section className="mt-6">
        <h3 className="text-lg font-semibold mb-2">Recetas</h3>
        <ul>{record.prescriptions?.map((r:any) => (
          <li key={r.id} className="border-b py-2">
            <b>{new Date(r.emittedAt).toLocaleDateString('es-SV')}</b> — Dr. {r.doctor?.lastName} · {r.items?.length} medicamento(s)
          </li>))}</ul>
      </section>
      <section className="mt-6">
        <h3 className="text-lg font-semibold mb-2">Signos vitales</h3>
        <ul>{record.vitalSigns?.map((v:any) => (
          <li key={v.id} className="border-b py-2">
            {new Date(v.recordedAt).toLocaleDateString('es-SV')} — T: {v.temperature}°C, FC: {v.heartRate}
          </li>))}</ul>
      </section>
    </div>
  );
}
```

**Comandos git MR-Semana3:**
```bash
git add frontend/src/pages/Expediente.tsx
git commit -m "feat(frontend): expediente del paciente con timeline recetas y signos vitales"
git push origin feature/avance2-melgar
```

---

### 5.7 MERINO VENTURA ALEJANDRO SEBASTIÁN — **FEATURE CRÍTICO (2.40 pts)**

**Apellido rama:** `merino` · **CIF:** `2026020122` · **Correo M365:** `2026020122@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-merino
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| M-01 (POCs backup/dashboard) | — | — | Arranca directo |
| M-02 (DatFileDao<T> genérico thread-safe) | VT-01 (Ventura) | Ventura | `git ls-files backend \| grep BaseEntity.java` |
| M-03 (AuditBackupScheduler cada 60s) | M-02 (tú) | Tú mismo | `git ls-files backend \| grep DatFileDao` |
| M-04 (backup diario expedientes .dat) | M-02 (tú) + VT-03 (Ventura) | Ventura | `git ls-files backend \| grep MedicalRecord.java` |
| M-05 (DashboardMetricsService paralelo) | F-04 (Flores, seed útil) | Flores | `git ls-files \| grep V8__seed` |
| M-06 (DashboardController REST) | M-05 (tú) + VG-08 (Vigil) | Vigil | `git ls-files backend \| grep JwtAuthenticationFilter` |


Concurrencia B (backup async a `.dat`) + C (dashboard paralelo).

#### Semana 1 (31/08 – 06/09)

**Tarea M-01 · POC de concurrencia**

- **Archivo:** `backend/src/test/java/poc/BackupDatSchedulerPoc.java`

```java
package poc;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;

public class BackupDatSchedulerPoc {
    public static void main(String[] args) throws Exception {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var data = List.of("evento1", "evento2", "evento3");
        scheduler.scheduleAtFixedRate(() -> {
            try (var out = new ObjectOutputStream(new FileOutputStream("data/poc_backup.dat"))) {
                out.writeObject(data);
                System.out.println("[" + Thread.currentThread().getName() + "] backup ejecutado " + System.currentTimeMillis());
            } catch (IOException e) { e.printStackTrace(); }
        }, 0, 5, TimeUnit.SECONDS);
        Thread.sleep(15_000);
        scheduler.shutdown();
    }
}
```

**Comandos git M-Semana1:**
```bash
git checkout feature/avance2-merino
mkdir -p backend/src/test/java/poc data
git add backend/src/test/java/poc/BackupDatSchedulerPoc.java
git commit -m "poc(dat): scheduler async que respalda a archivo .dat cada 5s"
git push origin feature/avance2-merino
```

#### Semana 2 (07/09 – 13/09)

**Tarea M-02 · Clase abstracta DatFileDao<T> (herencia + genéricos + concurrencia)**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/dat/DatFileDao.java`

```java
package com.sv.grupo7.medisuite.dat;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class DatFileDao<T extends Serializable> {

    protected final Path filePath;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected DatFileDao(String fileName) {
        this.filePath = Paths.get("data", fileName);
        try { Files.createDirectories(filePath.getParent()); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void save(List<T> items) {
        lock.writeLock().lock();
        try (var out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            out.writeObject(items);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo .dat: " + filePath, e);
        } finally { lock.writeLock().unlock(); }
    }

    @SuppressWarnings("unchecked")
    public List<T> loadAll() {
        lock.readLock().lock();
        try {
            if (!Files.exists(filePath)) return List.of();
            try (var in = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
                return (List<T>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error leyendo .dat: " + filePath, e);
        } finally { lock.readLock().unlock(); }
    }
}
```

**Tarea M-03 · AuditBackupScheduler (feature B activo)**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/dat/AuditBackupScheduler.java`

```java
package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.dao.AuditLogRepository;
import com.sv.grupo7.medisuite.model.audit.AuditLog;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuditBackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuditBackupScheduler.class);
    private final AuditLogRepository auditRepo;
    private final AuditLogDatDao datDao;
    private ScheduledExecutorService executor;

    @PostConstruct
    void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "audit-backup");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::backup, 60, 60, TimeUnit.SECONDS);
        log.info("AuditBackupScheduler iniciado (cada 60s)");
    }

    void backup() {
        try {
            var all = auditRepo.findAll();
            datDao.save(all);
            log.info("Backup .dat: {} eventos respaldados", all.size());
        } catch (Exception e) {
            log.error("Fallo backup audit .dat", e);
        }
    }

    @PreDestroy
    void stop() { if (executor != null) executor.shutdown(); }
}
```

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/dat/AuditLogDatDao.java`

```java
package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.audit.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogDatDao extends DatFileDao<AuditLog> {
    public AuditLogDatDao() { super("audit_backup.dat"); }
}
```

> **Coordinar con Ventura (VT-02):** `AuditLog` debe implementar `Serializable`.
> Como `AuditLog` tiene @ManyToOne LAZY a `User` y `Tenant`, la query que
> Merino hace antes de guardar debe forzar eager fetch o marcar esas
> relaciones `transient` para evitar `LazyInitializationException`:
>
> ```java
> @Column(name = "user_id") private Long userIdSnapshot;   // campo simple
> @Column(name = "tenant_id") private Long tenantIdSnapshot;
> ```
>
> Alternativa: leer via `auditRepo.findAll()` dentro de una `@Transactional`
> y mapear a un DTO serializable `AuditLogSnapshot` antes de escribir a `.dat`.

**Tarea M-04 · MedicalRecordBackupScheduler (backup expedientes .dat)**

- **Archivos a crear:**
  - `backend/src/main/java/com/sv/grupo7/medisuite/dat/MedicalRecordDatDao.java`
  - `backend/src/main/java/com/sv/grupo7/medisuite/dat/MedicalRecordBackupScheduler.java`
- **Archivos a modificar** (agregar `implements Serializable` + `serialVersionUID = 1L`):
  - `backend/src/main/java/com/sv/grupo7/medisuite/model/medical/MedicalRecord.java`
  - `backend/src/main/java/com/sv/grupo7/medisuite/model/medical/Patient.java`
  - `backend/src/main/java/com/sv/grupo7/medisuite/model/users/User.java`
  - `backend/src/main/java/com/sv/grupo7/medisuite/model/tenant/Tenant.java`

> **Nota:** `MedicalRecord` tiene asociaciones LAZY a `Patient` y `Tenant`. Para que
> `ObjectOutputStream` pueda serializarlas (como Hibernate proxies), todas las entidades
> de la cadena deben implementar `Serializable`. Los campos escalares (`tenantId`,
> `patientId`) siempre estarán disponibles tras deserializar.

**Paso 1 — Agregar `Serializable` a las 4 entidades**

En cada uno de los 4 archivos, agregar el import y la declaración:

```java
// Agregar imports (después de los imports existentes):
import java.io.Serial;
import java.io.Serializable;

// Modificar la declaración de clase, por ejemplo en MedicalRecord:
public class MedicalRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    // ... resto sin cambios ...
```

Mismo patrón para `Patient`, `User` y `Tenant`.

**Paso 2 — Crear `MedicalRecordDatDao.java`**

```java
package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.medical.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordDatDao extends DatFileDao<MedicalRecord> {

    public MedicalRecordDatDao() {
        super("medical_records_backup.dat");
    }
}
```

**Paso 3 — Crear `MedicalRecordBackupScheduler.java`**

```java
package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.dao.MedicalRecordRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MedicalRecordBackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordBackupScheduler.class);
    private final MedicalRecordRepository recordRepo;
    private final MedicalRecordDatDao datDao;
    private ScheduledExecutorService executor;

    @PostConstruct
    void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "medical-record-backup");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::backup, 60, 60, TimeUnit.SECONDS);
        log.info("MedicalRecordBackupScheduler iniciado (cada 60s)");
    }

    void backup() {
        try {
            var all = recordRepo.findAll();
            datDao.save(all);
            log.info("Backup .dat: {} expedientes respaldados", all.size());
        } catch (Exception e) {
            log.error("Fallo backup medical_records .dat", e);
        }
    }

    @PreDestroy
    void stop() { if (executor != null) executor.shutdown(); }
}
```

**Paso 4 — Commit**

```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/model/medical/MedicalRecord.java \
        backend/src/main/java/com/sv/grupo7/medisuite/model/medical/Patient.java \
        backend/src/main/java/com/sv/grupo7/medisuite/model/users/User.java \
        backend/src/main/java/com/sv/grupo7/medisuite/model/tenant/Tenant.java \
        backend/src/main/java/com/sv/grupo7/medisuite/dat/MedicalRecordDatDao.java \
        backend/src/main/java/com/sv/grupo7/medisuite/dat/MedicalRecordBackupScheduler.java
git commit -m "feat(M-04): backup scheduler para expedientes médicos en .dat"
git push origin feature/avance2-merino
```

**Tarea M-05 · DashboardMetricsService (feature C paralelo)**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/service/DashboardMetricsService.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DashboardMetricsService {

    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final PrescriptionRepository prescriptions;
    private final AuditLogRepository audit;

    public Map<String, Long> getMetrics(Long tenantId) {
        var start = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.of("-06:00"));
        var end = start.plusDays(1);

        var f1 = CompletableFuture.supplyAsync(() -> appointments.countByDateAndTenant(tenantId, start, end));
        var f2 = CompletableFuture.supplyAsync(() -> patients.count());       // total activos
        var f3 = CompletableFuture.supplyAsync(() -> prescriptions.count());
        var f4 = CompletableFuture.supplyAsync(() -> audit.count());

        CompletableFuture.allOf(f1, f2, f3, f4).join();

        return Map.of(
            "citasHoy",          f1.join(),
            "pacientesActivos",  f2.join(),
            "recetasEmitidas",   f3.join(),
            "alertas",           f4.join()
        );
    }
}
```

**Tarea M-06 · DashboardController**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/controller/api/DashboardController.java`

```java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardMetricsService service;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> metrics(@RequestParam(defaultValue = "1") Long tenantId) {
        return ResponseEntity.ok(service.getMetrics(tenantId));
    }
}
```

**Comandos git M-Semana2:**
```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/dat/ \
        backend/src/main/java/com/sv/grupo7/medisuite/service/DashboardMetricsService.java \
        backend/src/main/java/com/sv/grupo7/medisuite/controller/api/DashboardController.java
git commit -m "feat(dat): backup async .dat + dashboard con CompletableFuture

- DatFileDao<T> abstracta thread-safe (RRWL)
- AuditBackupScheduler cada 60s (ScheduledExecutorService)
- DashboardMetricsService con 4 queries paralelas (allOf)
- Cumple criterios: concurrencia (1.20) + persistencia .dat (1.20)"
git push origin feature/avance2-merino
```

#### Semana 3 (14/09 – 20/09)

**Tarea M-07 · Documentar la arquitectura híbrida**

- **Archivo:** `docs/PERSISTENCIA_DAT.md`

```markdown
# Persistencia híbrida PostgreSQL + .dat

## Qué va donde
- **PostgreSQL (Neon):** todos los datos transaccionales (pacientes, citas,
  recetas, expedientes, usuarios, tenants, audit_log).
- **Archivos `.dat`:** copia de respaldo asíncrona de `audit_log` cada 60s
  en `data/audit_backup.dat`.

## Por qué
- El profesor exige uso de archivos `.dat` (criterio 9, 1.20 pts).
- Concurrencia (criterio 5, 1.20 pts) se demuestra con `ScheduledExecutorService`
  respaldando `.dat` en un hilo dedicado sin bloquear el request principal.

## Arquitectura
`AuditLogRepository` (JPA) → `AuditBackupScheduler` (hilo daemon) →
`AuditLogDatDao extends DatFileDao<AuditLog>` → archivo `data/audit_backup.dat`
protegido con `ReentrantReadWriteLock`.

## Recuperación ante desastre
Si PostgreSQL se cae, `AuditLogDatDao.loadAll()` puede repoblar el audit log
desde el `.dat` (script manual de recuperación).
```

**Comandos git M-Semana3:**
```bash
git add docs/PERSISTENCIA_DAT.md
git commit -m "docs(dat): documentar arquitectura hibrida PostgreSQL + .dat"
git push origin feature/avance2-merino
```

---

### 5.8 FUENTES ORTIZ ERIKA ALEXANDRA — QA + Documentación

**Apellido rama:** `fuentes` · **CIF:** `2026011709` · **Correo M365:** `2026011709@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-fuentes
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| EK-01 (smoke test QA en Chrome/Firefox) | D-00..D-03 (Díaz) + backend levantado | Díaz | `git ls-files frontend \| grep -E "LoginPage\|MainLayout"` |
| D-08 (Perfil + cambio password) | D-00..D-03 (Díaz) + U-01 (López) | Díaz, Héctor | `git grep -l "change-password" backend/src/main/java` |
| D-09 (loading/error states consistentes) | Pantallas D-04..D-07 y MR-02..MR-06 en desarrollo | Díaz, Melgar | `git ls-files frontend/src/pages \| wc -l` (esperar ≥ 6) |
| VT-05 (JavaDoc List vs Set en Prescription) | VT-04 (Ventura) | Ventura | `git ls-files backend \| grep Prescription.java` |
| VT-07 (Serializable MedicalRecord + Prescription) | VT-03 y VT-04 (Ventura) | Ventura | `git ls-files backend \| grep -E "MedicalRecord.java\|Prescription.java"` |
| MR-08 (responsive Tailwind mobile) | Pantallas listas del equipo frontend | Melgar, Díaz | `git ls-files frontend/src/pages \| wc -l` (esperar ≥ 6) |


#### Semana 2 (07/09 – 13/09)

**Tarea EK-01 · Verificacion QA pantallas frontend (smoke test manual)**

- **Qué hace:** verificar manualmente Login, Dashboard, Pacientes y Citas en Chrome
  y Firefox; documentar cualquier bug encontrado.
- **Pasos:**
  1. Levantar el sistema: `docker compose up -d --build`
  2. Abrir `http://localhost:8097` en Chrome y en Firefox
  3. Ejecutar el recorrido: Login → Dashboard → Pacientes (buscar) → Citas → Perfil
  4. Anotar bugs en `docs/qa/BUGS_EK01.md` con: pantalla, pasos para reproducir, screenshot

- **Archivo de reporte:** `docs/qa/BUGS_EK01.md`

```bash
git checkout feature/avance2-fuentes
mkdir -p docs/qa
git add docs/qa/BUGS_EK01.md
git commit -m "docs(qa): reporte smoke test manual Chrome y Firefox — EK-01"
git push origin feature/avance2-fuentes
```

#### Semana 3 (14/09 – 20/09)

**Tarea D-09 · Estados loading/error consistentes**

- **Qué hace:** revisar todas las páginas del frontend (Login, Dashboard, Pacientes,
  Doctores, Citas, Expediente, Recetas) y agregar spinners de carga + mensajes de
  error consistentes usando un componente `<LoadingSpinner>` y `<ErrorAlert>`.
- **Archivo:** `frontend/src/components/LoadingSpinner.tsx` y `ErrorAlert.tsx`

```bash
git add frontend/src/components/LoadingSpinner.tsx \
        frontend/src/components/ErrorAlert.tsx \
        frontend/src/pages/
git commit -m "feat(frontend): estados loading/error consistentes en todas las páginas"
git push origin feature/avance2-fuentes
```

**Tarea D-08 · Pantalla Perfil con cambio de password**

- **Archivo:** `frontend/src/pages/Perfil.tsx`

```tsx
import { useState } from 'react';
import { api } from '../api/client';
import { useAuth } from '../auth/useAuth';

export default function Perfil() {
  const { user } = useAuth();
  const [oldPwd, setOld] = useState('');
  const [newPwd, setNew] = useState('');
  const [msg, setMsg] = useState('');

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/auth/change-password', { oldPassword: oldPwd, newPassword: newPwd });
      setMsg('Contrasena actualizada');
    } catch (err: any) { setMsg(err.response?.data?.error ?? 'Error'); }
  };

  return (
    <div className="max-w-md">
      <h2 className="text-2xl font-bold mb-4">Perfil</h2>
      <p><b>{user?.fullName}</b> · {user?.role}</p>
      <form onSubmit={submit} className="mt-6 flex flex-col gap-3">
        <input type="password" placeholder="Contrasena actual" value={oldPwd}
               onChange={e => setOld(e.target.value)} className="border rounded p-2"/>
        <input type="password" placeholder="Nueva contrasena" value={newPwd}
               onChange={e => setNew(e.target.value)} className="border rounded p-2"/>
        <button className="bg-blue-600 text-white py-2 rounded">Cambiar</button>
        {msg && <p>{msg}</p>}
      </form>
    </div>
  );
}
```

```bash
git add frontend/src/pages/Perfil.tsx
git commit -m "feat(frontend): pantalla Perfil con cambio de password"
git push origin feature/avance2-fuentes
```

**Tarea MR-08 · Verificar responsive Tailwind en movil**

- **Qué hace:** revisar todas las pantallas del frontend en viewport < 768px y
  corregir los layouts que se rompen en móvil usando clases responsivas de Tailwind.
- **Pantallas a revisar:** Login, Dashboard, Pacientes, Doctores, Citas, Expediente, Perfil.
- **Herramienta:** DevTools → Toggle device toolbar → iPhone 12 Pro (390px) y Galaxy S20 (412px).

Para cada pantalla que se rompa, corregir el CSS:
```tsx
// Ejemplo de fix típico: cambiar grid fijo a responsive
// Antes:
<div className="grid grid-cols-4 gap-4">
// Después:
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
```

- **Documentar** las pantallas revisadas en `docs/qa/RESPONSIVE_CHECK.md`:
```markdown
| Pantalla | 390px | 768px | 1280px | Fix aplicado |
|---|---|---|---|---|
| Login | ✅ | ✅ | ✅ | — |
| Dashboard | ⚠️ | ✅ | ✅ | grid responsive |
```

```bash
git checkout feature/avance2-fuentes
git add frontend/src/ docs/qa/RESPONSIVE_CHECK.md
git commit -m "feat(frontend): correcciones responsive Tailwind en viewports movil
- Grid 4-col → responsive 1/2/4 en Dashboard
- Tabla Pacientes con scroll horizontal en <768px"
git push origin feature/avance2-fuentes
```

**Tarea VT-05 · Documentar en JavaDoc por que List y por que Set**

- **Qué hace:** agregar JavaDoc en las clases de modelo que usan `List<T>` o `Set<T>`
  explicando el criterio de elección (orden vs. unicidad), para cumplir el criterio
  del profesor sobre colecciones justificadas.
- **Archivos a documentar:** `MedicalRecord.java`, `Prescription.java`, cualquier
  entidad con colecciones.

Ejemplo de JavaDoc a agregar:

```java
/**
 * Lista de ítems de receta en orden de inserción.
 * Se usa {@code List} (y no {@code Set}) porque el orden importa para impresión
 * y se permiten el mismo medicamento con distintas dosis.
 */
@OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL)
private List<PrescriptionItem> items = new ArrayList<>();
```

```bash
git checkout feature/avance2-fuentes
git add backend/src/main/java/com/sv/grupo7/medisuite/model/
git commit -m "docs(javadoc): justificacion de uso de List vs Set en entidades
- Cumple criterio colecciones justificadas del profesor"
git push origin feature/avance2-fuentes
```

**Tarea VT-07 · Coordinar Serializable en MedicalRecord y Prescription**

- **Qué hace:** asegurar que `MedicalRecord` y `Prescription` implementan
  `Serializable` para que el backup a `.dat` de Merino pueda serializarlas.
- **Coordinar con Ventura (VT-03, VT-04)** y con **Merino (M-02/M-03)**.

```java
// En MedicalRecord.java y Prescription.java — verificar/agregar:
public class MedicalRecord extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    // ... resto de la clase
}
```

Si BaseEntity ya implementa Serializable (por VT-01), solo confirmar que
`serialVersionUID` está declarado en las subclases.

- **Documentar la verificación** en `docs/qa/SERIALIZABLE_CHECK.md`.

```bash
git checkout feature/avance2-fuentes
git add backend/src/main/java/com/sv/grupo7/medisuite/model/ \
        docs/qa/SERIALIZABLE_CHECK.md
git commit -m "chore(backend): serialVersionUID en MedicalRecord y Prescription
- Habilita backup .dat de Merino sobre estas entidades"
git push origin feature/avance2-fuentes
```

**Tarea V-12 · Seccion 13 conclusiones del avance**

Editar `docs/AVANCE2_MEDISUITE.md` — escribir mínimo 5 conclusiones concretas sobre lo implementado en el Avance 2.

**Tarea V-13 · Seccion 14 bibliografia formato APA**

Editar `docs/AVANCE2_MEDISUITE.md` — mínimo 5 referencias bibliográficas en formato APA 7ma edición.

```bash
git add docs/AVANCE2_MEDISUITE.md
git commit -m "docs(avance2): secciones 13-14 conclusiones (min 5) y bibliografia APA (min 5 refs)"
git push origin feature/avance2-fuentes
```

---

### 5.9 VÁSQUEZ AMAYA WALTER AMÍLCAR — Backend + Frontend redistributed

**Apellido rama:** `vasquez` · **CIF:** `2026010068` · **Correo M365:** `2026010068@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-vasquez
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| DR-01 (DoctorService + DoctorController CRUD) | VG-08 (Vigil) | Vigil | `git ls-files backend \| grep JwtAuthenticationFilter` |
| D-06 (modal crear/editar paciente) | D-05 (Díaz) + P-02 (Orellana) | Díaz, Orellana | `git ls-files frontend \| grep PatientsPage` y `git ls-files backend \| grep PatientController` |
| VG-07 (2 queries paralelas para dashboard) | M-05 (Merino) | Merino | `git ls-files backend \| grep DashboardMetricsService` |
| F-05 (Tests integración Testcontainers) | F-02 (Flores) + S-01 (Sánchez) | Flores, Sánchez | `git ls-files \| grep -E "V5__\|V6__\|V7__"` y `git ls-files backend/src/test \| grep SmokeTest` |
| F-06 (verificar generación .dat) | M-04 (Merino) | Merino | `git ls-files backend \| grep -E "AuditBackupScheduler\|MedicalRecordBackup"` |
| MR-07 (vista imprimible receta @media print) | MR-06 (Melgar) | Melgar | `git ls-files frontend \| grep -i prescription` |


Vásquez recibió tareas reasignadas de otros integrantes. Todas son de código.

#### Semana 1 (31/08 – 06/09)

**Tarea H-06 · Verificar Docker levanta todo el sistema**

- **Qué hace:** comprobar que `docker compose up` construye todas las imágenes y que el
  sistema responde en el puerto 8097 con login funcional.

```bash
docker compose down -v
docker compose up -d --build
sleep 30
curl http://localhost:8097/actuator/health
# → {"status":"UP"}
# Abrir http://localhost:8097 → login con admin@demo.com / Demo2026!
```

```bash
git checkout feature/avance2-vasquez
git add docker-compose.yml
git commit -m "chore(docker): verificar que docker compose levanta en puerto 8097"
git push origin feature/avance2-vasquez
```

#### Semana 2 (07/09 – 13/09)

**Tarea VG-07 · Apoyar feature C con 2 queries paralelas**

- **Qué hace:** agregar 2 queries al repositorio que el `DashboardMetricsService` de
  Merino (M-05) invocará en paralelo con `CompletableFuture.allOf`.
- **Coordinar con Merino:** avisarle cuando estén las queries para que las integre en M-05.
- **Archivos:**

```java
// En AppointmentRepository.java — agregar:
@Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.scheduledAt) = CURRENT_DATE AND a.tenantId = :tid")
long countTodayByTenant(@Param("tid") Long tid);
```

```java
// En PatientRepository.java — agregar:
@Query("SELECT COUNT(p) FROM Patient p WHERE p.tenantId = :tid")
long countActiveByTenant(@Param("tid") Long tid);
```

```bash
git checkout feature/avance2-vasquez
git add backend/src/main/java/com/sv/grupo7/medisuite/dao/AppointmentRepository.java \
        backend/src/main/java/com/sv/grupo7/medisuite/dao/PatientRepository.java
git commit -m "feat(backend): queries countToday y countActive para feature C paralelo
- Requeridas por DashboardMetricsService de Merino (M-05)"
git push origin feature/avance2-vasquez
```

**Tarea DR-01 · DoctorService + DoctorController CRUD**

- **Qué hace:** expone el CRUD de doctores.
- **Para qué sirve:** D-07 (pantalla admin) y el selector de doctor en Nueva Cita consumen `/api/doctors`.
- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/service/DoctorService.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.DoctorRepository;
import com.sv.grupo7.medisuite.model.medical.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository repo;

    public List<Doctor> findAll() { return repo.findAll(); }

    public Doctor findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
    }

    @Transactional
    public Doctor create(Doctor d) {
        Long tid = com.sv.grupo7.medisuite.security.TenantContext.currentTenantId();
        com.sv.grupo7.medisuite.model.tenant.Tenant t = new com.sv.grupo7.medisuite.model.tenant.Tenant();
        t.setId(tid);
        d.setTenant(t);
        return repo.save(d);
    }
}
```

```java
// DoctorController.java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Doctor;
import com.sv.grupo7.medisuite.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService service;

    @GetMapping
    public ResponseEntity<List<Doctor>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> get(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }

    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor d) {
        return ResponseEntity.status(201).body(service.create(d));
    }
}
```

```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/service/DoctorService.java \
        backend/src/main/java/com/sv/grupo7/medisuite/controller/api/DoctorController.java
git commit -m "feat(backend): DoctorService + DoctorController CRUD
- GET /api/doctors lista todos los doctores del tenant
- POST /api/doctors crea doctor con tenant del JWT
- Requerido por D-07 y modal Nueva Cita"
git push origin feature/avance2-vasquez
```

**Tarea D-06 · Modal crear/editar paciente con validaciones**

- **Qué hace:** modal reutilizable con React Hook Form + Zod para crear o editar un
  paciente, validando DUI (formato `########-#`) y campos obligatorios.
- **Archivo:** `frontend/src/components/patients/PatientModal.tsx`

```tsx
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { api } from '../../api/client';

const schema = z.object({
  firstName:  z.string().min(2, 'Nombre requerido'),
  lastName:   z.string().min(2, 'Apellido requerido'),
  dui:        z.string().regex(/^\d{8}-\d$/, 'DUI: formato 00000000-0'),
  phone:      z.string().optional(),
  address:    z.string().optional(),
});
type PatientForm = z.infer<typeof schema>;

interface Props { patientId?: number; onClose: () => void; onSaved: () => void; }

export default function PatientModal({ patientId, onClose, onSaved }: Props) {
  const { register, handleSubmit, formState: { errors } } = useForm<PatientForm>({
    resolver: zodResolver(schema),
  });

  const submit = async (data: PatientForm) => {
    if (patientId) await api.put(`/patients/${patientId}`, data);
    else           await api.post('/patients', data);
    onSaved();
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <form onSubmit={handleSubmit(submit)} className="bg-white rounded-lg p-6 w-full max-w-md space-y-3">
        <h3 className="text-lg font-bold">{patientId ? 'Editar' : 'Nuevo'} paciente</h3>
        <input {...register('firstName')} placeholder="Nombre" className="border rounded p-2 w-full"/>
        {errors.firstName && <p className="text-red-500 text-sm">{errors.firstName.message}</p>}
        <input {...register('lastName')} placeholder="Apellido" className="border rounded p-2 w-full"/>
        {errors.lastName && <p className="text-red-500 text-sm">{errors.lastName.message}</p>}
        <input {...register('dui')} placeholder="DUI (00000000-0)" className="border rounded p-2 w-full"/>
        {errors.dui && <p className="text-red-500 text-sm">{errors.dui.message}</p>}
        <input {...register('phone')} placeholder="Teléfono (opcional)" className="border rounded p-2 w-full"/>
        <input {...register('address')} placeholder="Dirección (opcional)" className="border rounded p-2 w-full"/>
        <div className="flex gap-2 justify-end">
          <button type="button" onClick={onClose} className="border rounded px-4 py-2">Cancelar</button>
          <button className="bg-blue-600 text-white rounded px-4 py-2">Guardar</button>
        </div>
      </form>
    </div>
  );
}
```

Instalar dependencias si no están:
```bash
cd frontend
npm install react-hook-form zod @hookform/resolvers
```

```bash
git checkout feature/avance2-vasquez
git add frontend/src/components/patients/PatientModal.tsx frontend/package.json
git commit -m "feat(frontend): modal crear/editar paciente con React Hook Form + Zod
- Valida DUI formato SV, nombre y apellido
- Reutilizable en pantalla Pacientes de Diaz (D-05)"
git push origin feature/avance2-vasquez
```

#### Semana 3 (14/09 – 20/09)

**Tarea F-05 · Tests de integracion con Testcontainers PostgreSQL**

- **Qué hace:** 3 tests de integración que arrancan un contenedor real de PostgreSQL
  para verificar que PatientRepository, AppointmentRepository y UserRepository
  funcionan con la BD real (no mocks).
- **Archivo:** `backend/src/test/java/com/sv/grupo7/medisuite/integration/PatientRepositoryIT.java`

```java
package com.sv.grupo7.medisuite.integration;

import com.sv.grupo7.medisuite.dao.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PatientRepositoryIT {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired PatientRepository repo;

    @Test void contenedorArrastra() { assertThat(pg.isRunning()).isTrue(); }
    @Test void repositorioCarga()   { assertThat(repo).isNotNull(); }
    @Test void findAllNoExplota()   { assertThat(repo.findAll()).isNotNull(); }
}
```

En `backend/pom.xml` agregar dependencia Testcontainers:
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

```bash
git checkout feature/avance2-vasquez
git add backend/pom.xml \
        backend/src/test/java/com/sv/grupo7/medisuite/integration/PatientRepositoryIT.java
git commit -m "test(integration): Testcontainers — 3 tests con PostgreSQL real"
git push origin feature/avance2-vasquez
```

**Tarea F-06 · Verificar generacion de data/*.dat**

- **Qué hace:** verificar (coordinado con Merino) que el backup scheduler genera
  correctamente el archivo `data/audit_backup.dat` al correr la app.
- **Cómo verificarlo:**

```bash
# 1. Compilar y levantar la app
cd backend && mvn spring-boot:run &
sleep 15   # esperar que el scheduler dispare al menos una vez

# 2. Verificar que el archivo .dat existe y tiene contenido
ls -lh data/audit_backup.dat
# → debe mostrar el archivo con tamaño > 0

# 3. Confirmar que el archivo es deserializable (Java)
# (Merino tiene un método loadAll() en AuditLogDatDao que se puede llamar en el test)
```

- **Documenta el resultado** en `docs/qa/VERIFICACION_DAT.md`:

```markdown
# Verificación generación .dat

Fecha: <fecha>
Resultado: PASS / FAIL
Tamaño archivo: <bytes>
Tiempo hasta generación: <segundos>
Observaciones: <notas>
```

```bash
git add docs/qa/VERIFICACION_DAT.md
git commit -m "docs(qa): verificacion generacion data/audit_backup.dat coordinado con Merino"
git push origin feature/avance2-vasquez
```

**Tarea MR-07 · Vista imprimible de receta (@media print)**

- **Qué hace:** agrega estilos CSS `@media print` para que la página de receta
  se imprima en formato A4 limpio (sin navbar, sin sidebar).
- **Archivo:** `frontend/src/pages/RecetaPrint.tsx` + estilos

```tsx
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api/client';

export default function RecetaPrint() {
  const { id } = useParams();
  const [receta, setReceta] = useState<any>(null);
  useEffect(() => { api.get(`/prescriptions/${id}`).then(r => setReceta(r.data)); }, [id]);
  if (!receta) return <p>Cargando...</p>;
  return (
    <div className="print-only max-w-[21cm] mx-auto p-8 font-serif">
      <h1 className="text-2xl font-bold mb-1">MediSuite — Receta Médica</h1>
      <p className="text-sm text-slate-600 mb-4">Fecha: {new Date(receta.emittedAt).toLocaleDateString('es-SV')}</p>
      <p><b>Paciente:</b> {receta.patient?.firstName} {receta.patient?.lastName} — DUI: {receta.patient?.dui}</p>
      <p><b>Doctor:</b> Dr. {receta.doctor?.user?.firstName} {receta.doctor?.user?.lastName}</p>
      <hr className="my-4"/>
      <table className="w-full text-sm">
        <thead><tr><th className="text-left">Medicamento</th><th>Dosis</th><th>Frecuencia</th></tr></thead>
        <tbody>
          {receta.items?.map((i: any, idx: number) => (
            <tr key={idx}><td>{i.medicationName}</td><td>{i.dosage}</td><td>{i.frequency}</td></tr>
          ))}
        </tbody>
      </table>
      <p className="mt-8 text-xs text-slate-500">Firma del médico: ___________________________</p>
      <button onClick={() => window.print()} className="no-print mt-4 bg-blue-600 text-white px-4 py-2 rounded">
        Imprimir
      </button>
    </div>
  );
}
```

Agregar en `frontend/src/index.css`:
```css
@media print {
  .no-print { display: none !important; }
  nav, aside { display: none !important; }
  body { font-size: 12pt; }
}
```

```bash
git add frontend/src/pages/RecetaPrint.tsx frontend/src/index.css
git commit -m "feat(frontend): vista imprimible de receta con @media print A4
- Oculta navbar y sidebar al imprimir
- window.print() lanza dialogo del navegador"
git push origin feature/avance2-vasquez
```

**Tarea V-09 · Seccion 10 entradas/salidas por HU**

Editar `docs/AVANCE2_MEDISUITE.md` — escribir la Sección 10 con una tabla de entradas y salidas para cada HU (mínimo 5 HUs con I/O listadas).

```bash
git add docs/AVANCE2_MEDISUITE.md
git commit -m "docs(avance2): sección 10 — entradas y salidas por cada HU"
git push origin feature/avance2-vasquez
```

---

### 5.10 VENTURA VELÁSQUEZ CARLOS MARIO — Backend / BaseEntity + Expediente + Recetas

**Apellido rama:** `ventura` · **CIF:** `2026011585` · **Correo M365:** `2026011585@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-ventura
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| VT-01 (BaseEntity abstracta) | — (RAÍZ — todo el equipo te espera) | — | Arranca YA |
| VT-02 (refactorizar 8+ entidades) | VT-01 (tú) | Tú mismo | `git ls-files backend \| grep BaseEntity.java` |
| VT-03 (MedicalRecord entity+repo+service+ctrl) | VT-01 (tú) + F-02 (Flores) + VG-08 (Vigil) | Flores, Vigil | `git ls-files backend \| grep BaseEntity.java` y `git ls-files \| grep V6__` y `git ls-files backend \| grep JwtAuthenticationFilter` |
| VT-04 (Prescription + PrescriptionItem) | VT-01 (tú) + F-02 (Flores) | Flores | `git ls-files \| grep V7__` |
| VT-04b (PrescriptionController REST) | VT-04 (tú) + VG-08 (Vigil) | Vigil | `git ls-files backend \| grep Prescription.java` |
| VT-06 (fix audit_log null user/tenant) | — | — | Arranca directo |


#### Semana 1 (31/08 – 06/09)

**Tarea VT-01 · BaseEntity abstracta (herencia + clase abstracta = 2 criterios)**

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/model/BaseEntity.java`

```java
package com.sv.grupo7.medisuite.model;

import com.sv.grupo7.medisuite.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(); }

    public Long getTenantId() { return tenant != null ? tenant.getId() : null; }
}
```

**Tarea VT-02 · Refactorizar entidades para extender BaseEntity**

- Editar `Patient.java`, `Doctor.java`, `Appointment.java`, `MedicalRecord.java`,
  `Prescription.java`, `VitalSign.java`, `AuditLog.java`, `Specialty.java` y
  reemplazar sus `id`, `tenant`, `createdAt`, `updatedAt` heredándolos de `BaseEntity`.

Ejemplo Patient refactor:

```java
@Entity
@Table(name = "patients")
@Getter @Setter
public class Patient extends BaseEntity {   // ← extends

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;
    // ... resto de atributos SIN id/tenant/createdAt/updatedAt (los da BaseEntity)
}
```

**Comandos git VT-Semana1:**
```bash
git checkout feature/avance2-ventura
git add backend/src/main/java/com/sv/grupo7/medisuite/model/
git commit -m "feat(backend): BaseEntity abstracta + herencia en 8 entidades

- @MappedSuperclass con id/tenant/createdAt/updatedAt
- Implementa Serializable (habilita backup .dat de Merino)
- Cumple criterios: herencia (0.80) + clase abstracta (0.60)"
git push origin feature/avance2-ventura
```

#### Semana 2 (07/09 – 13/09)

**Tarea VT-03 · Ampliar módulo MedicalRecord (expediente completo)**

- **Nota:** la entidad `MedicalRecord` ya existe con `tenant`, `patient` (@OneToOne),
  `createdOn` (LocalDate), `generalNotes` (text). **No renombrar campos.** Solo
  refactorizarla para que extienda `BaseEntity` (VT-02) manteniendo los campos
  específicos.

- **Ampliar `MedicalRecordRepository.java`** (ya existe con `findByPatientIdAndTenantId`).
  Agregar consulta eager que traiga recetas y signos vitales del expediente:

```java
@EntityGraph(attributePaths = {"patient"})
Optional<MedicalRecord> findByPatientIdAndTenantId(Long patientId, Long tenantId);
```

- **Archivo nuevo:** `backend/src/main/java/com/sv/grupo7/medisuite/service/MedicalRecordService.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.model.medical.*;
import com.sv.grupo7.medisuite.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final VitalSignRepository vitalSignRepo;

    public Map<String, Object> findFullByPatientId(Long patientId) {
        Long tid = TenantContext.currentTenantId();
        MedicalRecord mr = recordRepo.findByPatientIdAndTenantId(patientId, tid)
            .orElseGet(() -> {
                Patient p = patientRepo.findById(patientId).orElseThrow();
                MedicalRecord nuevo = new MedicalRecord();
                nuevo.setPatient(p);
                nuevo.setTenant(p.getTenant());
                nuevo.setCreatedOn(java.time.LocalDate.now());
                return recordRepo.save(nuevo);
            });
        List<Prescription> prescriptions =
            prescriptionRepo.findByMedicalRecordIdOrderByIssuedOnDesc(mr.getId());
        List<VitalSign> vitalSigns =
            vitalSignRepo.findByMedicalRecordIdOrderByRecordedAtDesc(mr.getId());
        return Map.of(
            "id", mr.getId(),
            "patient", mr.getPatient(),
            "generalNotes", mr.getGeneralNotes() == null ? "" : mr.getGeneralNotes(),
            "prescriptions", prescriptions,
            "vitalSigns", vitalSigns
        );
    }
}
```

- **Archivo nuevo:** `backend/src/main/java/com/sv/grupo7/medisuite/controller/api/MedicalRecordController.java`

```java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService service;

    @GetMapping("/{patientId}/medical-record")
    public ResponseEntity<Map<String, Object>> get(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findFullByPatientId(patientId));
    }
}
```

- **Comandos git VT-03:**

```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/service/MedicalRecordService.java \
        backend/src/main/java/com/sv/grupo7/medisuite/controller/api/MedicalRecordController.java \
        backend/src/main/java/com/sv/grupo7/medisuite/dao/MedicalRecordRepository.java \
        backend/src/main/java/com/sv/grupo7/medisuite/model/medical/MedicalRecord.java
git commit -m "feat(backend): MedicalRecordService + endpoint expediente completo

- GET /api/patients/{id}/medical-record devuelve expediente con
  recetas y signos vitales anidadas (requerido por MR-05)
- Auto-crea expediente si no existe para el paciente"
git push origin feature/avance2-ventura
```

#### Semana 3 (14/09 – 20/09)

**Tarea VT-04 · Refactorizar Prescription con List<PrescriptionItem>**

- **Estado actual de la entidad:** ya existe `Prescription` con `tenant`,
  `medicalRecord`, `doctor`, `issuedOn (LocalDate)`, `medications (text)`,
  `dosage (String 150)`, `duration (String 50)`, `instructions (text)`.
- **Refactor:** dejar `issuedOn` y `instructions` (que pasa a llamarse
  `instructions` en JavaDoc como "indicaciones"). **Eliminar** los campos
  planos `medications`, `dosage`, `duration` y reemplazarlos por una colección
  `List<PrescriptionItem> items` (cumple criterio "colecciones justificadas"
  del profesor).

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/model/medical/Prescription.java`

```java
package com.sv.grupo7.medisuite.model.medical;

import com.sv.grupo7.medisuite.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
public class Prescription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "issued_on", nullable = false)
    private LocalDate issuedOn;

    @Column(columnDefinition = "text")
    private String instructions;

    /**
     * Uso List (no Set) porque el orden en que el médico escribió los
     * medicamentos importa: aparece igual en el PDF de receta y respeta la
     * secuencia de administración. Cumple criterio "colecciones justificadas".
     */
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "order_idx")
    private List<PrescriptionItem> items = new ArrayList<>();

    @PrePersist
    void prePersistPrescription() {
        if (issuedOn == null) issuedOn = LocalDate.now();
    }
}
```

- **Archivo nuevo:** `backend/src/main/java/com/sv/grupo7/medisuite/model/medical/PrescriptionItem.java`

```java
package com.sv.grupo7.medisuite.model.medical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "prescription_items")
@Getter @Setter
public class PrescriptionItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false, length = 200)
    private String medication;

    @Column(length = 100)
    private String dose;

    @Column(length = 100)
    private String frequency;

    @Column(name = "duration_days")
    private Integer durationDays;
}
```

**Tarea VT-04b · PrescriptionController REST**

- **Qué hace:** expone `POST /api/prescriptions` (crear receta con items) y
  `GET /api/prescriptions/{id}` (detalle).
- **Para qué sirve:** MR-06 (pantalla Recetas de Melgar) manda POST con
  paciente + medicamentos + indicaciones. Sin el controller la receta no se
  persiste.
- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/service/PrescriptionService.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.*;
import com.sv.grupo7.medisuite.model.medical.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repo;
    private final MedicalRecordRepository recordRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    @Transactional
    public Prescription create(Long patientId, Long doctorId,
                               String indications, List<PrescriptionItem> items) {
        // El frontend envia patientId; buscamos (o creamos) su expediente
        var patient = patientRepo.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Paciente no existe"));
        MedicalRecord mr = recordRepo
            .findByPatientIdAndTenantId(patientId, patient.getTenant().getId())
            .orElseGet(() -> {
                MedicalRecord nuevo = new MedicalRecord();
                nuevo.setPatient(patient);
                nuevo.setTenant(patient.getTenant());
                return recordRepo.save(nuevo);
            });
        Doctor d = doctorRepo.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor no existe"));

        Prescription p = new Prescription();
        p.setTenant(patient.getTenant());
        p.setMedicalRecord(mr);
        p.setDoctor(d);
        p.setIssuedOn(java.time.LocalDate.now());
        p.setInstructions(indications);
        items.forEach(it -> it.setPrescription(p));
        p.setItems(items);
        return repo.save(p);
    }

    public Prescription findById(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new RuntimeException("Receta " + id + " no existe"));
    }
}
```

- **Archivo:** `backend/src/main/java/com/sv/grupo7/medisuite/controller/api/PrescriptionController.java`

```java
package com.sv.grupo7.medisuite.controller.api;

import com.sv.grupo7.medisuite.model.medical.Prescription;
import com.sv.grupo7.medisuite.model.medical.PrescriptionItem;
import com.sv.grupo7.medisuite.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    public record CreateRequest(Long patientId, Long doctorId,
                                String indications, List<PrescriptionItem> items) {}

    @PostMapping
    public ResponseEntity<Prescription> create(@RequestBody CreateRequest r) {
        Prescription p = service.create(r.patientId(), r.doctorId(),
                                        r.indications(), r.items());
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
```

**Comandos git VT-Semana3:**
```bash
git add backend/src/main/java/com/sv/grupo7/medisuite/model/medical/Prescription.java \
        backend/src/main/java/com/sv/grupo7/medisuite/model/medical/PrescriptionItem.java \
        backend/src/main/java/com/sv/grupo7/medisuite/service/PrescriptionService.java \
        backend/src/main/java/com/sv/grupo7/medisuite/controller/api/PrescriptionController.java
git commit -m "feat(backend): modulo Prescription completo con controller REST

- Entity + List<PrescriptionItem> con orden preservado
- Service transaccional que asocia paciente-doctor-medicamentos
- POST /api/prescriptions requerido por MR-06 (frontend recetas)
- GET /api/prescriptions/{id} detalle
- Cumple HU-002 y criterio de colecciones justificadas"
git push origin feature/avance2-ventura
```

---

### 5.11 SÁNCHEZ MENJIVAR NICOLE NOHEMY — QA / Tests backend

**Apellido rama:** `sanchez` · **CIF:** `2026010813` · **Correo M365:** `2026010813@cvirtualuees.edu.sv`

#### ¿Puedo avanzar? — checklist antes de arrancar cualquier tarea

**Paso 1 — Actualiza tu rama con lo último de `develop` (siempre, antes de cada tarea):**

```bash
cd ~/Proyectos/medisuite
git fetch origin
git checkout develop && git pull origin develop
git checkout feature/avance2-sanchez
git merge develop
```

**Paso 2 — Ubica tu próxima tarea. Corre el comando y decide:**

- ✅ Sale la línea esperada → **arranca sin preguntar.**
- ❌ Sale vacío → **NO arranques.** Escribe por WhatsApp DIRECTAMENTE al responsable (no a Héctor) con capture del comando fallido.

| Tu tarea | Depende de | Responsable a contactar | Comando de verificación (desde raíz `medisuite/`) |
|---|---|---|---|
| S-01 (setup JUnit 5 + Mockito) | — (raíz, ya hecha) | — | Arranca directo |
| S-02 (tests AuthService, 5 casos) | S-01 (tú) | Tú mismo | `git ls-files backend/src/test \| grep SmokeTest` |
| S-03 (tests Appointment/Patient/MedicalRecord Service) | S-01 (tú) + O-03 (Orellana) + VT-03 (Ventura) | Orellana, Ventura | `git ls-files backend \| grep -E "AppointmentService\|MedicalRecordService"` |
| S-04 (test integración backup .dat) | S-01 (tú) + M-04 (Merino) | Merino | `git ls-files backend \| grep -E "DatFileDao\|MedicalRecordBackup"` |
| S-05 (casos manuales HU-001..HU-010, ≥20) | Pantallas D-04..D-07 y MR-02..MR-06 disponibles | Díaz, Melgar | `git ls-files frontend/src/pages \| wc -l` (esperar ≥ 6) |
| S-06 (reporte de bugs con priorización) | S-05 (tú) | Tú mismo | Después de completar los casos manuales |


#### Semana 1 (31/08 – 06/09)

**Tarea S-01 · Setup JUnit 5 + Mockito**

- **Archivo:** `backend/pom.xml` verificar presencia:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

- **Archivo:** `backend/src/test/java/com/sv/grupo7/medisuite/SmokeTest.java`

```java
package com.sv.grupo7.medisuite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmokeTest {
    @Test void arithmeticWorks() { assertEquals(4, 2+2); }
}
```

**Comandos git S-Semana1:**
```bash
git checkout feature/avance2-sanchez
git add backend/pom.xml backend/src/test/java/com/sv/grupo7/medisuite/SmokeTest.java
git commit -m "test(setup): habilitar JUnit 5 con smoke test"
git push origin feature/avance2-sanchez
```

#### Semana 2 (07/09 – 13/09)

**Tarea S-02 · Tests unitarios AuthService**

- **Archivo:** `backend/src/test/java/com/sv/grupo7/medisuite/service/AuthServiceTest.java`

```java
package com.sv.grupo7.medisuite.service;

import com.sv.grupo7.medisuite.dao.UserRepository;
import com.sv.grupo7.medisuite.dto.auth.LoginRequest;
import com.sv.grupo7.medisuite.exception.BusinessException;
import com.sv.grupo7.medisuite.model.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock UserRepository userRepo;
    AuthService service;

    @BeforeEach void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuthService(userRepo /* + otros mocks */);
    }

    @Test void loginConPasswordMalaLanzaException() {
        User u = new User();
        u.setEmail("test@test.com");
        u.setPasswordHash(new BCryptPasswordEncoder().encode("correcto"));
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(u));

        assertThrows(BusinessException.class,
            () -> service.login(new LoginRequest("demo", "test@test.com", "malo")));
    }
}
```

**Comandos git S-Semana2:**
```bash
git add backend/src/test/java/
git commit -m "test(auth): tests unitarios AuthService (login OK, password mala, bloqueo)"
git push origin feature/avance2-sanchez
```

#### Semana 3 (14/09 – 20/09)

**Tarea S-04 · Test de integración backup .dat (feature B de Merino)**

- **Archivo:** `backend/src/test/java/com/sv/grupo7/medisuite/dat/AuditBackupDatTest.java`

```java
package com.sv.grupo7.medisuite.dat;

import com.sv.grupo7.medisuite.model.audit.AuditLog;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AuditBackupDatTest {

    @Test void guardaYRecuperaAuditLogs() {
        AuditLogDatDao dao = new AuditLogDatDao();
        AuditLog log = new AuditLog();
        log.setAction("LOGIN");
        dao.save(List.of(log));

        List<AuditLog> loaded = dao.loadAll();
        assertEquals(1, loaded.size());
        assertEquals("LOGIN", loaded.get(0).getAction());
    }
}
```

**Casos manuales:** `docs/qa/CASOS_PRUEBA.md` con mínimo 20 casos cubriendo HU-001 a HU-010.

**Comandos git S-Semana3:**
```bash
git add backend/src/test/java/com/sv/grupo7/medisuite/dat/AuditBackupDatTest.java \
        docs/qa/CASOS_PRUEBA.md
git commit -m "test(dat): integration test del backup .dat + 20 casos manuales HU-001..010"
git push origin feature/avance2-sanchez
```

**Tarea V-11 · Sección 12 del documento: El Proyecto**

Editar `docs/AVANCE2_MEDISUITE.md` — escribir la Sección 12 con los 11 subpuntos técnicos del proyecto.

```bash
git add docs/AVANCE2_MEDISUITE.md
git commit -m "docs(avance2): sección 12 — descripción técnica del proyecto (11 subpuntos)"
git push origin feature/avance2-sanchez
```

---

## 6. Verificación end-to-end antes de la entrega (Héctor ejecuta 21/09)

```bash
# 1. Nadie subió commits con IA
git log --all --pretty=format:"%H %ae %s%n%b" | \
  grep -iE "co-authored-by|claude|anthropic|generated with|chatgpt|copilot"
# → VACÍO

# 2. Cada integrante tiene ≥ 5 commits en su rama
git shortlog -sne --all | sort -rn

# 3. Compila
cd backend && mvn clean package
# → BUILD SUCCESS

# 4. Docker levanta
docker compose up -d --build
sleep 30
curl http://localhost:8097/actuator/health
# → {"status":"UP"}

# 5. Backup .dat se genera
ls -la backend/data/audit_backup.dat

# 6. Dashboard metrics responde
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8097/api/dashboard/metrics
# → JSON con 4 métricas

# 7. PDF en raíz
ls AVANCE2_MEDISUITE.pdf

# 8. Tag de entrega
git tag -a v2.0.0-avance2 -m "Entrega Avance 2 — 27/09/2026"
git push origin v2.0.0-avance2
```

---

## 7. Contactar a Héctor si algo falla

WhatsApp del grupo → mensaje directo. **No** intentes forzar merges ni resetear
nada sin autorización. Un error de push a `main` es más costoso que esperar 10
minutos a que Héctor lo revise.
