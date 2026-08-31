# Documento Explicativo — Diseño UML Sistema Hospitalario
## Programación II · Semana 5 · Grupo 7

---

## 1. Tema Seleccionado

**Sistema de Gestión Hospitalaria — MediSuite**

Sistema orientado a objetos para la administración integral de una clínica u hospital, que cubre el ciclo completo de atención médica: registro de pacientes, agendamiento de citas, expedientes clínicos, prescripciones, signos vitales y auditoría de acciones del sistema.

---

## 2. Descripción del Problema

Las clínicas y hospitales gestionan diariamente grandes volúmenes de información: datos de pacientes, horarios de doctores, historial médico y prescripciones. Sin un sistema estructurado, esta información se dispersa en registros físicos o planillas desconectadas, generando errores en diagnósticos, pérdida de datos y dificultades en la continuidad del tratamiento.

**MediSuite** resuelve este problema mediante una aplicación orientada a objetos que modela los actores y procesos clave del entorno hospitalario:

- **Gestión de personal:** doctores, enfermeras, administradores y recepcionistas, cada uno con atributos y responsabilidades diferenciadas.
- **Atención al paciente:** registro completo, incluyendo tipo de sangre, alergias y contacto de emergencia.
- **Expediente médico unificado:** un expediente por paciente que centraliza prescripciones y mediciones de signos vitales.
- **Citas médicas:** agendamiento con control de estado (programada, completada, cancelada) y código de reserva único.
- **Trazabilidad:** registro de auditoría que almacena cada acción del sistema con información de cambios antes/después.

---

## 3. Justificación del Diseño

### Jerarquía de herencia: `PersonalClinico`

Se diseñó la clase abstracta `PersonalClinico` como base de los cuatro tipos de empleados del sistema: `Doctor`, `Enfermera`, `Administrador` y `Recepcionista`. Esta decisión aplica el principio de **herencia** y **polimorfismo**:

- Todos comparten atributos comunes (`id`, `user`, `tenant`, `createdAt`) y contratos abstractos (`getFullName()`, `isActive()`), lo que evita duplicación de código.
- Cada subclase agrega atributos propios según su rol (ej. `Doctor` añade `licenseNumber` y `horarioDisponible`; `Enfermera` añade `shift` y `assignedArea`).
- El método `getFullName()` es abstracto para que cada clase pueda derivarlo de su relación con `User`.

### Separación de `User` y `PersonalClinico`

La clase `User` gestiona exclusivamente credenciales de acceso y seguridad (contraseña hasheada, intentos fallidos de login, bloqueo temporal). `PersonalClinico` contiene los datos del rol clínico. Esta separación aplica el principio de **responsabilidad única** (SRP): si se cambia la lógica de autenticación no se afecta el modelo clínico, y viceversa.

### Clase `Tenant` como contexto institucional

Toda entidad del sistema pertenece a un `Tenant` (institución/clínica). Esto permite que el mismo sistema sirva a múltiples organizaciones con datos aislados. `Tenant` actúa como raíz del grafo de dependencias.

### `ExpedienteMedico` como aggregation root del historial

Se eligió `ExpedienteMedico` como punto central del historial clínico de un paciente. Las `Receta`s y `SignoVital`es se asocian al expediente (y no directamente al paciente), de forma que toda la información médica queda encapsulada y navegable desde un único objeto.

### `RegistroAuditoria` desacoplado

El registro de auditoría es independiente del dominio clínico: referencia a `User` y `Tenant`, pero no a entidades médicas específicas (usa `entityName` y `entityId` como cadenas). Esto permite auditar cualquier entidad del sistema sin crear dependencias circulares.

---

## 4. Explicación de las Relaciones

| Relación | Tipo | Multiplicidad | Descripción |
|---|---|---|---|
| `Tenant` → `User` | Asociación | 1 a * | Una institución registra muchos usuarios |
| `User` → `PersonalClinico` | Asociación | 1 a 1 | Cada empleado tiene exactamente una cuenta de usuario |
| `PersonalClinico` ← `Doctor` | Herencia | — | Doctor extiende PersonalClinico |
| `PersonalClinico` ← `Enfermera` | Herencia | — | Enfermera extiende PersonalClinico |
| `PersonalClinico` ← `Administrador` | Herencia | — | Administrador extiende PersonalClinico |
| `PersonalClinico` ← `Recepcionista` | Herencia | — | Recepcionista extiende PersonalClinico |
| `Doctor` → `Especialidad` | Asociación | * a 1 | Muchos doctores pueden pertenecer a la misma especialidad; cada doctor tiene exactamente una |
| `Paciente` → `Cita` | Asociación | 1 a * | Un paciente puede tener muchas citas a lo largo del tiempo |
| `Doctor` → `Cita` | Asociación | 1 a * | Un doctor atiende muchas citas en su agenda |
| `Paciente` → `ExpedienteMedico` | Asociación | 1 a 1 | Cada paciente tiene exactamente un expediente médico |
| `ExpedienteMedico` → `Receta` | Composición | 1 a * | Un expediente contiene múltiples recetas a lo largo del tratamiento |
| `ExpedienteMedico` → `SignoVital` | Composición | 1 a * | Un expediente registra múltiples mediciones de signos vitales |
| `Doctor` → `Receta` | Asociación | 1 a * | Un doctor puede emitir múltiples recetas (en diferentes expedientes) |
| `Tenant` → `RegistroAuditoria` | Asociación | 1 a * | Cada acción en la institución genera un registro de auditoría |
| `User` → `RegistroAuditoria` | Asociación | 1 a * | Cada usuario puede generar múltiples registros de auditoría |

