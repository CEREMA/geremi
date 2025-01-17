package fr.cerema.dsi.geremi.services.impl;

import java.time.LocalDate;
import java.util.*;

import fr.cerema.dsi.geremi.entities.Region;
import fr.cerema.dsi.geremi.repositories.*;
import fr.cerema.dsi.geremi.services.*;
import fr.cerema.dsi.geremi.services.dto.RegionDTO;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.services.dto.EtudeDTO;
import fr.cerema.dsi.geremi.services.dto.EtudesUtilisateurDTO;
import fr.cerema.dsi.geremi.services.mapper.EtudeMapper;
import fr.cerema.dsi.geremi.services.mapper.TerritoireMapper;

import static fr.cerema.dsi.geremi.services.impl.SecurityServiceImpl.*;

@Service("etudeService")
public class EtudeServiceImpl extends GenericServiceImpl<Etude, Long> implements EtudeService {

	private final EtudeRepository etudeRepository;
  private final UserService userService;
  private final EtudeMapper etudeMapper;
  private final TerritoireService territoireService;
  private final TerritoireMapper territoireMapper;
  private final PopulationService populationService;
  private final TracabiliteEtapeService tracabiliteEtapeService;
  private final SecurityService securityService;
  private final ZoneService zoneService;
  private final ContrainteEnvironnementaleRepository contrainteRepository;
  private final ChantierEnvergureRepository chantierRepository;
  private final InstallationStockageRepository installationStockageRepository;
  private final MateriauRepository materiauRepository;
  private final ScenarioService scenarioService;
  private final RelEtudeUserProcurationRepository relEtudeUserProcurationRepository;

	public EtudeServiceImpl(EtudeRepository etudeRepository, UserService userService, EtudeMapper etudeMapper, TerritoireService territoireService, TerritoireMapper territoireMapper, PopulationService populationService, TracabiliteEtapeService tracabiliteEtapeService, SecurityService securityService, ZoneServiceImpl zoneService, ContrainteEnvironnementaleRepository contrainteRepository, ChantierEnvergureRepository chantierRepository, InstallationStockageRepository installationStockageRepository, MateriauRepository materiauRepository, ScenarioServiceImpl scenarioService, RelEtudeUserProcurationRepository relEtudeUserProcurationRepository) {
		this.etudeRepository = etudeRepository;
		this.userService = userService;
		this.etudeMapper = etudeMapper;
		this.territoireService = territoireService;
		this.territoireMapper = territoireMapper;
		this.populationService = populationService;
		this.tracabiliteEtapeService = tracabiliteEtapeService;
		this.securityService = securityService;
    this.zoneService = zoneService;
    this.contrainteRepository = contrainteRepository;
    this.chantierRepository = chantierRepository;
    this.installationStockageRepository = installationStockageRepository;
    this.materiauRepository = materiauRepository;
    this.scenarioService = scenarioService;
    this.relEtudeUserProcurationRepository = relEtudeUserProcurationRepository;
  }

	@Override
	public Etude create(Etude entity) {
    securityService.checkCreationEtude();
    return this.etudeRepository.save(entity);
	}

	@Override
	public List<Etude> findAll() {
		throw new NotImplementedException();
	}

  @Override
  public EtudesUtilisateurDTO findEtudeUtilisateur() {
    Optional<User> user = userService.getCurrentUserEntity();
    if (user.isPresent()) {
      List<Etude> etudes = this.etudeRepository.findEtudeUtilisateur(user.get().getId());
      return categoriserEtude(etudes, user.get());
    }
    return null;
  }

  @Override
  public EtudesUtilisateurDTO findEtudeSuiviUtilisateur() {
    Optional<User> user = userService.getCurrentUserEntity();
    if (user.isPresent()) {
      List<Etude> etudes = this.etudeRepository.findEtudeSuiviUtilisateur(user.get().getId());
      return categoriserEtude(etudes, user.get());
    }
    return null;
  }

  @Override
  public Map<RegionDTO,List<EtudeDTO>> findEtudePublicInRegion(){
    HashMap<RegionDTO, List<EtudeDTO>> mapRegionEtude = new HashMap<>();
    Optional<User> user = userService.getCurrentUserEntity();
    if (user.isPresent()) {
      List<EtudeDTO> etudes = this.etudeRepository.findEtudePublic(user.get().getId()).stream().map(this.etudeMapper::toDto).toList();
      etudes.forEach(e -> {

        RegionDTO r = new RegionDTO();
        r.setId(e.getProprietaire().getIdRegion());
        r.setNom(e.getProprietaire().getNomRegion());
        r.setCode(e.getProprietaire().getInseeRegion());

        if(Objects.isNull(mapRegionEtude.get(r))){
          List<EtudeDTO> tmp = new ArrayList<>();
          tmp.add(e);
          mapRegionEtude.put(r,tmp);
        } else {
          List<EtudeDTO> tmplist = mapRegionEtude.get(r);
          tmplist.add(e);
          mapRegionEtude.put(r,tmplist);
        }
      });
    }
    return mapRegionEtude;
  }

  @Override
  public Optional<EtudeDTO> getEtudeById(Long id) {
    // Le security check est fait dans le findById
    Etude etude = findById(id);
    if (etude != null) {
      EtudeDTO etudeDTO = this.etudeMapper.toDto(etude);

      Territoire territoire = this.territoireService.findTerritoireByEtude(etude);
      if (territoire != null) {
        etudeDTO.setTerritoire(this.territoireMapper.toDto(territoire));
        Optional<String> typeZonage = this.territoireService.findTypeZonageByIdEtude(etude);
        etudeDTO.getTerritoire().setTypeZonage(typeZonage.orElse(null));
        etudeDTO.setPopulations(populationService.findByEtude(etude));
      }

      etudeDTO.setEtatEtapes(this.tracabiliteEtapeService.getEtatEtapeEtude(id, null));

      return Optional.of(etudeDTO);
    }
    return Optional.empty();
  }

