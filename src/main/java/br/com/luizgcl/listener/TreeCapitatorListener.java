package br.com.luizgcl.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TreeCapitatorListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();
    if (!player.getActiveItem().getType().name().endsWith("_AXE")) {
      return;
    }

    if (!block.getType().name().endsWith("_LOG")) {
      return;
    }

    World world = Bukkit.getServer().getWorlds().get(0);
    Double doub = Double.valueOf(block.getLocation().getY() + 1.0D);
    Location location = new Location(world, block.getLocation().getX(), doub.doubleValue(),
        block.getLocation().getZ());
    while (location.getBlock().getType().name().endsWith("_LOG")) {
      location.getBlock().breakNaturally();
      doub = Double.valueOf(doub.doubleValue() + 1.0D);
      location.setY(doub.doubleValue());
    }
  }

}
