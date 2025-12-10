package br.com.luizgcl.backpack;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.luizgcl.Main;
import br.com.luizgcl.menu.Menu;
import br.com.luizgcl.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class BackpackShopMenu extends Menu {

    private BackpackItemManager manager;

    public BackpackShopMenu() {
        super(3, MiniMessage.miniMessage().deserialize("<gradient:blue:aqua><b>Escolha sua Mochila</b></gradient>"));
        this.manager = Main.getBackpackItemManager();;
        initItems();
    }

    private void initItems() {
        // Slot 10: Mochila de Couro (Pequena)
        setItem(10, new ItemBuilder(Material.LEATHER)
            .name("<gradient:#C4A484:#8B4513><b>Mochila de Couro</b>")
            .lore("<gray>Tamanho: <white>27 Espaços (Pequena)", "", "<yellow>Custo: <white>32x Couros")
            .build(),
            e -> buy((Player) e.getWhoClicked(), BackpackType.LEATHER)
        );

        // Slot 12: Mochila de Ferro (Média)
        setItem(12, new ItemBuilder(Material.IRON_INGOT)
            .name("<gradient:#A9A9A9:#FFFFFF><b>Mochila de Ferro</b>")
            .lore("<gray>Tamanho: <white>36 Espaços (Média)", "", "<yellow>Custo: <white>64x Ferros")
            .build(),
            e -> buy((Player) e.getWhoClicked(), BackpackType.IRON)
        );

        // Slot 14: Mochila de Ouro (Grande)
        setItem(14, new ItemBuilder(Material.GOLD_INGOT)
            .name("<gradient:#FFD700:#FFFFE0><b>Mochila de Ouro</b>")
            .lore("<gray>Tamanho: <white>45 Espaços (Grande)", "", "<yellow>Custo: <white>32x Ouros")
            .build(),
            e -> buy((Player) e.getWhoClicked(), BackpackType.GOLD)
        );

        // Slot 16: Mochila de Diamante (Gigante)
        setItem(16, new ItemBuilder(Material.DIAMOND)
            .name("<gradient:#00FFFF:#E0FFFF><b>Mochila de Diamante</b>")
            .lore("<gray>Tamanho: <white>54 Espaços (Gigante)", "", "<yellow>Custo: <white>10x Diamantes")
            .build(),
            e -> buy((Player) e.getWhoClicked(), BackpackType.DIAMOND)
        );
        
        // Botão Voltar (Slot 22)
        setItem(22, new ItemBuilder(Material.ARROW).name("<red>Voltar").build(),
            e -> {
                 // Aqui você pode reabrir o ShopMenu principal se tiver acesso a ele
                 e.getWhoClicked().closeInventory();
            }
        );
    }

    private void buy(Player p, BackpackType type) {
        if (p.getInventory().contains(type.getMaterialType(), type.getCostAmount())) {
            // Remove os itens
            p.getInventory().removeItem(new ItemStack(type.getMaterialType(), type.getCostAmount()));
            
            // Dá a mochila
            p.getInventory().addItem(manager.createNewBackpack(type));
            
            p.sendMessage(Component.text("§aVocê comprou uma " + MiniMessage.miniMessage().stripTags(type.getName()) + "!"));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            p.closeInventory();
        } else {
            p.sendMessage(Component.text("§cVocê precisa de " + type.getCostAmount() + "x " + type.getMaterialType().name() + "!"));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }
    
}
