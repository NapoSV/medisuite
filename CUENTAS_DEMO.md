# MediSuite — Instrucciones de ejecución y cuentas de acceso

---

## Requisito previo: Docker Desktop

El proyecto se ejecuta completamente con Docker. No se requiere instalar Java, Node.js ni ninguna otra herramienta.

1. Descargar Docker Desktop desde: https://www.docker.com/products/docker-desktop/
2. Instalar y reiniciar el equipo si lo solicita.
3. Abrir Docker Desktop y esperar a que el ícono en la barra de tareas quede verde (indica que está listo).
4. Verificar desde una terminal:
   ```
   docker --version
   docker compose version
   ```
   Ambos comandos deben responder con una versión sin errores.

---

## Cómo ejecutar el proyecto

**Paso 1 — Descomprimir el ZIP**

Extraer el contenido del ZIP en una carpeta de su preferencia, por ejemplo:
```
C:\Proyectos\medisuite\
```

**Paso 2 — Abrir una terminal en esa carpeta**

En Windows: hacer clic derecho dentro de la carpeta → "Abrir en Terminal" (o abrir PowerShell y navegar hasta la carpeta).

**Paso 3 — Ejecutar el proyecto**

```bash
docker compose up --build
```

- La **primera vez** tarda aproximadamente 3–5 minutos (descarga imágenes de Java y Node, compila el código).
- Las veces siguientes tarda menos de 1 minuto.
- Cuando aparezca el mensaje `Started MediSuiteApplication in X seconds` en los logs, el sistema está listo.

**Paso 4 — Abrir en el navegador**

| Componente | URL |
|-----------|-----|
| Aplicación (frontend) | http://localhost:5173 |
| API REST (backend) | http://localhost:8097 |
| Documentación API (Swagger) | http://localhost:8097/swagger-ui/index.html |

**Para detener el proyecto**, presionar `Ctrl + C` en la terminal donde se ejecuta.

---

## Cuentas de acceso — Tenant: `clinica-san-rafael`

| Rol | Email | Contraseña |
|-----|-------|-----------|
| Administrador | beatriz.reyes.demo@medisuite.test | Demo2026! |
| Doctor | ana.martinez.demo@medisuite.test | Demo2026! |
| Enfermero/a | carlos.gomez.demo@medisuite.test | Demo2026! |
| Recepcionista | jorge.alas.demo@medisuite.test | Demo2026! |

> Cada cuenta tiene acceso a las funcionalidades correspondientes a su rol dentro del sistema.

---

## Notas técnicas

- La base de datos está alojada en Neon (PostgreSQL en la nube). No se requiere instalación local de base de datos.
- Las credenciales de conexión a la base de datos ya están incluidas en el archivo `.env` dentro del ZIP.
- El sistema implementa multi-tenancy: los datos de cada clínica están completamente aislados.
