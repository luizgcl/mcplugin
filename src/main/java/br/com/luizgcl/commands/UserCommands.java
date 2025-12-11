package br.com.luizgcl.commands;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.entity.User;
import br.com.luizgcl.repositories.UserRepository;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

import org.bukkit.Bukkit;

public class UserCommands extends CommandBase {

  @Override
  public void setup() {
    createSimplePlayerCommand(player -> {
      player.getWorld().setTime(0);
    }, "day", "dia");

    createSimplePlayerCommand(player -> {
      UserRepository userRepository = new UserRepository();
      User user = userRepository.findOne(player.getUniqueId());

      Dialog dialog = Dialog.create(builder -> builder.empty()
          .base(DialogBase
              .builder(Component.text("Preferências do Usuário", NamedTextColor.DARK_AQUA))
              .inputs(List.of(
                  DialogInput.bool(
                      "miner_mode",
                      Component.text("Excavação automática"))
                      .initial(user.isMinerActive())
                      .build()))
              .build())
          .type(DialogType.confirmation(
              ActionButton.create(
                  Component.text("Salvar").color(NamedTextColor.GREEN),
                  Component.text("Salvar preferências"),
                  100,
                  DialogAction.customClick((view, audience) -> {
                    boolean minerMode = view.getBoolean("miner_mode");

                    user.setMinerActive(minerMode);
                    player.sendMessage("§aPreferências salvas com sucesso.");

                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                      userRepository.update(user);
                    });
                  }, 
                ClickCallback.Options
                  .builder()
                  .uses(1)
                  .lifetime(ClickCallback.DEFAULT_LIFETIME)
                  .build()
                )),
              ActionButton.create(
                  Component.text("Cancelar").color(NamedTextColor.RED),
                  Component.text("Fechar menu"),
                  100,
                  null))));
      player.showDialog(dialog);
    }, "toggles", "pref", "preferences", "preferencias");

    createSimplePlayerCommand(player -> {
      player.sendMessage(
          "§eSeu ping é §b%s ms§e.".formatted(player.getPing()));
    }, "ms", "latency", "ping");

    createSimplePlayerCommand(player -> {
      UserRepository userRepository = new UserRepository();
      User user = userRepository.findOne(player.getUniqueId());

      boolean canFly = player.isFlying() || player.getAllowFlight();

      player.setAllowFlight(!canFly);
      if (canFly) {
        player.setFlying(false);
      }

      player.sendMessage(
          canFly ? "§cVocê desativou o modo de voô." : "§aVocê ativou o modo de voô.");

      user.setAllowFlight(player.getAllowFlight());
      user.setFlying(player.isFlying());

      Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
        userRepository.update(user);
      });
    }, "fly", "voar");

    createSimplePlayerCommand(player -> {
      UserRepository userRepository = new UserRepository();
      User user = userRepository.findOne(player.getUniqueId());

      boolean canMiner = user.isMinerActive();

      user.setMinerActive(!canMiner);
      player.sendMessage(
          canMiner ? "§cVocê desativou o modo miner." : "§aVocê ativou o modo miner.");

      Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
        userRepository.update(user);
      });
    }, "miner");
  }
}
