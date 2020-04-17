package com.caseystella.sketchy.sketches.statistics.distribution.types.number;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FloatType implements NumberType<Float> {

  @Override
  public Float zero() {
    return 0f;
  }

  @Override
  public Float minValue() {
    return -1 * Float.MAX_VALUE;
  }

  @Override
  public Float maxValue() {
    return Float.MAX_VALUE;
  }

  @Override
  public Float min(Float v1, Float v2) {
    return Float.min(v1, v2);
  }

  @Override
  public Float max(Float v1, Float v2) {
    return Float.max(v1, v2);
  }

  @Override
  public Float add(Float v1, Float v2) {
    return v1 + v2;
  }

  @Override
  public Float multiply(Float v1, Float v2) {
    return v1 * v2;
  }

  @Override
  public void serialize(Float v, Output output) {
    output.writeFloat(v);
  }

  @Override
  public Float materialize(Input input) {
    return input.readFloat();
  }

  @Override
  public void serialize(Float v, ObjectOutputStream output) throws IOException {
    output.writeFloat(v);
  }

  @Override
  public Float materialize(ObjectInputStream input) throws IOException {
    return input.readFloat();
  }
}
