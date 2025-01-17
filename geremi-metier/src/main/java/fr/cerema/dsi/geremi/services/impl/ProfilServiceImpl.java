package fr.cerema.dsi.geremi.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.Profil;
import fr.cerema.dsi.geremi.repositories.ProfilRepository;
import fr.cerema.dsi.geremi.services.ProfilService;

@Service("profilService")
public class ProfilServiceImpl extends GenericServiceImpl<Profil, Long> implements ProfilService {

	private final ProfilRepository profilRepository;

	public ProfilServiceImpl(ProfilRepository profilRepository) {
		this.profilRepository = profilRepository;
	}

  @Override
  public List<Profil> findAllCurrentRolesByUsername(String usersname) {
    return this.profilRepository.findAllCurrentRolesByUsername(usersname);
  }

  @Override
  public List<Profil> findByLibelle(String role) {
    return this.profilRepository.findByLibelle(role);
  }

  @Override
	public Profil create(Profil entity) {
		return this.profilRepository.save(entity);
	}

	@Override
	public List<Profil> findAll() {
		return this.profilRepository.findAll();
	}

	@Override
	public Profil findById(Long id) {
		return this.profilRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteById(Long id) {
		this.profilRepository.deleteById(id);
	}

}
