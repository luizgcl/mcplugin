package br.com.luizgcl.command.builder.impl;

import br.com.luizgcl.BasePlugin;
import br.com.luizgcl.command.builder.CommandBuilder;
import br.com.luizgcl.command.builder.impl.constants.CommandConstants;
import br.com.luizgcl.command.helper.impl.CommandHelperImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class CommandBuilderImpl implements CommandExecutor, CommandBuilder {
    private boolean async, onlyPlayer;
    private String permission;
    private ThreadPoolExecutor executor;

    private CommandConstants constants = new CommandConstants();
    private String usage;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (onlyPlayer && !(commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly for players.");
            return false;
        }
        if (permission != null && !(commandSender.hasPermission(permission))) {
            commandSender.sendMessage(constants.DONT_HAVE_PERMISSION);
            return false;
        }

        if (async) {
            CompletableFuture.runAsync(() -> {
                try {
                    handler(commandSender, new CommandHelperImpl(), args);
                } catch (Exception e) {
                    commandSender.sendMessage("§cAn error has occurred to execute this command §e/" + command.getName() + "§c.");
                    e.printStackTrace();
                }
            }, executor);
        } else {
            try {
                handler(commandSender, new CommandHelperImpl(), args);
            } catch (Exception e) {
                commandSender.sendMessage("§cAn error has occurred to execute this command §e/" + command.getName() + "§c.");
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void register(BasePlugin plugin, String... alias) {
        plugin.registerCommand(this, alias);
    }

    @Override
    public CommandBuilder async() {
        this.async = true;
        return this;
    }

    @Override
    public CommandBuilder executor(ThreadPoolExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public CommandBuilder usage(String usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public CommandBuilder sendUsage(CommandSender commandSender) {
        commandSender.sendMessage("§cUtilize: /" + this.usage);
        return this;
    }

    @Override
    public CommandBuilder permission(String perm) {
        this.permission = perm;
        return this;
    }

    @Override
    public CommandBuilder player() {
        this.onlyPlayer = true;
        return this;
    }
}
