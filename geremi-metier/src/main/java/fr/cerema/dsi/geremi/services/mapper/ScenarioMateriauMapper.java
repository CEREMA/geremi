package fr.cerema.dsi.geremi.services.mapper;

import fr.cerema.dsi.geremi.entities.ScenarioMateriau;
import fr.cerema.dsi.geremi.repositories.MateriauRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.dto.ScenarioMateriauDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {})
public abstract class ScenarioMateriauMapper {

  @Autowired
  protected ScenarioRepository scenarioRepository;
  @Autowired
  protected MateriauRepository materiauRepository;
  @Autowired
  protected ZoneRepository zoneRepository;

  @Mapping(target = "idRelScenarioMateriau", source = "id")
  @Mapping(target = "idScenario", source = "scenario.id")
  @Mapping(target = "idMateriau", source = "materiau.idMateriau")
  @Mapping(target = "idZone", source = "zone.id")
  @Mapping(target = "tonnage", source = "tonnage")
  public abstract ScenarioMateriauDTO toDto(ScenarioMateriau scenarioMateriau);

  @Mapping(target = "scenario", expression = "java(this.scenarioRepository.findById(scenarioMateriauDTO.getIdScenario()).orElse(null))")
  @Mapping(target = "materiau", expression = "java(this.materiauRepository.findById(convertLongToInteger(scenarioMateriauDTO.getIdMateriau())).orElse(null))")
  @Mapping(target = "zone", expression = "java(this.zoneRepository.findById(scenarioMateriauDTO.getIdZone()).orElse(null))")
  public abstract ScenarioMateriau toEntity(ScenarioMateriauDTO scenarioMateriauDTO);

  protected Integer convertLongToInteger(Long value) {
    return value != null ? value.intValue() : null;
  }
}
