package com.lmoustak.cardcostapi.services;

import com.lmoustak.cardcostapi.dtos.BinTableResponse;
import java.util.Map;
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
    String bin = issuerIdentificationNumber.substring(0, 6);
    BinTableResponse response = restClient.get()
        .uri("/{bin}?api_key={apiKey}", Map.of("bin", bin))
        .retrieve()
        .body(BinTableResponse.class);

    if (response == null) {
      throw new NullPointerException("Response from bintable.com was null");
    }

    Integer result = response.getResult();
    if (result == null) {
      throw new NullPointerException("Result HTTP code from bintable.com was null");
    }

    String message = response.getMessage();
    HttpStatusCode status = HttpStatusCode.valueOf(response.getResult());
    if (status.is4xxClientError()) {
      throw new HttpClientErrorException(status, message);
    } else if (status.is5xxServerError()) {
      throw new HttpServerErrorException(status, message);
    }

    return response.getData().getCountry().getCode();
  }
}
