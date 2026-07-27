# GUÍA PASO A PASO — Avance 1 MediSuite

> Documento único de referencia para los 11 integrantes del equipo MediSuite.
> **Cada persona lee SOLO su(s) tarea(s) y sigue las instrucciones al pie de la letra.**
>
> **Proyecto:** MediSuite — SaaS de gestión de citas médicas.
> **Universidad:** Universidad Evangélica de El Salvador (UEES).
> **Materia:** Programación II.
> **Repositorio GitHub:** https://github.com/NapoSV/medisuite
> **Documento Word del equipo (Teams · canal "📋 Avances del Proyecto"):**
> https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH
>
> **Fecha:** 26/07/2026 · **Versión:** v1.0

---

> ⚠️ **IMPORTANTE — LEE ESTO PRIMERO**
>
> **Este documento tiene TODO lo que necesitas.** No abras otros archivos a menos que se te indique explícitamente. Cada tarea trae:
> - Los comandos exactos que debes escribir.
> - El código o texto exacto que debes copiar y pegar.
> - Las verificaciones que debes hacer antes de subir tu trabajo.
> - Prompts de IA para cuando te trabes.
>
> Si algo no funciona: primero revisa los prompts de IA de tu tarea; después escribe en el canal de Teams **"📋 Avances del Proyecto"** con: número de tarea, número de paso donde te trabaste y captura del error.

---

## Índice

### Parte 1 — Preparación común (leer una sola vez)

