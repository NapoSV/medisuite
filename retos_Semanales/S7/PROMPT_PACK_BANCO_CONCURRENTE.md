# PACK DE INSTRUCCIONES Y PLANTILLA DE CÓDIGO: MULTIBANCO CONCURRENTE

Este documento contiene las indicaciones académicas oficiales, la arquitectura del sistema y un esqueleto funcional de código en Java diseñado para ser copiado y pegado directamente en Claude (u otro asistente de IA). Su objetivo es guiar la generación del 100% del proyecto de la **Semana 7: Diseño de sistema de banco con concurrencia** de la materia Programación II.

---

## PARTE 1: PROMPT DE INGENIERÍA PARA CLAUDE
*(Copia y pega el siguiente bloque de texto en un nuevo chat de Claude junto con este archivo para iniciar el desarrollo)*

```text
Actúa como un desarrollador experto en Java (JDK 21+) y especialista en sistemas concurrentes y de alta transaccionalidad. Tu objetivo es completar el código funcional para nuestra Guía de la Semana 7: "Diseño de sistema de banco con concurrencia" de la Universidad Evangélica de El Salvador.

Debes seguir rigurosamente las especificaciones técnicas del diseño adjunto, asegurando el uso correcto de exclusión mutua (synchronized), manejo asíncrono de hilos (Callable, Future, ExecutorService) y un mecanismo robusto de consistencia de datos (Rollback simulado).

Requisitos clave que debes cumplir en el código que generes:
1. Asegurar exclusión mutua con 'synchronized' en las operaciones de balance de 'BankAccount' para evitar condiciones de carrera (race conditions).
2. Implementar la jerarquía de 'Transaction' implementando 'Callable<Receipt>'.
3. Programar la lógica de 'InterBankTransfer' con un rollback simulado: si la acreditación en el banco destino falla, se debe revertir el retiro en el banco de origen.
4. Serializar todos los objetos 'Receipt' (Comprobantes) exitosos escribiéndolos en archivos binarios individuales con extensión '.dat'.
5. Configurar un controlador de simulación ('BankSimulation') que lance un mínimo de 50 hilos concurrentes simulando transacciones intramuros e interbancarias aleatorias al mismo tiempo.
6. Generar trazas de consola extremadamente claras e informativas que muestren el ciclo de vida de cada hilo (Inicio, Procesamiento, Éxito/Fallo, Rollback si aplica) y una auditoría final que imprima el saldo de todas las cuentas.

Por favor, genera las clases completas, estructuradas en paquetes limpios (models, controllers, persistence), implementando las mejores prácticas de clean code y comentarios detallados en español.
```

---

## PARTE 2: GUÍA DE REQUERIMIENTOS Y CRITERIOS DE EVALUACIÓN

### 1. Jerarquía Transaccional Polimórfica
*   **`Receipt`** (o `Comprobante`): Clase serializable que contiene ID de transacción, tipo, cuenta origen, cuenta destino, monto, marca de tiempo y estado final (ÉXITO / FALLO / REVERTIDA).
*   **`Transaction`**: Clase abstracta o interfaz que implementa `Callable<Receipt>`. Define el comportamiento base de ejecución asíncrona.
*   **Subclases**: 
    *   `LocalWithdraw` (Retiro local de cuenta intramuros).
    *   `LocalDeposit` (Depósito local de cuenta intramuros).
    *   `InterBankTransfer` (Transferencia asíncrona interbancaria entre bancos de distintos grupos).

### 2. Entidad Banco (`Bank`) y Concurrencia
*   Cada banco gestiona un conjunto local de cuentas mediante un `HashMap<String, BankAccount>`.
*   Cada banco administra su propio **`ExecutorService`** (se sugiere un `Executors.newFixedThreadPool`) para procesar en paralelo las solicitudes de sus clientes.

### 3. Operación Interbancaria Concurrente y Rollback (Consistencia)
Cuando se ejecuta una transferencia desde el **Banco A** hacia el **Banco B**:
1.  Se bloquea la cuenta de origen en el Banco A mediante `synchronized` y se realiza el retiro.
2.  Se invoca asíncronamente el abono en el Banco B.
3.  Dado que no se usan microservicios reales, se debe simular una tasa de éxito/error (por ejemplo, 85% de éxito y 15% de fallo) en el Banco B.
4.  **Manejo de inconsistencia**: Si el abono en el Banco B falla o no responde, el sistema debe **revertir (rollback)** la operación en la cuenta de origen del Banco A, reingresando los fondos de forma sincronizada y registrando el comprobante como "REVERTIDO".
5.  Se devuelve un objeto `Future<Receipt>` que permite auditar el estado final.

### 4. Persistencia Física
*   Todas las transacciones que finalicen deben guardarse físicamente en archivos locales binarios con extensión **`.dat`**.

