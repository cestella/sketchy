package com.caseystella.sketchy.nosql.exception;

public class UnableToPutException extends Exception {
  public UnableToPutException() {
    super();
  }

  public UnableToPutException(String message) {
    super(message);
  }

  public UnableToPutException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnableToPutException(Throwable cause) {
    super(cause);
  }

  protected UnableToPutException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
