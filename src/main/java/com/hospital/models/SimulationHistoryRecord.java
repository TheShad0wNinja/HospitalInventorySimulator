package com.hospital.models;

import com.hospital.simulation.SimulationData;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SimulationHistoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private LocalDateTime timestamp;
    private final SimulationParams simulationParams;
    private final SimulationConfigSnapshot configSnapshot;
    private final List<EventRow> events;
    private final List<SimulationData.Statistic> statistics;
    private final List<SimulationRunSnapshot> simulationRuns;

    public SimulationHistoryRecord(
            LocalDateTime timestamp,
            SimulationParams simulationParams,
            SimulationConfigSnapshot configSnapshot,
            List<EventRow> events,
            List<SimulationData.Statistic> statistics,
            List<SimulationRunSnapshot> simulationRuns
    ) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
        this.simulationParams = simulationParams;
        this.configSnapshot = configSnapshot;
        this.events = new ArrayList<>(events);
        this.statistics = new ArrayList<>(statistics);
        this.simulationRuns = new ArrayList<>(simulationRuns);
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public SimulationParams getSimulationParams() {
        return simulationParams;
    }

    public SimulationConfigSnapshot getConfigSnapshot() {
        return configSnapshot;
    }

    public List<EventRow> getEvents() {
        return new ArrayList<>(events);
    }

    public List<SimulationData.Statistic> getStatistics() {
        return new ArrayList<>(statistics);
    }

    public List<SimulationData> rebuildSimulationRuns() {
        return simulationRuns.stream()
                .map(SimulationRunSnapshot::toSimulationData)
                .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationHistoryRecord that = (SimulationHistoryRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public record SimulationParams(int simulationDays, int simulationRuns) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public static class SimulationConfigSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int reviewTime;
        private final int firstFloorMaxCapacity;
        private final int basementFloorMaxCapacity;
        private final int firstFloorStartUnits;
        private final int basementFloorStartUnits;
        private final LinkedHashMap<Integer, Double> occupiedRoomsProbabilities;
        private final LinkedHashMap<Integer, Double> orderLeadTimeProbabilities;
        private final LinkedHashMap<Integer, Double> roomConsumptionProbabilities;

        public SimulationConfigSnapshot(
                int reviewTime,
                int firstFloorMaxCapacity,
                int basementFloorMaxCapacity,
                int firstFloorStartUnits,
                int basementFloorStartUnits,
                Map<Integer, Double> occupiedRoomsProbabilities,
                Map<Integer, Double> orderLeadTimeProbabilities,
                Map<Integer, Double> roomConsumptionProbabilities
        ) {
            this.reviewTime = reviewTime;
            this.firstFloorMaxCapacity = firstFloorMaxCapacity;
            this.basementFloorMaxCapacity = basementFloorMaxCapacity;
            this.firstFloorStartUnits = firstFloorStartUnits;
            this.basementFloorStartUnits = basementFloorStartUnits;
            this.occupiedRoomsProbabilities = new LinkedHashMap<>(occupiedRoomsProbabilities);
            this.orderLeadTimeProbabilities = new LinkedHashMap<>(orderLeadTimeProbabilities);
            this.roomConsumptionProbabilities = new LinkedHashMap<>(roomConsumptionProbabilities);
        }

        public int getReviewTime() {
            return reviewTime;
        }

        public int getFirstFloorMaxCapacity() {
            return firstFloorMaxCapacity;
        }

        public int getBasementFloorMaxCapacity() {
            return basementFloorMaxCapacity;
        }

        public int getFirstFloorStartUnits() {
            return firstFloorStartUnits;
        }

        public int getBasementFloorStartUnits() {
            return basementFloorStartUnits;
        }

        public Map<Integer, Double> getOccupiedRoomsProbabilities() {
            return new LinkedHashMap<>(occupiedRoomsProbabilities);
        }

        public Map<Integer, Double> getOrderLeadTimeProbabilities() {
            return new LinkedHashMap<>(orderLeadTimeProbabilities);
        }

        public Map<Integer, Double> getRoomConsumptionProbabilities() {
            return new LinkedHashMap<>(roomConsumptionProbabilities);
        }
    }

    public static class EventRow implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int day;
        private final int demand;
        private final int firstFloorStart;
        private final int basementFloorStart;
        private final boolean didTransfer;
        private final int firstFloorEnd;
        private final int basementFloorEnd;
        private final int daysTillReview;
        private final Integer orderSize;
        private final Integer leadTime;

        public EventRow(
                int day,
                int demand,
                int firstFloorStart,
                int basementFloorStart,
                boolean didTransfer,
                int firstFloorEnd,
                int basementFloorEnd,
                int daysTillReview,
                Integer orderSize,
                Integer leadTime
        ) {
            this.day = day;
            this.demand = demand;
            this.firstFloorStart = firstFloorStart;
            this.basementFloorStart = basementFloorStart;
            this.didTransfer = didTransfer;
            this.firstFloorEnd = firstFloorEnd;
            this.basementFloorEnd = basementFloorEnd;
            this.daysTillReview = daysTillReview;
            this.orderSize = orderSize;
            this.leadTime = leadTime;
        }

        public int getDay() {
            return day;
        }

        public int getDemand() {
            return demand;
        }

        public int getFirstFloorStart() {
            return firstFloorStart;
        }

        public int getBasementFloorStart() {
            return basementFloorStart;
        }

        public boolean isDidTransfer() {
            return didTransfer;
        }

        public int getFirstFloorEnd() {
            return firstFloorEnd;
        }

        public int getBasementFloorEnd() {
            return basementFloorEnd;
        }

        public int getDaysTillReview() {
            return daysTillReview;
        }

        public Integer getOrderSize() {
            return orderSize;
        }

        public Integer getLeadTime() {
            return leadTime;
        }
    }

    public static class SimulationRunSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int totalDays;
        private final List<Integer> firstFloorEndUnits;
        private final List<Integer> basementFloorEndUnits;
        private final int totalShortageDays;
        private final int totalShortageAmount;
        private final int totalDemand;
        private final List<Integer> dailyDemandValues;
        private final int totalOrders;
        private final int totalLeadTime;
        private final int totalOrderSize;
        private final List<Integer> leadTimes;
        private final int totalTransfers;
        private final List<Integer> orderPlacementDays;
        private final List<Integer> deliveryDays;

        public SimulationRunSnapshot(
                int totalDays,
                List<Integer> firstFloorEndUnits,
                List<Integer> basementFloorEndUnits,
                int totalShortageDays,
                int totalShortageAmount,
                int totalDemand,
                List<Integer> dailyDemandValues,
                int totalOrders,
                int totalLeadTime,
                int totalOrderSize,
                List<Integer> leadTimes,
                int totalTransfers,
                List<Integer> orderPlacementDays,
                List<Integer> deliveryDays
        ) {
            this.totalDays = totalDays;
            this.firstFloorEndUnits = new ArrayList<>(firstFloorEndUnits);
            this.basementFloorEndUnits = new ArrayList<>(basementFloorEndUnits);
            this.totalShortageDays = totalShortageDays;
            this.totalShortageAmount = totalShortageAmount;
            this.totalDemand = totalDemand;
            this.dailyDemandValues = new ArrayList<>(dailyDemandValues);
            this.totalOrders = totalOrders;
            this.totalLeadTime = totalLeadTime;
            this.totalOrderSize = totalOrderSize;
            this.leadTimes = new ArrayList<>(leadTimes);
            this.totalTransfers = totalTransfers;
            this.orderPlacementDays = new ArrayList<>(orderPlacementDays);
            this.deliveryDays = new ArrayList<>(deliveryDays);
        }

        public static SimulationRunSnapshot fromSimulationData(SimulationData data) {
            return new SimulationRunSnapshot(
                    data.totalDays,
                    data.firstFloorEndUnits,
                    data.basementFloorEndUnits,
                    data.totalShortageDays,
                    data.totalShortageAmount,
                    data.totalDemand,
                    data.dailyDemandValues,
                    data.totalOrders,
                    data.totalLeadTime,
                    data.totalOrderSize,
                    data.leadTimes,
                    data.totalTransfers,
                    data.orderPlacementDays,
                    data.deliveryDays
            );
        }

        public SimulationData toSimulationData() {
            SimulationData data = new SimulationData();
            data.totalDays = totalDays;
            data.firstFloorEndUnits = new ArrayList<>(firstFloorEndUnits);
            data.basementFloorEndUnits = new ArrayList<>(basementFloorEndUnits);
            data.totalShortageDays = totalShortageDays;
            data.totalShortageAmount = totalShortageAmount;
            data.totalDemand = totalDemand;
            data.dailyDemandValues = new ArrayList<>(dailyDemandValues);
            data.totalOrders = totalOrders;
            data.totalLeadTime = totalLeadTime;
            data.totalOrderSize = totalOrderSize;
            data.leadTimes = new ArrayList<>(leadTimes);
            data.totalTransfers = totalTransfers;
            data.orderPlacementDays = new ArrayList<>(orderPlacementDays);
            data.deliveryDays = new ArrayList<>(deliveryDays);
            return data;
        }
    }
}

