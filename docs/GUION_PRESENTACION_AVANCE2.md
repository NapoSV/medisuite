# Guion — Presentación Avance 2 MediSuite
**Cátedra:** Programación II · Ciclo 1, 2026 · Universidad Evangélica de El Salvador  
**Proyecto:** MediSuite — Sistema de gestión clínica multi-tenant  
**Entrega:** Avance 2 · Tag `v2.0.0-avance2`  
**Duración estimada:** 25–28 minutos  
**Herramientas:** IntelliJ IDEA (o VS Code) + navegador con sistema corriendo en `http://localhost:5173`

---

## Orden de participaciones

| # | Integrante | Sección | Tiempo |
|---|---|---|---|
| 1 | Héctor López | Introducción + Arquitectura general | ~3 min |
| 2 | Héctor López | Modelo de dominio: herencia, encapsulamiento, abstracción | ~3 min |
| 3 | Alejandro Vigil | Seguridad: JWT, blacklist, rate limiting | ~3 min |
| 4 | Alejandro Merino | Concurrencia, hilos, archivos .dat | ~3 min |
| 5 | Bayron Orellana | Capa de persistencia: repositorios, interfaces, polimorfismo | ~2 min |
| 6 | Carlos Ventura | Expediente clínico: signos vitales, Map, Stream | ~2 min |
| 7 | Zair Díaz | Frontend: listado y gestión de pacientes y doctores | ~2 min |
| 8 | William Melgar | Frontend: recetas médicas y vista imprimible | ~2 min |
| 9 | Nicole Sánchez | Pruebas unitarias: JUnit 5 + Mockito | ~2 min |
| 10 | Erika Fuentes | QA: estados loading/error y flujo de autenticación | ~1 min |
| 11 | Walter Vásquez | QA documental y verificación de sistema | ~1 min |
| 12 | Walter Flores | Base de datos: migraciones Flyway y seguridad de contraseñas | ~1 min |
| 13 | Héctor López | Demo en vivo + cierre y conclusiones | ~3 min |

---

---

## PARTE 1 — Introducción y arquitectura general
**Participante: Héctor Napoleón López Ruiz**

### Qué decir

Buenas tardes. Soy Héctor López, project manager y arquitecto del equipo. Hoy les presentamos el Avance 2 de MediSuite, un sistema de gestión clínica desarrollado con Java 21 y Spring Boot en el backend y React con TypeScript en el frontend.

El objetivo del sistema es administrar múltiples clínicas desde una sola plataforma. A esto le llamamos arquitectura **multi-tenant**: cada clínica es un tenant independiente, y los datos de un tenant nunca son visibles para otro. Durante la presentación verán cómo este concepto atraviesa absolutamente todo el código.

Permítanme mostrar la estructura del proyecto.

*[Abrir IntelliJ. Mostrar el árbol de carpetas del proyecto.]*

El proyecto se divide en dos módulos principales. El **backend** está en la carpeta `backend/`, escrito en Java 21 con Spring Boot 3.3.2. Aquí viven las entidades de dominio, los servicios de negocio, los controladores REST y el módulo de seguridad. El **frontend** está en `frontend/`, construido con React 18 y TypeScript. Ambos se empaquetan y despliegan juntos mediante Docker Compose.

El punto de entrada a la JVM es la clase `MediSuiteApplication.java`. Cuando ejecutamos `java -jar medisuite.jar`, la JVM carga esta clase, encuentra el método `main`, y Spring Boot arranca su contenedor de inversión de control. Desde ese momento, la JVM gestiona la memoria automáticamente mediante el **Garbage Collector**: los objetos que ya no tienen referencias son eliminados sin que nosotros tengamos que liberarlos manualmente, a diferencia de lenguajes como C++.

En el `pom.xml` pueden ver que configuramos `<java.version>21</java.version>`, lo que le indica a Maven que compile con el JDK 21 y que la JVM objetivo sea compatible con esa versión.

### Qué mostrar en pantalla
- Árbol de carpetas en IntelliJ: `backend/src/main/java/...` y `frontend/src/`
- Archivo `MediSuiteApplication.java` — el método `main`
- Archivo `pom.xml` — línea `<java.version>21</java.version>`

### Conceptos cubiertos
- Entorno de desarrollo Java: JDK, JVM, Maven como build tool
- Manejo de memoria: Garbage Collector, ciclo de vida de objetos

---

## PARTE 2 — Modelo de dominio: herencia, encapsulamiento y abstracción
**Participante: Héctor Napoleón López Ruiz**

### Qué decir

Ahora voy a mostrar el corazón del modelo de dominio. Abran el archivo `BaseEntity.java`.

*[Abrir `backend/src/main/java/com/sv/grupo7/medisuite/model/BaseEntity.java`]*

Esta es la clase más importante de todo el backend. Es una **clase abstracta** — no se puede instanciar directamente — y está anotada con `@MappedSuperclass`, lo que le dice a JPA que sus campos deben ser heredados como columnas de base de datos por todas las subclases.

```java
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", insertable = false, updatable = false)
    private Long tenantId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
```

Aquí vemos varios conceptos juntos. Primero, **herencia**: todas las entidades del sistema — `Patient`, `Doctor`, `Appointment`, `MedicalRecord`, `VitalSign`, `Prescription` — extienden `BaseEntity`. Esto significa que heredan automáticamente el `id`, el `tenantId`, el `createdAt` y el `updatedAt`. No repetimos esos campos en ninguna entidad. Esta es la relación `Object → BaseEntity → Patient`, la jerarquía de clases de Java.

Segundo, **encapsulamiento**: todos los campos son `private`. Las anotaciones `@Getter` y `@Setter` de la librería Lombok generan automáticamente los métodos `getId()`, `setId()`, `getTenantId()`, etc. El campo nunca se accede directamente desde afuera; siempre pasa por los accesores.

