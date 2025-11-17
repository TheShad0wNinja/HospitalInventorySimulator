package com.bank.simulation;

import com.bank.models.*;

import java.util.*;

public class Simulator {
    private int simulationDays = 10;
    private int simulationCustomersCount = 10;
    private int simulationRetries = 10;

    private final Random rand = new Random();
    private final SimulationConfigs configs = SimulationConfigs.instance;
    private final ArrayList<SimulationListener> listeners = new ArrayList<>();

    private List<EmployeeData> outdoorTellersData;
    private List<EmployeeData> indoorTellersData;
    private List<EmployeeData> serviceEmployeesData;
    private int outdoorQueueCapacity;
    private ProbabilityDistribution timeBetweenArrivalDistribution;
    private boolean shouldDispatchEvent;

    private final PriorityQueue<SimulationEvent> events = new PriorityQueue<>();

    private final Queue<Customer> outdoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> indoorTellerQueue = new LinkedList<>();
    private final Queue<Customer> serviceEmployeeQueue = new LinkedList<>();

    private List<Employee> outdoorTellers;
    private List<Employee> indoorTellers;
    private List<Employee> serviceEmployees;

    private SimulationStatistics firstRunStats;
    private SimulationStatistics firstBatchStats;
    private SimulationStatistics totalStats;


    private int currentTime = 0;
    private SimulationStatistics currentStats;

    public Simulator() {
    }

    public void startSimulation() {
        outdoorTellersData = configs.getOutdoorCashEmployeesData();
        indoorTellersData = configs.getIndoorCashEmployeesData();
        serviceEmployeesData = configs.getIndoorServiceEmployeesData();
        outdoorQueueCapacity = configs.getOutdoorQueueCapacity();
        timeBetweenArrivalDistribution = configs.getTimeBetweenArrivalDistribution();

        totalStats = new SimulationStatistics();
        firstRunStats = null;
        firstBatchStats = null;

        for (int batch = 0; batch < simulationRetries; batch++) {
            for (int day = 0; day < simulationDays; day++) {
                if (batch == 0 && day == 0) {
                    shouldDispatchEvent = true;
                    runSingleSimulation();
                    shouldDispatchEvent = false;
                    firstRunStats = currentStats;
                } else {
                    runSingleSimulation();
                }
                totalStats.merge(currentStats);
            }
            if (batch == 0) {
                firstBatchStats = currentStats;
            }
        }

        if (firstRunStats != null) {
            firstRunStats.calculateStatistics();
        }
        if (firstBatchStats != null) {
            firstBatchStats.calculateStatistics();
        }
        totalStats.calculateStatistics();
    }

    private void runSingleSimulation() {
        currentTime = 0;
        currentStats = new SimulationStatistics();

        outdoorTellers = outdoorTellersData.stream().map(e -> new Employee(e, outdoorTellerQueue)).toList();
        indoorTellers = indoorTellersData.stream().map(e -> new Employee(e, indoorTellerQueue)).toList();
        serviceEmployees = serviceEmployeesData.stream().map(e -> new Employee(e, serviceEmployeeQueue)).toList();

        for (int i = 0; i < simulationCustomersCount; i++) {
            int timeBetweenCustomer = timeBetweenArrivalDistribution.getProbabilityValue(rand.nextDouble());
            int arrivalTime = currentTime + timeBetweenCustomer;

            ServiceType serviceType = rand.nextDouble() <= configs.getCashCustomerProbability()
                    ? ServiceType.CASH : ServiceType.SERVICE;

            Customer customer = new Customer(serviceType, arrivalTime, i);

            events.add(new SimulationEvent(SimulationEvent.Type.ARRIVAL, arrivalTime, customer));

            currentTime = arrivalTime;
        }

        currentTime = 0;
        while (!events.isEmpty()) {
            SimulationEvent event = events.poll();
            currentTime = event.getTime();

            if (event.getType() == SimulationEvent.Type.ARRIVAL) {
                handleArrival(event);
            } else {
                handleDeparture(event);
            }
        }

        currentStats.setTotalTime(currentTime);

        outdoorTellers.forEach(e -> e.updateTotalIdle(currentTime));
        currentStats.setTotalOutdoorTellerIdleTime(
                outdoorTellers.stream()
                        .map(Employee::getTotalIdle)
                        .reduce(0, Integer::sum)
        );

        indoorTellers.forEach(e -> e.updateTotalIdle(currentTime));
        currentStats.setTotalIndoorTellerIdleTime(
                indoorTellers.stream()
                        .map(Employee::getTotalIdle)
                        .reduce(0, Integer::sum)
        );

        serviceEmployees.forEach(e -> e.updateTotalIdle(currentTime));
        currentStats.setTotalServiceEmployeeIdleTime(
                serviceEmployees.stream()
                        .map(Employee::getTotalIdle)
                        .reduce(0, Integer::sum)
        );

        currentStats.setIndoorTellersCount(outdoorTellers.size());
        currentStats.setOutdoorTellersCount(outdoorTellers.size());
        currentStats.setServiceEmployeesCount(serviceEmployees.size());
    }

