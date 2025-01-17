package fr.cerema.dsi.geremi.services.mapper;

import fr.cerema.dsi.geremi.entities.RelScenarioZone;
import fr.cerema.dsi.geremi.services.dto.ZoneProductionDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScenarioZoneMapper {

  @Mapping(target = "idRelScenarioZone", ignore = true)
  @Mapping(target = "idScenario", ignore = true) //renseigné manuellement
  @Mapping(target = "idZone", source = "id")
  @Mapping(target = "projectionScondaireEcheance", source = "pourcentage2")
  @Mapping(target = "dateMaj", expression = "java(java.time.LocalDateTime.now())")
  RelScenarioZone toEntity(ZoneProductionDetailsDTO zoneProductionDetailsDTO);


}
