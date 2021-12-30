package com.deviget.minesweeper.repository;

import com.deviget.minesweeper.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

  Optional<UserEntity> findByUsername(String username);

  @Query(value = "select count(u.*) from user u where u.username = :username or u.email = :email", nativeQuery = true)
  Integer findByUsernameOrEmail(String username, String email);
}
