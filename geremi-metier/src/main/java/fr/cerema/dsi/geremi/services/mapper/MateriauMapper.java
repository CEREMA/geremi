package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.Materiau;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.dto.MateriauDTO;

@Mapper(componentModel = "spring", uses = {})
public abstract class MateriauMapper {

  @Autowired
  protected EtudeService etudeService;

  @Mapping(target = "idMateriau", source = "idMateriau")
  @Mapping(target = "libelle", source = "libelle")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "origine", source = "origine")
  @Mapping(target = "tonnage", source = "tonnage")
  @Mapping(target = "etude", expression = "java(this.etudeService.findById(materiauDTO.getIdEtude()))")
  public abstract Materiau toEntity(MateriauDTO materiauDTO);

  @Mapping(target = "idMateriau", source = "idMateriau")
  @Mapping(target = "libelle", source = "libelle")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "origine", source = "origine")
  @Mapping(target = "tonnage", source = "tonnage")
  @Mapping(target = "idEtude", source = "etude.id")
  public abstract MateriauDTO toDTO(Materiau materiau);
}
