package com.bank.utils;

import com.bank.simulation.SimulationStatistics.Statistic;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.util.List;

public class SimulationVisualization {
    private static double parseStatValue(String value) {
        if (value == null) return 0;
        try {
            if (value.endsWith("%")) {
                return Double.parseDouble(value.replace("%", "").trim());
            }
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static JFreeChart createWaitProbabilityPieChart(List<Statistic> stats) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (var s : stats) {
            if (s.label().toLowerCase().contains("wait probability")) {
                double val = parseStatValue(s.value());
                dataset.setValue(s.label(), val);
            }
        }

        return ChartFactory.createPieChart(
                "Wait Probability Distribution",
                dataset,
                true, true, false
        );
    }

    public static JFreeChart createAvgWaitTimesChart(List<Statistic> stats) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        for (var s : stats) {
            String key = s.label();

            if (key.equals("Average Indoor Teller Wait Time") ||
                    key.equals("Average Outdoor Teller Wait Time") ||
                    key.equals("Average Service Employee Wait Time") ||
                    key.equals("Average Total Wait Time")) {
                ds.addValue(parseStatValue(s.value()), "Avg Wait", key);
            }
        }

        return ChartFactory.createBarChart(
                "Average Wait Times",
                "Queue Type",
                "Avg Wait Time",
                ds
        );
    }

    public static JFreeChart createMaxQueueSizeChart(List<Statistic> stats) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var s : stats) {
            String key = s.label();
            if (key.equals("Max Indoor Teller Queue Size") ||
                    key.equals("Max Outdoor Teller Queue Size") ||
                    key.equals("Max Service Employee Queue Size")) {
                ds.addValue(parseStatValue(s.value()), "Max Queue Size", key);
            }
        }
        return ChartFactory.createBarChart(
                "Maximum Queue Sizes",
                "Queue",
                "Max Size",
                ds
        );
    }

    public static JFreeChart createIdlePortionChart(List<Statistic> stats) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var s : stats) {
            String key = s.label();
            if (key.endsWith("Idle Portion")) {
                double idle = parseStatValue(s.value());
                double busy = 100.0 - idle;

                ds.addValue(idle, "Idle %", key);
                ds.addValue(busy, "Busy %", key);
            }
        }

        return ChartFactory.createStackedBarChart(
                "Idle vs Busy Time",
                "Employee",
                "Percentage",
                ds
        );
    }

    public static JFreeChart createAvgServiceTimeChart(List<Statistic> stats) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        for (var s : stats) {
            String key = s.label();
            if (key.equals("Average Cash Customer Service Time") ||
                    key.equals("Average Service Customer Service Time")) {
                ds.addValue(parseStatValue(s.value()), "Avg Service Time", key);
            }
        }

        return ChartFactory.createBarChart(
                "Average Service Times",
                "Customer Type",
                "Avg Service Time",
                ds
        );
    }
}
