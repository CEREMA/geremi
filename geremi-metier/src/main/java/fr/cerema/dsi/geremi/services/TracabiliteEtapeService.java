package fr.cerema.dsi.geremi.services;

import java.util.Map;

import fr.cerema.dsi.commons.exceptions.WorkflowSecurityException;
import fr.cerema.dsi.commons.services.GenericService;
import fr.cerema.dsi.geremi.entities.TracabiliteEtape;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.EtatEtape;

public interface TracabiliteEtapeService extends GenericService<TracabiliteEtape, Long> {

  Map<Etape, EtatEtape> addTracabiliteEtape(Long idEtude, Long idScenario, Etape etape, EtatEtape etat);

  Map<Etape, EtatEtape> getEtatEtapeEtude(Long idEtude, Long idScenario);

  void checkEtapeModifiable(Long idEtude, Long idScenario, Etape etape) throws WorkflowSecurityException;

  void deleteByIdScenario(Long idScenario);

  void deleteByIdEtude(Long idEtude);
}
