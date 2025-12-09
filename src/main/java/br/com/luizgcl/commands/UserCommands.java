package br.com.luizgcl.commands;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.command.builder.impl.CommandBuilderImpl;
import br.com.luizgcl.command.factory.CommandFactory;
import br.com.luizgcl.command.helper.CommandHelper;
import br.com.luizgcl.entity.User;
import br.com.luizgcl.repositories.UserRepository;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommands extends CommandBase {

  @Override
  public void setup() {
    CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);

            player.sendMessage(
                "§eSeu ping é §b%s ms§e.".formatted(player.getPing())
            );
          }
        })
        .usage("ping")
        .player()
        .register(Main.getInstance(), "ms", "latency", "ping");

    CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);
            UserRepository userRepository = new UserRepository();
            User user = userRepository.findOne(player.getUniqueId());

            boolean canFly = player.isFlying() || player.getAllowFlight();

            player.setAllowFlight(!canFly);
            if (canFly) {
              player.setFlying(false);
            }

            player.sendMessage(
                canFly ? "§cVocê desativou o modo de voô." :
                    "§aVocê ativou o modo de voô."
            );

            user.setAllowFlight(player.getAllowFlight());
            user.setFlying(player.isFlying());

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
              userRepository.update(user);
            });
          }
        })
        .usage("fly")
        .player()
        .register(Main.getInstance(), "fly", "voar");
  }
}
