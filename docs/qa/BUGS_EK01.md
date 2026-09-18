# Reporte QA - Tarea EK-01
**Fecha:** 17/09/2026
**Tester:** [tu nombre]
**Navegadores probados:** Chrome
**URL:** http://localhost:5173

## Bug 1: No existe menú de navegación
**Pantalla:** Panel de control (Dashboard)
**Severidad:** Alta (bloquea continuar el flujo de prueba)

**Pasos para reproducir:**
1. Iniciar sesión con la cuenta de Administrador (beatriz.reyes.demo@medisuite.test)
2. Se carga el Panel de control en http://localhost:5173/dashboard
3. Buscar un menú de navegación (barra lateral, barra superior, ícono ☰) para acceder a Pacientes, Citas o Perfil

**Resultado esperado:** Debería existir un menú de navegación visible.
**Resultado actual:** No hay ningún menú de navegación en pantalla. Las únicas opciones visibles son el logo, nombre de usuario y botón "Salir". No es posible navegar a otras secciones (Pacientes, Citas, Perfil).

**Captura:** docs/qa/capturas/dashboard_sin_menu.png

---

## Bug 2: Indicadores del Panel de control sin datos
**Pantalla:** Panel de control (Dashboard)
**Severidad:** Media

**Pasos para reproducir:**
1. Iniciar sesión con la cuenta de Administrador
2. Observar las 4 tarjetas: Citas hoy, Pacientes activos, Alertas, Recetas añadidas

**Resultado esperado:** Cada tarjeta debería mostrar un número (ej. cantidad de pacientes activos).
**Resultado actual:** Las 4 tarjetas muestran un guion "—" en vez de un valor numérico, a pesar de tener datos de prueba cargados en la base de datos (seed.sql).

**Captura:** docs/qa/capturas/dashboard_sin_menu.png

---

## Observación menor: Errores de política de seguridad (CSP) en consola
**Pantalla:** Panel de control (Dashboard)
**Severidad:** Baja (cosmético)

En la consola del navegador (F12) aparecen errores relacionados con la carga de fuentes de Google Fonts, bloqueados por la Content Security Policy (CSP) del sitio. Esto no impide el uso de la app pero podría afectar la tipografía mostrada.

**Mensaje:** "violates the following Content Security Policy directive: style-src 'self' 'unsafe-inline'"

**Captura:** docs/qa/capturas/consola_errores_csp.png


