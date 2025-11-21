package com.bank.controllers;

import com.bank.simulation.Simulator;
import com.bank.simulation.SimulationData;
import com.bank.simulation.SimulationEventListener;
import com.bank.ui.components.SimulationEventsTable;
import com.bank.ui.components.SimulationStatisticsTable;
import com.bank.ui.pages.SimulationPage;
import com.bank.utils.StatisticsVisualization;

import javax.swing.*;
import java.util.*;

public class SimulationPageController {
    private final SimulationPage view;
    private final Simulator simulator;
    private Map<String, JTextField> parameters;
    private final SimulationEventsTable simulationEventsTable = new SimulationEventsTable();
    private final SimulationStatisticsTable statisticsTable = new SimulationStatisticsTable();

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
    }

//    private void saveSimulationHistory() {
//        try {
//            Object[][] eventsData = simulationEventsTable.getTableData();
//            List<SimulationHistoryRecord.EventRow> events = new ArrayList<>();
//            for (Object[] row : eventsData) {
//                events.add(new SimulationHistoryRecord.EventRow(
//                        Integer.parseInt(row[0].toString()),
//                        row[1].toString(),
//                        row[2].toString(),
//                        row[3].toString(),
//                        row[4].toString(),
//                        row[5].toString(),
//                        row[6].toString()
//                ));
//            }
//
//            SimulationConfigs configs = SimulationConfigs.instance;
//            List<SimulationHistoryRecord.EmployeeConfigSnapshot> employees = new ArrayList<>();
//            for (EmployeeData emp : configs.getOutdoorCashEmployeesData()) {
//                employees.add(new SimulationHistoryRecord.EmployeeConfigSnapshot(
//                        emp.getArea().toString(),
//                        emp.getType().toString(),
//                        emp.getId(),
//                        new LinkedHashMap<>(emp.getServiceTimeProbabilities())
//                ));
//            }
//            for (EmployeeData emp : configs.getIndoorCashEmployeesData()) {
//                employees.add(new SimulationHistoryRecord.EmployeeConfigSnapshot(
//                        emp.getArea().toString(),
//                        emp.getType().toString(),
//                        emp.getId(),
//                        new LinkedHashMap<>(emp.getServiceTimeProbabilities())
//                ));
//            }
//            for (EmployeeData emp : configs.getIndoorServiceEmployeesData()) {
//                employees.add(new SimulationHistoryRecord.EmployeeConfigSnapshot(
//                        emp.getArea().toString(),
//                        emp.getType().toString(),
//                        emp.getId(),
//                        new LinkedHashMap<>(emp.getServiceTimeProbabilities())
//                ));
//            }
//
//            SimulationHistoryRecord.SimulationConfigSnapshot configSnapshot =
//                    new SimulationHistoryRecord.SimulationConfigSnapshot(
//                            configs.getOutdoorQueueCapacity(),
//                            configs.getCashCustomerProbability(),
//                            new LinkedHashMap<>(configs.getTimeBetweenArrivalProbabilities()),
//                            employees
//                    );
//
//            SimulationHistoryRecord.SimulationParams params =
//                    new SimulationHistoryRecord.SimulationParams(
//                            Integer.parseInt(simulationParamFields.get("simulation_days").getText()),
//                            Integer.parseInt(simulationParamFields.get("simulation_customers").getText()),
//                            Integer.parseInt(simulationParamFields.get("simulation_repetition").getText())
//                    );
//
//            SimulationHistoryRecord record = new SimulationHistoryRecord(
//                    null,
//                    events,
//                    new ArrayList<>(simulator.getFirstRunStats().getStatistics()),
//                    new ArrayList<>(simulator.getFirstBatchStats().getStatistics()),
//                    new ArrayList<>(simulator.getTotalStats().getStatistics()),
//                    configSnapshot,
//                    params
//            );
//
//            historyStorage.saveSimulation(record);
//        } catch (Exception e) {
//            System.err.println("Failed to save simulation history: " + e.getMessage());
//        }
//    }

    private void setupActions() {
        view.setStartButtonAction(action -> startSimulation());
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}