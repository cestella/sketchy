package com.caseystella.sketchy.sketches.statistics.distribution.types.sketch;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public abstract class AbstractDistributionSketchType<S> implements SketchType<S> {
  protected int k;
  public AbstractDistributionSketchType(int k) {
    this.k = k;
  }

  protected abstract byte[] toByteArray(S v);
  protected abstract S heapify(byte[] heap);

  @Override
  public void serialize(S v, Output output) {
    byte[] bytes = toByteArray(v);
    output.writeInt(bytes.length);
    output.writeBytes(bytes);
  }

  @Override
  public S materialize(Input input) {
    int len = input.readInt();
    byte[] bytes = input.readBytes(len);
    return heapify(bytes);
  }
}
