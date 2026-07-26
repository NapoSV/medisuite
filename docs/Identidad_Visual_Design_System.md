# Identidad Visual y Design System

## 1. Visión general

**MedCore Clay** es la identidad visual propuesta para un SaaS médico y clínico-administrativo multi-tenant.

El concepto combina:

- La confianza y autoridad visual del sector salud.
- La claridad de un software SaaS empresarial.
- La profundidad visual del Claymorphism.
- Una experiencia moderna, limpia y profesional.
- Componentes suaves, redondeados y con sombras sutiles.
- Una jerarquía visual orientada a tareas y datos clínicos.

### Concepto de diseño

> **Modern Clinical Claymorphism**

### Sensaciones que debe transmitir

**Confianza + Tecnología + Calidez + Claridad + Profesionalismo**

### Regla de oro

> **Soft depth, clinical clarity.**
>
> Profundidad suave + claridad clínica.

El Claymorphism debe utilizarse de forma sutil. La interfaz no debe parecer infantil ni excesivamente decorativa; la información médica y administrativa siempre debe tener prioridad.

---

## 2. Principios de diseño

### 2.1. Confianza

El azul médico será el color principal de la identidad.

Debe utilizarse principalmente en:

- Acciones principales.
- Navegación activa.
- Links.
- Indicadores importantes.
- Gráficos.
- Iconografía principal.

### 2.2. Claridad clínica

La interfaz debe permitir que médicos, administrativos y otros profesionales encuentren rápidamente la información que necesitan.

Priorizar:

- Jerarquía visual clara.
- Espaciado generoso.
- Textos legibles.
- Formularios organizados.
- Estados visuales consistentes.
- Información clínica fácil de escanear.

### 2.3. Profundidad suave

El Claymorphism se expresará mediante:

- Sombras suaves.
- Bordes redondeados.
- Superficies blancas.
- Contraste ligero entre fondo y tarjetas.
- Sombras interiores muy sutiles.
- Sensación de volumen sin exageración.

Evitar:

- Sombras demasiado oscuras.
- Neumorphism extremo.
- Gradientes excesivos.
- Elementos 3D innecesarios.
- Demasiados efectos decorativos.

### 2.4. Consistencia

Todos los módulos deben compartir el mismo lenguaje visual.

Esto incluye:

- Botones.
- Inputs.
- Cards.
- Tablas.
- Modales.
- Badges.
- Alertas.
- Navegación.
- Dashboards.
- Formularios clínicos.

---

## 3. Paleta de colores

### 3.1. Colores principales

| Token | Nombre | Hex | Uso |
|---|---|---|---|
| `primary` | Azul Médico | `#0077BE` | Acciones principales, links, navegación activa, iconos |
| `primary-dark` | Azul Médico Oscuro | `#005A8D` | Hover, estados activos y contraste |
| `primary-light` | Azul Suave | `#E6F4FB` | Fondos de estados activos e iconos |
| `primary-pale` | Azul Muy Suave | `#F2F9FC` | Fondos secundarios y highlights |
| `secondary` | Gris Plata | `#7C8B95` | Texto secundario, iconos y metadata |
| `text` | Texto Principal | `#0F172A` | Títulos y datos importantes |
| `text-secondary` | Texto Secundario | `#334155` | Contenido secundario |
| `muted` | Texto Muted | `#64748B` | Labels, metadata y ayuda |
| `background` | Fondo | `#F8FAFC` | Fondo general de la aplicación |
| `surface` | Blanco | `#FFFFFF` | Cards, modales, formularios y paneles |
| `border` | Borde | `#E2E8F0` | Bordes y separadores |

### 3.2. Colores semánticos

Los colores semánticos deben utilizarse para representar estados y no como decoración.

**Éxito**

```
Principal: #16A34A
Fondo:     #DCFCE7
Texto:     #166534
```

Uso: cita confirmada, paciente activo, pago completado, consulta finalizada.

