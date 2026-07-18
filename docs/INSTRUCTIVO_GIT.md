# Instructivo Git — Equipo MediSuite

**Responsable de PRs:** ORELLANA BAYRON (Scrum Master) o LOPEZ HECTOR (PM)  
**Nivel requerido:** básico. Si tienes dudas, usa GitHub Desktop o pide ayuda a BAYRON/VIGIL.

---

## Instalación (una sola vez)

1. Descargar Git: https://git-scm.com/download/win
2. Alternativa GUI: **GitHub Desktop** — recomendado si eres nuevo en Git
3. Configurar tu identidad (reemplaza con tus datos reales):

```bash
git config --global user.name "Tu Nombre Completo"
git config --global user.email "tu@correo.com"
```

---

## Clonar el repositorio

```bash
git clone https://github.com/NapoSV/medisuite.git
cd medisuite
```

---

## Flujo de trabajo diario

### Antes de empezar a trabajar

```bash
# Asegurarse de estar en develop y tener lo último
git checkout develop
git pull origin develop

# Crear tu rama para la tarea
git checkout -b feature/HU-001-descripcion-corta
```

**Formato del nombre de rama:** `feature/HU-XXX-descripcion`  
Ejemplos: `feature/HU-001-registro-pacientes`, `feature/HU-002-recetas`

### Mientras trabajas

```bash
# Ver qué archivos cambiaste
git status

# Agregar tus cambios
git add .

# Hacer commit (usar prefijo estándar)
git commit -m "feat: implementar registro de pacientes HU-001"

# Subir tu rama a GitHub
git push origin feature/HU-001-descripcion-corta
```

### Abrir Pull Request

1. Ir a https://github.com/NapoSV/medisuite
2. GitHub mostrará un banner "Compare & pull request" — clic ahí
3. Asegurarse de que el PR va hacia **`develop`** (no hacia `main`)
4. Escribir descripción clara: qué hiciste, qué HU resuelve
5. Asignar a **BAYRON** como reviewer
6. Mover la tarea en Planner a `👀 En Revisión`

---

## Prefijos de commit (usar siempre)

| Prefijo | Cuándo usarlo |
|---------|--------------|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `docs:` | Solo documentación |
| `refactor:` | Refactorización sin cambio de comportamiento |
| `test:` | Agregar o corregir tests |
| `chore:` | Tareas de mantenimiento (deps, config) |
| `style:` | Formato, espaciado (sin cambio de lógica) |

---

## Resolver conflictos de merge

Si al hacer `git pull` aparecen conflictos:

1. Git te dirá qué archivos tienen conflicto
2. Abrir el archivo y buscar estas marcas:
   ```
   <<<<<<< HEAD
   tu código
   =======
   código de develop
   >>>>>>> develop
   ```
3. Decidir qué versión conservar (o mezclar ambas)
4. Eliminar las marcas `<<<<<<<`, `=======`, `>>>>>>>`
5. Guardar el archivo y correr:
   ```bash
   git add [archivo-con-conflicto]
   git commit -m "fix: resolver conflicto de merge en [archivo]"
   ```

Si no estás seguro, **pide ayuda a BAYRON o VIGIL antes de hacer cualquier cosa**.

---

## Ramas del proyecto

| Rama | Propósito | Quién hace push |
|------|-----------|-----------------|
| `main` | Solo entregas académicas | HECTOR (PM), supervisado |
| `develop` | Integración diaria | Vía PR aprobado |
| `feature/HU-XXX-*` | Tu trabajo | Cada developer |

**Regla de oro:** nunca hacer push directo a `main` ni a `develop`. Siempre vía Pull Request.

---

## Comandos útiles de referencia

```bash
git status                    # Ver estado del repositorio
git log --oneline -10         # Ver los últimos 10 commits
git diff                      # Ver cambios sin commitear
git stash                     # Guardar cambios temporalmente
git stash pop                 # Recuperar cambios guardados
git fetch origin              # Bajar actualizaciones sin aplicar
git branch -a                 # Ver todas las ramas
git checkout develop          # Cambiar a develop
```

---

## Errores comunes

**"Your branch is behind 'origin/develop'"**  
→ `git pull origin develop` antes de trabajar.

**"Please commit your changes before merging"**  
→ Hacer `git add . && git commit -m "..."` antes de hacer pull.

**"Permission denied (publickey)"**  
→ Tu clave SSH no está configurada. Usar HTTPS en vez de SSH, o pedir ayuda a HECTOR.

---

## Contacto

- Dudas de Git / PRs bloqueados: **@BAYRON** o **@VIGIL** en Teams
- Acceso al repo: **@HECTOR**
