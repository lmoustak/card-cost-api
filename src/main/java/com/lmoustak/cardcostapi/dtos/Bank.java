package com.lmoustak.cardcostapi.dtos;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Bank implements Serializable {

  private String name;
  private String website;
  private String phone;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Bank bank = (Bank) o;
    return Objects.equals(name, bank.name) && Objects.equals(website,
        bank.website) && Objects.equals(phone, bank.phone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, website, phone);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Bank.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("website='" + website + "'")
        .add("phone='" + phone + "'")
        .toString();
  }
}