**Advertencia**

```
Principal: #D97706
Fondo:     #FEF3C7
Texto:     #92400E
```

Uso: cita pendiente, pago pendiente, documento incompleto, acción requerida.

**Error**

```
Principal: #DC2626
Fondo:     #FEE2E2
Texto:     #991B1B
```

Uso: cita cancelada, error de sistema, información crítica, acción fallida.

**Información**

```
Principal: #0284C7
Fondo:     #E0F2FE
Texto:     #075985
```

Uso: mensajes informativos, notificaciones, información complementaria, indicadores clínicos no críticos.

**En consulta / Especial**

Para estados que requieran una categoría adicional:

```
Principal: #7C3AED
Fondo:     #EDE9FE
```

Uso: paciente en consulta, estado especial, procesos clínicos en curso.

---

## 4. Tipografía

### Fuente principal

**Inter**

La fuente oficial del sistema será Inter. Se utilizará en toda la aplicación:

- Dashboard.
- Formularios.
- Historia clínica.
- Tablas.
- Navegación.
- Modales.
- Reportes.
- Mensajes.

No se recomienda mezclar múltiples familias tipográficas.

La personalidad visual debe provenir de:

- Colores.
- Espaciado.
- Border radius.
- Sombras.
- Iconografía.
- Jerarquía visual.

### 4.1. Jerarquía tipográfica

| Elemento | Tamaño | Peso |
|---|---|---|
| Display | 32 px | 700 |
| H1 | 24–28 px | 700 |
| H2 | 20 px | 600 |
| H3 | 16 px | 600 |
| Body | 14 px | 400 |
| Body destacado | 14 px | 500 |
| Label | 12 px | 500 |
| Caption | 11 px | 400 |
| KPI | 28–32 px | 700 |

---

## 5. Border Radius

El sistema debe utilizar bordes redondeados para reforzar el lenguaje Claymorphism.

| Componente | Radius |
|---|---|
| Cards | 16 px |
| Cards principales | 20 px |
| Inputs | 10 px |
| Buttons | 10 px |
| Badges | 999 px |
| Icon containers | 10 px |
| Modales | 20 px |
| Avatars | 50% |

Evitar valores excesivamente altos en componentes que no sean badges o elementos circulares.

---

## 6. Sistema de sombras

Las sombras son una de las características principales de MedCore Clay.

### 6.1. Sombra base

```css
box-shadow:
  8px 8px 20px rgba(15, 23, 42, 0.08),
  -6px -6px 16px rgba(255, 255, 255, 0.9);
```

### 6.2. Sombra para Cards

```css
box-shadow:
  0 8px 24px rgba(15, 23, 42, 0.06),
  inset 1px 1px 2px rgba(255, 255, 255, 0.8);
```

### 6.3. Sombra para botones primarios

```css
box-shadow:
  0 5px 12px rgba(0, 119, 190, 0.22),
  inset 0 1px 1px rgba(255, 255, 255, 0.25);
```

### 6.4. Principio de profundidad

Los elementos deben parecer ligeramente elevados sobre el fondo.

La profundidad debe ser:

- Suave.
- Consistente.
- Discreta.
- Funcional.

La sombra nunca debe competir con la información.

---

## 7. Sistema de espaciado

La base del sistema será un múltiplo de 4 px.

| Valor | Uso |
|---|---|
| 4 px | Microespaciado |
| 8 px | Espaciado muy pequeño |
| 12 px | Espaciado pequeño |
| 16 px | Espaciado estándar |
| 20 px | Espaciado medio |
| 24 px | Separación entre bloques |
| 32 px | Separación de secciones |
| 40 px | Separaciones mayores |

**Recomendación general**

```
Card padding:      20 px
Gap entre cards:   16 px
Secciones:         24 px
Page padding:      32 px
```

---

## 8. Iconografía

La librería de iconos recomendada es **Lucide Icons**.

