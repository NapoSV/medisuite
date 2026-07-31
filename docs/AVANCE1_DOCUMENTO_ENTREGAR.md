# Documento por entregar debe contener:

> **📌 Aclaraciones del ingeniero (consulta de Héctor, respondida el 27/07/2026):**
> 1. **Portada con foto** → SÍ es obligatoria una foto de **cada integrante**; al ingeniero le facilita visualizar el rostro de cada uno al revisar.
> 2. **Gantt y planificación** → deben cubrir **todo el proyecto hasta la entrega final**, no solo el Avance 1. Para las fases futuras se aceptan tareas genéricas (ej. "entregar avance 2", "entrega final").
> 3. **Punto 12 (estructura base)** → no basta la planificación: ya deben existir las **entidades a utilizar y la base de datos** (una versión inicial, no necesariamente la final).

## 1. Portada con foto, nombre completo, CIF del estudiante, ¿participo? (SI/NO)

> **EXPLICACION ADICIONAL**
> La portada es la primera página del documento e identifica al estudiante. Debe incluir una fotografía, el nombre completo, el CIF (Carné de Identificación Fiscal o número de identificación del estudiante) y una indicación clara de si el estudiante participó (SI) o no (NO) en el avance.
>
> **Aclaración del ingeniero (27/07/2026):** la foto es **estrictamente obligatoria y de cada integrante** — le sirve para reconocer el rostro de cada quien al momento de revisar.

## 2. Objetivo general

Debe existir únicamente un objetivo general para todo el proyecto.

> **EXPLICACION ADICIONAL**
> El objetivo general describe el propósito global que se busca alcanzar con todo el proyecto (no solo con este avance). Se redacta una sola vez y se mantiene igual en todos los avances y en la entrega final; no cambia conforme avanza el proyecto.

## 3. Objetivos específicos

En el primer avance deberá presentarse únicamente el primer objetivo específico. En cada avance se agregará uno nuevo, hasta completar tres objetivos específicos en la entrega final.

> **EXPLICACION ADICIONAL**
> Los objetivos específicos son metas concretas y medibles que, en conjunto, permiten alcanzar el objetivo general. Se construyen de forma progresiva: Avance 1 = 1 objetivo específico, Avance 2 = 2 objetivos específicos (el anterior más uno nuevo), y así hasta llegar a 3 en la entrega final.

## 4. La distribución del equipo (Scrum) asignado coordinador (Scrum Master)

Ejemplo:

| N° | Nombre completo | Rol Scrum | Rol técnico |
|----|------------------|-----------|-------------|
| 1 | JUAN PEREZ | PRODUCT OWNER (BUSSINES OWNER) | ANALISTA |
| 2 | | SCRUM MASTER | DESARROLLADOR |
| 3 | | DEVELOPER | DESARROLLADOR FRONTEND |
| 4 | | DEVELOPER | DESARROLLADOR BACKEND |
| 5 | | DEVELOPER | DESARROLLADOR FULLSTACK |
| 6 | | DEVELOPER | BASE DE DATOS |
| 7 | | QA | ANALISTA DE PRUEBAS |
| 8 | | QA | ANALISTA DE PRUEBAS |
| 9 | | BUSSINES ANALYST | ANALISTA DE NEGOCIO |
| 10 | | ARCHITECT | ARQUITECTO |

> **EXPLICACION ADICIONAL**
> Este punto pide organizar al equipo de trabajo bajo la metodología Scrum, asignando a cada integrante un rol dentro del marco Scrum (Product Owner, Scrum Master, Developer, QA, etc.) y un rol técnico (Frontend, Backend, Base de Datos, Arquitecto, etc.). Un mismo equipo puede tener menos o más de 10 integrantes; la tabla del ejemplo solo ilustra el formato a seguir.

## 5. Definición de roles y funciones por rol del sistema

Ejemplo:

| Rol | Descripción | Funciones en el sistema |
|-----|-------------|--------------------------|
| Medico | Su rol se centra en el diagnóstico, la toma de decisiones sobre el tratamiento y la supervisión de la evolución del paciente. | Diagnosticar<br>Crear receta<br>Ver expediente<br>Crear expediente<br>Modificar expediente<br>Etc… |
| Paciente | Es el centro del sistema y el beneficiario de la atención. | Recibir notificación de cita<br>Cancelar cita<br>Reprogramar cita<br>Etc.. |
| Enfermera | Constituye el eje central del cuidado directo y continuo. Trabaja en colaboración con el médico. | Programar cita<br>Completar datos de paciente<br>Realizar clasificación de paciente<br>Etc.. |
| Administrador | | |
| Empleado | | |
| Etc. | | |

> **EXPLICACION ADICIONAL**
> Aquí no se habla de roles Scrum, sino de los roles de usuario **dentro del sistema/software** que se va a desarrollar (por ejemplo: Médico, Paciente, Enfermera, Administrador). Para cada rol se debe describir en qué consiste y qué funciones podrá realizar dentro del sistema.

## 6. Requerimientos del sistema

Cada equipo definirá qué hará el sistema. Investigar cómo crear Historias de Usuario - metodología Scrum.

| Prioridad | Historia | HU | Criterios |
|-----------|----------|----|-----------|
| | HU-001 | Como enfermera necesito un formulario para registrar paciente y consultar su expediente | • El paciente debe quedar registrado con todos sus datos.<br>• Se debe poder buscar el paciente por nombre<br>• Etc. |
| | HU-002 | Como Médico quiero registrar una receta para llevar el control del tratamiento del paciente. | • La receta debe quedar almacenada.<br>• Debe asociarse al paciente.<br>• Debe mostrar fecha.<br>• Debe mostrar medicamentos. |
| | HU-003 | Como paciente.. Etc. | |
| | HU-004 | Etc | Etc. |
| | HU-005 | etc | |

