package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.ScenarioInstallation;
import fr.cerema.dsi.geremi.repositories.InstallationStockageRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.services.dto.ScenarioInstallationDTO;

@Mapper(componentModel = "spring", uses = {})
public abstract class ScenarioInstallationMapper {

    @Autowired
    protected ScenarioRepository scenarioRepository;

    @Autowired
    protected InstallationStockageRepository installationStockageRepository;

    @Mapping(target = "idScenario", source = "scenario.id")
    @Mapping(target = "idInstallation", source = "installation.idStockage")
    public abstract ScenarioInstallationDTO toDto(ScenarioInstallation scenarioInstallation);

    @Mapping(target = "scenario", expression = "java(this.scenarioRepository.findById(scenarioInstallationDTO.getIdScenario()).orElse(null))")
    @Mapping(target = "installation", expression = "java(this.installationStockageRepository.findById(convertLongToInteger(scenarioInstallationDTO.getIdInstallation())).orElse(null))")
    public abstract ScenarioInstallation toEntity(ScenarioInstallationDTO scenarioInstallationDTO);

    protected Integer convertLongToInteger(Long value) {
        return value != null ? value.intValue() : null;
    }
}