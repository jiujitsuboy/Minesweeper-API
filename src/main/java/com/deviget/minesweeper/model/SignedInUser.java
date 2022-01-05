package com.deviget.minesweeper.model;

import java.util.UUID;
import lombok.Data;

@Data
public class SignedInUser {

  private String refreshToken;
  private String accessToken;
  private String username;
  private UUID userId;

}
