package br.com.luizgcl;

import br.com.luizgcl.database.MongoConnection;
import br.com.luizgcl.manager.BackpackItemManager;
import br.com.luizgcl.manager.PetManager;
import br.com.luizgcl.manager.TabListManager;
import lombok.Getter;

public class Main extends BasePlugin {

  @Getter
  private static MongoConnection mongoConnection;

  @Getter
  private static TabListManager tabManager;

  @Getter
  private static BackpackItemManager backpackItemManager;

  @Getter
  private static PetManager petManager;

  @Override
  public void load() {
    getServer().getLogger().info("Loading plugin...");

    mongoConnection = new MongoConnection(this);
  }

  @Override
  public void enable() {
    getMongoConnection().connect();

    tabManager = new TabListManager();

    getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
      getServer().getScheduler().runTask(this, () -> {
          tabManager.updateAll();
      });
    }, 0L, 20L); 

    backpackItemManager = new BackpackItemManager(this);

    petManager = new PetManager(this);

    getServer().getLogger().info("Plugin enabled!");
  }

  @Override
  public void disable() {
    getMongoConnection().disconnect();
    getServer().getLogger().info("Plugin disabled!");
  }
}