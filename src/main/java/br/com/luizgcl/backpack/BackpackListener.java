package br.com.luizgcl.backpack;

import br.com.luizgcl.Main;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BackpackListener implements Listener {

    // 1. Abrir ao clicar com direito
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        ItemStack item = event.getItem();
        if (Main.getBackpackItemManager().isBackpack(item)) {
            event.setCancelled(true); // Não colocar o bloco no chão se for bloco
            Main.getBackpackItemManager().openBackpack(event.getPlayer(), item);
        }
    }

    // 2. Salvar ao fechar
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        
        if (title.contains("Mochila")) {
            // Precisamos descobrir QUAL item ele usou para abrir.
            // Geralmente é o da mão principal.
            Player player = (Player) event.getPlayer();
            ItemStack handItem = player.getInventory().getItemInMainHand();
            
            if (Main.getBackpackItemManager().isBackpack(handItem)) {
                Main.getBackpackItemManager().saveBackpack(handItem, event.getInventory());
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1);
            }
        }
    }
    
    // 3. Segurança: Impedir colocar a mochila dentro dela mesma
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Mochila")) return;
        
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        
        // Se tentar mover uma mochila para dentro do inventário da mochila
        if (Main.getBackpackItemManager().isBackpack(current) || Main.getBackpackItemManager().isBackpack(cursor)) {
            // Bloqueia se o player estiver mexendo no inventário de CIMA (a mochila aberta)
            // ou se usar Shift-Click
            if (event.getClickedInventory() == event.getView().getTopInventory() || event.isShiftClick()) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cVocê não pode colocar mochilas dentro de mochilas!");
            }
        }
    }
}