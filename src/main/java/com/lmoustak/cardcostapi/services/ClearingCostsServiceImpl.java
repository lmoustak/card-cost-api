package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.entities.ClearingCosts;
import com.lmoustak.cardcostapi.repositories.ClearingCostsRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class ClearingCostsServiceImpl implements ClearingCostsService {

  private static final Logger logger = LoggerFactory.getLogger(ClearingCostsServiceImpl.class);

  private final ClearingCostsRepository clearingCostsRepository;

  public ClearingCostsServiceImpl(ClearingCostsRepository clearingCostsRepository) {
    this.clearingCostsRepository = clearingCostsRepository;
  }

  @Override
  @Caching(
      put = {
          @CachePut(value = "clearingCosts", key = "#result.id"),
          @CachePut(value = "clearingCosts", key = "#country", condition = "#country!=null")
      },
      evict = {
          @CacheEvict(value = "clearingCosts", key = "#result.id"),
          @CacheEvict(value = "clearingCosts", key = "#country", condition = "#country!=null")
      }
  )
  public ClearingCosts createClearingCosts(String country, BigDecimal price) {
    logger.debug("START createClearingCosts('{}', {})", country, price);

    Objects.requireNonNull(price, "`price` should not be null");

    Optional<ClearingCosts> optionalClearingCosts;
    if (country == null) {
      optionalClearingCosts = clearingCostsRepository.findByCountryIsNull();
    } else {
      optionalClearingCosts = clearingCostsRepository.findByCountry(country);
    }

    if (optionalClearingCosts.isPresent()) {
      logger.error("An entity for country '{}' already exists. Throwing...", country);
      throw new EntityExistsException(
          "There already exists a clearing cost for country " + country);
    } else {
      logger.debug("No entry for country '{}' exists yet. Creating...", country);
    }

    var clearingCosts = new ClearingCosts();
    clearingCosts.setCountry(country);
    clearingCosts.setPrice(price);

    ClearingCosts newEntity = clearingCostsRepository.save(clearingCosts);
    logger.debug("END createClearingCosts('{}', {}) returns {}", country, price, newEntity);
    return newEntity;
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
  @Caching(
      put = {
          @CachePut(value = "clearingCosts", key = "#result.id"),
          @CachePut(value = "clearingCosts", key = "#country", condition = "#country!=null")
      },
      evict = {
          @CacheEvict(value = "clearingCosts", key = "#result.id"),
          @CacheEvict(value = "clearingCosts", key = "#country", condition = "#country!=null")
      }
  )
  public ClearingCosts updateClearingCosts(String country, BigDecimal price) {
    logger.debug("START updateClearingCosts('{}', {})", country, price);
    Objects.requireNonNull(price, "`price` should not be null");

    Optional<ClearingCosts> optionalClearingCosts;
    if (country == null) {
      optionalClearingCosts = clearingCostsRepository.findByCountryIsNull();
    } else {
      optionalClearingCosts = clearingCostsRepository.findByCountry(country);
    }

    if (optionalClearingCosts.isEmpty()) {
      logger.error("No clearing costs entity found for country '{}'. Throwing...", country);
      throw new EntityNotFoundException(
          "No clearing costs for country='%s' found".formatted(country));
    }

    ClearingCosts clearingCosts = optionalClearingCosts.get();
    logger.debug("Found clearing costs entity {}", clearingCosts);

    clearingCosts.setPrice(price);
    ClearingCosts updatedEntity = clearingCostsRepository.save(clearingCosts);
    logger.debug("END updateClearingCosts('{}', {}) returns {}", country, price, updatedEntity);
    return updatedEntity;
  }

  @Override
  @Caching(
      evict = {
          @CacheEvict(value = "clearingCosts", key = "#id"),
          @CacheEvict(value = "clearingCosts", key = "#result.country", condition = "#result.country!=null")
      }
  )
  public ClearingCosts deleteClearingCosts(Long id) {
    logger.debug("START deleteClearingCosts({})", id);
    Objects.requireNonNull(id, "`id` should not be null");

    ClearingCosts clearingCosts = clearingCostsRepository.findById(id)
        .orElseThrow(
            () -> {
              logger.error("No clearing costs entity found for id {}", id);
              return new EntityNotFoundException("No clearing costs for id=%d found".formatted(id));
            });

    logger.debug("Found clearing costs entity {}", clearingCosts);
    clearingCostsRepository.delete(clearingCosts);
    logger.debug("END deleteClearingCosts({}) returns {}", id, clearingCosts);
    return clearingCosts;
  }

  @Override
  @Caching(
      evict = {
          @CacheEvict(value = "clearingCosts", key = "#result.id"),
          @CacheEvict(value = "clearingCosts", key = "#country", condition = "#country!=null")
      }
  )
  public ClearingCosts deleteClearingCostsByCountry(String country) {
    logger.debug("START deleteClearingCostsByCountry('{}')", country);
    Optional<ClearingCosts> optionalClearingCosts;
    if (country == null) {
      optionalClearingCosts = clearingCostsRepository.findByCountryIsNull();
    } else {
      optionalClearingCosts = clearingCostsRepository.findByCountry(country);
    }

    if (optionalClearingCosts.isEmpty()) {
      logger.error("No clearing costs entity found for country '{}'", country);
      throw new EntityNotFoundException(
          "No clearing costs for country='%s' found".formatted(country));
    }

    ClearingCosts clearingCosts = optionalClearingCosts.get();
    logger.debug("Found clearing costs entity {}", clearingCosts);

    clearingCostsRepository.delete(clearingCosts);
    logger.debug("END deleteClearingCostsByCountry({}) returns {}", country, clearingCosts);
    return clearingCosts;
  }
}
