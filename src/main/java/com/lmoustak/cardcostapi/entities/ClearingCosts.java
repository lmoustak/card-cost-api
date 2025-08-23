package com.lmoustak.cardcostapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;
import org.hibernate.annotations.Check;

@Entity
@Table(
    name = "CLEARING_COSTS",
    indexes = @Index(name = "COUNTRY_IDX", columnList = "COUNTRY", unique = true)
)
public class ClearingCosts implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "COUNTRY", length = 2, updatable = false)
  private String country;

  @Column(name = "PRICE", nullable = false)
  @Check(name = "price_should_be_non_negative", constraints = "price >= 0")
  private BigDecimal price;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClearingCosts that = (ClearingCosts) o;
    return Objects.equals(id, that.id) && Objects.equals(country, that.country)
        && Objects.equals(price, that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, country, price);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ClearingCosts.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("country='" + country + "'")
        .add("price=" + price)
        .toString();
  }
}
