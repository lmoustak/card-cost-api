package com.lmoustak.cardcostapi;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lmoustak.cardcostapi.entities.ClearingCosts;
import com.lmoustak.cardcostapi.services.ClearingCostsService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureCache(cacheProvider = CacheType.SIMPLE)
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClearingCostsServiceTests {

  private static List<ClearingCosts> sampleClearingCosts;

  @Autowired
  private ClearingCostsService clearingCostsService;

  @BeforeAll
  void beforeAllTests() {
    sampleClearingCosts = new ArrayList<>();
    var clearingCosts = new ClearingCosts();
    clearingCosts.setId(1L);
    clearingCosts.setCountry(null);
    clearingCosts.setPrice(new BigDecimal("10"));
    sampleClearingCosts.add(clearingCosts);

    clearingCosts = new ClearingCosts();
    clearingCosts.setId(2L);
    clearingCosts.setCountry("gr");
    clearingCosts.setPrice(new BigDecimal("3"));
    sampleClearingCosts.add(clearingCosts);

    clearingCosts = new ClearingCosts();
    clearingCosts.setId(3L);
    clearingCosts.setCountry("uk");
    clearingCosts.setPrice(new BigDecimal("4.5"));
    sampleClearingCosts.add(clearingCosts);

    sampleClearingCosts.forEach(
        entity -> clearingCostsService.createClearingCosts(entity.getCountry(), entity.getPrice()));
  }

  private ClearingCosts getByCountry(String country) {
    return sampleClearingCosts.stream()
        .filter(entity -> country.equals(entity.getCountry()))
        .findFirst()
        .orElse(sampleClearingCosts.get(0));
  }

  @Test
  void querying_existing_clearing_costs_by_country_should_return_it() {
    final String country = "gr";

    Optional<ClearingCosts> optionalActualEntity = clearingCostsService.readClearingCostsByCountry(
        country);

    ClearingCosts expectedEntity = getByCountry(country);

    assertNotEquals(Optional.empty(), optionalActualEntity);

    ClearingCosts actualEntity = optionalActualEntity.get();
    assertEquals(expectedEntity.getCountry(), actualEntity.getCountry());
    assertEquals(0, expectedEntity.getPrice().compareTo(actualEntity.getPrice()));
  }

  @Test
  void querying_clearing_costs_by_unknown_country_should_return_the_entry_with_null_country() {
    final String country = "mx";

    Optional<ClearingCosts> optionalActualEntity = clearingCostsService.readClearingCostsByCountry(
        country);

    ClearingCosts expectedEntity = getByCountry(country);

    assertNotEquals(Optional.empty(), optionalActualEntity);

    ClearingCosts actualEntity = optionalActualEntity.get();
    assertNull(actualEntity.getCountry());
    assertEquals(0, expectedEntity.getPrice().compareTo(actualEntity.getPrice()));
  }

  @Test
  void deleting_non_existing_id_should_throw() {
    assertThrows(EntityNotFoundException.class,
        () -> clearingCostsService.deleteClearingCosts(300L));
  }

  @Test
  void deleting_non_existing_country_should_throw() {
    assertThrows(EntityNotFoundException.class,
        () -> clearingCostsService.deleteClearingCostsByCountry("de"));
  }

}
