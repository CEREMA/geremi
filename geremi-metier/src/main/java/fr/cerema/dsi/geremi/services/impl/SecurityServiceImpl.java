package fr.cerema.dsi.geremi.services.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.exceptions.WorkflowSecurityException;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.RelEtudeUserProcuration;
import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.enums.Etape;
import fr.cerema.dsi.geremi.enums.Role;
import fr.cerema.dsi.geremi.repositories.EtudeRepository;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TracabiliteEtapeService;
import fr.cerema.dsi.geremi.services.UserService;
import fr.cerema.dsi.geremi.services.dto.UserDTO;

@Service("securityServiceImpl")
public class SecurityServiceImpl implements SecurityService {

  public static final String PROPRIETAIRE = "PROPRIÉTAIRE";
  public static final String MANDATAIRE_LECTURE_SEULE = "MANDATAIRE_LECTURE_SEULE";
  public static final String MANDATAIRE_ECRITURE = "MANDATAIRE_ECRITURE";
  public static final String PUBLIQUE = "PUBLIQUE";
  public static final String IMPORTE = "IMPORTE";

  private final UserService userService;

  private final EtudeRepository etudeRepository;

	private final TracabiliteEtapeService tracabiliteEtapeService;


	public SecurityServiceImpl(TracabiliteEtapeService tracabiliteEtapeService, UserService userService, EtudeRepository etudeRepository) {
		this.tracabiliteEtapeService = tracabiliteEtapeService;
		this.userService = userService;
		this.etudeRepository = etudeRepository;
	}

  public Optional<String> checkEtudeType(Etude etude, User user) {
    if(etude.isIfImporte()) {
      return Optional.of(IMPORTE);
    }
    if (etude.getUser().getId().equals(user.getId())) {
      return Optional.of(PROPRIETAIRE);
    }
    if(Arrays.asList(Role.ADMIN.getLibelle(), Role.GEST.getLibelle()).contains(user.getProfil().getLibelle())) {
      return Optional.of(MANDATAIRE_ECRITURE);
    }
    Optional<RelEtudeUserProcuration> relEtudeUserProcuration = etude.getRelEtudeUserProcuration().stream()
      .filter(p -> p.getUser().getId().equals(user.getId()))
      .findFirst();
    if (relEtudeUserProcuration.isPresent()) {
      if (relEtudeUserProcuration.get().getIfDroitEcriture()) {
        return Optional.of(MANDATAIRE_ECRITURE);
      }
      return Optional.of(MANDATAIRE_LECTURE_SEULE);
    }
    if(etude.isIfPublic()) {
      return Optional.of(PUBLIQUE);
    }
    return Optional.empty();
  }

  @Override
  public void checkModificationEtude(Long idEtude, Long idScenario, Etape etape) {
    Optional<Etude> etude = this.etudeRepository.findById(idEtude);
    checkModificationEtude(etude, idScenario, etape);
  }

  @Override
  public void checkModificationEtude(Optional<Etude> etude, Long idScenario, Etape etape) {
    Optional<User> user = userService.getCurrentUserEntity();
    if (user.isEmpty() || etude.isEmpty() ||
        (checkEtudeType(etude.get(), user.get()).filter(t -> t.equals(PROPRIETAIRE) || t.equals(MANDATAIRE_ECRITURE)).isEmpty() &&
          !Arrays.asList(Role.ADMIN.getLibelle(), Role.GEST.getLibelle()).contains(user.get().getProfil().getLibelle())
        )
      ) {
      throw new WorkflowSecurityException("L'utilisateur n'est pas autorisé à modifier cette étude");
    }
    this.tracabiliteEtapeService.checkEtapeModifiable(etude.get().getId(), idScenario, etape);
  }

  @Override
  public void checkSupressionEtude(Long idEtude) {
    Optional<Etude> etude = this.etudeRepository.findById(idEtude);
    checkSupressionEtude(etude);
  }

  @Override
  public void checkSupressionEtude(Optional<Etude> etude) {
    Optional<User> user = userService.getCurrentUserEntity();
    if (user.isEmpty() || etude.isEmpty() || checkEtudeType(etude.get(), user.get()).filter(t -> t.equals(PROPRIETAIRE)).isEmpty() &&
      !Arrays.asList(Role.ADMIN.getLibelle(), Role.GEST.getLibelle()).contains(user.get().getProfil().getLibelle())) {
      throw new WorkflowSecurityException("L'utilisateur n'est pas autorisé à supprimer cette étude");
    }
  }

  @Override
  public void checkConsultationEtude(Long idEtude) {
    Optional<Etude> etude = this.etudeRepository.findById(idEtude);
    checkConsultationEtude(etude);
  }

  @Override
  public void checkConsultationEtude(Optional<Etude> etude) {
    Optional<User> user = userService.getCurrentUserEntity();
    if (user.isEmpty() || etude.isEmpty() || (checkEtudeType(etude.get(), user.get()).isEmpty()) ) {
      throw new WorkflowSecurityException("L'utilisateur n'est pas autorisé à consulter cette étude");
    }
  }

  @Override
  public void checkCreationEtude() {
    Optional<UserDTO> user = this.userService.getCurrentUser();
    if (user.isEmpty() || Role.PUBLIC.getLibelle().equals(user.get().getLibelleProfil()) ) {
      throw new WorkflowSecurityException("L'utilisateur n'est pas autorisé à créer une étude");
    }
  }

}
