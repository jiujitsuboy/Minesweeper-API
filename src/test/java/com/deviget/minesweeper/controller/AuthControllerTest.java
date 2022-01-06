package com.deviget.minesweeper.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deviget.minesweeper.TestConstants;
import com.deviget.minesweeper.configuration.AppConfig;
import com.deviget.minesweeper.entity.UserEntity;
import com.deviget.minesweeper.exception.GenericAlreadyExistsException;
import com.deviget.minesweeper.exception.InvalidRefreshTokenException;
import com.deviget.minesweeper.hateoas.UserRepresentationModelAssembler;
import com.deviget.minesweeper.model.RefreshToken;
import com.deviget.minesweeper.model.SignInReq;
import com.deviget.minesweeper.model.SignedInUser;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@Import(AppConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper json;
  @MockBean
  private UserService userService;
  @SpyBean
  UserRepresentationModelAssembler userAssembler;

  @Test
  public void getAccessToken() throws Exception {
    String userName = TestConstants.USER_NAME_A;
    final String accessToken = TestConstants.ACCESS_TOKEN;
    final String refreshToken = TestConstants.REFRESH_TOKEN;

    RefreshToken refreshTokenModel = new RefreshToken(refreshToken);
    SignedInUser signedInUser = new SignedInUser();
    signedInUser.setUsername(userName);
    signedInUser.setAccessToken(accessToken);
    signedInUser.setRefreshToken(refreshToken);

    when(userService.getAccessToken(any(RefreshToken.class))).thenReturn(Optional.of(signedInUser));

    mvc.perform(post("/api/v1/auth/token/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(refreshTokenModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username", is(userName)))
        .andExpect(jsonPath("$.accessToken", is(accessToken)))
        .andExpect(jsonPath("$.refreshToken", is(refreshToken)));
  }

  @Test
  public void signIn() throws Exception {

    SignedInUser signedInUser = new SignedInUser();
    signedInUser.setUsername(TestConstants.USER_NAME_A);
    signedInUser.setAccessToken(TestConstants.ACCESS_TOKEN);
    signedInUser.setRefreshToken(TestConstants.REFRESH_TOKEN);

    SignInReq signInReq = new SignInReq();
    signInReq.setUsername(TestConstants.USER_NAME_A);
    signInReq.setPassword(TestConstants.USER_PASSWORD_A);

    UserEntity userEntity = TestConstants.getTestUserEntity(UUID.randomUUID(), TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);

    when(userService.signUser(anyString(), anyString())).thenReturn(signedInUser);
    when(userService.findUserByUserName(anyString())).thenReturn(Optional.of(userEntity));

    mvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(signInReq)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.username", is(TestConstants.USER_NAME_A)))
        .andExpect(jsonPath("$.accessToken", is(TestConstants.ACCESS_TOKEN)))
        .andExpect(jsonPath("$.refreshToken", is(TestConstants.REFRESH_TOKEN)));
  }

  @Test
  public void signInUsernameNotFoundException() throws Exception {
    String userName = TestConstants.USER_NAME_A;
    String password = TestConstants.USER_PASSWORD_A;

    SignInReq signInReq = new SignInReq();
    signInReq.setUsername(userName);
    signInReq.setPassword(password);

    when(userService.signUser(anyString(), anyString())).thenThrow(UsernameNotFoundException.class);

    mvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(signInReq)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void signInInsufficientAuthenticationException() throws Exception {
    String userName = TestConstants.USER_NAME_A;
    String password = TestConstants.USER_PASSWORD_A;

    SignInReq signInReq = new SignInReq();
    signInReq.setUsername(userName);
    signInReq.setPassword(password);

    when(userService.signUser(anyString(), anyString())).thenThrow(InsufficientAuthenticationException.class);

    mvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(signInReq)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void signOut() throws Exception {

    final String refreshToken = TestConstants.REFRESH_TOKEN;

    RefreshToken refreshTokenModel = new RefreshToken(refreshToken);

    doNothing().when(userService).removeRefreshToken(any(RefreshToken.class));

    mvc.perform(delete("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(refreshTokenModel)))
        .andExpect(status().isAccepted());
  }

  @Test
  public void signOutInvalidRefreshTokenException() throws Exception {

    final String refreshToken = TestConstants.REFRESH_TOKEN;

    RefreshToken refreshTokenModel = new RefreshToken(refreshToken);

    doThrow(InvalidRefreshTokenException.class).when(userService).removeRefreshToken(any(RefreshToken.class));

    mvc.perform(delete("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(refreshTokenModel)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void signUp() throws Exception {

    UUID userUUID = UUID.randomUUID();
    User user = TestConstants.getTestUser(userUUID, TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);
    UserEntity userEntity = TestConstants.getTestUserEntity(user);

    when(userService.signUp(any(User.class))).thenReturn(userEntity);

    mvc.perform(post("/api/v1/auth/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(user)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(userUUID.toString())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.password", is("Ciphered...")))
        .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(user.getLastName())))
        .andExpect(jsonPath("$.email", is(user.getEmail())));
  }

  @Test
  public void signUpGenericAlreadyExistsException() throws Exception {

    UUID userUUID = UUID.randomUUID();
    User user = TestConstants.getTestUser(userUUID, TestConstants.USER_NAME_A, TestConstants.USER_PASSWORD_A,
        TestConstants.USER_FIRST_NAME_A, TestConstants.USER_LAST_NAME_A, TestConstants.USER_EMAIL_A);

    when(userService.signUp(any(User.class))).thenThrow(GenericAlreadyExistsException.class);

    mvc.perform(post("/api/v1/auth/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(user)))
        .andExpect(status().isConflict());
  }
}