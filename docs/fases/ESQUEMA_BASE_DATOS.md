# Esquema de Base de Datos — MediSuite

> **Estado: borrador de validación, no es la fuente de verdad final.** Esto se creó para probar que el motor (PostgreSQL, local y Neon) funciona de punta a punta antes de comprometer desarrollo real sobre él. Las bases de datos (`clinica_dev`) se borraron intencionalmente en local y en Neon — el equipo las va a volver a crear como parte del proceso de aprendizaje. Los scripts de este documento son la referencia para reconstruirlas cuando estén listos, o para partir de aquí y ajustar.

---

## Dónde está todo

| Qué | Dónde |
|---|---|
| DDL completo (17 tablas, v2 auditada) | [`database/schema.sql`](../../database/schema.sql) |
| Script de creación de la BD compartida + permisos del equipo | [`database/00_create_database.sql`](../../database/00_create_database.sql) |
| Datos ficticios de ejemplo (2 tenants demo) | [`database/seed.sql`](../../database/seed.sql) |
| Este documento | `docs/fases/ESQUEMA_BASE_DATOS.md` |

**Cómo aplicarlos cuando el equipo esté listo para recrear la base:**
```bash
psql "<connection string de la base nueva>" -f database/schema.sql
psql "<connection string de la base nueva>" -f database/seed.sql   # opcional, solo para pruebas con datos ficticios
```

---

## Auditoría v2 (23/07/2026) — cambios aplicados

El esquema fue auditado buscando problemas de integridad, rendimiento y alineación con las HU. Resultado: **17 tablas** (antes 15). Cambios:

| # | Hallazgo | Solución aplicada |
|---|----------|-------------------|
| 1 | `doctors.specialty` era texto libre → errores de tipeo, sin reportes por especialidad | Nueva tabla `specialties` + `doctors.specialty_id` (FK) |
| 2 | Nada impedía la doble reserva a nivel de BD (métrica de éxito: "cero doble reserva") | Índice único parcial `uq_appointments_doctor_slot (doctor_id, scheduled_at) WHERE status <> 'CANCELLED'` |
| 3 | HU-003 exige código de reserva y la tabla no lo tenía | `appointments.reservation_code` + `UNIQUE(tenant_id, reservation_code)` |
| 4 | `purchase_orders` no tenía líneas de detalle — imposible saber qué productos pide una orden o actualizar stock al recibir | Nueva tabla `purchase_order_items` |
| 5 | Faltaban índices para consultas frecuentes | `tenant_id` en todas las tablas de dominio, timeline de signos vitales `(medical_record_id, recorded_at DESC)`, recetas por expediente/médico, auditoría `(tenant_id, created_at DESC)`, órdenes por estado |
| 6 | `users` no soportaba el bloqueo tras 5 intentos fallidos (requisito de seguridad Fase 4.2) | Columnas `failed_login_attempts` y `locked_until` |
| 7 | `products.current_stock` podía quedar negativo | `CHECK (current_stock >= 0)` |

## Las 17 tablas (alcance SaaS ampliado, v2)

9 corresponden al alcance académico oficial de los 3 avances (declaradas en la Sección 11 de `docs/intruccionesProyecto.md`); 8 son la extensión SaaS (multi-tenant, inventario, auditoría) descrita en `PLAN_DE_TRABAJO.md`.

| # | Tabla | Qué guarda | Relaciones clave |
|---|-------|-----------|-------------------|
| 1 | `tenants` | Cada clínica cliente (multi-tenant): nombre, plan, estado, límites | Raíz de todo — todas las demás tablas de dominio tienen `tenant_id` |
| 2 | `users` | Cuenta base de cualquier persona que entra al sistema (nombre, CIF, correo, password_hash, rol) | `tenant_id` |
| 3 | `patients` | Datos clínicos básicos del paciente (fecha nacimiento, teléfono, tipo de sangre, alergias) | `user_id` → `users` |
| 3b | `specialties` *(v2)* | Catálogo de especialidades médicas por clínica | `tenant_id` |
| 4 | `doctors` | Datos del médico (número de licencia, horario) | `user_id` → `users`, `specialty_id` → `specialties` |
| 5 | `nurses` | Datos de la enfermera (turno, área asignada) | `user_id` → `users` |
| 6 | `administrators` | Nivel de permiso del administrador | `user_id` → `users` |
| 7 | `receptionists` | Turno y consultorio asignado | `user_id` → `users` |
| 8 | `appointments` | Citas médicas (fecha/hora, estado, motivo, consultorio) | `patient_id` → `patients`, `doctor_id` → `doctors` |
| 9 | `medical_records` | Expediente clínico (uno por paciente) | `patient_id` → `patients` (único) |
| 10 | `vital_signs` | Registros de triaje (peso, talla, presión, temperatura, prioridad) | `medical_record_id` → `medical_records` |
| 11 | `prescriptions` | Recetas médicas (medicamentos, dosis, duración) | `medical_record_id` → `medical_records`, `doctor_id` → `doctors` |
| 12 | `products` | Inventario (insumos/medicamentos, stock actual/mínimo) | `tenant_id` |
| 13 | `purchase_orders` | Órdenes de compra a proveedores | `tenant_id` |
| 13b | `purchase_order_items` *(v2)* | Líneas de detalle de cada orden (producto, cantidad, precio, recibido) | `purchase_order_id` → `purchase_orders`, `product_id` → `products` |
| 14 | `physical_assets` | Activos físicos (equipo médico, mobiliario) | `tenant_id` |
| 15 | `audit_logs` | Registro de auditoría (quién hizo qué, cuándo) — tabla de solo escritura | `tenant_id`, `user_id` |

