package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.entities.ClearingCosts;
import com.lmoustak.cardcostapi.repositories.ClearingCostsRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ClearingCostsServiceImpl implements ClearingCostsService {

  private final ClearingCostsRepository clearingCostsRepository;

  public ClearingCostsServiceImpl(ClearingCostsRepository clearingCostsRepository) {
    this.clearingCostsRepository = clearingCostsRepository;
  }

  @Override
  @CachePut("clearingCosts")
  public ClearingCosts createClearingCosts(String country, BigDecimal price) {
    Objects.requireNonNull(price, "`price` should not be null");

    Optional<ClearingCosts> optionalClearingCosts = clearingCostsRepository.findByCountry(country);
    if (optionalClearingCosts.isPresent()) {
      throw new EntityExistsException(
          "There already exists a clearing cost for country " + country);
    }

    var clearingCosts = new ClearingCosts();
    clearingCosts.setCountry(country);
    clearingCosts.setPrice(price);

    return clearingCostsRepository.save(clearingCosts);
  }

  @Override
  public List<ClearingCosts> readAllClearingCosts() {
    return clearingCostsRepository.findAll();
  }

  @Override
  @Cacheable("clearingCosts")
  public Optional<ClearingCosts> readClearingCostsById(Long id) {
    Objects.requireNonNull(id, "`id` should not be null");
    return clearingCostsRepository.findById(id);
  }

  @Override
  @Cacheable("clearingCosts")
  public Optional<ClearingCosts> readClearingCostsByCountry(String country) {
    return clearingCostsRepository.findByCountry(country)
        .or(clearingCostsRepository::findByCountryIsNull);
  }

  @Override
  public List<ClearingCosts> readClearingCostsByPriceRange(BigDecimal minPrice,
      BigDecimal maxPrice) {
    return clearingCostsRepository.findByPriceRange(minPrice, maxPrice);
  }

  @Override
  @CachePut("clearingCosts")
  public ClearingCosts updateClearingCosts(String country, BigDecimal price) {
    Objects.requireNonNull(price, "`price` should not be null");

    ClearingCosts clearingCosts = clearingCostsRepository.findByCountry(country)
        .orElseThrow(
            () -> new EntityNotFoundException(
                "No clearing costs for country='%s' found".formatted(country)));

    clearingCosts.setPrice(price);
    return clearingCostsRepository.save(clearingCosts);
  }

  @Override
  @CacheEvict("clearingCosts")
  public ClearingCosts deleteClearingCosts(Long id) {
    Objects.requireNonNull(id, "`id` should not be null");

    ClearingCosts clearingCosts = clearingCostsRepository.findById(id)
        .orElseThrow(
            () -> new EntityNotFoundException("No clearing costs for id=%d found".formatted(id)));

    clearingCostsRepository.delete(clearingCosts);
    return clearingCosts;
  }

  @Override
  @CacheEvict("clearingCosts")
  public ClearingCosts deleteClearingCostsByCountry(String country) {
    ClearingCosts clearingCosts = clearingCostsRepository.findByCountry(country)
        .orElseThrow(
            () -> new EntityNotFoundException(
                "No clearing costs for country='%s' found".formatted(country)));

    clearingCostsRepository.delete(clearingCosts);
    return clearingCosts;
  }
}
