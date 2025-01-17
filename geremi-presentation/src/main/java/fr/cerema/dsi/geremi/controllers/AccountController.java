package fr.cerema.dsi.geremi.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "12 - Account", description = "Permet d'interroger le niveau d'authorisation de l'utilisateur courant")
@RestController
@RequestMapping("/api")
@CrossOrigin
public class AccountController {

  private final UserService userService;


  public AccountController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Récupère l'utilisateur actuellement connecté")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "L'utilisateur connecté", content = {
      @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = UserDTO.class)
      )
    }),
    @ApiResponse(responseCode = "404", description = "L'utilisateur n'existe pas dans l'application", content = @Content)
  })
  @GetMapping("/user")
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  @PreAuthorize("hasAnyRole('DREAL', 'ADMIN', 'CEREMA', 'GEST', 'PUBLIC')")
  public ResponseEntity<UserDTO> getCurrentUsers() {
    Optional<UserDTO> userOptional = this.userService.getCurrentUser();
    if (userOptional.isPresent()) {
      return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
      "L'utilisateur n'existe pas dans l'application");
  }
}
