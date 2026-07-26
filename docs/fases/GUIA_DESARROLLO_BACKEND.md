# Guía Maestra de Desarrollo — MediSuite (del Avance 1 a la Presentación Final)

> **Qué es este documento:** la hoja de ruta completa del proyecto — TODAS las tareas desde lo que falta del Avance 1 hasta la presentación final del MVP funcional, con fechas, dependencias y el **responsable con nombre completo** de cada una (Sección 7). Incluye además el paso a paso técnico del backend con el código de cada clase, la configuración, la API REST de todos los módulos y el flujo Git (Secciones 2–6).
>
> **Sobre lo visual:** el frontend se construye en versión **mínima funcional** (pantallas simples que consumen la API, sin diseño pulido). El diseño visual fino se hace al final si alcanza el tiempo — la funcionalidad completa es lo que no se negocia.
>
> **Prerequisitos:** esquema de BD v2 aplicado (`database/schema.sql`), acceso a Neon con tu usuario individual, Java 21 y Git instalados.

---

## 1. Mapa de la plataforma — módulos (menús) y su API

Estos son **todos los menús** que tendrá la plataforma. Cada menú del frontend corresponde a un módulo del backend con sus endpoints:

| # | Menú de la plataforma | Módulo backend | Endpoints base | Roles con acceso |
|---|----------------------|----------------|----------------|------------------|
| 1 | Login / Registro | Auth | `/api/auth/*` | Público (login), ADMIN (crear usuarios) |
| 2 | Dashboard | Reports | `/api/reports/*` | ADMIN, DOCTOR |
| 3 | Pacientes | Patients | `/api/patients/*` | ADMIN, RECEPTIONIST, NURSE, DOCTOR |
| 4 | Citas / Agenda | Appointments | `/api/appointments/*` | Todos (paciente solo las suyas) |
| 5 | Triaje | VitalSigns | `/api/vital-signs/*` | NURSE, DOCTOR |
| 6 | Expediente Clínico | MedicalRecords | `/api/medical-records/*` | DOCTOR, NURSE (lectura), PATIENT (solo el suyo) |
| 7 | Recetas | Prescriptions | `/api/prescriptions/*` | DOCTOR (crear), PATIENT (ver las suyas) |
| 8 | Médicos y Especialidades | Doctors, Specialties | `/api/doctors/*`, `/api/specialties/*` | ADMIN (gestión), todos (consulta) |
| 9 | Usuarios y Roles | Users | `/api/users/*` | ADMIN |
| 10 | Inventario | Products | `/api/products/*` | ADMIN |
| 11 | Compras | PurchaseOrders | `/api/purchase-orders/*` | ADMIN |
| 12 | Activos Físicos | PhysicalAssets | `/api/physical-assets/*` | ADMIN |
| 13 | Auditoría | AuditLogs | `/api/audit-logs/*` | ADMIN (solo lectura) |
| 14 | Configuración de la Clínica | Tenants | `/api/tenants/*` | ADMIN (el suyo), SUPER_ADMIN (todos) |

---

## 2. Fase 0 — Creación del proyecto Spring Boot

**Responsable: ORELLANA ROJAS BAYRON ALEXANDER** (apoya: VENTURA VELASQUEZ CARLOS MARIO)
**Rama:** `feature/setup-spring-boot`

### 2.1 Generar el proyecto

En https://start.spring.io con estas opciones:
- Project: **Maven** · Language: **Java** · Spring Boot: **3.3.x**
- Group: `com.sv.grupo` · Artifact: `medisuite` · Package: `com.sv.grupo.hospital.citas`
- Java: **21**
- Dependencias: Spring Web, Spring Data JPA, Spring Security, Validation, PostgreSQL Driver, Lombok

Descomprimir dentro de la carpeta `backend/` del repo.

### 2.2 Dependencias adicionales en `pom.xml`

```xml
<!-- JWT (io.jsonwebtoken) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<!-- Documentación automática de la API (Swagger UI en /swagger-ui.html) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
<!-- H2 para el perfil de tests -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### 2.3 `backend/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: medisuite
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=${DB_SSLMODE:require}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 5        # Neon free tier: mantener el pool pequeño
  jpa:
    hibernate:
      ddl-auto: validate          # NUNCA update/create: el esquema lo gobierna database/schema.sql
    open-in-view: false
    properties:
      hibernate.format_sql: true

server:
  port: ${APP_PORT:8080}

jwt:
  secret: ${JWT_SECRET}
  expiration-ms: ${JWT_EXPIRATION_MS:900000}
  refresh-expiration-ms: ${JWT_REFRESH_EXPIRATION_MS:604800000}

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

> **Regla:** `ddl-auto: validate` obliga a que las entidades Java coincidan con `database/schema.sql`. Si no coinciden, la app no arranca — así detectamos desajustes de inmediato en vez de dejar que Hibernate "invente" el esquema.

### 2.4 Variables de entorno

El `.env.example` del repo ya define todas. Para desarrollo local cada quien crea su `.env` (NUNCA se sube — está en `.gitignore`):

| Variable | Valor de desarrollo | Descripción |
|----------|--------------------|-------------|
| `DB_HOST` | `ep-nameless-water-avvbs1vv-pooler.c-11.us-east-1.aws.neon.tech` | Host pooler de Neon |
| `DB_PORT` | `5432` | Puerto PostgreSQL |
| `DB_NAME` | `clinica_dev` | Base compartida |
| `DB_USERNAME` | tu usuario individual (ej. `bayron`) | Rol PostgreSQL propio |
| `DB_PASSWORD` | (tu contraseña, enviada por DM) | — |
| `DB_SSLMODE` | `require` | Neon exige SSL |
| `JWT_SECRET` | cadena aleatoria ≥ 32 caracteres | Firma de tokens. Cada quien genera la suya en dev |
| `JWT_EXPIRATION_MS` | `900000` (15 min) | Vida del access token |
| `JWT_REFRESH_EXPIRATION_MS` | `604800000` (7 días) | Vida del refresh token |
| `APP_PORT` | `8080` | Puerto del backend |
| `DEFAULT_TENANT_ID` | `1` | Tenant demo para desarrollo |

