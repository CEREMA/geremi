package fr.cerema.dsi.commons.services;


import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.commons.entities.GenericEntity;
import fr.cerema.dsi.commons.exceptions.EntityNotFoundException;
import fr.cerema.dsi.commons.repositories.GenericRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class GenericServiceImpl<T extends GenericEntity, ID extends Serializable> implements
  GenericService<T, ID> {
  @Autowired
  protected GenericRepository<T, ID> repository;

  public List<T> findAll() {
    return repository.findAll();
  }

  public T getOne(ID primaryKey) {
    return repository.getOne(primaryKey);
  }

  public T findById(ID id) {
    return repository.findById(id)
      .orElseGet(() -> {
        throw new EntityNotFoundException(
          "L'entité d'identifiant {" + id + "} n'a pas été trouvée");
      });
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public T create(T entity) {
    return repository.save(entity);
  }


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void deleteById(ID id) {
    repository.deleteById(id);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public T save(T entity) {
    return this.repository.save(entity);
  }
}
