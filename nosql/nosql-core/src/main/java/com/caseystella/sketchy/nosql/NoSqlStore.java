package com.caseystella.sketchy.nosql;

import com.caseystella.sketchy.nosql.exception.UnableToGetException;
import com.caseystella.sketchy.nosql.exception.UnableToPutException;

public interface NoSqlStore {
  void put(Key key, Value value) throws UnableToPutException;
  void put(Batch batch) throws UnableToPutException;
  Iterable<Value> get(Key key) throws UnableToGetException;
  Batch get(Iterable<Key> keys) throws UnableToGetException;
}
