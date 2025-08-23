package com.lmoustak.cardcostapi.controllers;

import com.lmoustak.cardcostapi.controllers.requests.CardCostRequest;
import com.lmoustak.cardcostapi.dtos.CardCostDto;
import com.lmoustak.cardcostapi.services.CardCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-cards-cost")
@Tag(name = "card-cost", description = "Calculates the clearing cost of a card")
public class CardCostController {

  private final CardCostService cardCostService;

  public CardCostController(CardCostService cardCostService) {
    this.cardCostService = cardCostService;
  }

  @Operation(summary = "Get the clearing cost of a card")
  @ApiResponse(
      responseCode = "200",
      description = "Found clearing cost",
      content = @Content(
          schema = @Schema(implementation = CardCostDto.class),
          examples = @ExampleObject("""
              {
                  "country": "gr",
                  "cost": 4.5
              }
              """)
      )
  )
  @PostMapping
  public CardCostDto getCardCost(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "A request containing the card number, or Primary Account Number (PAN)",
          required = true
      )
      @RequestBody
      @Validated
      CardCostRequest request
  ) {
    return cardCostService.findCardCostFromIssuerIdentificationNumber(request.getCard_number());
  }
}
