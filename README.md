# MediSuite — Plataforma de Gestión Clínica SaaS

Sistema web multi-empresa (SaaS) para gestión de citas médicas, expediente clínico,
triaje, recetas y dashboard clínico por rol.

Repositorio: https://github.com/NapoSV/medisuite

---

## Integrantes

| # | Nombre | CIF | GitHub | Rol |
|---|--------|-----|--------|-----|
| 1 | Héctor Napoleon López Ruiz | 2026010132 | @NapoSV | PM / Arquitecto |
| 2 | Walter Amilcar Vásquez Amaya | 2026010068 | @WaltArt94 | QA |
| 3 | Alejandro Antonio Vigil Ramírez | 2026010204 | @Alejandro08vigil-dotcom | Scrum Master |
| 4 | Zair Benett Díaz Santos | 2026010796 | @Djmaanco | Frontend |
| 5 | Nicole Nohemy Sánchez Menjívar | 2026010813 | @nicolenohemysanchez | BA / Documentación |
| 6 | Walter Alejandro Flores Hernández | 2026011012 | @floreswa90 | Documentación |
| 7 | Carlos Mario Ventura Velásquez | 2026011585 | @mdealerdude | Backend |
| 8 | Bayron Alexander Orellana Rojas | 2026011707 | @crislomsu | Backend |
| 9 | Erika Alexandra Fuentes Ortiz | 2026011709 | @fuenteserika221-spec | QA |
| 10 | William Ariel Melgar Rivas | 2026011736 | @wamr01 | Frontend |
| 11 | Alejandro Sebastián Merino Ventura | 2026020122 | @mknz05 | BD / Entidades |

---

## Ejecución local desde cero

Requiere Docker Desktop activo y Git Bash. La base PostgreSQL es local y contiene
solo datos sintéticos; no necesita Neon ni las credenciales de otro integrante.

```bash
cd /c/Users/hlopez/medisuite
# Crear secretos locales solo si el archivo aún no existe.
if [ ! -f .env.local ]; then
  printf 'LOCAL_DB_PASSWORD=%s\nLOCAL_JWT_SECRET=%s\n' "$(openssl rand -hex 24)" "$(openssl rand -hex 32)" > .env.local
fi
docker compose --env-file .env.local -f compose.local.yml up --build -d --wait
```

`compose.local.yml` crea PostgreSQL 16, ejecuta `schema.sql` y `seed.sql` una sola
vez en un volumen vacío, y arranca Java 21/Spring Boot y React 19. Flyway aplica
las migraciones V10–V15. Conserva `.env.local` para reutilizar ese volumen.

| Servicio | URL |
|---|---|
| Aplicación | http://localhost:5173 |
| API / salud | http://localhost:8097/actuator/health |
| Swagger | http://localhost:8097/swagger-ui/index.html |

Usuarios y contraseñas de demostración: [CUENTAS_DEMO.md](CUENTAS_DEMO.md).

```bash
# Estado y diagnóstico
docker compose --env-file .env.local -f compose.local.yml ps
docker compose --env-file .env.local -f compose.local.yml logs --tail=80 backend
# Detener conservando todos los datos locales
docker compose --env-file .env.local -f compose.local.yml down
```

No uses `down -v` si necesitas conservar los datos: elimina los volúmenes locales.
Los puertos 5173 y 8097 deben estar libres. Usa `localhost` en el navegador.

## Conexión a una base externa

El archivo original `docker-compose.yml` continúa disponible para una base ya
configurada. Copia `.env.example` a `.env` y completa una conexión de desarrollo
propia antes de ejecutar `docker compose up --build`. El arranque aplica
migraciones; no apuntes las pruebas a la base compartida del equipo.
Consulta [database/README.md](database/README.md) para el orden de instalación.
No publiques `.env`, `.env.local` ni secretos de conexión.

## Validación

Para ejecutar pruebas fuera de los contenedores necesitas Java 21, Maven,
Node.js 22.18 o superior, pnpm 11.18 y Docker Desktop activo.

```bash
(cd frontend && pnpm install --frozen-lockfile)
bash scripts/validate-all.sh
```

El script ejecuta pruebas unitarias y de integración contra PostgreSQL desechable,
TypeScript de ambos proyectos, regresiones de fechas, build y lint del frontend.
Comandos individuales:

```bash
(cd backend && mvn -B -ntp '-Dtest=*Test,*Tests,*IT' test)
(cd frontend && pnpm typecheck && pnpm test && pnpm build && pnpm lint)
(cd frontend && pnpm audit --prod)
```

`mvn test` sin el filtro no incluye todos los archivos `*IT`.
Las pruebas de integración no requieren una conexión a Neon.

## Funciones

- Pacientes, expediente, signos vitales, recetas e impresión.
- Agenda por doctor, disponibilidad, creación, cancelación y reprogramación de citas.
- Dashboard y permisos por rol y clínica.
- Cola de recordatorios y respaldos programados. La cola registra recordatorios
  pendientes; no incluye entrega real por correo o SMS.

## Archivos del proyecto

- `backend/`, `frontend/`: código, configuración y pruebas.
- `database/`: esquema base, datos demo e instrucciones SQL.
- `docs/avances/Avance1_Grupo7_medisuit.pdf` y `docs/avances/Avance2_Grupo7_medisuit.pdf`: avances finales.
- `README.md`, `CUENTAS_DEMO.md`: ejecución y acceso.
- `compose.local.yml`: instalación local aislada; `docker-compose.yml`: base externa.
- `.github/workflows/`, `scripts/`: validaciones automatizadas.
