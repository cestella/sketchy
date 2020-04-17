package com.caseystella.sketchy.sketches.statistics.distribution.sketchimpl;

import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchType;
import com.caseystella.sketchy.sketches.statistics.distribution.types.sketch.SketchTypes;
import com.caseystella.sketchy.sketches.statistics.distribution.types.number.NumberTypes;
import org.apache.datasketches.kll.KllFloatsSketch;

public class LongDistributionSketch extends DistributionSketchImpl<Long, KllFloatsSketch> {
    public LongDistributionSketch() {
       super();
    }
    public LongDistributionSketch(int k) {
        super(k);
    }

    @Override
    protected NumberType<Long> createNumberType() {
        return NumberTypes.LONG.get();
    }

    @Override
    protected SketchType<KllFloatsSketch> createSketchType(int k) {
        return SketchTypes.FLOATS_SKETCH.create(k);
    }

    @Override
    protected DistributionSketchImpl<Long, KllFloatsSketch> createNew() {
        return new LongDistributionSketch(getK());
    }
}

