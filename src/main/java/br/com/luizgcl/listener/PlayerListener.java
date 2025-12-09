package br.com.luizgcl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerListener implements Listener {

  @EventHandler
  void event(PlayerJoinEvent event) {
    event.joinMessage(null);
    event.getPlayer()
        .sendMessage(Component.text()
            .append(Component.text("Seja bem-vindo ao servidor!").color(NamedTextColor.GREEN))
            .append(Component.newline())
            .append(Component.text("Divirta-se!").color(NamedTextColor.YELLOW)));
  }

  @EventHandler
  void event(PlayerQuitEvent event) {
    event.quitMessage(null);
  }
}
