# Diseño UI/UX — MediSuite

> Consolidación de identidad visual + wireframes por rol.
> Versión: 1.0 · Fecha: 26/07/2026 · Owner: William Melgar + Zair Diaz (Frontend)

Este documento **no reemplaza** la identidad visual — la fuente única es [Identidad_Visual_Design_System.md](Identidad_Visual_Design_System.md) (concepto **MedCore Clay / Modern Clinical Claymorphism**). Aquí resumimos lo esencial, mapeamos componentes shadcn/ui a los tokens, y agregamos wireframes descriptivos por rol y estados de interacción que la identidad no cubre.

---

## 1. Resumen ejecutivo de la identidad

- **Concepto:** *Modern Clinical Claymorphism* — profundidad suave + claridad clínica.
- **Sensación:** confianza + tecnología + calidez + claridad + profesionalismo.
- **Regla de oro:** *"Soft depth, clinical clarity."* El Claymorphism siempre subordinado a la información.

### 1.1. Tokens críticos (extracto)

```css
--primary:      #0077BE;   /* Azul médico — CTA, navegación, iconos */
--primary-dark: #005A8D;   /* Hover, estados activos */
--primary-light:#E6F4FB;   /* Fondos activos */
--background:   #F8FAFC;   /* Fondo general */
--surface:      #FFFFFF;   /* Cards, modales, formularios */
--text:         #0F172A;   /* Texto principal */
--border:       #E2E8F0;

/* Semánticos */
--success: #16A34A;  --success-bg: #DCFCE7;
--warning: #D97706;  --warning-bg: #FEF3C7;
--danger:  #DC2626;  --danger-bg:  #FEE2E2;
--info:    #0284C7;  --info-bg:    #E0F2FE;
--special: #7C3AED;  --special-bg: #EDE9FE;
```

- **Tipografía:** Inter (única familia). Body 14 px / 400 · H1 24–28 px / 700 · KPI 28–32 px / 700.
- **Border radius:** cards 16–20 px · inputs/buttons 10 px · badges 999 px · avatars 50%.
- **Iconografía:** Lucide Icons (`Stethoscope`, `CalendarDays`, `ClipboardList`, `UserRound`, `ShieldCheck`, `Bell`, `Search`, `LayoutDashboard`).

> **Nota importante:** si algún doc previo (ej. `PLAN_DE_TRABAJO.md`) menciona la paleta antigua `#1A2B4C / #10B981 / #F59E0B`, **prevalece** la paleta MedCore Clay definida arriba. Actualizar a esta.

---

## 2. Mapping componentes shadcn/ui → tokens MedCore Clay

| Componente shadcn | Uso en MediSuite | Tokens aplicados |
|---|---|---|
| `Button` (variant primary) | CTAs principales ("Agendar cita", "Guardar") | bg `--primary` · hover `--primary-dark` · radius 10 px · shadow `--shadow-primary` |
| `Button` (variant secondary) | Acciones secundarias | bg `--surface` · text `--primary` · border `--border` |
| `Button` (variant ghost) | Acciones destructivas suaves ("Cancelar") | text `--primary` · sin bg |
| `Input` | Formularios clínicos | bg `--surface` · border `--border` · focus ring `rgba(0,119,190,0.12)` · radius 10 px |
| `Card` | Contenedor principal de contenido | radius 18 px · shadow `--shadow-card` · border sutil |
| `Table` | Listados de pacientes/citas | header bg `--background` · hover fila `--background` · sin líneas verticales |
| `Badge` | Estados de cita/paciente | radius 999 px · paleta semántica (ver tabla estados) |
| `Dialog` / `Modal` | Confirmaciones, formularios complejos | radius 20 px · overlay `rgba(15,23,42,0.4)` |
| `Toast` | Notificaciones no bloqueantes | slide-in bottom-right · auto-dismiss 4 s |
| `Tabs` | Secciones del expediente | underline `--primary` en activa |
| `Calendar` | Selección de fecha en agenda | días con cita: dot `--primary` |
| `Dropdown` | Selector de organización, menú de usuario | shadow `--shadow-card` |
| `Avatar` | Cabecera de paciente/usuario | radius 50% · iniciales sobre `--primary-light` |
| `Tooltip` | Ayuda contextual sobre iconos | delay 500 ms · bg `--text` · text blanco |

