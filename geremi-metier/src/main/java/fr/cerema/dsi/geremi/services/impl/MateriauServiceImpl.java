package fr.cerema.dsi.geremi.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Materiau;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.MateriauRepository;
import fr.cerema.dsi.geremi.repositories.RefMateriauRepository;
import fr.cerema.dsi.geremi.services.MateriauService;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.dto.MateriauDTO;
import fr.cerema.dsi.geremi.services.mapper.MateriauMapper;
import fr.cerema.dsi.geremi.services.mapper.RefMateriauMapper;

@Service("materiauService")
public class MateriauServiceImpl extends GenericServiceImpl<Materiau,Integer> implements MateriauService {

  private MateriauRepository materiauRepository;
  private RefMateriauRepository refMateriauRepository;
  private MateriauMapper materiauMapper;
  private RefMateriauMapper refMateriauMapper;
  private TracabiliteEtapeService tracabiliteEtapeService;
  private final SecurityService securityService;

  public MateriauServiceImpl(MateriauRepository materiauRepository, RefMateriauRepository refMateriauRepository, MateriauMapper materiauMapper, RefMateriauMapper refMateriauMapper, TracabiliteEtapeService tracabiliteEtapeService, SecurityService securityService) {
    this.materiauRepository = materiauRepository;
    this.refMateriauRepository = refMateriauRepository;
    this.materiauMapper = materiauMapper;
    this.refMateriauMapper = refMateriauMapper;
    this.tracabiliteEtapeService = tracabiliteEtapeService;
    this.securityService = securityService;
  }

  @Override
  public Optional<List<MateriauDTO>> getMateriauxDisponibles() {
    return Optional.of(this.refMateriauRepository.getMateriauxDisponibles()
      .stream()
      .map(this.refMateriauMapper::toDTO)
      .toList());
  }

  @Override
  public Optional<List<MateriauDTO>> getMateriauxEtude(Long idEtude) {
    securityService.checkConsultationEtude(idEtude);
    return Optional.of(this.materiauRepository.getMateriauxEtude(idEtude)
      .stream()
      .map(this.materiauMapper::toDTO)
      .toList());
  }

  @Override
  public Integer supprimerMateriaux(Integer idMateriau) {
    Materiau mat = this.materiauRepository.getOne(idMateriau);
    securityService.checkModificationEtude(Optional.of(mat.getEtude()), null, Etape.MATERIAUX);
    tracabiliteEtapeService.addTracabiliteEtape(mat.getEtude().getId(), null, Etape.MATERIAUX, EtatEtape.MODIFIE);
    this.materiauRepository.delete(mat);
    return idMateriau;
  }

  @Override
  public MateriauDTO ajoutMateriauEtude(MateriauDTO materiauDTO){
    securityService.checkModificationEtude(materiauDTO.getIdEtude(), null, Etape.MATERIAUX);
    tracabiliteEtapeService.addTracabiliteEtape(materiauDTO.getIdEtude(), null, Etape.MATERIAUX, EtatEtape.MODIFIE);
    return this.materiauMapper.toDTO(
      this.materiauRepository.save(
        this.materiauMapper.toEntity(materiauDTO)));
  }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.materiauRepository.deleteByIdEtude(idEtude);
  }
}
