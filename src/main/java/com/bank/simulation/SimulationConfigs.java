package com.bank.simulation;

import com.bank.models.ProbabilityDistribution;
import com.bank.ui.components.ProbabilitiesTable;

import java.util.*;

public class SimulationConfigs {
    public static SimulationConfigs instance = new SimulationConfigs();

    private final int rooms = 5;
    private int reviewTime;
    private int firstFloorCapacity;
    private int basementFloorCapacity;
    private ProbabilityDistribution occupiedRoomsDistribution;
    private ProbabilityDistribution orderLeadTimeDistribution;
    private ProbabilityDistribution roomConsumptionDistribution;

    private SimulationConfigs() {
        resetParamsToDefault();
    }

    public void resetParamsToDefault() {
        reviewTime = 5;
        firstFloorCapacity = 15;
        basementFloorCapacity = 50;

        occupiedRoomsDistribution = new ProbabilityDistribution(getDefaultOccupiedRoomsProbabilities());
        orderLeadTimeDistribution = new ProbabilityDistribution(getDefaultOrderLeadTimeProbabilities());
        roomConsumptionDistribution = new ProbabilityDistribution(getDefaultRoomConsumptionProbabilities());

    }

    public int getRooms() {
        return rooms;
    }

    public int getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(int reviewTime) {
        this.reviewTime = reviewTime;
    }

    public int getFirstFloorCapacity() {
        return firstFloorCapacity;
    }

    public void setFirstFloorCapacity(int firstFloorCapacity) {
        this.firstFloorCapacity = firstFloorCapacity;
    }

    public int getBasementFloorCapacity() {
        return basementFloorCapacity;
    }

    public void setBasementFloorCapacity(int basementFloorCapacity) {
        this.basementFloorCapacity = basementFloorCapacity;
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
