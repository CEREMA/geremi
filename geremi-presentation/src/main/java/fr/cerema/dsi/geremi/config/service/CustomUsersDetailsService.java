package fr.cerema.dsi.geremi.config.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.security.exceptions.NoRoleForUsersServiceException;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import fr.cerema.dsi.geremi.services.mapper.UserMapper;


@Service
public class CustomUsersDetailsService implements UserDetailsService {

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  private static final String PREFIX_ROLE = "ROLE_";

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // récupération des rôles
    Optional<UserDTO> user = userService.findUserByEmail(username);
    if (user.isPresent() && user.get().getLibelleProfil() != null) {
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(PREFIX_ROLE + user.get().getLibelleProfil()));
      return new org.springframework.security.core.userdetails.User(username, "", authorities);
    } else {
      throw new NoRoleForUsersServiceException(
        String.format("L'utilisateur %s n'a pas de role nous ne pouvons pas l'authentifier",
          username));
    }
  }

}
