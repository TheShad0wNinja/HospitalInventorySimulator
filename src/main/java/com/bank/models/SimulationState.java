package com.bank.models;

public class SimulationState {
    public InventoryState inventory;
    public DemandState demandState;
    public OrderState orderState;
    public ReviewState reviewState;

    public SimulationState() {
        this.inventory = new InventoryState();
        this.demandState = new DemandState();
        this.orderState = new OrderState();
        this.reviewState = new ReviewState();
    }

    public static class InventoryState{
        public int firstFloorUnits;
        public int basementFloorUnits;
    }

    public static class DemandState{
        public int currentDemand;
        public int roomsOccupied;
    }

    public static class OrderState{
        public int orderSize;
        public boolean hasOrder;
        public int timeTillDelivery;
    }

    public static class ReviewState{
        public int timeTillReview;
    }
}