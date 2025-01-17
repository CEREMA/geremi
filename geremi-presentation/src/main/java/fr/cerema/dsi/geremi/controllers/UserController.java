package fr.cerema.dsi.geremi.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.enums.Role;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import fr.cerema.dsi.geremi.services.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Utilisateurs de GEREMI", description = "Permet d'interroger et de parcourir les utilisateurs de GEREMI")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private final UserService userService;

  @Autowired
  private final UserMapper userMapper;

  @Autowired
  public UserController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @Operation(summary = "Récupère le Profil de l'utilisateur", description = "Permet d'obtenir le Profil de l'utilisateur GEREMI")
  @ApiResponse(responseCode = "200", description = "Profil de l'utilisateur GEREMI demandé", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class)) })
  @ApiResponse(responseCode = "404", description = "Profil de l'utilisateur n'a pas été trouvée")
  @GetMapping("/user/{email}")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
    log.debug("Requête REST pour obtenir le profil");
    Optional<UserDTO> user = this.userService.findUserByEmail(email);
    return user.map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/user/alluser")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    log.debug("REST request to get all users");
    List<User> users = this.userService.findAll();
    if (users != null && !users.isEmpty()) {
      List<UserDTO> userDTOs = users.stream().map(this.userMapper::toDto).collect(Collectors.toList());
      return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping("/user/users-delegation")
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST')")
  public ResponseEntity<List<UserDTO>> getUsersDelegation() {
    log.debug("REST request to get users for delegation");
    List<User> users = this.userService.findAll();
    if (users != null && !users.isEmpty()) {
      List<UserDTO> userDTOs = users.stream().map(this.userMapper::toDto).filter(u -> !Role.PUBLIC.getLibelle().equals(u.getLibelleProfil())).collect(Collectors.toList());
      return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Récupère tous les administrateurs GEREMI",
    description =
      "Permet d'obtenir la liste de tous les utilisateurs qui sont administrateurs GEREMI")
  @ApiResponse(responseCode = "200", description = "Les administrateurs GEREMI demandés", content = {
    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
      array = @ArraySchema(schema = @Schema(implementation = User.class))
    )}
  )
  @GetMapping("/user/admin-geremi")
  @PreAuthorize("hasAnyRole('ADMIN', 'GEST')")
  public ResponseEntity<List<UserDTO>> getAllAdminGeremi() {
    log.debug("Requête REST pour obtenir tous les administrateurs GEREMI");
    List<UserDTO> userDTOList = this.userService.findAllAdminGeremi();
    return new ResponseEntity<>(userDTOList, HttpStatus.OK);
  }

}
