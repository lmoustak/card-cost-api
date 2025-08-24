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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/payment-cards-cost",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE}
)
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
  @ApiResponse(
      responseCode = "400",
      description = "card_number was invalid",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = {
              @ExampleObject("""
                  {
                      "title": "Validation error",
                      "status": 400,
                      "description": "One or more validation errors occurred. Please check the `errors` field.",
                      "instance": "http://localhost:8080/payment-cards-cost",
                      "errors": {
                          "card_number": "`card_number` should not be null"
                      }
                  }
                  """),
              @ExampleObject("""
                  {
                      "title": "Validation error",
                      "status": 400,
                      "description": "One or more validation errors occurred. Please check the `errors` field.",
                      "instance": "http://localhost:8080/payment-cards-cost",
                      "errors": {
                          "card_number": "`card_number` should contain 8-19 numeric digits"
                      }
                  }
                  """)
          }
      )
  )
  @ApiResponse(
      responseCode = "401",
      description = "BINTable balance exhausted",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "An error occurred while calling the BINTable API",
                  "status": 401,
                  "description": "Your balance is exhausted,or package expired",
                  "instance": "http://localhost:8080/payment-cards-cost"
              }
              """)
      )
  )
  @ApiResponse(
      responseCode = "403",
      description = "Invalid BINTable API key",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "An error occurred while calling the BINTable API",
                  "status": 401,
                  "description": "Invalid API Key",
                  "instance": "http://localhost:8080/payment-cards-cost"
              }
              """)
      )
  )
  @ApiResponse(
      responseCode = "404",
      description = "Clearing costs not found for country",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "Resource not found",
                  "status": 404,
                  "description": "No clearing costs found for country={country}",
                  "instance": "http://localhost:8080/payment-cards-cost"
              }
              """)
      )
  )
  @ApiResponse(
      responseCode = "422",
      description = "BINTable API key is missing",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "An error occurred while calling the BINTable API",
                  "status": 422,
                  "description": "API key is missing",
                  "instance": "http://localhost:8080/payment-cards-cost"
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
