package com.sv.grupo7.medisuite.dat;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class DatFileDao<T extends Serializable> {

    protected final Path filePath;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected DatFileDao(String fileName) {
        this.filePath = Paths.get("data", fileName);
        try { Files.createDirectories(filePath.getParent()); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void save(List<T> items) {
        lock.writeLock().lock();
        try (var out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            out.writeObject(items);
        } catch (IOException e) {
            throw new RuntimeException("Error escribiendo .dat: " + filePath, e);
        } finally { lock.writeLock().unlock(); }
    }

    @SuppressWarnings("unchecked")
    public List<T> loadAll() {
        lock.readLock().lock();
        try {
            if (!Files.exists(filePath)) return List.of();
            try (var in = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
                return (List<T>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error leyendo .dat: " + filePath, e);
        } finally { lock.readLock().unlock(); }
    }
}