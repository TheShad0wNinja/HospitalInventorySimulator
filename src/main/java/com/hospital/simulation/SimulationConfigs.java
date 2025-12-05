package com.hospital.simulation;

import com.hospital.models.ProbabilityDistribution;

import java.util.*;

public class SimulationConfigs {
    public static SimulationConfigs instance = new SimulationConfigs();

    private int reviewTime;
    private int firstFloorMaxCapacity;
    private int basementFloorMaxCapacity;
    private int firstFloorStartUnits;
    private int basementFloorStartUnits;
    private ProbabilityDistribution occupiedRoomsDistribution;
    private ProbabilityDistribution orderLeadTimeDistribution;
    private ProbabilityDistribution roomConsumptionDistribution;

    private SimulationConfigs() {
        resetParamsToDefault();
    }

    public void resetParamsToDefault() {
        reviewTime = 5;
        firstFloorMaxCapacity = 15;
        basementFloorMaxCapacity = 50;
        firstFloorStartUnits = 8;
        basementFloorStartUnits = 40;

        occupiedRoomsDistribution = new ProbabilityDistribution(getDefaultOccupiedRoomsProbabilities());
        orderLeadTimeDistribution = new ProbabilityDistribution(getDefaultOrderLeadTimeProbabilities());
        roomConsumptionDistribution = new ProbabilityDistribution(getDefaultRoomConsumptionProbabilities());

    }

    public int getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(int reviewTime) {
        this.reviewTime = reviewTime;
    }

    public int getFirstFloorMaxCapacity() {
        return firstFloorMaxCapacity;
    }

    public void setFirstFloorMaxCapacity(int firstFloorMaxCapacity) {
        this.firstFloorMaxCapacity = firstFloorMaxCapacity;
    }

    public int getBasementFloorMaxCapacity() {
        return basementFloorMaxCapacity;
    }

    public void setBasementFloorMaxCapacity(int basementFloorMaxCapacity) {
        this.basementFloorMaxCapacity = basementFloorMaxCapacity;
    }

    public ProbabilityDistribution getOccupiedRoomsDistribution() {
        return occupiedRoomsDistribution;
    }

    public ProbabilityDistribution getOrderLeadTimeDistribution() {
        return orderLeadTimeDistribution;
    }

    public ProbabilityDistribution getRoomConsumptionDistribution() {
        return roomConsumptionDistribution;
    }

    public int getBasementFloorStartUnits() {
        return basementFloorStartUnits;
    }

    public void setBasementFloorStartUnits(int basementFloorStartUnits) {
        this.basementFloorStartUnits = basementFloorStartUnits;
    }

    public int getFirstFloorStartUnits() {
        return firstFloorStartUnits;
    }

    public void setFirstFloorStartUnits(int firstFloorStartUnits) {
        this.firstFloorStartUnits = firstFloorStartUnits;
    }

    public void setOccupiedRoomsProbabilities(Map<Integer, Double> probabilities) {
        this.occupiedRoomsDistribution = new ProbabilityDistribution(probabilities);
    }

    public void setOrderLeadTimeProbabilities(Map<Integer, Double> probabilities) {
        this.orderLeadTimeDistribution = new ProbabilityDistribution(probabilities);
    }

    public void setRoomConsumptionProbabilities(Map<Integer, Double> probabilities) {
        this.roomConsumptionDistribution = new ProbabilityDistribution(probabilities);
    }

    public Map<Integer, Double> getDefaultOccupiedRoomsProbabilities() {
        return new LinkedHashMap<>(){{
            put(1, 0.1);
            put(2, 0.15);
            put(3, 0.35);
            put(4, 0.2);
            put(5, 0.2);
        }};
    }

    public Map<Integer, Double> getDefaultRoomConsumptionProbabilities() {
        return new LinkedHashMap<>(){{
            put(1, 0.7);
            put(2, 0.3);
        }};
    }

    public Map<Integer, Double> getDefaultOrderLeadTimeProbabilities() {
        return new LinkedHashMap<>(){{
            put(1, 0.35);
            put(2, 0.35);
            put(3, 0.3);
        }};
    }
}
