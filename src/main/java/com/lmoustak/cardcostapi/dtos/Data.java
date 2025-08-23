package com.lmoustak.cardcostapi.dtos;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class Data implements Serializable {

  private Card card;
  private Country country;
  private Bank bank;

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public Bank getBank() {
    return bank;
  }

  public void setBank(Bank bank) {
    this.bank = bank;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Data data = (Data) o;
    return Objects.equals(card, data.card) && Objects.equals(country,
        data.country) && Objects.equals(bank, data.bank);
  }

  @Override
  public int hashCode() {
    return Objects.hash(card, country, bank);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Data.class.getSimpleName() + "[", "]")
        .add("card=" + card)
        .add("country=" + country)
        .add("bank=" + bank)
        .toString();
  }

}
