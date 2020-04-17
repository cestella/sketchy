package com.caseystella.sketchy.nosql.exception;

public class UnableToGetException extends Exception {
  public UnableToGetException() {
  }

  public UnableToGetException(String message) {
    super(message);
  }

  public UnableToGetException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnableToGetException(Throwable cause) {
    super(cause);
  }

  public UnableToGetException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
