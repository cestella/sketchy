package com.caseystella.sketchy.nosql;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public class Value {
  public static class Builder {
    private String hostId;
    private Long computeTimestamp;
    private byte[] data;

    public Builder withHostId(String s) {
      hostId = s;
      return this;
    }

    public Builder withComputeTimestamp(long t) {
      computeTimestamp = t;
      return this;
    }

    public Builder withData(byte[] d) {
      data = d;
      return this;
    }

    public Value build() {
      return new Value(hostId, computeTimestamp, data);
    }
  }

  private String hostId;
  private Long computeTimestamp;
  private byte[] data;

  public Value(String hostId, Long computeTimestamp, byte[] data) {

    this.hostId = hostId;
    this.computeTimestamp = computeTimestamp;
    this.data = data;
    if (hostId == null || computeTimestamp == null || data == null) {
      throw new IllegalStateException(MessageFormat.format(
          "You must provide all of the parameters for Values: hostId={0}, computeTimestamp={1}, data={2}",
          hostId, computeTimestamp, data));
    }
  }

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
