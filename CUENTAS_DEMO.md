# MediSuite — Ejecución y cuentas de demostración

Sigue la instalación local de [README.md](README.md#ejecución-local-desde-cero).
Solo necesitas Docker Desktop y Git Bash para levantar la aplicación completa.
Abre http://localhost:5173 cuando los servicios estén saludables.

El tenant del formulario de acceso es `clinica-san-rafael`.
Estas cuentas se crean con `database/seed.sql` únicamente al inicializar una base local vacía:

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | beatriz.reyes.demo@medisuite.test | Demo2026! |
| Doctor | ana.martinez.demo@medisuite.test | Demo2026! |
| Enfermero/a | carlos.gomez.demo@medisuite.test | Demo2026! |
| Recepcionista | jorge.alas.demo@medisuite.test | Demo2026! |

Son credenciales públicas de demostración para datos sintéticos. Una base externa
puede tener usuarios o contraseñas diferentes. No se distribuyen credenciales de
Neon ni secretos de conexión dentro del repositorio o del ZIP.

## Prueba manual básica

1. Recepcionista: crear un paciente, editar su fecha de nacimiento y recargar para verificarla.
2. Recepcionista: agendar, reprogramar o cancelar una cita con motivo.
3. Enfermería: abrir el paciente y guardar el triaje; verificarlo en el expediente.
4. Doctor: consultar sus citas, emitir una receta desde el expediente e imprimirla.
5. Administrador: crear un doctor con especialidad y una contraseña inicial de al menos 12 caracteres.
6. El doctor nuevo puede cambiar su contraseña desde Perfil.

La impresión debe mostrar paciente, DUI, fecha, médico, medicamentos, frecuencia,
duración e indicaciones. Ante un error de red, verifica el estado en el servidor
antes de repetir una operación para evitar duplicados.

Cinco contraseñas incorrectas bloquean temporalmente la cuenta. Espera el plazo
indicado antes de reintentar. Detén los servicios con el comando `down` del README
para conservar los datos y poder continuar posteriormente.
