package fr.cerema.dsi.geremi.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.cerema.dsi.geremi.entities.RelScenarioZone;
import fr.cerema.dsi.geremi.repositories.RelScenarioZoneRepository;
import fr.cerema.dsi.geremi.services.RelScenarioZoneService;
import fr.cerema.dsi.geremi.services.dto.ZoneProductionDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("relScenarioZoneService")
public class RelScenarioZoneServiceImpl implements RelScenarioZoneService {

  @Autowired
  private RelScenarioZoneRepository relScenarioZoneRepository;

  @Override
  public List<RelScenarioZone> findAll() {
    return this.relScenarioZoneRepository.findAll();
  }

  @Override
  public RelScenarioZone getOne(Long aLong) {
    return this.relScenarioZoneRepository.getOne(aLong);
  }

  @Override
  public RelScenarioZone findById(Long aLong) {
    return this.relScenarioZoneRepository.findById(aLong).orElse(null);
  }

  @Override
  public RelScenarioZone create(RelScenarioZone entity) {
    return this.relScenarioZoneRepository.save(entity);
  }

  @Override
  public void deleteById(Long aLong) {
    this.relScenarioZoneRepository.deleteById(aLong);
  }

  @Override
  public RelScenarioZone save(RelScenarioZone entity) {
    return this.relScenarioZoneRepository.save(entity);
  }

  @Override
  public void deleteByIdScenario(Long idScenario) {
    this.relScenarioZoneRepository.deleteByIdScenario(idScenario);
  }

  @Override
  public Optional<List<ZoneProductionDetailsDTO>> findByIdScenario(Long idScenario) {
    List<RelScenarioZone> relScenarioZones = this.relScenarioZoneRepository.findByIdScenario(idScenario);
    if (relScenarioZones != null && !relScenarioZones.isEmpty()) {
      List<ZoneProductionDetailsDTO> res = new ArrayList<>();
      for (RelScenarioZone zone : relScenarioZones) {
        ZoneProductionDetailsDTO zoneProductionDetailsDTO = new ZoneProductionDetailsDTO();
        zoneProductionDetailsDTO.setId(zone.getIdZone());
        zoneProductionDetailsDTO.setPourcentage2(zone.getProjectionScondaireEcheance());
        res.add(zoneProductionDetailsDTO);
      }
      return Optional.of(res);
    }
    return Optional.empty();

  }
}

