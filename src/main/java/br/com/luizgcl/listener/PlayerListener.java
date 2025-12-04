package br.com.luizgcl.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

  @EventHandler
  void event(PlayerJoinEvent event) {
    event.setJoinMessage(null);
    event.getPlayer().sendMessage("§aBem vindo ao servidor!");
  }

  @EventHandler
  void event(PlayerQuitEvent event) {
    event.setQuitMessage(null);
  }
}

