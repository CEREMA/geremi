package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.services.ProfilService;
import fr.cerema.dsi.geremi.services.RefEtatService;
import fr.cerema.dsi.geremi.services.RegionService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;

@Mapper(componentModel = "spring")
public abstract class UserMapper {


  @Autowired
  protected RefEtatService refEtatService;

  @Autowired
  protected ProfilService profilService;

  @Autowired
  protected RegionService regionService;

  @Mapping(target = "idEtat", source = "user.refEtat.id")
  @Mapping(target = "libelleEtat", source = "user.refEtat.libelle")
  @Mapping(target = "idProfil", source = "user.profil.id")
  @Mapping(target = "libelleProfil", source = "user.profil.libelle")
  @Mapping(target = "idRegion", source = "user.refRegion.id")
  @Mapping(target = "inseeRegion", source = "user.refRegion.code")
  @Mapping(target = "nomRegion", source = "user.refRegion.nom")
  public abstract UserDTO toDto(User user);


  @Mapping(target = "refEtat", expression = "java(this.refEtatService.findById(userDto.getIdEtat()))")
  @Mapping(target = "profil", expression = "java(this.profilService.findById(userDto.getIdProfil()))")
  @Mapping(target = "refRegion", expression = "java(this.regionService.findById(userDto.getIdRegion()))")
  public abstract User toEntity(UserDTO userDto);

}
