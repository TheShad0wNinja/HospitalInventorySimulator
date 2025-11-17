package com.bank.simulation;

import com.bank.models.EmployeeData;
import com.bank.models.ProbabilityDistribution;

import java.util.*;
import java.util.stream.Collectors;

import static com.bank.models.ServiceType.CASH;
import static com.bank.models.ServiceType.SERVICE;

public class SimulationConfigs {
    public static SimulationConfigs instance = new SimulationConfigs();

    private int outdoorQueueCapacity;
    private double cashCustomerProbability;
    private ProbabilityDistribution timeBetweenArrivalDistribution;
    private List<EmployeeData> employeeData;

    private SimulationConfigs() {
        resetParamsToDefault();
    }

    public void resetParamsToDefault() {
        outdoorQueueCapacity = 2;
        cashCustomerProbability = 0.7;

        timeBetweenArrivalDistribution = new ProbabilityDistribution(new LinkedHashMap<>(Map.of(
                0, 0.15,
                1, 0.25,
                2, 0.25,
                3, 0.35
        )));

        employeeData = new ArrayList<>();
        EmployeeData outdoorTellerData = new EmployeeData(
                EmployeeData.Area.OUTDOOR,
                CASH,
                "outdoor_teller_1",
                getDefaultTellerProbability()
        );
        EmployeeData indoorTellerData = new EmployeeData(
                EmployeeData.Area.INDOOR,
                CASH,
                "indoor_teller_1",
                getDefaultTellerProbability()
        );

        EmployeeData serviceEmployeeData = new EmployeeData(
                EmployeeData.Area.INDOOR,
                SERVICE,
                "service_emp_1",
                getDefaultServiceEmployeeProbability()
        );

        employeeData.add(outdoorTellerData);
        employeeData.add(indoorTellerData);
        employeeData.add(serviceEmployeeData);
    }

    public Map<Integer, Double> getDefaultTellerProbability() {
        return new LinkedHashMap<>() {{
            put(2, 0.2);
            put(3, 0.3);
            put(4, 0.5);
        }};
    }

    public Map<Integer, Double> getDefaultServiceEmployeeProbability() {
        return new LinkedHashMap<>() {{
            put(4, 0.2);
            put(6, 0.5);
            put(8, 0.3);
        }};
    }

    public int getOutdoorQueueCapacity() {
        return outdoorQueueCapacity;
    }

    public void setOutdoorQueueCapacity(int outdoorQueueCapacity) {
        this.outdoorQueueCapacity = outdoorQueueCapacity;
    }

    public void setEmployees(List<EmployeeData> employeeData) {
        this.employeeData = employeeData;
    }

    public double getCashCustomerProbability() {
        return cashCustomerProbability;
    }

    public void setCashCustomerProbability(double cashCustomerProbability) {
        this.cashCustomerProbability = cashCustomerProbability;
    }

    public List<EmployeeData> getIndoorServiceEmployeesData() {
        return employeeData
                .stream()
                .filter(e -> e.getType().equals(SERVICE) && e.getArea().equals(EmployeeData.Area.INDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<EmployeeData> getOutdoorCashEmployeesData() {
        return employeeData
                .stream()
                .filter(e -> e.getType().equals(CASH) && e.getArea().equals(EmployeeData.Area.OUTDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<EmployeeData> getIndoorCashEmployeesData() {
        return employeeData
                .stream()
                .filter(e -> e.getType().equals(CASH) && e.getArea().equals(EmployeeData.Area.INDOOR))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setTimeBetweenArrivalProbabilities(Map<Integer, Double> timeBetweenArrivalDistribution) {
        this.timeBetweenArrivalDistribution = new ProbabilityDistribution(timeBetweenArrivalDistribution);
    }

    public ProbabilityDistribution getTimeBetweenArrivalDistribution() {
        return timeBetweenArrivalDistribution;
    }


    public Map<Integer, Double> getTimeBetweenArrivalProbabilities() {
        return timeBetweenArrivalDistribution.getProbabilities();
    }
}
