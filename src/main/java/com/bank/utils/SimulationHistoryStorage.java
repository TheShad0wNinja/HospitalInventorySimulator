package com.bank.utils;

import com.bank.models.SimulationHistoryRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulationHistoryStorage {
    private static final String HISTORY_DIR = "simulation_history";
    private static final String HISTORY_FILE = "history.dat";

    private final Path historyFilePath;

    public SimulationHistoryStorage() {
        try {
            Path historyDir = Paths.get(HISTORY_DIR);
            if (!Files.exists(historyDir)) {
                Files.createDirectories(historyDir);
            }
            historyFilePath = historyDir.resolve(HISTORY_FILE);
            if (!Files.exists(historyFilePath)) {
                saveHistory(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize history storage", e);
        }
    }

    public void saveSimulation(SimulationHistoryRecord record) {
        List<SimulationHistoryRecord> history = loadHistory();
        record.setTimestamp(LocalDateTime.now());
        history.add(record);
        history.sort(Comparator.comparing(SimulationHistoryRecord::getTimestamp).reversed());
        saveHistory(history);
    }

    public List<SimulationHistoryRecord> loadHistory() {
        try {
            if (!Files.exists(historyFilePath) || Files.size(historyFilePath) == 0) {
                return new ArrayList<>();
            }
            try (ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(Files.newInputStream(historyFilePath)))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    return (List<SimulationHistoryRecord>) obj;
                }
                return new ArrayList<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveHistory(List<SimulationHistoryRecord> history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(historyFilePath)))) {
            oos.writeObject(history);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save history", e);
        }
    }

    public void deleteSimulation(SimulationHistoryRecord record) {
        List<SimulationHistoryRecord> history = loadHistory();
        history.remove(record);
        saveHistory(history);
    }
}

