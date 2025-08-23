package com.lmoustak.cardcostapi.repositories;

import com.lmoustak.cardcostapi.entities.ClearingCosts;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClearingCostsRepository extends JpaRepository<ClearingCosts, Long> {

  @Query("""
      FROM ClearingCosts
      WHERE (:country IS NULL AND country IS NULL)
         OR LOWER(country) = LOWER(:country)
      """)
  Optional<ClearingCosts> findByCountry(String country);

  Optional<ClearingCosts> findByCountryIsNull();

  @Query("""
      FROM ClearingCosts
      WHERE (:minPrice IS NULL OR :minPrice <= price)
        AND (:maxPrice IS NULL OR :maxPrice >= price)
      """)
  List<ClearingCosts> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

}
