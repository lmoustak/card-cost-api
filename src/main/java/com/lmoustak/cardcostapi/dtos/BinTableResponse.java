package com.lmoustak.cardcostapi.dtos;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class BinTableResponse implements Serializable {

  private Integer result;
  private String message;
  private Data data;

  public Integer getResult() {
    return result;
  }

  public void setResult(Integer result) {
    this.result = result;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BinTableResponse that = (BinTableResponse) o;
    return Objects.equals(result, that.result) && Objects.equals(message,
        that.message) && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result, message, data);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", BinTableResponse.class.getSimpleName() + "[", "]")
        .add("result=" + result)
        .add("message='" + message + "'")
        .add("data=" + data)
        .toString();
  }

}
