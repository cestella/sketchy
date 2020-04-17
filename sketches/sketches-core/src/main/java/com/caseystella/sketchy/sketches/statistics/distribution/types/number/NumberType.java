package com.caseystella.sketchy.sketches.statistics.distribution.types.number;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface NumberType<T> {
  T zero();

  T minValue();

  T maxValue();

  T min(T v1, T v2);

  T max(T v1, T v2);

  T add(T v1, T v2);

  T multiply(T v1, T v2);

  void serialize(T v, Output output);

  T materialize(Input input);

  void serialize(T v, ObjectOutputStream output) throws IOException;

  T materialize(ObjectInputStream input) throws IOException;
}