Tercero, **sobrescritura con `@Override` implícito**: el método `@PrePersist` de `BaseEntity` establece `createdAt` y `updatedAt`. Pero en `Prescription.java` existe también un `@PrePersist` que establece `issuedOn`. En la jerarquía de herencia, JPA ejecuta ambos hooks. Miren la línea 45 de `Prescription.java`:

```java
@PrePersist
void prePersistPrescription() {
    if (issuedOn == null) issuedOn = LocalDate.now();
}
```

Esto es **polimorfismo por sobrescritura**: el comportamiento al persistir cambia según la clase concreta.

Cuarto, **tipos de datos**. En `BaseEntity` vemos `Long id` — un tipo de referencia wrapper, necesario porque JPA requiere que el ID pueda ser `null` cuando el objeto aún no se ha guardado. Si usáramos el primitivo `long`, nunca podría ser null y JPA no sabría distinguir entre "objeto nuevo" y "objeto con ID 0". En `VitalSign.java` vemos `BigDecimal temperatureC` — se usa `BigDecimal` en vez de `double` para evitar errores de punto flotante en datos médicos donde la precisión es crítica.

Finalmente, el uso de `this` y `super`: en `BusinessException.java`:

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);         // llama al constructor de RuntimeException
    }
}
```

`super(message)` llama al constructor de la superclase `RuntimeException`, pasándole el mensaje. Este es el mecanismo de constructores con herencia.

### Qué mostrar en pantalla
- `BaseEntity.java` completo — resaltar línea 14 (`abstract`), líneas 36-40 (`@PrePersist`/`@PreUpdate`)
- `Prescription.java` — resaltar líneas 17 (`extends BaseEntity`) y 45-48 (`@PrePersist`)
- `Patient.java` — resaltar campos: `String firstName`, `LocalDate birthDate`, `String bloodType`
- `BusinessException.java` completo — resaltar `super(message)`
- Mostrar en el árbol del proyecto que Patient, Doctor, Appointment, MedicalRecord, VitalSign, Nurse, Receptionist, Administrator, Specialty, Tenant, AuditLog — todos dicen `extends BaseEntity`

### Conceptos cubiertos
- Clases abstractas (`abstract class BaseEntity`)
- Herencia (`extends BaseEntity` en 11 clases)
- Sobrescritura de métodos (`@Override` / `@PrePersist` en subclases)
- Polimorfismo por sobrescritura
- Encapsulamiento (`private` + `@Getter @Setter`)
- Tipos de datos primitivos vs referencia (`Long` vs `long`, `BigDecimal`)
- Constructores y `super()`
- `this` y `super`
- Jerarquía de clases: `Object → BaseEntity → [todas las entidades]`
- Principio SOLID Open/Closed: `BaseEntity` no se modifica al agregar nuevas entidades

---

## PARTE 3 — Seguridad: JWT, blacklist y rate limiting
**Participante: Alejandro Antonio Vigil Ramírez**

### Qué decir

Gracias Héctor. Soy Alejandro Vigil, y mi responsabilidad en el Avance 2 fue el módulo de seguridad. Voy a mostrar tres componentes: el proveedor de tokens JWT, la lista negra de tokens revocados, y el filtro de límite de intentos de login.

*[Abrir `JwtTokenProvider.java`]*

JWT significa JSON Web Token. Es un estándar que permite autenticar usuarios sin guardar sesiones en el servidor. Cuando un usuario hace login, el sistema genera un token firmado digitalmente. En cada petición posterior, el cliente envía ese token y el servidor verifica la firma para saber quién es el usuario.

Observen el constructor de esta clase, líneas 21 a 35:

```java
public JwtTokenProvider(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-ms}") long expirationMs) {
    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < MIN_SECRET_BYTES) {
        throw new IllegalStateException("app.jwt.secret debe tener al menos 32 bytes...");
    }
    this.signingKey = Keys.hmacShaKeyFor(secretBytes);
    this.expirationMs = expirationMs;
}
```

Este es un **constructor con parámetros** que usa `@Value` para inyectar configuración del archivo `.env`. Si el secret es demasiado corto lanza una excepción — validación en el inicio del sistema. El método `generateToken()` en línea 37 genera el token con `userId`, `role` y `tenant_id` embebidos como claims.

*[Abrir `JwtAuthenticationFilter.java`]*

Este filtro extiende `OncePerRequestFilter`, que es una clase abstracta de Spring. El método `doFilterInternal()` en línea 28 es el método abstracto que nosotros sobrescribimos — aquí está el polimorfismo: Spring llama a este método en cada petición HTTP sin saber qué implementación concreta está usando.

```java
@Override
protected void doFilterInternal(HttpServletRequest req, ...) {
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        Claims claims = tokenProvider.parse(token);
        Long tenantId = claims.get("tenant_id", Long.class);
        TenantContext.set(tenantId);       // ← propagación del tenant al hilo
    }
    chain.doFilter(req, res);
    // finally:
    TenantContext.clear();                 // ← limpieza del ThreadLocal
}
```

Noten que en el bloque `finally` siempre se llama `TenantContext.clear()`. Esto es fundamental para la seguridad multi-tenant: si no limpiamos el `ThreadLocal`, el siguiente request que use ese mismo hilo del pool de Tomcat podría ver el tenant incorrecto.

*[Abrir `JwtBlacklist.java`]*

Para el cierre de sesión, implementamos una lista negra de tokens en línea 9:

```java
private final Set<String> revoked = ConcurrentHashMap.newKeySet();
```

Usamos `ConcurrentHashMap.newKeySet()` — una **colección thread-safe**. A diferencia de un `HashSet` normal, esta estructura permite que múltiples hilos de Tomcat la lean y modifiquen simultáneamente sin corrupción de datos. Este es un ejemplo concreto de sincronización con estructuras de datos concurrentes.

*[Abrir `RateLimitFilter.java`]*

Finalmente, el filtro de rate limiting en línea 21 usa otro `ConcurrentHashMap` para guardar un "bucket" por IP. Si una IP intenta hacer más de 5 logins por minuto, recibe un error 429. En la línea 37: `buckets.computeIfAbsent(ip, k -> newBucket())` — el método `computeIfAbsent` es thread-safe y crea el bucket si no existe, de forma atómica.

### Qué mostrar en pantalla
- `JwtTokenProvider.java` — constructor (líneas 21-35) y `generateToken()` (líneas 37-49)
- `JwtAuthenticationFilter.java` — `doFilterInternal()` completo, resaltar `TenantContext.set()` y `TenantContext.clear()`
- `JwtBlacklist.java` completo — resaltar `ConcurrentHashMap.newKeySet()`
- `RateLimitFilter.java` — resaltar línea 21 y línea 37

### Conceptos cubiertos
- Constructores con parámetros e inicialización con validación
- Sobrescritura de métodos abstractos (`@Override doFilterInternal`)
- Polimorfismo: `OncePerRequestFilter` abstracta → `JwtAuthenticationFilter` concreta
- `HashSet` vs `ConcurrentHashMap.newKeySet()` — thread safety
- `HashMap` → `ConcurrentHashMap` para acceso concurrente
- Enlace dinámico (Dynamic Binding): Spring invoca el método concreto en runtime
- Modificadores de acceso: `private final`, `public`, `protected`

---

## PARTE 4 — Concurrencia, hilos y archivos .dat
**Participante: Alejandro Sebastián Merino Ventura**

### Qué decir

Soy Alejandro Merino. Yo implementé el sistema de backup en archivos `.dat` y el módulo de métricas del dashboard. Estos son los componentes donde más se aplican los conceptos de programación concurrente.

*[Abrir `DatFileDao.java`]*

Esta es una **clase abstracta genérica**: `DatFileDao<T extends Serializable>`. La sintaxis `<T>` es un tipo genérico — `T` puede ser cualquier clase que implemente `Serializable`. Esto nos permite tener un DAO reutilizable para cualquier tipo de objeto que queramos guardar en disco.

En la línea 11: `private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock()`. Un `ReentrantReadWriteLock` es un mecanismo de sincronización que distingue entre lecturas y escrituras. Múltiples hilos pueden leer al mismo tiempo — método `loadAll()`, línea 30: `lock.readLock().lock()`. Pero solo un hilo puede escribir — método `save()`, línea 20: `lock.writeLock().lock()`. Esto previene una **condición de carrera** (race condition): si dos hilos escribieran el archivo simultáneamente, el archivo quedaría corrompido.

El patrón `try/finally` con el lock es fundamental. En la línea 25 y 38 siempre hay `finally { lock.unlock() }`. Si ocurre una excepción dentro del `try`, el `finally` garantiza que el lock se libere. Si no hiciéramos esto, los demás hilos quedarían bloqueados para siempre — esto se llama **deadlock**.

En el método `save()` usamos `ObjectOutputStream`, que serializa una `List<T>` completa a bytes y la escribe en el archivo `.dat`. En `loadAll()` usamos `ObjectInputStream` para deserializar — reconstruir los objetos desde los bytes. Este es el mecanismo de **entrada y salida de datos** más avanzado de Java: I/O binaria con serialización de objetos.

*[Abrir `AuditBackupScheduler.java`]*

Esta clase orquesta los backups automáticos. En la línea 27:

```java
executor = Executors.newSingleThreadScheduledExecutor(r -> {
    Thread t = new Thread(r, "audit-backup");
    t.setDaemon(true);
    return t;
});
```

Aquí creamos un `ScheduledExecutorService` — un pool de hilos programables. La lambda `r -> { ... }` es una **ThreadFactory**: una interfaz funcional que actúa como fábrica de hilos. El parámetro `r` es un `Runnable` — la interfaz que define la tarea a ejecutar. Lo envolvemos en un `Thread` al que le damos el nombre `"audit-backup"` para identificarlo en herramientas de monitoreo. `setDaemon(true)` significa que este hilo no impide que la JVM se cierre.

En la línea 32: `executor.scheduleAtFixedRate(this::backup, 60, 60, TimeUnit.SECONDS)`. Esto programa la ejecución del método `backup()` cada 60 segundos. `this::backup` es una referencia a método — equivale a `() -> this.backup()`. Es la forma moderna de pasar un `Runnable` sin crear una clase anónima.

La anotación `@PostConstruct` en línea 25 indica que Spring llame a `start()` inmediatamente después de construir el bean. `@PreDestroy` en línea 46 llama a `stop()` cuando la aplicación se cierra, invocando `executor.shutdown()` para terminar el pool ordenadamente.

*[Abrir `DashboardMetricsService.java`]*

Este servicio calcula las 4 métricas del dashboard. Las 4 consultas a la base de datos son independientes entre sí — no tiene sentido ejecutarlas una por una. Por eso usamos **`CompletableFuture`**, líneas 26-37:

```java
var f1 = CompletableFuture.supplyAsync(() -> appointments.countByDateAndTenant(...));
var f2 = CompletableFuture.supplyAsync(() -> patients.countActiveByTenant(...));
var f3 = CompletableFuture.supplyAsync(() -> prescriptions.count());
var f4 = CompletableFuture.supplyAsync(() -> audit.count());
CompletableFuture.allOf(f1, f2, f3, f4).join();
```

Cada `supplyAsync` lanza su tarea en el pool de hilos del sistema de forma asíncrona. `allOf(...).join()` espera que las 4 terminen. En lugar de esperar 4 veces el tiempo de una consulta, esperamos el tiempo de la consulta más lenta. Esto es el patrón **Fork/Join** — dividir trabajo en paralelo y reunir los resultados.

### Qué mostrar en pantalla
- `DatFileDao.java` completo — resaltar línea 8 (clase abstracta genérica), línea 11 (lock), líneas 19-26 (save con writeLock), líneas 29-39 (loadAll con readLock)
- `AuditLogDatDao.java` — resaltar línea 16 `extends` implícita (usa ObjectOutputStream directamente)
- `AuditBackupScheduler.java` — resaltar líneas 27-31 (ThreadFactory + Thread + Runnable), línea 32 (scheduleAtFixedRate)
- `DashboardMetricsService.java` — resaltar líneas 26-38 (CompletableFuture paralelas + allOf)

### Conceptos cubiertos
- Clases abstractas con genéricos: `DatFileDao<T extends Serializable>`
- Interfaces: `Serializable` (para I/O binaria), `Runnable` (para hilos)
- Creación de hilos: `new Thread(r, "audit-backup")`
- `ExecutorService` y `ScheduledExecutorService`
- Sincronización: `ReentrantReadWriteLock` — múltiples lectores, un escritor
- Race conditions y deadlocks — cómo se previenen
- Entrada/salida binaria: `ObjectOutputStream` / `ObjectInputStream`
- `CompletableFuture` — tareas asíncronas con retorno
- `@PostConstruct` / `@PreDestroy` — ciclo de vida de beans Spring
- Principio SOLID SRP: `AuditBackupScheduler` solo hace backups

---

## PARTE 5 — Capa de persistencia: repositorios, interfaces y polimorfismo
**Participante: Bayron Alexander Orellana Rojas**

### Qué decir

Soy Bayron Orellana y mi trabajo fue el módulo de pacientes y especialidades en el backend. Voy a mostrar cómo funcionan los repositorios y por qué representan un ejemplo puro de interfaces y polimorfismo.

*[Abrir `PatientRepository.java`]*

```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByTenantId(Long tenantId, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.tenantId = :tid AND ...")
    Page<Patient> searchByDuiOrName(@Param("tid") Long tid,
                                    @Param("q") String q, Pageable pageable);
}
```

`PatientRepository` es una **interfaz** — no tiene ninguna implementación. Solo declara los contratos de los métodos. En tiempo de ejecución, Spring Data JPA genera automáticamente una clase concreta que implementa esta interfaz. Esto es polimorfismo puro: `PatientService` recibe un `PatientRepository` (la interfaz), pero en runtime trabaja con el proxy generado por Spring. El servicio no sabe — ni necesita saber — cuál es la implementación concreta. Esto se llama **enlace dinámico (Dynamic Binding)**.

`JpaRepository<Patient, Long>` ya trae implementados más de 15 métodos: `save()`, `findById()`, `findAll()`, `delete()`, `count()`, etc. Nosotros solo añadimos los métodos específicos del negocio.

*[Abrir `PatientService.java`]*

En el método `search()`, líneas 18-23:

```java
@Transactional(readOnly = true)
public Page<Patient> search(String query, Pageable pageable) {
    Long tid = TenantContext.currentTenantId();
    if (query == null || query.isBlank()) return repo.findByTenantId(tid, pageable);
    String q = query.replace("-", "").toLowerCase();
    return repo.searchByDuiOrName(tid, q, pageable);
}
```

Aquí vemos un `if/else` para decidir qué consulta ejecutar según si hay texto de búsqueda. Si el DUI llega con guion `"06123456-7"`, lo normalizamos quitando el guion con `replace("-", "")`. Esto demuestra el uso de **operadores de comparación** (`==`, `isBlank()`) y **operadores de cadenas**. El `pageable` controla la paginación — cuántos registros devolver y en qué página, sin traer toda la tabla a memoria.

En el método `update()`, líneas 41-49, vemos el uso de los **setters** heredados de `BaseEntity` a través de `Patient`: `p.setFirstName(data.getFirstName())`. Los getters y setters generados por Lombok siguen la convención camelCase de Java — `firstName` → `getFirstName()` / `setFirstName()`.

### Qué mostrar en pantalla
- `PatientRepository.java` completo
- `PatientService.java` — métodos `search()` (líneas 17-23) y `update()` (líneas 41-49)
- Mostrar en IntelliJ el menú "Go to Implementation" sobre `PatientRepository` para ver el proxy generado por Spring

### Conceptos cubiertos
- Interfaces como contratos: `PatientRepository extends JpaRepository`
- Polimorfismo: Spring inyecta implementación concreta para una interfaz
- Enlace dinámico (Dynamic Binding)
- Implementación múltiple de interfaces
- Anatomía de métodos: modificadores, retorno, nombre, parámetros
- Operadores relacionales y de cadenas
- Estructuras de control: `if/else`
- Convenciones camelCase (getters/setters)
- SOLID LSP: `PatientRepository` puede usarse donde se espera `JpaRepository<Patient, Long>`

---

## PARTE 6 — Expediente clínico: signos vitales, Map y Stream
**Participante: Carlos Mario Ventura Velásquez**

### Qué decir

Soy Carlos Ventura. Yo trabajé en el módulo de expediente clínico y signos vitales, que es donde más se usan colecciones y la API de Streams.

*[Abrir `MedicalRecordService.java`]*

El método `findFullByPatientId()`, línea 27, devuelve `Map<String, Object>`. Un `Map` es una colección de **pares clave-valor** — como un diccionario. Usamos `Map.of()` en la línea 42:

```java
return Map.of(
    "id",           mr.getId(),
    "patient",      mr.getPatient(),
    "generalNotes", mr.getGeneralNotes() == null ? "" : mr.getGeneralNotes(),
    "prescriptions", prescriptions,
    "vitalSigns",   vitalSigns
);
```

Las claves son `String` y los valores son de tipos distintos: `Long`, `Patient`, `String`, `List`. Esto es un `HashMap` implícito creado con la fábrica `Map.of()`. Jackson serializa este mapa directamente a JSON para enviarlo al frontend.

*[Abrir `AppointmentService.java`, método `availableSlots()`]*

```java
public List<OffsetDateTime> availableSlots(Long doctorId, LocalDate date) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.of("-06:00"));
    List<OffsetDateTime> all = new ArrayList<>();
    for (int h = 8; h < 17; h++) {
        all.add(date.atTime(h, 0).atOffset(ZoneOffset.of("-06:00")));
    }
    return all.stream()
        .filter(s -> s.isAfter(now) &&
            !appointmentRepo.existsByDoctorIdAndScheduledAtAndStatusNot(doctorId, s, "CANCELLED"))
        .toList();
}
```

Primero, un `ArrayList` — lista dinámica indexada — que acumula todos los horarios posibles del día (8AM a 5PM). El bucle `for` con variable de control `h` recorre los enteros del 8 al 16.

Luego, la **API Stream**: `.stream()` convierte la lista en un flujo. `.filter()` aplica un predicado — una función `boolean` — a cada elemento. Solo pasan los horarios que aún no han ocurrido (`s.isAfter(now)`) Y que el doctor no tiene ocupados. El operador lógico `&&` combina ambas condiciones. `.toList()` recolecta los resultados de vuelta en una lista inmutable.

Esto es equivalente a un bucle con `if` interno, pero más expresivo y componible.

En el `record VitalSignRequest` (línea 51), usamos un **Java Record** — introducido en Java 16 — que es una clase inmutable con constructor, getters y `equals/hashCode` generados automáticamente. Es la forma moderna de los DTOs (Data Transfer Objects).

### Qué mostrar en pantalla
- `MedicalRecordService.java` — método `findFullByPatientId()` (líneas 27-49), resaltar `Map.of()`
- `AppointmentService.java` — método `availableSlots()` (líneas 102-110), resaltar `new ArrayList<>()`, bucle `for`, `.stream().filter()`
- `MedicalRecordService.java` — `record VitalSignRequest` (líneas 51-59)

### Conceptos cubiertos
- `ArrayList`: lista dinámica indexada
- `HashMap` implícito via `Map.of()`: estructura clave-valor
- Stream API: `.stream()`, `.filter()`, `.toList()`
- Bucle `for` clásico con variable de control
- Operadores lógicos: `&&`, `!`
- Operadores relacionales: `isAfter()`, `existsByDoctorIdAndScheduledAtAndStatusNot()`
- Java Records como constructores compactos e inmutables
- `List` con `@OrderColumn` en `Prescription.java`: orden preservado para impresión

---

## PARTE 7 — Frontend: gestión de pacientes y doctores
**Participante: Zair Benett Díaz Santos**

### Qué decir

Soy Zair Díaz y me encargué del frontend de gestión de pacientes y doctores. Voy a mostrar las pantallas y cómo se conectan con el backend.

*[Abrir el navegador en `http://localhost:5173`. Loguearse como Admin.]*

