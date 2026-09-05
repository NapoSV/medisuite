# Brief para Manus — Frontend MediSuite (construcción desde cero)

> Este documento describe **qué** hay que construir. **No** indica cómo debe verse.
> Manus tiene **libertad creativa total** sobre identidad visual, paleta de colores,
> tipografía, iconografía, layout, motion, componentes y estilo general.
> Solo se pide que el resultado sea profesional, accesible (WCAG AA) y responsive.
>
> **Alcance del build:** el frontend debe construirse **desde cero e incluyendo la
> pantalla de login** (y el cambio obligatorio de password en el primer acceso).
> No hay UI existente que reutilizar: todo lo que el usuario ve —desde que abre
> la URL hasta cualquier pantalla interna— es responsabilidad de Manus.

---

## 1. Qué es MediSuite

Plataforma web **SaaS multi-tenant** (multi-empresa) para la gestión integral de
clínicas y hospitales pequeños/medianos. Digitaliza el ciclo completo de atención:

```
Agenda → Triaje → Consulta → Expediente clínico → Recetas → Inventario
```

Cada clínica cliente ("tenant") tiene sus propios usuarios, pacientes, citas y
datos, aislados entre sí. El sistema debe sentirse como una aplicación de
producción real, no como un prototipo académico.

**Idioma:** bilingüe español (default) + inglés. Usar `i18next` o similar.

**Responsive:** debe funcionar bien en escritorio (uso principal en clínica) y
en móvil (paciente que agenda desde el celular). No hay app nativa.

---

## 2. Stack esperado del frontend

- **React 19** + **Vite** (ya está inicializado en `frontend/`).
- **TypeScript**.
- **React Router v6+** para las rutas.
- **Axios** o `fetch` con interceptor para JWT y refresh token.
- **React Query / TanStack Query** para estado servidor (opcional pero recomendado).
- **i18next** para internacionalización ES/EN.
- Librería de estilos y sistema de componentes a criterio de Manus
  (Tailwind, CSS Modules, shadcn, Chakra, Mantine, etc. — libre).
- **NO** copiar tokens, colores ni componentes existentes del repo:
  el frontend actual es un placeholder y debe **reemplazarse por completo**.

---

## 3. Cómo se conecta al backend

- El backend es **Spring Boot 3.3 (Java 21)** y ya está construido.
- Corre en `http://localhost:8097` en desarrollo (Docker Compose).
- Documentación viva de la API: **Swagger UI en `/swagger-ui/index.html`**
  (esa es la fuente de verdad de contratos, DTOs y esquemas de request/response).
- Autenticación: **JWT Bearer** en header `Authorization: Bearer <token>`.
  Refresh token en cookie HttpOnly.
- Base de datos: **PostgreSQL 16 en Neon** — el frontend **nunca** habla con la
  BD directamente, solo consume la API REST del backend.

**Variable de entorno del frontend:**
```env
VITE_API_BASE_URL=http://localhost:8097/api
```

**Manejo de sesión:**
- Guardar `accessToken` en memoria (o `sessionStorage`), nunca en `localStorage`.
- Interceptor: si 401 → intentar refresh; si falla → redirigir a `/login`.
- Al cargar la app, si hay refresh cookie válida, rehidratar sesión.
- Decodificar JWT para leer claims: `userId`, `role`, `tenantId`, `fullName`.

---

## 4. Roles del sistema

El rol viene en el JWT y determina qué rutas y componentes puede ver el usuario.

| Rol | Código | Qué hace |
|---|---|---|
| Paciente | `PATIENT` | Ve sus citas, solicita cita, ve su expediente (solo lectura). |
| Recepcionista | `RECEPTIONIST` | Agenda citas, registra pacientes, check-in. |
| Enfermera | `NURSE` | Cola de triaje, registra signos vitales, prioriza. |
| Médico | `DOCTOR` | Ve su agenda, abre expediente, registra consulta, emite recetas. |
| Administrador de clínica | `ADMIN` | Gestiona usuarios, especialidades, horarios, reportes. |
| Jefe de almacén | `STOCK_MANAGER` | Inventario, órdenes de compra, activos (extensión). |
| Super-admin SaaS | `SUPER_ADMIN` | Gestiona tenants (extensión). |

Cada rol tiene un **dashboard distinto** al iniciar sesión.

---

## 5. Módulos que deben construirse

