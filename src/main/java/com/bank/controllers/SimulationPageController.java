package com.bank.controllers;

import com.bank.models.*;
import com.bank.simulation.Simulator;
import com.bank.simulation.SimulationConfigs;
import com.bank.ui.components.SimulationEventsTable;
import com.bank.ui.components.SimulationStatisticsTable;
import com.bank.ui.pages.SimulationPage;
import com.bank.utils.SimulationHistoryStorage;

import javax.swing.*;
import java.util.*;

import static com.bank.utils.SimulationVisualization.*;

public class SimulationPageController {
    private final SimulationPage view;
    private final Simulator simulator;
    private final SimulationHistoryStorage historyStorage = new SimulationHistoryStorage();
    private Map<String, JTextField> simulationParamFields;
    private final SimulationEventsTable simulationEventsTable = new SimulationEventsTable();
    private final SimulationStatisticsTable firstRunStatsTable = new SimulationStatisticsTable();
    private final SimulationStatisticsTable firstBatchStatsTable = new SimulationStatisticsTable();
    private final SimulationStatisticsTable totalStatsTable = new SimulationStatisticsTable();

    public SimulationPageController(SimulationPage view) {
        this.view = view;
        this.simulator = new Simulator();
        this.simulator.addListener(new EventPrinter(simulationEventsTable));

        loadParams();
        setupActions();
    }

    private void loadParams() {
        simulationParamFields = view.addSimulationParameter(new LinkedHashMap<>(){{
            put("simulation_days", "Simulation Days");
            put("simulation_customers", "Customers per Day");
            put("simulation_repetition", "Simulation Repetition");
        }});
    }

    private void startSimulation() {
        view.clearSimulationResults();
        simulationEventsTable.clearEvents();

        simulator.setSimulationCustomersCount(Integer.parseInt(simulationParamFields.get("simulation_customers").getText()));
        simulator.setSimulationDays(Integer.parseInt(simulationParamFields.get("simulation_days").getText()));
        simulator.setSimulationRetries(Integer.parseInt(simulationParamFields.get("simulation_repetition").getText()));

        simulator.startSimulation();

        var firstRunStats = simulator.getFirstRunStats().getStatistics();
        var firstBatchStats = simulator.getFirstBatchStats().getStatistics();
        var totalStats = simulator.getTotalStats().getStatistics();

        firstRunStatsTable.setStatistics(firstRunStats);
        firstBatchStatsTable.setStatistics(firstBatchStats);
        totalStatsTable.setStatistics(totalStats);

        view.addDataTable("First Run's Simulation Events", simulationEventsTable, 400);
        view.addDataTable("First Run Statistics", firstRunStatsTable, 300);
        view.addDataTable("First Batch Statistics", firstBatchStatsTable, 300);
        view.addDataTable("Total Statistics", totalStatsTable, 300);


        view.addChart("Average Service Times", createAvgServiceTimeChart(totalStats));
        view.addChart("Average Wait Times", createAvgWaitTimesChart(totalStats));
        view.addChart("Maximum Queue Sizes", createMaxQueueSizeChart(totalStats));
        view.addChart("Wait Probability Distribution", createWaitProbabilityPieChart(totalStats));
        view.addChart("Idle vs Busy Portion", createIdlePortionChart(totalStats));

        view.showResults();
        saveSimulationHistory();
        showSuccessMessage("Simulation Finished!");
    }

    private void saveSimulationHistory() {
        try {
            Object[][] eventsData = simulationEventsTable.getTableData();
            List<SimulationHistoryRecord.EventRow> events = new ArrayList<>();
            for (Object[] row : eventsData) {
                events.add(new SimulationHistoryRecord.EventRow(
                        Integer.parseInt(row[0].toString()),
                        row[1].toString(),
                        row[2].toString(),
                        row[3].toString(),
                        row[4].toString(),
                        row[5].toString(),
                        row[6].toString()
                ));
            }

            SimulationConfigs configs = SimulationConfigs.instance;
            List<SimulationHistoryRecord.EmployeeConfigSnapshot> employees = new ArrayList<>();
            for (EmployeeData emp : configs.getOutdoorCashEmployeesData()) {
                employees.add(new SimulationHistoryRecord.EmployeeConfigSnapshot(
                        emp.getArea().toString(),
                        emp.getType().toString(),
                        emp.getId(),
                        new LinkedHashMap<>(emp.getServiceTimeProbabilities())
                ));
            }
            for (EmployeeData emp : configs.getIndoorCashEmployeesData()) {
                employees.add(new SimulationHistoryRecord.EmployeeConfigSnapshot(
                        emp.getArea().toString(),
                        emp.getType().toString(),
                        emp.getId(),
                        new LinkedHashMap<>(emp.getServiceTimeProbabilities())
                ));
            }
            for (EmployeeData emp : configs.getIndoorServiceEmployeesData()) {
                employees.add(new SimulationHistoryRecord.EmployeeConfigSnapshot(
                        emp.getArea().toString(),
                        emp.getType().toString(),
                        emp.getId(),
                        new LinkedHashMap<>(emp.getServiceTimeProbabilities())
                ));
            }

            SimulationHistoryRecord.SimulationConfigSnapshot configSnapshot =
                    new SimulationHistoryRecord.SimulationConfigSnapshot(
                            configs.getOutdoorQueueCapacity(),
                            configs.getCashCustomerProbability(),
                            new LinkedHashMap<>(configs.getTimeBetweenArrivalProbabilities()),
                            employees
                    );

            SimulationHistoryRecord.SimulationParams params =
                    new SimulationHistoryRecord.SimulationParams(
                            Integer.parseInt(simulationParamFields.get("simulation_days").getText()),
                            Integer.parseInt(simulationParamFields.get("simulation_customers").getText()),
                            Integer.parseInt(simulationParamFields.get("simulation_repetition").getText())
                    );

            SimulationHistoryRecord record = new SimulationHistoryRecord(
                    null,
                    events,
                    new ArrayList<>(simulator.getFirstRunStats().getStatistics()),
                    new ArrayList<>(simulator.getFirstBatchStats().getStatistics()),
                    new ArrayList<>(simulator.getTotalStats().getStatistics()),
                    configSnapshot,
                    params
            );

            historyStorage.saveSimulation(record);
        } catch (Exception e) {
            System.err.println("Failed to save simulation history: " + e.getMessage());
        }
    }

    private void setupActions() {
        view.setStartButtonAction(action -> startSimulation());
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(view, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}