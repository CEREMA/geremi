package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.commons.services.security.UsersSecurityServiceUtil;
import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.enums.Role;
import fr.cerema.dsi.geremi.repositories.UserRepository;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import fr.cerema.dsi.geremi.services.mapper.UserMapper;

@Service("userService")
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements UserService {


  private final UserMapper userMapper;
	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public User create(User entity) {
		return this.userRepository.save(entity);
	}

	@Override
	public List<User> findAll() {
		return this.userRepository.findAll();
	}

	@Override
	public User findById(Long id) {
		return this.userRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteById(Long id) {
		this.userRepository.deleteById(id);
	}

	@Override
	public Optional<UserDTO> findUserByEmail(String email) {
		return userRepository.findUserByEmail(email).map(this.userMapper::toDto);
	}

  @Override
  public Optional<UserDTO> getCurrentUser() {
      return getCurrentUserEntity().map(this.userMapper::toDto);
  }

  @Override
  public Optional<User> getCurrentUserEntity() {
    return UsersSecurityServiceUtil.getCurrentUsersLogin().flatMap(this.userRepository::findUserByEmail);
  }


  @Override
  public List<UserDTO> findAllAdminGeremi() {
    return this.userRepository.findAllByRole(Role.ADMIN.getLibelle()).stream().map(this.userMapper::toDto).toList();
  }

}