> **EXPLICACION ADICIONAL**
> Una Historia de Usuario (HU) es una descripción breve de una funcionalidad desde la perspectiva del usuario, con el formato: "Como [rol], quiero [funcionalidad], para [beneficio/propósito]". Cada HU debe incluir criterios de aceptación, que son condiciones específicas que deben cumplirse para considerar la historia como completada. La prioridad indica qué tan urgente/importante es esa HU respecto a las demás.

## 7. Alcances/limitaciones/límites (Que no hará el sistema)

> **EXPLICACION ADICIONAL**
> En esta sección se delimita el proyecto: se especifica claramente qué funcionalidades quedan fuera del sistema, para evitar expectativas incorrectas sobre lo que el software podrá hacer.

## 8. Planificación (distribución de actividades del equipo, según entrega de avances)

| Actividad | Responsable | Fecha |
|-----------|-------------|-------|
| | | |

> **EXPLICACION ADICIONAL**
> Se debe listar qué actividades se realizarán, quién es el responsable de cada una y para qué fecha debe estar lista, organizadas conforme a las fechas de entrega de los avances del proyecto.

## 9. Cronograma (según entrega de avances)

Diagrama de Gantt

*(Imagen de ejemplo de Diagrama de Gantt con tareas 1 a 7 distribuidas entre los meses de Enero a Julio)*

> **EXPLICACION ADICIONAL**
> El cronograma debe representarse mediante un Diagrama de Gantt, que es una herramienta visual (gráfico de barras horizontales) que muestra la duración de cada tarea o actividad del proyecto a lo largo del tiempo.
>
> **Aclaración del ingeniero (27/07/2026):** el Gantt y la planificación (punto 8) son para **todo el proyecto, hasta la entrega final**, no solo para el Avance 1. Las fases futuras pueden ir como tareas genéricas ("entregar avance 2", "entrega final", etc.). Versión completa del equipo: [`diagramas/gantt_proyecto_completo.md`](diagramas/gantt_proyecto_completo.md).

## 10. Entradas/salidas del sistema

Ejemplo (según HU definidas en punto 6):

**HU01:**

**Entradas:** nombre, apellido de paciente, teléfono, fecha de nacimiento, documento de identificación, etc.

**Salidas:** mostrar datos de paciente, reporte general de pacientes, etc.

> **EXPLICACION ADICIONAL**
> Para cada Historia de Usuario definida en el punto 6, se debe especificar qué datos ingresa el usuario al sistema (entradas) y qué información devuelve o muestra el sistema como resultado (salidas).

## 11. Declaración de entidades del sistema (según punto 6 requerimientos del sistema)

| Entidad | Descripción | Atributos |
|---------|-------------|-----------|
| Paciente | Persona que recibe atención | Id, nombre, teléfono |
| Medico | Profesional de salud | Id, nombre, especialidad |

> **EXPLICACION ADICIONAL**
> Las entidades son los objetos o conceptos principales que el sistema debe gestionar (normalmente corresponden a las tablas de la base de datos). Para cada entidad se indica una breve descripción y los atributos (campos/propiedades) que la componen, basándose en los requerimientos/HU definidos en el punto 6.

## 12. Proyecto base con la estructura (Código y clases según entidades)

**Paquete principal:**

```
com.sv.groupname.proyectname
```

Estructura de paquetes:

```
com.sv.grupo.proyecto
├── model
├── dao
├── service
├── util
├── exception
├── test
├── repository
└── main
```

**Ejemplos:**

```
com.sv.groupname.clinic.model
com.sv.groupname.clinic.dao
com.sv.groupname.clinic.service
```

**Dentro de model:**

```
Paciente.java
Medico.java
```

> **EXPLICACION ADICIONAL**
> Se pide crear la estructura base del proyecto de software (paquetes/carpetas) siguiendo una arquitectura por capas: `model` (entidades), `dao`/`repository` (acceso a datos), `service` (lógica de negocio), `util` (utilidades), `exception` (manejo de errores), `test` (pruebas) y `main` (punto de entrada). Dentro del paquete `model` se deben crear las clases Java correspondientes a las entidades declaradas en el punto 11 (por ejemplo, `Paciente.java`, `Medico.java`).
>
> **Aclaración del ingeniero (27/07/2026):** este punto implica tener **más que la planificación**: las entidades a utilizar ya deben existir en código, y la **base de datos ya debe estar creada** — quizá no la versión final, pero sí una versión inicial de lo que se va a desarrollar. (El equipo ya cumple: `database/schema.sql` con 17 tablas aplicado en Neon + entidades JPA en el backend.)

## 13. Conclusiones

Relacionadas con las actividades desarrolladas durante el avance.

> **EXPLICACION ADICIONAL**
> Las conclusiones deben reflejar un análisis o reflexión sobre el trabajo realizado específicamente en este avance, no sobre el proyecto completo.

## 14. Bibliografía (según avances)

> **EXPLICACION ADICIONAL**
> Se deben listar las fuentes de información (libros, artículos, sitios web, documentación oficial, etc.) consultadas para desarrollar el contenido de este avance en particular.

---

## ¿Por qué Scrum? (explicación en clase)

*(Imagen ilustrativa del equipo Scrum: Business Owner, End Users, Domain Expert, Product Owner, Scrum Master, y el equipo de desarrollo compuesto por Dev, QA, Business Analyst y Architect)*
