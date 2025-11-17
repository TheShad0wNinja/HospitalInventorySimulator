package com.bank.models;

import com.bank.simulation.SimulationStatistics;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SimulationHistoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDateTime timestamp;
    private List<EventRow> events;
    private List<SimulationStatistics.Statistic> firstRunStats;
    private List<SimulationStatistics.Statistic> firstBatchStats;
    private List<SimulationStatistics.Statistic> totalStats;
    private SimulationConfigSnapshot configSnapshot;
    private SimulationParams simulationParams;

    public SimulationHistoryRecord() {
    }

    public SimulationHistoryRecord(
            LocalDateTime timestamp,
            List<EventRow> events,
            List<SimulationStatistics.Statistic> firstRunStats,
            List<SimulationStatistics.Statistic> firstBatchStats,
            List<SimulationStatistics.Statistic> totalStats,
            SimulationConfigSnapshot configSnapshot,
            SimulationParams simulationParams
    ) {
        this.timestamp = timestamp;
        this.events = events;
        this.firstRunStats = firstRunStats;
        this.firstBatchStats = firstBatchStats;
        this.totalStats = totalStats;
        this.configSnapshot = configSnapshot;
        this.simulationParams = simulationParams;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<EventRow> getEvents() {
        return events;
    }

    public void setEvents(List<EventRow> events) {
        this.events = events;
    }

    public List<SimulationStatistics.Statistic> getFirstRunStats() {
        return firstRunStats;
    }

    public void setFirstRunStats(List<SimulationStatistics.Statistic> firstRunStats) {
        this.firstRunStats = firstRunStats;
    }

    public List<SimulationStatistics.Statistic> getFirstBatchStats() {
        return firstBatchStats;
    }

    public void setFirstBatchStats(List<SimulationStatistics.Statistic> firstBatchStats) {
        this.firstBatchStats = firstBatchStats;
    }

    public List<SimulationStatistics.Statistic> getTotalStats() {
        return totalStats;
    }

    public void setTotalStats(List<SimulationStatistics.Statistic> totalStats) {
        this.totalStats = totalStats;
    }

    public SimulationConfigSnapshot getConfigSnapshot() {
        return configSnapshot;
    }

    public void setConfigSnapshot(SimulationConfigSnapshot configSnapshot) {
        this.configSnapshot = configSnapshot;
    }

    public SimulationParams getSimulationParams() {
        return simulationParams;
    }

    public void setSimulationParams(SimulationParams simulationParams) {
        this.simulationParams = simulationParams;
    }

    public static class EventRow implements Serializable {
        private static final long serialVersionUID = 1L;
        private int time;
        private String type;
        private String customer;
        private String service;
        private String employee;
        private String queues;
        private String action;

        public EventRow() {
        }

        public EventRow(int time, String type, String customer, String service, String employee, String queues, String action) {
            this.time = time;
            this.type = type;
            this.customer = customer;
            this.service = service;
            this.employee = employee;
            this.queues = queues;
            this.action = action;
        }

        public int time() { return time; }
        public String type() { return type; }
        public String customer() { return customer; }
        public String service() { return service; }
        public String employee() { return employee; }
        public String queues() { return queues; }
        public String action() { return action; }
    }

    public static class SimulationConfigSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private int outdoorQueueCapacity;
        private double cashCustomerProbability;
        private Map<Integer, Double> timeBetweenArrivalProbabilities;
        private List<EmployeeConfigSnapshot> employees;

        public SimulationConfigSnapshot() {
        }

        public SimulationConfigSnapshot(
                int outdoorQueueCapacity,
                double cashCustomerProbability,
                Map<Integer, Double> timeBetweenArrivalProbabilities,
                List<EmployeeConfigSnapshot> employees
        ) {
            this.outdoorQueueCapacity = outdoorQueueCapacity;
            this.cashCustomerProbability = cashCustomerProbability;
            this.timeBetweenArrivalProbabilities = timeBetweenArrivalProbabilities;
            this.employees = employees;
        }

        public int getOutdoorQueueCapacity() {
            return outdoorQueueCapacity;
        }

        public void setOutdoorQueueCapacity(int outdoorQueueCapacity) {
            this.outdoorQueueCapacity = outdoorQueueCapacity;
        }

        public double getCashCustomerProbability() {
            return cashCustomerProbability;
        }

        public void setCashCustomerProbability(double cashCustomerProbability) {
            this.cashCustomerProbability = cashCustomerProbability;
        }

        public Map<Integer, Double> getTimeBetweenArrivalProbabilities() {
            return timeBetweenArrivalProbabilities;
        }

        public void setTimeBetweenArrivalProbabilities(Map<Integer, Double> timeBetweenArrivalProbabilities) {
            this.timeBetweenArrivalProbabilities = timeBetweenArrivalProbabilities;
        }

        public List<EmployeeConfigSnapshot> getEmployees() {
            return employees;
        }

        public void setEmployees(List<EmployeeConfigSnapshot> employees) {
            this.employees = employees;
        }
    }

    public static class EmployeeConfigSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        private String area;
        private String type;
        private String id;
        private Map<Integer, Double> serviceTimeProbabilities;

        public EmployeeConfigSnapshot() {
        }

        public EmployeeConfigSnapshot(String area, String type, String id, Map<Integer, Double> serviceTimeProbabilities) {
            this.area = area;
            this.type = type;
            this.id = id;
            this.serviceTimeProbabilities = serviceTimeProbabilities;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<Integer, Double> getServiceTimeProbabilities() {
            return serviceTimeProbabilities;
        }

        public void setServiceTimeProbabilities(Map<Integer, Double> serviceTimeProbabilities) {
            this.serviceTimeProbabilities = serviceTimeProbabilities;
        }
    }

    public static class SimulationParams implements Serializable {
        private static final long serialVersionUID = 1L;
        private int simulationDays;
        private int simulationCustomers;
        private int simulationRepetition;

        public SimulationParams() {
        }

        public SimulationParams(int simulationDays, int simulationCustomers, int simulationRepetition) {
            this.simulationDays = simulationDays;
            this.simulationCustomers = simulationCustomers;
            this.simulationRepetition = simulationRepetition;
        }

        public int getSimulationDays() {
            return simulationDays;
        }

        public void setSimulationDays(int simulationDays) {
            this.simulationDays = simulationDays;
        }

        public int getSimulationCustomers() {
            return simulationCustomers;
        }

        public void setSimulationCustomers(int simulationCustomers) {
            this.simulationCustomers = simulationCustomers;
        }

        public int getSimulationRepetition() {
            return simulationRepetition;
        }

        public void setSimulationRepetition(int simulationRepetition) {
            this.simulationRepetition = simulationRepetition;
        }
    }
}

