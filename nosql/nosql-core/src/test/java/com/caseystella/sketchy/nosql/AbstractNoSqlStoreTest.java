package com.caseystella.sketchy.nosql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.caseystella.sketchy.utilities.SerDeUtils;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public abstract class AbstractNoSqlStoreTest<T extends NoSqlStore> {
  public abstract void clearStore(T store) throws Exception;

  public abstract T getStore();



  @Before
  public void before() throws Exception {
    clearStore(getStore());
  }

  @Test
  public void testPutAndGet() throws Exception {
    for (int i = 0; i < 100; ++i) {
      Key k = new Key.Builder().withColumnName("col1").withStreamId("stream_0")
          .withDataType((short) 0).withTimestampBin(i * 100).build();
      Value v = new Value.Builder().withComputeTimestamp(i * 100).withData(SerDeUtils.toBytes(i))
          .withHostId(String.format("host_%d", i % 2)).build();
      Value v2 =
          new Value.Builder().withComputeTimestamp(i * 100).withData(SerDeUtils.toBytes(i * 2))
              .withHostId(String.format("host_%d", (i % 2) + 1)).build();
      getStore().put(k, v);
      getStore().put(k, v2);

    }
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
}