### 5.1. Autenticación (todos los roles)
- Pantalla de **login** (email + password + selector de clínica/tenant por slug).
- **Cambio de password obligatorio** en el primer inicio de sesión.
- Bloqueo de cuenta tras 5 intentos fallidos en 15 min (mensaje al usuario).
- Cerrar sesión, expiración de token, refresh silencioso.
- Pantalla de perfil del usuario logueado (`/profile`).

### 5.2. Gestión de pacientes (RECEP / NURSE / DOCTOR / ADMIN)
- Listado paginado con búsqueda por nombre o **DUI** (Documento Único de
  Identidad de El Salvador, formato `########-#`).
- Alta de paciente: nombre, **DUI**, fecha de nacimiento, sexo, contacto,
  dirección, alergias conocidas.
- La búsqueda por DUI debe permitir escribir con o sin guion
  (`012345678` = `01234567-8`).
- Detalle de paciente = **expediente clínico**: historial de citas,
  consultas previas, diagnósticos, signos vitales, recetas emitidas, alergias.

### 5.3. Gestión de citas (RECEP / PATIENT / DOCTOR / ADMIN)
- Vista de **calendario** (día / semana / mes) por médico o por consultorio.
- Crear cita: buscar paciente → elegir médico + especialidad → elegir fecha →
  cargar horarios disponibles → elegir slot → escribir motivo → confirmar.
- La respuesta del backend devuelve un **código de reserva** (`COD-XXXX`).
- **Anti doble-reserva:** si el backend responde 409 Conflict, mostrar toast y
  volver a cargar slots.
- Reprogramar y cancelar solo con ≥ 24 h de anticipación (validación en backend,
  pero el frontend debe reflejar el error 422 amablemente).
- Estados de la cita: `PENDING`, `CONFIRMED`, `IN_WAITING`, `IN_CONSULTATION`,
  `COMPLETED`, `CANCELLED`, `NO_SHOW`. Cada estado necesita representación visual.
- **Check-in** desde recepción (botón que mueve cita a `IN_WAITING`).

### 5.4. Triaje / Signos vitales (NURSE)
- Pantalla de **cola de triaje del día** (pacientes con cita en `IN_WAITING`).
- Formulario de signos vitales: peso, talla, presión arterial (sistólica/diastólica),
  temperatura, frecuencia cardíaca, frecuencia respiratoria, saturación O2, síntomas.
- Asignación de **prioridad**: bajo / medio / alto / crítico.
- Al guardar, la cita pasa a `IN_CONSULTATION` y el paciente aparece en la agenda
  del médico con badge de prioridad.

### 5.5. Consulta médica (DOCTOR)
- **Agenda del día** del médico: lista ordenada por hora con estado y prioridad.
- Click en cita → abre expediente del paciente con triaje de hoy resaltado arriba.
- Formulario de consulta: motivo, síntomas, exploración, diagnóstico, tratamiento,
  observaciones.
- Al cerrar consulta → cita pasa a `COMPLETED`.

### 5.6. Recetas (DOCTOR)
- Botón "Emitir receta" dentro de la consulta.
- Formulario con líneas dinámicas: medicamento, dosis, frecuencia, duración,
  vía de administración. Poder agregar/quitar líneas.
- Campo de "indicaciones adicionales".
- Al guardar, opción de **descargar PDF** (endpoint del backend genera el PDF,
  el frontend solo lo abre/descarga).
- Historial de recetas en el expediente del paciente.

### 5.7. Gestión de usuarios (ADMIN)
- Listado de usuarios del tenant (paginado, filtro por rol y estado).
- Alta de usuario: nombre, email, rol. Si el rol es DOCTOR → pedir especialidad y
  número de licencia. Si es NURSE → pedir carné profesional.
- El backend genera password temporal (se muestra una sola vez o se "envía" por
  email simulado — el frontend lo indica claramente).
- Editar, desactivar (soft delete), reactivar.

### 5.8. Especialidades y horarios (ADMIN)
- CRUD de especialidades médicas.
- Gestión de horarios/disponibilidad de cada médico (bloques semanales).
- Asignación de consultorios.

### 5.9. Reportes (ADMIN)
- **Dashboard** con KPIs: pacientes registrados, citas del mes, ocupación
  promedio, top médicos, distribución por estado.
- Reportes con filtros de fecha:
  - Ocupación por médico.
  - Distribución de citas por estado.
  - Pacientes nuevos por período.
