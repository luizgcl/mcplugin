package br.com.luizgcl.shop;

import br.com.luizgcl.Main;
import br.com.luizgcl.backpack.BackpackShopMenu;
import br.com.luizgcl.menu.Menu;
import br.com.luizgcl.pet.PetManager;
import br.com.luizgcl.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopMenu extends Menu {

    private final PetManager petManager;
    private final int SHEEP_PRICE = 128;

    public ShopMenu() {
        super(3, MiniMessage.miniMessage().deserialize("<gradient:gold:yellow><b>Loja de Mochilas</b></gradient>"));
        this.petManager = Main.getPetManager();
        initItems();
    }

    private void initItems() {
        // --- Slot 13: O Produto (Mochila) ---
        setItem(11,
            new ItemBuilder(Material.BUNDLE) // Use BUNDLE ou CHEST para representar a categoria
                .name("<green><b>Comprar Mochilas</b>")
                .lore(
                    "<gray>Clique para ver os modelos",
                    "<gray>disponíveis (Couro, Ferro...)",
                    "",
                    "<green>Clique para abrir!"
                ).build(),
            event -> {
                new BackpackShopMenu().open((Player) event.getWhoClicked());
            }
        );

        setItem(15,
            new ItemBuilder(Material.WHITE_WOOL)
                .name("<light_purple><b>Ovelha de Estimação</b>")
                .lore(
                    "<gray>Um companheiro fofo!",
                    "<gray>Ela te segue para onde for.",
                    "",
                    "<yellow>Preço: <white>" + SHEEP_PRICE + "x Mudas",
                    "",
                    "<green>Clique para adotar!"
                ).build(),

            event -> {
                Player player = (Player) event.getWhoClicked();
                
                if (hasFunds(player, SHEEP_PRICE) || player.getName().equalsIgnoreCase("anaclsz")) {

                    if (! player.getName().equalsIgnoreCase("anaclsz")) {
                        removeFunds(player, SHEEP_PRICE);
                    }
                    
                    this.petManager.spawnPet(player);

                    player.sendMessage(Component.text("§dVocê adotou uma ovelha!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    player.closeInventory();
                } else {
                    player.sendMessage(Component.text("§cMudas insuficientes!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
        );

        // Opcional: Decoração em volta
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 27; i++) {
            if (getInventory().getItem(i) == null) {
                setItem(i, border);
            }
        }
    }

    // Verifica se o player tem as sementes necessárias
    private boolean hasFunds(Player player, int amount) {
        int totalSaplings = 0;
        ItemStack[] contents = player.getInventory().getContents();

        // 1. Contagem: Verifica se o jogador tem o suficiente no total
        for (ItemStack item : contents) {
            if (item != null && Tag.SAPLINGS.isTagged(item.getType())) {
                totalSaplings += item.getAmount();
            }
        }

        return totalSaplings >= amount;
    }

    // Remove as sementes do inventário
    private void removeFunds(Player player, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int itemsToRemove = amount;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item != null && Tag.SAPLINGS.isTagged(item.getType())) {
                int quantity = item.getAmount();

                if (quantity <= itemsToRemove) {
                    // Se o stack for menor ou igual ao que precisamos remover, removemos tudo
                    itemsToRemove -= quantity;
                    player.getInventory().setItem(i, null); // Remove o item do slot
                } else {
                    // Se o stack for maior, removemos apenas a parte necessária
                    item.setAmount(quantity - itemsToRemove);
                    itemsToRemove = 0;
                    // Não precisamos setar o item de volta pois item é uma referência direta,
                    // mas o inventário atualiza automaticamente.
                }

                if (itemsToRemove <= 0) {
                    break; // Já removemos tudo que precisava
                }
            }
        }
    }
}