package com.bank.controllers;

import com.bank.simulation.SimulationConfigs;
import com.bank.ui.pages.SettingsPage;

import javax.swing.*;
import java.util.*;

public class SettingsPageController {
    private final SettingsPage view;
    private final SimulationConfigs configs;
    private Map<String, JTextField> parameters;

    public SettingsPageController(SettingsPage view) {
        this.view = view;
        this.configs = SimulationConfigs.instance;

        setupActions();
        loadParams();
    }

    public void loadParams() {
        view.clearData();

        view.setOccupiedRoomsTable(configs.getOccupiedRoomsDistribution().getProbabilities());
        view.setOrderLeadTimeTable(configs.getOrderLeadTimeDistribution().getProbabilities());
        view.setRoomConsumptionTable(configs.getRoomConsumptionDistribution().getProbabilities());

        parameters = view.addParameters(new String[][]{
                {"firstFloorCapacity", "First Floor Capacity", String.valueOf(configs.getFirstFloorCapacity())},
                {"basementFloorCapacity", "Basement Floor Capacity", String.valueOf(configs.getBasementFloorCapacity())},
                {"reviewTime", "Review Time", String.valueOf(configs.getReviewTime())},
        });
    }

    public void saveParams() {
        try {
            int firstFloorCapacity = getIntValue("firstFloorCapacity");
            int basementFloorCapacity = getIntValue("basementFloorCapacity");
            int reviewTime = getIntValue("reviewTime");

            if (firstFloorCapacity < 0 || basementFloorCapacity < 0 || reviewTime < 0) {
                showError("All values must be non-negative numbers");
                return;
            }

            configs.setFirstFloorCapacity(firstFloorCapacity);
            configs.setBasementFloorCapacity(basementFloorCapacity);
            configs.setReviewTime(reviewTime);

            var occupiedRoomProbabilities = extractProbabilitiesFromTable(view.getOccupiedRoomsTable().getTableData());
            var orderLeadTimeProbabilities = extractProbabilitiesFromTable(view.getOrderLeadTimeTable().getTableData());
            var roomConsumptionProbabilities = extractProbabilitiesFromTable(view.getRoomConsumptionTable().getTableData());

            configs.setOccupiedRoomsProbabilities(occupiedRoomProbabilities);
            configs.setOrderLeadTimeProbabilities(orderLeadTimeProbabilities);
            configs.setRoomConsumptionProbabilities(roomConsumptionProbabilities);
            configs.setReviewTime(reviewTime);

            showSuccess("Settings saved successfully!");

            loadParams();
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields");
        } catch (Exception e) {
            showError("Error saving settings: " + e.getMessage());
        }
    }

    public void resetParams() {
        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to reset all settings to default?", "Confirm Reset", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            configs.resetParamsToDefault();
            loadParams();
            showSuccess("Settings reset to defaults");
        }
    }

    private void setupActions() {
        view.setSaveButtonAction(e -> saveParams());
        view.setResetButtonAction(e -> resetParams());
    }

    private Map<Integer, Double> extractProbabilitiesFromTable(Object[][] tableData) {
        Map<Integer, Double> probabilities = new LinkedHashMap<>();

        for (Object[] row : tableData) {
            if (row[0] == null || row[1] == null) continue;

            try {
                int value = Integer.parseInt(row[0].toString());
                double probability = Double.parseDouble(row[1].toString());

                probabilities.put(value, probability);
            } catch (NumberFormatException ignored) {
            }
        }

        return probabilities;
    }

    private double getDoubleValue(String key) throws NumberFormatException {
        String value = parameters.get(key).getText();
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int getIntValue(String key) throws NumberFormatException {
        System.out.println(key + " + " + parameters);
        var value = parameters.get(key).getText();
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}