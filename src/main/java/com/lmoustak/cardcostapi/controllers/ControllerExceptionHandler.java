package com.lmoustak.cardcostapi.controllers;

import jakarta.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(
      value = {HttpClientErrorException.class, HttpServerErrorException.class},
      produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE
  )
  public ResponseEntity<ProblemDetail> handleBinTableException(HttpStatusCodeException e,
      WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getStatusCode(),
        e.getMessage());

    problemDetail.setTitle("An error occurred while calling the BINTable API");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(e.getStatusCode().value())
        .body(problemDetail);
  }

  @ExceptionHandler(
      value = EntityNotFoundException.class,
      produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE
  )
  public ResponseEntity<ProblemDetail> handleResourceNotFound(EntityNotFoundException e,
      WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
        e.getMessage());

    problemDetail.setTitle("Resource not found");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(problemDetail);
  }

  @ExceptionHandler(
      value = {NullPointerException.class, IllegalArgumentException.class},
      produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE
  )
  public ResponseEntity<ProblemDetail> handleBadRequest(RuntimeException e, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        e.getMessage());

    problemDetail.setTitle("Bad request");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "One or more validation errors occurred");
    problemDetail.setTitle("Validation error");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    List<FieldError> fieldErrors = ex.getFieldErrors();
    Map<String, String> fieldByMessage = new HashMap<>(fieldErrors.size());
    for (var error : fieldErrors) {
      fieldByMessage.put(error.getField(), error.getDefaultMessage());
    }

    problemDetail.setProperty("errors", fieldByMessage);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "A path variable (%s) is missing.".formatted(ex.getVariableName()));
    problemDetail.setTitle("Missing path variable");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status,
      WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "A request parameter (%s) is missing.".formatted(ex.getParameterName()));
    problemDetail.setTitle("Missing request parameter");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
  }
}
