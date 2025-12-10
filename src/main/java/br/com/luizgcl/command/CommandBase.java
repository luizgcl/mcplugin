package br.com.luizgcl.command;

import java.util.function.Consumer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.builder.impl.CommandBuilderImpl;
import br.com.luizgcl.command.factory.CommandFactory;
import br.com.luizgcl.command.helper.CommandHelper;

public abstract class CommandBase {

    public abstract void setup();

    protected void createSimplePlayerCommand(Consumer<Player> consumer, String... names) {
        CommandFactory.make(new CommandBuilderImpl() {
            @Override
            public void handler(CommandSender commandSender, CommandHelper helper, String... args)
                    throws Exception {
                Player player = helper.getPlayer(commandSender);
                consumer.accept(player);
            }
        })
                .player()
                .register(Main.getInstance(), names);
    }

    protected void createSimpleCommand(Consumer<CommandSender> consumer, String... names) {
        CommandFactory.make(new CommandBuilderImpl() {
            @Override
            public void handler(CommandSender commandSender, CommandHelper helper, String... args)
                    throws Exception {
                consumer.accept(commandSender);
            }
        })
                .register(Main.getInstance(), names);
    }

}
