package com.caseystella.sketchy.sketches.statistics.distribution.types.sketch;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public interface SketchType<S> {
  S createSketch();
  void addValue(S sketch, Number value);
  S merge(S s1, S s2);
  void serialize(S v, Output output);
  S materialize(Input input);
  double getPercentile(S sketch, double pctile);
}
