package com.lmoustak.cardcostapi.controllers;

import com.lmoustak.cardcostapi.controllers.requests.ClearingCostsRequest;
import com.lmoustak.cardcostapi.entities.ClearingCosts;
import com.lmoustak.cardcostapi.services.ClearingCostsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(
    value = "/clearing-costs",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE}
)
@Tag(name = "clearing-costs", description = "Performs CRUD operations on the clearing cost matrix")
public class ClearingCostsController {

  private static final Logger logger = LoggerFactory.getLogger(ClearingCostsController.class);

  private final ClearingCostsService clearingCostsService;

  public ClearingCostsController(ClearingCostsService clearingCostsService) {
    this.clearingCostsService = clearingCostsService;
  }

  @Operation(summary = "Create a new clearing costs entry")
  @ApiResponse(
      responseCode = "201",
      description = "Clearing costs entry created",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ClearingCosts.class)
      )
  )
  @ApiResponse(
      responseCode = "400",
      description = "Country or price was invalid",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = {
              @ExampleObject("""
                  {
                      "title": "Validation error",
                      "status": 400,
                      "description": "One or more validation errors occurred. Please check the `errors` field.",
                      "instance": "http://localhost:8080/clearing-costs",
                      "errors": {
                          "price": "`price` should not be null"
                      }
                  }
                  """),
              @ExampleObject("""
                  {
                      "title": "Validation error",
                      "status": 400,
                      "description": "One or more validation errors occurred. Please check the `errors` field.",
                      "instance": "http://localhost:8080/clearing-costs",
                      "errors": {
                          "country": "`country` should either be null or 2 letters long",
                          "price": "`price` should not be negative"
                      }
                  }
                  """)
          }
      )
  )
  @ApiResponse(
      responseCode = "409",
      description = "Clearing costs for country already exists",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "Resource already exists",
                  "status": 409,
                  "description": "There already exists a clearing cost for country {country}",
                  "instance": "http://localhost:8080/clearing-costs"
              }
              """)
      )
  )
  @PostMapping
  public ResponseEntity<ClearingCosts> createClearingCosts(
      @RequestBody ClearingCostsRequest request) {
    logger.debug("Calling POST /clearing-costs with request body {}", request);
    ClearingCosts clearingCosts = clearingCostsService.createClearingCosts(request.getCountry(),
        request.getPrice());

    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .build(clearingCosts.getId())
        )
        .body(clearingCosts);
  }

  @Operation(summary = "Get all clearing costs entries")
  @ApiResponse(
      responseCode = "200",
      description = "Clearing costs entries retrieved",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(
              schema = @Schema(implementation = ClearingCosts.class)
          )
      )
  )
  @GetMapping
  public List<ClearingCosts> getAllClearingCosts() {
    logger.debug("Calling GET /clearing-costs");
    return clearingCostsService.readAllClearingCosts();
  }

  @Operation(summary = "Get clearing costs entry by its id")
  @ApiResponse(
      responseCode = "200",
      description = "Clearing costs entry retrieved",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ClearingCosts.class)
      )
  )
  @GetMapping("/{id}")
  public ClearingCosts getClearingCostsById(@PathVariable Long id) {
    logger.debug("Calling GET /clearing-costs/{}", id);
    return clearingCostsService.readClearingCostsById(id)
        .orElseThrow(
            () -> new EntityNotFoundException("No clearing costs for id=%d found".formatted(id)));
  }

  @Operation(summary = "Get clearing costs entry by its country")
  @ApiResponse(
      responseCode = "200",
      description = "Clearing costs entry retrieved",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ClearingCosts.class)
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
                  "description": "No clearing costs found for country={country} found",
                  "instance": "http://localhost:8080/clearing-costs?country={country}"
              }
              """)
      )
  )
  @GetMapping("/country/{country}")
  public ClearingCosts getClearingCostsByCountry(@PathVariable String country) {
    logger.debug("Calling GET /clearing-costs/country/{}", country);
    return clearingCostsService.readClearingCostsByCountry(country)
        .orElseThrow(
            () -> new EntityNotFoundException(
                "No clearing costs for country=%s found".formatted(country)));
  }

  @Operation(summary = "Get clearing costs entries by the given price range")
  @ApiResponse(
      responseCode = "200",
      description = "Clearing costs entries retrieved",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(
              schema = @Schema(implementation = ClearingCosts.class)
          )
      )
  )
  @GetMapping("/priceRange")
  public List<ClearingCosts> getClearingCostsInPriceRange(
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice
  ) {
    logger.debug("Calling GET /clearing-costs/priceRange?minPrice={}&maxPrice={}", minPrice,
        maxPrice);
    return clearingCostsService.readClearingCostsByPriceRange(minPrice, maxPrice);
  }

  @Operation(summary = "Update a new clearing costs entry")
  @ApiResponse(
      responseCode = "200",
      description = "Clearing costs entry updated",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ClearingCosts.class)
      )
  )
  @ApiResponse(
      responseCode = "400",
      description = "Country or price was invalid",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = {
              @ExampleObject("""
                  {
                      "title": "Validation error",
                      "status": 400,
                      "description": "One or more validation errors occurred. Please check the `errors` field.",
                      "instance": "http://localhost:8080/clearing-costs",
                      "errors": {
                          "price": "`price` should not be null"
                      }
                  }
                  """),
              @ExampleObject("""
                  {
                      "title": "Validation error",
                      "status": 400,
                      "description": "One or more validation errors occurred. Please check the `errors` field.",
                      "instance": "http://localhost:8080/clearing-costs",
                      "errors": {
                          "country": "`country` should either be null or 2 letters long",
                          "price": "`price` should not be negative"
                      }
                  }
                  """)
          }
      )
  )
  @PutMapping
  public ClearingCosts updateClearingCosts(@RequestBody ClearingCostsRequest request) {
    logger.debug("Calling PUT /clearing-costs with request body {}", request);
    return clearingCostsService.updateClearingCosts(request.getCountry(), request.getPrice());
  }

  @Operation(summary = "Delete clearing costs entry by its id")
  @ApiResponse(
      responseCode = "204",
      description = "Clearing costs entry deleted"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Given id was null",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "Missing path variable",
                  "status": 404,
                  "description": "A path variable (id) is missing.",
                  "instance": "http://localhost:8080/clearing-costs?country={country}"
              }
              """)
      )
  )
  @ApiResponse(
      responseCode = "404",
      description = "Clearing costs not found for id",
      content = @Content(
          schema = @Schema(implementation = ProblemDetail.class),
          examples = @ExampleObject("""
              {
                  "title": "Resource not found",
                  "status": 404,
                  "description": "No clearing costs found for id={id} found",
                  "instance": "http://localhost:8080/clearing-costs/{id}"
              }
              """)
      )
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClearingCostsById(@PathVariable Long id) {
    logger.debug("Calling DELETE /clearing-costs/{}", id);
    clearingCostsService.deleteClearingCosts(id);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete clearing costs entry by country")
  @ApiResponse(
      responseCode = "204",
      description = "Clearing costs entry deleted"
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
                  "description": "No clearing costs found for country={country} found",
                  "instance": "http://localhost:8080/clearing-costs?country={country}"
              }
              """)
      )
  )
  @DeleteMapping
  public ResponseEntity<Void> deleteClearingCostsByCountry(
      @RequestParam(required = false) String country
  ) {
    logger.debug("Calling DELETE /clearing-costs?country={}", country);
    clearingCostsService.deleteClearingCostsByCountry(country);

    return ResponseEntity.noContent().build();
  }
}
