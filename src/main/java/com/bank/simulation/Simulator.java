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


    private SimulationStatistics firstRunStats;
    private SimulationStatistics firstBatchStats;
    private SimulationStatistics totalStats;

    private SimulationStatistics currentStats;
    private SimulationState state;

    public Simulator() {
        configs = SimulationConfigs.instance;
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

//        for (int reruns = 0; reruns < simulationReruns; reruns++) {
//            for (int runs = 0; runs < simulationRuns; runs++) {
        runSingleSimulation();
//            }
//        }

//        totalStats = new SimulationStatistics();
//        firstRunStats = null;
//        firstBatchStats = null;

//        for (int batch = 0; batch < simulationRetries; batch++) {
//            for (int day = 0; day < simulationDays; day++) {
//                if (batch == 0 && day == 0) {
//                    shouldDispatchEvent = true;
//                    runSingleSimulation();
//                    shouldDispatchEvent = false;
//                    firstRunStats = currentStats;
//                } else {
//                    runSingleSimulation();
//                }
//                totalStats.merge(currentStats);
//            }
//            if (batch == 0) {
//                firstBatchStats = currentStats;
//            }
//        }
//
//        if (firstRunStats != null) {
//            firstRunStats.calculateStatistics();
//        }
//        if (firstBatchStats != null) {
//            firstBatchStats.calculateStatistics();
//        }
//        totalStats.calculateStatistics();
    }

    private void runSingleSimulation() {
        resetStats();

        for(int day = 1; day <= simulationDays; day++) {
            if (state.orderState.hasOrder && state.orderState.timeTillDelivery < 0) {
                state.orderState.hasOrder = false;
                state.inventory.basementFloorUnits = Math.min(state.inventory.basementFloorUnits + state.orderState.orderSize, basementFloorMaxCapacity);
                System.out.println("Day " + day + ": Delivery of " + state.orderState.orderSize + "arrived");
            }

            updateCurrentDemand();

            System.out.print("Day " + day + ": Demand: " + state.demandState.currentDemand + " Start FF:" + state.inventory.firstFloorUnits + " Start B:" + state.inventory.basementFloorUnits);

            int consumed = Math.min(state.demandState.currentDemand, state.inventory.firstFloorUnits);
            int shortage = state.demandState.currentDemand - consumed;
            state.inventory.firstFloorUnits -= consumed;

            if (state.inventory.firstFloorUnits == 0) {
                state.inventory.firstFloorUnits += Math.min(state.inventory.basementFloorUnits, firstFloorMaxCapacity);
                state.inventory.basementFloorUnits -= Math.min(state.inventory.basementFloorUnits, firstFloorMaxCapacity);

                consumed += Math.min(shortage, state.inventory.firstFloorUnits);
                state.inventory.firstFloorUnits -= Math.min(shortage, state.inventory.firstFloorUnits);

                shortage = state.demandState.currentDemand - consumed;
            }

            if (state.orderState.hasOrder)
                state.orderState.timeTillDelivery--;

            state.reviewState.timeTillReview--;
            if (state.reviewState.timeTillReview == 0) {
                scheduleOrder();
                state.orderState.orderSize = basementFloorMaxCapacity - state.inventory.basementFloorUnits;
                state.orderState.hasOrder = true;
                state.reviewState.timeTillReview = reviewTime;
            }

            System.out.println(" Consumed: " + consumed + " Shortage: " + shortage + " End FF: " + state.inventory.firstFloorUnits + " End B: " + state.inventory.basementFloorUnits + " Days Till Review: " + state.reviewState.timeTillReview);
        }
    }

    private void resetStats() {
        state = new SimulationState();
        state.inventory.firstFloorUnits = firstFloorStartUnits;
        state.inventory.basementFloorUnits = basementFloorStartUnits;
        state.reviewState.timeTillReview = reviewTime;
        state.orderState.hasOrder = false;
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

    //    private void printEvent(SimulationEventRecord.Type type, SimulationEvent event, String description) {
//        SimulationEventRecord eventRecord = new SimulationEventRecord(
//                type,
//                event,
//                description,
//                indoorTellerQueue.size(),
//                outdoorTellerQueue.size(),
//                serviceEmployeeQueue.size(),
//                currentTime
//        );
//        dispatch(eventRecord);
//    }

//    public void addListener(SimulationListener listener) {
//        listeners.add(listener);
//    }
//
//    private void dispatch(SimulationEventRecord event) {
//        if (!shouldDispatchEvent) return;
//
//        for (SimulationListener listener : listeners) {
//            listener.onEvent(event);
//        }
//    }
}
