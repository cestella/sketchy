package com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl;

import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchTypes;
import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberTypes;
import org.apache.datasketches.kll.KllFloatsSketch;

public class FloatDistributionSketch extends DistributionSketchImpl<Float, KllFloatsSketch> {

  public FloatDistributionSketch() {
  }

  public FloatDistributionSketch(int k) {
    super(k);
  }

  @Override
  protected NumberType<Float> createNumberType() {
    return NumberTypes.FLOAT.get();
  }

  @Override
  protected SketchType<KllFloatsSketch> createSketchType(int k) {
    return SketchTypes.FLOATS_SKETCH.create(k);
  }

  @Override
  protected DistributionSketchImpl<Float, KllFloatsSketch> createNew() {
    return new FloatDistributionSketch(getK());
  }
}
