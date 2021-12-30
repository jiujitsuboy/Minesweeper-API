package com.deviget.minesweeper.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.deviget.minesweeper.controller.AuthController;
import com.deviget.minesweeper.entity.UserEntity;
import com.deviget.minesweeper.model.SignInReq;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.service.UserService;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserRepresentationModelAssembler extends RepresentationModelAssemblerSupport<UserEntity, User> {

  private UserService userService;
  public UserRepresentationModelAssembler(UserService userService) {
    super(AuthController.class, User.class);
    this.userService = userService;
  }

  @Override
  public User toModel(UserEntity entity) {
    User user = UserEntity.toModel(entity);
    SignInReq signInReq = new SignInReq(user.getUsername(),user.getPassword());
    user.setPassword("Ciphered...");
    user.add(linkTo(methodOn(AuthController.class).signIn(signInReq)).withRel("user-signin"));
    return user;
  }

//  public SignedInUser toModel(SignedInUser model) {
//    UserEntity userEntity =  userService.findUserByUserName(model.getUserName()).get();
//    return model.add(linkTo(methodOn(TeamController.class).getTeam(userEntity.getId().toString())).withRel("user-team"));
//  }


}