Para cargar el `.env` al correr localmente, en IntelliJ usar el plugin "EnvFile" o configurar las variables en la Run Configuration.

### 2.5 Checklist de esta fase

- [ ] El proyecto compila: `./mvnw clean compile`
- [ ] La app arranca conectada a Neon: `./mvnw spring-boot:run`
- [ ] Swagger responde en `http://localhost:8080/swagger-ui.html`
- [ ] PR abierto hacia `develop` con la estructura base

---

## 3. Fase 1 — Fundación (seguridad, multi-tenant, manejo de errores)

> Nada de esta fase es opcional: todos los módulos posteriores dependen de estas clases.

### 3.1 Clase base multi-tenant

**Responsable: VENTURA VELASQUEZ CARLOS MARIO**
**Rama:** `feature/fundacion-multitenant`

`model/common/TenantAwareEntity.java`:
```java
package com.sv.grupo.hospital.citas.model.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

/** Clase base de toda entidad de dominio: tenant_id + timestamps automáticos. */
@MappedSuperclass
@Getter
@Setter
public abstract class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /** El tenant JAMÁS viene del cliente: siempre del contexto del request (JWT). */
    @PrePersist
    public void assignTenant() {
        if (this.tenantId == null) {
            this.tenantId = TenantContext.getTenantId();
        }
    }
}
```

`model/common/TenantContext.java`:
```java
package com.sv.grupo.hospital.citas.model.common;

/** Guarda el tenant del request actual (lo setea el filtro JWT, lo limpia al terminar). */
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(Long tenantId) { CURRENT.set(tenantId); }

    public static Long getTenantId() {
        Long id = CURRENT.get();
        if (id == null) throw new IllegalStateException("No hay tenant en el contexto del request");
        return id;
    }

    public static void clear() { CURRENT.remove(); }
}
```

**Regla de oro para TODOS los repositorios:** toda consulta filtra por tenant. Los métodos de repositorio siempre reciben `tenantId`:
```java
Optional<Patient> findByIdAndTenantId(Long id, Long tenantId);
Page<Patient> findAllByTenantId(Long tenantId, Pageable pageable);
```
Nunca usar `findById(id)` a secas en un service — es la puerta a que un tenant vea datos de otro.

### 3.2 Seguridad JWT

**Responsables: ORELLANA ROJAS BAYRON ALEXANDER + VIGIL RAMIREZ ALEJANDRO ANTONIO**
**Rama:** `feature/HU-auth-jwt`

`security/JwtService.java`:
```java
package com.sv.grupo.hospital.citas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, Long tenantId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of("tenantId", tenantId, "role", role))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
```

`security/JwtAuthenticationFilter.java`:
```java
package com.sv.grupo.hospital.citas.security;

import com.sv.grupo.hospital.citas.model.common.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) { this.jwtService = jwtService; }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                Claims claims = jwtService.parse(header.substring(7));
                Long userId = Long.valueOf(claims.getSubject());
                Long tenantId = claims.get("tenantId", Long.class);
                String role = claims.get("role", String.class);

                TenantContext.setTenantId(tenantId);
                var auth = new UsernamePasswordAuthenticationToken(
                        userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();   // crítico: el thread se reutiliza entre requests
            SecurityContextHolder.clearContext();
        }
    }
}
```

`config/SecurityConfig.java`:
```java
package com.sv.grupo.hospital.citas.config;

import com.sv.grupo.hospital.citas.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // habilita @PreAuthorize en los controllers
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())                       // JWT en header → CSRF mitigado
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### 3.3 Manejo global de errores

**Responsable: VIGIL RAMIREZ ALEJANDRO ANTONIO**
**Rama:** misma `feature/fundacion-multitenant` o `feature/fundacion-excepciones`

`exception/NotFoundException.java`, `exception/BusinessException.java` (RuntimeException simples) y:

`exception/GlobalExceptionHandler.java`:
```java
package com.sv.grupo.hospital.citas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> business(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
        var first = ex.getBindingResult().getFieldErrors().get(0);
        return ResponseEntity.badRequest()
                .body(Map.of("error", first.getField() + ": " + first.getDefaultMessage()));
    }

    // Mensaje genérico: nunca exponer stack traces al cliente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> generic(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Error interno del servidor"));
    }
}
```

**Regla de aislamiento multi-tenant:** cuando un recurso no existe **o pertenece a otro tenant**, la respuesta es siempre **404** (nunca 403) — no revelamos que el recurso existe.

### 3.4 Módulo Auth (login)

**Responsables: ORELLANA ROJAS BAYRON ALEXANDER + VIGIL RAMIREZ ALEJANDRO ANTONIO**
**Rama:** `feature/HU-auth-jwt`

`model/users/Role.java`:
```java
package com.sv.grupo.hospital.citas.model.users;
public enum Role { DOCTOR, PATIENT, NURSE, ADMIN, RECEPTIONIST }
```

`model/users/User.java`:
```java
package com.sv.grupo.hospital.citas.model.users;

import com.sv.grupo.hospital.citas.model.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
public class User extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String cif;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;
}
```

`service/AuthService.java` (lógica clave — bloqueo tras 5 intentos):
```java
package com.sv.grupo.hospital.citas.service;

import com.sv.grupo.hospital.citas.dao.UserRepository;
import com.sv.grupo.hospital.citas.dto.auth.LoginRequest;
import com.sv.grupo.hospital.citas.dto.auth.LoginResponse;
import com.sv.grupo.hospital.citas.exception.BusinessException;
import com.sv.grupo.hospital.citas.model.users.User;
import com.sv.grupo.hospital.citas.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
public class AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // El login incluye el slug del tenant: un mismo correo puede existir en 2 clínicas
        User user = userRepository
                .findByEmailAndTenantSlug(request.email(), request.tenantSlug())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new BusinessException("Cuenta bloqueada temporalmente. Intenta más tarde.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_ATTEMPTS) {
                user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCK_MINUTES));
                user.setFailedLoginAttempts(0);
            }
            throw new BusinessException("Credenciales inválidas");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name(), user.getFirstName() + " " + user.getLastName());
    }
}
```

DTOs (`dto/auth/`) como records con validación:
```java
public record LoginRequest(
        @NotBlank String tenantSlug,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password) {}

