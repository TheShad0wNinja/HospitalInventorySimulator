package com.bank.models;

import java.util.Objects;

public final class Customer {
    private final ServiceType serviceType;
    private final int arrivalTime;
    private final int id;
    private int serviceTimeStart;

    public Customer(ServiceType serviceType, int arrivalTime, int id) {
        this.serviceType = serviceType;
        this.arrivalTime = arrivalTime;
        this.id = id;
    }

    public ServiceType serviceType() {
        return serviceType;
    }

    public int arrivalTime() {
        return arrivalTime;
    }

    public int id() {
        return id;
    }

    public int getServiceTimeStart() {
        return serviceTimeStart;
    }

    public void setServiceTimeStart(int serviceTimeStart) {
        this.serviceTimeStart = serviceTimeStart;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Customer) obj;
        return Objects.equals(this.serviceType, that.serviceType) &&
                this.arrivalTime == that.arrivalTime &&
                this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceType, arrivalTime, id);
    }

    @Override
    public String toString() {
        return "Customer[" +
                "serviceType=" + serviceType + ", " +
                "arrivalTime=" + arrivalTime + ", " +
                "id=" + id + ']';
    }
}