# Politica de commits — MediSuite

Reglas obligatorias para todos los integrantes del equipo. El workflow de CI
`.github/workflows/commit-trailers-check.yml` valida estas reglas en cada `push` y
`pull_request` a `main` y `develop`; si un commit incumple, la corrida falla y
el PR queda bloqueado.

## 1. Prohibido cualquier rastro de IA en los commits

**No debe aparecer ninguna mencion a Claude, Anthropic, GitHub Copilot, GPT,
OpenAI, Codex, Cursor, Gemini, Bard, Windsurf ni cualquier otra herramienta de
IA en ningun commit del repositorio.**

Esto aplica en:

- El **subject** (primera linea del commit).
- El **body** (mensaje largo).
- Los **trailers** al final del mensaje, especialmente `Co-authored-by:`,
  `Signed-off-by:`, `Generated-by:` o cualquier variante.
- Los mensajes de merge, revert y tag.

### Ejemplos prohibidos

```
Co-authored-by: Claude <noreply@anthropic.com>
Co-authored-by: GitHub Copilot <copilot@github.com>
Generated-by: Claude Code
feat: nueva pantalla generada con ayuda de Claude
```

### Ejemplos permitidos

```
feat(H-02): validar longitud del JWT secret
fix(O-05): registerFailedAttempt atomico via JPQL
docs: actualizar guia de despliegue
```

## 2. Formato de mensaje

Usar prefijo tipo Conventional Commits + codigo de tarea del Planner:

```
<tipo>(<codigo-tarea>): <descripcion en presente, minusculas, sin punto final>
```

Tipos aceptados: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `ci`,
`build`, `perf`, `style`.

Ejemplos:

- `feat(U-01): endpoint GET /api/users/me`
- `fix(H-03): cerrar swagger-ui detras de rol ADMIN`
- `docs(V-02): objetivo general y objetivos especificos`

## 3. Verificacion local antes de hacer push

Antes de `git push`, revisar los ultimos commits:

```bash
git log origin/main..HEAD --format='%H%n%B%n---'
```

Buscar manualmente cualquier trailer `Co-authored-by:` o mencion a las
herramientas listadas arriba. Si aparece algo, reescribir el commit:

```bash
# ultimo commit
git commit --amend

# commits mas atras
git rebase -i origin/main
```

## 4. Que hacer si el workflow falla en un PR

1. Revisar el log del job `check-commits` para ver que commit tiene el
   trailer prohibido.
2. Reescribir el historial local con `git rebase -i` para eliminar la linea
   ofensiva del mensaje.
3. Force-push a la rama del PR: `git push --force-with-lease origin <rama>`.
4. La CI vuelve a correr y el PR se desbloquea.

## 5. Configuracion recomendada en editores y agentes

- **VS Code + extensiones de IA:** desactivar cualquier opcion que agregue
  autoria automatica en commits.
- **Claude Code / Cursor / Copilot Chat:** al pedirles preparar un commit,
  indicar explicitamente "sin Co-authored-by" en el prompt.
- **Git hooks locales:** opcionalmente instalar un `commit-msg` hook que
  rechace localmente los mismos patrones (mismo regex que el workflow).

## 6. Justificacion

- **Integridad academica:** la entrega es del equipo humano; agregar autoria
  a herramientas de IA distorsiona el registro de contribuciones.
- **Trazabilidad:** cada commit debe atribuirse a un integrante identificable
  para efectos de evaluacion y responsabilidad.
- **Politica del docente:** las herramientas de IA se pueden usar como apoyo,
  pero no se acreditan como coautoras del codigo entregado.