public record LoginResponse(String token, String role, String fullName) {}
```

`controller/api/AuthController.java`:
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
```

---

## 4. Fase 2 — Módulo de referencia completo: Pacientes

**Responsable: DIAZ SANTOS ZAIR BENETT** (revisa: VIGIL RAMIREZ ALEJANDRO ANTONIO)
**Rama:** `feature/HU-001-registro-pacientes`

> Este módulo se implementa **primero y completo** porque es el patrón que TODOS los demás módulos copian: Entity → Repository → DTOs → Service → Controller. Quien tome cualquier otro módulo debe leer este código antes.

### 4.1 Entity — `model/users/Patient.java`

```java
package com.sv.grupo.hospital.citas.model.users;

import com.sv.grupo.hospital.citas.model.common.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter @Setter
public class Patient extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "blood_type", length = 5)
    private String bloodType;

    @Column(columnDefinition = "TEXT")
    private String allergies;
}
```

### 4.2 Repository — `dao/PatientRepository.java`

```java
package com.sv.grupo.hospital.citas.dao;

import com.sv.grupo.hospital.citas.model.users.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByIdAndTenantId(Long id, Long tenantId);

    Page<Patient> findAllByTenantId(Long tenantId, Pageable pageable);

    @Query("""
        SELECT p FROM Patient p JOIN p.user u
        WHERE p.tenantId = :tenantId
          AND (u.cif = :term OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :term, '%'))
                             OR LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :term, '%')))
        """)
    Page<Patient> search(@Param("tenantId") Long tenantId, @Param("term") String term, Pageable pageable);
}
```

### 4.3 DTOs — `dto/patient/`

```java
// Entrada (crear/actualizar) — con Bean Validation en TODOS los campos
public record PatientRequest(
        @NotBlank @Size(max = 80)  String firstName,
        @NotBlank @Size(max = 80)  String lastName,
        @NotBlank @Pattern(regexp = "\\d{8,20}", message = "CIF inválido") String cif,
        @NotBlank @Email           String email,
        @NotNull  @Past            LocalDate birthDate,
        @Size(max = 20)            String phone,
        @Size(max = 200)           String address,
        @Size(max = 100)           String emergencyContact,
        @Pattern(regexp = "(A|B|AB|O)[+-]", message = "Tipo de sangre inválido") String bloodType,
        String allergies) {}

// Salida — NUNCA exponer la entidad JPA directamente
public record PatientResponse(
        Long id, String firstName, String lastName, String cif, String email,
        LocalDate birthDate, String phone, String address,
        String emergencyContact, String bloodType, String allergies) {}
```

### 4.4 Service — `service/PatientService.java`

```java
package com.sv.grupo.hospital.citas.service;

import com.sv.grupo.hospital.citas.dao.PatientRepository;
import com.sv.grupo.hospital.citas.dao.UserRepository;
import com.sv.grupo.hospital.citas.dto.patient.*;
import com.sv.grupo.hospital.citas.exception.NotFoundException;
import com.sv.grupo.hospital.citas.model.common.TenantContext;
import com.sv.grupo.hospital.citas.model.users.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, AuditService auditService) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional
    public PatientResponse create(PatientRequest request) {
        Long tenantId = TenantContext.getTenantId();

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setCif(request.cif());
        user.setEmail(request.email());
        user.setRole(Role.PATIENT);
        // Contraseña temporal — el paciente la cambia en su primer login
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setBirthDate(request.birthDate());
        patient.setPhone(request.phone());
        patient.setAddress(request.address());
        patient.setEmergencyContact(request.emergencyContact());
        patient.setBloodType(request.bloodType());
        patient.setAllergies(request.allergies());
        patientRepository.save(patient);

        auditService.log("CREATE", "patients", patient.getId(), null, patient);
        return toResponse(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse findById(Long id) {
        return toResponse(getOwned(id));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String term, Pageable pageable) {
        Long tenantId = TenantContext.getTenantId();
        Page<Patient> page = (term == null || term.isBlank())
                ? patientRepository.findAllByTenantId(tenantId, pageable)
                : patientRepository.search(tenantId, term, pageable);
        return page.map(this::toResponse);
    }

    /** Busca el paciente validando tenant: si es de otro tenant → 404 (no 403). */
    private Patient getOwned(Long id) {
        return patientRepository.findByIdAndTenantId(id, TenantContext.getTenantId())
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));
    }

    private PatientResponse toResponse(Patient p) {
        User u = p.getUser();
        return new PatientResponse(p.getId(), u.getFirstName(), u.getLastName(), u.getCif(),
                u.getEmail(), p.getBirthDate(), p.getPhone(), p.getAddress(),
                p.getEmergencyContact(), p.getBloodType(), p.getAllergies());
    }
}
```

### 4.5 Controller — `controller/api/PatientController.java`

```java
package com.sv.grupo.hospital.citas.controller.api;

import com.sv.grupo.hospital.citas.dto.patient.*;
import com.sv.grupo.hospital.citas.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    public PatientController(PatientService patientService) { this.patientService = patientService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','NURSE')")
    public PatientResponse create(@Valid @RequestBody PatientRequest request) {
        return patientService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','NURSE','DOCTOR')")
    public PatientResponse findById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','NURSE','DOCTOR')")
    public Page<PatientResponse> search(@RequestParam(required = false) String q, Pageable pageable) {
        return patientService.search(q, pageable);   // paginación SIEMPRE — nunca findAll sin límite
    }
}
```

---

## 5. Fase 3 — Resto de módulos (mismo patrón, reglas específicas)

> Cada módulo replica exactamente la estructura del módulo Pacientes. Aquí van las entidades, los endpoints y las **reglas de negocio propias** de cada uno.

### 5.1 Especialidades y Médicos

**Responsable: FLORES HERNANDEZ WALTER ALEJANDRO**
**Rama:** `feature/medicos-especialidades`

