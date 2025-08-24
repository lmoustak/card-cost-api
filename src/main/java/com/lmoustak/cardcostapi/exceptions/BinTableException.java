package com.lmoustak.cardcostapi.exceptions;

import org.springframework.http.HttpStatusCode;

public class BinTableException extends RuntimeException {

  private final HttpStatusCode code;

  public BinTableException(HttpStatusCode code, String message) {
    super(message);
    this.code = code;
  }

  public HttpStatusCode getCode() {
    return code;
  }
}
