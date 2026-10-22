# Fase B · Guía de revisión y merge de PR para Carlos Mario y Bayron

**Vigente desde:** 07/10/2026. **Cierre interno:** 19/10/2026. **Rama integradora:** `feature/avance3-fase-b`.

Responsables de continuidad: Carlos Mario Ventura Velásquez (GitHub `@mdealerdude`) y Bayron Alexander Orellana Rojas (GitHub `@crislomsu`). El 07/10 se comprobó que ambas cuentas tienen permiso `write` en `NapoSV/medisuite`; vuelvan a comprobarlo si GitHub rechaza una acción. Estas cuentas son las que aparecen en PR anteriores y en la consulta de permisos del repositorio; las menciones `@cventura` y `@borellana` de las guías originales no son las cuentas GitHub verificadas.

## 1. Autoridad y límites

- Pueden revisar y ejecutar el merge de PR **de Fase B** hacia `feature/avance3-fase-b` una vez satisfechas todas las puertas de esta guía. No hagan push directo a la integradora.
- Ninguno aprueba ni mergea su propio PR. Carlos revisa el trabajo de Bayron y Bayron el de Carlos; para cambios de seguridad, multi-tenant, migraciones o configuración de base de datos pidan además una segunda revisión de alguien del área correspondiente.
- `develop` y `main` no son destinos de los PR individuales de Fase B. Un PR dirigido a otra rama se devuelve al autor para corregir la base **antes** de revisar o mergear. El PR automático de Dependabot hacia `main` se coordina con Héctor por separado.
- No basta que GitHub muestre `MERGEABLE` o CodeRabbit verde. En este repositorio esos checks no sustituyen la compilación, tests, inspección del diff ni la prueba de integración.
- Cuando el workflow `Fase B PR` esté integrado, exijan que `backend-tests` y `frontend-build` terminen en verde para el commit vigente. Hasta que GitHub los marque como obligatorios, son una puerta de revisión manual; nunca ignoren un check rojo.
- Si hay conflicto, prueba fallida, evidencia falsa/no reproducible, dato sensible, cambio de esquema no acordado o duda de aislamiento entre clínicas, usen **Request changes**. No hagan merge “para desbloquear” y corregir después.

## 2. Orden de integración

1. Configuración independiente de Axios: PR de seguridad, tras revisar build, lockfile y alcance.
2. B5 excepciones y B2 pool: pueden revisarse en paralelo, pero se mergean de uno en uno; B2 solo si Hikari/JPA/Flyway siguen usando el principal y las pruebas exigidas existen y pasan.
3. B4 DAO abstracto y puertos, después de B2 y B5.
4. B1 dashboard backend, B3a recordatorios y B3b reserva, después de sus dependencias y pruebas cruzadas. Un PR **draft** no se mergea.
5. B1b, regresión, documentación y cierre. Tras cada merge, vuelvan a validar la rama integradora antes de aprobar el siguiente PR.

Consulten la matriz de dependencias de Fase B que Héctor distribuyó al equipo. No interpreten “PR abierto” como “dependencia integrada”.

## 3. Puerta de entrada: verificar el PR antes de tocar código

Comandos para **Git Bash**, desde la raíz de su propio clon de MediSuite. Sustituyan únicamente el número de PR; el ejemplo usa `45`.

```bash
PR=45
git status --short --branch
git fetch origin
gh pr view "$PR" --repo NapoSV/medisuite --json number,title,state,isDraft,baseRefName,headRefName,mergeStateStatus,reviewDecision,commits,files
gh pr diff "$PR" --repo NapoSV/medisuite --name-only
gh pr checks "$PR" --repo NapoSV/medisuite
```

Confirmen en la salida: PR abierto; **base exactamente** `feature/avance3-fase-b`; no draft; autor distinto del revisor; rutas acordes con la tarea; commits nuevos desde la última revisión; ningún `.env`, secreto, token, IP sensible, dato clínico real, artefacto generado o migración con número duplicado. Si el árbol local tiene cambios de otra persona, no los limpien ni los agreguen al PR. Si la base es errónea, pidan al autor corregirla y revisen de nuevo el diff resultante; cambiar la base puede cambiar por completo la lista de archivos.