Entidades: `Specialty` (id, name, active) y `Doctor` (user 1:1, `specialty` ManyToOne, licenseNumber, availableSchedule JSONB como String).

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/specialties` | autenticado | Lista paginada |
| POST | `/api/specialties` | ADMIN | Nombre único por tenant |
| PUT | `/api/specialties/{id}` | ADMIN | |
| GET | `/api/doctors` | autenticado | Filtro opcional `?specialtyId=` |
| POST | `/api/doctors` | ADMIN | Crea User (rol DOCTOR) + Doctor, igual que Pacientes crea User+Patient. Valida `max_doctors` del plan del tenant |
| PUT | `/api/doctors/{id}/schedule` | ADMIN, DOCTOR (el suyo) | Actualiza `availableSchedule` |

### 5.2 Citas (Agenda)

**Responsable: ORELLANA ROJAS BAYRON ALEXANDER**
**Rama:** `feature/HU-003-citas`

Entidad `Appointment`: patient (ManyToOne), doctor (ManyToOne), scheduledAt, status (enum `AppointmentStatus`), reason, office, reservationCode.

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/appointments` | todos | PATIENT solo ve las suyas (filtrar por su userId); DOCTOR las suyas; ADMIN/RECEPTIONIST todas. Filtros `?doctorId=&date=` |
| GET | `/api/appointments/availability?doctorId=&date=` | todos | Devuelve slots libres según `availableSchedule` del médico menos citas activas |
| POST | `/api/appointments` | PATIENT, RECEPTIONIST | Ver lógica anti doble-reserva abajo |
| PUT | `/api/appointments/{id}/cancel` | PATIENT (la suya), RECEPTIONIST, DOCTOR | Solo con ≥ 24h de anticipación si es PATIENT |
| PUT | `/api/appointments/{id}/status` | DOCTOR, NURSE, RECEPTIONIST | SCHEDULED → WAITING → COMPLETED |

**Lógica clave del service (la regla más importante del sistema):**
```java
@Transactional
public AppointmentResponse create(AppointmentRequest request) {
    Long tenantId = TenantContext.getTenantId();

    // 1. Validación en aplicación (mensaje amable)
    boolean taken = appointmentRepository
            .existsByDoctorIdAndScheduledAtAndStatusNot(
                    request.doctorId(), request.scheduledAt(), AppointmentStatus.CANCELLED);
    if (taken) throw new BusinessException("El horario ya está reservado");

    Appointment appt = new Appointment();
    // ... asignar campos ...
    appt.setReservationCode(generateReservationCode());   // ej. "RSV-" + 6 chars aleatorios

    // 2. Red de seguridad: si dos requests simultáneos pasan la validación,
    //    el índice único parcial de la BD (uq_appointments_doctor_slot) rechaza el segundo.
    try {
        appointmentRepository.saveAndFlush(appt);
    } catch (DataIntegrityViolationException e) {
        throw new BusinessException("El horario ya está reservado");
    }
    auditService.log("CREATE", "appointments", appt.getId(), null, appt);
    return toResponse(appt);
}
```
Esta doble capa (validación + constraint de BD) es lo que garantiza la métrica "cero doble reserva" incluso con usuarios simultáneos.

### 5.3 Expediente Clínico

**Responsable: ORELLANA ROJAS BAYRON ALEXANDER**
**Rama:** `feature/expediente-clinico`

Entidad `MedicalRecord`: patient (OneToOne), createdOn, generalNotes.

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/medical-records/by-patient/{patientId}` | DOCTOR, NURSE | Crea el expediente automáticamente si el paciente no tiene (lazy creation) |
| GET | `/api/medical-records/by-cif/{cif}` | DOCTOR, NURSE | La búsqueda estrella: CIF → expediente completo (datos + signos + recetas + citas) |
| GET | `/api/medical-records/mine` | PATIENT | El paciente SOLO ve su propio expediente — resolver patientId desde el userId del token, jamás desde un parámetro |
| PUT | `/api/medical-records/{id}/notes` | DOCTOR | Actualiza notas generales (auditar SIEMPRE con data_before) |

### 5.4 Triaje (Signos Vitales)

**Responsable: DIAZ SANTOS ZAIR BENETT**
**Rama:** `feature/HU-004-triaje`

Entidad `VitalSign`: medicalRecord (ManyToOne), recordedAt, weightKg, heightCm, bloodPressure, temperatureC, heartRate, symptoms, priority (enum `Priority { LOW, MEDIUM, HIGH, CRITICAL }`).

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| POST | `/api/vital-signs` | NURSE | Validaciones de rango: peso 0–500 kg, temperatura 30–45 °C, frecuencia 20–250. Al registrar, si la cita del paciente está SCHEDULED pasarla a WAITING |
| GET | `/api/vital-signs/by-record/{recordId}` | DOCTOR, NURSE | Timeline ordenado por `recordedAt DESC` (el índice ya está optimizado para esto) |

### 5.5 Recetas

**Responsable: FLORES HERNANDEZ WALTER ALEJANDRO**
**Rama:** `feature/HU-002-recetas`

Entidad `Prescription`: medicalRecord (ManyToOne), doctor (ManyToOne), issuedOn, medications, dosage, duration, instructions.

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| POST | `/api/prescriptions` | DOCTOR | El doctorId sale del token (el médico autenticado), NO del body |
| GET | `/api/prescriptions/by-record/{recordId}` | DOCTOR, NURSE | |
| GET | `/api/prescriptions/mine` | PATIENT | Solo las del expediente propio |

### 5.6 Usuarios y Roles (administración)

**Responsable: VIGIL RAMIREZ ALEJANDRO ANTONIO**
**Rama:** `feature/gestion-usuarios`

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/users` | ADMIN | Paginado, filtro `?role=` |
| POST | `/api/users` | ADMIN | Valida `max_users` del plan. Contraseña temporal + flag de cambio obligatorio |
| PUT | `/api/users/{id}/deactivate` | ADMIN | Soft-delete: `active = false`. NUNCA DELETE físico (integridad referencial + auditoría) |
| PUT | `/api/users/{id}/reset-password` | ADMIN | Genera temporal nueva |

### 5.7 Inventario (Productos)

