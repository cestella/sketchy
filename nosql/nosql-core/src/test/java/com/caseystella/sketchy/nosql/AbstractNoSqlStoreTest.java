package com.caseystella.sketchy.nosql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.caseystella.sketchy.nosql.exception.UnableToGetException;
import com.caseystella.sketchy.utilities.SerDeUtils;
import com.google.common.collect.Iterables;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public abstract class AbstractNoSqlStoreTest<T extends NoSqlStore> {
  public abstract void clearStore(T store) throws Exception;

  public abstract T getStore();

  @Before
  public void before() throws Exception {
    clearStore(getStore());
  }

  private Iterable<Map.Entry<Key, Map.Entry<Value, Value>>> generateData() {
    List<Entry<Key, Entry<Value, Value>>> ret = new ArrayList<>();
    for (int i = 0; i < 100; ++i) {
      Key k = new Key.Builder().withColumnName("col1").withStreamId("stream_0")
          .withDataType((short) 0).withTimestampBin(i * 100).build();
      Value v = new Value.Builder().withComputeTimestamp(i * 100).withData(SerDeUtils.toBytes(i))
          .withHostId(String.format("host_%d", i % 2)).build();
      Value v2 =
          new Value.Builder().withComputeTimestamp(i * 100).withData(SerDeUtils.toBytes(i * 2))
              .withHostId(String.format("host_%d", (i % 2) + 1)).build();
      ret.add(new SimpleImmutableEntry<>(k, new SimpleImmutableEntry<>(v, v2)));
    }
    return ret;
  }

  private void validateStore() throws UnableToGetException {
    for (int i = 0; i < 100; ++i) {
      Key k = new Key.Builder().withColumnName("col1").withStreamId("stream_0")
          .withDataType((short) 0).withTimestampBin(i * 100).build();
      Iterable<Value> values = getStore().get(k);
      assertEquals(2, Iterables.size(values));
      for (Value v : values) {
        String firstHost = String.format("host_%d", i % 2);
        String secondHost = String.format("host_%d", (i % 2) + 1);
        if (firstHost.equals(v.getHostId())) {
          assertEquals(i, SerDeUtils.fromBytes(v.getData(), Integer.class));
        } else if (secondHost.equals(v.getHostId())) {
          assertEquals(i * 2, SerDeUtils.fromBytes(v.getData(), Integer.class));
        } else {
          fail(String.format("Found a host I didn't expect: %s", v.getHostId()));
        }
      }
    }
  }

  @Test
  public void testPutAndGet() throws Exception {
    for (Map.Entry<Key, Map.Entry<Value, Value>> entry : generateData()) {
      getStore().put(entry.getKey(), entry.getValue().getKey());
      getStore().put(entry.getKey(), entry.getValue().getValue());
    }
    validateStore();
  }

  @Test
  public void testPutAndGetBatch() throws Exception {
    Batch b = new Batch();
    for (Map.Entry<Key, Map.Entry<Value, Value>> entry : generateData()) {
      b.add(entry.getKey(), entry.getValue().getKey());
      b.add(entry.getKey(), entry.getValue().getValue());
    }
    getStore().put(b);
    validateStore();
  }
}
