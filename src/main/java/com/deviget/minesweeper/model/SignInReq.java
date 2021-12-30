package com.deviget.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInReq extends RepresentationModel<SignInReq>  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("username")
  private String userName;

  @JsonProperty("password")
  private String password;
}

