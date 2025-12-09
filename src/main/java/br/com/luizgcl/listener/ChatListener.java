package br.com.luizgcl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class ChatListener implements Listener {

    @EventHandler
    void event(AsyncChatEvent event) {
        event.message(
            event.getPlayer().displayName()
            .append(Component.text(" §8» §f" ))
            .append(event.message())
        );
    }
}
