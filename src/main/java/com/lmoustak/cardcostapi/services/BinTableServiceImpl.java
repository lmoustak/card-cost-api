package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.dtos.BinTableResponse;
import com.lmoustak.cardcostapi.exceptions.BinTableException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Service
public class BinTableServiceImpl implements BinTableService {

  private final RestClient restClient;

  public BinTableServiceImpl(@Value("${bintable.api-key}") String apiKey) {
    restClient = RestClient.builder()
        .baseUrl("https://api.bintable.com/v1")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultUriVariables(Map.of("apiKey", apiKey))
        .build();
  }

  @Override
  @Cacheable(cacheNames = "bins")
  public String getCountryFromIssuerIdentificationNumber(String issuerIdentificationNumber) {

    Objects.requireNonNull(issuerIdentificationNumber, "IIN should not be null");

    if (issuerIdentificationNumber.length() < 6) {
      throw new IllegalArgumentException("The IIN should be at least 6 digits long");
    }

    String bin = issuerIdentificationNumber.substring(0, 6);
    BinTableResponse response = restClient.get()
        .uri("/{bin}?api_key={apiKey}", Map.of("bin", bin))
        .retrieve()
        .onStatus(HttpStatusCode::isError, (req, res) -> {
          try (InputStream is = res.getBody()) {
            String errorResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            throw new BinTableException(res.getStatusCode(), errorResponse);
          }
        })
        .body(BinTableResponse.class);

    System.out.println("Finished request");

    return response.getData().getCountry().getCode();
  }
}
