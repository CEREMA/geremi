package fr.cerema.dsi.geremi.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "API open", description = "Permet d'interroger et de parcourir les API sans authentification")
@RestController
@RequestMapping("/api-open")
@CrossOrigin
public class ApiOpenController {

  private final Logger log = LoggerFactory.getLogger(ApiOpenController.class);

  private final UserService userService;


  @Autowired
  public ApiOpenController(UserService usersService, TracabiliteEtapeService tracabiliteEtapeService) {
    this.userService = usersService;
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
  public ResponseEntity<List<UserDTO>> getAllAdminGeremi() {
    log.debug("Requête REST pour obtenir tous les administrateurs GEREMI");
    List<UserDTO> userDTOList = this.userService.findAllAdminGeremi();
    return new ResponseEntity<>(userDTOList, HttpStatus.OK);
  }




}
