package com.bank.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.bank.utils.StatisticsUtils.calculateVariance;

public class SimulationData {
    public record Statistic(String label, String value) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public int totalDays = 0;

    public List<Integer> firstFloorEndUnits = new ArrayList<>();
    public List<Integer> basementFloorEndUnits = new ArrayList<>();

    public int totalShortageDays = 0;
    public int totalShortageAmount = 0;

    public int totalDemand = 0;
    public List<Integer> dailyDemandValues = new ArrayList<>();

    public int totalOrders = 0;
    public int totalLeadTime = 0;
    public int totalOrderSize = 0;
    public List<Integer> leadTimes = new ArrayList<>();
    public int totalTransfers = 0;
    
    public List<Integer> orderPlacementDays = new ArrayList<>();
    public List<Integer> deliveryDays = new ArrayList<>();

    public double avgEndingFF;
    public double avgEndingBasement;
    public double avgDailyDemand;
    public double avgLeadTime;
    public double avgOrderSize;

    public void calculateAverages() {
        avgEndingFF = firstFloorEndUnits.stream().reduce(0, Integer::sum) / (double) totalDays;
        avgEndingBasement = basementFloorEndUnits.stream().reduce(0, Integer::sum) / (double) totalDays;
        avgDailyDemand = totalDemand / (double) totalDays;
        avgLeadTime = totalLeadTime / (double) totalOrders;
        avgOrderSize = totalOrderSize / (double) totalOrders;
    }

    public static List<Statistic> calculateStatistics(List<SimulationData> simulationData) {
        List<Statistic> statistics = new ArrayList<>();

        int totalRuns = simulationData.size();

        simulationData.forEach(SimulationData::calculateAverages);
        // Total Averages
        double totalAvgEndingFF = simulationData.stream()
                .mapToDouble(sd -> sd.avgEndingFF)
                .average()
                .orElse(0.0);

        double totalAvgEndingBasement = simulationData.stream()
                .mapToDouble(sd -> sd.avgEndingBasement)
                .average()
                .orElse(0.0);

        double totalAvgDailyDemand = simulationData.stream()
                .mapToDouble(sd -> sd.avgDailyDemand)
                .average()
                .orElse(0.0);

        double totalAvgLeadTime = simulationData.stream()
                .mapToDouble(sd -> sd.avgLeadTime)
                .average()
                .orElse(0.0);

        double totalAvgOrderSize = simulationData.stream()
                .mapToDouble(sd -> sd.avgOrderSize)
                .average()
                .orElse(0.0);

        // Variances
        var avgFFEndUnits = simulationData.stream().map(sd -> sd.avgEndingFF).toList();
        double ffEndUnitsVariance = calculateVariance(avgFFEndUnits);
        var avgBFEndUnits = simulationData.stream().map(sd -> sd.avgEndingBasement).toList();
        double bfEndUnitsVariance = calculateVariance(avgBFEndUnits);
        var avgDailyDemand = simulationData.stream().map(sd -> sd.avgDailyDemand).toList();
        double dailyDemandVariance = calculateVariance(avgDailyDemand);
        var avgLeadTime = simulationData.stream().map(sd -> sd.avgLeadTime).toList();
        double leadTimeVariance = calculateVariance(avgLeadTime);

        // Shortage Related
        long runsWithShortage = simulationData.stream().filter(sd -> sd.totalShortageDays > 0).count();
        double shortageProbability = (double) runsWithShortage / totalRuns;
        double totalAvgShortageAmount = runsWithShortage > 0
                ? simulationData.stream()
                    .filter(sd -> sd.totalShortageDays > 0)
                    .mapToInt(sd -> sd.totalShortageAmount)
                    .sum()
                        / (double) runsWithShortage
                : 0.0;

        statistics.add(new Statistic("Total Average Ending FF Units", String.valueOf(totalAvgEndingFF)));
        statistics.add(new Statistic("Total Average Ending Basement Units", String.valueOf(totalAvgEndingBasement)));
        statistics.add(new Statistic("Total Average Daily Demand", String.valueOf(totalAvgDailyDemand)));
        statistics.add(new Statistic("Total Average Lead Time", String.valueOf(totalAvgLeadTime)));
        statistics.add(new Statistic("Total Average Order Size", String.valueOf(totalAvgOrderSize)));

        statistics.add(new Statistic("First Floor Ending Units Variance", String.valueOf(ffEndUnitsVariance)));
        statistics.add(new Statistic("Basement Floor Ending Units Variance", String.valueOf(bfEndUnitsVariance)));
        statistics.add(new Statistic("Daily Demand Variance", String.valueOf(dailyDemandVariance)));
        statistics.add(new Statistic("Lead Time Variance", String.valueOf(leadTimeVariance)));

        statistics.add(new Statistic("Runs with Shortage", String.valueOf(runsWithShortage)));
        statistics.add(new Statistic("Probability of Shortage", String.valueOf(shortageProbability)));
        statistics.add(new Statistic("Average Shortage Amount", String.valueOf(totalAvgShortageAmount)));

        return statistics;
    }


}
