package br.com.luizgcl.commands;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.entity.User;
import br.com.luizgcl.repositories.UserRepository;

import org.bukkit.Bukkit;

public class UserCommands extends CommandBase {

  @Override
  public void setup() {
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
