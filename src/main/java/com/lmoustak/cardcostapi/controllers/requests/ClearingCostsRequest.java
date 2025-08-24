package com.lmoustak.cardcostapi.controllers.requests;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

@Schema(description = "Request to create or update a clearing cost matrix entry")
public class ClearingCostsRequest implements Serializable {

  @Parameter(description = "The 2-letter country code. In lower case. May be null.")
  @Pattern(regexp = "[A-Za-z]{2}", message = "`country` should either be null or two letters long")
  private String country;

  @Parameter(description = "The clearing price for the country. Must not be negative.", required = true)
  @NotNull(message = "`price` should not be null")
  @DecimalMin(value = "0", message = "`price` should not be negative")
  private BigDecimal price;

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
    ClearingCostsRequest that = (ClearingCostsRequest) o;
    return Objects.equals(country, that.country) && Objects.equals(price,
        that.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(country, price);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ClearingCostsRequest.class.getSimpleName() + "[", "]")
        .add("country='" + country + "'")
        .add("price=" + price)
        .toString();
  }
}