- Todos los reportes deben poder exportarse (CSV o al menos "imprimir").
- Visualización con gráficos (Manus elige librería: Recharts, Chart.js, Nivo, etc.).

### 5.10. Portal del paciente (PATIENT)
- "Mis citas" (próximas y pasadas).
- "Solicitar cita": elegir especialidad → médico → ver calendario con
  disponibilidad → confirmar.
- Cancelar/reprogramar (≥ 24 h).
- Ver su expediente (**solo lectura**): consultas pasadas, recetas, signos vitales.

### 5.11. Inventario y compras (STOCK_MANAGER) — extensión
- CRUD de productos (SKU, categoría, unidad, stock actual, stock mínimo).
- Panel de "stock crítico" (productos por debajo del mínimo).
- CRUD de proveedores.
- Órdenes de compra con líneas dinámicas.
- Estados de la orden: `PENDING`, `PARTIALLY_RECEIVED`, `COMPLETE`, `CANCELLED`.
- Registro de recepción total o parcial (actualiza stock).
- Activos físicos (registro básico de equipos).

### 5.12. Auditoría (ADMIN)
- Tabla paginada de logs: usuario, acción, entidad afectada, timestamp, IP.
- Filtros por usuario, tipo de acción, rango de fechas.

### 5.13. Super-admin (SUPER_ADMIN) — extensión
- Alta de tenants (clínicas): nombre, slug único, plan
  (`FREE` / `STARTER` / `PRO` / `ENTERPRISE`).
- Suspender/reactivar tenant.
- Métricas globales.

---

## 6. Rutas del frontend (React Router)

```
/                            → Redirige según sesión y rol
/login                       → Pantalla de login
/change-password             → Cambio obligatorio primer login
/dashboard                   → Dashboard según rol
/profile                     → Perfil del usuario

/patients                    → Listado pacientes
/patients/new                → Alta paciente
/patients/:id                → Expediente paciente
/patients/:id/vital-signs    → Registro de triaje

/appointments                → Agenda (vista según rol)
/appointments/new            → Nueva cita
/appointments/:id            → Detalle cita
/my-appointments             → Portal paciente

/consultations/:id           → Registro de consulta médica

/prescriptions/new           → Emitir receta
/prescriptions/:id           → Detalle receta

/users                       → Gestión de usuarios (admin)
/users/new                   → Nuevo usuario
/specialties                 → Especialidades (admin)
/schedules                   → Horarios de médicos (admin)

/reports                     → Dashboard de reportes (admin)

/inventory/products          → Productos
/inventory/suppliers         → Proveedores
/inventory/purchase-orders   → Órdenes de compra
/inventory/assets            → Activos físicos

/audit                       → Auditoría (admin)
/tenants                     → Gestión de tenants (super-admin)
/settings                    → Configuración

/403                         → Sin permiso
/404                         → No encontrado
```

Cada ruta debe estar **protegida por rol**. Si el usuario no tiene permiso, redirige a `/403`.

---

## 7. Endpoints REST disponibles (resumen)

La fuente de verdad completa es **Swagger UI** (`/swagger-ui/index.html`).
Este es solo un resumen para orientar el desarrollo:

