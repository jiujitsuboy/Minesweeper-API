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
    String userName = "scott1";
    final String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzY290dDEiLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6IkRyZWFtIFRlYW0gQVBJIiwiZXhwIjoxNjM5NDQ1NjA1LCJpYXQiOjE2Mzk0NDQ3MDV9.jFJV4vhRMllJf1SaR5An9GrSB10fX66GCYOJYbj0t3JDwSmN_b1DYrkseCJQgDbWj6pFF8gGV0gb19N99dAga-x6w3rK7bjL0zO7y03bhXYEWSOqCzSlw8FqiFGSTpcNqykU6hLFn8AuiAIGjT1Y9jyhPhbKlb7Lq7IhUcmJrMJsHkXXBlk5237NTtp_LZjK0Kl3gZm7vmkdYInliWbVsNr4ehc24vcfykMPMHgZugpSYyN62b4O58HWYHnBwuxYYWtkyRFyCl_z75K8GsOyuOZ80HqsjDHXMuK1v7LVlOgy5tJsnDypqJIBe1-hj-KWyvSyZnXpUQqTTGby2cRKcBGH0QYSWiy1pASGNjYPcqAHa2j4UFQwQFKSO6XNO6BKtQ0i6xiTgnF0tOKRK1Y4Orjegr6KmQvYom5ZX6rcTZniH86VSiQVTq4cAzKTzTsfguv_GGzwqfv3gDkwjhH1Vs1CDDXLLb5OXudnpu62o4PBPlUUKbSwE9ntj1aDWDdTsxl86Jsx3fMMOvkYHY9Bba8T82JNIlmFNQXF9sscBdUNyQx55UMLbEz7N72KI1DWgU2UU5Qh2KIhdkD7yL3CDL5-B7y7vBe3Etb1Sc4HZfAHNFjcFXK1elaIzZUFGaqswLswy8wRbYyU7Qa29pesGsKwmXNej8h6fpzoQZKy6hg";
    final String refreshToken = "26rvap1cr3maf85akd9jb27c7dlrlkfn82hn3rahrb9qhr0rcmtl82jjt75tapoqtqktkh6rgdicb7mm1i38qqhdpgb7v8q3omoffu7dj1o8is3h763o54l978tfp95v";

    RefreshToken refreshTokenModel = new RefreshToken(refreshToken);
    SignedInUser signedInUser = new SignedInUser();
    signedInUser.setUserName(userName);
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
    signedInUser.setUserName(TestConstants.USER_NAME_A);
    signedInUser.setAccessToken(TestConstants.ACCESS_TOKEN);
    signedInUser.setRefreshToken(TestConstants.REFRESH_TOKEN);

    SignInReq signInReq = new SignInReq();
    signInReq.setUserName(TestConstants.USER_NAME_A);
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
    String userName = "scott1";
    String password = "tiger";

    SignInReq signInReq = new SignInReq();
    signInReq.setUserName(userName);
    signInReq.setPassword(password);

    when(userService.signUser(anyString(), anyString())).thenThrow(UsernameNotFoundException.class);

    mvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(signInReq)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void signInInsufficientAuthenticationException() throws Exception {
    String userName = "scott1";
    String password = "tiger";

    SignInReq signInReq = new SignInReq();
    signInReq.setUserName(userName);
    signInReq.setPassword(password);

    when(userService.signUser(anyString(), anyString())).thenThrow(InsufficientAuthenticationException.class);

    mvc.perform(post("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(signInReq)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void signOut() throws Exception {

    final String refreshToken = "26rvap1cr3maf85akd9jb27c7dlrlkfn82hn3rahrb9qhr0rcmtl82jjt75tapoqtqktkh6rgdicb7mm1i38qqhdpgb7v8q3omoffu7dj1o8is3h763o54l978tfp95v";

    RefreshToken refreshTokenModel = new RefreshToken(refreshToken);

    doNothing().when(userService).removeRefreshToken(any(RefreshToken.class));

    mvc.perform(delete("/api/v1/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(refreshTokenModel)))
        .andExpect(status().isAccepted());
  }

  @Test
  public void signOutInvalidRefreshTokenException() throws Exception {

    final String refreshToken = "26rvap1cr3maf85akd9jb27c7dlrlkfn82hn3rahrb9qhr0rcmtl82jjt75tapoqtqktkh6rgdicb7mm1i38qqhdpgb7v8q3omoffu7dj1o8is3h763o54l978tfp95v";

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
}