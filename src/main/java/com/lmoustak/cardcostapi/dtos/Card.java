package com.lmoustak.cardcostapi.dtos;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Card implements Serializable {

  private String scheme;
  private String type;
  private String category;
  private Integer length;
  private Integer checkLuhn;
  private Integer cvvLength;

  public String getScheme() {
    return scheme;
  }

  public void setScheme(String scheme) {
    this.scheme = scheme;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Integer getCheckLuhn() {
    return checkLuhn;
  }

  public void setCheckLuhn(Integer checkLuhn) {
    this.checkLuhn = checkLuhn;
  }

  public Integer getCvvLength() {
    return cvvLength;
  }

  public void setCvvLength(Integer cvvLength) {
    this.cvvLength = cvvLength;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Card card = (Card) o;
    return Objects.equals(scheme, card.scheme) && Objects.equals(type, card.type)
        && Objects.equals(category, card.category) && Objects.equals(length,
        card.length) && Objects.equals(checkLuhn, card.checkLuhn)
        && Objects.equals(cvvLength, card.cvvLength);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scheme, type, category, length, checkLuhn, cvvLength);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Card.class.getSimpleName() + "[", "]")
        .add("scheme='" + scheme + "'")
        .add("type='" + type + "'")
        .add("category='" + category + "'")
        .add("length=" + length)
        .add("checkLuhn=" + checkLuhn)
        .add("cvvLength=" + cvvLength)
        .toString();
  }
}
