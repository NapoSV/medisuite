# APPFLOW — MediSuite

> Flujos de navegación de la aplicación por rol y por HU.
> Versión: 1.0 · Fecha: 26/07/2026 · Owner: Zair Diaz + William Melgar (Frontend)

Este documento describe las rutas de usuario dentro de MediSuite. Cada diagrama Mermaid muestra la secuencia de pantallas, decisiones y estados. Complementa a [DISENO_UI_UX.md](DISENO_UI_UX.md) (cómo se ven las pantallas) y a [PRD.md](PRD.md) (HU que motivan el flujo).

---

## 1. Flujo transversal — Autenticación (HU-007)

```mermaid
flowchart TD
    Start([Usuario accede a /]) --> HasSession{¿Tiene JWT<br/>válido?}
    HasSession -- Sí --> Redirect[Redirigir a dashboard según rol]
    HasSession -- No --> Login[/Pantalla Login/]
    Login --> Fill[Selecciona clínica<br/>+ email + password]
    Fill --> Submit[POST /api/auth/login]
    Submit --> Valid{¿Credenciales<br/>válidas?}
    Valid -- No --> ErrorAuth[Toast error:<br/>Credenciales inválidas]
    ErrorAuth --> CountFail{5 intentos<br/>en 15 min?}
    CountFail -- Sí --> Lock[Cuenta bloqueada<br/>15 min]
    CountFail -- No --> Login
    Valid -- Sí --> FirstLogin{¿Primer login?}
    FirstLogin -- Sí --> ChangePwd[Forzar cambio<br/>de password]
    ChangePwd --> Redirect
    FirstLogin -- No --> Redirect
    Redirect --> DashAdmin[Dashboard Admin]
    Redirect --> DashMedico[Dashboard Médico]
    Redirect --> DashEnf[Cola de triaje]
    Redirect --> DashRecep[Agenda del día]
```

Notas de implementación:
- JWT devuelto en cuerpo (`accessToken`) + refresh token en cookie HttpOnly.
- `TenantContext` se hidrata desde el claim `tenant_id` del JWT en el filtro de Spring.
- Fallo tras 5 intentos: contador en Redis (fase futura) o tabla `login_attempts` (MVP).

---

## 2. Flujo Recepcionista — Agendar cita (HU-003 + HU-005)

```mermaid
flowchart TD
    Start([Recepción login]) --> DashRecep[Dashboard Recepción]
    DashRecep --> BtnNueva["+ Nueva cita"]
    BtnNueva --> ModalCita[Modal: Nueva cita]
    ModalCita --> Search[Buscar paciente<br/>por nombre o CIF]
    Search --> Found{¿Existe?}
    Found -- No --> NewPatient[+ Registrar paciente]
    NewPatient --> FormPac[Formulario Paciente]
    FormPac --> SavePac[POST /api/patients]
    SavePac --> Search
    Found -- Sí --> SelectDoc[Selecciona médico<br/>+ especialidad]
    SelectDoc --> SelectDate[Selecciona fecha]
    SelectDate --> LoadSlots[GET /api/doctors/:id/slots?date=X]
    LoadSlots --> SelectSlot[Elige hora disponible]
    SelectSlot --> Motivo[Escribe motivo]
    Motivo --> Confirm[POST /api/appointments]
    Confirm --> AntiDup{¿Doble<br/>reserva?}
    AntiDup -- Sí --> ToastErr[Toast error:<br/>Franja tomada]
    ToastErr --> SelectSlot
    AntiDup -- No --> CodRes[Genera código<br/>de reserva]
    CodRes --> Notify[Simula notif. email/WhatsApp]
    Notify --> Success[Toast éxito:<br/>Cita COD-8842]
    Success --> DashRecep
```

Reglas de negocio:
- Índice único parcial en `appointments(doctor_id, appointment_date, appointment_time) WHERE status IN ('PENDING','CONFIRMED')` impide doble reserva a nivel BD.
- Código de reserva: `COD-` + 4 dígitos aleatorios únicos por tenant.
- Cancelar/reprogramar solo con ≥ 24 h de anticipación (validación en service).

---

## 3. Flujo Enfermera — Triaje (HU-004)

```mermaid
flowchart TD
    Start([Enfermera login]) --> Queue[Cola de triaje del día]
    Queue --> ListPac[Lista pacientes<br/>en espera]
    ListPac --> Pick[Selecciona paciente]
    Pick --> LoadExp[GET /api/patients/:id/medical-record]
    LoadExp --> FormVital[Formulario Signos Vitales]
    FormVital --> Enter[Peso, talla, presión,<br/>temp, FC, síntomas]
    Enter --> Priority[Asigna prioridad<br/>bajo/medio/alto/crítico]
    Priority --> Save[POST /api/vital-signs]
    Save --> AppointmentUpdate[Update appointments.status<br/>= 'IN_CONSULTATION']
    AppointmentUpdate --> ToastOK[Toast: Pasado a médico]
    ToastOK --> Queue
```

