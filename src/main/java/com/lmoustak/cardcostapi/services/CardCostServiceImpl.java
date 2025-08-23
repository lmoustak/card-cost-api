package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.dtos.CardCostDto;
import com.lmoustak.cardcostapi.entities.ClearingCosts;
import jakarta.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CardCostServiceImpl implements CardCostService {

  private final ClearingCostsService clearingCostsService;
  private final BinTableService binTableService;

  public CardCostServiceImpl(ClearingCostsService clearingCostsService,
      BinTableService binTableService) {
    this.clearingCostsService = clearingCostsService;
    this.binTableService = binTableService;
  }

  @Override
  public CardCostDto findCardCostFromIssuerIdentificationNumber(String issuerIdentificationNumber) {
    Objects.requireNonNull(issuerIdentificationNumber, "IIN should not be null");

    if (issuerIdentificationNumber.length() < 6) {
      throw new IllegalArgumentException("The IIN should be at least 6 digits long");
    }

    String country = binTableService.getCountryFromIssuerIdentificationNumber(
        issuerIdentificationNumber);
    Optional<ClearingCosts> optionalClearingCosts = clearingCostsService.readClearingCostsByCountry(
        country);

    if (optionalClearingCosts.isEmpty()) {
      throw new EntityNotFoundException("No clearing costs found for country=" + country);
    }

    return new CardCostDto(country, optionalClearingCosts.get().getPrice());
  }
}
