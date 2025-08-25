package com.lmoustak.cardcostapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmoustak.cardcostapi.dtos.BinTableResponse;
import com.lmoustak.cardcostapi.exceptions.BinTableException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BinTableServiceImpl implements BinTableService {

  private static final Logger logger = LoggerFactory.getLogger(BinTableServiceImpl.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final RestClient restClient;
  private final String apiKey;

  @Autowired
  public BinTableServiceImpl(@Value("${bintable.api-key}") String apiKey) {
    this.apiKey = apiKey;
    restClient = RestClient.builder()
        .baseUrl("https://api.bintable.com/v1")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultUriVariables(Map.of("apiKey", this.apiKey))
        .build();
  }

  public BinTableServiceImpl(RestClient restClient) {
    this.apiKey = "";
    this.restClient = restClient;
  }

  @Override
  @Cacheable(cacheNames = "bins")
  public String getCountryFromIssuerIdentificationNumber(String issuerIdentificationNumber) {

    logger.debug("START getCountryFromIssuerIdentificationNumber('{}')",
        issuerIdentificationNumber);

    Objects.requireNonNull(issuerIdentificationNumber, "IIN should not be null");

    if (issuerIdentificationNumber.length() < 6) {
      throw new IllegalArgumentException("The IIN should be at least 6 digits long");
    }

    String bin = issuerIdentificationNumber.substring(0, 6);
    logger.debug("Using bin='{}', apiKey='{}'", bin, apiKey);

    BinTableResponse response = restClient.get()
        .uri("/{bin}?api_key={apiKey}", Map.of("bin", bin))
        .retrieve()
        .onStatus(HttpStatusCode::isError, (req, res) -> {
          try (InputStream is = res.getBody()) {
            BinTableResponse errorResponse = objectMapper.readValue(is.readAllBytes(),
                BinTableResponse.class);

            HttpStatusCode statusCode = res.getStatusCode();
            String message = errorResponse.getMessage();
            logger.error("BINTable API returned with error status code {} {} and message '{}'",
                statusCode.value(), statusCode, message);
            throw new BinTableException(statusCode, message);
          }
        })
        .body(BinTableResponse.class);

    logger.debug("BINTable API returned with success: {}", response);
    String code = response.getData().getCountry().getCode();
    logger.debug("END getCountryFromIssuerIdentificationNumber('{}') returns '{}'",
        issuerIdentificationNumber, code);
    return code;
  }
}
