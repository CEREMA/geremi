package fr.cerema.dsi.geremi.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.RelEtudeUserProcuration;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.repositories.RelEtudeUserProcurationRepository;
import fr.cerema.dsi.geremi.services.RelEtudeUserProcurationService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.dto.RelEtudeUserProcurationDTO;
import fr.cerema.dsi.geremi.services.mapper.RelEtudeUserProcurationMapper;

@Service("relEtudeUserProcurationService")
public class RelEtudeUserProcurationServiceImpl extends GenericServiceImpl<RelEtudeUserProcuration, Long> implements RelEtudeUserProcurationService {

  private final RelEtudeUserProcurationRepository relEtudeUserProcurationRepository;
  private final RelEtudeUserProcurationMapper relEtudeUserProcurationMapper;

  private final SecurityService securityService;

  public RelEtudeUserProcurationServiceImpl(RelEtudeUserProcurationRepository relEtudeUserProcurationRepository, RelEtudeUserProcurationMapper relEtudeUserProcurationMapper, SecurityService securityService) {
    this.relEtudeUserProcurationRepository = relEtudeUserProcurationRepository;
    this.relEtudeUserProcurationMapper = relEtudeUserProcurationMapper;
    this.securityService = securityService;
  }

  @Override
  public RelEtudeUserProcuration create(RelEtudeUserProcuration entity) {
    securityService.checkModificationEtude(Optional.of(entity.getEtude()), null, Etape.CREATION);
    return this.relEtudeUserProcurationRepository.save(entity);
  }

  @Override
  public Optional<RelEtudeUserProcurationDTO> getRelEtudeUserProcurationById(Long id) {
    return Optional.ofNullable(this.findById(id)).map(relEtudeUserProcurationMapper::toDto);
  }

  @Override
  public List<RelEtudeUserProcuration> findAll() {
    throw new NotImplementedException();
  }

  @Override
  public RelEtudeUserProcuration findById(Long id) {
    Optional<RelEtudeUserProcuration> relEtudeUserProcuration = this.relEtudeUserProcurationRepository.findById(id);
    securityService.checkConsultationEtude(relEtudeUserProcuration.map(RelEtudeUserProcuration::getEtude));
    return relEtudeUserProcuration.orElse(null);
  }

  @Override
  public void deleteById(Long id) {
    Optional<RelEtudeUserProcuration> relEtudeUserProcuration = this.relEtudeUserProcurationRepository.findById(id);
    securityService.checkConsultationEtude(relEtudeUserProcuration.map(RelEtudeUserProcuration::getEtude));
    relEtudeUserProcuration.ifPresent(relEtudeUserProcurationRepository::delete);
  }

  @Override
  public RelEtudeUserProcurationDTO addRelEtudeUserProcuration(RelEtudeUserProcurationDTO relEtudeUserProcurationDTO) {

    RelEtudeUserProcuration relEtudeUserProcuration = this.relEtudeUserProcurationMapper.toEntity(relEtudeUserProcurationDTO);
    securityService.checkModificationEtude(Optional.of(relEtudeUserProcuration.getEtude()), null, Etape.CREATION);
    this.relEtudeUserProcurationRepository.save(relEtudeUserProcuration);
    return this.relEtudeUserProcurationMapper.toDto(relEtudeUserProcuration);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public List<RelEtudeUserProcurationDTO> updateRelByIdEtude(Long idEtude,List<RelEtudeUserProcurationDTO> relEtudeUserProcurationDTO) {

    List<RelEtudeUserProcuration> relEtudeUserProcurations = relEtudeUserProcurationDTO.stream()
      .map(this.relEtudeUserProcurationMapper::toEntity)
      .collect(Collectors.toList());

    securityService.checkModificationEtude(idEtude, null, Etape.CREATION);
    List<RelEtudeUserProcuration> relations = new ArrayList<>();
    List<RelEtudeUserProcuration> oldRelations = this.relEtudeUserProcurationRepository.findAllByIdEtude(idEtude);
    for(RelEtudeUserProcuration rel1 : oldRelations){
      this.relEtudeUserProcurationRepository.deleteById(rel1.getId());
    }
    for(RelEtudeUserProcuration rel2 : relEtudeUserProcurations){
      relations.add(this.relEtudeUserProcurationRepository.save(rel2));
    }
    return relations.stream()
      .map(this.relEtudeUserProcurationMapper::toDto)
      .collect(Collectors.toList());
  }
}
