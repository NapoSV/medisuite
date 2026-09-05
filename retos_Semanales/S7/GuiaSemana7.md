# UNIVERSIDAD EVANGÉLICA DE EL SALVADOR

Facultad de Ingeniería

Ingeniería en Desarrollo de Software y Ciencia de Datos

**Programación II**

Semana 7 – Guia

**Facilitador:**

Ing. Daniel Enrique Guevara Gómez

San Salvador, El Salvador

---

## Contents

- Diseño de sistema de banco con concurrencia.............................................................. 3
  - Objetivo.................................................................................................................. 3
  - Instrucciones.......................................................................................................... 3
  - La entrega deberá contener:.................................................................................... 4
    1. Diagrama de Clases UML .................................................................................. 4
    2. Proyecto Java Funcional: ................................................................................... 4
    3. Consola de Registro Formateada ....................................................................... 4
    3. Registro de transferencia de pruebas.................................................................. 4
  - Criterios.............................................................................................................. 5

---

## Diseño de sistema de banco con concurrencia

**Modalidad:** Grupal

### Objetivo

Evolucionar el ejercicio del cajero automático simple hacia una plataforma Multibanco Concurrente, donde múltiples hilos ejecuten transacciones locales (retiros, depósitos) y transferencias asíncronas entre el Banco del Grupo 1 y los Bancos de los demás Grupos, garantizando la sincronización de las cuentas y el retorno de comprobantes mediante Callable y Future.

### Instrucciones

**Lógica del Proyecto:**

**Jerarquía Polimórfica de Transacciones:**

Crear la clase abstracta o interfaz Transaction que implemente Callable<Receipt>.

Crear las subclases derivadas: LocalWithdraw, LocalDeposit y InterBankTransfer.

**Entidad Banco y Administrador de Hilos:**

Cada grupo crea la clase Bank con un ExecutorService propio para atender solicitudes concurrentes de sus clientes.

La entidad Bank administra un HashMap<String, BankAccount> local.

**Operación Interbancaria Concurrente (Entre Grupos):**

El cliente del Banco 1 ejecuta un hilo de InterBankTransfer hacia una cuenta(BankAccount) del Banco 2.

La transacción debe:

✓ Retirar de forma sincronizada (synchronized) los fondos de la cuenta de origen en el Banco A.

✓ Invocar concurrentemente la acreditación en la cuenta de destino en el Banco B.

✓ Si la acreditación en el Banco B falla o no responde, revertir la operación en el Banco A (Manejo de Consistencia).(como no se ha revisado la conexión entre servicios usando microservicios, simular éxito y fallo de transacciones con algún random o mock o cualquier otro recurso que les facilite la implementación del punto).

✓ Devolver un objeto Future<Comprobante> indicando el estado final de la transacción.

Al no conectarnos a una base de datos, las transacciones deben quedar resguardadas en archivos .dat

### La entrega deberá contener:

**1. Diagrama de Clases UML**

Detallando las relaciones entre Bank, BankAccount, ExecutorService y Transaction.(DIAGRAMA_GRUPO_X.pdf)

**2. Proyecto Java Funcional:**

Con todo los especificado en las instrucciones de la tarea y con casos de prueba masivos (mínimo 50 hilos simulando clientes ejecutando transferencias interbancarias e intramuros al mismo tiempo). JAVA_GRUPO_X.zip

**3. Consola de Registro Formateada**

Muestra de trazas claras donde se visualice la entrada y salida de cada hilo y el saldo final auditado de todas las cuentas.

**3. Registro de transferencia de pruebas**

Es decir, los archivos .dat generados por las pruebas de 50 hilos.

### Criterios

| Criterio | Puntaje |
|---|---|
| Diseño UML (atributos, métodos y relaciones) | 1.5 pts |
| Proyecto java funcional | 6.0 pts |
| Archivos de pruebas realizadas | 1.5 pts |
| Presentación, organización y trabajo colaborativo<br>Recordar indicar en la portada quien ha participado en la entrega, así como la fotografía de cada integrante, si esta no se presenta se le restara puntos al integrante sin fotografía. | 1.0 pts |
| **Total** | **10 pts** |