Notas:
- Los signos vitales quedan en `vital_signs`, asociados a `medical_record_id`.
- El médico ve el triaje del día en su agenda antes de abrir el expediente.
- Prioridad "Crítica" dispara alerta visual (badge rojo pulsante) en la agenda del médico.

---

## 4. Flujo Médico — Consulta + Receta (HU-002 + HU-009)

```mermaid
flowchart TD
    Start([Médico login]) --> Agenda[Mi agenda del día]
    Agenda --> ClickCita[Click en una cita]
    ClickCita --> Expediente[Expediente del paciente]
    Expediente --> Review[Revisa historial,<br/>alergias, triaje de hoy]
    Review --> NewConsult[+ Registrar consulta]
    NewConsult --> FormConsult[Formulario:<br/>motivo · síntomas ·<br/>diagnóstico · tratamiento]
    FormConsult --> SaveConsult[POST /api/consultations]
    SaveConsult --> NeedRx{¿Emitir<br/>receta?}
    NeedRx -- No --> CloseConsult[Cierra consulta<br/>appointments.status = 'COMPLETED']
    NeedRx -- Sí --> FormRx[Formulario Receta]
    FormRx --> AddMed[+ Medicamento:<br/>nombre · dosis · duración]
    AddMed --> MoreMed{¿Otro?}
    MoreMed -- Sí --> AddMed
    MoreMed -- No --> Indications[Indicaciones adicionales]
    Indications --> SaveRx[POST /api/prescriptions]
    SaveRx --> Print{¿Imprimir<br/>PDF?}
    Print -- Sí --> GenPDF[GET /api/prescriptions/:id/pdf]
    Print -- No --> CloseConsult
    GenPDF --> CloseConsult
    CloseConsult --> Agenda
```

Reglas:
- Un mismo médico no puede modificar consulta después de 24 h de emitida (auditoría clínica).
- Toda emisión de receta queda en `prescriptions` con `emitted_at`, `doctor_id`, `patient_id`, líneas en `prescription_items`.

---

## 5. Flujo Administrador — Alta de usuario (HU-008)

```mermaid
flowchart TD
    Start([Admin login]) --> DashAdmin[Dashboard]
    DashAdmin --> MenuUsers[Menú: Usuarios y roles]
    MenuUsers --> ListUsers[Listado de usuarios<br/>del tenant]
    ListUsers --> BtnNew["+ Nuevo usuario"]
    BtnNew --> FormUser[Formulario:<br/>nombre · email · rol]
    FormUser --> SelectRole[Rol: Médico / Enfermera /<br/>Recepción / Admin]
    SelectRole --> RoleSpec{Rol requiere<br/>datos extra?}
    RoleSpec -- Médico --> DoctorExtra[Especialidad + Nº licencia]
    RoleSpec -- Otros --> Save[POST /api/users]
    DoctorExtra --> Save
    Save --> TempPwd[Sistema genera<br/>password temporal]
    TempPwd --> SendEmail[Simula envío email<br/>con password]
    SendEmail --> ListUsers
```

Notas:
- Password temporal se envía por email (o log en MVP) y **debe** cambiarse en el primer login (ver flujo §1).
- El rol determina permisos vía Spring Security (`@PreAuthorize("hasRole('DOCTOR')")`).

---

## 6. Flujo Paciente — Solicitar cita online (HU-003 rol paciente)

```mermaid
flowchart TD
    Start([Paciente login]) --> MyCitas[Mis citas]
    MyCitas --> BtnReq["+ Solicitar cita"]
    BtnReq --> SelectEsp[Selecciona especialidad]
    SelectEsp --> SelectDoc[Selecciona médico]
    SelectDoc --> Calendar[Ve calendario<br/>con disponibilidad]
    Calendar --> Pick[Elige fecha + hora]
    Pick --> Motivo[Escribe motivo]
    Motivo --> Confirm[POST /api/appointments]
    Confirm --> COD[Recibe COD-XXXX]
    COD --> Notify[Recibe email/WhatsApp<br/>simulado]
    Notify --> MyCitas
    MyCitas --> Cancel{¿Cancelar/<br/>reprogramar?}
    Cancel -- Sí --> Check24[Sistema verifica<br/>≥ 24h anticipación]
    Check24 --> CanCancel{¿Se puede?}
    CanCancel -- Sí --> CancelReason[Motivo cancelación]
    CancelReason --> UpdateStatus[PUT /api/appointments/:id/cancel]
    UpdateStatus --> MyCitas
    CanCancel -- No --> ToastErr[Toast: No se puede cancelar<br/>menos de 24h]
    ToastErr --> MyCitas
```