    private void handleArrival(SimulationEvent event) {
        Customer c = event.getCustomer();
        printEvent(SimulationEventRecord.Type.ARRIVE, event, "Customer arrived");

        if (c.serviceType() == ServiceType.CASH)
            routeToOutdoorTeller(event);
        else
            routeToServiceEmployee(event);
    }

    private void routeToOutdoorTeller(SimulationEvent event) {
        Customer c = event.getCustomer();
        if (outdoorTellerQueue.size() >= outdoorQueueCapacity) {
            printEvent(SimulationEventRecord.Type.ROUTE, event, "Outdoor queue full → reroute indoor");
            routeToIndoorTeller(event);
        } else {
            Employee availableEmployee = outdoorTellers.stream().filter(Employee::isIdle).findFirst().orElse(null);
            if (availableEmployee != null) {
                serveCustomer(c, availableEmployee);
            } else {
                outdoorTellerQueue.offer(c);
                printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined outdoor queue");
                currentStats.addOutdoorTellerWaitingCustomer();
                currentStats.updateOutdoorTellerMaxQueueSize(outdoorTellerQueue.size());
            }

            currentStats.addOutdoorTellerCustomer();
        }
    }

    private void routeToIndoorTeller(SimulationEvent event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = indoorTellers.stream().filter(Employee::isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            indoorTellerQueue.offer(c);
            printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined indoor teller queue");
            currentStats.addIndoorTellerWaitingCustomer();
            currentStats.updateIndoorTellerMaxQueueSize(indoorTellerQueue.size());
        }

        currentStats.addIndoorTellerCustomer();
    }

    private void routeToServiceEmployee(SimulationEvent event) {
        Customer c = event.getCustomer();
        Employee availableEmployee = serviceEmployees.stream().filter(Employee::isIdle).findFirst().orElse(null);
        if (availableEmployee != null) {
            serveCustomer(c, availableEmployee);
        } else {
            serviceEmployeeQueue.offer(c);
            printEvent(SimulationEventRecord.Type.QUEUE, event, "Joined service employee queue");
            currentStats.addServiceEmployeeWaitingCustomer();
            currentStats.updateServiceEmployeeMaxQueueSize(serviceEmployeeQueue.size());
        }

        currentStats.addServiceEmployeeCustomer();
    }

    private void serveCustomer(Customer customer, Employee employee) {
        customer.setServiceTimeStart(currentTime);
        employee.setBusy(currentTime);

        EmployeeData employeeData = employee.getEmployeeData();
        int serviceTime = employeeData.getServiceTime(rand.nextDouble());
        int departureTime = currentTime + serviceTime;

        if (employeeData.getType() == ServiceType.CASH) {
            currentStats.addTotalCashServiceTime(serviceTime);
            if (employeeData.getArea() == EmployeeData.Area.OUTDOOR) {
                currentStats.addTotalOutdoorTellerWaitTime(currentTime - customer.arrivalTime());
            } else {
                currentStats.addTotalIndoorTellerWaitTime(currentTime - customer.arrivalTime());
            }
        } else {
            currentStats.addTotalServiceServiceTime(serviceTime);
            currentStats.addTotalServiceEmployeeWaitTime(currentTime - customer.arrivalTime());
        }

        SimulationEvent event = new SimulationEvent(SimulationEvent.Type.DEPARTURE, departureTime, customer, employee);

        printEvent(SimulationEventRecord.Type.SERVE, event, String.format("Service started (serviceTime=%d, depart=%d)", serviceTime, departureTime));

        events.add(event);
    }

    private void handleDeparture(SimulationEvent event) {
        currentTime = event.getTime();
        Employee employee = event.getEmployeeStatus();

        employee.setIdle(currentTime);
        printEvent(SimulationEventRecord.Type.DEPART, event, "Service completed");

        Queue<Customer> assignedQueue = employee.getAssignedQueue();
        if (!assignedQueue.isEmpty()) {
            Customer next = assignedQueue.poll();
            printEvent(SimulationEventRecord.Type.NEXT, event, "Next customer begins service");
            serveCustomer(next, employee);
        }
    }

    private void printEvent(SimulationEventRecord.Type type, SimulationEvent event, String description) {
        SimulationEventRecord eventRecord = new SimulationEventRecord(
                type,
                event,
                description,
                indoorTellerQueue.size(),
                outdoorTellerQueue.size(),
                serviceEmployeeQueue.size(),
                currentTime
        );
        dispatch(eventRecord);
    }

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    private void dispatch(SimulationEventRecord event) {
        if (!shouldDispatchEvent) return;

        for (SimulationListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void setSimulationRetries(int simulationRetries) {
        this.simulationRetries = simulationRetries;
    }

    public void setSimulationCustomersCount(int simulationCustomersCount) {
        this.simulationCustomersCount = simulationCustomersCount;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }

    public SimulationStatistics getTotalStats() {
        return totalStats;
    }

    public SimulationStatistics getFirstBatchStats() {
        return firstBatchStats;
    }

    public SimulationStatistics getFirstRunStats() {
        return firstRunStats;
    }
}
