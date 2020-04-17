package com.caseystella.sketchy.nosql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Batch {
  Map<Key, List<Value>> underlyingBatch;
  public Batch() {
    underlyingBatch = new HashMap<>();
  }
  public Batch(int size) {
    underlyingBatch = new HashMap<>(size);
  }
  public void add(Key key, Value value) {
    List<Value> values = underlyingBatch.computeIfAbsent(key, k -> new ArrayList<>());
    values.add(value);
  }
  public Map<Key, List<Value>> getUnderlyingBatch() {
    return underlyingBatch;
  }
}
