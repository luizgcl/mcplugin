package br.com.luizgcl.cook;

import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import br.com.luizgcl.command.CommandBase;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CookCommand extends CommandBase {

    @Override
    public void setup() {
        createSimplePlayerCommand(player -> {
          MiniMessage mm = MiniMessage.miniMessage();
          int totalCoal = CookUtils.countCoal(player);

          if (totalCoal == 0) {
              player.sendMessage(mm.deserialize("<red>Você precisa de Carvão (Coal ou Charcoal) para cozinhar!"));
              player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.5f);
              return;
          }

          int cookingCapacity = totalCoal * 8;
          int itemsCooked = 0;

          ItemStack[] contents = player.getInventory().getContents();

          for (int i = 0; i < contents.length; i++) {
              ItemStack item = contents[i];
              
              if (itemsCooked >= cookingCapacity) break; 
              
              if (item == null || item.getType().isAir()) continue;

              ItemStack result = CookUtils.getSmeltingResult(item);

              if (result != null) {
                  int amount = item.getAmount();
                  
                  int toCook = Math.min(amount, cookingCapacity - itemsCooked);


                  if (toCook == amount) {
                      result.setAmount(amount);
                      player.getInventory().setItem(i, result);
                  } 
                  else {
                      item.setAmount(amount - toCook);
                      
                      ItemStack cookedStack = result.clone();
                      cookedStack.setAmount(toCook);
                      
                      HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(cookedStack);
                      if (!leftOver.isEmpty()) {
                          player.getWorld().dropItemNaturally(player.getLocation(), leftOver.get(0));
                      }
                  }

                  itemsCooked += toCook;
              }
          }

          if (itemsCooked > 0) {
              int coalConsumed = (int) Math.ceil((double) itemsCooked / 8.0);
              CookUtils.removeCoal(player, coalConsumed);

              player.sendMessage(mm.deserialize("<green>Você assou <white>" + itemsCooked + "</white> itens e gastou <white>" + coalConsumed + "</white> carvões!"));
              player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1, 1);
          } else {
              player.sendMessage(mm.deserialize("<yellow>Você não tem nenhuma comida crua no inventário."));
          }
        }, "cook", "cozinhar");
    }
    
}