---

## 5. Justificación del Uso de Colecciones Dinámicas

### `ArrayList<User> usuarios` — en `Tenant`

**Estructura elegida:** `ArrayList`

**Justificación:** La lista de usuarios de una institución es una secuencia **ordenada** (por fecha de registro) que permite **duplicados temporales** durante migraciones y admite acceso por índice para paginación. `ArrayList` es la estructura más adecuada cuando se requiere:
- Recorrer todos los elementos en orden.
- Acceso aleatorio por posición (ej. página 2 de 20 usuarios).
- Agregar usuarios frecuentemente al final de la lista.

Para esta colección específica no se eligió `HashSet` porque el orden de inserción puede ser relevante en reportes históricos, y tampoco `HashMap` porque no hay una clave natural distinta al índice para buscar un usuario dentro de la lista de la institución (los usuarios se buscan por email o id a nivel de repositorio, no dentro de esta colección).

---

### `ArrayList<Receta> prescripciones` — en `ExpedienteMedico`

**Estructura elegida:** `ArrayList`

**Justificación:** Las recetas médicas de un paciente son una **serie temporal cronológica** donde el orden importa clínicamente (se necesita saber qué medicamento se prescribió primero o cuál es el más reciente). `ArrayList` permite:
- Mantener el orden de inserción (cronológico por `issuedOn`).
- Agregar nuevas recetas al final.
- Acceder a la última receta directamente con `get(size()-1)`.

Para esta colección no se eligió `HashSet` porque el orden cronológico es esencial para el seguimiento del tratamiento, y una estructura de conjunto sin orden lo perdería.

---

### `ArrayList<SignoVital> signosVitales` — en `ExpedienteMedico`

**Estructura elegida:** `ArrayList`

**Justificación:** Las mediciones de signos vitales son series de tiempo donde cada registro tiene una marca temporal (`recordedAt`). El orden importa para detectar tendencias (ej. temperatura en ascenso). `ArrayList` permite recorrer la secuencia en orden cronológico y acceder al último registro sin recorrer toda la colección.

Para esta colección no se eligió `LinkedList` porque el acceso aleatorio por índice es frecuente (ej. mostrar los últimos 5 registros), y `ArrayList` es más eficiente para eso.

---

### `HashMap<String, String> horarioDisponible` — en `Doctor`

**Estructura elegida:** `HashMap`

**Justificación:** El horario de un doctor se estructura como un mapa de **día de la semana → rango de horas** (ej. `"Lunes" → "08:00-16:00"`, `"Miércoles" → "09:00-13:00"`). `HashMap` es la estructura correcta porque:
- Permite buscar la disponibilidad de un día específico en O(1) sin recorrer toda la estructura.
- Las claves (días) son únicas: no puede haber dos entradas para el mismo día.
- La estructura clave-valor expresa directamente la relación día↔horario.

Para esta colección no se eligió `ArrayList` porque implicaría buscar el día recorriendo la lista completa.

---

### `HashMap<String, Object> cambios` — en `RegistroAuditoria`

**Estructura elegida:** `HashMap`

**Justificación:** Un registro de auditoría almacena los cambios campo por campo: `{"nombre": "Juan → Carlos", "estado": "ACTIVO → INACTIVO"}`. `HashMap` es ideal porque:
- El nombre del campo (clave) identifica unívocamente el cambio.
- El valor puede variar en tipo según el campo auditado (por eso `Object`).
- Permite consultar en O(1) qué cambió en un campo específico.

Para esta colección no se eligió `ArrayList` porque los cambios se consultan por nombre de campo, no por posición.

---

### `HashSet<Doctor> medicos` — en `Especialidad`

**Estructura elegida:** `HashSet`

**Justificación:** Una especialidad tiene un **conjunto único** de doctores adscritos: el mismo doctor no puede estar registrado dos veces en la misma especialidad. `HashSet` garantiza:
- **Unicidad automática:** si se intenta agregar un doctor ya existente, la operación se ignora sin error.
- Consulta de pertenencia en O(1): verificar si un doctor pertenece a la especialidad es instantáneo.
- Sin necesidad de orden: la especialidad no requiere listar sus doctores en ningún orden particular.

Para esta colección no se eligió `ArrayList` porque permitiría duplicados y la búsqueda sería O(n).

---

*Documento generado como parte de la entrega de Semana 5 — Programación II.*
*Universidad Evangélica de El Salvador · Facultad de Ingeniería*
