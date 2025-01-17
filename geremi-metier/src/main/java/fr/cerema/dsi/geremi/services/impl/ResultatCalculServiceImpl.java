package fr.cerema.dsi.geremi.services.impl;

import java.util.List;

import fr.cerema.dsi.geremi.entities.ResultatCalcul;
import fr.cerema.dsi.geremi.repositories.ResultatCalculRepository;
import fr.cerema.dsi.geremi.services.ResultatCalculService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service("resultatCalculService")
public class ResultatCalculServiceImpl implements ResultatCalculService {

  @Autowired
  private ResultatCalculRepository resultatCalculRepository;

  @Override
  public List<ResultatCalcul> findAll() {
    return this.resultatCalculRepository.findAll();
  }

  @Override
  public ResultatCalcul getOne(Long aLong) {
    return this.resultatCalculRepository.getOne(aLong);
  }

  @Override
  public ResultatCalcul findById(Long aLong) {
    return this.resultatCalculRepository.findById(aLong).orElse(null);
  }

  @Override
  public ResultatCalcul create(ResultatCalcul entity) {
    return this.resultatCalculRepository.save(entity);
  }

  @Override
  @Modifying(clearAutomatically = true)
  public void deleteById(Long aLong) {
    this.resultatCalculRepository.deleteById(aLong);
  }

  @Override
  public ResultatCalcul save(ResultatCalcul entity) {
    return this.resultatCalculRepository.save(entity);
  }

  @Override
  public List<ResultatCalcul> findByIdScenario(Long idScenario) {
    return this.resultatCalculRepository.findByIdScenario(idScenario);
  }

  @Override
  public List<ResultatCalcul> findByIdScenarioForCalcul(Long idScenario) {
    return this.resultatCalculRepository.findByIdScenarioForCalcul(idScenario);
  }

  public void delete(ResultatCalcul entity) {
    this.resultatCalculRepository.delete(entity);
  }

  public void delete(List<ResultatCalcul> entities) {
    this.resultatCalculRepository.deleteInBatch(entities);
  }
}
