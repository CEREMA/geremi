package fr.cerema.dsi.geremi.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.cerema.dsi.geremi.entities.RelEtudeUserProcuration;
import fr.cerema.dsi.geremi.services.EtudeService;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.RelEtudeUserProcurationDTO;

@Mapper(componentModel = "spring", uses = {})
public abstract class RelEtudeUserProcurationMapper {
	@Autowired
	protected EtudeService etudeService;
	@Autowired
	protected EtudeMapper etudeMapper;
	@Autowired
	protected UserService userService;
	@Autowired
	protected UserMapper userMapper;

	@Mapping(target = "etudeDTO", expression = "java(this.etudeMapper.toDto(this.etudeService.findById( relEtudeUserProcuration.getEtude().getId())))")
	@Mapping(target = "userDTO", expression = "java(this.userMapper.toDto(this.userService.findById(relEtudeUserProcuration.getUser().getId())))")
	public abstract RelEtudeUserProcurationDTO toDto(RelEtudeUserProcuration relEtudeUserProcuration);

	@Mapping(target = "etude", expression = "java(this.etudeService.findById(relEtudeUserProcurationDTO.getEtudeDTO().getId()))")
	@Mapping(target = "user", expression = "java(this.userService.findById(relEtudeUserProcurationDTO.getUserDTO().getId()))")
	public abstract RelEtudeUserProcuration toEntity(RelEtudeUserProcurationDTO relEtudeUserProcurationDTO);
}