*[Navegar a la sección Pacientes]*

Esta es la vista de pacientes. Al cargar, hace una petición `GET /api/patients` al backend con paginación. La búsqueda en tiempo real filtra por nombre o DUI — el DUI puede escribirse con o sin guion y el backend lo normaliza.

*[Abrir `Pacientes.tsx` en el editor]*

En TypeScript, definimos la interfaz `Patient` para tipificar los datos que llegan del backend:

```typescript
type Patient = {
  id: number; firstName: string; lastName: string; dui: string;
  birthDate?: string; phone?: string; address?: string;
}
```

Esta es la analogía de las clases de Java aplicada al frontend: definimos un contrato de forma con TypeScript. Los campos opcionales (`?`) equivalen a los que pueden ser `null` en Java.

El componente `PatientModal.tsx` abre un formulario para crear o editar pacientes. Cuando editamos, se hace un `GET /api/patients/:id` para precargar los datos en el formulario con `useEffect` y `reset()` de react-hook-form. El botón guardar hace `PUT /api/patients/:id` con los datos actualizados.

*[Navegar a Doctores]*

La vista de doctores permite ver, crear y editar doctores. Cada doctor tiene asociada una especialidad — esta es una relación de **asociación** entre entidades: el `Doctor` tiene una referencia a `Specialty`, pero ambos pueden existir independientemente.

