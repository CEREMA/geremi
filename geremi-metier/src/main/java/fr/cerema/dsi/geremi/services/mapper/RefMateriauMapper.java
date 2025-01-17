package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.cerema.dsi.geremi.entities.RefMateriau;
import fr.cerema.dsi.geremi.services.dto.MateriauDTO;

@Mapper(componentModel = "spring", uses = {})
public abstract class RefMateriauMapper {
  @Mapping(target = "libelle", source = "libelle")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "origine", source = "origine")
  public abstract MateriauDTO toDTO(RefMateriau refmateriau);
}
