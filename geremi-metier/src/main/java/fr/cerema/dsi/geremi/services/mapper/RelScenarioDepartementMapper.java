package fr.cerema.dsi.geremi.services.mapper;

import fr.cerema.dsi.geremi.entities.RelScenarioDepartement;
import fr.cerema.dsi.geremi.services.DepartementService;
import fr.cerema.dsi.geremi.services.dto.RelScenarioDepartementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {})
public abstract class RelScenarioDepartementMapper {

  @Autowired
  protected DepartementService departementService;

  @Mapping(target = "idScenario", source = "scenario.id")
  @Mapping(target = "idDepartement", source = "departement.id")
  @Mapping(target = "idRelScenarioDepartement", source = "id")
  public abstract RelScenarioDepartementDTO toDto(RelScenarioDepartement relScenarioDepartement);

  @Mapping(target = "departement", expression = "java(this.departementService.findById(relScenarioDepartementDTO.getIdDepartement()))")
  @Mapping(target = "scenario", expression = "java(new Scenario())")
  public abstract RelScenarioDepartement toEntity(RelScenarioDepartementDTO relScenarioDepartementDTO);
}
