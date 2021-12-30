package com.deviget.minesweeper.exception;

public class GameNotFoundException extends RuntimeException {

  public GameNotFoundException(final String message) {
    super(message);
  }

}
