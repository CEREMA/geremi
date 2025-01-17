package fr.cerema.dsi.geremi.services.impl;

import java.util.List;

import fr.cerema.dsi.geremi.entities.RelResultatZone;
import fr.cerema.dsi.geremi.repositories.RelResultatZoneRepository;
import fr.cerema.dsi.geremi.services.RelResultatZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("relResultatZoneService")
public class RelResultatZoneServiceImpl implements RelResultatZoneService {

  @Autowired
  private RelResultatZoneRepository relResultatZoneRepository;

  @Override
  public List<RelResultatZone> findAll() {
    return this.relResultatZoneRepository.findAll();
  }

  @Override
  public RelResultatZone getOne(Long aLong) {
    return this.relResultatZoneRepository.getOne(aLong);
  }

  @Override
  public RelResultatZone findById(Long aLong) {
    return this.relResultatZoneRepository.findById(aLong).orElse(null);
  }

  @Override
  public RelResultatZone create(RelResultatZone entity) {
    return this.relResultatZoneRepository.save(entity);
  }

  @Override
  public void deleteById(Long aLong) {
    this.relResultatZoneRepository.deleteById(aLong);
  }

  @Override
  public RelResultatZone save(RelResultatZone entity) {
    return this.relResultatZoneRepository.save(entity);
  }

  @Override
  public List<RelResultatZone> findByIdResultat(Long idResultat) {
    return this.relResultatZoneRepository.findByIdResultat(idResultat);
  }


  public void delete(RelResultatZone entity) {
    this.relResultatZoneRepository.delete(entity);
  }

  public void delete(List<RelResultatZone> entities) {
    this.relResultatZoneRepository.deleteInBatch(entities);
  }



}
