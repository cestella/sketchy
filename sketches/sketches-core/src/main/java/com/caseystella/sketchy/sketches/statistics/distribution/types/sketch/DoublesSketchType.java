package com.caseystella.sketchy.sketches.statistics.distribution.types.sketch;

import org.apache.datasketches.memory.Memory;
import org.apache.datasketches.quantiles.*;

public class DoublesSketchType extends AbstractDistributionSketchType<DoublesSketch> {

    public DoublesSketchType(int k) {
        super(k);
    }

    @Override
    protected byte[] toByteArray(DoublesSketch v) {
        return v.toByteArray(true);
    }

    @Override
    protected DoublesSketch heapify(byte[] heap) {
        return CompactDoublesSketch.heapify(Memory.wrap(heap));
    }

    @Override
    public DoublesSketch createSketch() {
        return new DoublesSketchBuilder().setK(k).build();
    }

    @Override
    public void addValue(DoublesSketch sketch, Number value) {
        ((UpdateDoublesSketch)sketch).update(value.doubleValue());
    }

    @Override
    public DoublesSketch merge(DoublesSketch s1, DoublesSketch s2) {
        DoublesUnion builder = DoublesUnion.builder().setMaxK(Math.max(s1.getK(), s2.getK())).build();
        builder.update(s1);
        builder.update(s2);
        return builder.getResult();
    }

    @Override
    public double getPercentile(DoublesSketch sketch, double pctile) {
        return sketch.getQuantile(pctile);
    }
}
