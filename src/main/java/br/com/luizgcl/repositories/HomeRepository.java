package br.com.luizgcl.repositories;

import br.com.luizgcl.database.MongoRepository;
import br.com.luizgcl.entity.Home;

public class HomeRepository extends MongoRepository<Home> {

  public HomeRepository() {
    super("homes");
  }
}