### Auth
- `POST /api/auth/login` — body: `{ tenantSlug, email, password }` → devuelve `{ accessToken, tokenType, expiresIn, user: { id, fullName, role, tenantId } }`.
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/change-password`

### Usuarios
- `GET /api/users?page=&size=&role=`
- `POST /api/users`
- `GET /api/users/{id}` · `PUT /api/users/{id}` · `DELETE /api/users/{id}`
- `GET /api/users/me`

### Pacientes
- `GET /api/patients?search=&page=&size=`
- `POST /api/patients`
- `GET /api/patients/{id}` · `PUT /api/patients/{id}`
- `GET /api/patients/{id}/medical-record`
- `GET /api/patients/{id}/vital-signs`
- `GET /api/patients/{id}/prescriptions`

### Médicos y especialidades
- `GET /api/doctors`
- `GET /api/doctors/{id}/slots?date=YYYY-MM-DD`
- `GET /api/doctors/{id}/agenda?date=YYYY-MM-DD`
- `GET /api/specialties` · `POST /api/specialties`

### Citas
- `POST /api/appointments`
- `GET /api/appointments/{id}` · `PUT /api/appointments/{id}`
- `POST /api/appointments/{id}/cancel`
- `POST /api/appointments/{id}/check-in`
- `POST /api/appointments/{id}/complete`

### Triaje
- `POST /api/vital-signs`
- `GET /api/vital-signs/{id}`

### Recetas
- `POST /api/prescriptions`
- `GET /api/prescriptions/{id}`
- `GET /api/prescriptions/{id}/pdf` → devuelve PDF binario

### Reportes
- `GET /api/reports/occupancy?from=&to=`
- `GET /api/reports/appointments-by-status?from=&to=`
- `GET /api/reports/patients-registered?from=&to=`

### Inventario (extensión)
- `GET /api/inventory/products` · `POST /api/inventory/products`
- `POST /api/purchase-orders`
- `POST /api/purchase-orders/{id}/receive`

---

## 8. Reglas de negocio que el frontend debe respetar

1. **Anti doble reserva:** el backend impone unicidad de slot; ante error 409
   recargar los slots disponibles.
2. **Cancelación/reprogramación:** solo con ≥ 24 h de anticipación (backend
   responde 422 si no; el frontend debe explicar).
3. **Prioridad crítica** en triaje: mostrar alerta visual destacada en la agenda
   del médico (Manus decide cómo).
4. **Password temporal:** obligar cambio en primer login antes de dejar entrar.
5. **Multi-tenant:** el `tenantId` viene en el JWT; el frontend nunca necesita
   enviarlo manualmente en cada request, solo el token.
6. **Auditoría:** toda mutación de datos médicos se registra en el backend
   automáticamente; el frontend no hace nada especial.
7. **Bloqueo de cuenta:** 5 intentos fallidos en 15 min → bloqueo 15 min.
   Mostrar mensaje claro con tiempo restante si aplica.
8. **Roles y permisos:** las rutas del frontend deben mirror las restricciones
   del backend. Si aparece un botón que el rol no puede ejecutar, ocultarlo
   (defensa en profundidad, no seguridad).

---

## 9. Estados globales de UI que hay que resolver

| Estado | Cuándo | Comportamiento sugerido |
|---|---|---|
| Loading | Petición HTTP en vuelo | Skeleton en listas/cards, spinner en botones. |
| Empty | Respuesta con lista vacía | Ilustración + copy + CTA principal. |
| Error 4xx | Validación backend | Toast/inline con mensaje del backend. |
| Error 5xx | Excepción no controlada | Toast genérico + opción de reportar. |
| 401 sesión expirada | JWT vencido | Interceptor intenta refresh; si falla, redirige a login. |
| 403 sin permiso | Rol insuficiente | Toast: "No tienes permiso". No redirige. |
| Offline | Sin red | Banner superior indicando pérdida de conexión. |

---

## 10. Accesibilidad y calidad

- **WCAG 2.1 AA** como mínimo (contraste, foco visible, navegación por teclado,
  ARIA labels en formularios e iconos).
- **Responsive** desde 360 px (móvil) hasta 1920 px (escritorio).
- Semántica HTML correcta (`<main>`, `<nav>`, `<header>`, roles).
- Formularios con validación cliente + reflejo de errores del backend.
- Feedback claro en toda acción (toasts, estados de botones).
- No bloquear la UI durante requests largas.

---

## 11. Datos de prueba

El backend viene con datos seed y **cuentas demo** para todos los roles.
Manus puede solicitar credenciales de prueba al equipo. La estructura de los
DTOs se ve directamente en Swagger UI ejecutando cada endpoint.

---

## 12. Entregable esperado

Un frontend **completo y funcional** que:
- Cubra todos los módulos descritos en §5.
- Consuma la API descrita en §7 sin modificar el backend.
- Se pueda correr con `npm install && npm run dev` (o `pnpm`) en local.
- Se pueda dockerizar con un `Dockerfile` sencillo (multi-stage con Nginx o similar).
- Tenga una identidad visual propia y coherente **diseñada por Manus desde cero**.
- Sea profesional, moderno, accesible y responsive.

---

## 13. Lo que NO se le pide a Manus

- No se le impone paleta de colores, tipografía, iconografía ni logo.
- No debe seguir ningún wireframe existente del proyecto.
- No debe reutilizar componentes del `frontend/` actual (es placeholder).
- No debe tocar el backend, la base de datos ni los contratos de la API.
- No debe implementar módulos fuera del alcance (facturación, telemedicina,
  pagos, firma electrónica, app nativa — ver PRD §5.2).
