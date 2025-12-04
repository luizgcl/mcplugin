package br.com.luizgcl.listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import java.net.http.WebSocket.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class CombatListener implements Listener {

  @EventHandler
  public void onKnockback(EntityKnockbackByEntityEvent event) {
    if (event.getEntity() instanceof Player player) {
      Vector kb = player.getVelocity().multiply(0.8);
      player.setVelocity(kb);
    }
  }
}
