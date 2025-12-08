package br.com.luizgcl.database;

import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class MongoEntity {

  protected UUID id;

  public MongoEntity(UUID id) {
    this.id = id;
  }

}
