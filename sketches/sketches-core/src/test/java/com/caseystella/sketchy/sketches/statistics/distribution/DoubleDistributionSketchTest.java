package com.caseystella.sketchy.sketches.statistics.distribution;

public class DoubleDistributionSketchTest extends AbstractFloatingPointDistributionSketchTest<Double>{

  @Override
  DistributionSketch<Double> createSketch() {
    return DistributionSketches.DOUBLES.create(512, Double.class);
  }

  @Override
  Double convert(Double d) {
    return d;
  }
}
