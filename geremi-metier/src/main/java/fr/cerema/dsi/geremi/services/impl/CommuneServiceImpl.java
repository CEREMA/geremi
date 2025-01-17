package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import fr.cerema.dsi.geremi.entities.Commune;
import fr.cerema.dsi.geremi.repositories.CommuneRepository;
import fr.cerema.dsi.geremi.services.CommuneService;
import fr.cerema.dsi.geremi.services.mapper.SelectionZonageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wololo.geojson.Feature;

@Service
public class CommuneServiceImpl implements CommuneService {

	@Autowired
	private CommuneRepository communeRepository;

  @Autowired
  private SelectionZonageMapper selectionZonageMapper;

	@Override
	public Commune save(Commune entity) {
		return null;
	}

	@Override
	public Commune getOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Commune create(Commune entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Commune findById(String id) {
		return null;
	}

	@Override
	public List<Commune> findAll() {
		return this.communeRepository.findAll();
	}

	@Override
	public void deleteById(String id) {

	}

	@Override
	public List<Feature> findInBox(double bbox0, double bbox1, double bbox2, double bbox3, BigDecimal precision) {
    return this.communeRepository.findInBox(bbox0, bbox1, bbox2, bbox3, precision).stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
	}

  @Override
  public List<Feature> findInListeTerritoire(String territoire, List<Long> liste_id,  BigDecimal precision) {
    List<SelectionZonage> listeSelectionZonage = switch (territoire) {
      case "region" -> this.communeRepository.findCommuneInRegion(liste_id, precision);
      case "departement" -> this.communeRepository.findCommuneInDepartement(liste_id, precision);
      case "zoneemploi" -> this.communeRepository.findCommuneInZoneEmploi(liste_id, precision);
      case "bassinvie" -> this.communeRepository.findCommuneInBassinVie(liste_id, precision);
      case "epci" -> this.communeRepository.findCommuneInEPCI(liste_id, precision);
      case "commune" -> this.communeRepository.findCommuneByIds(liste_id, precision);
      default -> new ArrayList<>();
    };
    return listeSelectionZonage.stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
  }



}