- [§0. Herramientas a instalar](#seccion-0-herramientas)
- [§1. Clonar el repositorio (una sola vez)](#seccion-1-clonar)
- [§2. Actualizar el repo antes de empezar](#seccion-2-actualizar)
- [§3. Crear tu rama de trabajo](#seccion-3-crear-rama)
- [§4. Guardar cambios y subirlos (commit + push)](#seccion-4-commit-push)
- [§5. Abrir un Pull Request](#seccion-5-pull-request)
- [§6. Usar IA cuando te atasques](#seccion-6-ai-prompts)

### Parte 2 — Tareas (25 tareas del Avance 1)

**Documento (Word compartido en Teams):**

- [Tarea 1.1 — Portada individual](#tarea-1-1)
- [Tarea 2.1 — Objetivo General](#tarea-2-1)
- [Tarea 3.1 — Objetivo Específico Avance 1](#tarea-3-1)
- [Tarea 4.1 — Distribución del Equipo Scrum](#tarea-4-1)
- [Tarea 5.1 — Roles y funciones del sistema](#tarea-5-1)
- [Tarea 6.1 — Historias de Usuario](#tarea-6-1)
- [Tarea 7.1 — Alcances y límites](#tarea-7-1)
- [Tarea 8.1 — Planificación (tabla)](#tarea-8-1)
- [Tarea 9.1 — Cronograma Gantt](#tarea-9-1)
- [Tarea 10.1 — Entradas/Salidas por HU](#tarea-10-1)
- [Tarea 11.1 — Declaración de Entidades](#tarea-11-1)

**Base de datos, Backend, Frontend y QA (código):**

- [Tarea 12.1 — Ejecutar schema.sql en Neon](#tarea-12-1)
- [Tarea 12.2 — Inicializar Spring Boot](#tarea-12-2)
- [Tarea 12.3 — Configurar Neon (application.yml)](#tarea-12-3)
- [Tarea 12.4 — Estructura de paquetes + Entidades](#tarea-12-4)
- [Tarea 12.5 — Repositories JPA](#tarea-12-5)
- [Tarea 12.6 — DTOs + AuthService](#tarea-12-6)
- [Tarea 12.7 — Spring Security + JWT + /api/auth/login](#tarea-12-7)
- [Tarea 12.8 — Init frontend Vite + React + TS + Tailwind + Zustand](#tarea-12-8)
- [Tarea 12.9 — Pantalla LoginPage.tsx](#tarea-12-9)
- [Tarea 12.10 — Casos de prueba QA](#tarea-12-10)

**Cierre del avance:**

- [Tarea 13.1 — Conclusiones individuales](#tarea-13-1)
- [Tarea 14.1 — Bibliografía APA](#tarea-14-1)
- [Consolidar documento final + PDF](#tarea-consolidar)
- [Entrega Avance 1](#tarea-entrega)

---

# PARTE 1 — Preparación común

<a id="seccion-0-herramientas"></a>

## §0. Herramientas a instalar

Instala **todas** las herramientas que aplican a tu rol. Si tienes dudas de tu rol, mira la [Tarea 4.1](#tarea-4-1).

| Rol | Herramientas obligatorias |
|---|---|
| Todos | Git for Windows, GitHub Desktop, Cuenta GitHub, VS Code |
| Backend | + Java 21 (Temurin), IntelliJ IDEA Community, Postman |
| Frontend | + Node.js 20 LTS |
| Base de datos | + DBeaver Community |
| QA | + Postman |
| Documento | Solo Word (ya viene con Office 365 de la UEES) |

### 0.1 Cuenta de GitHub (obligatoria para todos)

1. Ve a https://github.com/signup
2. Crea la cuenta con tu correo personal (no el de la universidad, para evitar problemas después de graduarte).
3. Verifica el correo.
4. Envía tu usuario de GitHub por Teams a Héctor López para que te agregue como colaborador del repositorio.

### 0.2 Git for Windows

1. Descarga desde https://git-scm.com/download/win (el instalador inicia solo).
2. Ejecuta el instalador. En cada pantalla del wizard:
   - **Select Components:** dejar valores por defecto.
   - **Choosing the default editor:** selecciona **"Use Visual Studio Code as Git's default editor"** (si no lo tienes instalado, deja Vim).
   - **Adjusting your PATH environment:** selecciona **"Git from the command line and also from 3rd-party software"** ← IMPORTANTE.
   - **Choosing HTTPS transport backend:** OpenSSL.
   - **Configuring line ending conversions:** "Checkout Windows-style, commit Unix-style line endings".
   - **Configuring the terminal emulator:** MinTTY.
   - **Credential Manager:** "Git Credential Manager" ← IMPORTANTE.
   - Resto: valores por defecto.
3. **Verificar instalación:** abre PowerShell y ejecuta:
   ```powershell
   git --version
   ```
   Debe imprimir algo como `git version 2.46.0.windows.1`.

### 0.3 GitHub Desktop (recomendado si no te sientes cómodo con la consola)

1. Descarga desde https://desktop.github.com/
2. Ejecuta el instalador (se instala automático).
3. Al abrirlo, inicia sesión con tu cuenta de GitHub.
4. **Verificar instalación:** la app abre en "Let's get started!".

### 0.4 Java 21 (solo Backend)

1. Descarga **Eclipse Temurin JDK 21 (LTS)** desde https://adoptium.net/temurin/releases/?version=21
2. Elige: OS = Windows, Architecture = x64, Package Type = **JDK**, Version = 21 (LTS). Descarga el `.msi`.
3. Ejecuta el instalador. En el wizard, marca:
   - ✅ **"Set JAVA_HOME variable"**
   - ✅ **"Add to PATH"**
   - ✅ **"JavaSoft (Oracle) registry keys"**
4. **Verificar instalación:** abre PowerShell y ejecuta:
   ```powershell
   java -version
   javac -version
   ```
   Ambos deben mostrar `21.x.x`.

### 0.5 Node.js 20 LTS (solo Frontend)

1. Descarga desde https://nodejs.org/en/download (elige LTS 20.x, Windows Installer .msi 64-bit).
2. Ejecuta el instalador con opciones por defecto (dejará todo marcado, incluyendo "Add to PATH").
3. **Verificar instalación:**
   ```powershell
   node -v
   npm -v
   ```
   `node` debe ser `v20.x.x`, `npm` debe ser `10.x.x` o superior.

### 0.6 DBeaver Community (solo Base de datos)

1. Descarga desde https://dbeaver.io/download/ (elige **Community Edition**, Windows Installer).
2. Ejecuta el instalador con opciones por defecto.
3. Al abrirlo por primera vez, cerrar cualquier pop-up.
4. **Verificar instalación:** abre DBeaver → menú **Database → New Database Connection** debe mostrar una lista con "PostgreSQL".

### 0.7 Visual Studio Code (todos)

1. Descarga desde https://code.visualstudio.com/download (elige **User Installer 64 bit**).
2. Ejecuta el instalador. En el wizard, marca:
   - ✅ **"Add 'Open with Code' action to Windows Explorer file context menu"**
   - ✅ **"Add 'Open with Code' action to Windows Explorer directory context menu"**
   - ✅ **"Add to PATH"**
3. **Verificar instalación:**
   ```powershell
   code --version
   ```
4. **Extensiones recomendadas** (instalar todas desde el menú Extensions – icono de cuadros del panel izquierdo):
   - GitLens
   - GitHub Pull Requests and Issues
   - Prettier
   - ESLint (Frontend)
   - Tailwind CSS IntelliSense (Frontend)
   - Extension Pack for Java (Backend, si no usas IntelliJ)
   - Spring Boot Extension Pack (Backend)

### 0.8 IntelliJ IDEA Community (solo Backend, recomendado)

1. Descarga desde https://www.jetbrains.com/idea/download/?section=windows (elige **Community Edition** — es gratis).
2. Ejecuta el instalador con opciones por defecto. Marca:
   - ✅ **"Create Desktop Shortcut"**
   - ✅ **"Add 'bin' folder to the PATH"**
   - ✅ **"Add 'Open Folder as Project'"**
   - ✅ **".java"** (asociar archivos)
3. **Verificar instalación:** abre IntelliJ; en la primera pantalla debe aparecer "Welcome to IntelliJ IDEA".

### 0.9 Postman (Backend y QA)

1. Descarga desde https://www.postman.com/downloads/ (elige Windows 64-bit).
2. Ejecuta el instalador (se instala automáticamente en tu perfil).
3. Al abrirlo, puedes usar **"Skip and go to the app"** para no crear cuenta.
4. **Verificar instalación:** debes ver la interfaz principal con una pestaña "Untitled Request".

---

<a id="seccion-1-clonar"></a>

## §1. Clonar el repositorio (una sola vez)

Vas a bajar el proyecto a tu computadora. Esto se hace **solo la primera vez**.

### Opción Web (GitHub Desktop)

1. Abre **GitHub Desktop**.
2. Menú **File → Clone repository...**
3. Pestaña **URL**.
4. En "Repository URL", pega:
   ```
   https://github.com/NapoSV/medisuite
   ```
5. En "Local path", escribe (reemplaza `{tu-usuario}` por tu usuario de Windows):
   ```
   C:\Users\{tu-usuario}\medisuite
   ```
6. Click **Clone**.
7. Espera a que termine (barra de progreso). Al finalizar verás el repo en la ventana principal.

### Opción Consola (PowerShell)

1. Abre **PowerShell** (menú Inicio → escribe "PowerShell" → Enter).
2. Ejecuta:
   ```powershell
   cd C:\Users\$env:USERNAME
   git clone https://github.com/NapoSV/medisuite.git
   cd medisuite
   git status
   ```
3. Debe imprimir:
   ```
   On branch main
   Your branch is up to date with 'origin/main'.
   nothing to commit, working tree clean
   ```

### ¿Qué hacer si sale error de autenticación?

Si al hacer clone te pide usuario y contraseña y falla:

1. **Instala Git Credential Manager** (viene con Git for Windows si marcaste la opción en §0.2).
2. Si aún falla, crea un **Personal Access Token (PAT)**:
   - Ve a https://github.com/settings/tokens
   - Click **Generate new token → Generate new token (classic)**.
   - Note: `medisuite`. Expiration: `90 days`. Scopes: marca **repo** completo.
   - Click **Generate token** y **cópialo** (solo se muestra una vez).
   - Cuando Git pida contraseña, pega el PAT en lugar de tu contraseña.
3. Si sigue fallando, escribe en Teams "📋 Avances del Proyecto" con captura del error.

---

<a id="seccion-2-actualizar"></a>

## §2. Actualizar el repo antes de empezar

**Antes de crear tu rama o empezar cualquier tarea nueva**, actualiza tu repo local para bajar los cambios más recientes que otros integrantes hayan subido.

### Opción Web (GitHub Desktop)

1. Abre GitHub Desktop con el repo `medisuite` seleccionado.
2. Barra superior: click en **Current Branch** y selecciona **main**.
3. Click en **Fetch origin** (arriba a la derecha).
4. Si aparece **"Pull origin"**, click en ese botón.

### Opción Consola (PowerShell)

```powershell
cd C:\Users\$env:USERNAME\medisuite
git checkout main
git pull origin main
```

Debe imprimir `Already up to date.` o listar los archivos que se actualizaron.

---

<a id="seccion-3-crear-rama"></a>

## §3. Crear tu rama de trabajo

### ¿Qué es una rama?

Imagina el proyecto como un árbol. La **rama principal** (`main`) es el tronco: lo que ya está estable. Cuando tú trabajas en una tarea, creas una **rama nueva** (una ramita) para no romper el tronco. Cuando terminas y tu trabajo está validado, se hace un **Pull Request** para unir tu ramita al tronco.

**Regla de oro:** nunca trabajes directamente en `main` o `develop`. Siempre en tu propia rama.

### Opción Web (GitHub Desktop)

1. Barra superior: click en **Current Branch → New Branch**.
2. Name: escribe el nombre de rama exacto que aparece en tu tarea (ejemplo: `feature/HU-007-entities`).
3. Base: **main** (o **develop** si tu tarea lo indica).
4. Click **Create Branch**.
5. Click en el botón **Publish branch** que aparece arriba a la derecha.

### Opción Consola (PowerShell)

```powershell
cd C:\Users\$env:USERNAME\medisuite
git checkout main
git pull origin main
git checkout -b nombre-de-tu-rama
git push -u origin nombre-de-tu-rama
```

Reemplaza `nombre-de-tu-rama` por el nombre exacto de tu tarea (ejemplo: `docs/avance1`, `feature/HU-007-entities`, `chore/spring-boot-init`).

**El nombre exacto de rama para cada tarea está en la sección "📍 Dónde va tu trabajo" de esa tarea.**

---

<a id="seccion-4-commit-push"></a>

## §4. Guardar cambios y subirlos (commit + push)

Un **commit** guarda una foto de tus cambios con un mensaje. Un **push** sube esos commits a GitHub.

### Opción Web (GitHub Desktop)

1. Ya con tus archivos modificados, abre GitHub Desktop.
2. En el panel izquierdo verás los **Changes** (archivos modificados con check marcado).
3. **Desmarca los archivos que NO son de tu tarea** (por ejemplo, `.env`, archivos temporales del IDE).
4. Abajo a la izquierda:
   - **Summary (required):** escribe el mensaje del commit sugerido por tu tarea.
   - **Description (opcional):** deja vacío.
5. Click en el botón azul **"Commit to [rama]"**.
6. Arriba a la derecha click en **"Push origin"**.

### Opción Consola (PowerShell)

```powershell
git status
git add ruta/al/archivo1 ruta/al/archivo2
git commit -m "mensaje sugerido de tu tarea"
git push origin nombre-de-tu-rama
```

### ⚠️ Reglas importantes

- **NUNCA uses `git add .`** — puedes subir por accidente el `.env` con contraseñas o archivos temporales.
- **Siempre agrega los archivos por nombre** (mira la sección "🚀 Cómo commit + push" de tu tarea).
- Si tu tarea dice que hay que subir 3 archivos, agrega solo esos 3.

---

<a id="seccion-5-pull-request"></a>

## §5. Abrir un Pull Request (PR)

Un PR es la solicitud para que tu trabajo se una a la rama principal. Aquí tu jefe (Héctor López) revisa y aprueba.

1. Abre en el navegador: https://github.com/NapoSV/medisuite/pulls
2. Click en el botón verde **"New pull request"**.
3. Configura:
   - **base:** `develop` (o `main` si tu tarea lo indica).
   - **compare:** el nombre de tu rama.
4. Click **"Create pull request"**.
5. **Title:** copia el mismo mensaje del commit.
6. **Description:** escribe brevemente qué hiciste (opcional para tareas pequeñas).
7. En la barra derecha:
   - **Reviewers:** agrega a **NapoSV** (Héctor López).
   - **Assignees:** ponte a ti mismo.
8. Click **"Create pull request"**.
9. **Copia la URL del PR** (la barra del navegador). La vas a pegar en Teams al notificar.

---

<a id="seccion-6-ai-prompts"></a>

## §6. Usar IA cuando te atasques

### Herramientas gratuitas

- **ChatGPT** — https://chat.openai.com (necesitas cuenta con correo, gratis).
- **Claude** — https://claude.ai (necesitas cuenta con correo, gratis).
- **GitHub Copilot** — extensión en VS Code (gratis para estudiantes: https://education.github.com/pack).

### Fórmula de un prompt eficaz

```
ROL: Actúa como mentor de [tecnología, ej. Spring Boot / React / SQL] con nivel junior.
CONTEXTO: Estoy en el proyecto MediSuite (SaaS de citas médicas hecho por estudiantes de la UEES).
Estoy trabajando en la Tarea N.M — [nombre de la tarea].
PROBLEMA: Cuando ejecuto [comando] me sale este error:
[PEGAR EL ERROR COMPLETO, línea por línea]
CÓDIGO ACTUAL: El archivo que edité es [ruta] y su contenido es:
[PEGAR EL CÓDIGO COMPLETO]
PREGUNTA: ¿Qué significa el error y qué línea exacta debo cambiar? Explícamelo paso a paso, sin asumir que sé el tema.
RESULTADO ESPERADO: Que el comando termine sin errores y [describir qué debería pasar].
```

### Consejos

- **NO pegues credenciales** (usuarios, contraseñas, tokens, cadenas de conexión reales).
- **Revisa las sugerencias antes de aplicarlas** — la IA a veces inventa métodos que no existen.
- **Si la primera respuesta no funciona**, respóndele: "Eso no funcionó, sigue saliendo [nuevo error]. ¿Qué otra opción hay?"
- **Pide que te explique**, no solo el fix: "¿Por qué se solucionó cambiando eso?"

---

# PARTE 2 — Tareas del Avance 1

---

<a id="tarea-1-1"></a>
## Tarea 1.1 — Portada individual

**Responsable(s):** Todos los 11 integrantes.
**Fecha inicio:** 15/07 · **Fecha fin:** 20/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tu fila completa (foto + nombre + CIF + "SI") en la portada del documento del entregable.

### 📍 Dónde va tu trabajo

Documento del equipo (Word compartido en Teams canal **"📋 Avances del Proyecto"**):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Portada (página inicial)"**.

### 🛠️ Herramientas que vas a usar

- Microsoft Word (Office 365 de la UEES).
- Cualquier app para editar fotos (Paint 3D, Photos, o https://www.iloveimg.com/crop-image para recortar en línea).

### 📋 Paso a paso

1. Consigue una **foto tuya cuadrada** (mismo alto que ancho), mínimo **300×300 píxeles**. Puedes recortar una foto de perfil.
2. Abre el link del Word arriba (§ Dónde va tu trabajo). Se abre en el navegador con Word Online.
3. Baja a la sección **"Portada (página inicial)"**.
4. Busca la tabla de integrantes; encuentra la fila con tu nombre (o agrega tu fila si aún no está — ver plantilla abajo).
5. En la celda de **Fotografía**: menú **Insertar → Imagen → Este dispositivo** y elige tu foto.
6. Verifica que:
   - Tu **nombre completo esté en MAYÚSCULAS**.
   - Tu **CIF sea correcto** (si no lo sabes, escribe `—` y avisa a Héctor).
   - La celda **"¿Participó?"** diga **`SI`**.

### 💻 Contenido a copiar (plantilla de la tabla, ya está creada en el Word)

Formato de referencia con las 11 filas ya listas:

| Fotografía | Nombre completo | CIF | ¿Participó? |
|---|---|---|---|
| [foto] | LOPEZ RUIZ HECTOR NAPOLEON | 79360441 | SI |
| [foto] | VIGIL RAMIREZ ALEJANDRO ANTONIO | 60111191 | SI |
| [foto] | ORELLANA ROJAS BAYRON ALEXANDER | 75699490 | SI |
| [foto] | DIAZ SANTOS ZAIR BENETT | 74528330 | SI |
| [foto] | FLORES HERNANDEZ WALTER ALEJANDRO | 75497362 | SI |
| [foto] | MELGAR RIVAS WILLIAM ARIEL | 63167135 | SI |
| [foto] | ALEJANDRO SEBASTIAN MERINO VENTURA | — | SI |
| [foto] | FUENTES ORTIZ ERIKA ALEXANDRA | 76590699 | SI |
| [foto] | VASQUEZ AMAYA WALTER AMILCAR | 74869704 | SI |
| [foto] | VENTURA VELASQUEZ CARLOS MARIO | 60127297 | SI |
| [foto] | SANCHEZ MENJIVAR NICOLE NOHEMY | 74243942 | SI |

### ✅ Verificaciones ANTES de dar por terminada la tarea

- [ ] Tu foto se ve nítida y cuadrada (no aplastada).
- [ ] Tu nombre está en MAYÚSCULAS y bien escrito.
- [ ] Tu CIF corresponde al que tienes en la universidad.
- [ ] La celda "¿Participó?" dice `SI`.
- [ ] Guardaste (Word Online guarda automático — verifica que arriba diga "Guardado").

Si algo falla: NO cierres el Word sin guardar. Ve a "🤖 Prompts de IA" abajo.

### 🚀 Cómo commit + push

> **Nota:** esta tarea NO tiene commit en Git — el Word se guarda automáticamente en OneDrive/Teams. Solo asegúrate de que dice "Guardado" arriba en el Word.

### 🌐 Cómo abrir el Pull Request

No aplica (tarea de documento).

### 🤖 Prompts de IA útiles

1. **Para recortar tu foto cuadrada:**
   ```
   Actúa como mentor de edición de imagen. Tengo una foto rectangular en mi computadora y necesito recortarla en cuadrada 300x300 sin instalar programas. Guíame paso a paso usando una herramienta gratuita web.
   ```
2. **Para subir imagen a Word Online:**
   ```
   Actúa como mentor de Word Online. Necesito insertar una imagen dentro de una celda específica de una tabla existente, y que la imagen se ajuste al tamaño de la celda sin deformarse. Explícame paso a paso.
   ```
3. **Para arreglar problema con la tabla:**
   ```
   Estoy en Word Online editando una tabla y al pegar mi imagen se rompió la fila. ¿Cómo deshago sin perder los datos de mis compañeros? Explícame los shortcuts y el menú exacto.
   ```

### 🎬 Cierre

1. Marca todos los checks de "✅ Verificaciones" arriba.
2. Verifica que Word dice "Guardado" en la barra superior.
3. Notifica en Teams "📋 Avances del Proyecto":
   > ✅ Terminé Tarea 1.1 — Portada. Foto y datos subidos al Word.
4. Mueve la tarjeta a bucket **"Hecho"** en Planner.

### ⚠️ Si te trabas

1. Re-lee esta sección desde el paso 1.
2. Prueba los prompts de IA de arriba.
3. Escribe en Teams "📋 Avances del Proyecto" con: nombre tarea, número paso, screenshot del error.

---

<a id="tarea-2-1"></a>
## Tarea 2.1 — Objetivo General

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 15/07 · **Fecha fin:** 16/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Un párrafo con el objetivo general único del proyecto MediSuite.

### 📍 Dónde va tu trabajo

Documento del equipo (Word en Teams canal **"📋 Avances del Proyecto"**):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Objetivo general"**.

### 🛠️ Herramientas que vas a usar

- Microsoft Word (Office 365 UEES).

### 📋 Paso a paso

1. Abre el Word en el navegador (link de arriba).
2. Baja a la sección **"Objetivo general"**.
3. Borra cualquier texto placeholder que haya.
4. Pega tal cual el texto del bloque siguiente.
5. Verifica que quede en un solo párrafo, justificado.

### 💻 Contenido a copiar (pegar literal en el Word)

```
Desarrollar una plataforma de gestión de citas médicas multi-empresa (SaaS) que permita optimizar la administración de agendas, el registro de pacientes y el historial clínico en una clínica u hospital, mejorando la eficiencia y calidad de la atención sanitaria mediante control de acceso por roles, digitalización del triaje y trazabilidad del expediente clínico.
```

### ✅ Verificaciones

- [ ] Sección "Objetivo general" con exactamente ese texto.
- [ ] Sin marcas de texto rojo/verde por errores de ortografía sin resolver.
- [ ] Word muestra "Guardado" arriba.

### 🚀 Cómo commit + push

> Word guarda automático, no hay commit para esta tarea.

### 🤖 Prompts de IA útiles

1. **Si tienes que reescribirlo:**
   ```
   Actúa como redactor técnico. Necesito un objetivo general para un proyecto de sistema de citas médicas SaaS, en 3-4 líneas, empezando con verbo en infinitivo. Aquí el contexto: [pegar contexto]. Dame 3 versiones.
   ```
2. **Formato Word:**
   ```
   ¿Cómo justifico un párrafo en Word Online usando teclado? Explícame el shortcut y dónde queda el botón en el menú.
   ```
3. **Si el texto sale con formato raro:**
   ```
   Al pegar texto en Word Online sale con letra distinta al resto del documento. ¿Cómo lo pego como texto sin formato? Explícame las 2 formas.
   ```

### 🎬 Cierre

1. ✅ Verificaciones marcadas.
2. Word guardado.
3. Notificar en Teams: `✅ Terminé Tarea 2.1 — Objetivo General.`
4. Mover tarjeta a "Hecho" en Planner.

### ⚠️ Si te trabas
Re-lee → prueba prompts IA → escribe en Teams.

---

<a id="tarea-3-1"></a>
## Tarea 3.1 — Objetivo Específico del Avance 1

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 15/07 · **Fecha fin:** 16/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
El OE1 (objetivo específico del Avance 1) escrito en el Word.

### 📍 Dónde va tu trabajo

Word compartido (Teams · "📋 Avances del Proyecto"):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Objetivos específicos"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word.
2. Baja a la sección **"Objetivos específicos"**.
3. Deja únicamente el **OE1** (el profesor pide 1 por avance).
4. Pega el texto del bloque siguiente.

### 💻 Contenido a copiar

```
OE1: Diseñar e implementar el módulo de registro y autenticación de usuarios (pacientes, médicos, enfermeras, administradores, recepcionistas) con control de roles y permisos basado en JWT, sobre una arquitectura multi-tenant compatible con la evolución del proyecto a SaaS comercial.
```

### ✅ Verificaciones

- [ ] Sección "Objetivos específicos" muestra únicamente OE1.
- [ ] Texto pegado tal cual (empieza con "OE1:").
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático, no hay commit.

### 🤖 Prompts de IA útiles

1. **Reformular OE:**
   ```
   Actúa como asesor académico. Reescribe este objetivo específico manteniendo su significado técnico pero adaptado a estudiantes de segundo año: [pegar OE1].
   ```
2. **Justificar el OE:**
   ```
   Explícame en 2 párrafos por qué el módulo de autenticación con JWT es un buen primer entregable en un proyecto SaaS de salud. Estilo formal académico.
   ```
3. **Formato en Word:**
   ```
   ¿Cómo pongo "OE1:" en negrita usando Word Online sin usar el ratón? Dame el shortcut.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 3.1 — Objetivo Específico OE1.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-4-1"></a>
## Tarea 4.1 — Distribución del Equipo Scrum

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 15/07 · **Fecha fin:** 16/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tabla completa de 11 filas con Nombre · Rol Scrum · Rol Técnico.

### 📍 Dónde va tu trabajo

Word (Teams · "📋 Avances del Proyecto"):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Distribución del equipo"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word.
2. Baja a **"Distribución del equipo"**.
3. Inserta la tabla si no existe: menú **Insertar → Tabla → 4 columnas × 12 filas** (1 fila de encabezado + 11 integrantes).
4. Pega o escribe los datos del bloque siguiente.
5. Aplica estilo de tabla claro (menú **Diseño de tabla → estilo con encabezado azul**).

### 💻 Contenido a copiar

| N° | Nombre completo | Rol Scrum | Rol Técnico |
|----|-----------------|-----------|-------------|
| 1  | LOPEZ RUIZ HECTOR NAPOLEON | PRODUCT OWNER | Analista de Negocio / PM |
| 2  | VIGIL RAMIREZ ALEJANDRO ANTONIO | SCRUM MASTER | Desarrollador Fullstack |
| 3  | ORELLANA ROJAS BAYRON ALEXANDER | DEVELOPER | Desarrollador Backend |
| 4  | DIAZ SANTOS ZAIR BENETT | DEVELOPER | Desarrollador Frontend |
| 5  | FLORES HERNANDEZ WALTER ALEJANDRO | DEVELOPER | Desarrollador Backend |
| 6  | MELGAR RIVAS WILLIAM ARIEL | DEVELOPER | Desarrollador Frontend |
| 7  | MERINO VENTURA ALEJANDRO SEBASTIAN | DEVELOPER | Base de Datos |
| 8  | FUENTES ORTIZ ERIKA ALEXANDRA | QA | Analista de Pruebas |
| 9  | VASQUEZ AMAYA WALTER AMILCAR | QA | Analista de Pruebas |
| 10 | VENTURA VELASQUEZ CARLOS MARIO | ARCHITECT | Arquitecto de Software |
| 11 | SANCHEZ MENJIVAR NICOLE NOHEMY | BUSINESS ANALYST | Analista de Negocio |

### ✅ Verificaciones

- [ ] Tabla tiene exactamente 11 integrantes (12 filas contando encabezado).
- [ ] Todos los nombres en MAYÚSCULAS.
- [ ] Roles Scrum en MAYÚSCULAS.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Explicar los roles Scrum a compañeros:**
   ```
   Actúa como Scrum Master. Explícame en 2 líneas cada uno de estos roles: Product Owner, Scrum Master, Developer, QA, Architect, Business Analyst. Nivel principiante.
   ```
2. **Insertar tabla en Word Online:**
   ```
   ¿Cómo inserto una tabla de 4 columnas por 12 filas en Word Online y le aplico un estilo con encabezado azul? Paso a paso.
   ```
3. **Ordenar la tabla:**
   ```
   Tengo una tabla en Word con 11 filas y quiero ordenarla alfabéticamente por la columna "Nombre completo" sin perder el número de la primera columna. ¿Cómo lo hago?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 4.1 — Distribución del Equipo Scrum.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-5-1"></a>
## Tarea 5.1 — Roles y funciones del sistema

**Responsable(s):** Nicole Sánchez (BA).
**Fecha inicio:** 17/07 · **Fecha fin:** 20/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tabla con **5 roles del software** (no del equipo Scrum), cada uno con descripción y 4+ funciones.

### 📍 Dónde va tu trabajo

Word (Teams · "📋 Avances del Proyecto"):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Roles y fundamentos del sistema"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word.
2. Baja a **"Roles y fundamentos del sistema"**.
3. Inserta una tabla de **3 columnas × 6 filas** (encabezado + 5 roles).
4. Pega los datos del bloque de abajo.
5. Para los "bullets" dentro de una celda de Word, usa Enter dentro de la celda y luego el ícono de lista con viñetas.

### 💻 Contenido a copiar

**Encabezado (columnas):** Rol | Descripción | Funciones en el sistema

**Fila 1 — Médico**
- **Descripción:** Profesional de la salud encargado del diagnóstico, tratamiento y seguimiento del paciente.
- **Funciones:**
  - Ver agenda de citas asignadas.
  - Acceder al expediente clínico del paciente.
  - Registrar diagnóstico, recetas y evolución.
  - Modificar o cancelar citas con justificación.
  - Generar reportes de atención.

**Fila 2 — Paciente**
- **Descripción:** Persona que recibe atención médica y es el centro del sistema.
- **Funciones:**
  - Registrarse y gestionar su perfil.
  - Solicitar y cancelar citas.
  - Visualizar su historial clínico y recetas.
  - Recibir notificaciones de citas y recordatorios.

**Fila 3 — Enfermera**
- **Descripción:** Profesional que realiza triaje, toma signos vitales y apoya en la atención.
- **Funciones:**
  - Registrar signos vitales del paciente (peso, talla, presión, temperatura, FC).
  - Completar datos de pacientes en espera.
  - Clasificar prioridad de atención (triaje).
  - Consultar agenda y estado de citas.

**Fila 4 — Administrador**
- **Descripción:** Encargado de la gestión general del sistema y de los usuarios.
- **Funciones:**
  - Crear, modificar y eliminar usuarios (médicos, enfermeras, etc.).
  - Configurar horarios y disponibilidad de consultorios.
  - Generar reportes administrativos.
  - Gestionar auditoría de accesos.

**Fila 5 — Recepcionista**
- **Descripción:** Personal de recepción que maneja la entrada y salida de pacientes.
- **Funciones:**
  - Registrar pacientes nuevos.
  - Asignar citas de acuerdo a disponibilidad.
  - Confirmar asistencia de pacientes.
  - Imprimir órdenes de atención.

### ✅ Verificaciones

- [ ] Tabla con exactamente 5 roles del sistema (Médico, Paciente, Enfermera, Administrador, Recepcionista).
- [ ] Cada rol tiene 4 funciones como mínimo.
- [ ] Descripciones sin faltas de ortografía (Word marca en rojo — corrígelas).
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Ampliar descripciones:**
   ```
   Actúa como BA. Amplía la descripción de este rol del sistema en 1-2 líneas más, manteniendo el estilo formal: "[pegar descripción]".
   ```
2. **Diferencia rol Scrum vs rol del sistema:**
   ```
   Explícame la diferencia entre "rol del equipo Scrum" y "rol del sistema" en un proyecto de software, con un ejemplo del rubro de salud.
   ```
3. **Formato listas en tabla Word:**
   ```
   Estoy en Word Online. Dentro de una celda de tabla quiero poner viñetas para listar funciones, pero al presionar Enter el cursor salta de celda. ¿Cómo lo evito?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 5.1 — Roles y funciones del sistema (5 roles + 4-5 funciones cada uno).`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-6-1"></a>
## Tarea 6.1 — Historias de Usuario

**Responsable(s):** Nicole Sánchez (BA) + Héctor López (PM).
**Fecha inicio:** 17/07 · **Fecha fin:** 22/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tabla con **6 historias de usuario** (backlog priorizado), destacando **HU-007 Login** que es la que se implementa en el Avance 1.

### 📍 Dónde va tu trabajo

Word (Teams · "📋 Avances del Proyecto"):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Historias de usuario"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word.
2. Baja a **"Historias de usuario"**.
3. Inserta tabla de 4 columnas × 7 filas (encabezado + 6 HU).
4. Pega/escribe cada fila del bloque siguiente.
5. Debajo de la tabla, agrega una nota: *"HU implementada en el Avance 1: HU-007 (Login). Las demás quedan en el backlog para Avances 2-3."*

### 💻 Contenido a copiar

**Encabezado:** Prioridad | HU | Historia de Usuario | Criterios de Aceptación

**Fila 1 — Alta / HU-007**
- **HU:** Como usuario del sistema, quiero iniciar sesión de forma segura con mi rol y clínica, para acceder a las funciones que me corresponden.
- **Criterios:**
  - El sistema valida email + password contra la BD.
  - Devuelve un JWT con expiración de 15 minutos.
  - Redirige al dashboard según el rol.
  - Bloquea la cuenta tras 5 intentos fallidos en 15 minutos.
  - El primer login obliga a cambiar el password temporal.

**Fila 2 — Alta / HU-001**
- **HU:** Como enfermera o recepcionista, quiero un formulario para registrar pacientes y consultar su expediente, para atenderlos rápidamente.
- **Criterios:**
  - Registro con nombre, CIF, fecha nacimiento y contacto.
  - Búsqueda por nombre o CIF.
  - El expediente muestra citas, diagnósticos y signos vitales previos.

**Fila 3 — Alta / HU-008**
- **HU:** Como administrador de clínica, quiero crear usuarios (médicos, enfermeras, recepcionistas) con su rol, para gestionar el acceso al sistema.
- **Criterios:**
  - Alta con nombre, email y rol.
  - Password temporal generado automáticamente.
  - Usuario obligado a cambiar el password en el primer login.

**Fila 4 — Alta / HU-003**
- **HU:** Como paciente o recepcionista, quiero solicitar una cita médica en línea y elegir horario disponible, para asegurar la atención.
- **Criterios:**
  - Muestra horarios disponibles del médico seleccionado.
  - Confirma con código de reserva único.
  - Notifica a paciente y médico.
  - Cancela o reprograma con al menos 24 h de anticipación.

**Fila 5 — Media / HU-004**
- **HU:** Como enfermera, quiero realizar el triaje de pacientes en espera y registrar signos vitales, para que el médico atienda con contexto completo.
- **Criterios:**
  - Búsqueda de paciente por CIF o nombre.
  - Registra peso, talla, presión, temperatura, FC, síntomas.
  - Asigna prioridad (bajo/medio/alto/crítico).
  - Se guarda en el expediente.

**Fila 6 — Media / HU-002**
- **HU:** Como médico, quiero registrar una receta, para llevar el control del tratamiento del paciente.
- **Criterios:**
  - Receta almacenada en expediente.
  - Asociada a paciente y médico.
  - Fecha, hora, medicamentos, dosis, duración.

**Nota bajo la tabla:**
> HU implementada en el Avance 1: **HU-007 (Login)**. Las demás quedan en el backlog para Avances 2-3.

### ✅ Verificaciones

- [ ] 6 HU en la tabla.
- [ ] HU-007 aparece primera y en Alta prioridad.
- [ ] Cada HU sigue el formato `Como [rol], quiero [funcionalidad], para [propósito]`.
- [ ] Cada HU tiene criterios de aceptación en viñetas.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Escribir criterios de aceptación:**
   ```
   Actúa como Product Owner Agile. Dame 3 criterios de aceptación adicionales para esta HU: "[pegar la HU]". Formato: viñetas cortas, verificables, sin ambigüedad.
   ```
2. **Traducir HU técnica a lenguaje de negocio:**
   ```
   Traduce esta historia de usuario técnica a lenguaje que un doctor entienda sin tecnicismos: "[pegar HU-007]".
   ```
3. **Validar formato HU:**
   ```
   ¿Cumple esta historia con el formato "Como [rol], quiero [funcionalidad], para [propósito]"? Si no, dame la versión corregida: "[pegar tu HU]".
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 6.1 — 6 HU documentadas, HU-007 completa para implementación.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-7-1"></a>
## Tarea 7.1 — Alcances y límites

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 16/07 · **Fecha fin:** 18/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Lista clara de lo que el sistema **NO** hará + alcance específico del Avance 1.

### 📍 Dónde va tu trabajo

Word (Teams):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Alcances y límites"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word → sección "Alcances y límites".
2. Crea dos subsecciones: **"El sistema NO incluirá"** y **"Alcances del Avance 1"**.
3. Pega el contenido de abajo respetando el formato de viñetas.

### 💻 Contenido a copiar

**El sistema NO incluirá:**

- Módulo de facturación o pagos de servicios médicos.
- Integración con sistemas externos de laboratorio o farmacia.
- Funcionalidad de telemedicina (videollamadas) en esta versión.
- Historial de pagos o seguros médicos.
- Modificación de datos médicos por parte del paciente (solo lectura).
- Reportes financieros.
- Firma electrónica certificada para recetas.
- Aplicación móvil nativa (solo web responsive).

**Alcances del Avance 1 específicamente:**

- Se implementa únicamente el módulo de **autenticación y roles (HU-007)**.
- Se entrega el **código base Spring Boot** con estructura de paquetes, entidades JPA (mínimo `Patient` y `Doctor`), y endpoint `POST /api/auth/login` funcional.
- Se entrega la **pantalla de login** en React consumiendo el endpoint.
- La base de datos con las 17 tablas queda **desplegada en Neon** y accesible al equipo.

### ✅ Verificaciones

- [ ] Al menos 5 puntos en "NO incluirá".
- [ ] Sección "Alcances del Avance 1" presente.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Agregar más límites al sistema:**
   ```
   Actúa como PM de software. Estoy definiendo qué NO hará un sistema de citas médicas SaaS en su MVP. Dame 3 exclusiones más, escritas como viñeta corta.
   ```
2. **Justificar por qué no hay telemedicina:**
   ```
   ¿Cómo justifico académicamente que la primera versión NO incluye telemedicina, en 2 líneas? Nivel formal.
   ```
3. **Diferenciar alcance vs límite:**
   ```
   Explícame la diferencia entre "alcance", "limitación" y "límite" en un documento de ingeniería de software.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 7.1 — Alcances y límites.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-8-1"></a>
## Tarea 8.1 — Planificación (tabla)

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 20/07 · **Fecha fin:** 23/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tabla Actividad · Responsable · Fecha inicio · Fecha fin con las 19 actividades del Avance 1 **más 5 actividades genéricas de las fases 2 y 3** (aclaración del ingeniero 27/07: la planificación debe cubrir todo el proyecto hasta la entrega final; para fases futuras se aceptan tareas genéricas).

### 📍 Dónde va tu trabajo

Word (Teams):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Planificación"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word → sección "Planificación".
2. Inserta tabla de 4 columnas × 25 filas (encabezado + 19 actividades del Avance 1 + 5 genéricas de fases 2 y 3).
3. Copia los datos del bloque siguiente.

### 💻 Contenido a copiar

| Actividad | Responsable | Fecha inicio | Fecha fin |
|---|---|---|---|
| Redacción portada, objetivos y equipo Scrum | LOPEZ | 15/07 | 20/07 |
| Definición de roles y HU del sistema | SANCHEZ + LOPEZ | 17/07 | 22/07 |
| Alcances / límites | LOPEZ | 16/07 | 18/07 |
| Planificación y cronograma Gantt | LOPEZ | 20/07 | 25/07 |
| Ejecución de `schema.sql` y `seed.sql` en Neon | MERINO VENTURA | 22/07 | 24/07 |
| Setup del proyecto Spring Boot | ORELLANA + VENTURA | 23/07 | 27/07 |
| Configuración de conexión a Neon (`application.yml`) | ORELLANA | 27/07 | 28/07 |
| Entradas/salidas por HU + Declaración de entidades | ORELLANA + SANCHEZ + MERINO | 22/07 | 28/07 |
| Estructura de paquetes + entidades JPA | ORELLANA + VENTURA | 28/07 | 01/08 |
| Init frontend Vite + React + TS + Tailwind + Zustand | DIAZ + MELGAR | 25/07 | 30/07 |
| Repositories JPA | ORELLANA | 01/08 | 03/08 |
| DTOs + AuthService | ORELLANA | 02/08 | 04/08 |
| Spring Security + JWT + endpoint `POST /api/auth/login` | ORELLANA + VENTURA | 04/08 | 06/08 |
| Pantalla LoginPage.tsx + integración con backend | DIAZ + MELGAR | 04/08 | 07/08 |
| Casos de prueba QA (login OK, login fail, bloqueo) | FUENTES + VASQUEZ | 05/08 | 07/08 |
| Bibliografía APA | SANCHEZ | 04/08 | 06/08 |
| Conclusiones individuales | Todos | 03/08 | 07/08 |
| Consolidación final + generación de PDF | LOPEZ | 08/08 | 09/08 |
| Entrega | LOPEZ | 10/08 | 10/08 |
| Desarrollo Fase 2 — módulo de citas + blindaje de seguridad | Todos | 11/08 | 20/09 |
| Entrega Avance 2 | LOPEZ | 21/09 | 26/09 |
| Desarrollo Fase 3 — triaje, expediente, recetas, reportes e inventario | Todos | 21/09 | 18/10 |
| Deploy, demo y documento final | Todos | 19/10 | 25/10 |
| Entrega final y defensa | Todos | 26/10 | 31/10 |

> Detalle de las fases 2 y 3: [PLAN_FASES_2_3.md](PLAN_FASES_2_3.md).

### ✅ Verificaciones

- [ ] 24 actividades listadas (19 del Avance 1 + 5 genéricas de fases 2 y 3).
- [ ] Todas con responsable y fechas.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Estimar duración:**
   ```
   Actúa como PM. Revisa esta tabla de actividades y dame tu opinión: ¿alguna actividad tiene una duración irreal para 2 personas trabajando part-time? [pegar tabla].
   ```
2. **Formato fecha en Word:**
   ```
   ¿Cómo formateo automáticamente celdas de fecha en una tabla de Word Online al estilo dd/mm?
   ```
3. **Ordenar tabla:**
   ```
   ¿Cómo ordeno una tabla de Word Online por la columna "Fecha inicio" (formato dd/mm)?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 8.1 — Planificación (24 actividades, proyecto completo).`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-9-1"></a>
## Tarea 9.1 — Cronograma Gantt

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 21/07 · **Fecha fin:** 25/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Imagen del diagrama de Gantt **del proyecto completo** pegada en el Word (aclaración del ingeniero 27/07: el Gantt debe cubrir hasta la entrega final, con tareas genéricas para las fases futuras). El Gantt oficial vive en Microsoft Planner; para el documento se genera una imagen a partir del código Mermaid.

### 📍 Dónde va tu trabajo

Word (Teams):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Cronograma Gantt"**.

### 🛠️ Herramientas
- Navegador (GitHub renderiza el diagrama solo — no se instala ni se paga nada).
- Microsoft Word Online.
- Snipping Tool o "Recortes" de Windows (tecla `Win + Shift + S`).

### 📋 Paso a paso

1. Abre en el navegador el archivo del Gantt en GitHub (el diagrama se dibuja automáticamente al cargar la página):
   https://github.com/NapoSV/medisuite/blob/main/docs/diagramas/gantt_proyecto_completo.md
2. Espera a que el diagrama aparezca renderizado (barras de colores por sección).
3. Presiona `Win + Shift + S` y selecciona el área del diagrama completo (las 3 fases y los 3 hitos de entrega). La captura queda en el portapapeles.
4. Abre el Word → sección "Cronograma Gantt".
5. Pega la captura con `Ctrl + V` (o guárdala primero como PNG desde la app Recortes y usa **Insertar → Imagen → Este dispositivo**).
6. Debajo de la imagen agrega la nota: *"El diagrama de Gantt oficial vive en Microsoft Planner del equipo. Esta imagen es la versión de respaldo académico."*

> **Si necesitas modificar el Gantt:** edita el bloque ```` ```mermaid ```` dentro de `docs/diagramas/gantt_proyecto_completo.md`, haz commit, y GitHub re-dibuja el diagrama actualizado al refrescar la página. (El detalle interno del Avance 1 sigue en `docs/diagramas/gantt_avance1.md`.)

### 💻 Contenido a copiar (código Mermaid del detalle Avance 1 — la versión del proyecto completo está en `docs/diagramas/gantt_proyecto_completo.md`, que es la que va en el Word)

```
gantt
    title Avance 1 · MediSuite · 15/07 – 10/08/2026
    dateFormat  YYYY-MM-DD
    axisFormat  %d/%m
    section Documento
    Portada, objetivos, equipo Scrum            :doc1, 2026-07-15, 6d
    Roles, HU, alcances, planificación          :doc2, 2026-07-17, 9d
    Cronograma Gantt                            :doc3, 2026-07-21, 5d
    Entradas/Salidas + Entidades                :doc4, 2026-07-22, 7d
    Conclusiones + Bibliografía                 :doc5, 2026-08-03, 5d
    Consolidación y PDF final                   :doc6, 2026-08-08, 2d
    Entrega                                     :milestone, entrega, 2026-08-10, 1d
    section Base de datos
    Ejecutar schema.sql y seed.sql en Neon      :db1, 2026-07-22, 3d
    section Backend
    Init Spring Boot + config Neon              :be1, 2026-07-23, 6d
    Estructura de paquetes + Entidades JPA      :be2, 2026-07-28, 5d
    Repositories JPA                            :be3, 2026-08-01, 3d
    DTOs + AuthService                          :be4, 2026-08-02, 3d
    Spring Security + JWT + /api/auth/login     :be5, 2026-08-04, 3d
    section Frontend
    Init Vite + React + Tailwind + Zustand      :fe1, 2026-07-25, 6d
    Pantalla LoginPage.tsx                      :fe2, 2026-08-04, 4d
    section QA
    Casos de prueba de login                    :qa1, 2026-08-05, 3d
```

### ✅ Verificaciones

- [ ] Imagen del Gantt (captura desde GitHub) visible en el Word.
- [ ] Se distinguen las 5 secciones (Documento, BD, Backend, Frontend, QA).
- [ ] El hito "Entrega" aparece el 10/08.
- [ ] Nota bajo la imagen agregada.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Modificar Mermaid:**
   ```
   Tengo este código Mermaid de Gantt: [pegar código]. Necesito agregar una tarea nueva "Revisión final" del 08/08 al 09/08 en la sección Documento. Dame el bloque exacto.
   ```
2. **Solucionar error render:**
   ```
   GitHub no renderiza mi bloque mermaid en un archivo .md — muestra el código como texto o dice "Unable to render rich display". El código es: [pegar]. ¿Qué está mal?
   ```
3. **Alternativa sin Mermaid:**
   ```
   Necesito un diagrama de Gantt para presentación académica sin usar herramientas de pago ni crear cuentas. ¿Qué alternativa online gratis me recomiendas? Ordénalas por facilidad de uso.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 9.1 — Cronograma Gantt (imagen insertada).`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-10-1"></a>
## Tarea 10.1 — Entradas / Salidas por HU

**Responsable(s):** Bayron Orellana (Backend) + Nicole Sánchez (BA).
**Fecha inicio:** 23/07 · **Fecha fin:** 28/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tabla con las entradas (inputs) y salidas (outputs) de cada HU listada en la Tarea 6.1.

### 📍 Dónde va tu trabajo

Word (Teams):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Entradas y salidas por HU"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word → sección "Entradas y salidas por HU".
2. Inserta tabla de 3 columnas × 7 filas (encabezado + 6 HU).
3. Pega los datos del bloque siguiente.

### 💻 Contenido a copiar

| HU | Entradas | Salidas |
|---|---|---|
| **HU-007** (Login) | `tenantSlug`, `email`, `password`. | `accessToken` (JWT), `tokenType`, `expiresIn`, datos básicos del usuario (id, nombre, rol, tenantId). En error: 401 con mensaje. |
| **HU-001** (Registro paciente) | Nombre, apellido, CIF, fecha de nacimiento, teléfono, dirección, correo. | Confirmación de registro, expediente creado, visualización con datos y citas previas. |
| **HU-008** (Alta usuario) | Nombre, email, rol; para médicos también especialidad y nº licencia. | Usuario creado con password temporal enviado por email (simulado en MVP). |
| **HU-003** (Solicitud de cita) | ID paciente, ID médico, fecha, hora, motivo. | Cita confirmada, código de reserva (COD-XXXX), notificación a paciente y médico. |
| **HU-004** (Triaje) | ID paciente, peso, talla, presión, temperatura, FC, síntomas, prioridad. | Signos vitales registrados en expediente; estado de cita actualizado a `WAITING`. |
| **HU-002** (Receta) | ID paciente, ID médico, fecha, medicamentos, dosis, duración, indicaciones. | Receta almacenada en expediente, PDF descargable, notificación al paciente. |

### ✅ Verificaciones

- [ ] 6 filas de HU en la tabla.
- [ ] HU-007 primera (es la implementada en el Avance 1).
- [ ] Cada fila tiene entradas y salidas concretas.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Refinar entradas:**
   ```
   Actúa como BA. Revisa las entradas de esta HU y dime si falta algún campo obligatorio: "[pegar fila]".
   ```
2. **Convertir a diagrama:**
   ```
   ¿Cómo represento gráficamente entradas y salidas de una HU? Dame 3 formatos visuales usados en ingeniería de software.
   ```
3. **Validar datos de salida:**
   ```
   ¿Es correcto que la salida de un endpoint de login incluya el rol del usuario? ¿O es un anti-patrón de seguridad? Explícame.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 10.1 — Entradas/Salidas por HU.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-11-1"></a>
## Tarea 11.1 — Declaración de Entidades

**Responsable(s):** Alejandro Merino Ventura (BD).
**Fecha inicio:** 22/07 · **Fecha fin:** 28/07.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Tabla con las 10 entidades principales del sistema (Usuario, Paciente, Médico, Enfermera, Administrador, Recepcionista, Cita, Expediente, SignoVital, Receta) con descripción y atributos.

### 📍 Dónde va tu trabajo

Word (Teams):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Declaración de entidades"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word → sección "Declaración de entidades".
2. Inserta tabla de 3 columnas × 11 filas (encabezado + 10 entidades).
3. Pega los datos del bloque siguiente.
4. Agrega la nota final que menciona el esquema completo de 17 tablas.

### 💻 Contenido a copiar

| Entidad | Descripción | Atributos principales |
|---|---|---|
| **Usuario (User)** | Persona que accede al sistema (base para todos los roles). | `idUsuario`, `tenantId`, `firstName`, `lastName`, `cif`, `email`, `passwordHash`, `role`, `active`, `createdAt`, `updatedAt`. |
| **Paciente (Patient)** | Persona que recibe atención médica. | `idPaciente`, `tenantId`, `userId` (FK→User), `birthDate`, `phone`, `address`, `emergencyContact`, `bloodType`, `allergies`. |
| **Médico (Doctor)** | Profesional de salud. | `idMedico`, `tenantId`, `userId` (FK→User), `specialtyId` (FK→Specialty), `licenseNumber`, `availableSchedule` (JSON). |
| **Enfermera (Nurse)** | Profesional que realiza triaje. | `idEnfermera`, `tenantId`, `userId` (FK→User), `shift`, `assignedArea`. |
| **Administrador (Administrator)** | Usuario con permisos de gestión. | `idAdmin`, `tenantId`, `userId` (FK→User), `permissionLevel`. |
| **Recepcionista (Receptionist)** | Personal de recepción. | `idRecepcionista`, `tenantId`, `userId` (FK→User), `shift`, `assignedOffice`. |
| **Cita (Appointment)** | Registro de consulta programada. | `idCita`, `tenantId`, `patientId` (FK), `doctorId` (FK), `scheduledAt`, `status`, `reason`, `office`, `reservationCode`. |
| **Expediente (MedicalRecord)** | Historial clínico del paciente. | `idExpediente`, `tenantId`, `patientId` (FK), `createdOn`, `generalNotes`. |
| **SignoVital (VitalSign)** | Registro de triaje. | `idSigno`, `tenantId`, `medicalRecordId` (FK), `recordedAt`, `weightKg`, `heightCm`, `bloodPressure`, `temperatureC`, `heartRate`, `symptoms`, `priority`. |
| **Receta (Prescription)** | Prescripción médica. | `idReceta`, `tenantId`, `medicalRecordId` (FK), `doctorId` (FK), `issuedOn`, `medications`, `dosage`, `duration`, `instructions`. |

**Nota final:** El esquema completo (17 tablas, incluyendo `tenants`, `specialties`, `products`, `suppliers`, `purchase_orders`, `purchase_order_items`, `physical_assets`) está documentado en el archivo `database/schema.sql` del repositorio.

### ✅ Verificaciones

- [ ] 10 entidades en la tabla.
- [ ] Cada una tiene descripción + atributos principales.
- [ ] Los FK (foreign keys) están indicados con "(FK→Entidad)".
- [ ] Nota final agregada.
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Explicar FK:**
   ```
   Explícame en 2 líneas qué es un Foreign Key en BD y cómo se relaciona con las tablas. Dame un ejemplo con Paciente y Usuario.
   ```
2. **Validar atributos:**
   ```
   Revisa esta entidad y dime si faltan atributos comunes o si sobra alguno: "[pegar fila]".
   ```
3. **Diseñar entidad extra:**
   ```
   Actúa como diseñador de BD. Necesito una entidad nueva "Diagnóstico" que dependa de "Cita" y "Médico". Dame los atributos principales en formato similar a: `idDiagnostico, tenantId, ...`.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 11.1 — Declaración de 10 entidades.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-1"></a>
## Tarea 12.1 — Ejecutar `schema.sql` en Neon

**Responsable(s):** Alejandro Merino Ventura (BD).
**Fecha inicio:** 22/07 · **Fecha fin:** 24/07.
**Bucket Planner:** BD.

### 🎯 Qué vas a entregar
La BD `clinica_dev` en Neon con las 17 tablas creadas y los datos de ejemplo cargados.

### 📍 Dónde va tu trabajo

Rama Git: `chore/db-apply-schema`
Archivos a crear/modificar:
- (Opcional) `database/README.md` con nota "schema aplicado el DD/MM por Merino".

Base de datos: **Neon Postgres** (BD compartida del equipo).
- Host: el host *pooler* de Neon — lo encuentras en tu archivo `.env` (variable `DB_HOST`)
- Puerto: `5432`
- BD: `clinica_dev`
- Usuario: `clinica_app`
- Password: la encuentras en el archivo `.env` de la raíz del repo (no versionado). Si no tienes el `.env`, pídelo en el canal de Teams **📋 Avances del Proyecto**.
- SSL Mode: `require`

### 🛠️ Herramientas que vas a usar

- DBeaver Community ([§0.6](#seccion-0-herramientas)).
- Git for Windows / GitHub Desktop ([§0.2](#seccion-0-herramientas)).

### 📋 Paso a paso

1. Actualiza tu repo local ([§2](#seccion-2-actualizar)).
2. Abre DBeaver.
3. Menú **Database → New Database Connection → PostgreSQL**. Rellena:
   - **Host:** el del archivo `.env` (variable `DB_HOST`)
   - **Port:** `5432`
   - **Database:** `clinica_dev`
   - **Username:** `clinica_app`
   - **Password:** la del archivo `.env` (variable `DB_PASSWORD`)
4. Pestaña **SSL** → marca **Use SSL** → SSL Mode: **require**.
5. Click **Test Connection...** Si sale ok, click **Finish**.
6. En el panel izquierdo, expande `clinica_dev → Schemas → public`.
7. Verifica la conexión: menú **SQL Editor → New SQL Script**, escribe y ejecuta:
   ```sql
   SELECT 1;
   ```
   Debe responder `1`.
8. En el Explorer de Windows, abre `c:\Users\{tu-usuario}\medisuite\database\schema.sql` y copia todo su contenido.
9. Pega el contenido en el SQL Editor de DBeaver. Presiona **Alt + X** (ejecutar script completo).
10. Espera que termine. Debe salir "Script executed successfully".
11. Abre `database/seed.sql`, copia todo, pégalo en un nuevo SQL Editor y ejecuta con **Alt + X**.
12. Crea tu rama:
    - **Opción Web (GitHub Desktop):** Current Branch → New Branch → `chore/db-apply-schema` (base: `develop`) → Publish branch.
    - **Opción Consola (PowerShell):**
      ```powershell
      cd C:\Users\$env:USERNAME\medisuite
      git checkout develop
      git pull origin develop
      git checkout -b chore/db-apply-schema
      git push -u origin chore/db-apply-schema
      ```

### 💻 Contenido a copiar (validaciones a ejecutar en DBeaver)

```sql
-- Validación 1: contar tablas creadas
SELECT count(*) AS total_tables
FROM information_schema.tables
WHERE table_schema = 'public';
-- Esperado: 17

-- Validación 2: verificar tenants
SELECT id, slug, commercial_name, status FROM tenants;
-- Esperado: al menos 2 filas.

-- Validación 3: verificar usuarios seed
SELECT id, email, role FROM users LIMIT 5;
-- Esperado: al menos 1 usuario admin@demo.sv
```

### ✅ Verificaciones ANTES de dar por terminada la tarea

- [ ] `SELECT 1;` responde `1`.
- [ ] Total de tablas = **17**.
- [ ] `SELECT count(*) FROM tenants;` retorna **≥ 2**.
- [ ] `SELECT count(*) FROM users;` retorna **≥ 1**.
- [ ] DBeaver no muestra errores en rojo en el log inferior.

Si algo falla: NO hagas commit. Ve a los prompts de IA.

### 🚀 Cómo commit + push

Si actualizaste `database/README.md`:

- **Opción Web:** selecciona solo `database/README.md` → Summary: `chore(db): aplicar schema.sql y seed.sql en Neon` → Commit → Push origin.
- **Opción Consola:**
  ```powershell
  git add database/README.md
  git commit -m "chore(db): aplicar schema.sql y seed.sql en Neon"
  git push origin chore/db-apply-schema
  ```

### 🌐 Cómo abrir el Pull Request

Sigue [§5](#seccion-5-pull-request) con base `develop` y compare `chore/db-apply-schema`.

### 🤖 Prompts de IA útiles

1. **Error de conexión SSL:**
   ```
   Actúa como DBA de PostgreSQL. Al conectar DBeaver a Neon me sale este error: "[pegar error]". La conexión es a un host neon.tech con SSL require. ¿Qué debo configurar? Paso a paso en DBeaver.
   ```
2. **Error al ejecutar schema.sql:**
   ```
   Estoy ejecutando un schema.sql de PostgreSQL en DBeaver contra Neon. Me sale este error en la línea X: [pegar error]. ¿Cuál es la causa y cómo lo arreglo?
   ```
3. **Rollback:**
   ```
   ¿Cómo borro todas las tablas del schema `public` en PostgreSQL para volver a ejecutar el schema.sql desde cero? Dame el comando exacto.
   ```

### 🎬 Cierre

1. ✅ Verificaciones marcadas.
2. PR abierto (si aplica).
3. Teams: `✅ Terminé Tarea 12.1 — schema.sql y seed.sql aplicados a Neon. 17 tablas confirmadas.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-2"></a>
## Tarea 12.2 — Inicializar Spring Boot

**Responsable(s):** Bayron Orellana (Backend).
**Fecha inicio:** 23/07 · **Fecha fin:** 27/07.
**Bucket Planner:** Backend.

### 🎯 Qué vas a entregar
Proyecto Spring Boot 3.3 con Java 21 generado dentro de `backend/`, compilando sin errores.

### 📍 Dónde va tu trabajo

Rama Git: `chore/spring-boot-init`
Archivos a crear/modificar:
- `backend/pom.xml`
- `backend/mvnw`, `backend/mvnw.cmd`, `backend/.mvn/`
- `backend/src/`
- `backend/.gitignore`

### 🛠️ Herramientas

- Navegador para https://start.spring.io
- Java 21 ([§0.4](#seccion-0-herramientas)).
- IntelliJ IDEA ([§0.8](#seccion-0-herramientas)) o VS Code + Extension Pack for Java.
- Git ([§0.2](#seccion-0-herramientas)).

### 📋 Paso a paso

1. Actualiza el repo ([§2](#seccion-2-actualizar)).
2. Crea tu rama ([§3](#seccion-3-crear-rama)) con nombre exacto **`chore/spring-boot-init`** basada en `develop`.
3. Abre https://start.spring.io
4. Configura:
   - **Project:** Maven
   - **Language:** Java
   - **Spring Boot:** 3.3.4
   - **Group:** `com.sv.grupo`
   - **Artifact:** `medisuite`
   - **Name:** `medisuite`
   - **Description:** `MediSuite backend`
   - **Package name:** `com.sv.grupo.hospital.citas`
   - **Packaging:** Jar
   - **Java:** 21
5. En **Dependencies**, agrega una a una:
   - Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver, Validation, Lombok, Spring Boot DevTools, Flyway Migration, Spring Boot Actuator
6. Click **GENERATE**. Descarga `medisuite.zip`.
7. Descomprime dentro de `c:\Users\{tu-usuario}\medisuite\backend\` de forma que `backend\pom.xml` exista directamente.
8. Abre `backend\pom.xml` en VS Code o IntelliJ.
9. Ubica el bloque `<dependencies>...</dependencies>` y agrega las dependencias del bloque siguiente **antes del cierre** `</dependencies>`.
10. Guarda.
11. Compila:
    ```powershell
    cd C:\Users\$env:USERNAME\medisuite\backend
    .\mvnw.cmd clean install -DskipTests
    ```
12. Debe salir `BUILD SUCCESS`.

### 💻 Contenido a copiar

**Archivo:** `backend/pom.xml` (bloque a agregar dentro de `<dependencies>`)

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] Existe `backend/pom.xml`.
- [ ] `.\mvnw.cmd --version` funciona (Maven + Java 21).
- [ ] `.\mvnw.cmd clean install -DskipTests` → `BUILD SUCCESS`.
- [ ] `.\mvnw.cmd spring-boot:run` arranca y muestra `Started MedisuiteApplication`.

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona `backend/pom.xml`, `backend/mvnw`, `backend/mvnw.cmd`, `backend/.mvn`, `backend/src`, `backend/.gitignore`. Summary: `chore(backend): inicializar proyecto Spring Boot 3.3 con Java 21`. Commit → Push.
- **Opción Consola:**
  ```powershell
  cd C:\Users\$env:USERNAME\medisuite
  git add backend/pom.xml backend/mvnw backend/mvnw.cmd backend/.mvn backend/src backend/.gitignore
  git commit -m "chore(backend): inicializar proyecto Spring Boot 3.3 con Java 21"
  git push origin chore/spring-boot-init
  ```

### 🌐 Cómo abrir el Pull Request

Sigue [§5](#seccion-5-pull-request). Base `develop`, compare `chore/spring-boot-init`.

### 🤖 Prompts de IA útiles

1. **Error de compilación:**
   ```
   Actúa como mentor Spring Boot. Al ejecutar `mvnw clean install` en Spring Boot 3.3 con Java 21 me sale: [pegar]. Mi pom.xml es: [pegar]. ¿Qué línea corrijo?
   ```
2. **Java version mismatch:**
   ```
   Tengo JDK 21 pero Maven dice "invalid target release: 21". ¿Cómo verifico y configuro JAVA_HOME en Windows 11 PowerShell? Paso a paso.
   ```
3. **Descomprimir en la carpeta correcta:**
   ```
   Descargué medisuite.zip. Al descomprimir queda `backend/medisuite/pom.xml`, pero quiero `backend/pom.xml`. ¿Cómo lo muevo sin perder archivos?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR abierto.
3. Teams: `✅ Terminé Tarea 12.2 — Spring Boot inicializado. BUILD SUCCESS. PR: [URL]`.
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-3"></a>
## Tarea 12.3 — Configurar Neon (application.yml)

**Responsable(s):** Bayron Orellana (Backend).
**Fecha inicio:** 27/07 · **Fecha fin:** 28/07.
**Bucket Planner:** Backend.

### 🎯 Qué vas a entregar
`application.yml` configurado para conectar a Neon usando variables de entorno; `GET /actuator/health` retorna `db: UP`.

### 📍 Dónde va tu trabajo

Rama Git: `chore/spring-boot-init` (misma rama que la 12.2).
Archivos a crear/modificar:
- `backend/src/main/resources/application.yml`

### 🛠️ Herramientas
- IntelliJ IDEA o VS Code.
- PowerShell.
- Postman (probar `/actuator/health`).

### 📋 Paso a paso

1. Ubícate en la rama:
   ```powershell
   git checkout chore/spring-boot-init
   git pull origin chore/spring-boot-init
   ```
2. Elimina `backend/src/main/resources/application.properties` (generado por Spring Initializr).
3. Crea `backend/src/main/resources/application.yml` con el contenido del bloque de abajo.
4. Verifica que exista `.env` en la raíz (si no, cópialo desde `.env.example`).
5. Configura variables de entorno en tu IDE:
   - **IntelliJ:** Run → Edit Configurations → MedisuiteApplication → Environment Variables → pega la línea siguiente, **reemplazando cada `<...>` con el valor real de tu archivo `.env`**:
     ```
     APP_ENV=development;DB_HOST=<DB_HOST del .env>;DB_PORT=5432;DB_NAME=clinica_dev;DB_USERNAME=clinica_app;DB_PASSWORD=<DB_PASSWORD del .env>;DB_SSLMODE=require;JWT_SECRET=<JWT_SECRET del .env>;JWT_EXPIRATION_MS=900000;APP_PORT=8080
     ```
   - **VS Code:** crea `.vscode/launch.json` con la sección `env` (ver prompt IA #3).
6. Ejecuta:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite\backend
   .\mvnw.cmd spring-boot:run
   ```
7. Debe aparecer `HikariPool-1 - Start completed.`
8. Postman → GET `http://localhost:8080/actuator/health` → Send.
9. Respuesta esperada: `{"status":"UP","components":{"db":{"status":"UP"}}}`.

### 💻 Contenido a copiar

**Archivo:** `backend/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: medisuite
  profiles:
    active: ${APP_ENV:development}
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=${DB_SSLMODE:require}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    open-in-view: false
  flyway:
    enabled: false

server:
  port: ${APP_PORT:8080}

management:
  endpoints:
    web:
      exposure:
        include: health,info

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration-ms: ${JWT_EXPIRATION_MS:900000}
    refresh-expiration-ms: ${JWT_REFRESH_EXPIRATION_MS:604800000}
  default-tenant-id: ${DEFAULT_TENANT_ID:1}

logging:
  level:
    org.springframework.security: INFO
    org.hibernate.SQL: DEBUG
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] `.\mvnw.cmd spring-boot:run` arranca sin errores.
- [ ] Log muestra `HikariPool-1 - Start completed`.
- [ ] Log muestra `Started MedisuiteApplication in X seconds`.
- [ ] GET `http://localhost:8080/actuator/health` responde `{"status":"UP",...}`.
- [ ] JSON incluye `"db":{"status":"UP"}`.
- [ ] `.env` NO está en Git (`git status` no debe listarlo).

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** Summary: `chore(backend): configurar conexión a Neon vía variables de entorno`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add backend/src/main/resources/application.yml
  git commit -m "chore(backend): configurar conexión a Neon vía variables de entorno"
  git push origin chore/spring-boot-init
  ```

### 🌐 Cómo abrir el Pull Request

Si el PR ya está abierto (de la 12.2), el push se agrega automáticamente.

### 🤖 Prompts de IA útiles

1. **Error conexión Neon:**
   ```
   Actúa como mentor Spring Boot. Al arrancar me sale "Could not create connection to database server". Mi application.yml es: [pegar]. ¿Qué reviso?
   ```
2. **JPA validation error:**
   ```
   Al arrancar me sale "Schema-validation: missing table [users]" pero ya ejecuté schema.sql en Neon. ¿Por qué falla?
   ```
3. **Configurar env en VS Code:**
   ```
   Necesito configurar variables de entorno para arrancar Spring Boot desde VS Code. ¿Cómo edito launch.json? Ejemplo con placeholders.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR actualizado.
3. Teams: `✅ Terminé Tarea 12.3 — application.yml conectando a Neon. /actuator/health = UP.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-4"></a>
## Tarea 12.4 — Estructura de paquetes + Entidades

**Responsable(s):** Bayron Orellana (Backend) + Carlos Ventura (Architect).
**Fecha inicio:** 28/07 · **Fecha fin:** 01/08.
**Bucket Planner:** Backend.

### 🎯 Qué vas a entregar
Estructura de paquetes Java + entidades JPA `Tenant`, `User`, `Patient`, `Specialty`, `Doctor` que Hibernate pueda validar contra el esquema de Neon.

### 📍 Dónde va tu trabajo

Rama Git: `feature/HU-007-entities`
Archivos a crear:
- `backend/src/main/java/com/sv/grupo/hospital/citas/model/tenant/Tenant.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/model/users/User.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/model/users/Patient.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/model/users/Doctor.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/model/clinical/Specialty.java`

### 🛠️ Herramientas
- IntelliJ IDEA o VS Code + Extension Pack for Java.
- Git / GitHub Desktop.

### 📋 Paso a paso

1. Actualiza el repo ([§2](#seccion-2-actualizar)).
2. Crea la rama:
   - **GitHub Desktop:** Current Branch → New Branch → `feature/HU-007-entities` (base: `develop`) → Publish.
   - **Consola:**
     ```powershell
     git checkout develop
     git pull origin develop
     git checkout -b feature/HU-007-entities
     git push -u origin feature/HU-007-entities
     ```
3. Dentro de `backend/src/main/java/com/sv/grupo/hospital/citas/`, crea los subpaquetes (carpetas):
   - `model/tenant`
   - `model/users`
   - `model/clinical`
4. Crea los 5 archivos Java del bloque "Contenido a copiar" con el código exacto.
5. Compila:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite\backend
   .\mvnw.cmd compile
   ```
6. Arranca la app:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
7. En el log **no debe haber** errores de tipo `Schema-validation: missing table ...`.

### 💻 Contenido a copiar

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/model/tenant/Tenant.java`

```java
package com.sv.grupo.hospital.citas.model.tenant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tenants")
@Getter
@Setter
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "commercial_name", nullable = false, length = 150)
    private String commercialName;

    @Column(name = "legal_name", length = 150)
    private String legalName;

    @Column(name = "tax_id", length = 30)
    private String taxId;

    @Column(length = 60)
    private String country;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Column(name = "default_language", nullable = false, length = 5)
    private String defaultLanguage;

    @Column(nullable = false, length = 20)
    private String plan;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/model/users/User.java`

```java
package com.sv.grupo.hospital.citas.model.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"tenant_id", "cif"}),
           @UniqueConstraint(columnNames = {"tenant_id", "email"})
       })
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String cif;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/model/users/Patient.java`

```java
package com.sv.grupo.hospital.citas.model.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/model/clinical/Specialty.java`

```java
package com.sv.grupo.hospital.citas.model.clinical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "specialties",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "name"}))
@Getter
@Setter
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/model/users/Doctor.java`

```java
package com.sv.grupo.hospital.citas.model.users;

import com.sv.grupo.hospital.citas.model.clinical.Specialty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "doctors",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "license_number"}))
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "license_number", nullable = false, length = 30)
    private String licenseNumber;

    @Column(name = "available_schedule", columnDefinition = "jsonb")
    private String availableSchedule;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] Los 5 archivos `.java` existen en las rutas indicadas.
- [ ] `.\mvnw.cmd compile` → `BUILD SUCCESS`.
- [ ] `.\mvnw.cmd spring-boot:run` arranca sin errores de validación de esquema.
- [ ] En el log NO aparece `Schema-validation: missing table [...]`.

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona los 5 archivos (o la carpeta `backend/src/main/java/com/sv/grupo/hospital/citas/model/`). Summary: `feat(auth): agregar entidades User, Patient, Doctor, Tenant, Specialty`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add backend/src/main/java/com/sv/grupo/hospital/citas/model
  git commit -m "feat(auth): agregar entidades User, Patient, Doctor, Tenant, Specialty"
  git push origin feature/HU-007-entities
  ```

### 🌐 Cómo abrir el Pull Request

Sigue [§5](#seccion-5-pull-request). Base `develop`, compare `feature/HU-007-entities`.

### 🤖 Prompts de IA útiles

1. **Error de validación de esquema:**
   ```
   Actúa como mentor Hibernate. Al arrancar Spring Boot me sale "Schema-validation: wrong column type encountered in column [tenant_id]; found [bigint (Types#BIGINT)], but expecting [integer (Types#INTEGER)]". Mi entidad es: [pegar]. ¿Qué anotación cambio?
   ```
2. **Cyclic dependency entre entidades:**
   ```
   Tengo Doctor con @OneToOne User y User no referencia Doctor. ¿Es correcto o rompe algo? Estoy usando JPA/Hibernate 6.
   ```
3. **@Table uniqueConstraints:**
   ```
   Explícame la diferencia entre @Column(unique=true) y @UniqueConstraint dentro de @Table en JPA. Cuándo usar cada uno.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR abierto.
3. Teams: `✅ Terminé Tarea 12.4 — 5 entidades JPA. Compila y valida el esquema. PR: [URL]`.
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-5"></a>
## Tarea 12.5 — Repositories JPA

**Responsable(s):** Bayron Orellana (Backend).
**Fecha inicio:** 01/08 · **Fecha fin:** 03/08.
**Bucket Planner:** Backend.

### 🎯 Qué vas a entregar
Interfaces `JpaRepository` para User, Tenant, Patient, Doctor y Specialty.

### 📍 Dónde va tu trabajo

Rama Git: `feature/HU-007-entities` (misma rama que la 12.4).
Archivos a crear:
- `backend/src/main/java/com/sv/grupo/hospital/citas/dao/UserRepository.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/dao/TenantRepository.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/dao/PatientRepository.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/dao/DoctorRepository.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/dao/SpecialtyRepository.java`

### 🛠️ Herramientas
- IntelliJ / VS Code.
- Git.

### 📋 Paso a paso

1. Ubícate en la rama:
   ```powershell
   git checkout feature/HU-007-entities
   git pull origin feature/HU-007-entities
   ```
2. Crea la carpeta `backend/src/main/java/com/sv/grupo/hospital/citas/dao/`.
3. Crea los 5 archivos con el código exacto del bloque siguiente.
4. Compila:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite\backend
   .\mvnw.cmd compile
   ```

### 💻 Contenido a copiar

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dao/UserRepository.java`

```java
package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndTenantId(String email, Long tenantId);
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dao/TenantRepository.java`

```java
package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findBySlug(String slug);
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dao/PatientRepository.java`

```java
package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dao/DoctorRepository.java`

```java
package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.users.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dao/SpecialtyRepository.java`

```java
package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.clinical.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] Los 5 archivos existen.
- [ ] `.\mvnw.cmd compile` → `BUILD SUCCESS`.
- [ ] `.\mvnw.cmd spring-boot:run` arranca sin errores de "No bean of type ... UserRepository".

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona la carpeta `backend/src/main/java/com/sv/grupo/hospital/citas/dao/`. Summary: `feat(auth): agregar repositories JPA (User, Tenant, Patient, Doctor, Specialty)`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add backend/src/main/java/com/sv/grupo/hospital/citas/dao
  git commit -m "feat(auth): agregar repositories JPA (User, Tenant, Patient, Doctor, Specialty)"
  git push origin feature/HU-007-entities
  ```

### 🌐 Cómo abrir el Pull Request

Si el PR de la 12.4 ya está abierto, el push se agrega. Si no, sigue [§5](#seccion-5-pull-request).

### 🤖 Prompts de IA útiles

1. **Error `No bean of type UserRepository`:**
   ```
   Al arrancar Spring Boot me sale "Consider defining a bean of type UserRepository". Mi interfaz está en `.../dao/UserRepository.java` con `extends JpaRepository<User, Long>`. ¿Por qué no lo detecta y cómo lo arreglo?
   ```
2. **Query method:**
   ```
   Explícame cómo Spring Data JPA construye la query a partir del nombre `findByEmailAndTenantId(String, Long)`. ¿Qué SQL genera?
   ```
3. **Test rápido de un repository:**
   ```
   Dame un test JUnit 5 mínimo para probar que `UserRepository.findByEmailAndTenantId("admin@demo.sv", 1L)` retorna Optional no vacío. Usa @DataJpaTest.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR actualizado.
3. Teams: `✅ Terminé Tarea 12.5 — 5 repositories JPA creados. Compila.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-6"></a>
## Tarea 12.6 — DTOs + AuthService

**Responsable(s):** Bayron Orellana (Backend).
**Fecha inicio:** 02/08 · **Fecha fin:** 04/08.
**Bucket Planner:** Backend.

### 🎯 Qué vas a entregar
DTOs `LoginRequest` y `LoginResponse`, excepciones globales y `AuthService` con lógica de login (incluye bloqueo por 5 intentos fallidos).

### 📍 Dónde va tu trabajo

Rama Git: `feature/HU-007-entities` (misma rama).
Archivos a crear:
- `backend/src/main/java/com/sv/grupo/hospital/citas/dto/auth/LoginRequest.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/dto/auth/LoginResponse.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/exception/BusinessException.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/service/AuthService.java`

### 🛠️ Herramientas
- IntelliJ / VS Code.
- Git.

### 📋 Paso a paso

1. Ubícate en la rama:
   ```powershell
   git checkout feature/HU-007-entities
   git pull origin feature/HU-007-entities
   ```
2. Crea las carpetas:
   - `backend/src/main/java/com/sv/grupo/hospital/citas/dto/auth/`
   - `backend/src/main/java/com/sv/grupo/hospital/citas/exception/`
   - `backend/src/main/java/com/sv/grupo/hospital/citas/service/`
3. Crea los 5 archivos con el código exacto.
4. Compila:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite\backend
   .\mvnw.cmd compile
   ```
5. **Nota:** `AuthService` importa `JwtTokenProvider` que se creará en la Tarea 12.7. Compila igual porque Spring lo inyecta en runtime, pero si tu IDE marca error en rojo por ahora, ignóralo — se resuelve al terminar la 12.7.

### 💻 Contenido a copiar

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dto/auth/LoginRequest.java`

```java
package com.sv.grupo.hospital.citas.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{auth.tenant.required}") String tenantSlug,
        @Email(message = "{auth.email.invalid}") @NotBlank String email,
        @NotBlank(message = "{auth.password.required}") String password
) {}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/dto/auth/LoginResponse.java`

```java
package com.sv.grupo.hospital.citas.dto.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserInfo user
) {
    public record UserInfo(Long id, String fullName, String role, Long tenantId) {}
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/exception/BusinessException.java`

```java
package com.sv.grupo.hospital.citas.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/exception/GlobalExceptionHandler.java`

```java
package com.sv.grupo.hospital.citas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "invalid_credentials", "message", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "business_rule", "message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(Map.of("error", "validation_failed", "fields", fieldErrors));
    }
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/service/AuthService.java`

```java
package com.sv.grupo.hospital.citas.service;

import com.sv.grupo.hospital.citas.dao.TenantRepository;
import com.sv.grupo.hospital.citas.dao.UserRepository;
import com.sv.grupo.hospital.citas.dto.auth.LoginRequest;
import com.sv.grupo.hospital.citas.dto.auth.LoginResponse;
import com.sv.grupo.hospital.citas.exception.BusinessException;
import com.sv.grupo.hospital.citas.model.tenant.Tenant;
import com.sv.grupo.hospital.citas.model.users.User;
import com.sv.grupo.hospital.citas.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Tenant tenant = tenantRepository.findBySlug(req.tenantSlug())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        User user = userRepository.findByEmailAndTenantId(req.email(), tenant.getId())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BusinessException("La cuenta está inactiva. Contacte al administrador.");
        }
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new BusinessException("Cuenta bloqueada temporalmente. Intente más tarde.");
        }

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            registerFailedAttempt(user);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole(), user.getTenantId());
        long expiresIn = jwtTokenProvider.getExpirationSeconds();
        String fullName = user.getFirstName() + " " + user.getLastName();

        return new LoginResponse(token, "Bearer", expiresIn,
                new LoginResponse.UserInfo(user.getId(), fullName, user.getRole(), user.getTenantId()));
    }

    private void registerFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCK_MINUTES));
        }
    }
}
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] Los 5 archivos existen.
- [ ] `.\mvnw.cmd compile` → `BUILD SUCCESS` (después de terminar la 12.7).
- [ ] Al terminar la 12.7, la app arranca sin errores.

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona las 3 carpetas nuevas (`dto`, `exception`, `service`). Summary: `feat(auth): agregar DTOs, AuthService y excepciones globales`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add backend/src/main/java/com/sv/grupo/hospital/citas/dto backend/src/main/java/com/sv/grupo/hospital/citas/service backend/src/main/java/com/sv/grupo/hospital/citas/exception
  git commit -m "feat(auth): agregar DTOs, AuthService y excepciones globales"
  git push origin feature/HU-007-entities
  ```

### 🌐 Cómo abrir el Pull Request

Push a la rama del PR existente. Si no, sigue [§5](#seccion-5-pull-request).

### 🤖 Prompts de IA útiles

1. **Explicar records en Java 21:**
   ```
   Explícame qué es un `record` en Java 21 y por qué se usa para DTOs. Compara con una clase Lombok @Data.
   ```
2. **Lógica de bloqueo:**
   ```
   Revisa mi método registerFailedAttempt(user) y dime si tiene alguna race condition en un entorno concurrente. Código: [pegar]. ¿Cómo lo hago thread-safe?
   ```
3. **Errores de compilación:**
   ```
   Al compilar me sale "cannot find symbol JwtTokenProvider". Está en el paquete `.../security/JwtTokenProvider.java` y lo importo así: `import com.sv.grupo.hospital.citas.security.JwtTokenProvider;`. ¿Qué reviso?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR actualizado.
3. Teams: `✅ Terminé Tarea 12.6 — DTOs, GlobalExceptionHandler y AuthService.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-7"></a>
## Tarea 12.7 — Spring Security + JWT + /api/auth/login

**Responsable(s):** Bayron Orellana (Backend) + Carlos Ventura (Architect).
**Fecha inicio:** 04/08 · **Fecha fin:** 06/08.
**Bucket Planner:** Backend.

### 🎯 Qué vas a entregar
Endpoint funcional `POST /api/auth/login` que retorna JWT, con Spring Security configurado (stateless, CORS al frontend, 5 intentos → bloqueo).

### 📍 Dónde va tu trabajo

Rama Git: `feature/HU-007-entities` (última tarea de esta rama).
Archivos a crear:
- `backend/src/main/java/com/sv/grupo/hospital/citas/security/JwtTokenProvider.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/config/SecurityConfig.java`
- `backend/src/main/java/com/sv/grupo/hospital/citas/controller/api/AuthController.java`

### 🛠️ Herramientas
- IntelliJ / VS Code.
- Postman.
- Git.

### 📋 Paso a paso

1. Ubícate en la rama:
   ```powershell
   git checkout feature/HU-007-entities
   git pull origin feature/HU-007-entities
   ```
2. Crea las carpetas:
   - `backend/src/main/java/com/sv/grupo/hospital/citas/security/`
   - `backend/src/main/java/com/sv/grupo/hospital/citas/config/`
   - `backend/src/main/java/com/sv/grupo/hospital/citas/controller/api/`
3. Crea los 3 archivos con el código exacto del bloque siguiente.
4. Compila y arranca:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite\backend
   .\mvnw.cmd clean install -DskipTests
   .\mvnw.cmd spring-boot:run
   ```
5. Prueba con Postman:
   - **Method:** POST
   - **URL:** `http://localhost:8080/api/auth/login`
   - **Headers:** `Content-Type: application/json`
   - **Body (raw JSON):**
     ```json
     { "tenantSlug": "demo", "email": "admin@demo.sv", "password": "admin123" }
     ```
   - **Send.**
6. Debe responder 200 OK con JSON `{"accessToken":"eyJ...","tokenType":"Bearer","expiresIn":900,"user":{...}}`.
7. Prueba credenciales inválidas (password mala): debe responder 401.

### 💻 Contenido a copiar

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/security/JwtTokenProvider.java`

```java
package com.sv.grupo.hospital.citas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String role, Long tenantId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("tenant_id", tenantId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/config/SecurityConfig.java`

```java
package com.sv.grupo.hospital.citas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login",
                                 "/actuator/health",
                                 "/v3/api-docs/**",
                                 "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

**Archivo:** `backend/src/main/java/com/sv/grupo/hospital/citas/controller/api/AuthController.java`

```java
package com.sv.grupo.hospital.citas.controller.api;

import com.sv.grupo.hospital.citas.dto.auth.LoginRequest;
import com.sv.grupo.hospital.citas.dto.auth.LoginResponse;
import com.sv.grupo.hospital.citas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

**Prueba manual (curl desde PowerShell):**

```powershell
curl.exe -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{\"tenantSlug\":\"demo\",\"email\":\"admin@demo.sv\",\"password\":\"admin123\"}'
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] `.\mvnw.cmd clean install -DskipTests` → `BUILD SUCCESS`.
- [ ] `.\mvnw.cmd spring-boot:run` arranca.
- [ ] POST `/api/auth/login` con credenciales seed válidas responde **200** con `accessToken`.
- [ ] POST con password mala responde **401**.
- [ ] Tras 5 intentos fallidos, el 6º responde **422** con mensaje "Cuenta bloqueada".
- [ ] Consulta SQL de verificación (DBeaver):
  ```sql
  SELECT failed_login_attempts, locked_until FROM users WHERE email='admin@demo.sv';
  ```
  Debe mostrar el contador incrementado y `locked_until` con timestamp.

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona las 3 carpetas nuevas. Summary: `feat(auth): agregar Spring Security, JWT y endpoint POST /api/auth/login`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add backend/src/main/java/com/sv/grupo/hospital/citas/security backend/src/main/java/com/sv/grupo/hospital/citas/config backend/src/main/java/com/sv/grupo/hospital/citas/controller
  git commit -m "feat(auth): agregar Spring Security, JWT y endpoint POST /api/auth/login"
  git push origin feature/HU-007-entities
  ```

### 🌐 Cómo abrir el Pull Request

**Este es el PR final del backend.** Sigue [§5](#seccion-5-pull-request) con base `develop` y compare `feature/HU-007-entities`. Título: `feat(auth): implementar módulo de autenticación HU-007 (entidades, repositories, service, JWT, endpoint)`. Reviewer: Héctor López (NapoSV).

### 🤖 Prompts de IA útiles

1. **Error 403 en Postman:**
   ```
   Al hacer POST a /api/auth/login en Postman me responde 403 Forbidden. Mi SecurityConfig es: [pegar]. ¿Por qué bloquea si la ruta está en permitAll?
   ```
2. **JWT invalid signature:**
   ```
   Al decodificar el JWT en jwt.io me dice "Invalid signature". Mi secret es de 40 chars ASCII. Mi JwtTokenProvider es: [pegar]. ¿Qué reviso?
   ```
3. **CORS bloquea al frontend:**
   ```
   Desde el frontend en http://localhost:5173 llamo al endpoint y me sale error CORS: "No 'Access-Control-Allow-Origin' header". Mi CorsConfigurationSource permite ese origin. ¿Qué más reviso?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR final del backend abierto.
3. Teams: `✅ Terminé Tarea 12.7 — Endpoint POST /api/auth/login funcionando con JWT y bloqueo. PR: [URL]`.
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-8"></a>
## Tarea 12.8 — Init frontend Vite + React + TS + Tailwind + Zustand

**Responsable(s):** Zair Diaz + William Melgar (Frontend).
**Fecha inicio:** 25/07 · **Fecha fin:** 30/07.
**Bucket Planner:** Frontend.

### 🎯 Qué vas a entregar
Proyecto frontend con Vite + React 18 + TypeScript + Tailwind CSS 3 + Zustand corriendo en `http://localhost:5173`.

### 📍 Dónde va tu trabajo

Rama Git: `chore/frontend-init`
Archivos a crear/modificar:
- `frontend/package.json`
- `frontend/vite.config.ts`
- `frontend/tsconfig.json`
- `frontend/tailwind.config.js`
- `frontend/postcss.config.js`
- `frontend/index.html`
- `frontend/src/`
- `frontend/.gitignore`

### 🛠️ Herramientas
- Node.js 20 ([§0.5](#seccion-0-herramientas)).
- VS Code + extensiones ESLint, Prettier, Tailwind CSS IntelliSense ([§0.7](#seccion-0-herramientas)).
- Git.

### 📋 Paso a paso

1. Actualiza el repo ([§2](#seccion-2-actualizar)).
2. Crea la rama:
   - **GitHub Desktop:** Current Branch → New Branch → `chore/frontend-init` (base: `develop`) → Publish.
   - **Consola:**
     ```powershell
     cd C:\Users\$env:USERNAME\medisuite
     git checkout develop
     git pull origin develop
     git checkout -b chore/frontend-init
     git push -u origin chore/frontend-init
     ```
3. Ejecuta desde `c:\Users\{tu-usuario}\medisuite\`:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite
   npm create vite@latest frontend -- --template react-ts
   cd frontend
   npm install
   npm install -D tailwindcss@^3 postcss autoprefixer
   npm install zustand react-router-dom axios react-hook-form zod @hookform/resolvers lucide-react
   npx tailwindcss init -p
   ```
4. Sobrescribe `frontend/tailwind.config.js` con el contenido del bloque siguiente.
5. Sobrescribe `frontend/src/index.css` con el contenido del bloque siguiente.
6. Verifica el arranque:
   ```powershell
   npm run dev
   ```
7. Debe imprimir `Local: http://localhost:5173/`. Abre esa URL y debes ver la pantalla por defecto de Vite/React.

### 💻 Contenido a copiar

**Archivo:** `frontend/tailwind.config.js`

```js
/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#0077BE',
          dark: '#005A8D',
          light: '#E6F4FB',
          pale: '#F2F9FC',
        },
        surface: '#FFFFFF',
        background: '#F8FAFC',
        border: '#E2E8F0',
        text: '#0F172A',
        muted: '#64748B',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        clay: '8px 8px 20px rgba(15,23,42,0.08), -6px -6px 16px rgba(255,255,255,0.9)',
        card: '0 8px 24px rgba(15,23,42,0.06), inset 1px 1px 2px rgba(255,255,255,0.8)',
        primaryBtn: '0 5px 12px rgba(0,119,190,0.22), inset 0 1px 1px rgba(255,255,255,0.25)',
      },
    },
  },
  plugins: [],
};
```

**Archivo:** `frontend/src/index.css` (reemplazar completo)

```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

@tailwind base;
@tailwind components;
@tailwind utilities;

body {
  font-family: 'Inter', sans-serif;
  background-color: #F8FAFC;
  color: #0F172A;
}
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] `frontend/package.json` existe.
- [ ] `npm run dev` levanta el servidor en `http://localhost:5173`.
- [ ] La pantalla por defecto de Vite se ve en el navegador.
- [ ] Agrega temporalmente `<div className="bg-blue-500 text-white p-4">Tailwind funciona</div>` en `App.tsx` y refresca: el div debe verse con fondo azul (esto confirma Tailwind).
- [ ] Elimina el div de prueba antes de commitear.
- [ ] `node_modules/` NO está en Git (Vite lo agrega automáticamente al `.gitignore`).

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona todos los archivos del panel Changes que estén dentro de `frontend/` (EXCEPTO `node_modules/`). Summary: `chore(frontend): inicializar React + Vite + TS + Tailwind + Zustand`. Commit → Push.
- **Opción Consola:**
  ```powershell
  cd C:\Users\$env:USERNAME\medisuite
  git add frontend/package.json frontend/package-lock.json frontend/vite.config.ts frontend/tsconfig.json frontend/tsconfig.node.json frontend/tailwind.config.js frontend/postcss.config.js frontend/index.html frontend/src frontend/.gitignore frontend/eslint.config.js
  git commit -m "chore(frontend): inicializar React + Vite + TS + Tailwind + Zustand"
  git push origin chore/frontend-init
  ```

### 🌐 Cómo abrir el Pull Request

Sigue [§5](#seccion-5-pull-request). Base `develop`, compare `chore/frontend-init`. Título: `chore(frontend): inicializar React + Vite + TS + Tailwind + Zustand`.

### 🤖 Prompts de IA útiles

1. **Error al ejecutar npm create:**
   ```
   Al ejecutar `npm create vite@latest frontend -- --template react-ts` me sale: [pegar error]. Estoy en Windows 11 PowerShell con Node 20. ¿Qué reviso?
   ```
2. **Tailwind no aplica estilos:**
   ```
   Tailwind CSS 3 no aplica estilos aunque instalé todo. Mi tailwind.config.js tiene `content: ['./index.html', './src/**/*.{ts,tsx}']`. Mi index.css tiene los `@tailwind`. ¿Qué reviso?
   ```
3. **Puerto 5173 ocupado:**
   ```
   Al ejecutar `npm run dev` me dice "Port 5173 is in use". ¿Cómo lo libero en Windows 11 o cómo hago que Vite use otro puerto?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR abierto.
3. Teams: `✅ Terminé Tarea 12.8 — Frontend Vite corriendo en :5173. Tailwind ok. PR: [URL]`.
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-9"></a>
## Tarea 12.9 — Pantalla LoginPage.tsx

**Responsable(s):** Zair Diaz + William Melgar (Frontend).
**Fecha inicio:** 04/08 · **Fecha fin:** 07/08.
**Bucket Planner:** Frontend.

### 🎯 Qué vas a entregar
Pantalla `/login` funcional consumiendo el endpoint `POST /api/auth/login` del backend, con Zustand para persistir el token y redirect a `/dashboard`.

### 📍 Dónde va tu trabajo

Rama Git: `feature/HU-007-login-ui`
Archivos a crear/modificar:
- `frontend/src/store/authStore.ts`
- `frontend/src/api/authApi.ts`
- `frontend/src/pages/LoginPage.tsx`
- `frontend/src/App.tsx` (reemplazar completo)
- `frontend/.env.local` (crear local, NO commitear)

### 🛠️ Herramientas
- VS Code.
- Navegador (Chrome/Edge).
- Postman (para verificar backend antes).
- Git.

### 📋 Paso a paso

1. Actualiza el repo ([§2](#seccion-2-actualizar)).
2. **IMPORTANTE:** verifica que el backend esté corriendo (`.\mvnw.cmd spring-boot:run` en la carpeta `backend/`) y que Postman responda 200 al endpoint (ver Tarea 12.7).
3. Crea tu rama:
   - **GitHub Desktop:** Current Branch → New Branch → `feature/HU-007-login-ui` (base: `develop`) → Publish.
   - **Consola:**
     ```powershell
     git checkout develop
     git pull origin develop
     git checkout -b feature/HU-007-login-ui
     git push -u origin feature/HU-007-login-ui
     ```
4. Crea las carpetas dentro de `frontend/src/`:
   - `store/`
   - `api/`
   - `pages/`
5. Crea los 4 archivos con el código del bloque siguiente.
6. Crea el archivo `frontend/.env.local` con el contenido del bloque (este archivo **NO se commitea**).
7. Levanta el frontend:
   ```powershell
   cd C:\Users\$env:USERNAME\medisuite\frontend
   npm run dev
   ```
8. Abre `http://localhost:5173/login`. Debe verse la pantalla de login con logo, campos y botón azul.
9. Con el backend corriendo, ingresa `tenantSlug=demo`, `email=admin@demo.sv`, `password=admin123` y click "Iniciar sesión". Debe redirigir a `/dashboard` y mostrar "Bienvenido, [nombre]".
10. Verifica en DevTools (F12) → Application → Local Storage: debe haber `accessToken` y `user`.

### 💻 Contenido a copiar

**Archivo:** `frontend/src/store/authStore.ts`

```typescript
import { create } from 'zustand';

type User = { id: number; fullName: string; role: string; tenantId: number };

type AuthState = {
  token: string | null;
  user: User | null;
  setSession: (token: string, user: User) => void;
  clear: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  token: localStorage.getItem('accessToken'),
  user: JSON.parse(localStorage.getItem('user') ?? 'null'),
  setSession: (token, user) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('user', JSON.stringify(user));
    set({ token, user });
  },
  clear: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
    set({ token: null, user: null });
  },
}));
```

**Archivo:** `frontend/src/api/authApi.ts`

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
});

export type LoginRequest = { tenantSlug: string; email: string; password: string };
export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: { id: number; fullName: string; role: string; tenantId: number };
};

export async function login(payload: LoginRequest): Promise<LoginResponse> {
  const res = await api.post<LoginResponse>('/api/auth/login', payload);
  return res.data;
}
```

**Archivo:** `frontend/src/pages/LoginPage.tsx`

```tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { Stethoscope, Building2, Mail, Lock, Loader2 } from 'lucide-react';
import { login, type LoginRequest } from '../api/authApi';
import { useAuthStore } from '../store/authStore';

const schema = z.object({
  tenantSlug: z.string().min(1, 'Selecciona una clínica'),
  email: z.string().email('Correo inválido'),
  password: z.string().min(1, 'La contraseña es obligatoria'),
});

export default function LoginPage() {
  const navigate = useNavigate();
  const setSession = useAuthStore((s) => s.setSession);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<LoginRequest>({
    resolver: zodResolver(schema),
    defaultValues: { tenantSlug: 'demo', email: '', password: '' },
  });

  const onSubmit = async (data: LoginRequest) => {
    setError(null);
    setLoading(true);
    try {
      const res = await login(data);
      setSession(res.accessToken, res.user);
      navigate('/dashboard');
    } catch (e: any) {
      setError(e?.response?.data?.message ?? 'Credenciales inválidas');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <div className="w-full max-w-md bg-surface rounded-[20px] shadow-card p-8">
        <div className="flex flex-col items-center mb-8">
          <div className="w-16 h-16 rounded-full bg-primary-light flex items-center justify-center mb-3">
            <Stethoscope className="w-8 h-8 text-primary" />
          </div>
          <h1 className="text-2xl font-bold text-text">MediSuite</h1>
          <p className="text-sm text-muted">Healthcare SaaS</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Field icon={<Building2 className="w-4 h-4 text-muted" />}
                 label="Clínica"
                 error={errors.tenantSlug?.message}>
            <input {...register('tenantSlug')}
                   placeholder="demo"
                   className="w-full outline-none bg-transparent" />
          </Field>

          <Field icon={<Mail className="w-4 h-4 text-muted" />}
                 label="Correo"
                 error={errors.email?.message}>
            <input {...register('email')}
                   type="email"
                   placeholder="admin@clinica.sv"
                   className="w-full outline-none bg-transparent" />
          </Field>

          <Field icon={<Lock className="w-4 h-4 text-muted" />}
                 label="Contraseña"
                 error={errors.password?.message}>
            <input {...register('password')}
                   type="password"
                   placeholder="********"
                   className="w-full outline-none bg-transparent" />
          </Field>

          {error && (
            <div className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</div>
          )}

          <button type="submit"
                  disabled={loading}
                  className="w-full h-11 rounded-[10px] bg-primary hover:bg-primary-dark text-white font-medium shadow-primaryBtn flex items-center justify-center disabled:opacity-50">
            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Iniciar sesión'}
          </button>

          <p className="text-center text-sm text-muted">
            ¿Olvidaste tu contraseña?
          </p>
        </form>
      </div>
    </div>
  );
}

function Field(props: { icon: React.ReactNode; label: string; error?: string; children: React.ReactNode }) {
  return (
    <div>
      <label className="text-xs font-medium text-muted block mb-1">{props.label}</label>
      <div className="flex items-center gap-2 h-11 px-3 bg-surface border border-border rounded-[10px] focus-within:border-primary focus-within:ring-2 focus-within:ring-primary/20">
        {props.icon}
        {props.children}
      </div>
      {props.error && <p className="text-xs text-red-600 mt-1">{props.error}</p>}
    </div>
  );
}
```

**Archivo:** `frontend/src/App.tsx` (reemplazar completo)

```tsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import { useAuthStore } from './store/authStore';

function Dashboard() {
  const user = useAuthStore((s) => s.user);
  return (
    <div className="min-h-screen p-8">
      <h1 className="text-2xl font-bold text-text">Bienvenido, {user?.fullName}</h1>
      <p className="text-muted mt-2">Rol: {user?.role} · Tenant: {user?.tenantId}</p>
    </div>
  );
}

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = useAuthStore((s) => s.token);
  return token ? <>{children}</> : <Navigate to="/login" />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}
```

**Archivo:** `frontend/.env.local` (crear localmente, **NO commitear**)

```
VITE_API_BASE_URL=http://localhost:8080
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] `npm run dev` levanta sin errores.
- [ ] `http://localhost:5173/login` muestra la pantalla con estilos MedCore Clay.
- [ ] Login exitoso con credenciales seed redirige a `/dashboard`.
- [ ] `localStorage` en DevTools contiene `accessToken` y `user`.
- [ ] Login con password mala muestra mensaje rojo "Credenciales inválidas".
- [ ] `.env.local` NO aparece en `git status` (verifica agregarlo al `.gitignore` si es necesario).

Si algo falla: NO commitees. Ve a los prompts de IA.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona los 4 archivos (store, api, pages, App.tsx) **sin** `.env.local`. Summary: `feat(ui): pantalla LoginPage con integración a /api/auth/login`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add frontend/src/store frontend/src/api frontend/src/pages frontend/src/App.tsx
  git commit -m "feat(ui): pantalla LoginPage con integración a /api/auth/login"
  git push origin feature/HU-007-login-ui
  ```

### 🌐 Cómo abrir el Pull Request

Sigue [§5](#seccion-5-pull-request). Base `develop`, compare `feature/HU-007-login-ui`. Título: `feat(ui): pantalla LoginPage con integración a /api/auth/login`.

### 🤖 Prompts de IA útiles

1. **Error CORS al llamar al backend:**
   ```
   Al llamar POST /api/auth/login desde el frontend en :5173 me sale error CORS. El backend tiene CorsConfigurationSource con allowedOrigins `http://localhost:5173`. ¿Qué más reviso?
   ```
2. **Error TypeScript en zodResolver:**
   ```
   Al usar `zodResolver(schema)` con react-hook-form me sale error de tipos: [pegar error]. Mi schema es: [pegar]. ¿Cómo lo tipo correctamente?
   ```
3. **localStorage undefined:**
   ```
   Al arrancar mi app React me sale "localStorage is not defined" en el store de Zustand. Solo pasa en algunos casos. ¿Cómo protejo el acceso a localStorage?
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR abierto.
3. Teams: `✅ Terminé Tarea 12.9 — LoginPage funcional integrada con backend. PR: [URL]`.
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-12-10"></a>
## Tarea 12.10 — Casos de prueba QA

**Responsable(s):** Erika Fuentes + Walter Vásquez (QA).
**Fecha inicio:** 05/08 · **Fecha fin:** 07/08.
**Bucket Planner:** QA.

### 🎯 Qué vas a entregar
3 casos de prueba documentados en `docs/qa/CP_LOGIN.md`, ejecutados manualmente contra backend + frontend, con resultado PASS/FAIL registrado.

### 📍 Dónde va tu trabajo

Rama Git: `test/HU-007-login`
Archivos a crear:
- `docs/qa/CP_LOGIN.md`

### 🛠️ Herramientas
- Navegador (para el frontend).
- Postman (para las llamadas directas al backend).
- DBeaver (para verificar contador de intentos fallidos en la BD).
- VS Code (para editar el markdown).
- Git.

### 📋 Paso a paso

1. Actualiza el repo ([§2](#seccion-2-actualizar)).
2. **PREREQUISITO:** las Tareas 12.7 (backend login) y 12.9 (frontend login) deben estar mergeadas a `develop`. Si no, coordina con el equipo antes.
3. Crea tu rama:
   - **GitHub Desktop:** New Branch → `test/HU-007-login` (base: `develop`) → Publish.
   - **Consola:**
     ```powershell
     git checkout develop && git pull origin develop
     git checkout -b test/HU-007-login
     git push -u origin test/HU-007-login
     ```
4. Crea la carpeta `docs/qa/` si no existe.
5. Crea el archivo `docs/qa/CP_LOGIN.md` con el contenido del bloque siguiente.
6. Levanta backend + frontend (en dos terminales distintas):
   ```powershell
   # Terminal 1
   cd C:\Users\$env:USERNAME\medisuite\backend
   .\mvnw.cmd spring-boot:run
   ```
   ```powershell
   # Terminal 2
   cd C:\Users\$env:USERNAME\medisuite\frontend
   npm run dev
   ```
7. Ejecuta los 3 casos de prueba paso a paso. Para cada uno, sustituye `_[pendiente]_` en el markdown por `PASS` o `FAIL: <descripción del bug>`.
8. Para CP-03 (bloqueo), antes de empezar ejecuta en DBeaver:
   ```sql
   UPDATE users SET failed_login_attempts = 0, locked_until = NULL WHERE email='admin@demo.sv';
   ```
   Al finalizar, verifica con:
   ```sql
   SELECT failed_login_attempts, locked_until FROM users WHERE email='admin@demo.sv';
   ```

### 💻 Contenido a copiar

**Archivo:** `docs/qa/CP_LOGIN.md`

```markdown
# Casos de prueba — HU-007 Login

## CP-01 · Login exitoso
**Precondiciones:** Backend en :8080, frontend en :5173, usuario `admin@demo.sv / admin123` en BD.
**Pasos:**
1. Abrir http://localhost:5173/login.
2. Seleccionar clínica `demo`.
3. Ingresar email `admin@demo.sv` y password `admin123`.
4. Click en "Iniciar sesión".
**Resultado esperado:** redirige a `/dashboard`, muestra nombre del usuario, token en localStorage.
**Resultado real:** _[pendiente]_

## CP-02 · Login con credenciales inválidas
**Precondiciones:** iguales a CP-01.
**Pasos:**
1. Ingresar email `admin@demo.sv` y password incorrecta.
2. Click en "Iniciar sesión".
**Resultado esperado:** toast/mensaje rojo "Credenciales inválidas", permanece en /login.
**Resultado real:** _[pendiente]_

## CP-03 · Bloqueo tras 5 intentos fallidos
**Precondiciones:** iguales a CP-01. `failed_login_attempts` del usuario en 0.
**Pasos:**
1. Ingresar credenciales incorrectas 5 veces consecutivas.
2. Al 6º intento, incluso con credenciales correctas, esperar la respuesta.
**Resultado esperado:** el 6º intento responde 422 con mensaje "Cuenta bloqueada temporalmente".
**Consulta BD:** `SELECT failed_login_attempts, locked_until FROM users WHERE email='admin@demo.sv';`
**Resultado real:** _[pendiente]_
```

### ✅ Verificaciones ANTES de hacer commit

- [ ] Archivo `docs/qa/CP_LOGIN.md` creado.
- [ ] 3 casos de prueba con `Resultado real` completado (PASS o FAIL con detalle).
- [ ] Si algún caso es FAIL, documenta el bug encontrado y abre issue en GitHub para el responsable.
- [ ] Consulta SQL de CP-03 muestra `failed_login_attempts >= 5` y `locked_until` con timestamp futuro.

Si algún caso falla: NO significa que tu tarea está mal. Documenta el FAIL y avisa al responsable del backend o frontend.

### 🚀 Cómo commit + push

- **Opción Web:** selecciona `docs/qa/CP_LOGIN.md`. Summary: `test(auth): agregar casos de prueba de login (HU-007)`. Commit → Push.
- **Opción Consola:**
  ```powershell
  git add docs/qa/CP_LOGIN.md
  git commit -m "test(auth): agregar casos de prueba de login (HU-007)"
  git push origin test/HU-007-login
  ```

### 🌐 Cómo abrir el Pull Request

Sigue [§5](#seccion-5-pull-request). Base `develop`, compare `test/HU-007-login`. Título: `test(auth): casos de prueba HU-007 Login (resultado: X PASS / Y FAIL)`.

### 🤖 Prompts de IA útiles

1. **Redactar mejor un caso de prueba:**
   ```
   Actúa como QA senior. Revisa este caso de prueba y mejóralo agregando datos de test específicos, resultado esperado inequívoco y validaciones adicionales: "[pegar CP]".
   ```
2. **Documentar un bug encontrado:**
   ```
   Encontré este bug ejecutando CP-02: [describir]. Ayúdame a redactar el issue de GitHub con: título, resumen, pasos para reproducir, resultado observado, resultado esperado, ambiente. Formato markdown.
   ```
3. **Automatizar en Postman:**
   ```
   ¿Cómo automatizo el caso CP-03 (5 intentos fallidos + 1 correcto) en Postman usando la Collection Runner? Explícame paso a paso.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PR abierto.
3. Teams: `✅ Terminé Tarea 12.10 — 3 CP ejecutados. Resultados: [PASS/FAIL por caso]. PR: [URL]`.
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-13-1"></a>
## Tarea 13.1 — Conclusiones individuales

**Responsable(s):** Todos los 11 integrantes; Héctor consolida.
**Fecha inicio:** 03/08 · **Fecha fin:** 07/08.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
2-3 líneas escritas por ti sobre lo que aprendiste durante el Avance 1, agregadas a la sección "Conclusiones" del Word.

### 📍 Dónde va tu trabajo

Word (Teams · "📋 Avances del Proyecto"):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Conclusiones"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word → sección "Conclusiones".
2. Busca la línea con tu nombre.
3. Reemplaza `_[2-3 líneas]_` por tu conclusión personal.
4. Escribe en primera persona: "aprendí que...", "me tocó...", "lo más difícil fue...".
5. Mantén el formato de viñeta y el nombre en negrita.

### 💻 Contenido a copiar (plantilla ya en el Word, tú solo reemplazas tu bloque)

```markdown
## 13. Conclusiones

- **LOPEZ RUIZ HECTOR NAPOLEON (PM):** _[2-3 líneas]_
- **VIGIL RAMIREZ ALEJANDRO ANTONIO (SM):** _[2-3 líneas]_
- **ORELLANA ROJAS BAYRON ALEXANDER (Backend):** _[2-3 líneas]_
- **DIAZ SANTOS ZAIR BENETT (Frontend):** _[2-3 líneas]_
- **FLORES HERNANDEZ WALTER ALEJANDRO (Backend):** _[2-3 líneas]_
- **MELGAR RIVAS WILLIAM ARIEL (Frontend):** _[2-3 líneas]_
- **MERINO VENTURA ALEJANDRO SEBASTIAN (BD):** _[2-3 líneas]_
- **FUENTES ORTIZ ERIKA ALEXANDRA (QA):** _[2-3 líneas]_
- **VASQUEZ AMAYA WALTER AMILCAR (QA):** _[2-3 líneas]_
- **VENTURA VELASQUEZ CARLOS MARIO (Architect):** _[2-3 líneas]_
- **SANCHEZ MENJIVAR NICOLE NOHEMY (BA):** _[2-3 líneas]_
```

**Ejemplo de conclusión (referencia):**
> **LOPEZ RUIZ HECTOR NAPOLEON (PM):** Como PM del equipo aprendí a coordinar 11 personas de distintos niveles técnicos y a partir el trabajo en tareas pequeñas y verificables. La organización con Planner nos ahorró muchas horas de re-explicación.

### ✅ Verificaciones

- [ ] Tu bloque personal tiene 2-3 líneas escritas (no queda `_[2-3 líneas]_`).
- [ ] Está escrito en primera persona.
- [ ] Sin faltas de ortografía (Word marca en rojo — corrige).
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático, no hay commit.

### 🤖 Prompts de IA útiles

1. **Escribir tu conclusión (si te bloqueas):**
   ```
   Actúa como mentor académico. Fui responsable de [rol y tareas ejecutadas] durante 3 semanas en un proyecto de software. Ayúdame a redactar 2-3 líneas en primera persona sobre lo que aprendí, sin sonar cliché. Menciona 1 dificultad y 1 fortaleza.
   ```
2. **Revisar redacción:**
   ```
   Revisa esta conclusión y mejórala manteniendo mi voz personal: "[pegar]". Corrige ortografía, gramática y estilo académico.
   ```
3. **Adaptar tono:**
   ```
   Convierte esta conclusión de informal a formal académico sin perder la esencia: "[pegar]".
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 13.1 — Mi conclusión agregada.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-14-1"></a>
## Tarea 14.1 — Bibliografía APA

**Responsable(s):** Nicole Sánchez (BA).
**Fecha inicio:** 04/08 · **Fecha fin:** 06/08.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Bibliografía consolidada en formato APA en el Word.

### 📍 Dónde va tu trabajo

Word (Teams):
https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH

**Sección a rellenar:** **"Bibliografía"**.

### 🛠️ Herramientas
- Microsoft Word Online.

### 📋 Paso a paso

1. Abre el Word → sección "Bibliografía".
2. Divide la sección en 6 subtítulos: Ingeniería de Software, Metodología Ágil / Scrum, Java / Backend, Bases de Datos, Frontend, Fuentes web.
3. Bajo cada subtítulo, pega las referencias del bloque siguiente respetando el formato APA (autor apellido, inicial, año, título en cursiva, editorial).

### 💻 Contenido a copiar

**Ingeniería de Software**
- Sommerville, I. (2011). *Ingeniería del software* (9.ª ed.). Pearson Educación.
- Pressman, R. S., & Maxim, B. R. (2015). *Ingeniería del software: un enfoque práctico* (8.ª ed.). McGraw-Hill.
- Larman, C. (2004). *UML y patrones: introducción al análisis y diseño orientado a objetos* (2.ª ed.). Prentice Hall.

**Metodología Ágil / Scrum**
- Schwaber, K., & Sutherland, J. (2020). *The Scrum Guide*. Scrum.org. https://scrumguides.org
- Sutherland, J. (2014). *Scrum: el arte de hacer el doble de trabajo en la mitad de tiempo*. Paidós Empresa.

**Java / Backend**
- Horstmann, C. S. (2019). *Core Java, Volume I: Fundamentals* (11.ª ed.). Prentice Hall.
- Bloch, J. (2018). *Effective Java* (3.ª ed.). Addison-Wesley.
- Walls, C. (2019). *Spring in Action* (5.ª ed.). Manning Publications.

**Bases de Datos**
- Silberschatz, A., Korth, H. F., & Sudarshan, S. (2019). *Fundamentos de bases de datos* (7.ª ed.). McGraw-Hill.

**Frontend**
- Banks, A., & Porcello, E. (2020). *Learning React* (2.ª ed.). O'Reilly Media.

**Fuentes web**
- Oracle. (2024). *Java SE Documentation*. https://docs.oracle.com/en/java/
- Spring. (2024). *Spring Framework Reference Documentation*. https://spring.io/projects/spring-framework
- PostgreSQL Global Development Group. (2024). *PostgreSQL Documentation*. https://www.postgresql.org/docs/
- Neon. (2026). *Neon Serverless Postgres Documentation*. https://neon.tech/docs

### ✅ Verificaciones

- [ ] 6 subtítulos con al menos 1 referencia cada uno.
- [ ] Todas las referencias en formato APA (Apellido, Inicial. (Año). *Título en cursiva*. Editorial.).
- [ ] Títulos de libro en cursiva.
- [ ] URLs completas (empiezan con https://).
- [ ] Word guardado.

### 🚀 Cómo commit + push
Word guarda automático.

### 🤖 Prompts de IA útiles

1. **Verificar formato APA:**
   ```
   Actúa como bibliotecario académico. Verifica si esta cita está en APA 7.ª edición y corrígela si no: "[pegar cita]".
   ```
2. **Buscar referencia adicional:**
   ```
   Necesito una referencia académica formato APA sobre "arquitectura multi-tenant en SaaS", autor reconocido, publicado después de 2018. Dame 3 opciones con datos completos.
   ```
3. **Generar cita a partir de URL:**
   ```
   Genera una cita APA 7.ª edición a partir de esta URL: [pegar URL]. Necesito: autor(es), año, título del artículo, sitio web, URL.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Word guardado.
3. Teams: `✅ Terminé Tarea 14.1 — Bibliografía APA con 14 referencias.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-consolidar"></a>
## Consolidar documento final + PDF

**Responsable(s):** Héctor López (PM).
**Fecha inicio:** 08/08 · **Fecha fin:** 09/08.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Documento Word revisado, con todas las secciones (14 puntos oficiales) validadas, y exportado a PDF listo para entregar.

### 📍 Dónde va tu trabajo

- Word (Teams): https://uees.sharepoint.com/:w:/s/ProyectoTareasProgramacinII/IQBmmLSsQ2fvTaLWS2673XGjAZ-FXiCXCeAG1C541-VYKeY?e=25uAyH
- PDF resultante subido al mismo canal de Teams.

### 🛠️ Herramientas
- Microsoft Word Online + Escritorio (para exportar a PDF con mejor calidad).
- Microsoft Teams (para subir el PDF final).

### 📋 Paso a paso

1. Abre el Word.
2. Revisa el checklist de cobertura de los 14 puntos:
   - [ ] 1. Portada (Tarea 1.1)
   - [ ] 2. Objetivo General (Tarea 2.1)
   - [ ] 3. Objetivo Específico (Tarea 3.1)
   - [ ] 4. Distribución del Equipo Scrum (Tarea 4.1)
   - [ ] 5. Roles y funciones del sistema (Tarea 5.1)
   - [ ] 6. Requerimientos / HU (Tarea 6.1)
   - [ ] 7. Alcances y límites (Tarea 7.1)
   - [ ] 8. Planificación (Tarea 8.1)
   - [ ] 9. Cronograma Gantt (Tarea 9.1)
   - [ ] 10. Entradas/Salidas por HU (Tarea 10.1)
   - [ ] 11. Entidades del sistema (Tarea 11.1)
   - [ ] 12. Proyecto base con código (Tareas 12.1-12.10) — mencionar el repo y capturar screenshots del backend/frontend funcionando
   - [ ] 13. Conclusiones (Tarea 13.1)
   - [ ] 14. Bibliografía APA (Tarea 14.1)
3. Verifica formato:
   - [ ] Portada con logo UEES.
   - [ ] Índice generado automáticamente (menú Referencias → Tabla de contenido).
   - [ ] Numeración de páginas.
   - [ ] Encabezado con "MediSuite · Avance 1".
   - [ ] Ortografía sin marcas rojas.
4. Descarga el Word:
   - Word Online → menú **Archivo → Guardar como → Descargar copia**.
5. Abre el `.docx` descargado con Microsoft Word Escritorio.
6. Menú **Archivo → Exportar → Crear documento PDF/XPS**.
7. Nombre del PDF: `MediSuite_Avance1_Entrega_2026-08-10.pdf`.
8. Verifica el PDF: se abren todas las páginas, imágenes visibles, tablas bien formateadas.
9. Sube el PDF al canal de Teams "📋 Avances del Proyecto".

### ✅ Verificaciones

- [ ] Los 14 puntos del checklist marcados.
- [ ] Índice generado.
- [ ] Numeración de páginas.
- [ ] Sin ortografía roja.
- [ ] PDF nombrado correctamente.
- [ ] PDF subido a Teams.

### 🚀 Cómo commit + push
No aplica (no hay Git).

### 🤖 Prompts de IA útiles

1. **Revisar cobertura:**
   ```
   Actúa como PM revisor. Aquí está el índice de un documento académico de proyecto de software: [pegar índice]. Compáralo con los 14 puntos requeridos por el profesor: [listar]. ¿Falta algo?
   ```
2. **Generar tabla de contenido:**
   ```
   ¿Cómo genero una tabla de contenido automática en Word Escritorio 365 usando los estilos Heading 1, Heading 2? Paso a paso.
   ```
3. **Exportar PDF con calidad:**
   ```
   Al exportar Word a PDF las imágenes salen borrosas. ¿Qué opciones activo para máxima calidad? Menú y checkbox exacto.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. PDF subido.
3. Teams: `✅ Documento final consolidado y PDF subido. Todos los 14 puntos cubiertos.`
4. Planner → "Hecho".

### ⚠️ Si te trabas
Re-lee → IA → Teams.

---

<a id="tarea-entrega"></a>
## Entrega Avance 1

**Responsable(s):** Héctor López (PM).
**Fecha:** 10/08/2026.
**Bucket Planner:** Documento.

### 🎯 Qué vas a entregar
Subir el PDF final al aula virtual del profesor y notificar al equipo.

### 📍 Dónde va tu trabajo

- Aula virtual (Moodle o el LMS que use la UEES).
- Canal Teams "📋 Avances del Proyecto".

### 🛠️ Herramientas
- Navegador.
- Correo institucional UEES (si aplica).

### 📋 Paso a paso

1. Confirma que el PDF final está subido al canal de Teams (Tarea "Consolidar").
2. Abre el aula virtual del profesor.
3. Ubica la tarea "Avance 1 — Entrega".
4. Sube el PDF `MediSuite_Avance1_Entrega_2026-08-10.pdf`.
5. En el cuadro de comentarios del envío, agrega:
   ```
   Equipo: MediSuite (11 integrantes).
   Repositorio GitHub: https://github.com/NapoSV/medisuite
   Rama de código funcional: develop (con PRs mergeados de HU-007).
   Documento adjunto: MediSuite_Avance1_Entrega_2026-08-10.pdf
   ```
6. Click **Enviar / Entregar**.
7. Toma captura de pantalla de la confirmación de entrega.
8. Sube la captura al canal Teams.

### ✅ Verificaciones

- [ ] PDF subido al aula virtual.
- [ ] Comentario con datos del equipo y repo agregado.
- [ ] Captura de confirmación tomada.
- [ ] Captura subida a Teams.
- [ ] Todos los integrantes notificados.

### 🚀 Cómo commit + push
No aplica.

### 🤖 Prompts de IA útiles

1. **Redactar mensaje de entrega:**
   ```
   Redacta un mensaje formal al profesor confirmando la entrega del Avance 1 de un proyecto de software académico. Menciona: nombre del equipo, número de integrantes, repositorio, archivo entregado. Tono respetuoso, 3-4 líneas.
   ```
2. **Comprimir PDF si pesa mucho:**
   ```
   Mi PDF pesa 25 MB y el aula virtual solo acepta 15 MB. ¿Cómo lo comprimo online sin perder calidad legible? Recomiéndame 2 herramientas gratuitas.
   ```
3. **Verificar que el PDF no tiene errores:**
   ```
   Dame un checklist de 5 puntos para revisar antes de entregar un PDF académico al profesor.
   ```

### 🎬 Cierre
1. ✅ Verificaciones.
2. Entrega confirmada.
3. Teams: `🎉 AVANCE 1 ENTREGADO. Gracias a todos. Confirmación adjunta.`
4. Planner → "Hecho" (todas las tarjetas restantes).

### ⚠️ Si te trabas

- Si el aula virtual rechaza el archivo: revisa el tamaño (comprime si es necesario) y el formato (debe ser PDF).
- Si el aula está caída: notifica al profesor por correo con el PDF adjunto y CC a los coordinadores.
- Si te pasas la hora de cierre: envía el PDF por correo con explicación breve y CC al equipo.

---

## Créditos y contacto

Documento consolidado por Héctor López (PM). Versión v1.0 · 26/07/2026.

Preguntas y bloqueos: canal Teams **"📋 Avances del Proyecto"**.

Repositorio: https://github.com/NapoSV/medisuite

¡Éxito equipo MediSuite! 🚀

