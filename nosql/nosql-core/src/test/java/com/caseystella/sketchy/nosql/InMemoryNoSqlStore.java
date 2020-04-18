package com.caseystella.sketchy.nosql;

import com.caseystella.sketchy.nosql.exception.StoreInitializationException;
import com.caseystella.sketchy.nosql.exception.UnableToGetException;
import com.caseystella.sketchy.nosql.exception.UnableToPutException;
import com.caseystella.sketchy.utilities.SerDeUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InMemoryNoSqlStore implements NoSqlStore {
  public static Comparator<byte[]> LEXICOGRAPHIC_COMPARATOR = (left, right) -> {
    for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
      int a = (left[i] & 0xff);
      int b = (right[j] & 0xff);
      if (a != b) {
        return a - b;
      }
    }
    return left.length - right.length;
  };

  private class Column implements KryoSerializable {
    private String hostId;
    private long computeTimestamp;

    public Column() {}

    public Column(Value v) {
      hostId = v.getHostId();
      computeTimestamp = v.getComputeTimestamp();
    }

    @Override
    public void write(Kryo kryo, Output output) {
      output.writeString(hostId);
      output.writeLong(computeTimestamp);
    }

    @Override
    public void read(Kryo kryo, Input input) {
      hostId = input.readString();
      computeTimestamp = input.readLong();
    }
  }

  Map<byte[], Map<byte[], byte[]>> _map = new TreeMap<>(LEXICOGRAPHIC_COMPARATOR);

  @Override
  public void put(Key key, Value value) throws UnableToPutException {
    byte[] keyBytes = SerDeUtils.toBytes(key);
    byte[] column = SerDeUtils.toBytes(new Column(value));
    byte[] data = value.getData();
    Map<byte[], byte[]> columns =
        _map.computeIfAbsent(keyBytes, b -> new TreeMap<>(LEXICOGRAPHIC_COMPARATOR));
    columns.put(column, data);
  }

  @Override
  public void put(Batch batch) throws UnableToPutException {
    for (Map.Entry<Key, List<Value>> entry : batch.getUnderlyingBatch().entrySet()) {
      for (Value v : entry.getValue()) {
        put(entry.getKey(), v);
      }
    }
  }

  @Override
  public Iterable<Value> get(Key key) throws UnableToGetException {
    byte[] keyBytes = SerDeUtils.toBytes(key);
    Map<byte[], byte[]> columnMap = _map.get(keyBytes);
    List<Value> ret = new ArrayList<>();
    if (columnMap != null) {
      for (Map.Entry<byte[], byte[]> entry : columnMap.entrySet()) {
        Column c = SerDeUtils.fromBytes(entry.getKey(), Column.class);
        Value v = new Value.Builder().withHostId(c.hostId).withComputeTimestamp(c.computeTimestamp)
            .withData(entry.getValue()).build();
        ret.add(v);
      }
    }
    return ret;
  }

  @Override
  public Batch get(Iterable<Key> keys) throws UnableToGetException {
    Batch ret = new Batch();
    Set<Key> s = new HashSet<>();
    Iterables.addAll(s, keys);
    for (Key k : s) {
      Iterable<Value> vals = get(k);
      List<Value> allVals = ret.getUnderlyingBatch().computeIfAbsent(k, x -> new ArrayList<>());
      Iterables.addAll(allVals, vals);
    }
    return ret;
  }

  public void clear() {
    _map.clear();
  }

  @Override
  public void configure(NoSqlStoreConfig config) throws StoreInitializationException {

  }



}