### Qué mostrar en pantalla
- Navegador: lista de pacientes con búsqueda activa
- `Pacientes.tsx` — definición de tipo `Patient`, función `calcAge()`, tabla con columnas
- `PatientModal.tsx` — `useEffect` que precarga datos en el formulario
- Navegador: lista de doctores con su especialidad

### Conceptos cubiertos
- Abstracción en TypeScript: tipos e interfaces como contratos
- Estructuras de control: `if` en `calcAge()`, filtros con `useMemo`
- Relaciones UML: Asociación (`Doctor` → `Specialty`)
- Encapsulamiento: componentes React como unidades encapsuladas
- Convenciones camelCase en TypeScript

---

## PARTE 8 — Frontend: recetas médicas y vista imprimible
**Participante: William Ariel Melgar Rivas**

### Qué decir

Soy Ariel Melgar. Yo implementé el módulo de recetas médicas desde el frontend, incluyendo la vista imprimible en formato A4.

*[Navegar al expediente de un paciente que tenga receta]*

Esta es la vista del expediente clínico. El historial muestra dos tipos de eventos en orden cronológico: recetas médicas (en azul) y signos vitales (en verde). Esta es una **lista dinámica** — se construye concatenando dos arrays, ordenándolos por fecha, y renderizando un componente distinto según el tipo de evento. Es un ejemplo de **polimorfismo en el frontend**: el mismo código de renderizado maneja tipos diferentes.