Se recomienda utilizar una única familia de iconos para mantener consistencia.

Iconos sugeridos:

- Stethoscope
- LayoutDashboard
- Users
- CalendarDays
- ClipboardList
- UserRound
- CreditCard
- BarChart3
- Settings
- ShieldCheck
- Bell
- Search

**Reglas — utilizar:**

- Iconos lineales.
- Grosor consistente.
- Tamaños consistentes.
- Colores derivados del Design System.

**Evitar mezclar:**

- Emojis como iconografía principal.
- Font Awesome con Lucide.
- Material Icons con Lucide.
- Iconos 3D.
- Familias de iconos diferentes.

Los emojis pueden utilizarse únicamente en contextos informales como mensajes de bienvenida, pero no deben formar parte del lenguaje visual principal.

---

## 9. Cards

Las Cards son un componente fundamental.

Características:

- Fondo `#FFFFFF`.
- Border radius de 16–20 px.
- Borde `#E2E8F0` muy sutil.
- Sombra Clay suave.
- Padding de aproximadamente 20 px.

Ejemplo:

```css
.card {
  background: #FFFFFF;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.7);

  box-shadow:
    0 8px 24px rgba(15, 23, 42, 0.06),
    inset 1px 1px 2px rgba(255, 255, 255, 0.8);
}
```

---

## 10. KPI Cards

Los KPI deben ser visualmente claros y no depender de grandes bloques de color.

Ejemplo conceptual:

```
┌───────────────────────────────┐
│ 👥                            │
│                               │
│ Pacientes registrados         │
│                               │
│ 1,248                         │
│                               │
│ ↑ 8.2% vs. mes anterior       │
└───────────────────────────────┘
```

**Reglas**

- Fondo blanco.
- Icono con fondo `#E6F4FB`.
- Icono principal `#0077BE`.
- KPI grande en `#0F172A`.
- Variación positiva en verde.
- Variación negativa en rojo.

El color debe concentrarse en:

- Iconos.
- Indicadores.
- Badges.
- Datos relevantes.

No utilizar colores fuertes como fondo completo de todos los KPI.

---

## 11. Sidebar

El Sidebar debe combinar Claymorphism sutil con una estructura SaaS empresarial.

Estructura:

```
MedCore
Healthcare SaaS

Dashboard
Pacientes
Citas
Historia Clínica
Profesionales
Facturación
Reportes

──────────────

Configuración
Auditoría

Usuario
Administrador
```

Estado activo:

```css
background: #E6F4FB;
color: #0077BE;
```

Sombra interior opcional:

```css
box-shadow:
  inset 2px 2px 5px rgba(0, 119, 190, 0.06),
  inset -2px -2px 5px rgba(255, 255, 255, 0.8);
```

---

## 12. Botones

**Primary Button**

```
Background: #0077BE
Hover:      #005A8D
Text:       #FFFFFF
Radius:     10px
```

Ejemplo: `[ + Nueva cita ]`

**Secondary Button**

```
Background: #FFFFFF
Text:       #0077BE
Border:     #E2E8F0
Radius:     10px
```

**Ghost Button**

```
Background: transparent
Text:       #0077BE
```

---

## 13. Inputs y formularios

Los inputs deben ser limpios y discretos.

```
Nombre completo

┌─────────────────────────────────────┐
│ Juan Carlos Pérez                   │
└─────────────────────────────────────┘
```

Configuración:

```css
background: #FFFFFF;
border: 1px solid #E2E8F0;
border-radius: 10px;
```

Estado focus:

```css
border-color: #0077BE;

box-shadow:
  0 0 0 3px rgba(0, 119, 190, 0.12);
```

Los formularios clínicos deben evitar pantallas excesivamente largas.

Se recomienda dividir información compleja en:

- Datos personales.
- Información de contacto.
- Información clínica.
- Antecedentes.
- Documentos.
- Confirmación.

