package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.ScenarioChantier;
import fr.cerema.dsi.geremi.repositories.ChantierEnvergureRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.services.dto.ScenarioChantierDTO;

@Mapper(componentModel = "spring", uses = {})
public abstract class ScenarioChantierMapper {

    @Autowired
    protected ScenarioRepository scenarioRepository;

    @Autowired
    protected ChantierEnvergureRepository chantierEnvergureRepository;

    @Mapping(target = "idScenario", source = "scenario.id")
    @Mapping(target = "idChantier", source = "chantier.idChantier")
    public abstract ScenarioChantierDTO toDto(ScenarioChantier scenarioChantier);

    @Mapping(target = "scenario", expression = "java(this.scenarioRepository.findById(scenarioChantierDTO.getIdScenario()).orElse(null))")
    @Mapping(target = "chantier", expression = "java(this.chantierEnvergureRepository.findById(convertLongToInteger(scenarioChantierDTO.getIdChantier())).orElse(null))")
    public abstract ScenarioChantier toEntity(ScenarioChantierDTO scenarioChantierDTO);

    protected Integer convertLongToInteger(Long value) {
        return value != null ? value.intValue() : null;
    }
}
