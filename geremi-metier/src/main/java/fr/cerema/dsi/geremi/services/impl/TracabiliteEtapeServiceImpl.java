package fr.cerema.dsi.geremi.services.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import fr.cerema.dsi.geremi.entities.Etude;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.exceptions.WorkflowSecurityException;
import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Scenario;
import fr.cerema.dsi.geremi.entities.TracabiliteEtape;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;
import fr.cerema.dsi.geremi.repositories.EtudeRepository;
import fr.cerema.dsi.geremi.repositories.ScenarioRepository;
import fr.cerema.dsi.geremi.repositories.TracabiliteEtapeRepository;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.UserService;

@Service("tracabiliteEtapeService")
public class TracabiliteEtapeServiceImpl extends GenericServiceImpl<TracabiliteEtape, Long> implements TracabiliteEtapeService {

  private final UserService userService;

  private final EtudeRepository etudeRepository;

  private final ScenarioRepository scenarioRepository;

	private final TracabiliteEtapeRepository tracabiliteEtapeRepository;


	public TracabiliteEtapeServiceImpl(TracabiliteEtapeRepository tracabiliteEtapeRepository, UserService userService, EtudeRepository etudeRepository, ScenarioRepository scenarioRepository) {
		this.tracabiliteEtapeRepository = tracabiliteEtapeRepository;
		this.userService = userService;
		this.etudeRepository = etudeRepository;
		this.scenarioRepository = scenarioRepository;
	}

	@Override
	public Map<Etape, EtatEtape> addTracabiliteEtape(Long idEtude, Long idScenario, Etape etape, EtatEtape etat) {
    this.checkEtapeModifiable(idEtude,idScenario,etape);
    TracabiliteEtape te = new TracabiliteEtape();
    te.setEtude(etudeRepository.getOne(idEtude));
    if (idScenario != null) {
      te.setScenario(scenarioRepository.getOne(idScenario));
    }
    te.setUser(userService.getCurrentUserEntity().get());
    te.setDateMaj(LocalDateTime.now());
    te.setEtat(etat);
    te.setEtape(etape);
    tracabiliteEtapeRepository.save(te);
    return getEtatEtapeEtude(idEtude, idScenario);
	}

	@Override
	public Map<Etape, EtatEtape> getEtatEtapeEtude(Long idEtude, Long idScenario) {
    List<TracabiliteEtape> tel = tracabiliteEtapeRepository.findTracabiliteEtapeByEtude_IdAndAndScenario_Id(idEtude, idScenario);
    Map<Etape, EtatEtape> etatEtapeEtude = new LinkedHashMap<>();
    Stream.of(Etape.values())
      .filter(e -> Objects.isNull(idScenario) != e.isScenario())
      .forEach(
          e -> etatEtapeEtude.put(
              e,
              tel.stream().filter(t -> t.getEtape().equals(e))
                          .max(Comparator.comparing(TracabiliteEtape::getDateMaj))
                          .map(TracabiliteEtape::getEtat)
                          .orElse(EtatEtape.NON_RENSEIGNE)
          )
      );
    return etatEtapeEtude;
	}

  @Override
  public void checkEtapeModifiable(Long idEtude, Long idScenario, Etape etape) throws WorkflowSecurityException {
    Map<Etape, EtatEtape> etatEtapes = this.getEtatEtapeEtude(idEtude, idScenario);
    //On vérifie qu'il n'y a aucune étape non validée avant l'étape demandée
    //La map est triée, on peut itérer sur l'ordre des étapes

    if(idScenario != null){
      Map<Etape, EtatEtape> etatEtapesEtude = this.getEtatEtapeEtude(idEtude, null);

      for (Map.Entry<Etape, EtatEtape> entry :  etatEtapesEtude.entrySet()){
        if (entry.getKey().equals(etape)) {
          break;
        }
        if (entry.getValue().equals(EtatEtape.VALIDE_VIDE)) {
          if(entry.getKey().equals(Etape.CHANTIERS)){
            etatEtapes.remove(Etape.CHANTIERS_SCENARIO);
          }
          if(entry.getKey().equals(Etape.INSTALLATIONS)){
            etatEtapes.remove(Etape.INSTALLATIONS_SCENARIO);
          }
          if(entry.getKey().equals(Etape.MATERIAUX)){
            etatEtapes.remove(Etape.MATERIAUX_SCENARIO);
          }
        }
      }
    }

    for (Map.Entry<Etape, EtatEtape> entry :  etatEtapes.entrySet()){
      if (entry.getKey().equals(etape)) {
        break;
      }
      if (!entry.getValue().equals(EtatEtape.VALIDE) && !entry.getValue().equals(EtatEtape.VALIDE_VIDE)) {
        throw new WorkflowSecurityException("Impossible de réaliser une opération sur l'étape " + etape.getLibelle() + " tant que l'étape " + entry.getKey().getLibelle() + " n'est pas validée");
      }
    }
  }

  @Override
  public void deleteByIdScenario(Long idScenario) {
    TracabiliteEtape t1 = new TracabiliteEtape();
    Scenario s = new Scenario();
    s.setId(idScenario);
    t1.setScenario(s);

    this.tracabiliteEtapeRepository.deleteInBatch(
      this.tracabiliteEtapeRepository.findAll(Example.of(t1)));
  }

  @Override
  public void deleteByIdEtude(Long idEtude) {
    this.tracabiliteEtapeRepository.deleteInBatch(
      this.tracabiliteEtapeRepository.findTracabiliteEtapeByEtude_IdAndAndScenario_Id(idEtude, null));
  }
}
