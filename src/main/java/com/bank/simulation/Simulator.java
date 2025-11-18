package com.bank.simulation;

import com.bank.models.*;

import java.util.*;

public class Simulator {
    private final Random rand = new Random();
    private final SimulationConfigs configs;
    private int simulationDays;
    private int simulationRuns;
    private int simulationReruns;

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
        int firstFloorUnits = firstFloorStartUnits;
        int basementFloorUnits = basementFloorStartUnits;
        int daysTillReview = reviewTime;

        boolean hasOrder = false;
        int timeTillDelivery = -1;
        int orderSize = 0;

        for(int day = 1; day <= simulationDays; day++) {
            if (hasOrder && timeTillDelivery < 0) {
                hasOrder = false;
                basementFloorUnits = Math.max(basementFloorUnits + orderSize, basementFloorMaxCapacity);
                timeTillDelivery = -1;
                System.out.println("Day " + day + ": Delivery of " + orderSize + "arrived");
            }

            int demand = getDemand();
            System.out.print("Day " + day + ": Demand: " + demand + " Start FF:" + firstFloorUnits + " Start B:" + basementFloorUnits);

            int consumed = Math.min(demand, firstFloorUnits);
            int shortage = demand - consumed;
            firstFloorUnits = firstFloorUnits - consumed;

            // Transfer
            if (firstFloorUnits == 0) {
                firstFloorUnits += Math.min(basementFloorUnits, firstFloorMaxCapacity);
                basementFloorUnits -= Math.min(basementFloorUnits, firstFloorMaxCapacity);

                consumed += Math.min(shortage, firstFloorUnits);
                firstFloorUnits -= Math.min(shortage, firstFloorUnits);

                shortage = demand - consumed;
            }



            if (hasOrder)
                timeTillDelivery--;

            daysTillReview--;
            if (daysTillReview == 0) {
                timeTillDelivery = getLeadTime();
                orderSize = basementFloorMaxCapacity - basementFloorUnits;
                hasOrder = true;
                daysTillReview = reviewTime;
            }

            System.out.println(" Consumed: " + consumed + " Shortage: " + shortage + " End FF: " + firstFloorUnits + " End B: " + basementFloorUnits + " Days Till Review: " + daysTillReview);
        }
    }

    private int getLeadTime() {
        return orderLeadTimeDistribution.getProbabilityValue(rand.nextDouble());
    }

    private int getDemand() {
        int occupiedRooms = occupiedRoomsDistribution.getProbabilityValue(rand.nextDouble());

        int totalDemand = 0;
        for (int room = 0; room < occupiedRooms; room++) {
            int roomDemand = roomConsumptionDistribution.getProbabilityValue(rand.nextDouble());
            totalDemand += roomDemand;
        }

        return totalDemand;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }

    public void setSimulationRuns(int simulationRuns) {
        this.simulationRuns = simulationRuns;
    }

    public void setSimulationReruns(int simulationReruns) {
        this.simulationReruns = simulationReruns;
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
