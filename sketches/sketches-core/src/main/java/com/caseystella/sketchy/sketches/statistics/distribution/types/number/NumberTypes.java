package com.caseystella.sketchy.sketches.statistics.distribution.types.number;

public enum NumberTypes {
  FLOAT(new FloatType()),
  DOUBLE(new DoubleType()) ,
  LONG(new LongType())
  ;

  private NumberType<? extends Number> numberType;
  NumberTypes(NumberType<? extends Number> nt) {
    this.numberType = nt;
  }

  public <T> NumberType<T> get() {
    return (NumberType<T>)numberType;
  }
}
