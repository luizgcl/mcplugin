package br.com.luizgcl.shop;

import br.com.luizgcl.Main;
import br.com.luizgcl.manager.BackpackItemManager;
import br.com.luizgcl.menu.system.Menu;
import br.com.luizgcl.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopMenu extends Menu {

    private final BackpackItemManager backpackManager;
    // Preço: 64 Mudas de Carvalho
    private final Material CURRENCY = Material.OAK_SAPLING;
    private final int PRICE = 64;

    public ShopMenu() {
        super(3, MiniMessage.miniMessage().deserialize("<gradient:gold:yellow><b>Loja de Mochilas</b></gradient>"));
        this.backpackManager = Main.getBackpackItemManager();
        initItems();
    }

    private void initItems() {
        // --- Slot 13: O Produto (Mochila) ---
        setItem(13,
            new ItemBuilder(Material.LEATHER)
                .name("<green><b>Mochila de Viagem</b>")
                .lore(
                    "<gray>Carregue mais itens com você!",
                    "",
                    "<yellow>Preço: <white>" + PRICE + "x Mudas de Carvalho",
                    "",
                    "<green>Clique para comprar!"
                ).build(),
            
            // AÇÃO DE COMPRA
            event -> {
                Player player = (Player) event.getWhoClicked();
                
                if (hasFunds(player)) {
                    removeFunds(player);
                    
                    // Entrega a mochila
                    player.getInventory().addItem(backpackManager.createNewBackpack());
                    
                    player.sendMessage(Component.text("§aCompra realizada com sucesso!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                    player.closeInventory();
                } else {
                    player.sendMessage(Component.text("§cVocê precisa de " + PRICE + " mudas de carvalho!"));
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
    private boolean hasFunds(Player player) {
        return player.getInventory().contains(CURRENCY, PRICE);
    }

    // Remove as sementes do inventário
    private void removeFunds(Player player) {
        // O método removeItem do Bukkit já lida com remover de vários slots se estiver espalhado
        player.getInventory().removeItem(new ItemStack(CURRENCY, PRICE));
    }
}