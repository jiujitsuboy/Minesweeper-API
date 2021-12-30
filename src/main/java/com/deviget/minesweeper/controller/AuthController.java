package com.deviget.minesweeper.controller;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import com.deviget.minesweeper.hateoas.UserRepresentationModelAssembler;
import com.deviget.minesweeper.model.RefreshToken;
import com.deviget.minesweeper.model.SignInReq;
import com.deviget.minesweeper.model.SignedInUser;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Api(value = "Authorization Controller")
public class AuthController {

  private final UserService service;
  private final UserRepresentationModelAssembler userAssembler;

  public AuthController(UserService service, UserRepresentationModelAssembler userAssembler) {
    this.service = service;
    this.userAssembler = userAssembler;
  }

  @ApiOperation(value = "Refresh Tokens", nickname = "getAccessToken", notes = "Generate a new Refresh and Access Tokens-")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Refresh current tokens and generate a new pair."),
      @ApiResponse(code = 400, message = "Invalid Refresh Token Exception.") })
  @PostMapping(value = "/token/refresh")
  public ResponseEntity<SignedInUser> getAccessToken(@Valid @RequestBody(required = false) RefreshToken refreshToken) {
    return ok(service.getAccessToken(refreshToken).get());
  }

  @ApiOperation(value = "SignIn user", nickname = "signIn", notes = "Sign In an User and generate the access and refresh token-")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "SignIn user and generate tokens."),
      @ApiResponse(code = 404, message = "Username Not Found Exception."),
      @ApiResponse(code = 401, message = "Insufficient Authentication Exception.")})
  @PostMapping(value = "/token")
  public ResponseEntity<?> signIn(@Valid @RequestBody(required = false) SignInReq signInReq) {
      return status(HttpStatus.ACCEPTED).body(service.signUser(signInReq.getUserName(), signInReq.getPassword()));
  }

  @ApiOperation(value = "SignOut user", nickname = "signOut", notes = "Sign out an User and destroy the refresh token-")
  @ApiResponses(value = {
      @ApiResponse(code = 202, message = "SignOut user and destroy refresh token."),
      @ApiResponse(code = 400, message = "Invalid Refresh Token Exception.")})
  @DeleteMapping(value = "/token")
  public ResponseEntity<Void> signOut(@Valid @RequestBody(required = false) RefreshToken refreshToken) {
      service.removeRefreshToken(refreshToken);
      return accepted().build();
  }

  @ApiOperation(value = "SignUp user", nickname = "signUp", notes = "Sign up an new User and create his random team")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "SignUp user and create his team."),
      @ApiResponse(code = 409, message = "Generic Already Exists Exception.")})
  @PostMapping(value = "/users")
  public ResponseEntity<User> signUp(@Valid @RequestBody(required = false) User user) {
    return status(HttpStatus.CREATED).body(userAssembler.toModel(service.signUp(user)));
  }

}
