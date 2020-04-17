package com.caseystella.sketchy.sketches.statistics.distribution;

import com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl.DoubleDistributionSketch;
import com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl.FloatDistributionSketch;
import com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl.LongDistributionSketch;
import java.util.Optional;
import java.util.function.Function;

public enum DistributionSketches {
  DOUBLES(k -> new DoubleDistributionSketch(k)),
  LONG(k -> new LongDistributionSketch(k)),
  FLOAT(k -> new FloatDistributionSketch(k))
  ;
  public static final int DEFAULT_K = 128;
  Function<Integer, DistributionSketch<? extends Number>> creator;
  DistributionSketches(Function<Integer, DistributionSketch<? extends Number>> creator) {
    this.creator = creator;
  }

  public <T extends Number> DistributionSketch<T> create(Class<T> clazz) {
    return create(DEFAULT_K, clazz);
  }

  public <T extends Number> DistributionSketch<T> create(int k, Class<T> clazz) {
    return (DistributionSketch<T>) creator.apply(k);
  }
}
