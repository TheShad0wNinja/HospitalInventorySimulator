package com.hospital.simulation;

public interface SimulationEventListener {
    void onDayEvent(
            int day,
            int demand,
            int firstFloorStart,
            int basementFloorStart,
            boolean didTransfer,
            int firstFloorEnd,
            int basementFloorEnd,
            int daysTillReview,
            int orderSize,
            int leadTime
    );
    
    void onDeliveryEvent(int day, int orderSize);
}

