package com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl;

import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchTypes;
import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberTypes;
import org.apache.datasketches.quantiles.DoublesSketch;

public class DoubleDistributionSketch extends DistributionSketchImpl<Double, DoublesSketch> {
  public DoubleDistributionSketch() {
    super();
  }

  public DoubleDistributionSketch(int k) {
    super(k);
  }

  @Override
  protected NumberType<Double> createNumberType() {
    return NumberTypes.DOUBLE.get();
  }

  @Override
  protected SketchType<DoublesSketch> createSketchType(int k) {
    return SketchTypes.DOUBLES_SKETCH.create(k);
  }

  @Override
  protected DistributionSketchImpl<Double, DoublesSketch> createNew() {
    return new DoubleDistributionSketch(this.getK());
  }

}
