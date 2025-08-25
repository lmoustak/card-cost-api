package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.controllers.CardCostController;
import com.lmoustak.cardcostapi.dtos.CardCostDto;
import com.lmoustak.cardcostapi.entities.ClearingCosts;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CardCostServiceImpl implements CardCostService {

  private static final Logger logger = LoggerFactory.getLogger(CardCostServiceImpl.class);

  private final ClearingCostsService clearingCostsService;
  private final BinTableService binTableService;

  public CardCostServiceImpl(ClearingCostsService clearingCostsService,
      BinTableService binTableService) {
    this.clearingCostsService = clearingCostsService;
    this.binTableService = binTableService;
  }

  @Override
  public CardCostDto findCardCostFromIssuerIdentificationNumber(String issuerIdentificationNumber) {
    logger.debug("START findCardCostFromIssuerIdentificationNumber('{}')",
        issuerIdentificationNumber);

    String country = binTableService.getCountryFromIssuerIdentificationNumber(
        issuerIdentificationNumber);
    logger.debug("BINTable found country: '{}'", country);

    Optional<ClearingCosts> optionalClearingCosts = clearingCostsService.readClearingCostsByCountry(
        country);

    if (optionalClearingCosts.isEmpty()) {
      logger.error("No clearing costs found for country='{}'. Throwing...", country);
      throw new EntityNotFoundException("No clearing costs found for country=" + country);
    }

    CardCostDto cardCostDto = new CardCostDto(country, optionalClearingCosts.get().getPrice());
    logger.debug("END findCardCostFromIssuerIdentificationNumber('{}') returns {}",
        issuerIdentificationNumber, cardCostDto);
    return cardCostDto;
  }
}
