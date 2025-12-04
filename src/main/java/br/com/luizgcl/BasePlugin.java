package br.com.luizgcl;

import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.command.loader.CommandRegister;
import br.com.luizgcl.utils.ClassGetter;
import br.com.luizgcl.utils.Util;
import java.lang.reflect.Method;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BasePlugin extends JavaPlugin {

  @Getter
  private static BasePlugin instance;

  @Override
  public void onLoad() {
    this.instance = this;
    this.load();
  }

  @Override
  public void onEnable() {
    super.onEnable();
    this.registerListeners();
    this.enable();
  }

  @Override
  public void onDisable() {
    super.onDisable();
    this.disable();
  }

  public abstract void load();
  public abstract void enable();
  public abstract void disable();

  protected void registerListeners() {
    int count = 0;
    long millis = System.currentTimeMillis();

    getLogger().info("Registrando eventos...");
    for (Class<?> classes : ClassGetter.getClassesForPackage(this, getClass().getPackage().toString())) {
      if (! Listener.class.isAssignableFrom(classes)) {
        continue;
      }

      try {
        Listener listener = (Listener) classes.newInstance();
        registerEvent(listener);

        for (Method method : listener.getClass().getDeclaredMethods()) {
          if (method.getAnnotation(EventHandler.class) != null) {
            count += 1;
          }
        }
      } catch (InstantiationException | IllegalAccessException  e) {
        getLogger().severe("Ocorreu um erro ao registrar eventos da classe " + classes.getSimpleName());
        getLogger().severe("> Causa do erro " + e.getCause());
      }
    }

    if (count > 0)
      getLogger().info("Registro de eventos concluidos (Total de eventos registrados: " + count + ") em "
          + Util.formatMillis(millis));
  }

  protected void registerCommands() {
    int count = 0;
    long millis = System.currentTimeMillis();

    getLogger().info("Iniciando registro de comandos...");
    for (Class<?> classes : ClassGetter.getClassesForPackage(this, getClass().getPackage().toString())) {
      if (CommandBase.class.isAssignableFrom(classes) && classes != CommandBase.class) {
        try {
          CommandBase command = (CommandBase) classes.newInstance();
          command.setup();
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
          getLogger().severe("Ocorreu algum erro ao registrar o comando '" + classes.getName() + "'.");
        }
      }
    }

    if (count > 0)
      getLogger().info("Registro de comandos concluido (Total de comandos registrados: " + count + ") em "
          + Util.formatMillis(millis));
  }

  public <T extends CommandExecutor> void registerCommand(T command, String... aliases) {
    CommandRegister.registerCommand(this, command, aliases);
  }

  protected void registerEvent(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }
}
