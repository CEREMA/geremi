package fr.cerema.dsi.geremi.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.enums.Etape;

@Service("securityService")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public interface SecurityService  {

  Optional<String> checkEtudeType(Etude etude, User user);
  void checkModificationEtude(Long idEtude, Long idScenario, Etape etape);

  void checkModificationEtude(Optional<Etude> etude, Long idScenario, Etape etape) ;

  void checkConsultationEtude(Long idEtude) ;

  void checkConsultationEtude(Optional<Etude> etude);

  void checkSupressionEtude(Long idEtude);
  void checkSupressionEtude(Optional<Etude> etude);

  void checkCreationEtude();

}
