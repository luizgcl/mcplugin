package br.com.luizgcl.command.builder;

import br.com.luizgcl.BasePlugin;
import br.com.luizgcl.command.helper.CommandHelper;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ThreadPoolExecutor;

public interface CommandBuilder {

    void handler(CommandSender commandSender, CommandHelper helper, String... args) throws Exception;

    void register(BasePlugin plugin, String... alias);

    CommandBuilder player();

    CommandBuilder async();

    CommandBuilder executor(ThreadPoolExecutor executor);

    CommandBuilder usage(String usage);

    CommandBuilder sendUsage(CommandSender commandSender);
    CommandBuilder permission(String perm);

}
