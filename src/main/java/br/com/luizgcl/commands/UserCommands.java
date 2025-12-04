package br.com.luizgcl.commands;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.command.builder.impl.CommandBuilderImpl;
import br.com.luizgcl.command.factory.CommandFactory;
import br.com.luizgcl.command.helper.CommandHelper;
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
    }).usage("ping").player()
        .register(Main.getInstance(),"ms", "latency");
  }
}
