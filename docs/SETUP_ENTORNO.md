# Setup del Entorno de Desarrollo — MediSuite

---

## Versiones requeridas (todos deben usar estas exactas)

| Herramienta | Versión | Descarga |
|-------------|---------|----------|
| Java JDK | **21 LTS** | https://adoptium.net |
| Node.js | **20 LTS** | https://nodejs.org |
| Git | Última estable | https://git-scm.com/download/win |
| Docker Desktop | Última estable | https://www.docker.com/products/docker-desktop |

---

## 1. Instalar Java JDK 21

1. Ir a https://adoptium.net
2. Seleccionar **Temurin 21 (LTS)** → Windows x64 → `.msi`
3. Ejecutar el instalador → marcar **"Set JAVA_HOME variable"**
4. Verificar:
   ```bash
   java -version
   # Debe mostrar: openjdk version "21.x.x"
   ```

---

## 2. Instalar Node.js 20 LTS

1. Ir a https://nodejs.org
2. Descargar la versión **20.x LTS** (no la "Current")
3. Instalar con opciones por defecto
4. Verificar:
   ```bash
   node -v
   # Debe mostrar: v20.x.x
   npm -v
   ```

---

## 3. Instalar Docker Desktop

1. Ir a https://www.docker.com/products/docker-desktop
2. Descargar para Windows
3. Instalar y reiniciar si lo pide
4. Abrir Docker Desktop y esperar a que el ícono en la barra de tareas quede verde
5. Verificar:
   ```bash
   docker --version
   docker compose version
   ```

> **Nota:** Docker Desktop requiere WSL 2 en Windows. Si el instalador lo pide, seguir las instrucciones para activar WSL 2.

---

## 4. Instalar Git

1. Ir a https://git-scm.com/download/win
2. Descargar e instalar (opciones por defecto)
3. Configurar tu identidad:
   ```bash
   git config --global user.name "Tu Nombre Completo"
   git config --global user.email "tu@correo.com"
   ```

---

## 5. IDE recomendado

### Backend (Java)
- **IntelliJ IDEA Community** (gratis): https://www.jetbrains.com/idea/download/
  - Al abrir el proyecto: File → Open → seleccionar la carpeta `backend/`
  - IntelliJ detecta automáticamente Maven y Spring Boot
- **VS Code** con extensión "Extension Pack for Java" (Microsoft) — alternativa más liviana

### Frontend (React)
- **VS Code** con extensiones:
  - ESLint
  - Prettier
  - Tailwind CSS IntelliSense
  - ES7+ React/Redux/React-Native snippets

### Base de datos
- **DBeaver Community** (gratis): https://dbeaver.io/download/
  - Conectar a MySQL en localhost:3306 con usuario `clinica_app`

---

## 6. Clonar el repo y configurar el entorno

```bash
# Clonar
git clone https://github.com/NapoSV/medisuite.git
cd medisuite

# Copiar variables de entorno
cp .env.example .env
```

Abrir `.env` y completar:
- `DB_ROOT_PASSWORD` — cualquier contraseña para el root de MySQL local
- `DB_PASSWORD` — contraseña para el usuario `clinica_app`
- `JWT_SECRET` — cadena aleatoria de mínimo 32 caracteres

---

## 7. Levantar la base de datos

```bash
docker compose up -d db
```

Verificar que está corriendo:
```bash
docker ps
# Debe aparecer: medisuite_db   Up
```

Conectar con DBeaver:
- Host: `localhost`, Puerto: `3306`
- Usuario: el valor de `DB_USERNAME` en tu `.env`
- Contraseña: el valor de `DB_PASSWORD`

---

## 8. Levantar el backend

```bash
cd backend
./mvnw spring-boot:run
```

Primera vez tarda unos minutos porque descarga dependencias Maven.  
Cuando veas `Started MedisuiteApplication in X.XXX seconds` — está listo.

---

## 9. Levantar el frontend

```bash
cd frontend
npm install
npm run dev
```

Abrir el navegador en http://localhost:5173

---

## Verificación final

Ejecutar todo esto y confirmar en el canal `📢 General` de Teams que funciona:

```bash
java -version        # → 21.x
node -v              # → 20.x
docker --version     # → cualquier versión reciente
git --version        # → cualquier versión reciente
docker compose up -d db && docker ps   # → medisuite_db Up
```

---

## Problemas comunes

**`./mvnw: Permission denied`** (Mac/Linux)  
→ `chmod +x mvnw` y volver a intentar.

**Puerto 3306 ya en uso**  
→ Hay un MySQL local corriendo. Detenerlo o cambiar el puerto en `docker-compose.yml` a `3307:3306`.

**Docker no inicia en Windows**  
→ Verificar que WSL 2 está instalado y que la virtualización está habilitada en la BIOS.

**Contacto:** @HECTOR o @BAYRON en Teams