**Responsable: FLORES HERNANDEZ WALTER ALEJANDRO**
**Rama:** `feature/inventario-productos`

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/products` | ADMIN | Filtro `?lowStock=true` → `current_stock < min_stock` |
| POST | `/api/products` | ADMIN | Nombre único por tenant |
| PUT | `/api/products/{id}` | ADMIN | El stock NO se edita directo aquí — cambia solo por recepción de compras (5.8) |

### 5.8 Compras (Órdenes + Detalle)

**Responsable: FLORES HERNANDEZ WALTER ALEJANDRO**
**Rama:** `feature/ordenes-compra`

Entidades `PurchaseOrder` + `PurchaseOrderItem` (OneToMany con cascade).

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| POST | `/api/purchase-orders` | ADMIN | Body incluye los items; `total_amount` se calcula en el service (Σ quantity × unitPrice), nunca lo manda el cliente |
| GET | `/api/purchase-orders?status=` | ADMIN | |
| PUT | `/api/purchase-orders/{id}/receive` | ADMIN | Recibe cantidades por item → suma a `received_quantity`, **actualiza `products.current_stock`** en la misma transacción, y recalcula estado: todo recibido → COMPLETE, parcial → PARTIALLY_RECEIVED |

### 5.9 Activos Físicos

**Responsable: MELGAR RIVAS WILLIAM ARIEL**
**Rama:** `feature/activos-fisicos`

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| CRUD | `/api/physical-assets` | ADMIN | Enum de estado ACTIVE / MAINTENANCE / DECOMMISSIONED |
| GET | `/api/physical-assets/total-value` | ADMIN | Σ `acquisition_value` de activos ACTIVE — alimenta el dashboard "TOTAL ASSET VALUE" |

### 5.10 Reportes (Dashboard)

**Responsable: MELGAR RIVAS WILLIAM ARIEL** (apoya: ORELLANA ROJAS BAYRON ALEXANDER)
**Rama:** `feature/reportes-dashboard`

| Método | Endpoint | Rol | Contenido |
|--------|----------|-----|-----------|
| GET | `/api/reports/summary` | ADMIN | Totales: pacientes, citas de hoy, citas de la semana, productos bajo stock, valor total de activos |
| GET | `/api/reports/occupancy?from=&to=` | ADMIN | Citas por médico por día (query agregada con GROUP BY — usar `@Query` nativa) |
| GET | `/api/reports/care?from=&to=` | ADMIN, DOCTOR | Atenciones completadas por médico/especialidad |

### 5.11 Auditoría

**Responsable: SANCHEZ MENJIVAR NICOLE NOHEMY** (con apoyo técnico de VENTURA VELASQUEZ CARLOS MARIO)
**Rama:** `feature/auditoria`

`service/AuditService.java` — lo consumen todos los services:
```java
@Service
public class AuditService {

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    // llamado por cada service en operaciones de escritura
    public void log(String action, String entityName, Long entityId, Object before, Object after) {
        AuditLog log = new AuditLog();
        log.setTenantId(TenantContext.getTenantId());
        log.setUserId(currentUserId());              // del SecurityContext
        log.setAction(action);                       // CREATE / UPDATE / DELETE / LOGIN
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDataBefore(toJson(before));
        log.setDataAfter(toJson(after));
        repository.save(log);
    }
    // ... helpers ...
}
```

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/audit-logs?entity=&from=&to=` | ADMIN | Solo lectura, solo su tenant, paginado |

### 5.12 Configuración del Tenant

**Responsable: VENTURA VELASQUEZ CARLOS MARIO**
**Rama:** `feature/tenant-config`

| Método | Endpoint | Rol | Regla |
|--------|----------|-----|-------|
| GET | `/api/tenants/current` | autenticado | Datos del tenant propio (nombre, logo, colores, plan) |
| PUT | `/api/tenants/current` | ADMIN | Solo campos de personalización (nombre comercial, logo, color, idioma, tz) — NUNCA plan/estado/límites desde aquí |

---

## 6. Flujo Git — cómo se trabaja cada tarea

**Todo el equipo. Aprobadores de PR: ORELLANA ROJAS BAYRON ALEXANDER o LOPEZ RUIZ HECTOR NAPOLEON.**

### Por cada tarea de esta guía:

```bash
# 1. Partir SIEMPRE de develop actualizado
git checkout develop
git pull

# 2. Crear tu rama (nombre = el indicado en cada sección)
git checkout -b feature/HU-001-registro-pacientes

# 3. Trabajar en commits pequeños y frecuentes (formato conventional en español)
git add .
git commit -m "feat: entidad y repositorio de pacientes HU-001"
# ... más commits ...
git commit -m "feat: service de pacientes con busqueda por cif HU-001"
git commit -m "test: unit tests de PatientService HU-001"

# 4. Subir la rama y abrir PR hacia develop
git push origin feature/HU-001-registro-pacientes
```

**En el PR (GitHub):**
- Título: `HU-001: Registro y búsqueda de pacientes`
- Descripción: qué hace, cómo probarlo, checklist de seguridad (¿`@PreAuthorize`? ¿DTOs validados? ¿sin secretos? ¿queries filtradas por tenant?)
- Esperar aprobación de BAYRON o HECTOR → **merge con "Squash and merge"** hacia `develop`
- Mover la tarjeta del Planner a `✅ Completado` con el enlace del PR

**Prefijos de commit:** `feat:` (funcionalidad), `fix:` (corrección), `test:` (pruebas), `refactor:`, `docs:`.

**Entregas académicas:** PR de `develop` → `main` con tag (`v2.0-avance2`, `v3.0-avance3`) — solo lo hace LOPEZ RUIZ HECTOR NAPOLEON.

---

## 7. Hoja de ruta completa — del Avance 1 a la Presentación Final

> Orden obligatorio: las etapas se construyen una sobre otra. Dentro de una etapa, las tareas SÍ pueden ir en paralelo. Fechas de referencia: **Avance 1 → 10/08** · **Avance 2 → ~14/09** · **Entrega final/presentación → semana del 19–23/10, cierre 26/10**.

### Etapa 0 — Avance 1 COMPLETO: armado del documento sección por sección · hasta el 08/08

> El documento del ingeniero (`docs/intruccionesProyecto.md`) tiene **14 secciones** y se arma entre todos: cada quien es dueño de su sección — la redacta o, si ya existe un borrador, la **verifica, corrige y da por buena** (el borrador no cuenta como terminado hasta que su dueño lo valide). Todo se entrega a Héctor, que consolida.

