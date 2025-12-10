package br.com.luizgcl.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // Verifica se clicou em um inventário válido e se tem item
        if (event.getInventory() == null || event.getCurrentItem() == null) return;

        InventoryHolder holder = event.getInventory().getHolder();

        // A MÁGICA: Se o dono do inventário for nossa classe Menu
        if (holder instanceof Menu) {
            event.setCancelled(true); // Previne roubar itens por padrão
            
            // Chama o método de click do próprio menu
            ((Menu) holder).handleMenuClick(event);
        }
    }
}