### 5. Criterios de Calificación (Escala de 10 puntos)
*   **Diseño UML** (Diagrama de clases detallado en formato PDF `DIAGRAMA_GRUPO_X.pdf`): **1.5 pts**
*   **Proyecto Java Funcional** (Código completo con simulación masiva de mínimo 50 hilos `JAVA_GRUPO_X.zip`): **6.0 pts**
*   **Archivos de pruebas** (Los archivos binarios `.dat` generados por la simulación): **1.5 pts**
*   **Presentación y Colaboración** (Portada formal obligatoria con **fotografía de cada integrante**; de lo contrario, se penalizará al alumno sin foto): **1.0 pts**

---

## PARTE 3: ESQUELETO DE CÓDIGO FUENTE (BOILERPLATE)

### 1. `Receipt.java` (Comprobante Serializable)
```java
package com.uees.banco.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String transactionId;
    private final String type;
    private final String originAccount;
    private final String destinationAccount;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String status; // SUCCESS, FAILED, ROLLED_BACK

    public Receipt(String transactionId, String type, String originAccount, String destinationAccount, double amount, String status) {
        this.transactionId = transactionId;
        this.type = type;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | %s | Orig: %s -> Dest: %s | Monto: $%.2f | Estado: %s", 
                timestamp, transactionId, type, originAccount, destinationAccount, amount, status);
    }

    // Getters correspondientes...
    public String getTransactionId() { return transactionId; }
    public String getStatus() { return status; }
}
```

### 2. `BankAccount.java` (Cuenta Bancaria Sincronizada)
```java
package com.uees.banco.models;

public class BankAccount {
    private final String accountNumber;
    private double balance;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    // Exclusión mutua obligatoria para el balance
    public synchronized boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public synchronized void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public synchronized double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
```

### 3. `Transaction.java` (Clase Abstracta Callable)
```java
package com.uees.banco.models;

import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class Transaction implements Callable<Receipt> {
    protected final String transactionId;
    protected final String type;
    protected final double amount;

    public Transaction(String type, double amount) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8);
        this.type = type;
        this.amount = amount;
    }
}
```

### 4. `LocalWithdraw.java` (Retiro Local)
```java
package com.uees.banco.models;

public class LocalWithdraw extends Transaction {
    private final BankAccount account;

    public LocalWithdraw(BankAccount account, double amount) {
        super("WITHDRAW_LOCAL", amount);
        this.account = account;
    }

    @Override
    public Receipt call() throws Exception {
        System.out.println("[Retiro Hilo] Iniciando retiro local de $" + amount + " en cuenta " + account.getAccountNumber());
        boolean success = account.withdraw(amount);
        String status = success ? "SUCCESS" : "FAILED";
        return new Receipt(transactionId, type, account.getAccountNumber(), "N/A", amount, status);
    }
}
```

### 5. `InterBankTransfer.java` (Transferencia con Rollback Simulado)
```java
package com.uees.banco.models;

import java.util.Random;

public class InterBankTransfer extends Transaction {
    private final BankAccount originAccount;
    private final BankAccount destinationAccount;
    private final Random random = new Random();

    public InterBankTransfer(BankAccount originAccount, BankAccount destinationAccount, double amount) {
        super("INTERBANK_TRANSFER", amount);
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
    }

    @Override
    public Receipt call() throws Exception {
        System.out.println(String.format("[Transferencia Hilo] Iniciando envío de $%s de la cuenta %s a la cuenta %s", 
                amount, originAccount.getAccountNumber(), destinationAccount.getAccountNumber()));
        
        // 1. Intentar el retiro sincronizado en el Banco A (Origen)
        boolean withdrawOk = originAccount.withdraw(amount);
        if (!withdrawOk) {
            System.err.println("[FALLO] Fondos insuficientes en origen: " + originAccount.getAccountNumber());
            return new Receipt(transactionId, type, originAccount.getAccountNumber(), destinationAccount.getAccountNumber(), amount, "FAILED");
        }

        // 2. Simular llamada concurrente y asíncrona de acreditación al Banco B
        Thread.sleep(random.nextInt(150, 400)); // Simular latencia de red
        boolean bankBSuccess = random.nextDouble() > 0.15; // 85% probabilidad de éxito

        if (bankBSuccess) {
            // Acreditación exitosa en destino
            destinationAccount.deposit(amount);
            System.out.println("[ÉXITO] Transferencia acreditada correctamente en: " + destinationAccount.getAccountNumber());
            return new Receipt(transactionId, type, originAccount.getAccountNumber(), destinationAccount.getAccountNumber(), amount, "SUCCESS");
        } else {
            // 3. FALLO EN DESTINO -> Iniciar Rollback consistente e inmediato en Origen
            System.err.println("[FALLO RED] El banco destino no respondió. Iniciando Rollback automático en cuenta origen...");
            originAccount.deposit(amount); // Reingresar fondos
            System.out.println("[ROLLBACK] Fondos restituidos en cuenta origen " + originAccount.getAccountNumber() + ". Saldo restaurado.");
            return new Receipt(transactionId, type, originAccount.getAccountNumber(), destinationAccount.getAccountNumber(), amount, "ROLLED_BACK");
        }
    }
}
```