*[Hacer clic en el ícono de imprimir de una receta]*

*[Abrir `RecetaPrint.tsx`]*

La vista de impresión usa CSS `@media print` para ocultar toda la navegación y mostrar solo el contenido de la receta en formato A4. El botón "Imprimir" llama a `window.print()` — la API del navegador para imprimir. Esto demuestra la integración con APIs del navegador desde TypeScript.

*[Abrir `Recetas.tsx`]*

El formulario de nueva receta permite agregar múltiples medicamentos dinámicamente. Cada ítem tiene `medication`, `dosage` y `frequency`. La lista de ítems es un **array de objetos** en el estado de React — equivalente al `List<PrescriptionItem>` del backend. Al enviar, se hace `POST /api/prescriptions` con toda la receta y sus ítems.

*[Mostrar `LoadingSpinner.tsx`]*

El componente `LoadingSpinner` es reutilizado por múltiples páginas para mostrar el estado de carga. Es un ejemplo del **Principio de Responsabilidad Única** (SOLID SRP) aplicado al frontend: un componente hace una sola cosa y se puede reutilizar en cualquier lugar.

### Qué mostrar en pantalla
- Navegador: expediente clínico con timeline de eventos
- `RecetaPrint.tsx` — resaltar `@media print` en CSS, `window.print()`
- `Recetas.tsx` — formulario con items dinámicos, envío al backend
- `LoadingSpinner.tsx` — componente de una sola responsabilidad

