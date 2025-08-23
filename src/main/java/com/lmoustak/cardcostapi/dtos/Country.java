package com.lmoustak.cardcostapi.dtos;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Country implements Serializable {

  private String name;
  private String code;
  private String flag;
  private String currency;
  private String currency_code;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCurrency_code() {
    return currency_code;
  }

  public void setCurrency_code(String currency_code) {
    this.currency_code = currency_code;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Country country = (Country) o;
    return Objects.equals(name, country.name) && Objects.equals(code,
        country.code) && Objects.equals(flag, country.flag) && Objects.equals(
        currency, country.currency) && Objects.equals(currency_code, country.currency_code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, code, flag, currency, currency_code);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Country.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("code='" + code + "'")
        .add("flag='" + flag + "'")
        .add("currency='" + currency + "'")
        .add("currency_code='" + currency_code + "'")
        .toString();
  }
}
