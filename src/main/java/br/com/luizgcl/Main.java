package br.com.luizgcl;

import br.com.luizgcl.database.MongoConnection;
import lombok.Getter;

public class Main extends BasePlugin {

  @Getter
  private static MongoConnection mongoConnection;

  @Override
  public void load() {
    getServer().getLogger().info("Loading plugin...");

    this.mongoConnection = new MongoConnection(this);
  }

  @Override
  public void enable() {
    getMongoConnection().connect();
    getServer().getLogger().info("Plugin enabled!");
  }

  @Override
  public void disable() {
    getMongoConnection().disconnect();
    getServer().getLogger().info("Plugin disabled!");
  }
}