### Conceptos cubiertos
- `ArrayList` equivalente en frontend: array de ítems de receta
- Estructuras de control: `.map()` para renderizar listas
- Polimorfismo en frontend: mismo renderer para tipos distintos
- SOLID SRP: `LoadingSpinner` como componente de una sola responsabilidad
- `@OrderColumn` del backend reflejado en orden de items en el frontend

---

## PARTE 9 — Pruebas unitarias: JUnit 5 y Mockito
**Participante: Nicole Nohemy Sánchez Menjívar**

### Qué decir

Soy Nicole Sánchez y me encargué de las pruebas unitarias del sistema. Voy a mostrar los tests de `PatientService`.

*[Abrir `PatientServiceTest.java`]*

Las pruebas unitarias verifican que cada componente funcione correctamente de forma aislada, sin depender de la base de datos ni de otros servicios. Para esto usamos **JUnit 5** como framework de pruebas y **Mockito** para crear objetos simulados.

La anotación `@ExtendWith(MockitoExtension.class)` en línea 23 integra Mockito con JUnit 5. `@Mock` en línea 27 crea un `PatientRepository` simulado — un objeto que imita la interfaz real pero no hace consultas reales. `@InjectMocks` en línea 30 crea un `PatientService` real con el mock inyectado.

Miren el test en línea 35:

```java
@Test
void search_sinQuery_debeRetornarPacientesDelTenant() {
    try (MockedStatic<TenantContext> ctx = mockStatic(TenantContext.class)) {
        ctx.when(TenantContext::currentTenantId).thenReturn(TENANT_ID);
        when(repo.findByTenantId(TENANT_ID, pageable)).thenReturn(page);

        Page<Patient> result = patientService.search("", pageable);

        assertEquals(1, result.getTotalElements());
        verify(repo).findByTenantId(TENANT_ID, pageable);
    }
}
```

Primero configuramos el comportamiento simulado: `when(repo.findByTenantId(...)).thenReturn(page)` — "cuando se llame a este método con estos argumentos, devuelve este resultado". Luego llamamos al método real. Finalmente verificamos con `assertEquals` y `verify`.

`MockedStatic` en línea 40 permite mockear métodos estáticos — necesario para `TenantContext.currentTenantId()` que es un método estático. Esta es la parte avanzada: simular el **static binding** de Java.

El test en línea 79 prueba el caso negativo: cuando el paciente no existe, debe lanzarse una `RuntimeException` con el mensaje correcto. `assertThrows` captura la excepción y `assertEquals` verifica el mensaje. Esto garantiza que el manejo de errores funciona como se diseñó.

Tenemos 5 tests que cubren: búsqueda sin query, búsqueda con query y normalización de DUI, búsqueda de paciente existente, paciente no existente, y actualización de datos.

### Qué mostrar en pantalla
- `PatientServiceTest.java` completo
- Resaltar: `@Mock`, `@InjectMocks`, `MockedStatic`, `when/thenReturn`, `assertEquals`, `verify`, `assertThrows`
- Ejecutar los tests en IntelliJ: clic derecho → Run `PatientServiceTest` → mostrar barra verde

### Conceptos cubiertos
- Interfaces (Mockito las usa para crear proxies de mocks)
- Polimorfismo: `@Mock PatientRepository` es una implementación proxy de la interfaz
- Enlace estático vs dinámico: `MockedStatic` para métodos estáticos
- `ArrayList` implícito: `List.of(patient)` en los test data
- Estructuras de control: `if` en el servicio verificado por los tests
- `HashMap` en `GlobalExceptionHandler` para respuestas de error

---

## PARTE 10 — QA: estados de carga, error y autenticación
**Participante: Erika Alexandra Fuentes Ortiz**

### Qué decir

Soy Erika Fuentes y mi rol fue QA y verificación de experiencia de usuario. Me encargué de que todas las pantallas manejen correctamente los estados de carga, error y vacío.

