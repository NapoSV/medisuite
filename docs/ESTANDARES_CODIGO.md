# Estándares de Código — MediSuite

Reglas mínimas para que el código de los 11 miembros del equipo se lea igual.
Aplicar en todo código que entre al repo vía Pull Request.

---

## Idioma

| Qué | Idioma | Ejemplo |
|-----|--------|---------|
| Nombres de clases, métodos, variables | **Inglés** | `PatientService`, `getAppointmentById` |
| Comentarios en el código | **Español** | `// Valida que la cita no colisione con otra existente` |
| Mensajes de commit | **Español** + prefijo | `feat: implementar registro de pacientes` |
| Nombres de archivos de docs | **Español** | `INSTRUCTIVO_GIT.md` |

---

## Java — Backend

### Nomenclatura

| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Clases | `PascalCase` | `PatientService`, `AppointmentController` |
| Métodos y variables | `camelCase` | `findPatientById`, `patientList` |
| Constantes | `UPPER_SNAKE_CASE` | `MAX_LOGIN_ATTEMPTS` |
| Paquetes | `lowercase.separado.por.puntos` | `com.sv.grupo.hospital.citas.service` |

### Estructura de paquetes

```
com.sv.grupo.hospital.citas/
├── config/           ← Beans de configuración (Security, Locale, DB)
├── model/
│   ├── tenant/       ← Tenant, Plan, Feature
│   ├── users/        ← Usuario, Medico, Enfermera, Paciente
│   ├── clinical/     ← Cita, Expediente, SignoVital, Receta
│   └── inventory/    ← Producto, OrdenCompra, ActivoFisico
├── dao/              ← Repositories (Spring Data JPA)
├── service/          ← Lógica de negocio
├── controller/api/   ← Endpoints REST
├── dto/              ← Data Transfer Objects (entrada/salida de APIs)
├── security/         ← JWT, TenantContext, filtros
├── util/             ← Validadores, helpers, generadores
└── exception/        ← Excepciones personalizadas
```

### Reglas de código

- **1 clase por archivo.** El nombre del archivo debe ser igual al nombre de la clase.
- **Todos los atributos de entidades: `private`.**
- **Getters y setters:** usar Lombok (`@Getter`, `@Setter`) o generarlos con el IDE.
- **Validaciones en DTOs:** usar Bean Validation (`@NotNull`, `@Size`, `@Email`, `@Pattern`).
- **Sin `TODO` sin resolver** al hacer PR. Si algo queda pendiente, crear tarea en Planner.
- **Sin `System.out.println`** en código de producción — usar SLF4J (`log.info`, `log.debug`).
- **Sin secretos hardcoded** — contraseñas, claves JWT, API keys van en `.env`, nunca en el código.

### Ejemplo de clase correcta

```java
package com.sv.grupo.hospital.citas.model.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String cif;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private LocalDate birthDate;
}
```

---

## React — Frontend

### Nomenclatura

| Elemento | Convención | Ejemplo |
|----------|-----------|---------|
| Componentes | `PascalCase.jsx` | `PatientForm.jsx`, `AppointmentCard.jsx` |
| Hooks personalizados | `useAlgo.js` | `usePatients.js`, `useAuth.js` |
| Utilidades / helpers | `camelCase.js` | `dateFormatter.js`, `apiClient.js` |
| Constantes | `UPPER_SNAKE_CASE.js` | `API_ROUTES.js` |

### Reglas de componentes

- **1 componente por archivo.**
- **Usar Tailwind CSS** para estilos — no agregar CSS custom salvo casos justificados en PR.
- **No usar `dangerouslySetInnerHTML`** con contenido del usuario (XSS).
- **Validar formularios con `zod`** antes de enviar al backend.
- **Textos visibles al usuario siempre con i18n** — usar `t('clave')`, nunca strings hardcoded en JSX.

### Estructura de carpetas frontend

```
src/
├── components/        ← Componentes reutilizables
├── pages/             ← Una carpeta por página/ruta
├── hooks/             ← Hooks personalizados
├── locales/           ← es.json, en.json
├── lib/               ← apiClient, validaciones, utilidades
└── types/             ← TypeScript types / JSDoc si se usa JS
```

---

## SQL

- Tablas en `snake_case` **plural**: `patients`, `appointments`, `medical_records`
- Columnas en `snake_case`: `patient_id`, `birth_date`, `tenant_id`
- **Toda tabla de dominio debe tener `tenant_id`** (multi-tenancy desde el diseño)
- **Toda tabla de dominio mutable debe tener `created_at` y `updated_at`** — heredados de `BaseEntity`
- **Excepción: tablas append-only** (ej. `audit_logs`) solo tienen `created_at`. No extienden `BaseEntity` porque nunca se actualizan; agregar `updated_at` sería semánticamente incorrecto
- Claves foráneas con sufijo `_id`: `doctor_id`, `patient_id`

---

## Commits

Formato: `prefijo: descripción corta en español`

```
feat: implementar módulo de registro de pacientes HU-001
fix: corregir validación de fechas en formulario de citas
docs: agregar instructivo de setup del entorno
refactor: extraer lógica de validación a PatientValidator
test: agregar unit tests a AppointmentService
```

**Reglas:**
- Descripción en **minúsculas** después del prefijo
- Sin punto al final
- Máximo 72 caracteres en la primera línea
- Si necesitas más detalle, dejar línea en blanco y agregar párrafo

---

## Revisión de PR (checklist del reviewer)

Antes de aprobar un PR, verificar:

- [ ] El código compila y corre sin errores
- [ ] Sigue las convenciones de nomenclatura de este documento
- [ ] No hay `System.out.println`, `TODO`, ni código comentado sin justificación
- [ ] No hay secretos hardcoded
- [ ] Los DTOs tienen Bean Validation
- [ ] Los endpoints tienen `@PreAuthorize` con el rol correcto
- [ ] Si agrega lógica de negocio → existe al menos 1 unit test
- [ ] Si agrega dependencia → auditoría limpia (`npm audit` o `dependency-check`)

**Aprobadores:** ORELLANA BAYRON o LOPEZ HECTOR
