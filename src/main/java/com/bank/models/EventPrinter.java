package com.bank.models;

import com.bank.ui.components.SimulationEventsTable;

public class EventPrinter implements SimulationListener{
    private final SimulationEventsTable eventsTable;

    public EventPrinter(SimulationEventsTable eventsTable) {
        this.eventsTable = eventsTable;
    }

    public void printEvent( SimulationEventRecord simulationEventRecord ) {
        Employee e = simulationEventRecord.event().getEmployeeStatus();
        Customer c = simulationEventRecord.event().getCustomer();
        int indoorTellerQueueSize = simulationEventRecord.indoorTellerQueueSize();
        int outdoorTellerQueueSize = simulationEventRecord.outdoorTellerQueueSize();
        int serviceEmployeeQueueSize = simulationEventRecord.serviceEmployeeQueueSize();
        int currentTime = simulationEventRecord.currentTime();
        String description = simulationEventRecord.description();
        SimulationEventRecord.Type type = simulationEventRecord.type();

        String empName = (e == null) ? "-" : e.getEmployeeData().toString();
        String queues = String.format("O:%d | I:%d | S:%d",
                outdoorTellerQueueSize,
                indoorTellerQueueSize,
                serviceEmployeeQueueSize
        );

        eventsTable.addEventRow(
                currentTime,
                type.toString(),
                "Cust#" + c.id(),
                c.serviceType().toString(),
                empName,
                queues,
                description
        );

        System.out.printf("%-8d | %-10s | %-12s | %-10s | %-15s | %-15s | %-15s%n",
                currentTime,
                type,
                "Cust#" + c.id(),
                c.serviceType(),
                empName,
                queues,
                description
        );
    }

    @Override
    public void onEvent(SimulationEventRecord eventRecord) {
        printEvent(eventRecord);
    }
}
