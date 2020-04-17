package com.caseystella.sketchy.sketches.statistics.distribution;

import com.esotericsoftware.kryo.KryoSerializable;

import java.util.Optional;

public interface DistributionSketch<T extends Number> extends KryoSerializable {
    int getK();

    void addValue(T value);

    long getCount();

    Optional<T> getMin();

    Optional<T> getMax();

    double getMean();

    T getSum();

    double getVariance();

    double getStandardDeviation();

    double getGeometricMean();

    double getPopulationVariance();

    double getQuadraticMean();

    double getSumLogs();

    T getSumSquares();

    double getKurtosis();

    double getSkewness();

    double getPercentile(double p);

    DistributionSketch<T> merge(DistributionSketch<T> sketch);
}
