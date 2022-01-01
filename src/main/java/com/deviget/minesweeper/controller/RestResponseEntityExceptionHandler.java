package com.deviget.minesweeper.controller;

import com.deviget.minesweeper.exception.GameIsOverException;
import com.deviget.minesweeper.exception.GameNotFoundException;
import com.deviget.minesweeper.exception.GenericAlreadyExistsException;
import com.deviget.minesweeper.exception.InvalidGameParameter;
import com.deviget.minesweeper.exception.InvalidRefreshTokenException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

  @ExceptionHandler(GenericAlreadyExistsException.class)
  protected ResponseEntity<Object> genericAlreadyExistsException(
      GenericAlreadyExistsException ex, WebRequest request) {

      Map<String, Object> body = new LinkedHashMap<>();
      body.put("timestamp", LocalDateTime.now());
      body.put("message", ex.getMessage());

      return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  protected ResponseEntity<Object> invalidRefreshTokenException(
      InvalidRefreshTokenException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(GameNotFoundException.class)
  protected ResponseEntity<Object> noSuchPlayerException(
      GameNotFoundException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InsufficientAuthenticationException.class)
  protected ResponseEntity<Object> insufficientAuthenticationException(
      InsufficientAuthenticationException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  protected ResponseEntity<Object> usernameNotFoundException(
      UsernameNotFoundException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(GameIsOverException.class)
  protected ResponseEntity<Object> gameIsOverException(
      GameIsOverException ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidGameParameter.class)
  protected ResponseEntity<Object> invalidGameParameter(
      InvalidGameParameter ex, WebRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}

