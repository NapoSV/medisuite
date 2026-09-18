# Plan de mejoras técnicas para la Fase 3 (entrega final)

**Autor:** Héctor López (con investigación asistida por Claude)
**Fecha de redacción:** 2026-09-09
**Alcance:** entrega final del proyecto (Fase 3, semana 26–31/10/2026).
**Estado:** documento de planificación. **NO se implementa en Avance 2.** Se incorpora al plan de Fase 3 cuando arranque esa fase.

## Contexto

Este documento consolida 10 buenas prácticas técnicas identificadas al revisar 20 repositorios médicos/clínicos de referencia (ver detalle en `_research/repos-referencia.md`, no versionado). Los repos más relevantes fueron:

- **mohammadumar-dev/pathlab** — stack idéntico al nuestro (Spring Boot 3.5 + Java 21 + Postgres + Flyway + React/Vite/TS).
- **xinyiklin/careflow** — mismo dominio funcional (citas, expedientes, portal paciente, multi-tenant).
- **Imhotep-Tech/imhotep_smart_clinic** — clínica completa en producción, MIT, en español.
- **ayushirathour/The-Averion-Labs-Front-End** — patrones de frontend React modernos.
- **yagizuygarunlu/MyDoctorApp** — modelo de dominio + Vertical Slice + CQRS.

Cada mejora está descrita con: qué es, por qué la queremos, cómo se aterriza en nuestro stack, tareas concretas, esfuerzo estimado, dependencias, riesgos y criterios de aceptación.

Convenciones:
- **Esfuerzo:** S (≤ 4h), M (medio día a 1 día), L (2–3 días), XL (>3 días).
- **Prioridad Fase 3:** ⭐⭐⭐ crítica, ⭐⭐ importante, ⭐ deseable.

---

## 1. Contratos OpenAPI → tipos TypeScript autogenerados

**Prioridad:** ⭐⭐⭐ · **Esfuerzo:** S · **Referencia:** careflow, pathlab

### Qué es
El backend Spring Boot ya expone una especificación OpenAPI (JSON con todos los endpoints, request/response, tipos) vía `springdoc-openapi` en `/v3/api-docs`. La idea es que el frontend **no** escriba a mano interfaces TypeScript para cada DTO, sino que las genere automáticamente desde esa especificación.

### Por qué
Hoy, si el backend cambia `PacienteDTO` (por ejemplo agrega `fechaUltimaCita: Instant`), el frontend no se entera hasta que un endpoint devuelve un campo desconocido o falta uno. Esto causa bugs silenciosos: campos que llegan `null`, formularios que envían tipos incorrectos, o `TypeError` en runtime. Al autogenerar los tipos, cualquier cambio de contrato **rompe la compilación del frontend**, forzando a actualizar ambos lados.

### Implementación en MediSuite
1. Agregar dependencia dev en `frontend/`:
   ```bash
   pnpm add -D openapi-typescript
   ```
