package fr.cerema.dsi.geremi.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.cerema.dsi.commons.services.GenericServiceImpl;
import fr.cerema.dsi.geremi.entities.RefEtat;
import fr.cerema.dsi.geremi.repositories.RefEtatRepository;
import fr.cerema.dsi.geremi.services.RefEtatService;

@Service("refEtatService")
public class RefEtatServiceImpl extends GenericServiceImpl<RefEtat, Long> implements RefEtatService {

	private final RefEtatRepository refEtatRepository;

	public RefEtatServiceImpl(RefEtatRepository refEtatRepository) {
		this.refEtatRepository = refEtatRepository;
	}

	@Override
	public RefEtat create(RefEtat entity) {
		return this.refEtatRepository.save(entity);
	}

	@Override
	public List<RefEtat> findAll() {
		return this.refEtatRepository.findAll();
	}

	@Override
	public RefEtat findById(Long id) {
		return this.refEtatRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteById(Long id) {
		this.refEtatRepository.deleteById(id);
	}

  public List<RefEtat> findByLibelle(String etat) {
    return this.refEtatRepository.findByLibelle(etat);
  }

}
