package fr.cerema.dsi.commons.services;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.commons.entities.GenericEntity;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GenericService<T extends GenericEntity, ID extends Serializable> {

  List<T> findAll();

  T getOne(ID id);

  T findById(ID id);

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  T create(T entity);


  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  void deleteById(ID id);

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  T save(T entity);
}
