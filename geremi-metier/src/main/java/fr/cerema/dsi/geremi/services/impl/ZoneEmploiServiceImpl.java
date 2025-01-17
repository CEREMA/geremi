package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import fr.cerema.dsi.geremi.services.mapper.SelectionZonageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.entities.ZoneEmploi;
import fr.cerema.dsi.geremi.repositories.ZoneEmploiRepository;
import fr.cerema.dsi.geremi.services.ZoneEmploiService;
import fr.cerema.dsi.geremi.services.dto.ZoneEmploiDTO;
import org.wololo.geojson.Feature;

@Service("zoneEmploiService")
public class ZoneEmploiServiceImpl implements ZoneEmploiService {

  @Autowired
  private ZoneEmploiRepository zoneEmploiRepository;

  @Autowired
  SelectionZonageMapper selectionZonageMapper;

  @Override
  public List<ZoneEmploi> findAll() {
    return this.zoneEmploiRepository.findAll();
  }

  @Override
  public ZoneEmploi getOne(String aLong) {
    return null;
  }

  @Override
  public ZoneEmploi findById(String aLong) {
    return null;
  }

  @Override
  public ZoneEmploi create(ZoneEmploi entity) {
    return null;
  }

  @Override
  public void deleteById(String aLong) {

  }

  @Override
  public ZoneEmploi save(ZoneEmploi entity) {
    return null;
  }

  @Override
  public  List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision){
    return this.zoneEmploiRepository.findInBox(bbox0, bbox1, bbox2, bbox3, precision).stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
  }

  public List<Feature> findSelectionTerritoireInterieurExterieur(String territoire, List<Long> liste_id, BigDecimal precision) {
    List<Feature> zonage = new ArrayList<>();
    List<SelectionZonage> listeSelectionZonage = switch (territoire) {
      case "region" -> this.zoneEmploiRepository.selectionInterieurExterieurZoneEmploiInRegion(liste_id, precision);
      case "departement" -> this.zoneEmploiRepository.selectionInterieurExterieurZoneEmploiInDepartement(liste_id, precision);
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
