package fr.cerema.dsi.geremi.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.entities.Zone;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.repositories.ZoneRepository;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.ZoneService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;
import fr.cerema.dsi.geremi.services.dto.ZoneDTO;

@Service("zoneService")
public class ZoneServiceImpl implements ZoneService {

  private final ZoneRepository zoneRepository;
  private final UserService userService;
  private final SecurityService securityService;

  public ZoneServiceImpl (ZoneRepository zoneRepository, UserService userService, SecurityService securityService){
    this.zoneRepository = zoneRepository;
    this.userService = userService;
    this.securityService = securityService;
  }

  @Override
  public List<Zone> findAll() {
    return this.zoneRepository.findAll();
  }

  @Override
  public Zone getOne(Long id) {
    return this.zoneRepository.getOne(id);
  }

  @Override
  public Zone findById(Long id) {
    return this.zoneRepository.findById(id).orElse(null);
  }

  @Override
  public Zone create(Zone entity) {
    return null;
  }

  @Override
  public void deleteById(Long id) {
    this.zoneRepository.deleteById(id);
  }

  @Override
  public Zone save(Zone entity) {
    securityService.checkModificationEtude(Optional.of(entity.getEtude()), null, Etape.ZONAGE);
    return zoneRepository.save(entity);
  }

  @Override
  public Zone findZoneByCode(String code, Long id) {
    Zone zone = zoneRepository.findZoneByCode(code, id);
    securityService.checkConsultationEtude(Optional.of(zone.getEtude()));
    return zone;
  }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    securityService.checkModificationEtude(idEtude, null, Etape.ZONAGE);
    this.zoneRepository.deleteAllFromEtude(idEtude);
  }

  @Override
  public List<ZoneDTO> findZoneByEtudeInterieur(Long id, double precision) {
    securityService.checkConsultationEtude(id);
    return this.zoneRepository.findByEtude(id,precision).stream()
      .map(t -> {
        ZoneDTO zoneDTO = new ZoneDTO();
        zoneDTO.setIdZone(t.getId());
        zoneDTO.setCode(t.getCode());
        zoneDTO.setNom(t.getNom());
        zoneDTO.setType(t.getType());
        zoneDTO.setDescription(t.getDescription());
        zoneDTO.setGeometry(t.getTheGeom());
        zoneDTO.setExterieur(false);
        return zoneDTO;
      }).collect(Collectors.toList());
  }

  @Override
  public List<ZoneDTO> findZoneByEtudeExterieur(Long id, double precision) {
    securityService.checkConsultationEtude(id);
    Optional<UserDTO> userDTO = this.userService.getCurrentUser();
    if(userDTO.isPresent()){
      if ("DREAL".equals(userDTO.get().getLibelleProfil())) {
        return this.zoneRepository.findZoneByEtudeExterieurRegion(id, userDTO.get().getIdRegion(), precision).stream()
          .map(z -> {
            ZoneDTO zoneDTO = new ZoneDTO();
            zoneDTO.setIdZone(z.getId());
            zoneDTO.setCode(z.getCode());
            zoneDTO.setNom(z.getNom());
            zoneDTO.setType(z.getType());
            zoneDTO.setDescription(z.getDescription());
            zoneDTO.setGeometry(z.getTheGeom());
            zoneDTO.setExterieur(true);
            return zoneDTO;
          }).collect(Collectors.toList());
      }
    }

    return this.zoneRepository.findZoneByEtudeExterieurFrance(id, precision).stream().map(z -> {
      ZoneDTO zoneDTO = new ZoneDTO();
      zoneDTO.setIdZone(z.getId());
      zoneDTO.setCode(z.getCode());
      zoneDTO.setNom(z.getNom());
      zoneDTO.setType(z.getType());
      zoneDTO.setDescription(z.getDescription());
      zoneDTO.setGeometry(z.getTheGeom());
      zoneDTO.setExterieur(true);
      return zoneDTO;
    }).collect(Collectors.toList());
  }

  @Override
  public List<Zone> findZonesByCode(Long id) {
    securityService.checkConsultationEtude(id);
    return zoneRepository.findZonesByCode(id);
  }

  @Override
  public List<String> getZoneNamesByIds(List<Long> ids) {
    return zoneRepository.getZoneNamesByIds(ids);
  }

  @Override
  public Map<Long, String> getZoneNamesEtude(Long id) {
    HashMap<Long, String> map = new HashMap<>();
    this.zoneRepository.findZoneByEtude(id).forEach(
      zone -> map.put(zone.getId(), zone.getNom())
    );
    return map;
  }
}
