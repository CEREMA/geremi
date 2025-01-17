package fr.cerema.dsi.geremi.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.services.dto.UserDTO;

@Service("userService")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public interface UserService extends GenericService<User, Long> {

  Optional<UserDTO> findUserByEmail(String email);

  Optional<UserDTO> getCurrentUser();

  Optional<User> getCurrentUserEntity();

  List<UserDTO> findAllAdminGeremi();
}
