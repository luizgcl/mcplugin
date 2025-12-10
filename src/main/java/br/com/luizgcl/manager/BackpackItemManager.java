package br.com.luizgcl.manager;

import br.com.luizgcl.utils.SerializerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;

public class BackpackItemManager {

    private final NamespacedKey key;
    private final NamespacedKey uuidKey; // Para garantir que cada mochila seja única

    public BackpackItemManager(JavaPlugin plugin) {
        this.key = new NamespacedKey(plugin, "backpack_content");
        this.uuidKey = new NamespacedKey(plugin, "backpack_uuid");
    }

    // Abre a mochila baseada no ITEM que está na mão
    public void openBackpack(Player player, ItemStack backpackItem) {
        // Cria o inventário
        Inventory inventory = Bukkit.createInventory(player, 27, 
            MiniMessage.miniMessage().deserialize("<dark_green>Mochila de Viagem"));

        ItemMeta meta = backpackItem.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Se já tem itens salvos, carrega
        if (container.has(key, PersistentDataType.STRING)) {
            String data = container.get(key, PersistentDataType.STRING);
            try {
                ItemStack[] items = SerializerUtils.itemStackArrayFromBase64(data);
                inventory.setContents(items);
            } catch (IOException e) {
                player.sendMessage("Erro ao ler dados da mochila.");
            }
        }

        player.openInventory(inventory);
    }

    // Salva o inventário DENTRO do item
    public void saveBackpack(ItemStack backpackItem, Inventory inventory) {
        ItemMeta meta = backpackItem.getItemMeta();
        
        try {
            String data = SerializerUtils.itemStackArrayToBase64(inventory.getContents());
            
            // Salva na NBT do item
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);
            
            // Atualiza o item
            backpackItem.setItemMeta(meta);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Cria uma nova mochila para dar ao player
    public ItemStack createNewBackpack() {
        ItemStack item = new ItemStack(Material.LEATHER);
        ItemMeta meta = item.getItemMeta();
        
        meta.displayName(MiniMessage.miniMessage().deserialize("<green><b>Mochila de Couro</b>"));
        
        // Dá um ID único para ela (opcional, mas bom para evitar stackar mochilas diferentes)
        meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        
        item.setItemMeta(meta);
        return item;
    }
    
    // Verifica se o item é uma mochila nossa
    public boolean isBackpack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(uuidKey, PersistentDataType.STRING);
    }
}