Lean el cuerpo del PR como **declaración del autor**, no como prueba. Verifiquen la existencia de cada test, script y archivo citado y que el comando realmente lo ejecute. Una suite verde que no carga el contexto Spring, la BD o los casos nuevos no demuestra la integración.

## 4. Revisión aislada y reproducible

No cambien su rama de trabajo ni toquen la BD compartida de Neon para revisar. Desde la raíz del clon en Git Bash:

```bash
PR=45
REVIEW_DIR="../medisuite-review-pr-$PR"
if [ -e "$REVIEW_DIR" ]; then
  echo "La carpeta de revisión ya existe; inspecciónala antes de continuar"
else
  git fetch origin "pull/$PR/head" &&
  git worktree add --detach "$REVIEW_DIR" FETCH_HEAD &&
  git -C "$REVIEW_DIR" diff --check origin/feature/avance3-fase-b...HEAD &&
  git -C "$REVIEW_DIR" diff --stat origin/feature/avance3-fase-b...HEAD
fi
```

Inspeccionen el diff completo y los consumidores de las clases cambiadas. Revisen lógica, errores, valores nulos, límites, seguridad por rol/tenant, transacciones, cierre de conexiones, compatibilidad de migraciones y efectos sobre A1/A2. Si el PR agrega JDBC, exijan consultas parametrizadas y `tenant_id` en las rutas apropiadas. No ejecuten migraciones o pruebas destructivas en Neon compartido; usen Testcontainers/PostgreSQL descartable.

Pruebas mínimas según los archivos afectados:

```bash
(cd "$REVIEW_DIR/backend" && mvn -q test)
(cd "$REVIEW_DIR/frontend" && pnpm install --frozen-lockfile && pnpm exec tsc --noEmit && pnpm build && pnpm audit --prod)
```

Ejecuten solo el bloque del componente afectado, más las pruebas de regresión que su contrato necesite. El check automático `backend-tests` ejecuta `mvn test` y, por separado, `PatientRepositoryIT` con PostgreSQL descartable. `mvn test` **no incluye por defecto** otras clases nuevas llamadas `*IT`: exíjanlas explícitamente y no las presenten como cubiertas por el check hasta incluirlas. Para reproducir `PatientRepositoryIT` en Windows con Docker Desktop y contexto `desktop-linux`, puede ser necesario definir `DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine` solo para el proceso de Maven. Nunca utilicen Neon compartido para esta prueba. Si el PR modifica `frontend/Dockerfile`, hagan también `docker build -t medisuite-review-pr-$PR "$REVIEW_DIR/frontend"`. Ejecuten `pnpm lint` cuando toque frontend; si falla, comparen **la misma versión de la herramienta** y el mismo comando contra la base e identifiquen si el PR añadió errores. No llamen verde a una prueba omitida. Registren comando, número de tests, fallos/omisiones, fecha y entorno en la revisión del PR.

Antes de aprobar una migración Flyway: comprueben orden y unicidad del número, arranque desde esquema limpio y actualización desde la versión anterior en una BD descartable. Antes de aprobar un pool: prueben el tipo/nombre de cada bean y que JPA y Flyway reciban Hikari primario; un `mvn test` que no levanta Spring no prueba esto.

Al terminar, desde la raíz del clon y **solo si la ruta coincide con el worktree creado**:

```bash
git worktree list
git worktree remove "$REVIEW_DIR"
```

Si `git worktree remove` se niega porque hay cambios, deténganse e inspeccionen `git -C "$REVIEW_DIR" status --short`; no usen `--force` ni borren la carpeta a ciegas.

## 5. Decisión de revisión

Dejen un comentario que cualquiera pueda reproducir:

```text
Resultado: APROBAR / PEDIR CAMBIOS / BLOQUEADO
PR y commit revisado:
Base y archivos verificados:
Comandos ejecutados y resultados (tests/fallos/omitidos):
Pruebas no ejecutadas y motivo:
Hallazgos con archivo, línea, escenario e impacto:
Dependencias/QA pendiente:
```