2. Agregar script en `frontend/package.json`:
   ```json
   "scripts": {
     "gen:api": "openapi-typescript http://localhost:8097/v3/api-docs -o src/api/schema.d.ts"
   }
3. Refactorizar cliente HTTP (probablemente Axios/fetch wrapper) para tipar `data` con `components["schemas"]["PacienteDTO"]` de `schema.d.ts`.
4. Documentar en `README.md` del frontend: "correr `pnpm gen:api` cuando cambien contratos del backend".

### Tareas concretas para Fase 3
- [ ] F3-API-01: instalar `openapi-typescript` y agregar script.
- [ ] F3-API-02: generar `schema.d.ts` inicial y commitearlo.
- [ ] F3-API-03: refactorizar 2–3 servicios del frontend para usar los tipos autogenerados como prueba de concepto.
- [ ] F3-API-04: documentar flujo en README y (opcional) automatizar con hook de pre-commit.

### Dependencias
- Backend debe tener `springdoc-openapi` bien anotado en controllers y DTOs (usar `@Schema`, `@Operation`).

### Riesgos
- Si el OpenAPI backend está mal documentado (falta descripción, tipos genéricos como `Object`), los tipos generados serán débiles. Mitigación: hacer una pasada de anotaciones antes de generar.

### Criterio de aceptación
- Un cambio de campo en un DTO backend, tras correr `pnpm gen:api`, provoca error de compilación en frontend en los lugares afectados.

---

## 2. Audit-log de accesos a datos sensibles

**Prioridad:** ⭐⭐⭐ · **Esfuerzo:** M · **Referencia:** careflow (reveal auditado de SSN)

### Qué es
Registrar en una tabla append-only cada acceso de lectura/modificación a datos sensibles del paciente: quién (userId), qué (tipo de recurso + id), cuándo (timestamp UTC), desde dónde (IP + user agent), y opcionalmente por qué (motivo).

### Por qué
Las clínicas manejan datos regulados (historia clínica, DUI, diagnósticos, recetas). Un caso de mal uso interno (un asistente mira pacientes que no le corresponden) hoy es indetectable. El audit-log da:
1. **Evidencia legal** ante inspecciones o denuncias.
2. **Detección** de patrones anómalos (mismo user viendo 100 pacientes distintos en 1 hora).
3. **Rendición de cuentas** — los usuarios saben que su acceso queda registrado.

### Implementación en MediSuite

**Tabla:**
```sql
CREATE TABLE audit_data_access (
    id BIGSERIAL PRIMARY KEY,
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    user_email TEXT NOT NULL,
    resource_type TEXT NOT NULL,       -- 'PACIENTE', 'EXPEDIENTE', 'RECETA', 'CITA'
    resource_id TEXT NOT NULL,
    action TEXT NOT NULL,              -- 'READ', 'UPDATE', 'DELETE', 'EXPORT'
    ip_address INET,
    user_agent TEXT,
    reason TEXT,                       -- opcional, texto libre
    accessed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_tenant_user ON audit_data_access (tenant_id, user_id, accessed_at DESC);
CREATE INDEX idx_audit_resource ON audit_data_access (tenant_id, resource_type, resource_id, accessed_at DESC);
```

**Aspecto Spring:**
```java
@Aspect
@Component
public class AuditAccessAspect {
    @AfterReturning("@annotation(Audited)")
    public void logAccess(JoinPoint jp, Audited audited) {
        // extraer userId de SecurityContext, resourceId del argumento, insertar fila
    }
}
```

**Uso:**
```java
@GetMapping("/pacientes/{id}")
@Audited(resource = "PACIENTE", action = "READ")
public PacienteDTO obtener(@PathVariable UUID id) { ... }
```

### Tareas concretas para Fase 3
- [ ] F3-AUD-01: migración Flyway con tabla `audit_data_access` e índices.
- [ ] F3-AUD-02: anotación `@Audited` + aspecto AOP.
- [ ] F3-AUD-03: aplicar anotación en endpoints de Paciente, Expediente, Receta, Cita (lectura y modificación).
- [ ] F3-AUD-04: endpoint admin `GET /admin/audit?resourceId=...&userId=...&from=...&to=...` con paginación.
- [ ] F3-AUD-05: vista simple en el portal admin para consultar logs.
- [ ] F3-AUD-06: política de retención documentada (sugerido: 5 años activos + archivo).

### Dependencias
- JWT filter (VG-08) debe estar estable y exponer el user en SecurityContext.
- TenantContext debe estar funcionando.

### Riesgos
- **Volumen:** la tabla crecerá rápido. Mitigar con particionado por mes (Postgres native partitioning) desde el día 1.
- **Performance:** escritura extra por cada request auditado. Mitigar con inserts asíncronos (`@Async`) si se detecta latencia.
- **Utilidad depende de revisión:** si nadie mira los logs, no sirve. Definir responsable (admin de clínica).

### Criterio de aceptación
- Todo endpoint marcado como `@Audited` produce una fila en `audit_data_access` con datos completos.
- El endpoint admin permite filtrar y paginar los últimos accesos a un recurso.

---

## 3. Multi-tenant como capa transversal (no repetido en cada query)

**Prioridad:** ⭐⭐⭐ · **Esfuerzo:** L · **Referencia:** careflow (facility-scoping)

### Qué es
Actualmente (VG-08) tenemos `TenantContext` que guarda el `tenantId` del usuario en un `ThreadLocal`. La mejora es que **ningún repositorio escriba manualmente** `WHERE tenant_id = ?` en sus queries. Eso se aplica automáticamente vía:
- **Opción A:** filtros Hibernate (`@FilterDef` + `@Filter` en la entidad, activado por interceptor).
- **Opción B:** anotación `@Where(clause = "tenant_id = current_tenant()")` + función Postgres `current_tenant()` que lee de una variable de sesión seteada por interceptor.

### Por qué
El bug clásico en apps multi-tenant es: un dev nuevo agrega un query (por ejemplo `findByFechaCitaAfter(...)`) y **olvida** filtrar por tenant. Resultado: la Clínica A ve datos de la Clínica B. Es una fuga de datos silenciosa y catastrófica. La solución correcta es hacer el filtro **invisible pero obligatorio**.

### Implementación en MediSuite

**Opción recomendada: Hibernate `@Filter`**

```java
@Entity
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Paciente {
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;
    // ...
}
```

**Activador (interceptor JPA / filtro Spring):**
```java
@Component
public class TenantFilterInterceptor {
    @PersistenceContext EntityManager em;

    public void enableFilter() {
        UUID tenantId = TenantContext.getTenantId();
        em.unwrap(Session.class)
          .enableFilter("tenantFilter")
          .setParameter("tenantId", tenantId);
    }
}
```

Se llama desde un `@Around` en `@RestController` o desde un `Filter` de servlet.

**Prevención de saltarse el filtro:**
- Prohibir `native queries` sin revisión.
- Test de integración que intente `findAll()` con tenant A y verifique que no ve datos de tenant B.

### Tareas concretas para Fase 3
- [ ] F3-MT-01: agregar `@FilterDef` + `@Filter` en todas las entidades con `tenant_id`.
- [ ] F3-MT-02: interceptor que activa el filtro al inicio de cada request autenticado.
- [ ] F3-MT-03: revisar todos los repos actuales y **eliminar** los `findByTenantIdAnd...` (ya no son necesarios).
- [ ] F3-MT-04: test de aislamiento: crear 2 tenants, hacer 10 queries desde tenant A, verificar 0 resultados de tenant B.
- [ ] F3-MT-05: documentar la regla "toda entidad de negocio debe llevar `@Filter`".

### Dependencias
- `TenantContext` (VG-08) debe estar consolidado.

### Riesgos
- **Complejidad de debugging:** un dev que no conoce el filtro puede confundirse cuando su query no devuelve nada. Mitigar con logging DEBUG del filtro activo.
- **Casos que necesitan cruzar tenants:** operaciones admin globales. Mitigar con anotación explícita `@IgnoreTenantFilter` (uso restringido, revisado en PR).

### Criterio de aceptación
- Un query nuevo escrito sin filtrar manualmente por tenant, ejecutado desde el contexto del tenant A, no devuelve datos del tenant B.
- Test automatizado en CI que valida el aislamiento.

---

## 4. Constraint de agenda a nivel de base de datos (evitar doble booking)

**Prioridad:** ⭐⭐ · **Esfuerzo:** M · **Referencia:** careflow

### Qué es
Postgres soporta constraints de exclusión (`EXCLUDE USING GIST`) sobre rangos de tiempo (`tstzrange`). Se define una regla: **no puede existir dos filas en la tabla `cita` con el mismo `doctor_id` y rangos `[inicio, fin)` que se traslapen**. La BD lo garantiza; Java no tiene que validar nada.

### Por qué
Validar en Java tiene una ventana de race condition:
1. Doctor A llama `POST /citas` a las 10:00:00.001 → Java consulta "¿hay cita solapada?" → no.
2. Doctor A llama `POST /citas` a las 10:00:00.003 → Java consulta → no (la del paso 1 aún no está commiteada).
3. Ambas se insertan. **Doble booking.**

El constraint BD cierra esa ventana porque Postgres lo evalúa en el momento del `INSERT`, con lock apropiado.

### Implementación en MediSuite

**Extensión necesaria (una sola vez):**
```sql
CREATE EXTENSION IF NOT EXISTS btree_gist;
```

**Constraint:**
```sql
ALTER TABLE cita
ADD CONSTRAINT cita_no_solapada_doctor
EXCLUDE USING GIST (
    doctor_id WITH =,
    tenant_id WITH =,
    tstzrange(fecha_inicio, fecha_fin, '[)') WITH &&
) WHERE (estado NOT IN ('CANCELADA', 'NO_ASISTIO'));
```

Notas:
- `WITH =` requiere igualdad exacta en doctor y tenant.
- `WITH &&` es el operador "se traslapa" para rangos.
- `WHERE` excluye citas canceladas del cálculo (una cita cancelada no bloquea el slot).

**Manejo del error en Java:**
```java
try {
    citaRepository.save(cita);
} catch (DataIntegrityViolationException e) {
    if (e.getMessage().contains("cita_no_solapada_doctor")) {
        throw new HorarioOcupadoException("El doctor ya tiene una cita en ese horario");
    }
    throw e;
}
```

### Tareas concretas para Fase 3
- [ ] F3-CIT-01: habilitar extensión `btree_gist` en migración.
- [ ] F3-CIT-02: agregar constraint de exclusión a tabla `cita`.
- [ ] F3-CIT-03: crear `HorarioOcupadoException` + handler global que devuelve 409 Conflict con mensaje amigable.
- [ ] F3-CIT-04: test de concurrencia: 10 threads intentan crear la misma cita simultáneamente; solo 1 debe pasar.

### Dependencias
- Tabla `cita` debe existir con `doctor_id`, `tenant_id`, `fecha_inicio`, `fecha_fin`, `estado`.

### Riesgos
- **Mensaje técnico:** el error crudo de Postgres es feo. Mitigar con el handler global (paso F3-CIT-03).
- **Casos válidos de solapamiento** (ej. dos doctores en misma sala): el constraint es solo por `doctor_id`, no bloquea eso. Si más adelante se agrega `sala_id`, se puede añadir un constraint análogo.

### Criterio de aceptación
- Intentar insertar dos citas del mismo doctor con horarios solapados falla con 409 y mensaje claro.
- Test de concurrencia pasa consistentemente.

---

## 5. Portales separados por rol en el frontend

**Prioridad:** ⭐⭐ · **Esfuerzo:** L · **Referencia:** imhotep_smart_clinic

### Qué es
Reorganizar el frontend en **3 sub-aplicaciones** dentro de la misma SPA, cada una con su router, layout y componentes:
```
frontend/src/
├── portals/
│   ├── doctor/
│   │   ├── routes.tsx
│   │   ├── layout/
│   │   └── pages/
│   ├── asistente/
│   └── paciente/
├── shared/               # componentes UI, hooks, api client comunes
└── auth/                 # login, refresh, guards
```

Al login, se detecta el rol y se redirige a `/doctor/*`, `/asistente/*` o `/paciente/*`.

### Por qué
La alternativa (una sola app con `if (user.role === 'DOCTOR') { ... }` esparcido en 30 componentes) escala mal:
- El bundle carga código de todos los roles aunque solo uses uno.
- Los componentes se vuelven ilegibles.
- Un cambio en la UX del doctor puede romper accidentalmente la del paciente.

Con portales separados:
- Cada rol carga solo su código (lazy loading por portal).
- El código es más fácil de leer y probar.
- Los layouts pueden ser muy distintos (ej. paciente móvil-first, doctor desktop-first).

### Implementación en MediSuite
1. Definir rutas top-level:
   ```tsx
   <Routes>
     <Route path="/login" element={<Login />} />
     <Route path="/doctor/*" element={<RequireRole role="DOCTOR"><DoctorPortal /></RequireRole>} />
     <Route path="/asistente/*" element={<RequireRole role="ASISTENTE"><AsistentePortal /></RequireRole>} />
     <Route path="/paciente/*" element={<RequireRole role="PACIENTE"><PacientePortal /></RequireRole>} />
   </Routes>
   ```
2. Cada `*Portal` carga con `React.lazy()` para code-splitting.
3. `shared/` contiene: cliente API tipado, componentes UI (Button, Modal, Table), hooks (`useAuth`, `useTenant`).

### Tareas concretas para Fase 3
- [ ] F3-FE-01: crear estructura `portals/{doctor,asistente,paciente}/`.
- [ ] F3-FE-02: mover pantallas existentes a su portal correspondiente.
- [ ] F3-FE-03: implementar `RequireRole` guard.
- [ ] F3-FE-04: agregar lazy loading por portal.
- [ ] F3-FE-05: extraer componentes comunes a `shared/`.

### Dependencias
- Roles del backend deben estar consolidados y venir en el JWT.

### Riesgos
- **Duplicación:** algunos componentes serán muy similares entre portales. Regla: si se duplica una segunda vez, se sube a `shared/`.
- **Migración incremental:** no hacerlo de golpe. Un portal a la vez.

### Criterio de aceptación
- Un doctor que abre `/paciente/expediente` es redirigido a su portal.
- El bundle del portal paciente no incluye código del portal doctor.

---

## 6. Organización por feature (Vertical Slice) en el backend

**Prioridad:** ⭐⭐ · **Esfuerzo:** L · **Referencia:** MyDoctorApp

### Qué es
Cambiar la estructura del backend de:
```
com.medisuite/
├── controllers/     # PacienteController, CitaController, RecetaController, ...
├── services/        # PacienteService, CitaService, ...
├── repositories/    # PacienteRepository, ...
└── dto/             # PacienteDTO, CitaDTO, ...
```
a:
```
com.medisuite/
├── pacientes/       # Controller + Service + Repository + DTO + Validator
├── citas/
├── recetas/
├── expedientes/
├── auth/
└── shared/          # excepciones globales, config, utils
```

### Por qué
Con la estructura por capa, tocar una feature (ej. "agregar campo de alergia al paciente") requiere abrir:
1. `dto/PacienteDTO.java`
2. `entities/Paciente.java`
3. `repositories/PacienteRepository.java`
4. `services/PacienteService.java`
5. `controllers/PacienteController.java`

En Vertical Slice, todo está en `pacientes/`. Un dev nuevo puede leer y entender la feature completa sin saltar entre 5 carpetas. Además, si eventualmente se quiere extraer un módulo a microservicio, el trabajo ya está delimitado.

### Implementación en MediSuite
- Regla: **nuevas features nacen con Vertical Slice** (recetas, expedientes en Fase 3).
- Refactor incremental de features existentes solo si el cambio es de bajo riesgo.
- Cada feature-package expone solo lo público (controller endpoints); todo lo demás package-private.

### Tareas concretas para Fase 3
- [ ] F3-VS-01: crear módulo `recetas/` como Vertical Slice desde cero.
- [ ] F3-VS-02: crear módulo `expedientes/` como Vertical Slice.
- [ ] F3-VS-03: documentar convención en `CONTRIBUTING.md` o CLAUDE.md.
- [ ] F3-VS-04 (opcional): refactorizar módulo `pacientes/` o `citas/` a Vertical Slice si el esfuerzo es bajo.

### Dependencias
- Ninguna técnica; requiere alineación de equipo.

### Riesgos
- **Resistencia cultural:** el patrón por capas es lo que la mayoría de tutoriales de Spring enseñan. Mitigar con ejemplo funcionando + documentación clara.
- **Código compartido:** si `PacienteService` es llamado por `CitaService`, hay que decidir si `citas/` depende de `pacientes/` (sí, con interfaz pública clara) o si se crea un `shared/`. Regla: dependencia hacia adentro solo por interfaces expuestas.

### Criterio de aceptación
- Los módulos nuevos de Fase 3 siguen la estructura Vertical Slice.
- Un dev del equipo puede explicar en 30 segundos dónde vive todo el código de una feature.

---

## 7. Plantillas PDF server-side (Thymeleaf/Freemarker)

**Prioridad:** ⭐⭐ · **Esfuerzo:** M · **Referencia:** pathlab

### Qué es
Los PDFs (recetas, comprobantes, constancias) se generan a partir de plantillas HTML separadas del código Java. El servicio Java solo pasa un mapa de datos; la plantilla los renderiza.

### Por qué
Alternativa mala: `StringBuilder html = new StringBuilder("<html><body>...")`. Consecuencias:
- Cambiar el logo de la clínica → recompilar y redesplegar.
- El diseñador no puede tocar el layout (necesita saber Java).
- Testing visual es imposible sin correr toda la app.

Con plantillas separadas: `resources/templates/receta.html` se edita como un archivo web normal, se previsualiza en el navegador, y el diseñador puede iterar sin tocar Java.

### Implementación en MediSuite

**Stack recomendado:**
- **Thymeleaf** (ya integrado con Spring Boot) para renderizar HTML.
- **OpenHTML2PDF** o **Flying Saucer** para convertir HTML → PDF.

**Estructura:**
```
backend/src/main/resources/templates/pdf/
├── receta.html
├── constancia.html
├── factura.html
└── partials/
    ├── header.html      # logo, datos clínica
    └── footer.html      # firma, disclaimer
```

**Ejemplo de plantilla (`receta.html`):**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><style>...CSS optimizado para print...</style></head>
<body>
    <div th:replace="~{pdf/partials/header :: header}"></div>
    <h1>Receta médica</h1>
    <p>Paciente: <span th:text="${paciente.nombreCompleto}"></span></p>
    <p>Fecha: <span th:text="${#temporals.format(receta.fecha, 'dd/MM/yyyy')}"></span></p>
    <table>
        <tr th:each="med : ${receta.medicamentos}">
            <td th:text="${med.nombre}"></td>
            <td th:text="${med.dosis}"></td>
        </tr>
    </table>
    <div th:replace="~{pdf/partials/footer :: footer}"></div>
</body>
</html>
```

**Servicio Java:**
```java
@Service
public class PdfService {
    private final SpringTemplateEngine templateEngine;

    public byte[] generarReceta(Receta receta) {
        Context ctx = new Context();
        ctx.setVariable("receta", receta);
        ctx.setVariable("paciente", receta.getPaciente());
        String html = templateEngine.process("pdf/receta", ctx);
        return htmlToPdf(html);   // OpenHTML2PDF
    }
}
```

### Tareas concretas para Fase 3
- [ ] F3-PDF-01: agregar dependencia OpenHTML2PDF.
- [ ] F3-PDF-02: crear estructura de templates y partials header/footer.
- [ ] F3-PDF-03: implementar `PdfService` genérico (`generar(templateName, model)`).
- [ ] F3-PDF-04: migrar generación de receta (si existe) a esta forma.
- [ ] F3-PDF-05: migrar constancia y factura.
- [ ] F3-PDF-06: endpoint dev `GET /dev/preview-pdf?template=receta` que renderiza HTML sin PDF (para iterar diseño).

### Dependencias
- Datos de la clínica (logo, dirección) accesibles desde tenant.

### Riesgos
- **Errores en runtime:** un typo en la plantilla (`${paciente.nombree}`) explota al generar el PDF, no al compilar. Mitigar con tests que rendericen cada plantilla con datos de ejemplo.
- **CSS de print vs pantalla:** los PDFs se ven distinto. Diseñar pensando en print (unidades en mm, evitar flexbox complejo).

### Criterio de aceptación
- Cambiar el logo o dirección de la clínica se hace editando la plantilla, sin recompilar.
- Existen tests que verifican que cada plantilla renderiza sin errores con datos de ejemplo.

---

## 8. Migraciones Flyway estrictas + `ddl-auto: validate` en producción

**Prioridad:** ⭐⭐⭐ · **Esfuerzo:** S · **Referencia:** pathlab

### Qué es
Regla de disciplina:
- **Todo cambio de esquema BD** entra como archivo Flyway numerado: `V{N}__descripcion.sql`.
- **Nunca** se modifica la BD directamente (`ALTER TABLE` en pgAdmin).
- En `application-prod.yaml`: `spring.jpa.hibernate.ddl-auto: validate` (no `update`, no `create`).

### Por qué
`ddl-auto: update` es peligroso: Hibernate infiere cambios de esquema desde las anotaciones y los aplica automáticamente. Problemas:
- El cambio no queda en el repo (no se puede revisar en PR).
- Diferencias entre dev y prod son invisibles.
- Un dev que rename una columna en la entidad puede provocar `ALTER TABLE RENAME` en prod → downtime.

Con `validate`: la app arranca comparando el esquema real vs las entidades, y **falla si no coinciden**. Fuerza a que todo cambio pase por migración explícita.

### Implementación en MediSuite

**`application-prod.yaml`:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    baseline-on-migrate: false
    locations: classpath:db/migration
```

**Convención de nombres:**
```
V1__init_schema.sql
V2__add_paciente_alergias.sql
V3__cita_constraint_no_solapada.sql
V4__audit_data_access.sql
```

**Reglas:**
- Nunca modificar una migración ya aplicada. Solo agregar nueva.
- Toda migración debe ser reversible en la práctica (aunque Flyway community no ejecuta down; se documenta el rollback en el commit).

### Tareas concretas para Fase 3
- [ ] F3-DB-01: auditar el estado actual de migraciones (¿todas están?).
- [ ] F3-DB-02: consolidar `application-{profile}.yaml` con `ddl-auto: validate` en prod y staging.
- [ ] F3-DB-03: documentar la regla en `CONTRIBUTING.md`.
- [ ] F3-DB-04: agregar CI check que corra Flyway migrate + validate en cada PR.
- [ ] F3-DB-05: policy: PRs que tocan entidades sin migración correspondiente son rechazados.

### Dependencias
- Ninguna.

### Riesgos
- **Fricción inicial:** los devs quieren editar la BD directo cuando prototipan. Solución: en dev local sí pueden, pero deben "recapturar" el cambio como migración antes de PR.
- **Migraciones fallidas en prod:** una migración mal escrita bloquea el arranque. Mitigar con testing en staging + snapshot BD antes del deploy.

### Criterio de aceptación
- El deploy a prod falla si hay diferencia entre el esquema de las entidades y el de la BD.
- Todo cambio de esquema en git tiene un archivo Flyway asociado.

---

## 9. TanStack Query + React Hook Form como stack canónico

**Prioridad:** ⭐⭐ · **Esfuerzo:** L (según estado actual) · **Referencia:** Averion, careflow

### Qué es
Adoptar dos librerías estándar del ecosistema React:

**TanStack Query** (antes React Query): manejo de estado del servidor.
- Cache automático.
- Refetch en background (útil para KPIs).
- Invalidación cruzada (crear una cita invalida el cache de "citas de hoy").
- Loading, error, retry manejados.

**React Hook Form + Zod:** manejo de formularios grandes.
- Validación tipada (Zod define el schema una vez, TS y runtime lo comparten).
- Performance (menos re-renders que Formik).
- Integración natural con componentes controlados/no-controlados.

### Por qué
Sin estas librerías, el frontend termina con:
- `useState + useEffect + fetch` esparcidos → lógica duplicada, bugs de doble-fetch, memory leaks.
- Formularios largos (expediente clínico con 30 campos) → código de 500 líneas por pantalla.
- Sin cache: cada navegación refetchea todo → UX lenta.

### Implementación en MediSuite

**Instalación:**
```bash
pnpm add @tanstack/react-query react-hook-form zod @hookform/resolvers
```

**Setup TanStack Query:**
```tsx
// main.tsx
const queryClient = new QueryClient({
  defaultOptions: { queries: { staleTime: 30_000, retry: 1 } }
});

<QueryClientProvider client={queryClient}>
  <App />
</QueryClientProvider>
```

**Ejemplo de hook para KPIs:**
```tsx
export function useKpis() {
  return useQuery({
    queryKey: ['kpis', 'dashboard'],
    queryFn: () => api.get<KpisDTO>('/dashboard/kpis'),
    refetchInterval: 60_000,   // refresh cada minuto
  });
}
```

**Ejemplo de formulario:**
```tsx
const schema = z.object({
  nombre: z.string().min(1, 'Requerido'),
  dui: z.string().regex(/^\d{8}-\d$/, 'Formato DUI inválido'),
  fechaNacimiento: z.date(),
});

function PacienteForm() {
  const { register, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema)
  });
  return <form onSubmit={handleSubmit(onSubmit)}>...</form>;
}
```

### Tareas concretas para Fase 3
- [ ] F3-RQ-01: instalar y configurar TanStack Query.
- [ ] F3-RQ-02: migrar hooks de KPIs (D-04) a `useQuery` con refetch periódico.
- [ ] F3-RQ-03: instalar RHF + Zod.
- [ ] F3-RQ-04: migrar formulario de paciente a RHF+Zod como prueba de concepto.
- [ ] F3-RQ-05: aplicar mismo patrón a formularios de cita, receta, expediente.
- [ ] F3-RQ-06: documentar patrón en README frontend.

### Dependencias
- Cliente API tipado (Mejora #1) — refuerza el valor de estos hooks.

### Riesgos
- **Sobre-caching:** cachear datos que deberían ser siempre frescos causa bugs sutiles. Regla: `staleTime` corto para datos volátiles (citas del día), largo para catálogos (medicamentos).
- **Curva de aprendizaje:** el equipo debe entender queryKey, invalidación, y mutations. Mitigar con sesión de 1h + ejemplos.

### Criterio de aceptación
- Los KPIs se refrescan automáticamente sin recargar la página.
- El formulario de paciente valida en tiempo real con mensajes de error claros.
- No hay `fetch` directo en componentes nuevos de Fase 3.

---

## 10. Roles como enum + `@PreAuthorize` declarativo

**Prioridad:** ⭐⭐⭐ · **Esfuerzo:** S · **Referencia:** pathlab, ZainAftab HMS

### Qué es
Definir los roles como enum Java en un solo lugar, y usar anotaciones de Spring Security en cada endpoint para restringir acceso. **Nunca** lógica del tipo `if (user.role.equals("DOCTOR")) { ... }` dentro del service.

### Por qué
Las reglas de acceso deben ser:
1. **Visibles:** al abrir un controller, ves de un vistazo qué roles pueden llamar cada endpoint.
2. **Centralizadas:** cambiar los permisos de "asistente" se hace en un archivo.
3. **Auditables:** un revisor puede validar el modelo de seguridad sin leer todos los services.

Con lógica esparcida en services, la auditoría es imposible y los bugs de autorización se cuelan (endpoint nuevo que olvida verificar rol).

### Implementación en MediSuite

**Enum:**
```java
public enum Rol {
    ADMIN,       // gestión del tenant
    DOCTOR,      // atención clínica
    ASISTENTE,   // agenda, recepción
    PACIENTE;    // portal paciente

    public String authority() { return "ROLE_" + name(); }
}
```

**Habilitar Method Security:**
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig { ... }
```

**Uso en controllers:**
```java
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ASISTENTE', 'ADMIN')")
    public List<PacienteDTO> listar() { ... }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'ASISTENTE')")
    public PacienteDTO crear(@RequestBody CrearPacienteDTO dto) { ... }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable UUID id) { ... }

    @GetMapping("/mio")
    @PreAuthorize("hasRole('PACIENTE')")
    public PacienteDTO miPerfil() { ... }
}
```

**Casos complejos (owner-based):**
```java
@GetMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or @pacienteAccessGuard.puedeVer(#id, authentication)")
public PacienteDTO obtener(@PathVariable UUID id) { ... }
```
El `pacienteAccessGuard` es un bean que encapsula la lógica ("un doctor solo ve sus pacientes"). La regla queda en un solo lugar reutilizable.

### Tareas concretas para Fase 3
- [ ] F3-SEC-01: consolidar enum `Rol` y asegurarse que el JWT lo trae correctamente (`ROLE_DOCTOR` etc).
- [ ] F3-SEC-02: agregar `@PreAuthorize` en todos los endpoints existentes.
- [ ] F3-SEC-03: crear guards para reglas owner-based (paciente ve solo lo suyo, doctor ve solo sus pacientes).
- [ ] F3-SEC-04: test de seguridad automatizado por endpoint: para cada rol, verificar 200 o 403 esperado.
- [ ] F3-SEC-05: documentar matriz "endpoint × rol → permitido/denegado" en `docs/security-matrix.md`.

### Dependencias
- JWT filter (VG-08) debe estar completo.
- Spring Security bien configurado.

### Riesgos
- **Errores tipográficos en anotaciones:** `hasRole('DOCTR')` fallará en runtime silenciosamente. Mitigar con tests de seguridad.
- **Reglas complejas se vuelven ilegibles en SpEL:** cuando la expresión crezca, mover a bean (`@pacienteAccessGuard`).

### Criterio de aceptación
- Cada endpoint tiene una anotación de autorización explícita.
- Existe una matriz documentada de "quién puede hacer qué".
- Test automatizado que valida la matriz.

---

## Resumen y priorización sugerida para Fase 3

| # | Mejora | Prioridad | Esfuerzo |
|---|---|---|---|
| 8 | Flyway estricto + `validate` en prod | ⭐⭐⭐ | S |
| 10 | Roles + `@PreAuthorize` | ⭐⭐⭐ | S |
| 1 | Contratos OpenAPI → TS | ⭐⭐⭐ | S |
| 2 | Audit-log de accesos | ⭐⭐⭐ | M |
| 3 | Multi-tenant transversal | ⭐⭐⭐ | L |
| 4 | Constraint de agenda BD | ⭐⭐ | M |
| 7 | Plantillas PDF | ⭐⭐ | M |
| 9 | TanStack Query + RHF | ⭐⭐ | L |
| 5 | Portales por rol frontend | ⭐⭐ | L |
| 6 | Vertical Slice backend | ⭐⭐ | L |

**Orden recomendado de ataque (primeros días de Fase 3):**
1. Consolidar bases: #8 (Flyway), #10 (Roles), #1 (OpenAPI). Todos son S y desbloquean el resto.
2. Estructura de seguridad: #2 (Audit), #3 (Multi-tenant filter). Cuidan el activo más crítico (datos de paciente).
3. Funcional: #4 (Agenda), #7 (PDFs). Requeridos por features de Fase 3.
4. Refactor grande: #9, #5, #6. Son mejoras de arquitectura, se hacen en paralelo por distintos integrantes del equipo.

## Cómo usar este documento en Fase 3

Al arrancar Fase 3 (semana del 26/10/2026):
1. Revisar este documento en la primera reunión de planificación.
2. Convertir cada tarea `F3-XXX-YY` en tarea de Planner.
3. Asignar por integrante según fortalezas.
4. Actualizar este archivo con notas de implementación real (lo que funcionó, lo que se ajustó).

---

**Referencias externas revisadas** (repos completos en `_research/repos-referencia.md`):
- github.com/mohammadumar-dev/pathlab
- github.com/xinyiklin/careflow
- github.com/Imhotep-Tech/imhotep_smart_clinic
- github.com/ayushirathour/The-Averion-Labs-Front-End
- github.com/yagizuygarunlu/MyDoctorApp
- github.com/ZainAftab-dev/hospital-management-system
