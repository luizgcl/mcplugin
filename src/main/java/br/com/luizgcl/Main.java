package br.com.luizgcl;

public class Main extends BasePlugin {
  @Override
  public void load() {
    getServer().getLogger().info("Loading plugin...");
  }

  @Override
  public void enable() {
    getServer().getLogger().info("Plugin enabled!");
  }

  @Override
  public void disable() {
    getServer().getLogger().info("Plugin disabled!");
  }
}