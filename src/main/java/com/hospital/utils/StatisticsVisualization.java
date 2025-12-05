package com.hospital.utils;

import com.hospital.simulation.SimulationData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.hospital.utils.StatisticsUtils.calculateMean;
import static com.hospital.utils.StatisticsUtils.calculateStdDeviation;

public class StatisticsVisualization {
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

    public static JFreeChart createAvgEndingFFChart(List<SimulationData> simulationData) {
        if (simulationData.isEmpty()) {
            return ChartFactory.createXYLineChart("Average Ending First Floor Inventory", "Day", "Average Ending FF", new XYSeriesCollection());
        }

        int maxDays = simulationData.getFirst().totalDays;
        XYSeries avgSeries = new XYSeries("Average");
        XYSeries upperSeries = new XYSeries("Upper 95% CI");
        XYSeries lowerSeries = new XYSeries("Lower 95% CI");

        for (int day = 0; day < maxDays; day++) {
            final int dayIndex = day;
            List<Double> values = simulationData.stream()
                .filter(sd -> dayIndex < sd.firstFloorEndUnits.size())
                .map(sd -> (double) sd.firstFloorEndUnits.get(dayIndex))
                .toList();

            if (!values.isEmpty()) {
                double mean = calculateMean(values);
                double stdDev = calculateStdDeviation(values);
                double n = values.size();
                double tValue = 1.96;
                double margin = tValue * (stdDev / Math.sqrt(n));

                avgSeries.add(day + 1, mean);
                upperSeries.add(day + 1, mean + margin);
                lowerSeries.add(day + 1, mean - margin);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(avgSeries);
        dataset.addSeries(upperSeries);
        dataset.addSeries(lowerSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Average Ending First Floor Inventory",
                "Day",
                "Average Ending FF",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.GRAY);
        renderer.setSeriesPaint(2, Color.GRAY);
        plot.setRenderer(renderer);

        return chart;
    }

    public static JFreeChart createAvgEndingBasementChart(List<SimulationData> simulationData) {
        if (simulationData.isEmpty()) {
            return ChartFactory.createXYLineChart("Average Ending Basement Inventory", "Day", "Average Ending Basement", new XYSeriesCollection());
        }

        int maxDays = simulationData.get(0).totalDays;
        XYSeries avgSeries = new XYSeries("Average");
        XYSeries upperSeries = new XYSeries("Upper 95% CI");
        XYSeries lowerSeries = new XYSeries("Lower 95% CI");

        for (int day = 0; day < maxDays; day++) {
            final int dayIndex = day;
            List<Double> values = simulationData.stream()
                .filter(sd -> dayIndex < sd.basementFloorEndUnits.size())
                .map(sd -> (double) sd.basementFloorEndUnits.get(dayIndex))
                .collect(Collectors.toList());

            if (!values.isEmpty()) {
                double mean = calculateMean(values);
                double stdDev = calculateStdDeviation(values);
                double n = values.size();
                double tValue = 1.96;
                double margin = tValue * (stdDev / Math.sqrt(n));

                avgSeries.add(day + 1, mean);
                upperSeries.add(day + 1, mean + margin);
                lowerSeries.add(day + 1, mean - margin);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(avgSeries);
        dataset.addSeries(upperSeries);
        dataset.addSeries(lowerSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Average Ending Basement Inventory",
                "Day",
                "Average Ending Basement",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.GRAY);
        renderer.setSeriesPaint(2, Color.GRAY);
        plot.setRenderer(renderer);

        return chart;
    }

    public static JFreeChart createDailyDemandHistogram(List<SimulationData> simulationData) {
        List<Integer> allDemands = new ArrayList<>();
        for (SimulationData data : simulationData) {
            allDemands.addAll(data.dailyDemandValues);
        }

        if (allDemands.isEmpty()) {
            return ChartFactory.createXYBarChart("Distribution of Daily Demand", "Demand", false, "Frequency", new XYSeriesCollection());
        }

        Map<Integer, Integer> frequency = new TreeMap<>();
        for (Integer demand : allDemands) {
            frequency.put(demand, frequency.getOrDefault(demand, 0) + 1);
        }

        XYSeries series = new XYSeries("Frequency");
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYBarChart(
                "Distribution of Daily Demand",
                "Demand",
                false,
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
    }

    public static JFreeChart createLeadTimeHistogram(List<SimulationData> simulationData) {
        List<Integer> allLeadTimes = new ArrayList<>();
        for (SimulationData data : simulationData) {
            allLeadTimes.addAll(data.leadTimes);
        }

        if (allLeadTimes.isEmpty()) {
            return ChartFactory.createXYBarChart("Distribution of Lead Time", "Lead Time", false, "Frequency", new XYSeriesCollection());
        }

        Map<Integer, Integer> frequency = new TreeMap<>();
        for (Integer leadTime : allLeadTimes) {
            frequency.put(leadTime, frequency.getOrDefault(leadTime, 0) + 1);
        }

        XYSeries series = new XYSeries("Frequency");
        for (Map.Entry<Integer, Integer> entry : frequency.entrySet()) {
            series.add(entry.getKey(), entry.getValue());
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYBarChart(
                "Distribution of Lead Time",
                "Lead Time",
                false,
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
    }

    public static JFreeChart createShortageDaysChart(List<SimulationData> simulationData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < simulationData.size(); i++) {
            dataset.addValue(simulationData.get(i).totalShortageDays, "Shortage Days", "Run " + (i + 1));
        }

        return ChartFactory.createBarChart(
                "Shortage Days Per Run",
                "Run",
                "Shortage Days",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    public static JFreeChart createTransfersChart(List<SimulationData> simulationData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < simulationData.size(); i++) {
            dataset.addValue(simulationData.get(i).totalTransfers, "Transfers", "Run " + (i + 1));
        }

        return ChartFactory.createBarChart(
                "Total Basement Transfers Per Run",
                "Run",
                "Number of Transfers",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    public static JFreeChart createDualAxisInventoryChart(List<SimulationData> simulationData) {
        if (simulationData.isEmpty() || simulationData.getFirst().firstFloorEndUnits.isEmpty()) {
            return ChartFactory.createXYLineChart("Ending FF & Ending B Inventory", "Day", "Units", new XYSeriesCollection());
        }

        int maxDays = simulationData.getFirst().totalDays;
        XYSeries ffSeries = new XYSeries("First Floor");
        XYSeries basementSeries = new XYSeries("Basement");

        for (int day = 0; day < maxDays; day++) {
            final int dayIndex = day;
            double avgFF = simulationData.stream()
                .filter(sd -> dayIndex < sd.firstFloorEndUnits.size())
                .mapToDouble(sd -> sd.firstFloorEndUnits.get(dayIndex))
                .average()
                .orElse(0.0);

            double avgBasement = simulationData.stream()
                .filter(sd -> dayIndex < sd.basementFloorEndUnits.size())
                .mapToDouble(sd -> sd.basementFloorEndUnits.get(dayIndex))
                .average()
                .orElse(0.0);

            ffSeries.add(day + 1, avgFF);
            basementSeries.add(day + 1, avgBasement);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ffSeries);
        dataset.addSeries(basementSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Ending FF & Ending B Inventory",
                "Day",
                "Units",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer1 = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer1.setSeriesPaint(0, Color.BLUE);
        renderer1.setSeriesPaint(1, Color.GREEN);

        NumberAxis rangeAxis2 = new NumberAxis("Basement Units");
        rangeAxis2.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(1, rangeAxis2);
        
        XYSeriesCollection basementDataset = new XYSeriesCollection(basementSeries);
        plot.setDataset(1, basementDataset);
        plot.mapDatasetToRangeAxis(1, 1);
        
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer(true, true);
        renderer2.setSeriesPaint(0, Color.GREEN);
        plot.setRenderer(1, renderer2);

        return chart;
    }

    public static JFreeChart createReviewCycleTimeline(List<SimulationData> simulationData) {
        if (simulationData.isEmpty()) {
            return ChartFactory.createXYLineChart("Review Cycle Timeline", "Day", "Event", new XYSeriesCollection());
        }

        XYSeries orderSeries = new XYSeries("Order Placed");
        XYSeries deliverySeries = new XYSeries("Delivery Arrived");

        SimulationData firstRun = simulationData.get(0);
        for (Integer day : firstRun.orderPlacementDays) {
            orderSeries.add((double) day, 1.0);
        }
        for (Integer day : firstRun.deliveryDays) {
            deliverySeries.add((double) day, 2.0);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(orderSeries);
        dataset.addSeries(deliverySeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Review Cycle Timeline",
                "Day",
                "Event Type",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesLinesVisible(1, false);

        return chart;
    }

}
