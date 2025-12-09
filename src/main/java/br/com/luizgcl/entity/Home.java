package br.com.luizgcl.entity;

import br.com.luizgcl.database.MongoEntity;

import com.google.common.reflect.TypeToken;
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
    if (this.location == null) {
      return null;
    }
    
    Map<String, Object> map = new Gson().fromJson(
        this.location,
        new TypeToken<Map<String, Object>>() {}.getType()
    );

    return Location.deserialize(map);
  }
}