Un hallazgo bloqueante se comenta en la línea afectada y se marca **Request changes**. Después del push correctivo del autor, vuelvan a leer el diff y repetir pruebas; la aprobación anterior no valida commits nuevos. Si no hay hallazgos materiales y la evidencia está completa, usen **Approve**. No aprueben su propio PR ni acepten la marca `mergeable` como aprobación técnica.

## 6. Merge seguro (solo después de aprobar)

Antes del merge, vuelvan a comprobar base, estado, commit HEAD, revisiones y checks; no utilicen un número de PR de memoria. En Git Bash:

```bash
PR=45
gh pr view "$PR" --repo NapoSV/medisuite --json state,isDraft,baseRefName,headRefName,mergeStateStatus,reviewDecision,commits,reviews,statusCheckRollup
gh pr checks "$PR" --repo NapoSV/medisuite
```

Si todo sigue conforme y no hubo commits nuevos sin revisar, el responsable autorizado ejecuta **un PR a la vez**:

```bash
gh pr merge "$PR" --repo NapoSV/medisuite --merge
```

No usen `--auto`, `--admin`, force-push ni borren la rama fuente mientras pueda ser base de otro trabajo. Tras el merge:

```bash
gh pr view "$PR" --repo NapoSV/medisuite --json state,mergedAt,mergeCommit,baseRefName
git fetch origin
git log -1 --oneline origin/feature/avance3-fase-b
```

Repitan compilación y pruebas pertinentes **sobre el nuevo estado integrado**, no solo sobre el branch del autor, y comuniquen a los dependientes el número de PR, commit de integración y contrato que pueden usar. Si falla la integradora, detengan los siguientes merges, avisen a Héctor y preparen una corrección o revert mediante PR; nunca `reset --hard` ni push forzado.

## 7. Checklist especial por riesgo

| Cambio | Evidencia indispensable antes del merge |
|---|---|
| B2 pool JDBC | Hikari explícitamente primario, Tomcat solo `jdbcDataSource`, misma BD/esquema salvo decisión documentada, 15 parámetros reales y justificados, test del contexto/beans y conexión descartable. |
| B5 excepciones | SQLState y causa preservados, respuesta pública sin SQL/PHI, 400/422 existentes conservados, tests de handler y clasificación. |
| B4/B1 JDBC | DAO/puertos compatibles, cierre de recursos, errores seguros, dos tenants y dos médicos, pruebas contra PostgreSQL descartable. |
| B3a/B3b | V11 de Bayron y V12 de Merino sin colisión, restricción de BD e idempotencia bajo carrera, sin datos duplicados. |
| Frontend/dependencias | Contrato backend real, estados de carga/error/permiso, lockfile congelado, build y smoke; diferenciar lint preexistente de regresión. |
| Documentación/entrega | Afirmaciones respaldadas por código/pruebas/capturas, 11 contribuciones reales y ninguna credencial o dato clínico en evidencias. |

Si necesitan acceso de merge, comprueben su cuenta activa con `gh auth status` **sin compartir tokens**. Si pierden el permiso `write` o un ruleset cambia, no intenten rodearlo; pidan a Héctor que corrija el acceso.

## 8. Protección de la integradora (configuración de Héctor)

La rama `feature/avance3-fase-b` debe exigir PR, una aprobación de otra persona, descartar aprobaciones cuando haya nuevos commits, resolver conversaciones y bloquear force-push/borrado. Las tareas sensibles mantienen la segunda revisión definida en la sección 1, aunque GitHub solo exija una como mínimo. Héctor activa esta regla en **Settings → Branches** para el nombre exacto de la rama; no aplica a `develop` ni `main` y no corrige por sí sola un PR abierto con base equivocada.

Después de integrar el workflow y observar una ejecución exitosa, Héctor agrega como checks obligatorios `backend-tests` y `frontend-build`. No los marque obligatorios antes de que existan y pasen en GitHub. El lint de frontend aún tiene fallos preexistentes y **otras** pruebas `*IT` nuevas no entrarán automáticamente en `mvn test`; `PatientRepositoryIT` sí se ejecuta expresamente en el CI con Docker. Carlos y Bayron conservan las pruebas de riesgo y regresión manuales de esta guía.