---

## 14. Estados de la plataforma

Los estados deben ser visualmente consistentes.

| Estado | Color | Fondo |
|---|---|---|
| Confirmada / Activo | `#16A34A` | `#DCFCE7` |
| Pendiente | `#D97706` | `#FEF3C7` |
| Cancelada | `#DC2626` | `#FEE2E2` |
| En espera | `#0284C7` | `#E0F2FE` |
| En consulta | `#7C3AED` | `#EDE9FE` |

Se recomienda utilizar badges con formato:

```css
border-radius: 999px;
```

---

## 15. Tablas

Las tablas deben mantener un estilo limpio y administrativo.

Características:

- Fondo blanco.
- Header con fondo `#F8FAFC`.
- Bordes inferiores suaves.
- Hover de fila `#F8FAFC`.
- Sin líneas verticales fuertes.
- Badges para estados.

Ejemplo:

```
┌──────────────────────────────────────────────────────────┐
│ PACIENTE          │ MÉDICO       │ ÚLTIMA VISITA │ ESTADO│
├──────────────────────────────────────────────────────────┤
│ 👤 Juan Pérez     │ Dr. Martínez │ 26 Jul 2026   │ Activo│
│ 👤 Ana López      │ Dra. Rivera  │ 25 Jul 2026   │ Activo│
│ 👤 Carlos Ruiz    │ Dr. Martínez │ 24 Jul 2026   │ Seguim│
└──────────────────────────────────────────────────────────┘
```

---

## 16. Gráficos

La identidad de los gráficos debe ser discreta.

```
Principal: #0077BE
Secundario: #7C8B95
Grid: #E2E8F0
Background: transparent
```

Evitar:

- Gráficos 3D.
- Gradientes fuertes.
- Más de 5–6 colores simultáneos.
- Sombras exageradas.

La información debe ser fácilmente interpretable.

---

## 17. Layout general

La estructura base recomendada es:

```
┌───────────────────────────────────────────────────────────┐
│                        TOPBAR                              │
│ Buscar...       Clínica San Rafael    🔔    Usuario       │
├───────────────┬───────────────────────────────────────────┤
│               │                                           │
│   MEDCORE     │   Buenos días, Héctor                     │
│               │                                           │
│ Dashboard     │   ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐   │
│ Pacientes     │   │ KPI  │ │ KPI  │ │ KPI  │ │ KPI  │   │
│ Citas         │   └──────┘ └──────┘ └──────┘ └──────┘   │
│ Historia      │                                           │
│ Profesionales │   ┌──────────────────┐ ┌──────────────┐ │
│ Facturación   │   │                  │ │              │ │
│ Reportes      │   │     GRÁFICO      │ │    CITAS     │ │
│               │   │                  │ │              │ │
│ ───────────   │   └──────────────────┘ └──────────────┘ │
│ Configuración │                                           │
│ Auditoría     │   ┌──────────────────┐ ┌──────────────┐ │
│               │   │   PACIENTES      │ │   ALERTAS    │ │
│ Usuario       │   └──────────────────┘ └──────────────┘ │
└───────────────┴───────────────────────────────────────────┘
```

---

## 18. Arquitectura visual del SaaS

La plataforma debe contemplar una experiencia multi-tenant.

Selector de organización:

```
┌────────────────────────────┐
│ 🏥 Clínica San Rafael   ▼  │
└────────────────────────────┘
```

Ejemplo:

- 🏥 Clínica San Rafael
- 🏥 Hospital Central
- 🏥 Centro Médico Norte

El usuario debe poder cambiar de organización únicamente si su cuenta tiene permisos para múltiples tenants.

La interfaz debe mostrar siempre de forma clara la organización activa.

---

## 19. Navegación por rol

La navegación debe ser contextual.

**Administrador**

- Dashboard
- Pacientes
- Citas
- Profesionales
- Historia Clínica
- Facturación
- Reportes
- Usuarios y roles
- Configuración
- Auditoría

