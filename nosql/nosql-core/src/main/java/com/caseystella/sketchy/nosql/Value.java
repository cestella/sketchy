package com.caseystella.sketchy.nosql;

import java.util.Arrays;
import java.util.Objects;

public class Value {
  public static class Builder {
    private Value value = new Value();

    public Builder withHostId(String s) {
      value.hostId = s;
      return this;
    }

    public Builder withComputeTimestamp(long t) {
      value.computeTimestamp = t;
      return this;
    }

    public Builder withData(byte[] d) {
      value.data = d;
      return this;
    }

    public Value build() {
      return value;
    }
  }

  private String hostId;
  private long computeTimestamp;
  private byte[] data;

  public String getHostId() {
    return hostId;
  }

  public long getComputeTimestamp() {
    return computeTimestamp;
  }

  public byte[] getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Value value = (Value) o;
    return computeTimestamp == value.computeTimestamp && Objects.equals(hostId, value.hostId)
        && Arrays.equals(data, value.data);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(hostId, computeTimestamp);
    result = 31 * result + Arrays.hashCode(data);
    return result;
  }

  @Override
  public String toString() {
    return "Value{" + "hostId='" + hostId + '\'' + ", computeTimestamp=" + computeTimestamp
        + ", data=" + Arrays.toString(data) + '}';
  }
}