### Convenciones aplicadas (de `docs/ESTANDARES_CODIGO.md`)
- Nombres de tabla/columna en inglés, `snake_case`, tablas en plural
- Todas las tablas de dominio tienen `tenant_id`, `created_at` y `updated_at`
- `updated_at` se actualiza solo, vía trigger (`set_updated_at()`)
- `audit_logs` es la única excepción sin `updated_at` — un registro de auditoría no debería modificarse nunca después de creado

---

## Pendientes de revisión por el equipo

- [x] ~~`doctors.specialty` texto libre~~ → **Resuelto en v2:** tabla `specialties` + FK. (Si más adelante un médico necesita varias especialidades, se agrega `doctor_specialties` muchos-a-muchos — no se hizo ahora para no complicar el MVP.)
- [x] ~~Doble reserva sin protección en BD~~ → **Resuelto en v2:** índice único parcial `uq_appointments_doctor_slot`.
- [ ] Confirmar si `receptionists`, `products`, `purchase_orders`, `physical_assets` quedan dentro del alcance real de los 3 avances o solo se usan para la demo/presentación final (son parte del "4º objetivo" de valor agregado, no de los 3 avances oficiales).
- [ ] Diagrama visual del DER (`docs/diagramas/DER.png`) — este documento describe las tablas en texto, falta el diagrama en draw.io (tarea de MERINO/VENTURA). **Actualizar a las 17 tablas de v2.**

---

## Historial de esta base de datos

- **22/07/2026:** Héctor validó el motor de punta a punta (local PostgreSQL 18 nativo + Neon compartido, región AWS us-east-1) creando este esquema completo y datos ficticios de prueba, con roles individuales por integrante.
- **22/07/2026:** Las bases de datos `clinica_dev` (local y Neon) se **eliminaron a propósito** después de validar — el equipo las recreará como parte del aprendizaje del proyecto. Los roles de cada integrante se mantienen activos (con permiso `CREATEDB`, vigentes hasta 31/12/2026) para que puedan practicar creando y ajustando el esquema ellos mismos.
- **23/07/2026:** Auditoría completa del esquema (v1 → v2): se detectaron y corrigieron 7 problemas de integridad/rendimiento (ver tabla "Auditoría v2" arriba). El esquema pasó de 15 a **17 tablas**. `database/schema.sql` y `database/seed.sql` quedaron actualizados a v2; **no se aplicó a Neon todavía** — el equipo sigue con el ejercicio de creación manual de tablas (`docs/MANUAL_AVANCE1_EQUIPO.md`), ya ajustado a la nueva estructura.
- **23/07/2026:** Se corrigió un permiso faltante: el `GRANT CREATE ON SCHEMA public` para los 11 roles individuales no se había aplicado correctamente a `clinica_dev` (el `\c` de psql no funciona pegado en el editor SQL de DBeaver). Corregido directamente en Neon y verificado con `has_schema_privilege` para los 11 roles. Se agregó nota de troubleshooting en el manual para que el equipo lo resuelva solo si les vuelve a pasar.
- **23/07/2026:** Héctor probó el ejercicio de práctica end-to-end: creó una tabla de prueba (`test_dummy`) desde DBeaver conectado a Neon con su usuario individual, confirmando que los cambios se reflejan en tiempo real para todo el equipo (con el único matiz de que el panel de Neon tarda unos segundos en refrescar la vista de tablas).
- **23/07/2026:** Se creó [`docs/fases/GUIA_DESARROLLO_BACKEND.md`](GUIA_DESARROLLO_BACKEND.md) — guía maestra con la hoja de ruta completa del proyecto (Etapas 0 a H: desde el armado del documento de Avance 1 sección por sección, hasta el backend completo, frontend mínimo funcional, deploy y presentación final del 26/10), con responsable por nombre completo y fecha en cada tarea.
