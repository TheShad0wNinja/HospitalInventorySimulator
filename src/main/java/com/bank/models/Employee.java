package com.bank.models;

import java.util.Queue;

public class Employee {
    private boolean isIdle = true;
    private int lastEventTime = 0;
    private int totalIdle = 0;
    private final EmployeeData employeeData;
    private final Queue<Customer> assignedQueue;

    public Employee(EmployeeData employeeData, Queue<Customer> assignedQueue) {
        this.employeeData = employeeData;
        this.assignedQueue = assignedQueue;
    }

    public void setBusy(int time) {
        isIdle = false;
        updateTotalIdle(time);
    }

    public void setIdle(int time) {
        isIdle = true;
        lastEventTime = time;
    }

    public void updateTotalIdle(int time) {
        totalIdle += time - lastEventTime;
    }

    public boolean isIdle() {
        return isIdle;
    }

    public int getTotalIdle() {
        return totalIdle;
    }

    public Queue<Customer> getAssignedQueue() {
        return assignedQueue;
    }

    public EmployeeData getEmployeeData() {
        return employeeData;
    }

    @Override
    public String toString() {
        return "EmployeeStatus{" +
                "totalIdle=" + totalIdle +
                ", lastEvent=" + lastEventTime +
                ", idle=" + isIdle +
                '}';
    }
}
