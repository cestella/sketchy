package com.caseystella.sketchy.nosql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.caseystella.sketchy.utilities.SerDeUtils;
import org.junit.jupiter.api.Test;

public class ValueTest {
  @Test
  public void testSerDe() {
    Value v = new Value.Builder().withComputeTimestamp(10L)
        .withData(new byte[] { 0,1,2})
        .withHostId("host")
        .build();
    Value clone = SerDeUtils.fromBytes(SerDeUtils.toBytes(v), Value.class);
    assertEquals(v, clone);
  }

  @Test
  public void testMissingComputeTimestamp() {
    assertThrows(IllegalStateException.class,
        () -> new Value.Builder()
            .withData(new byte[] { 0,1,2})
            .withHostId("host")
            .build());
  }

  @Test
  public void testMissingData() {
    assertThrows(IllegalStateException.class,
        () -> new Value.Builder()
            .withComputeTimestamp(10L)
            .withHostId("host")
            .build());
  }
  @Test
  public void testMissingHostId() {
    assertThrows(IllegalStateException.class,
        () -> new Value.Builder()
            .withComputeTimestamp(10L)
            .withData(new byte[] { 0,1,2})
            .build());
  }
}
