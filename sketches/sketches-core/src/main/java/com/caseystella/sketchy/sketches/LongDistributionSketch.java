package com.caseystella.sketchy.sketches;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.datasketches.quantiles.DoublesSketch;
import org.apache.datasketches.quantiles.DoublesSketchBuilder;

public class LongDistributionSketch extends DistributionSketchImpl<Long, DoublesSketch> {
    public LongDistributionSketch() {
       this(new DoublesSketchBuilder());
    }
    public LongDistributionSketch(DoublesSketchBuilder builder) {
        super(new LongType(), new DoublesSketchType(builder));
    }

    public int getK() {
        return sketch.getK();
    }

    public static class LongType implements NumberType<Long> {

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
    }
}