**Médico**

- Dashboard
- Mis pacientes
- Agenda
- Historia Clínica
- Consultas
- Diagnósticos
- Recetas

**Recepción**

- Dashboard
- Pacientes
- Agenda
- Citas
- Admisiones
- Pagos

La aplicación debe aplicar dos capas:

- Permisos de backend: seguridad real.
- Visibilidad de frontend: mostrar únicamente las funciones relevantes.

Ocultar una opción en el frontend no sustituye la autorización en backend.

---

## 20. Responsive Design

La aplicación debe ser responsive.

**Desktop**

```
Sidebar: 240–248 px
Topbar: 64–68 px
Page padding: 32 px
```

**Tablet**

- Sidebar colapsable
- Topbar
- Contenido adaptable

**Mobile**

```
┌───────────────────────┐
│ ☰   MedCore      🔔  │
├───────────────────────┤
│ Dashboard             │
│                       │
│ [ Citas ] [ Pacientes]│
│                       │
│ [ Próxima cita ]      │
│                       │
├───────────────────────┤
│ 🏠  📅  👥  ⚙️        │
└───────────────────────┘
```

Se recomienda utilizar:

- Sidebar móvil.
- Bottom navigation para acciones principales.
- Cards apiladas.
- Tablas con scroll horizontal.
- Formularios de una columna.

---

## 21. Tecnologías recomendadas

La identidad visual está pensada para implementarse con:

**Frontend:** React, TypeScript, Vite

**Styling:** Tailwind CSS

**UI Components:** shadcn/ui

**Icons:** Lucide Icons

**Charts:** Recharts

**Forms:** React Hook Form

**Validation:** Zod

La recomendación es personalizar los componentes de shadcn/ui para que adopten los tokens de MedCore Clay.

---

## 22. Design Tokens

**Colores**

```css
:root {
  --primary: #0077BE;
  --primary-dark: #005A8D;
  --primary-light: #E6F4FB;
  --primary-pale: #F2F9FC;

  --secondary: #7C8B95;

  --background: #F8FAFC;
  --surface: #FFFFFF;

  --text: #0F172A;
  --text-secondary: #334155;
  --muted: #64748B;

  --border: #E2E8F0;

  --success: #16A34A;
  --success-bg: #DCFCE7;

  --warning: #D97706;
  --warning-bg: #FEF3C7;

  --danger: #DC2626;
  --danger-bg: #FEE2E2;

  --info: #0284C7;
  --info-bg: #E0F2FE;

  --special: #7C3AED;
  --special-bg: #EDE9FE;
}
```

**Sombras**

```css
:root {
  --shadow-clay:
    8px 8px 20px rgba(15, 23, 42, 0.08),
    -6px -6px 16px rgba(255, 255, 255, 0.9);

  --shadow-card:
    0 8px 24px rgba(15, 23, 42, 0.06),
    inset 1px 1px 2px rgba(255, 255, 255, 0.8);

  --shadow-primary:
    0 5px 12px rgba(0, 119, 190, 0.22),
    inset 0 1px 1px rgba(255, 255, 255, 0.25);
}
```

---

## 23. Reglas de uso del Claymorphism

**Sí utilizar**

- Cards suaves.
- Sombras sutiles.
- Bordes redondeados.
- Azul médico como identidad.
- Mucho espacio en blanco.
- Información jerarquizada.
- Microinteracciones.
- Iconografía lineal.
- Superficies blancas.
- Profundidad visual moderada.

**No utilizar**

- Sombras exageradas.
- Neumorphism extremo.
- Gradientes fuertes.
- Colores pastel excesivos.
- Botones gigantes.
- Elementos decorativos sin función.
- Interfaces infantiles.
- Demasiadas capas visuales.
- Animaciones largas.
- Gráficos 3D.

---

## 24. Componentes principales del Design System

