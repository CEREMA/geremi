package fr.cerema.dsi.geremi.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@IntegrationTest
@ExtendWith(SpringExtension.class)
public class UsersServiceImplIT {

  //@Autowired
  //private UsersServiceImpl userService;


  //@Ignore
  //@Test
  //public void shouldReturnUserNullWhenUsernameDoesntExist() {
  //  SecurityContextHolder.getContext()
  //    .setAuthentication(new UsernamePasswordAuthenticationToken("ptardiveau@sqli", "admin"));
  //  Optional<User> user = userService.getCurrentUser();
  //  Assertions.assertTrue(user.isEmpty());
  //}
//
  //@Ignore
  //@Test
  //public void shouldReturnUserWhenUsernameExist() {
  //  User user = new User();
  //  user.setId(1L);
  //  user.setOrionId("");
  //  user.setEmail("ptardiveau@sqli.com");
  //  SecurityContextHolder.getContext()
  //    .setAuthentication(new UsernamePasswordAuthenticationToken("ptardiveau@sqli.com", "admin"));
  //  Optional<User> userCurrent = userService.getCurrentUser();
  //  Assertions.assertEquals(user.getEmail(), userCurrent.orElseThrow().getEmail());
  //}

}
