package com.caseystella.sketchy.sketches;

import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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
}
