package com.caseystella.sketchy.nosql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.caseystella.sketchy.utilities.SerDeUtils;
import org.junit.jupiter.api.Test;

public class KeyTest {
  @Test
  public void testSerDe() {
    Key k = new Key.Builder().withDataType((short) 0).withStreamId("s").withColumnName("c")
        .withTimestampBin(0L).build();
    Key clone = SerDeUtils.fromBytes(SerDeUtils.toBytes(k), Key.class);
    assertEquals(k, clone);
  }

  @Test
  public void testKeyCreationMissingColumnName() {
    assertThrows(IllegalStateException.class, () -> new Key.Builder().withDataType((short) 0)
        .withStreamId("s").withTimestampBin(0L).build());
  }

  @Test
  public void testKeyCreationMissingDataType() {
    assertThrows(IllegalStateException.class,
        () -> new Key.Builder().withColumnName("c").withStreamId("s").withTimestampBin(0L).build());
  }

  @Test
  public void testKeyCreationMissingStreamId() {
    assertThrows(IllegalStateException.class, () -> new Key.Builder().withDataType((short) 0)
        .withColumnName("c").withTimestampBin(0L).build());
  }

  @Test
  public void testKeyCreationMissingTimestamp() {
    assertThrows(IllegalStateException.class, () -> new Key.Builder().withDataType((short) 0)
        .withStreamId("s").withColumnName("c").build());
  }
}
