package com.bank.models;

import java.util.ArrayList;
import java.util.Map;

public class ProbabilityDistribution {
    private final Map<Integer, Double> probabilities;
    private final ArrayList<Range> ranges;

    public ProbabilityDistribution(Map<Integer, Double> probabilities) {
        this.probabilities = probabilities;
        this.ranges = updateProbabilityRanges();
    }

    private ArrayList<Range> updateProbabilityRanges() {
        ArrayList<Range> ranges = new ArrayList<>();
        double lastProbability = 0.0;
        for (var entry : probabilities.entrySet()) {
            // No need to add onto the last probability because it'll be avoided due to findFirst being used
            ranges.add(new Range(lastProbability , lastProbability + entry.getValue(), entry.getKey()));
            lastProbability += entry.getValue();
        }

        if (lastProbability > 1)
            throw new ArithmeticException("Probability distribution is out of range");

        return ranges;
    }

    public int getProbabilityValue(double probability) {
        return ranges.stream()
                .filter(range -> range.contains(probability))
                .findFirst()
                .map(Range::value)
                .orElseThrow();
    }

    public Map<Integer, Double> getProbabilities() {
        return probabilities;
    }
}
