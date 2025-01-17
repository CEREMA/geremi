package fr.cerema.dsi.geremi.services.mapper;

import fr.cerema.dsi.geremi.entities.ScenarioContrainte;
import fr.cerema.dsi.geremi.repositories.ContrainteEnvironnementaleRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.services.dto.ScenarioContrainteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {})
public abstract class ScenarioContrainteMapper {

  @Autowired
  protected ScenarioRepository scenarioRepository;
  @Autowired
  protected ContrainteEnvironnementaleRepository contrainteEnvironnementaleRepository;

  @Mapping(target = "idScenario", source = "scenario.id")
  @Mapping(target = "idContrainte", source = "contrainte.idContrEnv")
  public abstract ScenarioContrainteDTO toDto(ScenarioContrainte scenarioContrainte);

  @Mapping(target = "scenario", expression = "java(this.scenarioRepository.findById(scenarioContrainteDTO.getIdScenario()).orElse(null))")
  @Mapping(target = "contrainte", expression = "java(this.contrainteEnvironnementaleRepository.findById(convertLongToInteger(scenarioContrainteDTO.getIdContrainte())).orElse(null))")
  public abstract ScenarioContrainte toEntity(ScenarioContrainteDTO scenarioContrainteDTO);

  protected Integer convertLongToInteger(Long value) {
    return value != null ? value.intValue() : null;
  }
}
