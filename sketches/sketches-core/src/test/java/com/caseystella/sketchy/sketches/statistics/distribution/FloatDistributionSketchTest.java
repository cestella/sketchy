package com.caseystella.sketchy.sketches.statistics.distribution;

public class FloatDistributionSketchTest
    extends AbstractFloatingPointDistributionSketchTest<Double> {

  @Override
  DistributionSketch<Double> createSketch() {
    return DistributionSketches.FLOAT.create(1024, Double.class);
  }

  @Override
  Double convert(Double d) {
    return d;
  }

  @Override
  protected double getDelta() {
    return 1e-3;
  }

  @Override
  protected double getPercentileDelta() {
    return 1e-2;
  }
}
