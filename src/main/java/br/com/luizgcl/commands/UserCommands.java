package br.com.luizgcl.commands;

import br.com.luizgcl.Main;
import br.com.luizgcl.command.CommandBase;
import br.com.luizgcl.command.builder.impl.CommandBuilderImpl;
import br.com.luizgcl.command.factory.CommandFactory;
import br.com.luizgcl.command.helper.CommandHelper;
import br.com.luizgcl.cook.CookUtils;
import br.com.luizgcl.entity.User;
import br.com.luizgcl.repositories.UserRepository;
import br.com.luizgcl.shop.ShopMenu;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UserCommands extends CommandBase {

  @Override
  public void setup() {
    CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);

            player.sendMessage(
                "§eSeu ping é §b%s ms§e.".formatted(player.getPing())
            );
          }
        })
        .usage("ping")
        .player()
        .register(Main.getInstance(), "ms", "latency", "ping");

    CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);
            UserRepository userRepository = new UserRepository();
            User user = userRepository.findOne(player.getUniqueId());

            boolean canFly = player.isFlying() || player.getAllowFlight();

            player.setAllowFlight(!canFly);
            if (canFly) {
              player.setFlying(false);
            }

            player.sendMessage(
                canFly ? "§cVocê desativou o modo de voô." :
                    "§aVocê ativou o modo de voô."
            );

            user.setAllowFlight(player.getAllowFlight());
            user.setFlying(player.isFlying());

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
              userRepository.update(user);
            });
          }
        })
        .usage("fly")
        .player()
        .register(Main.getInstance(), "fly", "voar");

      CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);
            UserRepository userRepository = new UserRepository();
            User user = userRepository.findOne(player.getUniqueId());

            boolean canMiner = user.isMinerActive();

            user.setMinerActive(!canMiner);
            player.sendMessage(
                canMiner ? "§cVocê desativou o modo miner." :
                    "§aVocê ativou o modo miner."
            );

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
              userRepository.update(user);
            });
          }
        })
        .usage("miner")
        .player()
        .register(Main.getInstance(), "miner");

      CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);
            UserRepository userRepository = new UserRepository();
            User user = userRepository.findOne(player.getUniqueId());

            boolean canMiner = user.isMinerActive();

            user.setMinerActive(!canMiner);
            player.sendMessage(
                canMiner ? "§cVocê desativou o modo miner." :
                    "§aVocê ativou o modo miner."
            );

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
              userRepository.update(user);
            });
          }
        })
        .usage("miner")
        .player()
        .register(Main.getInstance(), "miner");

        createSimpleCommand(player -> {
          new ShopMenu().open(player);
        }, "shop");


        createSimpleCommand(player -> {
          MiniMessage mm = MiniMessage.miniMessage();
          int totalCoal = CookUtils.countCoal(player);

          if (totalCoal == 0) {
              player.sendMessage(mm.deserialize("<red>Você precisa de Carvão (Coal ou Charcoal) para cozinhar!"));
              player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.5f);
              return;
          }

          // Capacidade de cozimento (1 carvão = 8 itens)
          int cookingCapacity = totalCoal * 8;
          int itemsCooked = 0;

          // 2. Iterar pelo inventário procurando itens crus
          ItemStack[] contents = player.getInventory().getContents();

          for (int i = 0; i < contents.length; i++) {
              ItemStack item = contents[i];
              
              // Se já atingimos o limite do carvão, paramos
              if (itemsCooked >= cookingCapacity) break; 
              
              if (item == null || item.getType().isAir()) continue;

              // Verifica o que esse item vira quando assado
              ItemStack result = CookUtils.getSmeltingResult(item);

              if (result != null) {
                  int amount = item.getAmount();
                  
                  // Calcula quantos desse pack podemos assar
                  // Ex: Tenho 10 bifes, mas capacidade para assar só mais 5
                  int toCook = Math.min(amount, cookingCapacity - itemsCooked);

                  // --- A MÁGICA DA TROCA ---
                  
                  // 1. Se vamos cozinhar o stack inteiro
                  if (toCook == amount) {
                      result.setAmount(amount);
                      player.getInventory().setItem(i, result);
                  } 
                  // 2. Se vamos cozinhar só uma parte (sobra item cru)
                  else {
                      item.setAmount(amount - toCook); // Reduz o cru
                      
                      ItemStack cookedStack = result.clone();
                      cookedStack.setAmount(toCook);
                      
                      // Tenta adicionar o assado em outro lugar, se não der, dropa no chão
                      HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(cookedStack);
                      if (!leftOver.isEmpty()) {
                          player.getWorld().dropItemNaturally(player.getLocation(), leftOver.get(0));
                      }
                  }

                  itemsCooked += toCook;
              }
          }

          // 3. Finalização
          if (itemsCooked > 0) {
              // Calcula quanto carvão foi gasto (arredondando para cima)
              int coalConsumed = (int) Math.ceil((double) itemsCooked / 8.0);
              CookUtils.removeCoal(player, coalConsumed);

              player.sendMessage(mm.deserialize("<green>Você assou <b>" + itemsCooked + "</b> itens e gastou <b>" + coalConsumed + "</b> carvões!"));
              player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 1, 1);
          } else {
              player.sendMessage(mm.deserialize("<yellow>Você não tem nenhuma comida crua no inventário."));
          }
        }, "cook");
  }

  private void createSimpleCommand(Consumer<Player> consumer, String... names) {
    CommandFactory.make(new CommandBuilderImpl() {
          @Override
          public void handler(CommandSender commandSender, CommandHelper helper, String... args)
              throws Exception {
            Player player = helper.getPlayer(commandSender);
            consumer.accept(player);
          }
        })
        .player()
        .register(Main.getInstance(), names);
  } 
}
