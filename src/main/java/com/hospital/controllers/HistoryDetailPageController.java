package com.hospital.controllers;

import com.hospital.models.SimulationHistoryRecord;
import com.hospital.ui.components.ProbabilitiesTable;
import com.hospital.ui.components.SimulationEventsTable;
import com.hospital.ui.components.SimulationStatisticsTable;
import com.hospital.ui.pages.HistoryDetailPage;
import com.hospital.utils.StatisticsVisualization;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class HistoryDetailPageController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HistoryDetailPage view;
    private final SimulationHistoryRecord record;

    public HistoryDetailPageController(HistoryDetailPage view, SimulationHistoryRecord record) {
        this.view = view;
        this.record = record;

        loadRecord();
    }

    private void loadRecord() {
        view.setSubtitleText("Run Date: " + record.getTimestamp().format(DATE_FORMAT));

        loadGeneralConfigs();
        loadDistributions();
        loadEvents();
        loadStatistics();
        loadCharts();
    }

    private void loadGeneralConfigs() {
        SimulationHistoryRecord.SimulationConfigSnapshot config = record.getConfigSnapshot();
        SimulationHistoryRecord.SimulationParams params = record.getSimulationParams();

        String[][] configs = new String[][]{
                {"Review Interval (days)", String.valueOf(config.getReviewTime())},
                {"First Floor Max Capacity", String.valueOf(config.getFirstFloorMaxCapacity())},
                {"Basement Max Capacity", String.valueOf(config.getBasementFloorMaxCapacity())},
                {"First Floor Start Units", String.valueOf(config.getFirstFloorStartUnits())},
                {"Basement Start Units", String.valueOf(config.getBasementFloorStartUnits())},
                {"Simulation Days", String.valueOf(params.simulationDays())},
                {"Simulation Runs", String.valueOf(params.simulationRuns())}
        };
        view.setGeneralConfigPanelCells(configs);
    }

    private void loadDistributions() {
        SimulationHistoryRecord.SimulationConfigSnapshot config = record.getConfigSnapshot();

        addDistributionTable("Occupied Rooms Distribution", config.getOccupiedRoomsProbabilities());
        addDistributionTable("Order Lead Time Distribution", config.getOrderLeadTimeProbabilities());
        addDistributionTable("Room Consumption Distribution", config.getRoomConsumptionProbabilities());
    }

    private void addDistributionTable(String title, Map<Integer, Double> probabilities) {
        ProbabilitiesTable table = new ProbabilitiesTable(probabilities);
        table.setEnabled(false);
        view.addDistributionTable(title, table);
    }

    private void loadEvents() {
        SimulationEventsTable eventsTable = new SimulationEventsTable();
        eventsTable.setEnabled(false);

        for (SimulationHistoryRecord.EventRow event : record.getEvents()) {
            eventsTable.addEventRow(
                    event.getDay(),
                    event.getDemand(),
                    event.getFirstFloorStart(),
                    event.getBasementFloorStart(),
                    event.isDidTransfer() ? "Yes" : "No",
                    event.getFirstFloorEnd(),
                    event.getBasementFloorEnd(),
                    event.getDaysTillReview(),
                    event.getOrderSize() == null ? "N/A" : event.getOrderSize().toString(),
                    event.getLeadTime() == null ? "N/A" : event.getLeadTime().toString()
            );
        }

        view.addDataTable("First Run's Simulation Events", eventsTable, 400);
    }

    private void loadStatistics() {
        SimulationStatisticsTable statisticsTable = new SimulationStatisticsTable();
        statisticsTable.setStatistics(new ArrayList<>(record.getStatistics()));
        statisticsTable.setEnabled(false);
        view.addDataTable("Simulation Statistics", statisticsTable, 300);
    }

    private void loadCharts() {
        var simulationRuns = record.rebuildSimulationRuns();
        view.addChart("Average Ending First Floor Inventory",
                StatisticsVisualization.createAvgEndingFFChart(simulationRuns));
        view.addChart("Average Ending Basement Inventory",
                StatisticsVisualization.createAvgEndingBasementChart(simulationRuns));
        view.addChart("Distribution of Daily Demand",
                StatisticsVisualization.createDailyDemandHistogram(simulationRuns));
        view.addChart("Distribution of Lead Time",
                StatisticsVisualization.createLeadTimeHistogram(simulationRuns));
        view.addChart("Shortage Days Per Run",
                StatisticsVisualization.createShortageDaysChart(simulationRuns));
        view.addChart("Total Basement Transfers Per Run",
                StatisticsVisualization.createTransfersChart(simulationRuns));
        view.addChart("Ending FF & Ending B Inventory",
                StatisticsVisualization.createDualAxisInventoryChart(simulationRuns));
        view.addChart("Review Cycle Timeline",
                StatisticsVisualization.createReviewCycleTimeline(simulationRuns));
    }
}