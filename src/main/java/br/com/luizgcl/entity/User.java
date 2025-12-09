package br.com.luizgcl.entity;

import java.util.UUID;

import br.com.luizgcl.database.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends MongoEntity {

    private boolean isFlying;
    private boolean isAllowFlight;

    private boolean isMinerActive = false;

    public User(UUID id) {
        super(id);
    }
}
