package com.lmoustak.cardcostapi.controllers.requests;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

@Schema(description = "A request to find the country and clearing cost for a card")
public class CardCostRequest implements Serializable {

  @Parameter(
      description = """
          The card number, or Primary Account Number (PAN),
          Must be between 8 to 19 numeric digits long (inclusive).
          """,
      required = true
  )
  @NotNull(message = "`card_number` should not be null")
  @Pattern(regexp = "[0-9]{8,19}", message = "`card_number` should contain 8-19 numeric digits")
  private String card_number;

  public String getCard_number() {
    return card_number;
  }

  public void setCard_number(String card_number) {
    this.card_number = card_number;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardCostRequest that = (CardCostRequest) o;
    return Objects.equals(card_number, that.card_number);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(card_number);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CardCostRequest.class.getSimpleName() + "[", "]")
        .add("card_number='" + card_number + "'")
        .toString();
  }
}
