package com.caseystella.sketchy.sketches.statistics.distribution.types.sketch;

import java.util.function.Function;

public enum SketchTypes {
  DOUBLES_SKETCH(k -> new DoublesSketchType(k)),
  FLOATS_SKETCH(k -> new FloatsSketchType(k))
  ;
  Function<Integer, SketchType<?>> creator;
  SketchTypes(Function<Integer, SketchType<?>> creator) {
    this.creator = creator;
  }

  public <S> SketchType<S> create(int k) {
    return (SketchType<S>) creator.apply(k);
  }
}