*[Navegar por el sistema mostrando diferentes estados]*

Cuando una petición está en curso, el usuario ve el `LoadingSpinner`. Si el backend devuelve error, aparece el `ErrorAlert` con el mensaje específico. Si no hay datos, se muestra un mensaje descriptivo. Estas no son pantallas "bonus" — son requisitos de calidad que hacen la diferencia entre un prototipo y un sistema usable.

En `CitaNueva.tsx` implementé el flujo de validación completo: el botón "Confirmar cita" queda deshabilitado hasta que se hayan seleccionado todos los campos requeridos, incluyendo al paciente mediante el autocomplete. Si el backend rechaza la cita por regla de negocio — por ejemplo, "La cita no puede ser en el pasado" — el error se muestra junto al formulario, no en una alerta del navegador.

Esto aplica el principio de **manejo de excepciones**: el `catch` en cada llamada API captura el `ApiError` y lo presenta al usuario de forma amigable, sin exponer el stack trace ni el código HTTP.

### Qué mostrar en pantalla
- Navegador: mostrar `LoadingSpinner` al refrescar una página con red lenta (throttle en DevTools)
- `CitaNueva.tsx` — resaltar el botón deshabilitado `disabled={!selectedPatient}`
- Mostrar el mensaje de error al intentar crear una cita con hora pasada

### Conceptos cubiertos
- Manejo de excepciones: `try/catch` en llamadas API
- Estructuras de control: `if` para estados de carga/error
- Encapsulamiento: `ErrorAlert` y `LoadingSpinner` como componentes encapsulados

---

## PARTE 11 — QA documental y verificación del sistema
**Participante: Walter Amílcar Vásquez Amaya**

### Qué decir

Soy Walter Vásquez. Me encargué de la vista imprimible de recetas y de la documentación de verificación del sistema en el puerto 8097.

La documentación que generé en `docs/GUIA_CORRIDA_LOCAL.md` describe paso a paso cómo verificar que el sistema corre correctamente: qué endpoints probar, qué respuestas esperar, y cómo interpretar los logs del backend. Esto es fundamental para que cualquier integrante del equipo pueda verificar su instalación local.

El endpoint `GET /api/patients/{id}/vital-signs` que co-implementé registra los signos vitales en el expediente del paciente desde la pantalla de triaje, disponible para médicos y enfermeras.

### Qué mostrar en pantalla
- `docs/GUIA_CORRIDA_LOCAL.md` abierto
- Backend corriendo: mostrar en terminal el log `Started MedisuiteApplication`
- Hacer un `curl` o abrir en navegador `http://localhost:8097/actuator/health`

### Conceptos cubiertos
- Control de versiones Git: documentación en el repositorio
- Convenciones de código: rutas REST siguiendo convenciones HTTP

---

## PARTE 12 — Base de datos: migraciones Flyway y seguridad de contraseñas
**Participante: Walter Alejandro Flores Hernández**

### Qué decir

Soy Walter Flores. Trabajé en las migraciones de base de datos y en la funcionalidad de cambio obligatorio de contraseña.

Las migraciones Flyway son archivos SQL versionados que evolucionan el esquema de la base de datos de forma controlada. El archivo `V5__multi_tenant_constraints.sql` agrega las constraints de foreign key que garantizan la integridad multi-tenant. `V6__seed_demo.sql` inserta los 5 usuarios demo con sus clínicas, 20 pacientes y 30 citas de prueba.

En `UserController.java`, el endpoint `POST /api/users/change-password` verifica que la contraseña actual sea correcta antes de permitir cambiarla. El campo `mustChangePassword` en la entidad `User` es un `boolean` — tipo primitivo Java — que el sistema verifica en cada login. Si está en `true`, redirige al usuario a cambiar su contraseña antes de continuar.

Esto muestra el uso de tipos primitivos (`boolean`, `int`) en contextos reales de negocio, y el control de flujo `if/else` para implementar reglas de seguridad.

### Qué mostrar en pantalla
- Carpeta `backend/src/main/resources/db/migration/` — listar los archivos V1 a V7
- `UserController.java` — endpoint `change-password`, resaltar el `if` que verifica `mustChangePassword`

### Conceptos cubiertos
- Tipos primitivos: `boolean mustChangePassword`
- Estructuras de control: `if/else` para reglas de negocio
- Convenciones de código: snake_case en SQL, camelCase en Java

---

## PARTE 13 — Demo en vivo y cierre
**Participante: Héctor Napoleón López Ruiz**

### Qué decir

Gracias a todos. Ahora vamos a hacer un recorrido en vivo del sistema mostrando el flujo completo de una consulta médica.

*[Navegador en `http://localhost:5173`]*

**Paso 1 — Login como Recepcionista:**
Ingresamos con `jorge.alas.demo@medisuite.test`. El sistema valida el JWT, extrae el `tenant_id` y el `role`, y redirige al dashboard mostrando las métricas del día: citas de hoy, pacientes activos, recetas emitidas.

*[Navegar a "Nueva cita"]*

**Paso 2 — Agendar una cita:**
Seleccionamos especialidad, doctor y buscamos al paciente escribiendo en el campo autocompletado. El sistema filtra dinámicamente. Elegimos una fecha y los horarios disponibles — solo aparecen los que no han pasado en el día de hoy. Confirmamos la cita.

*[Cerrar sesión. Loguearse como Doctor.]*

**Paso 3 — Vista del doctor:**
El doctor ve sus citas del día. Puede marcar una cita como completada.

*[Navegar a Pacientes, clic en "Ver" de un paciente]*

**Paso 4 — Expediente clínico:**
Aquí se consolida todo el trabajo del equipo: datos del paciente, historial cronológico de recetas y signos vitales. Registramos nuevos signos vitales desde esta misma pantalla.

