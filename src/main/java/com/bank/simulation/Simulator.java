package com.bank.simulation;

import com.bank.models.*;

import java.util.*;

public class Simulator {
    private final Random rand = new Random();
    private final SimulationConfigs configs;
    private int simulationDays;
    private int simulationRuns;

    private int reviewTime;
    private int firstFloorMaxCapacity;
    private int basementFloorMaxCapacity;
    private int firstFloorStartUnits;
    private int basementFloorStartUnits;
    private ProbabilityDistribution occupiedRoomsDistribution;
    private ProbabilityDistribution orderLeadTimeDistribution;
    private ProbabilityDistribution roomConsumptionDistribution;


    private SimulationData firstRunStats;
    private SimulationData firstBatchStats;
    private SimulationData totalStats;

    private SimulationState state;
    private List<SimulationData> simulationData;
    private SimulationEventListener eventListener;

    public Simulator() {
        configs = SimulationConfigs.instance;
    }
    
    public void setEventListener(SimulationEventListener listener) {
        this.eventListener = listener;
    }

    public void startSimulation() {
        reviewTime = configs.getReviewTime();
        firstFloorMaxCapacity = configs.getFirstFloorMaxCapacity();
        basementFloorMaxCapacity = configs.getBasementFloorMaxCapacity();
        firstFloorStartUnits = configs.getFirstFloorStartUnits();
        basementFloorStartUnits = configs.getBasementFloorStartUnits();
        occupiedRoomsDistribution = configs.getOccupiedRoomsDistribution();
        orderLeadTimeDistribution = configs.getOrderLeadTimeDistribution();
        roomConsumptionDistribution = configs.getRoomConsumptionDistribution();
        simulationData = new ArrayList<>();

        for (int runs = 0; runs < simulationRuns; runs++) {
            runSingleSimulation(runs == 0);
        }

        System.out.println("Simulation completed.");
        System.out.println(SimulationData.calculateStatistics(simulationData));
    }

    private void runSingleSimulation(boolean shouldPrint) {
        resetState();

        SimulationData data = new SimulationData();
        data.totalDays = simulationDays;

        for(int day = 1; day <= simulationDays; day++) {
            if (state.orderState.hasOrder && state.orderState.timeTillDelivery == 0) {
                state.inventory.basementFloorUnits = Math.min(state.inventory.basementFloorUnits + state.orderState.orderSize, basementFloorMaxCapacity);
                data.deliveryDays.add(day);

                if(shouldPrint && eventListener != null) {
                    eventListener.onDeliveryEvent(day, state.orderState.orderSize);
                }

                state.orderState.hasOrder = false;
                state.orderState.timeTillDelivery = -1;
                state.orderState.orderSize = -1;
            }

            int firstFloorStart = state.inventory.firstFloorUnits;
            int basementFloorStart = state.inventory.basementFloorUnits;
            boolean didTransfer = false;

            updateCurrentDemand();

            data.totalDemand += state.demandState.currentDemand;
            data.dailyDemandValues.add(state.demandState.currentDemand);

            int consumed = Math.min(state.demandState.currentDemand, state.inventory.firstFloorUnits);
            int shortage = state.demandState.currentDemand - consumed;
            state.inventory.firstFloorUnits -= consumed;

            if (state.inventory.firstFloorUnits == 0) {
                didTransfer = true;
                data.totalTransfers++;
                state.inventory.firstFloorUnits += Math.min(state.inventory.basementFloorUnits, firstFloorMaxCapacity);
                state.inventory.basementFloorUnits -= Math.min(state.inventory.basementFloorUnits, firstFloorMaxCapacity);

                consumed += Math.min(shortage, state.inventory.firstFloorUnits);
                state.inventory.firstFloorUnits -= Math.min(shortage, state.inventory.firstFloorUnits);

                shortage = state.demandState.currentDemand - consumed;

                if (shortage > 0) {
                    data.totalShortageDays++;
                    data.totalShortageAmount += shortage;
                }
            }

            if (state.orderState.hasOrder)
                state.orderState.timeTillDelivery--;

            state.reviewState.timeTillReview--;
            if (state.reviewState.timeTillReview == 0) {
                data.totalOrders++;
                scheduleOrder();

                state.orderState.orderSize = basementFloorMaxCapacity - state.inventory.basementFloorUnits;
                data.totalOrderSize += state.orderState.orderSize;
                data.totalLeadTime += state.orderState.timeTillDelivery;
                data.leadTimes.add(state.orderState.timeTillDelivery);
                data.orderPlacementDays.add(day);
                state.orderState.hasOrder = true;
                state.reviewState.timeTillReview = reviewTime;
            }

            data.firstFloorEndUnits.add(state.inventory.firstFloorUnits);
            data.basementFloorEndUnits.add(state.inventory.basementFloorUnits);

            if(shouldPrint && eventListener != null) {
                eventListener.onDayEvent(
                    day,
                    state.demandState.currentDemand,
                    firstFloorStart,
                    basementFloorStart,
                    didTransfer,
                    state.inventory.firstFloorUnits,
                    state.inventory.basementFloorUnits,
                    state.reviewState.timeTillReview,
                    state.orderState.orderSize,
                    state.orderState.timeTillDelivery
                );
            }
        }

        simulationData.add(data);
    }

    private void resetState() {
        state = new SimulationState();
        state.inventory.firstFloorUnits = firstFloorStartUnits;
        state.inventory.basementFloorUnits = basementFloorStartUnits;
        state.reviewState.timeTillReview = reviewTime;
        state.orderState.hasOrder = false;
        state.orderState.timeTillDelivery = -1;
        state.orderState.orderSize = -1;
    }

    private void scheduleOrder() {
        state.orderState.timeTillDelivery = orderLeadTimeDistribution.getProbabilityValue(rand.nextDouble());
    }

    private void updateCurrentDemand() {
        int occupiedRooms = occupiedRoomsDistribution.getProbabilityValue(rand.nextDouble());

        int totalDemand = 0;
        for (int room = 0; room < occupiedRooms; room++) {
            int roomDemand = roomConsumptionDistribution.getProbabilityValue(rand.nextDouble());
            totalDemand += roomDemand;
        }

        state.demandState.currentDemand = totalDemand;
        state.demandState.roomsOccupied = occupiedRooms ;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }

    public void setSimulationRuns(int simulationRuns) {
        this.simulationRuns = simulationRuns;
    }

    public List<SimulationData> getSimulationData() {
        return simulationData;
    }
}
