package com.deviget.minesweeper.exception;

public class InvalidRefreshTokenException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidRefreshTokenException(final String message) {
    super(message);
  }
}
