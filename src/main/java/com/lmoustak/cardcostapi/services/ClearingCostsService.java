package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.entities.ClearingCosts;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClearingCostsService {

  ClearingCosts createClearingCosts(String country, BigDecimal price);

  List<ClearingCosts> readAllClearingCosts();

  Optional<ClearingCosts> readClearingCostsById(Long id);

  Optional<ClearingCosts> readClearingCostsByCountry(String country);

  List<ClearingCosts> readClearingCostsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

  ClearingCosts updateClearingCosts(String country, BigDecimal price);

  void deleteClearingCosts(Long id);

  void deleteClearingCostsByCountry(String country);

}
