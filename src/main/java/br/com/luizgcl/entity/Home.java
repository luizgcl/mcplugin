package br.com.luizgcl.entity;

import br.com.luizgcl.database.MongoEntity;
import com.google.gson.Gson;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;

public class Home extends MongoEntity {

  String location;

  public Home(UUID id, Location location) {
    super(id);
    this.location = new Gson().toJson(location.serialize());
  }

  public void setLocation(Location location) {
    this.location = new Gson().toJson(location.serialize());
  }

  public Location getLocation() {
    return Location.deserialize(new Gson().fromJson(this.location, Map.class));
  }
}
