package com.bank.controllers;

import com.bank.models.SimulationHistoryRecord;
import com.bank.ui.pages.HistoryDetailPage;
import com.bank.ui.pages.HistoryPage;
import com.bank.utils.SimulationHistoryStorage;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryPageController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HistoryPage view;
    private final SimulationHistoryStorage historyStorage;

    public HistoryPageController(HistoryPage view) {
        this.view = view;
        this.historyStorage = new SimulationHistoryStorage();
    }

    public void loadHistory() {
        List<SimulationHistoryRecord> history = historyStorage.loadHistory();
        view.clearHistory();

        if (history.isEmpty()) {
            view.showEmptyState();
            return;
        }

        for (SimulationHistoryRecord record : history) {
            SimulationHistoryRecord.SimulationParams params = record.getSimulationParams();
            String label = String.format(
                    "Simulation - %s | Days: %d | Runs: %d",
                    record.getTimestamp().format(DATE_FORMAT),
                    params.simulationDays(),
                    params.simulationRuns()
            );
            view.addHistoryItem(label, record);
        }
    }

    public void showHistoryDetail(SimulationHistoryRecord record) {
        HistoryDetailPage detailPage = new HistoryDetailPage(record);
        JFrame detailFrame = new JFrame("Simulation Details");
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setContentPane(detailPage);
        detailFrame.pack();
        detailFrame.setLocationRelativeTo(null);
        detailFrame.setVisible(true);
    }

    public void deleteHistory(SimulationHistoryRecord record) {
        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to delete this simulation record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            historyStorage.deleteSimulation(record);
            loadHistory();
        }
    }
}