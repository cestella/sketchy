package com.caseystella.sketchy.nosql;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.text.MessageFormat;
import java.util.Objects;

public class Key implements KryoSerializable {
  public static class Builder {
    private Long timestampBin;
    private String streamId;
    private String columnName;
    private Short dataType;

    public Builder withTimestampBin(long t) {
      timestampBin = t;
      return this;
    }

    public Builder withStreamId(String v) {
      streamId = v;
      return this;
    }

    public Builder withColumnName(String v) {
      columnName = v;
      return this;
    }

    public Builder withDataType(short v) {
      dataType = v;
      return this;
    }

    public Key build() {
      return new Key(timestampBin, streamId, columnName, dataType);
    }
  }

  private Long timestampBin;
  private String streamId;
  private String columnName;
  private Short dataType;

  public Key(Long timestampBin, String streamId, String columnName, Short dataType) {
    this.timestampBin = timestampBin;
    this.streamId = streamId;
    this.columnName = columnName;
    this.dataType = dataType;
    if (timestampBin == null || streamId == null || columnName == null || dataType == null) {
      throw new IllegalStateException(
          MessageFormat.format(
              "You must provide all of the parameters for a Key: timestampBin={0}, streamId={1}"
                  + ", columnName={2}, dataType={3}",
              timestampBin, streamId, columnName, dataType));
    }
  }

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
