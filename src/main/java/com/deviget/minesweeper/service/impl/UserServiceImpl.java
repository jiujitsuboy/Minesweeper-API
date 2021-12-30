package com.deviget.minesweeper.service.impl;

import com.deviget.minesweeper.entity.RoleEnum;
import com.deviget.minesweeper.entity.UserEntity;
import com.deviget.minesweeper.entity.UserTokenEntity;
import com.deviget.minesweeper.exception.GenericAlreadyExistsException;
import com.deviget.minesweeper.exception.InvalidRefreshTokenException;
import com.deviget.minesweeper.model.RefreshToken;
import com.deviget.minesweeper.model.SignedInUser;
import com.deviget.minesweeper.model.User;
import com.deviget.minesweeper.repository.UserRepository;
import com.deviget.minesweeper.repository.UserTokenRepository;
import com.deviget.minesweeper.security.JwtManager;
import com.deviget.minesweeper.service.UserService;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserTokenRepository userTokenRepository;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final JwtManager tokenManager;

  public UserServiceImpl(UserRepository userRepository, UserTokenRepository userTokenRepository,
      PasswordEncoder bCryptPasswordEncoder, JwtManager tokenManager
  ) {
    this.userRepository = userRepository;
    this.userTokenRepository = userTokenRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.tokenManager = tokenManager;
  }

  @Override
  public UserEntity signUp(User user) {
    return createUser(user);
  }

  @Override
  @Transactional
  public SignedInUser signUser(String username, String password) {

    if (Strings.isBlank(username)) {
      throw new UsernameNotFoundException("Invalid user.");
    }
    if (Strings.isBlank(password)) {
      throw new UsernameNotFoundException("Invalid password.");
    }
    final String uname = username.trim();
    Optional<UserEntity> oUserEntity = findUserByUserName(uname);
    UserEntity userEntity = oUserEntity.orElseThrow(
        () -> new InsufficientAuthenticationException("Unauthorized."));

    if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword())) {
      throw new InsufficientAuthenticationException("Unauthorized.");
    }
    return getSignedInUser(userEntity);
  }

  @Override
  public void removeRefreshToken(RefreshToken refreshToken) {
    userTokenRepository.findByRefreshToken(refreshToken.getRefreshToken())
        .ifPresentOrElse(userTokenRepository::delete, () -> {
          throw new InvalidRefreshTokenException("Invalid token.");
        });
  }

  @Override
  public Optional<SignedInUser> getAccessToken(RefreshToken refreshToken) {
    // You may add an additional validation for time that would remove/invalidate the refresh token
    return userTokenRepository.findByRefreshToken(refreshToken.getRefreshToken())
        .map(ut -> {
          SignedInUser signedInUser = createSignedInUser(ut.getUser());
          signedInUser.setRefreshToken(refreshToken.getRefreshToken());
          return Optional.of(signedInUser);
        })
        .orElseThrow(() -> new InvalidRefreshTokenException("Invalid token."));
  }

  @Override
  public Optional<UserEntity> findUserByUserName(String username) {
    return userRepository.findByUsername(username);
  }

  @Transactional
  public SignedInUser getSignedInUser(UserEntity userEntity) {
    userTokenRepository.deleteByUserId(userEntity.getId());
    return createSignedUserWithRefreshToken(userEntity);
  }

  @Transactional
  protected UserEntity createUser(User user) {
    Integer count = userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());
    if (count > 0) {
      throw new GenericAlreadyExistsException("Use different username and email.");
    }
    if (user.getRole() == null) {
      user.setRole(RoleEnum.USER);
    }
    UserEntity userEntity = UserEntity.toEntity(user);
    userEntity.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    userRepository.save(userEntity);
    return userEntity;
  }

  private SignedInUser createSignedUserWithRefreshToken(UserEntity userEntity) {
    SignedInUser signedInUser = createSignedInUser(userEntity);
    signedInUser.setRefreshToken(createRefreshToken(userEntity));
    return signedInUser;
  }

  private SignedInUser createSignedInUser(UserEntity userEntity) {
    String token = tokenManager.create(org.springframework.security.core.userdetails.User.builder()
        .username(userEntity.getUsername())
        .password(userEntity.getPassword())
        .authorities(Objects.nonNull(userEntity.getRole()) ? userEntity.getRole().name() : "")
        .build());

    SignedInUser signedInUser = new SignedInUser();
    signedInUser.setUserId(userEntity.getId());
    signedInUser.setUserName(userEntity.getUsername());
    signedInUser.setAccessToken(token);

    return signedInUser;
  }

  private String createRefreshToken(UserEntity user) {
    String token = RandomHolder.randomKey(128);
    UserTokenEntity userTokenEntity = new UserTokenEntity();
    userTokenEntity.setRefreshToken(token);
    userTokenEntity.setUser(user);
    userTokenRepository.save(userTokenEntity);
    return token;
  }

  /**
   * Generate secure randoms Strings
   */
  private static class RandomHolder {

    static final Random random = new SecureRandom();

    public static String randomKey(int length) {
      return String.format("%" + length + "s", new BigInteger(length * 5/*base 32,2^5*/, random)
          .toString(32)).replace('\u0020', '0');
    }
  }
}