---

## 7. Flujo Jefe de almacén — Órdenes de compra (HU-011)

```mermaid
flowchart TD
    Start([Jefe login]) --> DashInv[Dashboard Inventario]
    DashInv --> LowStock[Panel: Stock crítico]
    LowStock --> BtnPO["+ Nueva orden de compra"]
    BtnPO --> SelectSup[Selecciona proveedor]
    SelectSup --> AddLine[+ Línea:<br/>producto + cantidad + costo]
    AddLine --> MoreLines{¿Otra?}
    MoreLines -- Sí --> AddLine
    MoreLines -- No --> Save[POST /api/purchase-orders]
    Save --> StatusPending[Estado: PENDING 🟡]
    StatusPending --> Receive{¿Recepción?}
    Receive -- Parcial --> PartRcv[Estado: PARTIALLY_RECEIVED 🔵]
    Receive -- Total --> FullRcv[Estado: COMPLETE 🟢]
    PartRcv --> UpdateStock[Actualiza stock<br/>de productos recibidos]
    FullRcv --> UpdateStock
    UpdateStock --> DashInv
```

---

## 8. Diagrama de estados — Cita

```mermaid
stateDiagram-v2
    [*] --> PENDING: Creada
    PENDING --> CONFIRMED: Confirma paciente
    PENDING --> CANCELLED: Cancela (≥24h)
    CONFIRMED --> IN_WAITING: Paciente check-in
    IN_WAITING --> IN_CONSULTATION: Enfermera pasa triaje
    IN_CONSULTATION --> COMPLETED: Médico cierra
    CONFIRMED --> CANCELLED: Cancela (≥24h)
    CONFIRMED --> NO_SHOW: No se presentó
    COMPLETED --> [*]
    CANCELLED --> [*]
    NO_SHOW --> [*]
```

## 9. Diagrama de estados — Orden de compra

```mermaid
stateDiagram-v2
    [*] --> PENDING: Emitida
    PENDING --> PARTIALLY_RECEIVED: Recepción parcial
    PENDING --> COMPLETE: Recepción total
    PENDING --> CANCELLED: Anulada
    PARTIALLY_RECEIVED --> COMPLETE: Recepción final
    PARTIALLY_RECEIVED --> CANCELLED: Anulada
    COMPLETE --> [*]
    CANCELLED --> [*]
```

---

## 10. Estados globales de UI

| Estado | Origen | Comportamiento |
|---|---|---|
| **Loading** | HTTP en vuelo | Skeleton en cards, spinner en botones. |
| **Empty** | Respuesta con array vacío | Ilustración + copy + CTA principal. |
| **Error 4xx** | Validación backend | Toast rojo + mensaje del backend en `messages_XX.properties`. |
| **Error 5xx** | Excepción no controlada | Toast rojo genérico + botón "Reportar". Log en backend con `trace_id`. |
| **Session expired (401)** | JWT vencido | Interceptor de Axios/fetch → intenta refresh token → si falla, redirige a login con toast. |
| **Sin permiso (403)** | Rol insuficiente | Toast: "No tienes permiso para esta acción". No redirige. |
| **Offline** | Sin red | Banner superior amarillo: "Sin conexión — algunos cambios no se guardarán". |

---

## 11. Rutas frontend (React Router)

```
/                          → Redirige según sesión
/login                     → Pantalla de login
/dashboard                 → Dashboard según rol
/patients                  → Listado pacientes
/patients/new              → Alta paciente
/patients/:id              → Expediente paciente
/patients/:id/vital-signs  → Triaje
/appointments              → Agenda (según rol)
/appointments/new          → Nueva cita
/appointments/:id          → Detalle cita
/prescriptions/new         → Emitir receta
/prescriptions/:id         → Detalle receta
/users                     → Gestión de usuarios (admin)
/users/new                 → Nuevo usuario
/reports                   → Reportes (admin)
/inventory                 → Inventario (jefe almacén)
/inventory/purchase-orders → Órdenes de compra
/settings                  → Configuración
/audit                     → Auditoría (admin)
/profile                   → Perfil del usuario logueado
/403                       → Sin permiso
/404                       → No encontrado
```

---

## 12. Referencias

- [DISENO_UI_UX.md](DISENO_UI_UX.md) — cómo se ven las pantallas mencionadas.
- [PRD.md](PRD.md) — HU que motivan cada flujo.
- [ESQUEMA_BACKEND.md](ESQUEMA_BACKEND.md) — endpoints REST invocados en cada paso.
