package com.bank.models;

public record Range(double low, double high, int value) {
    public Range {
        if (low > high) {
            throw new IllegalArgumentException("`low` can't be > than `high`");
        }
    }
    public boolean contains(double x) {
        return low <= x && x <= high;
    }
}