	@Override
	public Etude findById(Long id) {

		Optional<Etude> e = this.etudeRepository.findById(id);
    //vérification des droits d'accès en lecture
    securityService.checkConsultationEtude(e);
    return e.get();
	}

	@Override
	public void deleteById(Long id) {
    securityService.checkSupressionEtude(id);
    Etude etude = this.etudeRepository.getOne(id);

    // Délégation
    etude.getRelEtudeUserProcuration().forEach(relEtudeUserProcurationRepository::delete);
    // Scenario
    this.scenarioService.deleteByIdEtude(id);
    // Population
    this.populationService.deleteByIdEtude(id);
    // Zone
    this.zoneService.deleteByIdEtude(id);
    // Territoire
    Territoire territoire = this.territoireService.findTerritoireByEtude(etude);
    if(Objects.nonNull(territoire)){
      this.territoireService.deleteById(territoire.getId());
    }
    // Contrainte
    this.contrainteRepository.deleteByIdEtude(id);
    // Chantier
    this.chantierRepository.deleteByIdEtude(id);
    // Installation
    this.installationStockageRepository.deleteByIdEtude(id);
    // Materiaux
    this.materiauRepository.deleteByIdEtude(id);
    // Tracabilité
    this.tracabiliteEtapeService.deleteByIdEtude(id);

		this.etudeRepository.deleteById(id);
	}

	@Override
	public Etude save(Etude entity) {
		if (entity != null) {
      if (entity.getId() != null) {
        securityService.checkModificationEtude(Optional.of(entity), null, Etape.CREATION);
      } else {
        securityService.checkCreationEtude();
      }
      return etudeRepository.save(entity);
    } else {
      return entity;
    }

	}

  @Override
  public EtudeDTO save(EtudeDTO etudeDTO) {
    Etude etude = this.etudeMapper.toEntity(etudeDTO);
    //le security check est implémenté dans le save
    etude = this.save(etude);
    tracabiliteEtapeService.addTracabiliteEtape(etude.getId(), null, Etape.CREATION, EtatEtape.VALIDE);
    return this.etudeMapper.toDto(etude);
  }

  @Override
  public EtudeDTO saveEtudeFromImport(EtudeDTO etudeDTO) {
    Etude etude = this.etudeMapper.toEntityFromImport(etudeDTO);
    etude.setDateCreation(LocalDate.now());
    etude.setIfImporte(true);
    etude = this.save(etude);
    tracabiliteEtapeService.addTracabiliteEtape(etude.getId(), null, Etape.CREATION, EtatEtape.VALIDE);
    return this.etudeMapper.toDto(etude);
  }

  public Optional<EtudeDTO> updateEtude(Long id, EtudeDTO etudeDTO){
    Etude existingEtude = findById(id);
    if (existingEtude != null) {
      Etude etude = this.etudeMapper.toEntity(etudeDTO);
      etude.setId(id);
      etude.setUser(existingEtude.getUser());
      //le security check est implémenté dans le save
      etude = this.save(etude);
      tracabiliteEtapeService.addTracabiliteEtape(etude.getId(), null, Etape.CREATION, EtatEtape.VALIDE);
      EtudeDTO responseDTO = this.etudeMapper.toDto(etude);
      return Optional.of(responseDTO);
    }
    return Optional.empty();
  }

  @Override
  public Optional<EtudeDTO> publierEtude(EtudeDTO etudeDTO) {
    Etude existingEtude = findById(etudeDTO.getId());
    if (existingEtude != null) {
      existingEtude.setIfPublic(!existingEtude.isIfPublic());
      EtudeDTO responseDTO = this.etudeMapper.toDto(this.save(existingEtude));
      return Optional.of(responseDTO);
    }
    return Optional.empty();
  }

  private EtudesUtilisateurDTO categoriserEtude(List<Etude> etudes, User user) {
    Map<String, List<EtudeDTO>> etudesParTypes = new HashMap<>();
    for (Etude e : etudes) {
      securityService.checkEtudeType(e, user).ifPresent(type -> etudesParTypes.computeIfAbsent(type, k -> new ArrayList<>()).add(etudeMapper.toDto(e))) ;
    }
    EtudesUtilisateurDTO etudesUtilisateurDTO = new EtudesUtilisateurDTO();
    etudesUtilisateurDTO.setEtudesProprietaire(etudesParTypes.computeIfAbsent(PROPRIETAIRE, k -> new ArrayList<>()));
    etudesUtilisateurDTO.setEtudesMandataireLectureSeule(etudesParTypes.computeIfAbsent(MANDATAIRE_LECTURE_SEULE, k -> new ArrayList<>()));
    etudesUtilisateurDTO.setEtudesMandataireEcriture(etudesParTypes.computeIfAbsent(MANDATAIRE_ECRITURE, k -> new ArrayList<>()));
    etudesUtilisateurDTO.setEtudesPublique(etudesParTypes.computeIfAbsent(PUBLIQUE, k -> new ArrayList<>()));
    etudesUtilisateurDTO.setEtudesImporte(etudesParTypes.computeIfAbsent(IMPORTE, k -> new ArrayList<>()));

    return etudesUtilisateurDTO;
  }




}
