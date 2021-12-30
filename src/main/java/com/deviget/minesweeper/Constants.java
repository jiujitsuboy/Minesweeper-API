package com.deviget.minesweeper;

/**
 * @author : github.com/sharmasourabh
 * @project : Chapter06 - Modern API Development with Spring and Spring Boot
 **/
public class Constants {
  public static final String ENCODER_ID = "bcrypt";
  public static final String API_URL_PREFIX = "/api/v1/**";
  public static final String SWAGGER_2_DOCS_URL = "/v2/api-docs/**";
  public static final String SWAGGER_2_UI_URL = "/swagger-ui.html/**";
  public static final String H2_URL_PREFIX = "/h2-console/**";
  public static final String SIGNUP_URL = "/api/v1/auth/users";
  public static final String TOKEN_URL = "/api/v1/auth/token";
  public static final String REFRESH_URL = "/api/v1/auth/token/refresh";;
  public static final long EXPIRATION_TIME = 120000;//900_000; // 15 mins
  public static final String ROLE_CLAIM = "roles";
  public static final String AUTHORITY_PREFIX = "ROLE_";
}
