package bank.service;

import java.io.*;
import java.nio.file.*;

/** Saves and loads the Bank object so data survives restarts. */
public class BankStorage {
    private final Path file;

    public BankStorage(Path file) { this.file = file; }

    public void save(Bank bank) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(bank);
        }
    }

    public Bank load() {
        if (!Files.exists(file)) return new Bank();
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (Bank) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new Bank();   // unreadable file: start fresh rather than crash
        }
    }
}
