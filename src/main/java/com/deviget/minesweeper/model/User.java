package com.deviget.minesweeper.model;

import com.deviget.minesweeper.entity.RoleEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class User extends RepresentationModel<User> {

  @JsonProperty("id")
  private UUID id;
  @JsonProperty("username")
  private String username;
  @JsonProperty("firstName")
  private String firstName;
  @JsonProperty("lastName")
  private String lastName;
  @JsonProperty("email")
  private String email;
  @NotNull(message = "User password is required.")
  @JsonProperty("password")
  private String password;
  @JsonProperty("role")
  private RoleEnum role;
}
