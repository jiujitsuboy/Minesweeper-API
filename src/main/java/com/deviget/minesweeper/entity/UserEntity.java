package com.deviget.minesweeper.entity;


import com.deviget.minesweeper.model.User;
import java.util.List;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserEntity {

  @Id
  @GeneratedValue
  @Column(name="ID", updatable = false, nullable = false)
  private UUID id;

  @NotNull(message = "User name is required.")
  @Basic(optional = false)
  @Column(name = "USERNAME")
  private String username;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "ROLE")
  @Basic(optional = false)
  @Enumerated(EnumType.STRING)
  private RoleEnum role = RoleEnum.USER;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  List<MinesweeperGameEntity> games;

  public static UserEntity toEntity(User user){
    UserEntity userEntity = new UserEntity();
    BeanUtils.copyProperties(user, userEntity);

    return userEntity;
  }

  public static User toModel(UserEntity userEntity){
    User user = new User();
    BeanUtils.copyProperties(userEntity,user);

    return user;
  }
}