*[Loguearse como Enfermera. Navegar a Triaje.]*

**Paso 5 — Triaje:**
La enfermera busca al paciente, ve sus últimos 5 registros de signos vitales, y registra los de hoy: temperatura, frecuencia cardíaca, presión arterial, peso, talla y prioridad.

---

Para cerrar: en este Avance 2 demostramos la aplicación de los principios de Programación Orientada a Objetos en un sistema real. La herencia en `BaseEntity` elimina 50 líneas de código repetido. Los hilos en `AuditBackupScheduler` garantizan backups sin bloquear las peticiones HTTP. Los `ConcurrentHashMap` en seguridad previenen condiciones de carrera. Los `CompletableFuture` en el dashboard ejecutan 4 consultas en paralelo. Las interfaces de repositorio permiten escribir servicios sin conocer la implementación de base de datos.

No construimos estos patrones porque los vimos en clase — los construimos porque el sistema los necesitaba. Esa es la diferencia entre entender un concepto y aplicarlo.

Gracias.

### Qué mostrar en pantalla
- Demo completo en el navegador siguiendo los 5 pasos
- Al final: mostrar brevemente `git log --oneline` en terminal para evidenciar los 79 commits de 11 integrantes

### Conceptos cubiertos (síntesis)
- Git y control de versiones: flujo real con ramas, PRs y merge
- UML implícito: la demo en vivo muestra las relaciones Patient→MedicalRecord→Prescription (composición) y Doctor→Specialty (asociación)
- Todos los conceptos anteriores reflejados en la funcionalidad del sistema

---

---

## Tabla de cobertura completa de temas del currículo

| Tema | Parte # | Integrante | Archivo | Línea |
|---|---|---|---|---|
| JDK / JVM / entorno de desarrollo | 1 | Héctor | `MediSuiteApplication.java` | 1 / `pom.xml` |
| Manejo de memoria: Stack, Heap, GC | 1 | Héctor | `TenantContext.java` | 4 (`ThreadLocal`) |
| Tipos primitivos vs referencia | 2 | Héctor | `BaseEntity.java`, `Patient.java`, `VitalSign.java` | 20, varios |
| Operadores aritméticos / relacionales / lógicos | 6 | Carlos | `AppointmentService.java` | 104-110 |
| Estructuras condicionales `if/else/switch` | 5 | Bayron | `PatientService.java` | 20 |
| Estructuras iterativas `for/while` | 6 | Carlos | `AppointmentService.java` | 104 (bucle `for`) |
| Entrada / salida de datos (I/O) | 4 | Merino | `DatFileDao.java` | 21, 33 |
| Convenciones camelCase / PascalCase | 5 | Bayron | Todos los servicios y entidades | — |
| Abstracción: Clase y Objeto | 2 | Héctor | `BaseEntity.java` | 14 |
| Anatomía y firma de métodos | 5 | Bayron | `PatientService.java` | 18 |
| Constructores y sobrecarga | 2/3 | Héctor / Vigil | `BusinessException.java`, `JwtTokenProvider.java` | 4, 21 |
| Encapsulamiento: `private` + getters/setters | 2 | Héctor | `BaseEntity.java`, `Patient.java` | 13, todos |
| `this` y `super` | 2 | Héctor | `BusinessException.java` | 4 |
| Herencia: `extends` y jerarquía | 2 | Héctor | `BaseEntity.java` + todas las entidades | 14 |
| Sobrescritura `@Override` | 2/3 | Héctor / Vigil | `Prescription.java`, `JwtAuthenticationFilter.java` | 45, 28 |
| Polimorfismo | 3/5 | Vigil / Bayron | `OncePerRequestFilter`, `JpaRepository` | 22, — |
| Static vs Dynamic Binding | 3/4 | Vigil / Merino | `TenantContext.java`, `DashboardMetricsService.java` | 4-12, 26 |
| Clases abstractas | 2/4 | Héctor / Merino | `BaseEntity.java`, `DatFileDao.java` | 14, 8 |
| Interfaces | 4/5 | Merino / Bayron | `Serializable`, `Runnable`, `JpaRepository` | varios |
| Principios SOLID | 2/4/8 | Héctor / Merino / Melgar | `BaseEntity.java`, servicios, `LoadingSpinner` | — |
| Arrays / ArrayList | 6 | Carlos | `AppointmentService.java` | 103 |
| `List` con `@OrderColumn` | 6 | Carlos | `Prescription.java` | 41-43 |
| `HashMap` / `Map` clave-valor | 4/6 | Merino / Carlos | `DashboardMetricsService.java`, `MedicalRecordService.java` | 40, 42 |
| Stream API y `filter()` | 6 | Carlos | `AppointmentService.java` | 107-110 |
| `HashSet` thread-safe | 3 | Vigil | `JwtBlacklist.java` | 9 |
| Hilos: `Thread` + `Runnable` | 4 | Merino | `AuditBackupScheduler.java` | 27-31 |
| `ExecutorService` / `ScheduledExecutorService` | 4 | Merino | `AuditBackupScheduler.java` | 27, 32 |
| `Callable` y `Future` / `CompletableFuture` | 4 | Merino | `DashboardMetricsService.java` | 26-38 |
| Sincronización: `synchronized` / `ReentrantReadWriteLock` | 4 | Merino | `DatFileDao.java` | 11, 20, 30 |
| UML — diagramas y relaciones | 13 | Héctor | `docs/AVANCE2_MEDISUITE.md`, demo en vivo | — |
| Git — comandos y flujo de trabajo | 13 | Héctor | `git log --oneline` en terminal | — |
| Git — ramas, PRs y resolución de conflictos | 13 | Héctor | GitHub repo, historial de 37 PRs mergeados | — |
