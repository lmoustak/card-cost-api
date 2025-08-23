package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.dtos.CardCostDto;

public interface CardCostService {

  CardCostDto findCardCostFromIssuerIdentificationNumber(String issuerIdentificationNumber);

}
