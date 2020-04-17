package com.caseystella.sketchy.sketches.statistics.distribution.types.number;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class DoubleType implements NumberType<Double> {

    @Override
    public Double zero() {
        return 0d;
    }

    @Override
    public Double minValue() {
        return Long.MIN_VALUE*1.0;
    }

    @Override
    public Double maxValue() {
        return Double.MAX_VALUE;
    }

    @Override
    public Double min(Double v1, Double v2) {
        return Double.min(v1, v2);
    }

    @Override
    public Double max(Double v1, Double v2) {
        return Double.max(v1, v2);
    }

    @Override
    public Double add(Double v1, Double v2) {
        return v1 + v2;
    }

    @Override
    public Double multiply(Double v1, Double v2) {
        return v1 * v2;
    }

    @Override
    public void serialize(Double v, Output output) {
        output.writeDouble(v);
    }

    @Override
    public Double materialize(Input input) {
        return input.readDouble();
    }
}
