package br.com.luizgcl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChatListener implements Listener {

    @EventHandler
    void event(AsyncChatEvent event) {
        event.renderer((source, sourceDisplayName, message, viewer) -> 
            source.displayName().colorIfAbsent(NamedTextColor.GRAY)
            .append(Component.text(" §8» §f" ))
            .append(message)
        );
    }
}
