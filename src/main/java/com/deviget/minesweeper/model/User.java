package com.deviget.minesweeper.model;

import com.deviget.minesweeper.entity.RoleEnum;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class  User extends RepresentationModel<User> {

  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  @NotNull(message = "User password is required.")
  private String password;
  private RoleEnum role;
}