**Parte 1 — Las 14 secciones del documento (entregar cada una al 01/08):**

| Sección | Contenido | Responsable (nombre completo) | Estado actual |
|---------|-----------|-------------------------------|---------------|
| 1. Portada | Tabla con fotografía, nombre completo, CIF y participación de los 11 | SANCHEZ MENJIVAR NICOLE NOHEMY (recopila fotos de todos) | Falta: fotos reales (hay placeholders) |
| 2. Objetivo General | Redacción final del objetivo del sistema | LOPEZ RUIZ HECTOR NAPOLEON | Borrador — validar |
| 3. Objetivos Específicos | Los 3 OE alineados a los 3 avances | LOPEZ RUIZ HECTOR NAPOLEON | Borrador — validar |
| 4. Distribución del Equipo (Scrum) | Tabla de 11 integrantes con rol Scrum y rol técnico | VASQUEZ AMAYA WALTER AMILCAR | Borrador — verificar roles y CIF de cada quien |
| 5. Roles y Funciones del Sistema | Médico, Paciente, Enfermera, Administrador, Recepcionista y sus funciones | FUENTES ORTIZ ERIKA ALEXANDRA | Borrador — validar contra el alcance real |
| 6. Requerimientos (Historias de Usuario) | Las 6 HU con criterios de aceptación | SANCHEZ MENJIVAR NICOLE NOHEMY + FUENTES ORTIZ ERIKA ALEXANDRA | Borrador — **incorporar hallazgos de las entrevistas (tarea 0.2)** |
| 7. Alcances / Limitaciones | Qué NO hará el sistema | VIGIL RAMIREZ ALEJANDRO ANTONIO | Borrador — validar |
| 8. Planificación por Avance | Tabla de actividades por avance con responsables y fechas | LOPEZ RUIZ HECTOR NAPOLEON | **Reescribir** — alinearla con la hoja de ruta de esta guía (Etapas 0–H) |
| 9. Cronograma (Gantt) | Gráfico de Gantt de las 10 semanas | MELGAR RIVAS WILLIAM ARIEL | **Rehacer** — el actual es una tabla de texto; hacer gráfico real (Excel/Canva/draw.io) alineado a las etapas de esta guía |
| 10. Entradas / Salidas | Tabla de entradas y salidas por HU | DIAZ SANTOS ZAIR BENETT | Borrador — validar |
| 11. Declaración de Entidades | Tabla de entidades con atributos | MERINO VENTURA ALEJANDRO SEBASTIAN | **Actualizar** — el borrador tiene 9 entidades; ampliar a las 17 tablas del esquema v2 (`database/schema.sql`) |
| 12. Proyecto Base (Código y Clases) | Paquetes Java + mapeo entidad→clase + 2 clases modelo | VENTURA VELASQUEZ CARLOS MARIO | Redactada — revisión técnica final (¿coincide con schema v2?) |
| 13. Conclusiones | 2–3 líneas por cada uno de los 11 integrantes | TODOS (los 11) → envían a Héctor | Plantilla lista — faltan los 11 textos |
| 14. Bibliografía (APA) | Referencias de ing. de software, Scrum, Java, BD y web | SANCHEZ MENJIVAR NICOLE NOHEMY | Redactada — revisar formato APA y agregar lo citado en las entrevistas |

**Parte 2 — Tareas de soporte del Avance 1:**

| # | Tarea | Responsable (nombre completo) | Fecha límite | Depende de |
|---|-------|-------------------------------|--------------|------------|
| 0.1 | Cerrar nombre del producto (MediSuite vs ClinicaOS) y unificarlo en TODO el documento y el repo | LOPEZ RUIZ HECTOR NAPOLEON + SANCHEZ MENJIVAR NICOLE NOHEMY | 26/07 | — |
| 0.2 | 2 entrevistas a personal de clínica real (5 preguntas guía del plan) → insumo para Sección 6 | SANCHEZ MENJIVAR NICOLE NOHEMY | 28/07 | — |
| 0.3 | Ejercicio de práctica en BD: cada quien crea su tabla asignada (ver `MANUAL_AVANCE1_EQUIPO.md`) | TODOS (los 11) | 26/07 | Paso 0 del manual |
| 0.4 | Diagrama DER de las 17 tablas v2 en draw.io → `docs/diagramas/DER.png` (se anexa a la Sección 11) | MERINO VENTURA ALEJANDRO SEBASTIAN (backup: VENTURA VELASQUEZ CARLOS MARIO) | 30/07 | 0.3 |
| 0.5 | Documento de arquitectura multi-tenant → `docs/fases/MULTI_TENANT.md` | VENTURA VELASQUEZ CARLOS MARIO | 30/07 | — |
| 0.6 | Revisión cruzada del documento completo (ortografía, formato, consistencia de nombres) | ORELLANA ROJAS BAYRON ALEXANDER | 04/08 | Parte 1 completa |
| 0.7 | Consolidar el documento final, exportar a PDF y subir al aula virtual | LOPEZ RUIZ HECTOR NAPOLEON | 08/08 | 0.6 |

### Etapa A — Fundación del backend · 04/08 al 16/08 (todo lo demás depende de esto)

| # | Tarea | Responsable (nombre completo) | Rama | Depende de |
|---|-------|-------------------------------|------|------------|
| A1 | Proyecto Spring Boot base + conexión a Neon + Swagger | ORELLANA ROJAS BAYRON ALEXANDER | `feature/setup-spring-boot` | Esquema v2 aplicado |
| A2 | Aplicar esquema v2 + seed en `clinica_dev` (Neon) | MERINO VENTURA ALEJANDRO SEBASTIAN | (script, sin rama) | — |
| A3 | `TenantAwareEntity` + `TenantContext` + regla de repositorios | VENTURA VELASQUEZ CARLOS MARIO | `feature/fundacion-multitenant` | A1 |
| A4 | `JwtService` + `JwtAuthenticationFilter` + `SecurityConfig` | ORELLANA ROJAS BAYRON ALEXANDER + VIGIL RAMIREZ ALEJANDRO ANTONIO | `feature/HU-auth-jwt` | A1 |
| A5 | Excepciones + `GlobalExceptionHandler` | VIGIL RAMIREZ ALEJANDRO ANTONIO | `feature/fundacion-excepciones` | A1 |
| A6 | Módulo Auth (login + bloqueo 5 intentos) | ORELLANA ROJAS BAYRON ALEXANDER + VIGIL RAMIREZ ALEJANDRO ANTONIO | `feature/HU-auth-jwt` | A3, A4, A5 |
| A7 | `AuditService` + entidad `AuditLog` | SANCHEZ MENJIVAR NICOLE NOHEMY (apoyo: VENTURA VELASQUEZ CARLOS MARIO) | `feature/auditoria` | A3 |

