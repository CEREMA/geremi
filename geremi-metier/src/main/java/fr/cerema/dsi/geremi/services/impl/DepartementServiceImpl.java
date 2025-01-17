package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.dto.RepartitionDepartementPartielEtude;
import fr.cerema.dsi.geremi.entities.Departement;
import fr.cerema.dsi.geremi.repositories.CalculsProductionDepartementRepository;
import fr.cerema.dsi.geremi.repositories.DepartementRepository;
import fr.cerema.dsi.geremi.services.DepartementService;
import fr.cerema.dsi.geremi.services.dto.DepartementDTO;
import fr.cerema.dsi.geremi.services.dto.RelScenarioDepartementDTO;
import fr.cerema.dsi.geremi.services.mapper.SelectionZonageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wololo.geojson.Feature;

@Service("departementService")
public class DepartementServiceImpl implements DepartementService {

  @Autowired
  private DepartementRepository departementRepository;

  @Autowired
  private CalculsProductionDepartementRepository calculsProductionDepartementRepository;

  @Autowired
  private SelectionZonageMapper selectionZonageMapper;

  @Override
  public List<Departement> findAll() {
    return this.departementRepository.findAll();
  }

  @Override
  public Departement getOne(Long aLong) {
    return this.departementRepository.getOne(aLong);
  }

  @Override
  public Departement findById(Long aLong) {
    return this.departementRepository.findById(aLong).orElse(null);
  }

  @Override
  public Departement create(Departement entity) {
    return null;
  }

  @Override
  public void deleteById(Long aLong) {

  }

  @Override
  public Departement save(Departement entity) {
    return null;
  }

  @Override
  public  List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision){
    return this.departementRepository.findInBox(bbox0, bbox1, bbox2, bbox3, precision).stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
	}

  public List<Feature> findInListeRegion(List<Long> liste_id, BigDecimal precision){
		return this.departementRepository.findInListeRegions(liste_id, precision).stream()
              .map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
	}

  @Override
  public List<RelScenarioDepartementDTO> findDepartementPartielInEtude(Long idEtude) {
    List<RelScenarioDepartementDTO> defaultRepartitionDepartement = new ArrayList<>();
    List<RepartitionDepartementPartielEtude> repartitionDepartementPartielEtude = this.calculsProductionDepartementRepository.getRepartionDepartementPartielEtude(idEtude);
    for (RepartitionDepartementPartielEtude rep : repartitionDepartementPartielEtude) {
      RelScenarioDepartementDTO rel = new RelScenarioDepartementDTO();
      rel.setIdDepartement(rep.getIdDepartement());
      rel.setNom(rep.getNom());
      rel.setRepartitionDepartementBeton(rep.getRepartitionDepartement().setScale(0, RoundingMode.HALF_UP).intValue());
      rel.setRepartitionDepartementViabilite(rep.getRepartitionDepartement().setScale(0, RoundingMode.HALF_UP).intValue());
      defaultRepartitionDepartement.add(rel);
    }
   return defaultRepartitionDepartement;
  }
}
