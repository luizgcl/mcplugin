package br.com.luizgcl.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TreeCapitatorListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();
    if (!Tag.ITEMS_AXES.isTagged(player.getInventory().getItemInMainHand().getType())) {
      Bukkit.getLogger().info(player.getInventory().getItemInMainHand().getType().name() + " is not a axe");
      return;
    }

    if (!Tag.LOGS.isTagged(block.getType())) {
      Bukkit.getLogger().info(block.getType().name() + " is not a log");
      return;
    }

    event.setCancelled(true);
    breakWholeTree(block, player);
  }

  public void breakWholeTree(Block start, Player player) {
    List<Block> logs = getTreeLogs(start);
    Set<Block> leaves = getTreeLeaves(logs);

    ItemStack tool = player.getInventory().getItemInMainHand();

    for (Block log : logs) {
      log.breakNaturally(player.getInventory().getItemInMainHand());
      tool.damage(1, player);
    }

    for (Block leaf : leaves) {
      leaf.breakNaturally();
    }
  }

  public List<Block> getTreeLogs(Block start) {
    List<Block> logs = new ArrayList<>();
    Block current = start;

    // Sobe enquanto for LOG
    while (Tag.LOGS.isTagged(current.getType())) {
      logs.add(current);
      current = current.getRelative(0, 1, 0);
    }

    return logs;
  }

  public Set<Block> getTreeLeaves(List<Block> logs) {
    Set<Block> leaves = new HashSet<>();

    for (Block log : logs) {
      for (int x = -2; x <= 2; x++) {
        for (int y = -2; y <= 2; y++) {
          for (int z = -2; z <= 2; z++) {
            Block b = log.getRelative(x, y, z);

            if (Tag.LEAVES.isTagged(b.getType())) {
              leaves.add(b);
            }
          }
        }
      }
    }

    return leaves;
  }


}
