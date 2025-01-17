package fr.cerema.dsi.commons.repositories;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.cerema.dsi.commons.entities.GenericEntity;

@NoRepositoryBean
@Transactional(propagation = Propagation.MANDATORY, readOnly = true)
public interface GenericRepository<T extends GenericEntity, ID extends Serializable>
  extends JpaRepository<T, ID>

/* Afin que la propagation MANDATORY d'applique à toutes les méthodes existantes dans le JpaRepository,
    Il est nécessaire de les redéclarer ici. Sinon, le comportement pat défaut de JpaRepository s'applique, et ce n'est pas mandatory.
*/ {


  @Override
  List<T> findAll();

  @Override
  List<T> findAll(Sort sort);

  @Override
  List<T> findAllById(Iterable<ID> primaryKeys);

  @Override
  <S extends T> List<S> saveAll(Iterable<S> entities);

  @Override
  void flush();

  @Override
  @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
  <S extends T> S saveAndFlush(S entity);

  @Override
  void deleteInBatch(Iterable<T> entities);

  @Override
  void deleteAllInBatch();

  @Override
  T getOne(ID primaryKey);

  @Override
  <S extends T> List<S> findAll(Example<S> example);

  @Override
  <S extends T> List<S> findAll(Example<S> example, Sort tri);

  @Override
  Page<T> findAll(Pageable pageable);

  @Override
  <S extends T> Optional<S> findOne(Example<S> example);

  @Override
  <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

  @Override
  <S extends T> long count(Example<S> example);

  @Override
  <S extends T> boolean exists(Example<S> example);

  @Override
  @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
  <S extends T> S save(S entity);

  @Override
  Optional<T> findById(ID primaryKey);

  @Override
  boolean existsById(ID primaryKey);

  @Override
  long count();

  @Override
  void deleteById(ID primaryKey);

  @Override
  void delete(T entity);

  @Override
  void deleteAll(Iterable<? extends T> entities);

  @Override
  void deleteAll();
}

