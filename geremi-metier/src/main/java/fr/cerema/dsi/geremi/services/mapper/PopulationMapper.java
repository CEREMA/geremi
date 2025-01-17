package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.Population;
import fr.cerema.dsi.geremi.repositories.EtudeRepository;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.dto.PopulationDTO;

@Mapper(componentModel = "spring", uses = {})
public abstract class PopulationMapper {

    @Autowired
    protected EtudeRepository etudeRepository;

    @Autowired
    protected ZoneRepository zoneRepository;

    @Mapping(target = "idEtude", source = "etude.id")
    @Mapping(target = "idZone", source = "zone.id")
    @Mapping(target = "codeZone", source = "zone.code")
    @Mapping(target = "nomZone", source = "zone.nom")
    public abstract PopulationDTO toDto(Population population);

    @Mapping(target = "etude", expression = "java(this.etudeRepository.findById(populationDTO.getIdEtude()).orElse(null))")
    @Mapping(target = "zone", expression = "java(this.zoneRepository.findZoneByCode(populationDTO.getCodeZone(), populationDTO.getIdEtude() ))")
    public abstract Population toEntity(PopulationDTO populationDTO);
}
