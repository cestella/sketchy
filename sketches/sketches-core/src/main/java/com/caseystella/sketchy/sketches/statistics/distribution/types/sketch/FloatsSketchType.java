package com.caseystella.sketchy.sketches.statistics.distribution.types.sketch;

import org.apache.datasketches.kll.KllFloatsSketch;
import org.apache.datasketches.memory.Memory;

public class FloatsSketchType extends AbstractDistributionSketchType<KllFloatsSketch> {
  public FloatsSketchType(int k) {
    super(k);
  }

  @Override
  protected byte[] toByteArray(KllFloatsSketch v) {
    return v.toByteArray();
  }

  @Override
  protected KllFloatsSketch heapify(byte[] heap) {
    return KllFloatsSketch.heapify(Memory.wrap(heap));
  }

  @Override
  public KllFloatsSketch createSketch() {
    return new KllFloatsSketch(k);
  }

  @Override
  public void addValue(KllFloatsSketch sketch, Number value) {
    sketch.update(value.floatValue());
  }

  @Override
  public KllFloatsSketch merge(KllFloatsSketch s1, KllFloatsSketch s2) {
    KllFloatsSketch s = new KllFloatsSketch(Math.max(s1.getK(), s2.getK()));
    s.merge(s1);
    s.merge(s2);
    return s;
  }

  @Override
  public double getPercentile(KllFloatsSketch sketch, double pctile) {
    return sketch.getQuantile(pctile);
  }

}
