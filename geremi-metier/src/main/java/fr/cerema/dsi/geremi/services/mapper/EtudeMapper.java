package fr.cerema.dsi.geremi.services.mapper;


import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.services.ScenarioService;
import fr.cerema.dsi.geremi.services.dto.ScenarioDTO;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.EtudeDTO;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class EtudeMapper {

    @Autowired
    public UserMapper userMapper;

  @Autowired
  public UserService userService;

  @Autowired
  public ScenarioRepository scenarioRepository;

  @Mapping(target = "anneeFin", source = "anneeFin")
  @Mapping(target = "proprietaire", source = "user")
  @Mapping(target = "territoire", ignore = true)
  @Mapping(target = "zones", ignore = true)
  @Mapping(target = "ifImporte", source = "ifImporte")
  public abstract EtudeDTO toDto(Etude etude);

  @Mapping(target = "user", expression = "java(this.userService.getCurrentUserEntity().orElse(null))")
  @Mapping(target = "anneeFin", source = "anneeFin")
  public abstract Etude toEntity(EtudeDTO etudeDTO);

  @Mapping(target = "user", expression = "java(this.userService.getOne(etudeDTO.getProprietaire().getId()))")
  public abstract Etude toEntityFromImport(EtudeDTO etudeDTO);

  @AfterMapping
  public void toDtoAfterMapping(Etude entitySource, @MappingTarget EtudeDTO dtoTarget) {
    if (entitySource.getRelEtudeUserProcuration() != null && !entitySource.getRelEtudeUserProcuration().isEmpty() ){
      dtoTarget.setMandataires(entitySource.getRelEtudeUserProcuration()
        .stream()
        .map(r -> {
          UserDTO user = userMapper.toDto(r.getUser());
          user.setIfDroitEcriture(r.getIfDroitEcriture());
          return user;
        })
        .toList());
    }
    dtoTarget.getProprietaire().setIfDroitEcriture(true);

    this.scenarioRepository.getIdScenarioRetenuEtude(dtoTarget.getId())
      .ifPresent(sc-> dtoTarget.setScenarioRetenu(true));
  }
}