---

## 3. Estados de interacción

| Estado | Aplicación visual |
|---|---|
| **Default** | Colores base según componente. |
| **Hover** | Botón primary: `bg --primary-dark`. Fila de tabla: `bg --background`. Sidebar item: `bg --primary-light`. |
| **Focus** | Input: `border --primary` + ring `rgba(0,119,190,0.12)` de 3 px. Botón: `outline-offset 2px` con `--primary`. |
| **Active / Pressed** | Botón: `translate-y-0.5` + shadow reducida. |
| **Disabled** | Opacity 50% · `cursor-not-allowed` · sin hover. |
| **Loading** | Botón: spinner de Lucide (`Loader2` animado) reemplaza el texto. Card: skeleton con `bg --border` pulsando. |
| **Error** | Input: `border --danger` + mensaje `text --danger` debajo. Toast con `bg --danger-bg` + icono `AlertCircle`. |
| **Success** | Toast con `bg --success-bg` + icono `CheckCircle`. Badge verde en estado confirmado. |
| **Empty state** | Ilustración simple + copy explicativo + CTA para la acción principal (ej. "Aún no hay pacientes registrados. + Nuevo paciente"). |

### 3.1. Estados de dominio (badges)

| Estado | Color texto | Fondo | Contexto |
|---|---|---|---|
| Confirmada / Activo | `#16A34A` | `#DCFCE7` | Cita confirmada, paciente activo |
| Pendiente | `#D97706` | `#FEF3C7` | Cita pendiente, orden PENDING |
| Cancelada | `#DC2626` | `#FEE2E2` | Cita cancelada, error |
| En espera | `#0284C7` | `#E0F2FE` | Paciente en sala de espera, orden PARTIALLY_RECEIVED |
| En consulta | `#7C3AED` | `#EDE9FE` | Paciente siendo atendido |
| Completada | `#16A34A` | `#DCFCE7` | Consulta finalizada, orden COMPLETE |

---

## 4. Wireframes por rol

Se describen las pantallas prioritarias del MVP. Ilustración en ASCII para no depender de assets. La versión pixel-perfect vive en Figma (pendiente de crear).

### 4.1. Login (todos los roles)

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│                          [🩺]                               │
│                       MediSuite                             │
│                Healthcare SaaS                              │
│                                                             │
│              ┌───────────────────────────┐                  │
│              │ 🏥 Selecciona clínica  ▼ │                  │
│              └───────────────────────────┘                  │
│                                                             │
│              ┌───────────────────────────┐                  │
│              │ ✉  correo@clinica.sv     │                  │
│              └───────────────────────────┘                  │
│              ┌───────────────────────────┐                  │
│              │ 🔒 ●●●●●●●●              │                  │
│              └───────────────────────────┘                  │
│                                                             │
│              [    Iniciar sesión    ]                       │
│                                                             │
│              ¿Olvidaste tu contraseña?                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

Notas: card centrada, sombra clay. En error, mensaje `--danger` bajo el botón. Redirección post-login según rol (ver [APPFLOW.md](APPFLOW.md)).

### 4.2. Dashboard Administrador

