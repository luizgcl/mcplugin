package br.com.luizgcl.menu.system;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;
    protected Map<Integer, Consumer<InventoryClickEvent>> actions; // Mapa de Slot -> Ação

    public Menu(int rows, Component title) {
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.actions = new HashMap<>();
    }

    // Método para definir um item e sua ação ao mesmo tempo
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action) {
        inventory.setItem(slot, item);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    // Método sem ação (apenas decoração)
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    // Abre o menu para o player
    public void open(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
        player.openInventory(inventory);
    }

    // Chamado pelo MenuListener quando houver um click
    public void handleMenuClick(InventoryClickEvent event) {
        // Verifica se existe uma ação registrada para esse slot
        if (actions.containsKey(event.getRawSlot())) {
            actions.get(event.getRawSlot()).accept(event);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}