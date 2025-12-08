package br.com.luizgcl.database;

import java.util.UUID;

public interface RepositoryContract<T> {

  T findOne(UUID id);
  T findByField(String field, Object value);
  void create(T entity) throws Exception;
  void update(UUID id, T entity) throws Exception;
  void destroy(UUID id) throws Exception;

}
