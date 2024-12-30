package com.github.shCHO9801.climbing_record_app.user.auth.dto;


import com.github.shCHO9801.climbing_record_app.user.entity.Role;
import com.github.shCHO9801.climbing_record_app.user.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor
public class CustomerUserDetails implements UserDetails {

  private final Long userNum;
  private final String id;
  private final String password;
  private final String email;
  private final Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> "ROLE_" + role.name());
  }

  @Override
  public String getUsername() {
    return this.id;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  // 필요한 경우 계정 상태에 따라 반환 값을 변경할 수 있습니다.
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public static CustomerUserDetails fromEntity(User user) {
    return new CustomerUserDetails(
        user.getUserNum(),
        user.getId(),
        user.getPassword(),
        user.getEmail(),
        user.getRole()
    );
  }
}
