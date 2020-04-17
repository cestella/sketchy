package com.caseystella.sketchy.sketches.statistics.distribution.types.number;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LongType implements NumberType<Long> {

    @Override
    public Long zero() {
        return 0L;
    }

    @Override
    public Long minValue() {
        return Long.MIN_VALUE;
    }

    @Override
    public Long maxValue() {
        return Long.MAX_VALUE;
    }

    @Override
    public Long min(Long v1, Long v2) {
        return Long.min(v1, v2);
    }

    @Override
    public Long max(Long v1, Long v2) {
        return Long.max(v1, v2);
    }

    @Override
    public Long add(Long v1, Long v2) {
        return v1 + v2;
    }

    @Override
    public Long multiply(Long v1, Long v2) {
        return v1 * v2;
    }

    @Override
    public void serialize(Long v, Output output) {
        output.writeLong(v);
    }

    @Override
    public Long materialize(Input input) {
        return input.readLong();
    }

    @Override
    public void serialize(Long v, ObjectOutputStream output) throws IOException {
        output.writeLong(v);
    }

    @Override
    public Long materialize(ObjectInputStream input) throws IOException {
        return input.readLong();
    }
}
