package com.hospital.controllers;

import com.hospital.models.SimulationHistoryRecord;
import com.hospital.simulation.SimulationConfigs;
import com.hospital.simulation.Simulator;
import com.hospital.simulation.SimulationData;
import com.hospital.simulation.SimulationEventListener;
import com.hospital.ui.components.SimulationEventsTable;
import com.hospital.ui.components.SimulationStatisticsTable;
import com.hospital.ui.pages.SimulationPage;
import com.hospital.utils.SimulationHistoryStorage;
import com.hospital.utils.StatisticsVisualization;

import javax.swing.*;
import java.util.*;

public class SimulationPageController {
    private final SimulationPage view;
    private final Simulator simulator;
    private Map<String, JTextField> parameters;
    private final SimulationEventsTable simulationEventsTable = new SimulationEventsTable();
    private final SimulationStatisticsTable statisticsTable = new SimulationStatisticsTable();
    private final SimulationHistoryStorage historyStorage = new SimulationHistoryStorage();

    public SimulationPageController(SimulationPage view) {
        this.view = view;
        this.simulator = new Simulator();
        this.simulator.setEventListener(new SimulationEventListener() {
            @Override
            public void onDayEvent(int day, int demand, int firstFloorStart, int basementFloorStart,
                                 boolean didTransfer, int firstFloorEnd, int basementFloorEnd,
                                 int daysTillReview, int orderSize, int leadTime) {
                SwingUtilities.invokeLater(() -> {
                    simulationEventsTable.addEventRow(
                        day,
                        demand,
                        firstFloorStart,
                        basementFloorStart,
                        didTransfer ? "Yes" : "No",
                        firstFloorEnd,
                        basementFloorEnd,
                        daysTillReview,
                        orderSize == -1 ? "N/A" : String.valueOf(orderSize),
                        leadTime == -1 ? "N/A" : String.valueOf(leadTime)
                    );
                });
            }

            @Override
            public void onDeliveryEvent(int day, int orderSize) {
            }
        });

        loadParams();
        setupActions();
    }

    private void loadParams() {
        parameters = view.addParameters(new String[][]{
                {"simulationDays", "Simulation Days", "10"},
                {"simulationRuns", "Simulation Runs", "10"},
        });
    }

    private void startSimulation() {
        view.clearSimulationResults();
        simulationEventsTable.clearEvents();

        simulator.setSimulationDays(Integer.parseInt(parameters.get("simulationDays").getText()));
        simulator.setSimulationRuns(Integer.parseInt(parameters.get("simulationRuns").getText()));

        simulator.startSimulation();
        
        List<SimulationData> simulationData = simulator.getSimulationData();
        List<SimulationData.Statistic> statistics = SimulationData.calculateStatistics(simulationData);
        statisticsTable.setStatistics(new ArrayList<>(statistics));
        
        view.addDataTable("First Run's Events", simulationEventsTable, 400);
        view.addDataTable("Simulation Statistics", statisticsTable, 300);
        
        view.addChart("Average Ending First Floor Inventory", 
            StatisticsVisualization.createAvgEndingFFChart(simulationData));
        view.addChart("Average Ending Basement Inventory", 
            StatisticsVisualization.createAvgEndingBasementChart(simulationData));
        view.addChart("Distribution of Daily Demand", 
            StatisticsVisualization.createDailyDemandHistogram(simulationData));
        view.addChart("Distribution of Lead Time", 
            StatisticsVisualization.createLeadTimeHistogram(simulationData));
        view.addChart("Shortage Days Per Run", 
            StatisticsVisualization.createShortageDaysChart(simulationData));
        view.addChart("Total Basement Transfers Per Run", 
            StatisticsVisualization.createTransfersChart(simulationData));
        view.addChart("Ending FF & Ending B Inventory", 
            StatisticsVisualization.createDualAxisInventoryChart(simulationData));
        view.addChart("Review Cycle Timeline", 
            StatisticsVisualization.createReviewCycleTimeline(simulationData));

        view.showResults();
        showSuccessMessage("Simulation Finished!");
        saveSimulationHistory(simulationData, statistics);
    }

    private void saveSimulationHistory(List<SimulationData> simulationData,
                                       List<SimulationData.Statistic> statistics) {
        try {
            Object[][] eventsData = simulationEventsTable.getTableData();
            List<SimulationHistoryRecord.EventRow> events = new ArrayList<>();
            for (Object[] row : eventsData) {
                events.add(new SimulationHistoryRecord.EventRow(
                        parseInt(row[0]),
                        parseInt(row[1]),
                        parseInt(row[2]),
                        parseInt(row[3]),
                        "Yes".equalsIgnoreCase(row[4].toString()),
                        parseInt(row[5]),
                        parseInt(row[6]),
                        parseInt(row[7]),
                        parseNotApplicable(row[8]),
                        parseNotApplicable(row[9])
                ));
            }

            SimulationConfigs configs = SimulationConfigs.instance;
            SimulationHistoryRecord.SimulationConfigSnapshot configSnapshot =
                    new SimulationHistoryRecord.SimulationConfigSnapshot(
                            configs.getReviewTime(),
                            configs.getFirstFloorMaxCapacity(),
                            configs.getBasementFloorMaxCapacity(),
                            configs.getFirstFloorStartUnits(),
                            configs.getBasementFloorStartUnits(),
                            configs.getOccupiedRoomsDistribution().getProbabilities(),
                            configs.getOrderLeadTimeDistribution().getProbabilities(),
                            configs.getRoomConsumptionDistribution().getProbabilities()
                    );

            SimulationHistoryRecord.SimulationParams params =
                    new SimulationHistoryRecord.SimulationParams(
                            Integer.parseInt(parameters.get("simulationDays").getText()),
                            Integer.parseInt(parameters.get("simulationRuns").getText())
                    );

            List<SimulationHistoryRecord.SimulationRunSnapshot> runSnapshots = simulationData.stream()
                    .map(SimulationHistoryRecord.SimulationRunSnapshot::fromSimulationData)
                    .toList();

            SimulationHistoryRecord record = new SimulationHistoryRecord(
                    null,
                    params,
                    configSnapshot,
                    events,
                    new ArrayList<>(statistics),
                    new ArrayList<>(runSnapshots)
            );

            historyStorage.saveSimulation(record);
        } catch (Exception e) {
            System.err.println("Failed to save simulation history: " + e.getMessage());
        }
    }

    private int parseInt(Object value) {
        return Integer.parseInt(value.toString());
    }

    private Integer parseNotApplicable(Object value) {
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        if (text.isEmpty() || "N/A".equalsIgnoreCase(text)) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setupActions() {
        view.setStartButtonAction(action -> startSimulation());
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}