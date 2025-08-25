package com.lmoustak.cardcostapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.lmoustak.cardcostapi.dtos.BinTableResponse;
import com.lmoustak.cardcostapi.dtos.CardCostDto;
import com.lmoustak.cardcostapi.dtos.Country;
import com.lmoustak.cardcostapi.dtos.Data;
import com.lmoustak.cardcostapi.entities.ClearingCosts;
import com.lmoustak.cardcostapi.exceptions.BinTableException;
import com.lmoustak.cardcostapi.services.BinTableService;
import com.lmoustak.cardcostapi.services.BinTableServiceImpl;
import com.lmoustak.cardcostapi.services.CardCostService;
import com.lmoustak.cardcostapi.services.CardCostServiceImpl;
import com.lmoustak.cardcostapi.services.ClearingCostsService;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.ValueSources;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureCache(cacheProvider = CacheType.SIMPLE)
@ExtendWith(MockitoExtension.class)
public class CardCostServiceTests {

  private static List<ClearingCosts> sampleClearingCosts;

  private RestClient restClient;
  private ClearingCostsService clearingCostsService;
  private CardCostService cardCostService;

  @BeforeAll
  static void beforeAllTests() {
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

  }

  @BeforeEach
  void setupMocks() {
    restClient = mock();
    BinTableService binTableService = new BinTableServiceImpl(restClient);
    clearingCostsService = mock();
    cardCostService = new CardCostServiceImpl(clearingCostsService, binTableService);
  }

  private ClearingCosts getByCountry(String country) {
    return sampleClearingCosts.stream()
        .filter(entity -> country.equals(entity.getCountry()))
        .findFirst()
        .orElse(sampleClearingCosts.get(0));
  }

  @Test
  void card_cost_should_throw_if_iin_is_null() {
    assertThrows(NullPointerException.class,
        () -> cardCostService.findCardCostFromIssuerIdentificationNumber(null));
  }

  @Test
  void card_cost_should_throw_if_iin_is_less_then_6_digits() {
    String iin = "12345";
    assertThrows(IllegalArgumentException.class,
        () -> cardCostService.findCardCostFromIssuerIdentificationNumber(iin));
  }

  @Test
  void card_cost_should_return_country_code_on_bintable_api_success() {
    final String countryCode = "eg";
    final BigDecimal price = new BigDecimal("6.80");
    final String iin = "4032448502837539";
    final String bin = iin.substring(0, 6);

    var binTableResponse = new BinTableResponse();
    binTableResponse.setResult(200);
    binTableResponse.setMessage("SUCCESS");
    var data = new Data();
    var country = new Country();
    country.setCode(countryCode);
    data.setCountry(country);
    binTableResponse.setData(data);

    RestClient.RequestHeadersUriSpec<?> uriSpec = mock();
    doReturn(uriSpec).when(restClient).get();

    RestClient.RequestHeadersSpec<?> headersSpec = mock();
    doReturn(headersSpec).when(uriSpec).uri("/{bin}?api_key={apiKey}", Map.of("bin", bin));

    RestClient.ResponseSpec responseSpec = mock();
    when(headersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(BinTableResponse.class)).thenReturn(binTableResponse);

    var clearingCost = new ClearingCosts();
    clearingCost.setId(56L);
    clearingCost.setCountry(countryCode);
    clearingCost.setPrice(price);
    when(clearingCostsService.readClearingCostsByCountry(countryCode))
        .thenReturn(Optional.of(clearingCost));

    assertEquals(new CardCostDto(countryCode, price),
        cardCostService.findCardCostFromIssuerIdentificationNumber(iin));

  }

  @ParameterizedTest
  @ValueSource(ints = {401, 403, 422})
  void card_cost_should_throw_on_bintable_api_error_status(int statusCode) {
    var binTableResponse = new BinTableResponse();
    binTableResponse.setResult(statusCode);

    String message = switch (statusCode) {
      case 401 -> "Your balance is exhausted,or package expired";
      case 403 -> "Invalid API Key";
      case 422 -> "API key is missing";
      default -> throw new IllegalArgumentException("Unexpected status code: " + statusCode);
    };
    binTableResponse.setMessage(message);

    var data = new Data();
    binTableResponse.setData(data);

    RestClient.RequestHeadersUriSpec<?> uriSpec = mock();
    doReturn(uriSpec).when(restClient).get();

    RestClient.RequestHeadersSpec<?> headersSpec = mock();
    doReturn(headersSpec).when(uriSpec).uri("/{bin}?api_key={apiKey}", Map.of("bin", "123456"));

    RestClient.ResponseSpec responseSpec = mock();
    when(headersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
        .thenThrow(new BinTableException(HttpStatusCode.valueOf(statusCode), message));

    BinTableException e = assertThrows(BinTableException.class,
        () -> cardCostService.findCardCostFromIssuerIdentificationNumber("123456"));

    assertEquals(statusCode, e.getCode().value());
    assertEquals(message, e.getMessage());

  }

}