### Etapa B — Módulo de referencia · 17/08 al 24/08

| # | Tarea | Responsable | Rama | Depende de |
|---|-------|-------------|------|------------|
| B1 | Módulo Pacientes completo (patrón de referencia) | DIAZ SANTOS ZAIR BENETT (revisa: VIGIL RAMIREZ ALEJANDRO ANTONIO) | `feature/HU-001-registro-pacientes` | Etapa A completa |
| B2 | Unit tests de `PatientService` (cobertura ≥ 70%) | DIAZ SANTOS ZAIR BENETT | misma rama | B1 |

### Etapa C — Módulos clínicos · 24/08 al 13/09 (en paralelo, todos copian el patrón de B1; el Avance 2 se entrega con esto funcionando)

| # | Tarea | Responsable | Rama | Depende de |
|---|-------|-------------|------|------------|
| C1 | Especialidades + Médicos | FLORES HERNANDEZ WALTER ALEJANDRO | `feature/medicos-especialidades` | B1 |
| C2 | Citas + disponibilidad + anti doble-reserva | ORELLANA ROJAS BAYRON ALEXANDER | `feature/HU-003-citas` | C1 |
| C3 | Expediente clínico + búsqueda por CIF | ORELLANA ROJAS BAYRON ALEXANDER | `feature/expediente-clinico` | B1 |
| C4 | Triaje / signos vitales | DIAZ SANTOS ZAIR BENETT | `feature/HU-004-triaje` | C3 |
| C5 | Recetas | FLORES HERNANDEZ WALTER ALEJANDRO | `feature/HU-002-recetas` | C3 |
| C6 | Gestión de usuarios (admin) | VIGIL RAMIREZ ALEJANDRO ANTONIO | `feature/gestion-usuarios` | B1 |
| C7 | Documento académico del Avance 2 (según formato que defina el ingeniero) + PDF al aula virtual | LOPEZ RUIZ HECTOR NAPOLEON + SANCHEZ MENJIVAR NICOLE NOHEMY | (entrega ~14/09) | C1–C6 |

### Etapa D — Módulos administrativos · 14/09 al 04/10 (en paralelo)

| # | Tarea | Responsable | Rama | Depende de |
|---|-------|-------------|------|------------|
| D1 | Inventario (productos) | FLORES HERNANDEZ WALTER ALEJANDRO | `feature/inventario-productos` | B1 |
| D2 | Órdenes de compra + recepción + stock | FLORES HERNANDEZ WALTER ALEJANDRO | `feature/ordenes-compra` | D1 |
| D3 | Activos físicos + valor total | MELGAR RIVAS WILLIAM ARIEL | `feature/activos-fisicos` | B1 |
| D4 | Reportes / dashboard | MELGAR RIVAS WILLIAM ARIEL (apoyo: ORELLANA ROJAS BAYRON ALEXANDER) | `feature/reportes-dashboard` | C2, D3 |
| D5 | Configuración del tenant | VENTURA VELASQUEZ CARLOS MARIO | `feature/tenant-config` | A3 |
| D6 | Endpoint de consulta de auditoría | SANCHEZ MENJIVAR NICOLE NOHEMY | `feature/auditoria` | A7 |

### Etapa E — Calidad · transversal desde el 18/08 hasta el final

| # | Tarea | Responsable | Depende de |
|---|-------|-------------|------------|
| E1 | Test de aislamiento multi-tenant (tenant A no ve datos de tenant B, respuesta 404) | VENTURA VELASQUEZ CARLOS MARIO + FUENTES ORTIZ ERIKA ALEXANDRA | A6, B1 |
| E2 | Casos de prueba manuales por módulo (formato tabla en `casos-de-prueba.md`) | FUENTES ORTIZ ERIKA ALEXANDRA + VASQUEZ AMAYA WALTER AMILCAR | cada módulo al mergearse |
| E3 | Prueba de estrés de doble reserva (2 usuarios simultáneos → 0 conflictos) | VASQUEZ AMAYA WALTER AMILCAR | C2 |
| E4 | Revisión de checklist de seguridad en cada PR | VENTURA VELASQUEZ CARLOS MARIO (Security Champion) | continuo |
| E5 | Documentación de la API (revisar que Swagger esté completo y entendible) | SANCHEZ MENJIVAR NICOLE NOHEMY | continuo |
| E6 | Coordinación general, aprobación de PRs, merges a develop/main | LOPEZ RUIZ HECTOR NAPOLEON + ORELLANA ROJAS BAYRON ALEXANDER | continuo |

### Etapa F — Frontend mínimo funcional · 14/09 al 11/10

> "Mínimo funcional" = pantallas simples que consumen la API real: formularios, tablas y navegación, sin diseño pulido. Cada pantalla corresponde a un menú de la Sección 1. El diseño visual fino se hace al final SOLO si sobra tiempo.

