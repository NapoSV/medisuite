package poc;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;

public class BackupDatSchedulerPoc {
    public static void main(String[] args) throws Exception {
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        var data = List.of("evento1", "evento2", "evento3");
        scheduler.scheduleAtFixedRate(() -> {
            try (var out = new ObjectOutputStream(new FileOutputStream("data/poc_backup.dat"))) {
                out.writeObject(data);
                System.out.println("[" + Thread.currentThread().getName() + "] backup ejecutado " + System.currentTimeMillis());
            } catch (IOException e) { e.printStackTrace(); }
        }, 0, 5, TimeUnit.SECONDS);
        Thread.sleep(15_000);
        scheduler.shutdown();
    }
}