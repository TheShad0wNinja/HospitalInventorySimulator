package com.bank.models;

public record SimulationEventRecord(
        Type type,
        SimulationEvent event,
        String description,
        int indoorTellerQueueSize,
        int outdoorTellerQueueSize,
        int serviceEmployeeQueueSize,
        int currentTime
) {
    public enum Type {
        ARRIVE,
        DEPART,
        NEXT,
        QUEUE,
        SERVE,
        ROUTE
    }
}