```
┌─── TOPBAR ── 🔍 Buscar...  🏥 Clínica San Rafael ▼   🔔  👤 Héctor ─┐
├──────────┬────────────────────────────────────────────────────────┤
│ SIDEBAR  │  Buenos días, Héctor                                    │
│          │                                                         │
│ 📊 Dash  │  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐               │
│ 👥 Pac.  │  │ 1248 │  │  34  │  │  87% │  │ 12   │               │
│ 📅 Citas │  │Pacien│  │ Citas│  │ Ocup.│  │Alrts │               │
│ 📋 H.C.  │  │ ↑8%  │  │ hoy  │  │      │  │      │               │
│ 👨‍⚕ Prof.  │  └──────┘  └──────┘  └──────┘  └──────┘               │
│ 💊 Recet │                                                         │
│ 📈 Rep.  │  ┌────────────────────────┐ ┌──────────────────┐       │
│          │  │  Citas por semana      │ │  Próximas citas  │       │
│ ─────    │  │  [chart]               │ │  10:00 Juan Pz   │       │
│ ⚙  Conf. │  │                        │ │  10:30 Ana Lz    │       │
│ 🛡 Audit  │  └────────────────────────┘ └──────────────────┘       │
└──────────┴────────────────────────────────────────────────────────┘
```

### 4.3. Agenda del Médico

```
┌─── TOPBAR ─────────────────────────────────────────────────────────┐
├──────────┬─────────────────────────────────────────────────────────┤
│ SIDEBAR  │  Mi agenda — Lunes 27/07/2026        [< Sem >] [Día]   │
│          │                                                         │
│          │  ┌───────────────────────────────────────────────────┐ │
│          │  │ 08:00  Juan Pérez     [👁 Expediente]  🟢 Confir │ │
│          │  │ 08:30  Ana López      [👁 Expediente]  🟡 Pend.  │ │
│          │  │ 09:00  Carlos Ruiz    [👁 Expediente]  🔵 Espera │ │
│          │  │ 09:30  (libre)                                   │ │
│          │  │ 10:00  María González [👁 Expediente]  🟢 Confir │ │
│          │  └───────────────────────────────────────────────────┘ │
│          │                                                         │
│          │  [ + Bloquear franja ]     [ Ver semana ]              │
└──────────┴─────────────────────────────────────────────────────────┘
```

### 4.4. Triaje / Signos Vitales (Enfermera)

```
┌─── Triaje — Juan Pérez (CIF 12345678) ─────────────────────────────┐
│                                                                    │
│  Datos vitales                                                     │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌──────────────┐   │
│  │ Peso (kg)  │ │ Talla (cm) │ │ Temp (°C)  │ │ Presión mmHg │   │
│  │ 78.5       │ │ 175        │ │ 36.8       │ │ 120/80       │   │
│  └────────────┘ └────────────┘ └────────────┘ └──────────────┘   │
│  ┌────────────┐ ┌──────────────────────────────────────────────┐  │
│  │ FC (bpm)   │ │ Síntomas                                     │  │
│  │ 72         │ │ Dolor de cabeza, náuseas leves               │  │
│  └────────────┘ └──────────────────────────────────────────────┘  │
│                                                                    │
│  Prioridad:  ( ) Baja  (•) Media  ( ) Alta  ( ) Crítica          │
│                                                                    │
│  [ Cancelar ]                                    [ Guardar y pasar]│
└────────────────────────────────────────────────────────────────────┘
```

### 4.5. Expediente del Paciente (Médico)

```
┌── Juan Pérez  ·  35 años  ·  CIF 12345678 ─────────────────────────┐
│  🅰️  Alergias: Penicilina    🩸 Tipo: O+                          │
├────────────────────────────────────────────────────────────────────┤
│  [ Resumen ]  [ Consultas ]  [ Signos vitales ]  [ Recetas ]  [+] │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Última consulta — 20/06/2026 · Dr. Rivera                         │
│  Motivo: Control anual                                             │
│  Diagnóstico: Hipertensión leve                                    │
│  Tratamiento: Losartán 50 mg / día                                 │
│                                                                    │
│  Signos vitales de hoy (por Enf. Alexandra 09:15)                  │
│  Peso 78.5 · Presión 120/80 · Temp 36.8 · FC 72 · Prioridad Media │
│                                                                    │
│  [ + Registrar consulta ]     [ + Emitir receta ]                  │
└────────────────────────────────────────────────────────────────────┘
```