| # | Tarea | Responsable (nombre completo) | Rama | Depende de |
|---|-------|-------------------------------|------|------------|
| F1 | Decisión final React vs Angular + setup del proyecto (routing, cliente HTTP, manejo de token JWT) | DIAZ SANTOS ZAIR BENETT + MELGAR RIVAS WILLIAM ARIEL (arbitra: VENTURA VELASQUEZ CARLOS MARIO) | `feature/front-setup` | Etapa C |
| F2 | Pantalla de login + guardado del token + interceptor Authorization | DIAZ SANTOS ZAIR BENETT | `feature/front-login` | F1, A6 |
| F3 | Layout base: menú lateral con los 14 módulos + guards por rol (cada rol ve solo sus menús) | MELGAR RIVAS WILLIAM ARIEL | `feature/front-layout` | F2 |
| F4 | Pantallas Pacientes: lista con búsqueda por CIF/nombre + formulario de registro (HU-001) | DIAZ SANTOS ZAIR BENETT | `feature/front-pacientes` | F3, B1 |
| F5 | Pantallas Citas: agenda por médico/fecha, solicitud con slots disponibles, cancelación (HU-003) | MELGAR RIVAS WILLIAM ARIEL | `feature/front-citas` | F3, C2 |
| F6 | Pantallas Triaje + Expediente + Recetas (HU-002, HU-004): registro de signos, vista de expediente por CIF, emisión de receta | DIAZ SANTOS ZAIR BENETT | `feature/front-clinico` | F3, C3–C5 |
| F7 | Pantallas Inventario + Compras + Activos: tablas CRUD y recepción de órdenes | MELGAR RIVAS WILLIAM ARIEL | `feature/front-admin` | F3, D1–D3 |
| F8 | Dashboard: tarjetas de resumen + TOTAL ASSET VALUE + reporte de ocupación | MELGAR RIVAS WILLIAM ARIEL | `feature/front-dashboard` | F3, D4 |
| F9 | Pantallas Usuarios + Configuración de clínica + Auditoría | DIAZ SANTOS ZAIR BENETT | `feature/front-config` | F3, C6, D5, D6 |

### Etapa G — Despliegue · 05/10 al 18/10

| # | Tarea | Responsable (nombre completo) | Depende de |
|---|-------|-------------------------------|------------|
| G1 | CI/CD GitHub Actions: build + tests backend y frontend + audit en cada PR (bloquea merge si falla) | LOPEZ RUIZ HECTOR NAPOLEON + VIGIL RAMIREZ ALEJANDRO ANTONIO | Etapas B–D |
| G2 | Deploy del backend en Render o Railway, conectado a Neon, con variables de entorno de producción (JWT_SECRET distinto al de dev) | VIGIL RAMIREZ ALEJANDRO ANTONIO + LOPEZ RUIZ HECTOR NAPOLEON | G1 |
| G3 | Deploy del frontend en Vercel apuntando al backend desplegado + CORS restrictivo al dominio de Vercel | MELGAR RIVAS WILLIAM ARIEL | G2, Etapa F |
| G4 | Prueba end-to-end en el ambiente desplegado: flujo completo login → paciente → cita → triaje → receta con los 2 tenants demo | FUENTES ORTIZ ERIKA ALEXANDRA + VASQUEZ AMAYA WALTER AMILCAR | G3 |

### Etapa H — Cierre y Presentación Final · 12/10 al 26/10

| # | Tarea | Responsable (nombre completo) | Fecha límite | Depende de |
|---|-------|-------------------------------|--------------|------------|
| H1 | Pruebas de regresión final de todos los módulos + corrección de bugs críticos | FUENTES ORTIZ ERIKA ALEXANDRA + VASQUEZ AMAYA WALTER AMILCAR (fixes: cada responsable de módulo) | 20/10 | G4 |
| H2 | Validar las métricas de éxito del plan (agendar cita < 60 s, búsqueda por CIF < 2 s, cero doble reserva, auditoría 100%) | VASQUEZ AMAYA WALTER AMILCAR | 20/10 | H1 |
| H3 | Documento académico del Avance 3 / entrega final + PDF al aula virtual | LOPEZ RUIZ HECTOR NAPOLEON + SANCHEZ MENJIVAR NICOLE NOHEMY | 21/10 | H1 |
| H4 | Manual de usuario (PDF, con capturas de cada menú) | SANCHEZ MENJIVAR NICOLE NOHEMY + FUENTES ORTIZ ERIKA ALEXANDRA | 22/10 | Etapa F |
| H5 | Manual técnico (PDF: arquitectura, esquema BD, API, deploy) | VENTURA VELASQUEZ CARLOS MARIO + ORELLANA ROJAS BAYRON ALEXANDER | 22/10 | Etapa G |
| H6 | Presentación (PowerPoint) + guion de la demo en vivo con el ambiente desplegado | TODOS (coordina: LOPEZ RUIZ HECTOR NAPOLEON) | 23/10 | H2, H3 |
| H7 | Ensayo general de la demo (al menos 1 corrida completa en equipo) | TODOS (los 11) | 24/10 | H6 |
| H8 | Tag `v3.0-final` en `main` y entrega formal | LOPEZ RUIZ HECTOR NAPOLEON | 26/10 | H7 |

---

## 8. Definición de Terminado por módulo

Un módulo de esta guía se considera **terminado** cuando:

1. ✅ Compila y la app arranca con `ddl-auto: validate` sin errores contra el esquema v2
2. ✅ Todos los endpoints tienen `@PreAuthorize` con los roles de la tabla del módulo
3. ✅ Todos los DTOs de entrada tienen Bean Validation
4. ✅ Todas las queries del repositorio filtran por `tenantId`
5. ✅ Las operaciones de escritura llaman a `AuditService.log(...)`
6. ✅ Unit tests del service pasando (cobertura ≥ 70% del service)
7. ✅ Los endpoints aparecen y funcionan en Swagger
8. ✅ PR aprobado y mergeado a `develop`, tarjeta del Planner en `✅ Completado`
9. ✅ QA validó los criterios de aceptación (FUENTES ORTIZ ERIKA ALEXANDRA / VASQUEZ AMAYA WALTER AMILCAR)

---

## 9. Notas finales

- **La parte visual es mínima por ahora a propósito:** Swagger UI (`/swagger-ui.html`) sirve como "interfaz" para probar todos los módulos sin escribir una sola línea de frontend. Cuando llegue el momento del frontend, cada menú de la Sección 1 ya tendrá su API lista.
- **El esquema no se toca desde Java:** cualquier cambio de BD se hace en `database/schema.sql` (PR + revisión de MERINO VENTURA ALEJANDRO SEBASTIAN / VENTURA VELASQUEZ CARLOS MARIO) y luego se ajustan las entidades para que `validate` pase.
- **Dudas de patrón:** el módulo Pacientes (Sección 4) es la referencia canónica. Si tu módulo necesita algo que Pacientes no tiene, pregunta en el canal `🔧 Backend` antes de inventar un patrón nuevo.
