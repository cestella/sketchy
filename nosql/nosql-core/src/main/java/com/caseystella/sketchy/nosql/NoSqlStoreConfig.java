package com.caseystella.sketchy.nosql;

import com.caseystella.sketchy.nosql.exception.StoreInitializationException;
import java.util.Map;

public class NoSqlStoreConfig {
  String storeClass;
  Map<String, Object> config;

  public String getStoreClass() {
    return storeClass;
  }

  public void setStoreClass(String storeClass) {
    this.storeClass = storeClass;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

  public static NoSqlStore create(NoSqlStore config) throws StoreInitializationException {
  }
}