### 6. `Bank.java` (Administrador del ExecutorService)
```java
package com.uees.banco.models;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Bank {
    private final String bankName;
    private final HashMap<String, BankAccount> accounts = new HashMap<>();
    private final ExecutorService executor;

    public Bank(String bankName, int poolSize) {
        this.bankName = bankName;
        // Cada banco tiene su administrador de hilos
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public BankAccount getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Envía la transacción para ejecutarse asíncronamente en el pool del banco
    public Future<Receipt> submitTransaction(Transaction transaction) {
        return executor.submit(transaction);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void printAuditReport() {
        System.out.println("\n--- REPORTE DE AUDITORÍA: " + bankName.toUpperCase() + " ---");
        accounts.values().forEach(acc -> 
            System.out.printf("Cuenta: %s | Saldo Auditado: $%.2f\n", acc.getAccountNumber(), acc.getBalance())
        );
    }
}
```

### 7. `PersistenceManager.java` (Guardado Binario `.dat`)
```java
package com.uees.banco.persistence;

import com.uees.banco.models.Receipt;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.File;

public class PersistenceManager {
    private static final String DIR_PATH = "transacciones_finalizadas";

    public static synchronized void saveReceipt(Receipt receipt) {
        // Asegurar que el directorio de salida existe
        File dir = new File(DIR_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = DIR_PATH + File.separator + "receta_" + receipt.getTransactionId() + ".dat";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(receipt);
            System.out.println("[Persistencia] Comprobante guardado físicamente: " + fileName);
        } catch (Exception e) {
            System.err.println("[Error Persistencia] No se pudo guardar el comprobante: " + e.getMessage());
        }
    }
}
```

### 8. `BankSimulation.java` (Orquestador de 50 Hilos Concurrentes)
```java
package com.uees.banco.controllers;

import com.uees.banco.models.*;
import com.uees.banco.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

public class BankSimulation {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SIMULACIÓN DE CAJEROS CONCURRENTE (50 HILOS) ===\n");

        // Instanciar los dos Bancos
        Bank bankA = new Bank("Banco_Grupo_1", 10);
        Bank bankB = new Bank("Banco_Grupo_2", 10);

        // Inicializar cuentas con $1,000.00 cada una
        for (int i = 1; i <= 25; i++) {
            bankA.addAccount(new BankAccount("A-100" + i, 1000.0));
            bankB.addAccount(new BankAccount("B-200" + i, 1000.0));
        }

        Random random = new Random();
        List<Future<Receipt>> futures = new ArrayList<>();

        // Lanzar un mínimo de 50 transacciones concurrentes simultáneamente
        for (int i = 1; i <= 50; i++) {
            double amount = random.nextInt(50, 200); // transacciones de entre $50 y $200
            int accountSuffixSrc = random.nextInt(1, 26);
            int accountSuffixDest = random.nextInt(1, 26);
            
            BankAccount accountA = bankA.getAccount("A-100" + accountSuffixSrc);
            BankAccount accountB = bankB.getAccount("B-200" + accountSuffixDest);

            Transaction task;
            // 50% de probabilidad de transferencia local, 50% interbancaria
            if (random.nextBoolean()) {
                task = new InterBankTransfer(accountA, accountB, amount);
                futures.add(bankA.submitTransaction(task));
            } else {
                task = new LocalWithdraw(accountA, amount);
                futures.add(bankA.submitTransaction(task));
            }
        }

        // Recuperar asíncronamente los futuros y persistirlos en archivos locales binarios
        int completedCount = 0;
        for (Future<Receipt> future : futures) {
            try {
                // Obtener el comprobante asíncrono retornado por la tarea
                Receipt receipt = future.get(); 
                completedCount++;
                
                // Persistencia de auditoría física si la transacción finalizó (incluso si falló/revertió)
                PersistenceManager.saveReceipt(receipt);
            } catch (Exception e) {
                System.err.println("[Error Simulación] No se pudo obtener resultado del futuro: " + e.getMessage());
            }
        }

        System.out.println("\n=== SIMULACIÓN COMPLETADA CON ÉXITO ===");
        System.out.println("Total transacciones procesadas y persistidas: " + completedCount);

        // Apagar pools de hilos de forma ordenada
        bankA.shutdown();
        bankB.shutdown();

        // Auditoría final e impresión de saldos consolidados
        bankA.printAuditReport();
        bankB.printAuditReport();
    }
}
```