La librería base debería incluir:

```
/components
    /ui
        Button
        Input
        Select
        Dialog
        Modal
        Table
        Badge
        Card
        Tabs
        Calendar
        Dropdown
        Tooltip
        Toast
        Alert
        Avatar

    /layout
        Sidebar
        Topbar
        MobileNav
        PageHeader
        OrganizationSwitcher

    /dashboard
        KPICard
        AppointmentChart
        RevenueChart
        AlertsPanel
        ActivityTimeline

    /clinical
        PatientCard
        PatientHeader
        PatientTimeline
        VitalSigns
        MedicalHistory
        PrescriptionCard
        DiagnosisCard

    /appointments
        AppointmentCalendar
        AppointmentCard
        AppointmentForm

    /patients
        PatientTable
        PatientFilters
        PatientSearch

    /billing
        InvoiceTable
        PaymentStatus
```

---

## 25. Pantallas prioritarias del MVP

La primera versión del producto debería concentrarse en seis pantallas maestras.

### 1. Login / Selección de organización

Responsabilidades:

- Inicio de sesión.
- Selección de tenant si corresponde.
- Recuperación de contraseña.
- Control de acceso.

### 2. Dashboard

Debe mostrar:

- KPIs.
- Citas del día.
- Próximas citas.
- Pacientes recientes.
- Alertas.
- Métricas principales.

### 3. Pacientes

Debe incluir:

- Listado.
- Búsqueda.
- Filtros.
- Alta de pacientes.
- Datos básicos.
- Acceso al expediente.

### 4. Patient Workspace

Debe centralizar:

- Información del paciente.
- Historia clínica.
- Consultas.
- Citas.
- Diagnósticos.
- Recetas.
- Laboratorios.
- Documentos.
- Facturación.

La idea es evitar que el profesional tenga que navegar por múltiples pantallas para conocer el contexto clínico.

### 5. Agenda / Citas

Debe incluir:

- Vista diaria.
- Vista semanal.
- Vista mensual.
- Profesionales.
- Especialidades.
- Disponibilidad.
- Estados de citas.
- Creación y modificación de citas.

Estados: Pendiente, Confirmada, En espera, En consulta, Completada, Cancelada, No asistió.

### 6. Consulta médica

Debe permitir:

- Motivo de consulta.
- Síntomas.
- Signos vitales.
- Antecedentes.
- Diagnósticos.
- Tratamiento.
- Recetas.
- Indicaciones.
- Archivos y documentos.
- Cierre de consulta.

---

## 26. Identidad visual definitiva

```
MedCore Clay

Style:        Modern Clinical Claymorphism
Typography:   Inter
Icons:        Lucide Icons
Primary:      #0077BE
Primary Dark: #005A8D
Secondary:    #7C8B95
Background:   #F8FAFC
Surface:      #FFFFFF
Text:         #0F172A
Border:       #E2E8F0
Radius:       10–20px
Shadow:       Soft Multi-Layer
UI Framework: shadcn/ui
CSS:          Tailwind CSS
Frontend:     React + TypeScript + Vite
```

---

## 27. Resumen ejecutivo

La identidad de MedCore debe sentirse como un SaaS médico moderno y confiable, no como una aplicación médica tradicional ni como una interfaz excesivamente experimental.

La fórmula visual recomendada es:

```
Azul Médico
        +
Inter
        +
Lucide Icons
        +
Superficies Blancas
        +
Fondos #F8FAFC
        +
Border Radius 10–20px
        +
Sombras Clay Sutiles
        +
Estados Semánticos
        +
Mucho Espacio
        +
Jerarquía Clara
        =
MedCore Clay
```

### Principio central

La tecnología debe sentirse moderna, pero la información médica debe sentirse clara, segura y confiable.

Esta identidad visual debe utilizarse como referencia única para el desarrollo del frontend, evitando que cada módulo del sistema adopte estilos diferentes.
