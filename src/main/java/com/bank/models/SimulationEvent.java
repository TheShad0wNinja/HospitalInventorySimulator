package com.bank.models;

public class SimulationEvent implements Comparable<SimulationEvent> {
    @Override
    public int compareTo(SimulationEvent o) {
        return this.time - o.time ;
    }

    public enum Type {
        ARRIVAL,
        DEPARTURE,
    }

    private final Type type;
    private final int time;
    private final Customer customer;
    private Employee employee;

    public SimulationEvent(Type type, int time, Customer customer) {
        this.type = type;
        this.time = time;
        this.customer = customer;
    }

    public SimulationEvent(Type type, int time, Customer customer, Employee employee) {
        this(type, time, customer);
        this.employee = employee;
    }

    public Type getType() {
        return type;
    }

    public int getTime() {
        return time;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Employee getEmployeeStatus() {
        return employee;
    }

    @Override
    public String toString() {
        return "Event{" +
                type +
                "@" + time +
                ":" + customer.id() +
                "-" + customer.serviceType() +
                '}';
    }
}
