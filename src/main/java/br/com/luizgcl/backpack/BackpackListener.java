package br.com.luizgcl.backpack;

import br.com.luizgcl.Main;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BackpackListener implements Listener {

    // 1. Abrir ao clicar com direito
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
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
        if (event.getInventory().getHolder() != event.getPlayer()) return;
        
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
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (event.getInventory().getHolder() != event.getWhoClicked()) return;

        boolean isCurrentBackpack = Main.getBackpackItemManager().isBackpack(current);
        boolean isCursorBackpack = Main.getBackpackItemManager().isBackpack(cursor);

        // Se nenhuma das peças for nossa mochila, segue o jogo
        if (!isCurrentBackpack && !isCursorBackpack) return;

        if (isCurrentBackpack && (event.isRightClick() || event.getClick() == ClickType.SHIFT_RIGHT)) {
            // O Bundle vanilla tenta cuspir itens ao clicar com direito. Bloqueamos isso.
            // Permitimos apenas pegar o item (movê-lo) se for clique esquerdo
            event.setCancelled(true);
            return;
        }

        if (isCurrentBackpack && cursor != null && !cursor.getType().isAir()) {
            // O vanilla tentaria engolir o item. Cancelamos.
            event.setCancelled(true);
            // Opcional: Se você quiser que isso ABRA a mochila, você pode chamar openBackpack aqui
            // Mas cuidado, pois abrir menu dentro de evento de click pode bugar se não for agendado.
            return;
        }

        if (isCursorBackpack && current != null && !current.getType().isAir()) {
            // O vanilla tentaria sugar o item do slot. Cancelamos.
            event.setCancelled(true);
            return;
        }

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Mochila")) return;

        // Bloqueia se o player estiver mexendo no inventário de CIMA (a mochila aberta)
        // ou se usar Shift-Click
        if (event.getClickedInventory() == event.getView().getTopInventory() || event.isShiftClick()) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cVocê não pode colocar mochilas dentro de mochilas!");
        }
    }
}