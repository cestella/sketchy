package com.caseystella.sketchy.nosql.exception;

public class StoreInitializationException extends Exception {
  public StoreInitializationException() {}

  public StoreInitializationException(String message) {
    super(message);
  }

  public StoreInitializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public StoreInitializationException(Throwable cause) {
    super(cause);
  }

  public StoreInitializationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
