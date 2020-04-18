package com.caseystella.sketchy.nosql;

import org.junit.jupiter.api.BeforeAll;

public class InMemoryNoSqlStoreTest extends AbstractNoSqlStoreTest<InMemoryNoSqlStore> {
  static InMemoryNoSqlStore store = null;

  @Override
  public void clearStore(InMemoryNoSqlStore store) throws Exception {
    store.clear();
  }

  @Override
  public InMemoryNoSqlStore getStore() {
    return store;
  }

  @BeforeAll
  public static void beforeAll() throws Exception {
    store = new InMemoryNoSqlStore();
  }
}
