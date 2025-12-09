package br.com.luizgcl.repositories;

import br.com.luizgcl.database.MongoRepository;
import br.com.luizgcl.entity.User;

public class UserRepository extends MongoRepository<User> {
    
    public UserRepository() {
        super("users");
    }
}
