package com.deviget.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class SignedInUser extends RepresentationModel<SignedInUser> implements Serializable {

  @JsonProperty("refreshToken")
  private String refreshToken;
  @JsonProperty("accessToken")
  private String accessToken;
  @JsonProperty("username")
  private String userName;
  @JsonProperty("userId")
  private UUID userId;

}
