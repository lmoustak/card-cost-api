package com.lmoustak.cardcostapi.controllers;

import com.lmoustak.cardcostapi.exceptions.BinTableException;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BinTableException.class)
  public ResponseEntity<ProblemDetail> handleBinTableException(BinTableException e,
      WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getCode(),
        e.getMessage());

    problemDetail.setTitle("An error occurred while calling the BINTable API");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(e.getCode().value())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(EntityNotFoundException e,
      WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
        e.getMessage());

    problemDetail.setTitle("Resource not found");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
  public ResponseEntity<ProblemDetail> handleBadRequest(RuntimeException e, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        e.getMessage());

    problemDetail.setTitle("Bad request");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "One or more validation errors occurred. Please check the `errors` field.");
    problemDetail.setTitle("Validation error");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    List<FieldError> fieldErrors = ex.getFieldErrors();
    Map<String, String> fieldByMessage = new HashMap<>(fieldErrors.size());
    for (var error : fieldErrors) {
      fieldByMessage.put(error.getField(), error.getDefaultMessage());
    }

    problemDetail.setProperty("errors", fieldByMessage);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "A path variable (%s) is missing.".formatted(ex.getVariableName()));
    problemDetail.setTitle("Missing path variable");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status,
      WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
        "A request parameter (%s) is missing.".formatted(ex.getParameterName()));
    problemDetail.setTitle("Missing request parameter");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleAnyOtherError(Exception ex, WebRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
        "Something happened on the server: " + ex.getLocalizedMessage());
    problemDetail.setTitle("Internal server error");
    problemDetail.setInstance(URI.create(request.getContextPath()));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }
}
