package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import fr.cerema.dsi.geremi.entities.BassinVie;
import fr.cerema.dsi.geremi.repositories.BassinVieRepository;
import fr.cerema.dsi.geremi.services.BassinVieService;
import fr.cerema.dsi.geremi.services.dto.BassinVieDTO;
import fr.cerema.dsi.geremi.services.mapper.SelectionZonageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wololo.geojson.Feature;

@Service("bassinvieService")
public class BassinVieServiceImpl implements BassinVieService {

  @Autowired
  private BassinVieRepository bassinvieRepository;

  @Autowired
  private SelectionZonageMapper selectionZonageMapper;

  @Override
  public List<BassinVie> findAll() {
    return null;
  }

  @Override
  public BassinVie getOne(String aLong) {
    return null;
  }

  @Override
  public BassinVie findById(String aLong) {
    return null;
  }

  @Override
  public BassinVie create(BassinVie entity) {
    return null;
  }

  @Override
  public void deleteById(String aLong) {

  }

  @Override
  public BassinVie save(BassinVie entity) {
    return null;
  }

  @Override
  public  List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision){
    return this.bassinvieRepository.findInBox(bbox0, bbox1, bbox2, bbox3, precision).stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
  }

  @Override
  public List<Feature> findSelectionTerritoireInterieurExterieur(String territoire, List<Long> liste_id, BigDecimal precision) {
    List<Feature> zonage = new ArrayList<>();
    List<SelectionZonage> listeSelectionZonage = switch (territoire) {
      case "region" -> this.bassinvieRepository.selectionInterieurExterieurBassinVieInRegion(liste_id, precision);
      case "departement" -> this.bassinvieRepository.selectionInterieurExterieurBassinVieInDepartement(liste_id, precision);
      case "zoneemploi" -> this.bassinvieRepository.selectionInterieurExterieurBassinVieInZoneEmploi(liste_id, precision);
      default -> new ArrayList<>();
    };

    for (SelectionZonage selectionZonage : listeSelectionZonage) {
      //ajout de la partie intérieure au territoire
      zonage.add(selectionZonageMapper.toFeature(selectionZonage, false));
      if (selectionZonage.getExterieur()) {
        //et de la partie qui dépasse du territoire la cas échéant
        zonage.add(selectionZonageMapper.toFeature(selectionZonage, true));
      }
    }
    return zonage;
  }


}
