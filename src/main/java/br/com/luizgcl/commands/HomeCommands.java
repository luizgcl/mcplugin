package br.com.luizgcl.commands;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.command.builder.impl.CommandBuilderImpl;
import br.com.luizgcl.command.factory.CommandFactory;
import br.com.luizgcl.command.helper.CommandHelper;
import br.com.luizgcl.database.MongoRepository;
import br.com.luizgcl.entity.Home;
import br.com.luizgcl.repositories.HomeRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommands extends CommandBase {

  @Override
  public void setup() {
    CommandFactory.make(new CommandBuilderImpl() {
      @Override
      public void handler(CommandSender commandSender, CommandHelper helper, String... args)
          throws Exception {
        Player player = (Player) commandSender;
        HomeRepository homeRepository = new HomeRepository();

        Home home = homeRepository.findOne(player.getUniqueId());

        if (home == null) {
          home = new Home(player.getUniqueId(), player.getLocation());
          homeRepository.create(home);
          player.sendMessage("§aVocê definiu sua casa.");
          player.sendMessage("§7§mDICA:§r§7 Para ir para sua casa, digite: /home");
          return;
        }

        home.setLocation(player.getLocation());
        homeRepository.update(player.getUniqueId(), home);
        player.sendMessage("§eVocê atualizou a localização da sua casa.");
        player.sendMessage("§7§mDICA:§r§7 Para ir para sua casa, digite: /home");
      }
    }).usage("sethome")
        .player()
        .register(Main.getInstance(), "sethome");

    CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = (Player) commandSender;
            HomeRepository homeRepository = new HomeRepository();

            Home home = homeRepository.findOne(player.getUniqueId());

            if (home == null) {
              player.sendMessage("§cVocê não possui uma casa definida.");
              player.sendMessage("§7§mDICA:§r§7 Para definir sua casa, digite: /sethome");
              return;
            }

            player.teleport(home.getLocation());
            player.sendMessage("§aVocê se teleportou para sua casa.");
          }
        }).usage("home")
        .player()
        .register(Main.getInstance(), "home", "casa");
  }
}
