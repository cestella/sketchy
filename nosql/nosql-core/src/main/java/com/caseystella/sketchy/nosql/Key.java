package com.caseystella.sketchy.nosql;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.Objects;

public class Key implements KryoSerializable {


  public static class Builder {
    Key key = new Key();

    public Builder withTimestampBin(long t) {
      key.timestampBin = t;
      return this;
    }

    public Builder withStreamId(String v) {
      key.streamId = v;
      return this;
    }

    public Builder withColumnName(String v) {
      key.columnName = v;
      return this;
    }

    public Builder withDataType(short v) {
      key.dataType = v;
      return this;
    }

    public Key build() {
      return key;
    }
  }

  // TODO: Ensure we specify ALL of these parameters.
  private long timestampBin;
  private String streamId;
  private String columnName;
  private short dataType;

  public long getTimestampBin() {
    return timestampBin;
  }

  public String getStreamId() {
    return streamId;
  }

  public String getColumnName() {
    return columnName;
  }

  public short getDataType() {
    return dataType;
  }

  @Override
  public void write(Kryo kryo, Output output) {
    output.writeLong(timestampBin);
    output.writeString(streamId);
    output.writeString(columnName);
    output.writeShort(dataType);
  }

  @Override
  public void read(Kryo kryo, Input input) {
    timestampBin = input.readLong();
    streamId = input.readString();
    columnName = input.readString();
    dataType = input.readShort();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Key key = (Key) o;
    return timestampBin == key.timestampBin && dataType == key.dataType
        && Objects.equals(streamId, key.streamId) && Objects.equals(columnName, key.columnName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestampBin, streamId, columnName, dataType);
  }

  @Override
  public String toString() {
    return "Key{" + "timestampBin=" + timestampBin + ", streamId='" + streamId + '\''
        + ", columnName='" + columnName + '\'' + ", dataType=" + dataType + '}';
  }
}
