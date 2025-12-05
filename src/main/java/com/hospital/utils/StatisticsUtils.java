package com.hospital.utils;

import java.util.List;

public class StatisticsUtils {
    public static double calculateMean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public static double calculateStdDeviation(List<Double> values) {
        int n = values.size();
        if (n <= 1) return 0.0;
        double mean = calculateMean(values);
        double sumSquaredDiffs = values.stream()
                .mapToDouble(v -> (v - mean) * (v - mean))
                .sum();
        return Math.sqrt(sumSquaredDiffs / (n - 1));
    }

    public static double calculateVariance(List<Double> values) {
        int n = values.size();
        if (n <= 1) return 0.0;

        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double sumSquaredDiffs = values.stream()
                .mapToDouble(v -> (v - mean) * (v - mean))
                .sum();
        return sumSquaredDiffs / (n - 1);
    }
}
