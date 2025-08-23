package com.lmoustak.cardcostapi.controllers;

import com.lmoustak.cardcostapi.controllers.requests.ClearingCostsRequest;
import com.lmoustak.cardcostapi.entities.ClearingCosts;
import com.lmoustak.cardcostapi.services.ClearingCostsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clearing-costs")
@Tag(name = "clearing-costs", description = "Performs CRUD operations on the clearing cost matrix")
public class ClearingCostsController {

  private final ClearingCostsService clearingCostsService;

  public ClearingCostsController(ClearingCostsService clearingCostsService) {
    this.clearingCostsService = clearingCostsService;
  }

  @PostMapping
  public ClearingCosts createClearingCosts(@RequestBody ClearingCostsRequest request) {
    return clearingCostsService.createClearingCosts(request.getCountry(), request.getPrice());
  }

  @GetMapping
}
