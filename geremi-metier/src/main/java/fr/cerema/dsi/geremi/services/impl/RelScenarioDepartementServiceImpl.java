package fr.cerema.dsi.geremi.services.impl;

import fr.cerema.dsi.geremi.entities.RelScenarioDepartement;
import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.repositories.RelScenarioDepartementRepository;
import fr.cerema.dsi.geremi.services.RelScenarioDepartementService;
import fr.cerema.dsi.geremi.services.dto.RelScenarioDepartementDTO;
import fr.cerema.dsi.geremi.services.mapper.RelScenarioDepartementMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("relScenarioDepartementService")
public class RelScenarioDepartementServiceImpl implements RelScenarioDepartementService {

  @Autowired
  private RelScenarioDepartementRepository relScenarioDepartementRepository;
  @Autowired
  private RelScenarioDepartementMapper relScenarioDepartementMapper;

  @Override
  public List<RelScenarioDepartement> findAll() {
    return this.relScenarioDepartementRepository.findAll();
  }

  @Override
  public RelScenarioDepartement getOne(Long aLong) {
    return this.relScenarioDepartementRepository.getOne(aLong);
  }

  @Override
  public RelScenarioDepartement findById(Long aLong) {
    return this.relScenarioDepartementRepository.findById(aLong).orElse(null);
  }

  @Override
  public RelScenarioDepartement create(RelScenarioDepartement entity) {
    return this.relScenarioDepartementRepository.save(entity);
  }

  @Override
  public void deleteById(Long aLong) {
    this.relScenarioDepartementRepository.deleteById(aLong);
  }

  @Override
  public RelScenarioDepartement save(RelScenarioDepartement entity) {
    return this.relScenarioDepartementRepository.save(entity);
  }

  @Override
  public Optional<List<RelScenarioDepartementDTO>> getListeRelScenarioDepartement(Long idScenario) {
    return Optional.of(this.relScenarioDepartementRepository.getListeRelScenarioDepartement(idScenario)
      .stream()
      .map(this.relScenarioDepartementMapper::toDto).toList());
  }

  @Override
  public void deleteByIdScenario(Long idScenario) {
    this.relScenarioDepartementRepository.deleteByIdScenario(idScenario);
  }

  @Override
  @Transactional
  public List<RelScenarioDepartementDTO> saveAll(Long idScenario, List<RelScenarioDepartementDTO> relScenarioDepartementDTOs) {
    this.deleteByIdScenario(idScenario);

    return this.relScenarioDepartementRepository.saveAll(relScenarioDepartementDTOs.stream()
      .map(this.relScenarioDepartementMapper::toEntity)
        .map(r -> {
          Scenario scenario = new Scenario();
          scenario.setId(idScenario);
          r.setScenario(scenario);
          return r;
        })
      .toList())
      .stream()
      .map(this.relScenarioDepartementMapper::toDto).toList();
  }
}
