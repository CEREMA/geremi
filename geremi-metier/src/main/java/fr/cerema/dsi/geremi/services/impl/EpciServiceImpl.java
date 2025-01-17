package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import fr.cerema.dsi.geremi.services.mapper.SelectionZonageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.entities.Epci;
import fr.cerema.dsi.geremi.repositories.EpciRepository;
import fr.cerema.dsi.geremi.services.EpciService;
import fr.cerema.dsi.geremi.services.dto.EpciDTO;
import org.wololo.geojson.Feature;

@Service("epciService")
public class EpciServiceImpl implements EpciService {

	@Autowired
	private EpciRepository epciRepository;

  @Autowired
  private SelectionZonageMapper selectionZonageMapper;

	@Override
	public List<Epci> findAll() {
		return this.epciRepository.findAll();
	}

	@Override
	public Epci getOne(String aLong) {
		return null;
	}

	@Override
	public Epci findById(String aLong) {
		return null;
	}

	@Override
	public Epci create(Epci entity) {
		return null;
	}

	@Override
	public void deleteById(String aLong) {

	}

	@Override
	public Epci save(Epci entity) {
		return null;
	}

  @Override
	public  List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision){
    return this.epciRepository.findInBox(bbox0, bbox1, bbox2, bbox3, precision).stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
	}

  @Override
  public List<Feature> findSelectionTerritoireInterieurExterieur(String territoire, List<Long> liste_id, BigDecimal precision) {
    List<Feature> zonage = new ArrayList<>();
    List<SelectionZonage> listeSelectionZonage = switch (territoire) {
      case "region" -> this.epciRepository.selectionInterieurExterieurEpciInRegion(liste_id, precision);
      case "departement" -> this.epciRepository.selectionInterieurExterieurEpciInDepartement(liste_id, precision);
      case "zoneemploi" -> this.epciRepository.selectionInterieurExterieurEpciInZoneEmploi(liste_id, precision);
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
