# MediSuite — Sistema de Logo

## Resumen
Sistema de marca completo para MediSuite. Cubre modo claro, modo oscuro y variantes de uso especial (monocromo, favicon), más los tokens de integración para el equipo de desarrollo.

## Archivos en este repositorio

| Archivo | Descripción |
|---|---|
| `LOGO_SYSTEM.md` | Este documento — spec y guía de uso |
| `MediSuite_Logo_Spec.html` | Spec visual completo con las 10 variantes renderizadas (abrir en navegador) |

### Assets de producción (código)
Los SVGs listos para importar en React están en `frontend/src/assets/logo/`:

| Archivo | Variante | Uso |
|---|---|---|
| `icon-light.svg` | 1, 2, 3 | Login, sidebar modo claro |
| `icon-dark.svg` | 5, 6, 7 | Sidebar modo oscuro |
| `icon-on-primary.svg` | 4 | PWA icon, email header (48px) |
| `favicon.svg` | 10 | Browser tab (16×16) |
| `mono-white.svg` | 8 | Headers oscuros, fondos de color |
| `mono-primary.svg` | 9 | Fondo gris claro, impresión |

El componente React está en `frontend/src/components/Logo.tsx`.

---

## Anatomía del logo

**Ícono:** pinwheel de 4 cuadros (viewBox 240×240), radio de esquina 26px por cuadro de 90px, más un badge de cruz redondeada (azul) con trazo ECG/pulso blanco (stroke-width 9 a esta escala, ≈ 2.5px a 64px de render final).

**Wordmark:** "MediSuite" en Inter 700. Subtítulo "MEDICAL SAAS" en Inter 500, 10px, uppercase, letter-spacing 0.08em, #64748B.

---

## Variantes

1. **Logo vertical completo** — ícono 64px + wordmark + subtítulo centrados debajo. Uso: login page.
2. **Logo horizontal completo** — ícono 40px a la izquierda, wordmark + subtítulo apilados a su derecha. Uso: sidebar expandido.
3. **Solo ícono** — pinwheel + cruz, 32px, sin wordmark. Uso: sidebar colapsado, fuente de favicon.
4. **Ícono sobre primario** — ícono sobre cuadro sólido #0077BE (16px radius). Cuadros del pinwheel en blanco 25% opacidad; cruz/pulso en blanco. Uso: app icon, PWA, email header (48px).
5. **Logo vertical completo (dark)** — igual a #1 sobre fondo #0F172A. Pinwheel: azules más claros (#38BDF8 → #0077BE) alternando con blanco 90% opacidad. Wordmark #F8FAFC.
6. **Logo horizontal (dark)** — igual a #2 con colores dark.
7. **Solo ícono (dark)** — pinwheel/cruz, colores dark, 32px.
8. **Monocromo blanco** — marca completa en blanco sólido. Uso: headers oscuros, fondos de color.
9. **Monocromo primario** — marca completa en #0077BE sólido. Uso: fondos gris claro, impresión.
10. **Favicon 16×16** — simplificado: solo el pinwheel de 4 cuadros, sin cruz/pulso interior (demasiado pequeño para renderizar limpio). Radio de esquina escalado proporcionalmente.

---

## Tokens de diseño

### Colores
```css
--color-primary:       #0077BE;
--color-primary-dark:  #005A8D;
--color-primary-light: #E6F4FB;
--color-text:          #0F172A;
--color-muted:         #64748B;
--color-background:    #F8FAFC;
--color-surface:       #FFFFFF;
--color-border:        #E2E8F0;
```
Acentos del pinwheel en modo oscuro: `#38BDF8`, `#0077BE`, `#005A8D`, blanco al 90% de opacidad.

### Tamaños
```css
--logo-icon-sm: 32px;   /* sidebar colapsado, favicon */
--logo-icon-md: 40px;   /* sidebar expandido */
--logo-icon-lg: 64px;   /* login page */
--logo-icon-xl: 128px;  /* splash / onboarding */
```

### Tipografía
- Wordmark: Inter 700, letter-spacing -0.01em a -0.02em.
- Subtítulo: Inter 500, 10px, uppercase, letter-spacing 0.08em, #64748B.

### Reglas de forma
- Radio de esquina de los cuadros: 28% del tamaño del ícono (escala limpia a cualquier tamaño).
- Grosor del trazo ECG/pulso: 2.5px a 64px de ícono, escala proporcional.

---

## Tabla de uso

| Contexto | Variante | Tamaño | Componente React |
|---|---|---|---|
| Login page | 1 | 64px | `<Logo variant="vertical" />` |
| Sidebar expandido | 2 | 40px | `<Logo variant="horizontal" />` |
| Sidebar colapsado | 3 | 32px | `<Logo variant="icon-only" />` |
| Login modo oscuro | 5 | 64px | `<Logo variant="vertical" mode="dark" />` |
| Sidebar oscuro | 6 | 40px | `<Logo variant="horizontal" mode="dark" />` |
| Email / PWA | 4 | 48px | `<Logo variant="icon-on-primary" />` |
| Favicon | 10 | 16px | `favicon.svg` directo en `index.html` |

---

## Notas de exportación
- Todos los SVGs sin rellenos rasterizados ni efectos bitmap.
- Colores exactos según los hex definidos; opacidad solo en cuadros secundarios del pinwheel dark y variante "ícono sobre primario".
- Inter peso 700 debe estar disponible en el pipeline de fuentes (ya configurado en `index.css` vía Google Fonts).
