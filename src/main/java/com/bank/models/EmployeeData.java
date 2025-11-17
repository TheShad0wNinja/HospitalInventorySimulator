package com.bank.models;

import java.util.ArrayList;
import java.util.Map;

public class EmployeeData {
    public enum Area {
        INDOOR, OUTDOOR
    }

    private final Area area;
    private final ServiceType type;
    private final String id;
    private final ProbabilityDistribution serviceTimeDistribution;

    public EmployeeData(Area area, ServiceType type, String id, Map<Integer, Double> serviceTimeProbabilities) {
        this.area = area;
        this.type = type;
        this.id = id;
        this.serviceTimeDistribution = new ProbabilityDistribution(serviceTimeProbabilities);
    }

    public Area getArea() {
        return area;
    }

    public ServiceType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Map<Integer, Double> getServiceTimeProbabilities() {
        return serviceTimeDistribution.getProbabilities();
    }

    public int getServiceTime(double probability) {
        return serviceTimeDistribution.getProbabilityValue(probability);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
