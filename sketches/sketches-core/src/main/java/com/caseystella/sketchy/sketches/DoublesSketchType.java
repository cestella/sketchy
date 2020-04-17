package com.caseystella.sketchy.sketches;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.datasketches.memory.Memory;
import org.apache.datasketches.quantiles.*;

public class DoublesSketchType implements SketchType<DoublesSketch> {
    DoublesSketchBuilder sketchBuilder;
    public DoublesSketchType(DoublesSketchBuilder sketchBuilder) {
       this.sketchBuilder = sketchBuilder;
    }

    @Override
    public DoublesSketch createSketch() {
        return sketchBuilder.build();
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
    public void serialize(DoublesSketch v, Output output) {
        byte[] bytes = v.toByteArray(true);
        output.writeInt(bytes.length);
        output.writeBytes(bytes);
    }

    @Override
    public DoublesSketch materialize(Input input) {
        int len = input.readInt();
        byte[] bytes = input.readBytes(len);
        return CompactDoublesSketch.heapify(Memory.wrap(bytes));
    }

    @Override
    public double getPercentile(DoublesSketch sketch, double pctile) {
        return sketch.getQuantile(pctile);
    }

    @Override
    public void write(Kryo kryo, Output output) {
       output.writeInt(sketchBuilder.getK());
    }

    @Override
    public void read(Kryo kryo, Input input) {
       sketchBuilder = new DoublesSketchBuilder().setK(input.readInt());
    }
}