### 4.6. Recepción — Agendar cita

```
┌── Nueva cita ──────────────────────────────────────────────────────┐
│                                                                    │
│  Paciente     [ 🔍 Buscar por nombre o CIF ▼ ]                     │
│  Médico       [ Dr. Martínez — Cardiología ▼ ]                     │
│  Fecha        [ 📅 28/07/2026 ]                                    │
│  Hora         [ 10:00 ▼ ]  (horarios disponibles del médico)       │
│  Motivo       [ Control anual                             ]        │
│                                                                    │
│  [ Cancelar ]                        [ Confirmar cita → COD-8842 ] │
└────────────────────────────────────────────────────────────────────┘
```

---

## 5. Layout general

Consistente con [Identidad Visual §17](Identidad_Visual_Design_System.md#17-layout-general):

- **Sidebar** izquierdo 240–248 px con navegación por rol (ver Identidad §19).
- **Topbar** 64–68 px con buscador global, selector de organización, notificaciones, avatar.
- **Contenido** padding 32 px desktop / 16 px mobile.
- **Bottom navigation** en mobile con 4 accesos rápidos por rol.

---

## 6. Navegación por rol (resumen)

| Rol | Menú lateral |
|---|---|
| **Administrador** | Dashboard · Pacientes · Citas · Profesionales · Historia Clínica · Reportes · Usuarios y roles · Configuración · Auditoría |
| **Médico** | Dashboard · Mis pacientes · Agenda · Historia Clínica · Consultas · Diagnósticos · Recetas |
| **Recepción** | Dashboard · Pacientes · Agenda · Citas · Admisiones |
| **Enfermera** | Dashboard · Cola de triaje · Signos vitales · Historial rápido |
| **Paciente** | Mis citas · Solicitar cita · Mi expediente · Mis recetas |
| **Jefe de almacén** | Inventario · Productos · Órdenes de compra · Activos físicos |

**Regla:** la navegación se oculta según rol en el frontend, **pero** el backend valida el acceso con `@PreAuthorize`. Ocultar en frontend nunca sustituye la autorización en backend.

---

## 7. Accesibilidad — checklist

- [ ] Contraste texto ≥ 4.5:1 (usar [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/) al agregar color).
- [ ] Navegación completa por teclado en todas las pantallas (Tab, Shift+Tab, Enter, Esc).
- [ ] `aria-label` en todo botón con solo icono.
- [ ] `alt=""` en decorativos, `alt` descriptivo en imágenes con significado.
- [ ] Focus visible en todos los elementos interactivos (`outline` de 2 px `--primary`).
- [ ] Formularios con `<label>` asociado por `htmlFor`.
- [ ] Errores de formulario anunciados con `aria-live="polite"`.
- [ ] No usar color como único indicador (agregar icono o texto).

---

## 8. Responsive — checklist

- [ ] Sidebar colapsa a icónico en tablet (< 1024 px).
- [ ] Sidebar se oculta y aparece hamburguesa en mobile (< 768 px).
- [ ] Tablas con scroll horizontal en mobile.
- [ ] Formularios de una columna en mobile.
- [ ] Bottom navigation con las 4 acciones principales del rol en mobile.
- [ ] KPI cards en grid de 4 desktop, 2 tablet, 1 mobile.

---

## 9. Referencias

- [Identidad_Visual_Design_System.md](Identidad_Visual_Design_System.md) — **fuente única** de tokens.
- [APPFLOW.md](APPFLOW.md) — flujos de navegación entre pantallas.
- [PRD.md](PRD.md) — HU y personas que motivan cada pantalla.
- [shadcn/ui docs](https://ui.shadcn.com/) — componentes base.
- [Lucide Icons](https://lucide.dev/) — librería oficial de iconos.
